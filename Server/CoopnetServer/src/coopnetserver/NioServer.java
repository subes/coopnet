/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
 *                  Kovacs Zsolt (kovacs.zsolt.85@gmail.com)
 *
 *  This file is part of Coopnet.
 *
 *  Coopnet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Coopnet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
 */
package coopnetserver;

import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.channel.ChannelData;
import coopnetserver.data.connection.Connection;
import coopnetserver.data.connection.ConnectionData;
import coopnetserver.enums.LogTypes;
import coopnetserver.enums.TaskTypes;
import coopnetserver.protocol.out.Message;
import coopnetserver.utils.Database;
import coopnetserver.utils.ErrThread;
import coopnetserver.utils.Logger;
import coopnetserver.utils.RuntimeConsoleCommands;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Sends and recieves messages from the connected clients, and sends them to the commandhandler object for execution.
 * and gives services to the database
 */
public class NioServer extends ErrThread {

    private static final String CHARSET = "UTF-8";
    private static int BUFFER_SIZE = 500;
    private static Charset charset = Charset.forName(CHARSET);
    private final static CharsetDecoder decoder = charset.newDecoder();
    private final static CharsetEncoder encoder = charset.newEncoder();
    private static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    
    private static String getIpAddress(SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();            
            return client.socket().getInetAddress().getHostAddress();
        } catch (Exception e) {
            return "?";
        }
    }
    
    public static void send(Connection con, Message message){
        Logger.logOutTraffic(message.getLogString(), con);
        
        if(message.isSent()){
            throw new IllegalArgumentException("A Message can only be sent once! They send themselves!");
        }
        
        try {
            CharBuffer charBuffer = CharBuffer.wrap(message.getMessage());
            ByteBuffer byteBuffer;
            synchronized(encoder){
                byteBuffer= encoder.encode(charBuffer);
            }
            if (byteBuffer.limit() > BUFFER_SIZE) {
                for (ByteBuffer piece : cutToLength(byteBuffer, BUFFER_SIZE)) {
                    synchronized (con) {
                        do {
                            ((SocketChannel) con.getSelectionKey().channel()).write(piece);
                        } while (piece.hasRemaining());
                    }
                }
            } else {
                synchronized (con) {
                    do {
                        ((SocketChannel) con.getSelectionKey().channel()).write(byteBuffer);
                    } while (byteBuffer.hasRemaining());
                }
            }
        } catch (Exception ex) {
            TaskProcesser.addTask(
                new Task(TaskTypes.LOGOFF,con,new String[] {} ));
            Logger.log(LogTypes.ERROR, "Could not send message: "+ex.getLocalizedMessage(), con);
        }
    }

    private void process(SelectionKey key, ByteBuffer buffer) {
        Connection con = ConnectionData.getConnection(key);
        try {
            if (con == null) {
                con = ConnectionData.createNewConnection(key);
            }
            con.updateLastMessageTimeStamp();
            CharBuffer charBuffer;
            synchronized(decoder){
                charBuffer = decoder.decode(buffer);
            }
            String readString = charBuffer.toString();
            if (readString.equals(Protocol.HEARTBEAT)) {
                Logger.logInTraffic(new String[]{readString}, con);
                return;
            }
            TaskProcesser.addTask( //execute the command
                    new Task(new Object[]{con, readString}));
        } catch (Exception e) {
            Logger.log(e, con);
        }
    }

    private static ByteBuffer extractBytes(ByteBuffer buffer) {
        ByteBuffer temp = ByteBuffer.allocate(buffer.limit());
        //copy data
        buffer.rewind();
        temp.put(buffer);
        temp.flip();

        return temp;
    }

    private static ByteBuffer arrayCut(ByteBuffer buffer, int start, int end) {
        ByteBuffer temp = ByteBuffer.allocate(end - start);
        //copy data 
        System.arraycopy(buffer.array(), start, temp.array(), 0, end - start);
        temp.position(end - start);
        temp.flip();
        return temp;
    }

    private static ByteBuffer arrayConcat(ByteBuffer buffer1, ByteBuffer buffer2, int offset1, int offset2) {
        int size1, size2;
        size1 = buffer1 == null ? 0 : buffer1.limit();
        size2 = buffer2 == null ? 0 : buffer2.limit();

        ByteBuffer temp = ByteBuffer.allocate(size1 + size2);
        //copy data 1
        if (size1 > 0) {
            temp.put(buffer1.array(), offset1, buffer1.limit() - offset1);
        //copy data 2
        }
        if (size2 > 0) {
            temp.put(buffer2.array(), offset2, buffer2.limit() - offset2);
        }
        temp.flip();

        return temp;
    }

    private static ByteBuffer[] cutToLength(ByteBuffer byteBuffer, int size) {
        ArrayList<ByteBuffer> dataarray = new ArrayList<ByteBuffer>();
        int begin, end;
        begin = 0;
        end = 0;

        while (end < byteBuffer.limit()) {
            end += size;
            if (end > byteBuffer.limit()) {
                end = byteBuffer.limit();
            }
            dataarray.add(arrayCut(byteBuffer, begin, end));
            begin = end;
        }

        return dataarray.toArray(new ByteBuffer[1]);
    }

    private static int findDelimiter(ByteBuffer buffer, int start) {
        byte[] array = buffer.array();

        if (array != null) {
            int i = start;
            while (i < array.length - Protocol.ENCODED_MESSAGE_DELIMITER.length + 1) {
                boolean isFound = true;
                int j = 0;
                while (j < Protocol.ENCODED_MESSAGE_DELIMITER.length && isFound) {
                    if (array[i + j] != Protocol.ENCODED_MESSAGE_DELIMITER[j]) {
                        isFound = false;
                    } else {
                        j++;
                    }
                }

                if (isFound) {
                    return i + Protocol.ENCODED_MESSAGE_DELIMITER.length;
                } else {
                    if (j == 0) {
                        i++;
                    } else {
                        i += j;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public void handledRun() throws Throwable {
        Selector selector = null;
        ServerSocketChannel server = null;
        
        try {
            // Create the server socket channel
            server = ServerSocketChannel.open();
            // nonblocking I/O
            server.configureBlocking(false);
            // bind
            server.socket().bind(new java.net.InetSocketAddress(Globals.getIP(), Globals.getPort()));
            //show ready status on console
            if(Globals.getDebug()){
                Logger.log(LogTypes.LOG, "Server ready at " + Globals.getIP() + ":" + Globals.getPort());
            }else{
                System.out.println("Server ready at " + Globals.getIP() + ":" + Globals.getPort());
            }
            // Create the selector
            selector = Selector.open();
            // Recording server to selector
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception ex) {
            if(Globals.getDebug()){
                Logger.log(LogTypes.ERROR, "binding Server to " + Globals.getIP() + ":" + Globals.getPort() + " failed");
            }else{
                System.out.println("ERROR: binding Server to " + Globals.getIP() + ":" + Globals.getPort() + " failed");
            }
            System.exit(1);
        }

        //the server loop
        while (true) {
            try {
                // Waiting for events, this will sleep until something happens
                selector.select();
                // Get keys
                Set keys = selector.selectedKeys();
                Iterator keyiterator = keys.iterator();

                // For each keys...
                while (keyiterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keyiterator.next();

                    // Remove the current key
                    keyiterator.remove();
                    // if isAccetable = true
                    // then a client required a connection
                    if ( key.isValid() && key.isAcceptable()) {
                        // get client socket channel
                        try {
                            //accept connection
                            SocketChannel client = server.accept();
                            // Non Blocking I/O
                            client.configureBlocking(false);                            
                            // recording to the selector (reading)
                            client.register(selector, SelectionKey.OP_READ);
                            continue;
                        } catch (Exception ex) {
                            Logger.log(LogTypes.ERROR,"couldnt accept connection" );
                        }
                    }

                    // if isReadable = true
                    // then the server is ready to read
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (key.isValid() && key.isReadable()) {
                        
                        SocketChannel client = (SocketChannel) key.channel();
                        //check for attack
                        if (ConnectionData.getConnection(key) == null && key.attachment() != null && ((ByteBuffer) key.attachment()).array().length > 50) {
                            key.cancel();
                            synchronized (key) {
                                key.channel().close();
                            }
                            Logger.log(LogTypes.WARNING, "Attack from:" + getIpAddress(key) + " detected");
                        }
                        // Read byte coming from the client
                        try {
                            buffer.clear();
                            synchronized (key) {
                                if (client.read(buffer) == -1) {
                                    throw new IOException("Connection terminated by the client.");
                                }
                            }
                            buffer.flip();

                            ByteBuffer prev = (ByteBuffer) key.attachment();
                            //search for delimiter
                            ByteBuffer bufarray = extractBytes(buffer);
                            ByteBuffer tmp = arrayConcat(prev, bufarray, 0, 0);
                            //get all readed packets
                            int idx;
                            while ((idx = findDelimiter(tmp, 0)) > -1) {
                                ByteBuffer packet = arrayCut(tmp, 0, idx - Protocol.ENCODED_MESSAGE_DELIMITER.length);
                                tmp = arrayCut(tmp, idx, tmp.limit()); //cut off packet from start
                                process(key, packet);
                            }
                            //save remaining data
                            key.attach(tmp);
                        } catch (Exception e) {
                            key.cancel();
                            Connection con = ConnectionData.getConnection(key);
                            if(con != null){
                                TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,ConnectionData.getConnection(key),new String[] {} ));
                            }else{
                                ConnectionData.removeConnection(key);
                            }
                            //logOff(key);//TODO dont log off directly
                            // client is no longer active
                            if (!(e.getMessage() != null && (e.getMessage().contains("reset by peer") || e.getMessage().contains("closed by the remote host") || e.getMessage().contains("terminated by the client.")))) {                               
                                Logger.log(e, con);
                            }
                        }
                        continue;
                    }
                }
            } catch (Exception ex) {
                Logger.log(ex, null);
            }
        }
    }

    /**
     * initialise the database and the server and start
     */
    public static void startup() {
        ChannelData.load();

        //start the server
        new NioServer().start();
        
        //force DB connection
        try{
            Database.ensureConnection();
        }catch(Exception e){}

        //start dropping connections
        new ConnectionDropper().start();
        //start task processing
        new TaskProcesser().start();

        //read console
        RuntimeConsoleCommands.bindAndListen();
    }
}
