package coopnetclient.voicechat;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.SwingUtilities;

class HandlerThread extends Thread {

    boolean running = true;
    private SocketChannel socketChannel;
    public static final int WRITEBUFFER_SIZE = 1000;
    public static final int READBUFFER_SIZE = 1000;
    private static ByteBuffer readbuffer = ByteBuffer.allocate(READBUFFER_SIZE);
    private ByteBuffer attachment = null;
    Vector<MixedMessage> outqueue = new Vector<MixedMessage>();

    public void stopThread() {
        running = false;
        try {
            socketChannel.close();
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
        System.out.println("VoiceClient connecting to " + VoiceClient.serveraddress);
        socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(VoiceClient.serveraddress, VoiceClient.serverport));
            running = true;            
            //start sender thread
            new Thread() {

                @Override
                public void run() {
                    while (running) {
                        try {
                            sleep(10);
                            if (running) {
                                doSend();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

             SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().connected();
                    }
                });
            
            VoiceClient.send("login" + Protocol.MESSAGE_DELIMITER +Globals.getThisPlayer_loginName() );

            while (running) {
                Read();
            }
        } catch (Exception e) {
            running = false;
            String msg = e.getMessage();
            if (e instanceof java.nio.channels.AsynchronousCloseException) {
                return; // do nothing, happens when disconnecting
            }
            if (msg != null && msg.contains("UnknownHostException")) {
                Globals.getClientFrame().printToVisibleChatbox("System", "Invalid voiceChat address!", ChatStyles.SYSTEM , false);                
            } else if (msg != null && (msg.equals("Connection refused: connect") || msg.equals("Connection refused"))) {
                Globals.getClientFrame().printToVisibleChatbox("System", "Could not connect to voiceChat address!", ChatStyles.SYSTEM , false);                
            } else if ((msg != null && (msg.equals("Connection reset")) || e instanceof IOException)) {
                Globals.getClientFrame().printToVisibleChatbox("System", "Connection with voiceChat server was lost!", ChatStyles.SYSTEM , false);                
            } else {
                Logger.log(e);
            }
             SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().connectFailedOrBroken();
                    }
                });
            
        }
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("client thread died");
    }

    private void process(ByteBuffer packet) {
        try {
            MixedMessage msg = new MixedMessage(packet);
            if (msg.commandType == MixedMessage.STRING_COMMAND && msg.valid) {
                VoiceCommandhandler.execute2(msg.commandString);
            } else if (msg.commandType == MixedMessage.AUDIO_DATA_PACKAGE && msg.valid) {
                String sender = msg.commandString;
                byte[] data = msg.byteData;
                VoicePlayback.putDataInChannel(data, sender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ByteBuffer extractBytes(ByteBuffer buffer) {
        ByteBuffer temp = ByteBuffer.allocate(buffer.limit());
        if (buffer.limit() > 0) {
            //copy data
            buffer.rewind();
            temp.put(buffer);
        }
        temp.flip();
        return temp;
    }

    private static ByteBuffer arrayCut(ByteBuffer buffer, int start, int end) {
        ByteBuffer temp = ByteBuffer.allocate(end-start);
        //copy data 
        System.arraycopy(buffer.array(), start, temp.array(), 0, end - start);
        temp.position(end - start);
        temp.flip();
        return temp;
    }

    private ByteBuffer arrayconcat(ByteBuffer buffer1, ByteBuffer buffer2, int offset1, int offset2) {
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
        for (int i = start; array != null && i <= buffer.limit() - 5; i++) {
            if (      (array[i] == -128 
                    && array[i + 1] == 127 
                    && array[i + 2] == -128 
                    && array[i + 3] == 127 
                    && array[i + 4] == -128) 
                    && (i == (buffer.limit() - 5) 
                       || (array[i + 5] == 1 || array[i + 5] == 2))) {
                return i + 5;
            }
        }
        return -1;
    }

    private void Read() throws Exception {
        readbuffer.clear();
        if (socketChannel.read(readbuffer) == -1) {
            running = false;
            throw new IOException("Connection terminated by the client.");
        }
        readbuffer.flip();

        ByteBuffer prev = (ByteBuffer) attachment;
        //search for delimiter
        ByteBuffer bufarray = extractBytes(readbuffer);
        ByteBuffer tmp = arrayconcat(prev, bufarray, 0, 0);
        //get all readed packets
        int idx;
        while ((idx = findDelimiter(tmp, 0)) > -1) {
            ByteBuffer packet = arrayCut(tmp, 0, idx - 5);
            tmp = arrayCut(tmp, idx, tmp.limit()); //cut off packet from start
            process(packet);
        }
        //save remaining data
        attachment = tmp;
    }

    public boolean doSend() {
        if (outqueue.size() > 0) {
            MixedMessage msg = outqueue.firstElement();
            outqueue.removeElementAt(0);
            //System.out.println("trying to send:" + rawdata);
            // Betöltés a karakterpufferbe:
            try {
                ByteBuffer byteBuffer = msg.getBytesToSend();
                if (byteBuffer == null) {
                    return false;
                }
                if (byteBuffer.limit() > WRITEBUFFER_SIZE) {
                    for (ByteBuffer piece : cutToLength(byteBuffer, WRITEBUFFER_SIZE)) {
                        socketChannel.write(piece);
                    }
                } else {
                    socketChannel.write(byteBuffer);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
}

/**
 * Contains the global  fields of the client, these fields are used by most of the other classes.<br>
 * Also has the StartUp method that will initialise and start the client
 */
public class VoiceClient {

    public static String serveraddress;
    public static int serverport;
    public static String playername;
    private static HandlerThread client;

    public void disconnect() {
        try {
            client.stopThread();
            client = null;
            VoicePlayback.shutDown();
        } catch (Exception e) {
        }
    }

    /**
     * Sends the command to the server
     */
    public static void send(String command) {
        if (client != null) {
            MixedMessage msg = new MixedMessage(command);
            client.outqueue.add(msg);
        }
    }

    public static void sendVoiceData(byte[] data) {
        if (client != null) {
            MixedMessage msg = new MixedMessage(data, VoiceClient.playername);
            client.outqueue.add(msg);
        }
    }

    /**
     *Initialises and starts the client
     * 
     */
    public VoiceClient(String name,String serveraddress, String serverport) {
        //INITIALIZE FIELDS
        VoiceClient.playername = name;
        VoiceClient.serveraddress = serveraddress;
        VoiceClient.serverport = new Integer(serverport);       
        //connect
        VoicePlayback.init();
        client = new HandlerThread();
        client.start();
    }
}
