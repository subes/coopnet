package coopnetclient.voicechat;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Sends and recieves messages from the connected clients, and sends them to the commandhandler object for execution.
 * and gives services to the database
 */
public class VoiceServer extends Thread {

    static int BUFFER_SIZE = 1000;
    private static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private static java.util.Map<java.nio.channels.SelectionKey, String> Keys_to_Players = new java.util.HashMap<java.nio.channels.SelectionKey, String>();
    private static java.util.Map<String, java.nio.channels.SelectionKey> Players_to_Keys = new java.util.HashMap<String, java.nio.channels.SelectionKey>();
    public static Vector<String> players = new Vector<String>();
    protected static Vector<Vector<SelectionKey>> playersInChannels = new Vector<Vector<SelectionKey>>();
    private static Vector<SelectionKey> brokenClients = new Vector<SelectionKey>();
    protected static boolean isLocked = false;


    static {
        for (int i = 0; i < 8; ++i) {
            playersInChannels.add(new Vector<SelectionKey>());
        }
    }

    public static void setLocked(boolean locked) {
        isLocked = locked;
    }

    private static int channelIndexOfKey(java.nio.channels.SelectionKey key) {
        int i = 0;
        for (; i < playersInChannels.size(); ++i) {
            Vector<SelectionKey> v = playersInChannels.get(i);
            if (v.contains(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns a player with the specified selectionkey
     */
    static String playerByKey(java.nio.channels.SelectionKey key) {
        return Keys_to_Players.get(key);
    }

    /**
     * returns the selectionkey associated with the player
     */
    static java.nio.channels.SelectionKey keyByPlayer(String p) {
        return Players_to_Keys.get(p);
    }

    static void logIn(String userName, java.nio.channels.SelectionKey key) {
        Keys_to_Players.put(key, userName);
        Players_to_Keys.put(userName, key);        
        //send already connected players
        for (String player : players) {
            sendToKey(key,
                    new MixedMessage("addplayer" + Protocol.MESSAGE_DELIMITER + player + Protocol.MESSAGE_DELIMITER + channelIndexOfKey(Players_to_Keys.get(player))));
        }
        players.add(userName);
        playersInChannels.get(0).add(key);
        sendtoall(new MixedMessage("addplayer" + Protocol.MESSAGE_DELIMITER + userName + Protocol.MESSAGE_DELIMITER + "0"));
    }

    /**
     * removes the player and the key from the server-database
     */
    static void logOff(SelectionKey key) {
        key.cancel();
        try {
            key.channel().close();
        } catch (Exception e) {
        }
        String userName = playerByKey(key);
        if (userName != null) {
            Keys_to_Players.remove(key);
            Players_to_Keys.remove(userName);
            players.remove(userName);
            for (Vector<SelectionKey> v : VoiceServer.playersInChannels) {
                v.remove(key);
            }
            sendtoall(new MixedMessage("removeplayer" + Protocol.MESSAGE_DELIMITER + userName));
        }
    }

    /**
     * sends the command to the players client
     */
    static public void sendToKey(SelectionKey key, MixedMessage msg) {
        SocketChannel client = (SocketChannel) key.channel();
        if (msg.valid) {
            try {
                ByteBuffer byteBuffer = msg.getBytesToSend();
                if (byteBuffer == null) {
                    return;
                }
                if (byteBuffer.limit() > BUFFER_SIZE) {
                    for (ByteBuffer piece : cutToLength(byteBuffer, BUFFER_SIZE)) {
                        ((SocketChannel) client).write(piece);
                    }
                } else {
                    ((SocketChannel) client).write(byteBuffer);
                }
            } catch (IOException ex) {
                brokenClients.add(key);
            }
        }
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

    static public void sendtoall(MixedMessage msg) {
        Iterator<SelectionKey> pit = Keys_to_Players.keySet().iterator();
        while (pit.hasNext()) {
            SelectionKey key = pit.next();
            sendToKey(key, msg);
        }
        doLogoff();
    }

    static public void sendtoOthers(MixedMessage msg, SelectionKey key) {
        Iterator<SelectionKey> pit = Keys_to_Players.keySet().iterator();
        while (pit.hasNext()) {
            SelectionKey k = pit.next();
            if (!k.equals(key)) {
                sendToKey(k, msg);
            }
        }
        doLogoff();
    }

    static public void sendtoChannel(MixedMessage msg, SelectionKey key) {
        int idx = channelIndexOfKey(key);
        for (SelectionKey k : playersInChannels.get(idx)) {
            if (!key.equals(k)) {
                sendToKey(k, msg);
            }
        }
    }

    private static void doLogoff() {
        for (SelectionKey key : brokenClients) {
            logOff(key);
        }
        brokenClients.clear();
    }
    int port;
    public static boolean serverrun = false;

    /** Creates a new instance of the server  */
    public VoiceServer(int port) {
        this.port = port;
        serverrun = true;
        isLocked = false;
        players.clear();
        Keys_to_Players.clear();
        Players_to_Keys.clear();
        brokenClients.clear();
        for (int i = 0; i < playersInChannels.size(); ++i) {
            Vector<SelectionKey> v = playersInChannels.get(i);
            v.clear();
        }
    }

    public void shutdown() {
        serverrun = false;
        for (SelectionKey key : Keys_to_Players.keySet()) {
            try {
                key.channel().close();
            } catch (Exception e) {
            }
        }
    }

    private void process(SelectionKey key, ByteBuffer buffer) {
        MixedMessage msg = new MixedMessage(buffer);
        if (msg.commandType == MixedMessage.STRING_COMMAND && msg.valid) {
            try{
                VoiceCommandhandler.execute(key, msg.commandString);
            }catch(Exception e){
                Logger.log(e) ;
            }
        } else if (msg.commandType == MixedMessage.AUDIO_DATA_PACKAGE && msg.valid) {
            if (playerByKey(key) != null) {
                sendtoChannel(msg, key);
            }
        } else {
            System.out.println("Bad packet");
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

    public static ByteBuffer arrayCut(ByteBuffer buffer, int start, int end) {
        ByteBuffer temp = ByteBuffer.allocate(end - start);
        //copy data 
        for (int i = start; i < end; i++) {
            temp.put(buffer.get(i));
        }
        temp.flip();
        return temp;
    }

    public static ByteBuffer arrayConcat(ByteBuffer buffer1, ByteBuffer buffer2, int offset1, int offset2) {
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

    private static int findDelimiter(ByteBuffer buffer, int start) {
        byte[] array = buffer.array();
        for (int i = start; array != null && i <= buffer.limit() - 5; i++) {
            if ((array[i] == -128 && array[i + 1] == 127 && array[i + 2] == -128 && array[i + 3] == 127 && array[i + 4] == -128) && (i == (buffer.limit() - 5) || (array[i + 5] == 1 || array[i + 5] == 2))) {
                return i + 5;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            // Create the server socket channel
            server = ServerSocketChannel.open();
            //ex.printStackTrace();
            server.configureBlocking(false);
            //bind to all interfaces
            server.socket().bind(new java.net.InetSocketAddress("0.0.0.0", port));
            System.out.println("Server ready at port " + port);
            // Create the selector
            selector = Selector.open();
            // Recording server to selector
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception ex) {
            System.out.println("server setup error");
            Globals.getClientFrame().printToVisibleChatbox("System", "The port is already used, cannot start VoiceChat service!", ChatStyles.SYSTEM, false);
            Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().connectFailedOrBroken();
            return;
        }
        Globals.getClientFrame().printToVisibleChatbox("System", "VoiceChat service started! Your URL is voice://" + Globals.getMyIP() + ":" + Settings.getVoiceChatPort(), ChatStyles.SYSTEM, false);
        //the server loop
        while (serverrun) {
            try {
                sleep(10);
            } catch (InterruptedException ex) {
            }
            try {
                // Waiting for events, this will sleep until something happens
                int jobs = selector.selectNow();
                if(jobs == 0){
                    continue;
                }
                // Get keys
                Set keys = selector.selectedKeys();
                Iterator keyiterator = keys.iterator();
                // Remove the current key
                while (keyiterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keyiterator.next();
                    // Remove the current key
                    keyiterator.remove();
                    // if isAccetable = true
                    // then a client required a connection
                    if (key.isAcceptable()) {
                        try {
                            //accept connection
                            SocketChannel client = server.accept();
                            System.out.println("incomming connection!");
                            // Non Blocking I/O
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            continue;
                        } catch (Exception ex) {
                            System.out.println("couldnt accept connection");
                        }
                    }

                    // if isReadable = true
                    // then the server is ready to read
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    if (key.isValid() && key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        // Read byte coming from the client
                        try {
                            buffer.clear();
                            if (client.read(buffer) == -1) {
                                throw new IOException("Connection terminated by the client.");
                            }
                            buffer.flip();
                            ByteBuffer prev = (ByteBuffer) key.attachment();
                            //search for delimiter
                            ByteBuffer bufarray = extractBytes(buffer);
                            ByteBuffer tmp = arrayConcat(prev, bufarray, 0, 0);
                            //get all readed packets
                            int idx;
                            while ((idx = findDelimiter(tmp, 0)) > -1) {
                                ByteBuffer packet = arrayCut(tmp, 0, idx - 5);
                                tmp = arrayCut(tmp, idx, tmp.limit()); //cut off packet from start
                                process(key, packet);
                            }
                            //save remaining data
                            if (tmp.limit() > 0) {
                                key.attach(tmp);
                            } else {
                                key.attach(null);
                            }

                        } catch (Exception e) {
                            key.cancel();
                            logOff(key);
                        }
                        continue;
                    }
                }
            } catch (Exception ex) {
                System.out.println("shit happens");
                ex.printStackTrace();
            }
        }
        try {
            server.close();
        } catch (IOException ex) {
        }
    }
}
