/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

This file is part of Coopnet.

Coopnet is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Coopnet is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
 */
package coopnetclient;

import coopnetclient.modules.SwingWorker;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
    private ByteBuffer byteBufferOut = ByteBuffer.allocate(WRITEBUFFER_SIZE);
    private CharBuffer charBufferOut = CharBuffer.allocate(WRITEBUFFER_SIZE);
    private ByteBuffer attachment = null;
    private Thread sender;
    private Vector<String> outQueue = new Vector<String>();

    protected void addToOutQueue(String element) {
        outQueue.add(element);
    }

    public void stopThread() {
        running = false;
        try {
            socketChannel.socket().close();
            sender.join();
        } catch (Exception e) {
            System.out.println("Could not close socket");
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
            socketChannel.connect(new InetSocketAddress(coopnetclient.modules.Settings.getServerIp(), coopnetclient.modules.Settings.getServerPort()));
            socketChannel.socket().setSendBufferSize(WRITEBUFFER_SIZE);
            //start sender thread
            sender = new Thread() {

                @Override
                public void run() {
                    try{
                        while (running) {
                            try {
                                sleep(10);
                            } catch (InterruptedException ex) {}
                            doSend();
                        }
                    } catch (Exception e) {
                       ErrorHandler.handleException(e);
                    }
                }
            };
            sender.start();

            //login
            if (coopnetclient.modules.Settings.getAutoLogin()) {
                String name = coopnetclient.modules.Settings.getLastLoginName();
                String passw = coopnetclient.modules.Settings.getLastLoginPassword();

                Client.send(Protocol.login(name, passw), null);
                String s = coopnetclient.modules.Settings.getHomeChannel();
                if (s.length() > 0) {
                    Client.send(Protocol.JoinChannel(s), null);
                }

                String input = null;
                try {
                    input = read();
                } catch (Exception q) {
                    JOptionPane.showMessageDialog(null, q, "ERROR", JOptionPane.ERROR_MESSAGE);
                    ErrorHandler.handleException(q);
                }
                if (input == null) {
                    JOptionPane.showMessageDialog(null, "No response from server", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else if (input.equals("OK_LOGIN")) {
                    Globals.setThisPlayer_loginName(name);
                    Globals.setLoggedInStatus(true);
                    Globals.getClientFrame().removeLoginTab();
                } else {
                    Globals.getClientFrame().addLoginTab();
                }
            } else {
                Globals.getClientFrame().addLoginTab();
            }
            //READING DATA
            String input = "";
            while (running) {
                input = read();
                //execute command
                SwingUtilities.invokeLater(new SwingWorker(input));
                input = "";
                sleep(20);
            }
            System.out.println("handlerthread stopped");
        } catch (Exception e) {
            //disconnect
            Globals.getClientFrame().disconnect();
            //ErrorHandler decides if there is a need for the stacktrace, 
            //this helps looking at the log of a bugreport
            //handle excptions
            ErrorHandler.handleException(e);
        }
        System.out.println("connection closed");
    }

    private String process(ByteBuffer packet) {
        try {
            CharBuffer charBuffer = decoder.decode(packet);
            String readedString = charBuffer.toString();

            if (readedString.length() > 0) {
                return readedString;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        for (int i = start; i < end; i++) {
            temp.put(buffer.get(i));
        }
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
            ByteBuffer packet = arrayCut(attachment, 0, idx - 3);
            attachment = arrayCut(attachment, idx, attachment.limit()); //cut off packet from start
            return process(packet);
        }

        //read new mesages from socket
        readBuffer.clear();
        if (socketChannel.read(readBuffer) == -1) {
            running = false;
            throw new IOException("Connection terminated by the client.");
        }
        readBuffer.flip();

        ByteBuffer prev = attachment;
        //search for delimiter
        ByteBuffer bufarray = extractBytes(readBuffer);
        ByteBuffer tmp = arrayConcat(prev, bufarray, 0, 0);
        attachment = tmp;
        if (attachment != null && (idx = findDelimiter(attachment, 0)) > -1) {
            ByteBuffer packet = arrayCut(attachment, 0, idx - 3);
            attachment = arrayCut(attachment, idx, attachment.limit()); //cut off packet from start
            return process(packet);
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
            } catch (Exception e) {
                System.out.println("Failed to send: " + rawdata);
                e.printStackTrace();
            }        
        return true;
        }
        return false;
    }

    public boolean isConnected() {
        return socketChannel.isConnected();
    }
}
    