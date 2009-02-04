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
package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.TransferStatuses;
import coopnetclient.protocol.out.Protocol;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import javax.swing.JOptionPane;

public class FileTransferHandler {

    public static final int SEND_MODE = 1;
    public static final int RECIEVE_MODE = 2;
    public static final int FLUSH_INTERVAL = 1000000;
    private static final Object lock = new Object();
    private int mode;
    private String fileName;
    private String peerName;
    private String peerIP;
    private String peerPort;
    private long totalsize;
    private long firstByteToSend = 0;
    private File sentFile = null;
    private String savePath = Settings.getRecieveDestination();
    private boolean resuming = false;
    private boolean running = false;
    private ServerSocket serverSocket = null;
    private SocketChannel socket = null;
    private UUID ID;
    //variables for observing the transfer
    long starttime;
    long processedBytes = 0;
    long currenttime;
    long timeelapsed;
    long speedtmptime;
    int progress;

    public FileTransferHandler(UUID ID, String peerName, File sentFile) {
        this.mode = SEND_MODE;
        this.ID = ID;
        this.sentFile = sentFile;
        this.totalsize = sentFile.length();
        this.peerName = peerName;
        this.fileName = sentFile.getName();
    }

    public FileTransferHandler(UUID ID, String peerName, long size, String fileName, String ip, String port) {
        this.mode = RECIEVE_MODE;
        this.ID = ID;
        this.fileName = fileName;
        this.peerIP = ip;
        this.peerPort = port;
        this.totalsize = size;
        this.peerName = peerName;
    }

    private void startObserving() {
        new Thread() {

            long lastBytePosition = 0;

            @Override
            public void run() {
                currenttime = System.currentTimeMillis();
                speedtmptime = System.currentTimeMillis();
                while (running) {
                    try {
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                        }
                        //DO stuff
                        currenttime = System.currentTimeMillis();
                        long tmp = currenttime - speedtmptime;
                        int speed = (int) ((processedBytes - lastBytePosition) / tmp); //Byte per millisec / kilobyte per sec
                        feedBackSpeed(speed);
                        speedtmptime = currenttime;
                        progress = (int) ((((processedBytes) * 1.0) / (totalsize - firstByteToSend)) * 100);
                        feedBackProgress(progress);

                        if (processedBytes > 0) {
                            timeelapsed = currenttime - starttime;
                            timeelapsed = timeelapsed / 1000;
                            feedBackTime((long) ((((totalsize - firstByteToSend) - processedBytes) / (speed * 1.0f)) / 1000.0f));
                        }

                        lastBytePosition = processedBytes;
                    } catch (Exception ex) {
                        Logger.log(ex);
                    }
                }
            }
        }.start();
    }

    public File getSentFile() {
        return sentFile;
    }

    public boolean getResuming() {
        return resuming;
    }

    public boolean setResuming(boolean value) {
        resuming = value;
        if (!resuming) {
            firstByteToSend = 0;
        } else {
            setResuming();
        }
        return resuming;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String path) {
        savePath = path;
    }

    public long getFullSize() {
        return totalsize;
    }

    public long getFirstByteToSend() {
        return firstByteToSend;
    }

    private void feedBackStatus(TransferStatuses status) {
        Globals.getTransferModel().updateStatus(ID, status);
    }

    private void feedBackTime(long timeInSeconds) {
        Globals.getTransferModel().updateTime(ID, timeInSeconds);
    }

    private void feedBackProgress(int value) {
        Globals.getTransferModel().updateProgress(ID, value);
    }

    private void feedBackSpeed(int value) {
        Globals.getTransferModel().updateSpeed(ID, value);
    }

    private File Checkfile(File file, int n) {
        try {
            String path = file.getCanonicalPath();
            String name = file.getName();
            path = path.substring(0, path.length() - name.length());
            int idx = -1;
            idx = name.indexOf(".");
            //if not the first (i) file
            if (name.charAt(idx - 1) == ')' && name.charAt(idx - 3) == '(') {
                name = name.substring(0, idx - 2) + n + ")" + name.substring(idx);
            } //first try, add (n)
            else {
                name = name.substring(0, idx) + "(" + n + ")" + name.substring(idx);
            }
            return new File(path + name);
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    private boolean setResuming() {
        try {
            File checkthis = getDestFile();
            long currentsize = checkthis.length();
            if (totalsize <= currentsize) {
                firstByteToSend = 0;
                JOptionPane.showMessageDialog(Globals.getClientFrame(),
                        "<html>The existing file is larger or equal to the file sent!<br>Probably not the same file!",
                        "Cannot resume file!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            firstByteToSend = currentsize + 1;
            resuming = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            firstByteToSend = 0;
            resuming = false;
            return false;
        }
    }

    public File getDestFile() throws IOException {
        File dest;
        String fullpath = savePath;
        if (!(new File(fullpath).exists())) {
            new File(fullpath).mkdirs();
        }
        if (!fullpath.endsWith("/") || !fullpath.endsWith("\\")) {
            fullpath = fullpath + "/";
        }
        fullpath += fileName;
        dest = new File(fullpath).getCanonicalFile();
        return dest;
    }

    public void cancel() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        }
    }

    public void turnAround() {
        switch (mode) {
            case RECIEVE_MODE:
                try {
                    serverSocket.close();
                } catch (Exception e) {
                }
                break;
            case SEND_MODE:
                try {
                    socket.close();
                } catch (Exception e) {
                }
                break;
        }
    }

    public void startSend(String ip, String port, long firstByte) {
        peerIP = ip;
        peerPort = port;
        firstByteToSend = firstByte;
        send1(peerIP, peerPort, firstByteToSend);
        startObserving();
    }

    public void startRecieve() {
        recieve1();
        Protocol.acceptTransfer(peerName, fileName, firstByteToSend);
        startObserving();
    }

    //binds download to the port, sender connects here
    private void recieve1() {
        new Thread() {

            @Override
            public void run() {
                Socket socket = null;
                BufferedInputStream bi = null;
                BufferedOutputStream bo = null;
                try {
                    try {
                        running = true;
                        feedBackStatus(TransferStatuses.Starting);
                        synchronized (lock) {
                            serverSocket = new ServerSocket(Settings.getFiletTansferPort());
                            socket = serverSocket.accept();
                            serverSocket.close();
                        }
                    } catch (Exception e) {
                        if (e instanceof java.net.BindException || e instanceof java.net.SocketException) {
                            if (serverSocket != null) {
                                serverSocket.close();
                                serverSocket = null;
                            }
                            Protocol.turnTransferAround(peerName, fileName);
                            Thread.sleep(500);
                            recieve2();
                            return;
                        } else {
                            e.printStackTrace();
                        }
                    }

                    bi = new BufferedInputStream(socket.getInputStream());

                    File destfile = getDestFile();
                    //if not resuming, rename file if it already exists
                    if (!resuming) {
                        int n = 1;
                        while (!destfile.createNewFile()) {
                            destfile = Checkfile(destfile, n);
                            n++;
                        }
                    } else {
                        //make sure it exists
                        if (!destfile.exists()) {
                            destfile.createNewFile();
                        }
                    }

                    bo = new BufferedOutputStream(new FileOutputStream(destfile, resuming));

                    feedBackStatus(TransferStatuses.Transferring);
                    starttime = System.currentTimeMillis();
                    processedBytes = 0;
                    int readedbyte;
                    int tmp = 0;
                    //Read data
                    while (running && (readedbyte = bi.read()) != -1) {
                        bo.write(readedbyte);
                        ++processedBytes;
                        ++tmp;
                        if (tmp > FLUSH_INTERVAL) {
                            bo.flush();
                            tmp = 0;
                        }
                    }
                    bo.flush();
                    if ((processedBytes + firstByteToSend - (resuming ? 1 : 0)) == totalsize) {
                        feedBackStatus(TransferStatuses.Finished);
                        feedBackProgress(100);
                    } else {
                        feedBackStatus(TransferStatuses.Failed);
                    }
                    running = false;
                    bi.close();
                    bo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    feedBackStatus(TransferStatuses.Error);
                } finally {
                    try {
                        if (bi != null) {
                            bi.close();
                        }
                        if (bo != null) {
                            bo.close();
                        }
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    //turns around the connection(connect to the sender )
    private void recieve2() {
        new Thread() {

            BufferedOutputStream bo = null;

            @Override
            public void run() {
                try {
                    running = true;
                    feedBackStatus(TransferStatuses.Retrying);
                    Thread.sleep(1000);
                    socket = SocketChannel.open();
                    socket.connect(new InetSocketAddress(peerIP, new Integer(peerPort)));
                    File destfile = getDestFile();

                    if (!resuming) {
                        int n = 1;
                        while (!destfile.createNewFile()) {
                            destfile = Checkfile(destfile, n);
                            n++;
                        }
                    } else {
                        if (!destfile.exists()) {
                            destfile.createNewFile();
                        }
                    }
                    bo = new BufferedOutputStream(new FileOutputStream(destfile, resuming));

                    feedBackStatus(TransferStatuses.Transferring);
                    starttime = System.currentTimeMillis();
                    processedBytes = 0;
                    int tmp = 0;
                    ByteBuffer buffer = ByteBuffer.allocate(1000);
                    buffer.rewind();
                    //Read data
                    while (running && ((socket.read(buffer)) != -1)) {
                        buffer.flip();
                        processedBytes += buffer.limit();
                        bo.write(buffer.array(), 0, buffer.limit());
                        ++tmp;
                        if (tmp > FLUSH_INTERVAL) {
                            bo.flush();
                            tmp = 0;
                        }
                        buffer.rewind();
                    }
                    bo.flush();
                    if ((processedBytes + firstByteToSend - (resuming ? 1 : 0)) == totalsize) {
                        feedBackStatus(TransferStatuses.Finished);
                        feedBackProgress(100);
                    } else {
                        feedBackStatus(TransferStatuses.Failed);
                    }
                    running = false;
                    bo.close();
                    socket.close();
                    socket = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    feedBackStatus(TransferStatuses.Error);
                } finally {
                    try {
                        if (bo != null) {
                            bo.close();
                        }
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                        if (serverSocket != null) {
                            serverSocket.close();
                            serverSocket = null;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    private void send1(final String ip, final String port, long firstByte) {
        firstByteToSend = firstByte;
        new Thread() {

            @Override
            public void run() {
                running = true;
                feedBackStatus(TransferStatuses.Starting);
                socket = null;
                BufferedInputStream bi = null;
                try {
                    try {
                        socket = SocketChannel.open();
                        socket.connect(new InetSocketAddress(ip, new Integer(port)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        socket.close();
                        Protocol.turnTransferAround(peerName, fileName);
                        Thread.sleep(500);
                        send2();
                        return;
                    }

                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    bi = new BufferedInputStream(new FileInputStream(sentFile));
                    int readedbyte;
                    //discard data that is not needed
                    long i = 1;
                    while ((i < firstByteToSend) && running) {
                        bi.read();
                        ++i;
                    }

                    feedBackStatus(TransferStatuses.Transferring);
                    starttime = System.currentTimeMillis();
                    processedBytes = 0;
                    //Send data
                    while ((readedbyte = bi.read()) != -1 && running) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                            ++processedBytes;
                        } else {
                            temp.flip();
                            socket.write(temp);
                            temp.rewind();
                            temp.put((byte) readedbyte);
                            ++processedBytes;
                        }
                    }
                    //send last chunk
                    if (temp.position() != 0) {
                        temp.flip();
                        socket.write(temp);
                        temp.rewind();
                    }

                    feedBackStatus(TransferStatuses.Finished);
                    feedBackProgress(100);
                } catch (Exception e) {
                    //set error messag
                    e.printStackTrace();
                    feedBackStatus(TransferStatuses.Error);
                } finally {
                    try {
                        bi.close();
                    } catch (Exception e) {
                    }
                    try {
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                    } catch (Exception e) {
                    }
                }
                running = false;
            }
        }.start();
    }

    //turns around the connection, connect to the reciever
    private void send2() {
        new Thread() {

            long starttime;

            @Override
            public void run() {
                running = true;
                feedBackStatus(TransferStatuses.Retrying);
                Socket socket = null;
                BufferedInputStream bi = null;
                BufferedOutputStream bo = null;
                try {
                    synchronized (lock) {
                        serverSocket = new ServerSocket(Settings.getFiletTansferPort());
                        socket = serverSocket.accept();
                        serverSocket.close();
                    }
                    bo = new BufferedOutputStream(socket.getOutputStream());
                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    bi = new BufferedInputStream(new FileInputStream(sentFile));
                    int readedbyte;
                    //discard data that is not needed
                    long i = 0;
                    while (i < firstByteToSend) {
                        bi.read();
                        ++i;
                    }

                    processedBytes = 0;
                    feedBackStatus(TransferStatuses.Transferring);
                    starttime = System.currentTimeMillis();
                    int tmp = 0;
                    //Send data
                    while ((readedbyte = bi.read()) != -1 && running) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                        } else {
                            temp.flip();
                            bo.write(temp.array(), 0, temp.limit());
                            temp.rewind();
                            temp.put((byte) readedbyte);
                        }
                        ++processedBytes;
                        ++tmp;
                        if (tmp > FLUSH_INTERVAL) {
                            bo.flush();
                            tmp = 0;
                        }
                    }
                    bo.flush();
                    //send last chunk
                    if (temp.position() != 0) {
                        temp.flip();
                        bo.write(temp.array(), 0, temp.limit());
                        temp.rewind();
                        bo.flush();
                    }
                    serverSocket.close();
                    feedBackStatus(TransferStatuses.Finished);
                    feedBackProgress(100);
                } catch (Exception e) {
                    //set error message
                    e.printStackTrace();
                    feedBackStatus(TransferStatuses.Error);
                } finally {
                    try {
                        bi.close();
                    } catch (Exception e) {
                    }
                    try {
                        bo.close();
                    } catch (Exception e) {
                    }
                    try {
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                        if (serverSocket != null) {
                            serverSocket.close();
                            serverSocket = null;
                        }
                    } catch (Exception e) {
                    }
                }
                running = false;
            }
        }.start();
    }
}
