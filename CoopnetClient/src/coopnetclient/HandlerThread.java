/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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
package coopnetclient;

import coopnetclient.enums.LogTypes;
import coopnetclient.enums.ServerProtocolCommands;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.protocol.out.Message;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import coopnetclient.utils.SwingWorker;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.SwingUtilities;

public class HandlerThread extends Thread {

    private final String CHARSET = "UTF-8";
    private Charset charset = Charset.forName(CHARSET);
    boolean running = true;
    private SocketChannel socketChannel;
    public static final int WRITEBUFFER_SIZE = 400;
    public static final int READBUFFER_SIZE = 400;
    private static ByteBuffer readBuffer = ByteBuffer.allocate(READBUFFER_SIZE);
    private CharsetDecoder decoder = charset.newDecoder();
    private CharsetEncoder encoder = charset.newEncoder();
    private ByteBuffer attachment = null;
    private Thread sender;
    private Vector<String> outQueue = new Vector<String>();
    private Long lastMessageSentAt = 0l;

    protected void addToOutQueue(String element) {
        outQueue.add(element);
    }

    public void stopThread() {
        running = false;
        try {
            socketChannel.socket().close();
            if (sender != null) {
                sender.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
    The client reads the incoming commands in this thread
     */
    @Override
    public void run() {
        try {
            socketChannel = null;
            socketChannel = SocketChannel.open();
            socketChannel.socket().connect(new InetSocketAddress(Globals.getServerIP(), Globals.getServerPort()), 15000);
            //start sender thread
            sender = new Thread() {

                @Override
                public void run() {
                    try {
                        while (running) {
                            try {
                                sleep(10);
                            } catch (InterruptedException ex) {
                            }
                            doSend();
                        }
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                    }
                }
            };
            sender.start();
            
            //send heartbeat
            new Thread() {

                @Override
                public void run() {
                    try {
                        while (running) {
                            if((System.currentTimeMillis() - lastMessageSentAt) > 30000 ){
                                new Message(Protocol.HEARTBEAT);
                                synchronized(lastMessageSentAt){
                                    lastMessageSentAt = System.currentTimeMillis();
                                }
                            }
                            try {
                                sleep(1000);
                            } catch (InterruptedException ex) {
                            }                            
                        }
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();

            Protocol.sendVersion();
            //login
            if (coopnetclient.utils.Settings.getAutoLogin()) {
                Protocol.autoLogin();
                String input = null;
                try {
                    input = read();
                } catch (Exception q) {
                    ErrorHandler.handleException(q);
                }
                if (input == null) {
                    TabOrganizer.openLoginPanel();
                } else if (SwingWorker.getParts(input)[0].equals(ServerProtocolCommands.OK_LOGIN.ordinal()+"")) {
                    Globals.setThisPlayer_loginName(SwingWorker.getParts(input)[1]);
                    Globals.setLoggedInStatus(true);
                    TabOrganizer.closeLoginPanel();
                    Protocol.setSleep(Settings.getSleepEnabled());
                    String s = coopnetclient.utils.Settings.getHomeChannel();
                    if (s.length() > 0) {
                        Protocol.joinChannel(s);
                    }
                } else {
                    TabOrganizer.openLoginPanel();
                }
            } else {
                TabOrganizer.openLoginPanel();
            }
            //READING DATA
            String input = "";
            while (running) {
                input = read();
                if (input == null) {
                    continue;
                }
                //execute command
                SwingUtilities.invokeLater(new SwingWorker(input));
                input = "";
                sleep(20);
            }
            Logger.log(LogTypes.LOG, "handlerthread stopped");
        } catch (Exception e) {
            //disconnect
            if (Globals.getConnectionStatus()) {
                Client.disconnect();
                //ErrorHandler decides if there is a need for the stacktrace, 
                //this helps looking at the log of a bugreport
                //handle excptions
                ErrorHandler.handleException(e);
            }
        }
        Logger.log(LogTypes.LOG, "HandlerThreads main loop ended");
    }

    private String process(ByteBuffer packet) {
        try {
            CharBuffer charBuffer = decoder.decode(packet);
            String readedString = charBuffer.toString();

            if (readedString.length() > 0) {
                return readedString;
            }
        } catch (Exception e) {
            Logger.log(e);
        }
        return null;
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

    private static ByteBuffer extractBytes(ByteBuffer buffer) {
        ByteBuffer temp = ByteBuffer.allocate(buffer.limit());
        //copy data
        buffer.rewind();
        temp.put(buffer);
        temp.flip();

        return temp;
    }

    private static ByteBuffer arrayCut(ByteBuffer buffer, int start, int end) {
        ByteBuffer temp = ByteBuffer.allocate(buffer.limit());
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
            buffer1.rewind();
            temp.put(buffer1.array(), offset1, buffer1.limit() - offset1);
        } //copy data 2

        if (size2 > 0) {
            buffer2.rewind();
            temp.put(buffer2.array(), offset2, buffer2.limit() - offset2);
        }
        temp.flip();

        return temp;
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

    private String read() throws Exception {
        //return remaining message if any
        int idx;
        if (attachment != null && (idx = findDelimiter(attachment, 0)) > -1) {
            ByteBuffer packet = arrayCut(attachment, 0, idx - Protocol.ENCODED_MESSAGE_DELIMITER.length );
            attachment = arrayCut(attachment, idx, attachment.limit()); //cut off packet from start
            return process(packet);
        }

        //read new mesages from socket
        readBuffer.clear();
        int read = 0;
        read = socketChannel.read(readBuffer);
        if (read == -1) {
            running = false;
            throw new IOException("Connection lost");
        }

        readBuffer.flip();

        if ( read > 0 ) {
            ByteBuffer prev = attachment;
            //search for delimiter
            ByteBuffer bufarray = extractBytes(readBuffer);
            ByteBuffer tmp = arrayConcat(prev, bufarray, 0, 0);
            attachment = tmp;
        }        
        return null;
    }

    public boolean doSend() {
        if (outQueue.size() > 0) {
            String rawdata = outQueue.firstElement();
            outQueue.removeElementAt(0);
            //new code
            try {
                CharBuffer charBuffer = CharBuffer.wrap(rawdata);
                ByteBuffer byteBuffer = encoder.encode(charBuffer);
                if (byteBuffer.limit() > WRITEBUFFER_SIZE) {
                    for (ByteBuffer piece : cutToLength(byteBuffer, WRITEBUFFER_SIZE)) {
                        socketChannel.write(piece);
                    }
                } else {
                    socketChannel.write(byteBuffer);
                }
                synchronized (lastMessageSentAt) {
                    lastMessageSentAt = System.currentTimeMillis();
                }
            } catch (Exception e) {
                Logger.log(LogTypes.ERROR, "Failed to send: " + rawdata);
                ErrorHandler.handleException(e);
            }
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        return socketChannel.isConnected();
    }
}
    