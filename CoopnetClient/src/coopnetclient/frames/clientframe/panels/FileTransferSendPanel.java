/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.frames.clientframe.panels;

import coopnetclient.Client;
import coopnetclient.Protocol;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.Settings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javax.swing.SwingUtilities;

public class FileTransferSendPanel extends javax.swing.JPanel {

    private File data;
    private boolean sending = true;
    private long totalbytes = 0;
    private long firstByteToSend = 0;
    private ServerSocket serverSocket = null;
    private String reciever;
    private String filename;
    private static int progress;

    /** Creates new form FileTransfer */
    public FileTransferSendPanel(String reciever, File sentfile) {
        initComponents();
        this.reciever = reciever;
        this.filename = sentfile.getName();
        data = sentfile;
        totalbytes = data.length();
        lbl_fileValue.setText(sentfile.getName());
        lbl_recieverValue.setText(reciever);
        long size = sentfile.length();
        int i = 0;
        while (size > 1024) {
            size = size / 1024;
            i++;
        }
        lbl_sizeValue.setText(size + " ");
        if (i == 0) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + " B");
        }
        if (i == 1) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + " kB");
        }
        if (i == 2) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + " MB");
        }
        if (i == 3) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + " GB");
        }
        coopnetclient.modules.Colorizer.colorize(this);
    }

    public String getReciever() {
        return lbl_recieverValue.getText();
    }

    public String getFilename() {
        return lbl_fileValue.getText();
    }

    public void refused() {
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        lbl_statusValue.setText("Peer refused transfer!");
                        btn_cancel.setText("Close");
                    }
                });
    }

    public void setTimeLeft(long time) {
        final int seconds;
        final int minutes;
        final int hours;
        //time=time/1000;//scale to seconds
        seconds = (int) (time % 60);
        time = time / 60;   //scale to minutes
        minutes = (int) (time % 60);
        time = time / 60;
        hours = (int) time;
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        lbl_timeLeftValue.setText(hours + ":" + minutes + ":" + seconds);
                    }
                });
    }

    public void turnAround() {
    }

    private void updateStatusLabel(final String status) {
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        lbl_statusValue.setText(status);
                    }
                });
    }

    public void startSending(final String ip, final String port, long firstByte) {
        firstByteToSend = firstByte;
        new Thread() {

            long starttime;

            @Override
            public void run() {
                long sentBytes = 0;
                sending = true;
                updateStatusLabel("Connecting...");
                SocketChannel socket = null;
                try {
                    try {
                        socket = SocketChannel.open();
                        socket.connect(new InetSocketAddress(ip, new Integer(port)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        socket.close();
                        Client.send(Protocol.turnTransferAround(reciever, filename), null);
                        Thread.sleep(500);
                        startSendingRetry();
                        return;
                    }

                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    BufferedInputStream bi = new BufferedInputStream(new FileInputStream(data));
                    int readedbyte;
                    long currenttime;
                    long timeelapsed;

                    //discard data that is not needed
                    long i = 1;
                    while ((i < firstByteToSend) && sending) {
                        bi.read();
                        i++;
                    }

                    updateStatusLabel("Transferring...");
                    starttime = System.currentTimeMillis();

                    while ((readedbyte = bi.read()) != -1 && sending) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                            sentBytes++;
                        } else {
                            temp.flip();
                            socket.write(temp);
                            temp.rewind();
                            //dont forge to sent the new byte aswell :S
                            temp.put((byte) readedbyte);
                            sentBytes++;

                        }
                        if (sentBytes % 20000 == 0) {
                            progress = (int) ((((sentBytes + firstByteToSend) * 1.0) / totalbytes) * 100);
                            SwingUtilities.invokeLater(
                                    new Runnable() {

                                        public void run() {
                                            pgb_progress.setValue(progress);
                                        }
                                    });
                            currenttime = System.currentTimeMillis();
                            timeelapsed = currenttime - starttime;
                            timeelapsed = timeelapsed / 1000;
                            setTimeLeft((long) (((totalbytes - firstByteToSend) - sentBytes) / (sentBytes * 1.0) * timeelapsed));
                        }
                    }
                    //send last chunk
                    if (temp.position() != 0) {
                        temp.flip();
                        socket.write(temp);
                        temp.rewind();
                    }
                    SwingUtilities.invokeLater(
                            new Runnable() {

                                public void run() {
                                    lbl_statusValue.setText("Transfer complete!");
                                    pgb_progress.setValue(100);
                                    btn_cancel.setText("Close");
                                }
                            });
                    sending = false;

                } catch (Exception e) {
                    //set error messag
                    e.printStackTrace();
                    updateStatusLabel("Error: " + e.getLocalizedMessage());
                } finally {
                    try {
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
    ;
    //turns around the connection, connect to the reciever
    public void startSendingRetry() {
        new Thread() {

            long starttime;

            @Override
            public void run() {
                long sentBytes = 0;
                sending = true;
                updateStatusLabel("Retrying...");
                Socket socket = null;
                try {
                    serverSocket = new ServerSocket(Settings.getFiletTansferPort());
                    socket = serverSocket.accept();
                    BufferedOutputStream bo = new BufferedOutputStream(socket.getOutputStream());

                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    BufferedInputStream bi = new BufferedInputStream(new FileInputStream(data));
                    int readedbyte;
                    long currenttime;
                    long timeelapsed;

                    //discard data that is not needed
                    long i = 0;
                    while (i < firstByteToSend) {
                        bi.read();
                        i++;
                    }

                    updateStatusLabel("Transferring...");
                    starttime = System.currentTimeMillis();

                    while ((readedbyte = bi.read()) != -1 && sending) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                            sentBytes++;
                        } else {
                            temp.flip();
                            bo.write(temp.array(), 0, temp.limit());
                            temp.rewind();
                            temp.put((byte) readedbyte);
                            sentBytes++;
                        }
                        if (sentBytes % 20000 == 0) {
                            bo.flush();
                            progress = (int) ((((sentBytes + firstByteToSend) * 1.0) / totalbytes) * 100);
                            SwingUtilities.invokeLater(
                                    new Runnable() {

                                        public void run() {
                                            pgb_progress.setValue(progress);
                                        }
                                    });
                            currenttime = System.currentTimeMillis();
                            timeelapsed = currenttime - starttime;
                            timeelapsed = timeelapsed / 1000;
                            setTimeLeft((long) (((totalbytes - firstByteToSend) - sentBytes) / (sentBytes * 1.0) * timeelapsed));
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
                    bo.close();
                    serverSocket.close();

                    SwingUtilities.invokeLater(
                            new Runnable() {

                                public void run() {
                                    lbl_statusValue.setText("Transfer complete!");
                                    pgb_progress.setValue(100);
                                    btn_cancel.setText("Close");
                                }
                            });

                    sending = false;

                } catch (Exception e) {
                    //set error message
                    e.printStackTrace();
                    sending = false;
                    updateStatusLabel("Error: " + e.getLocalizedMessage());
                    try {
                        serverSocket.close();
                    } catch (Exception ex) {
                    }
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                        if (serverSocket != null) {
                            serverSocket.close();
                        }

                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_reciever = new javax.swing.JLabel();
        lbl_recieverValue = new javax.swing.JLabel();
        lbl_file = new javax.swing.JLabel();
        lbl_fileValue = new javax.swing.JLabel();
        lbl_size = new javax.swing.JLabel();
        lbl_sizeValue = new javax.swing.JLabel();
        lbl_progress = new javax.swing.JLabel();
        pgb_progress = new javax.swing.JProgressBar();
        btn_cancel = new javax.swing.JButton();
        lbl_status = new javax.swing.JLabel();
        lbl_statusValue = new javax.swing.JLabel();
        lbl_timeLeft = new javax.swing.JLabel();
        lbl_timeLeftValue = new javax.swing.JLabel();
        lbl_note = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Send File"));
        setMaximumSize(null);

        lbl_reciever.setText("Reciever:");

        lbl_recieverValue.setText("username");

        lbl_file.setText("Filename:");

        lbl_fileValue.setText("filename");

        lbl_size.setText("Size:");

        lbl_sizeValue.setText("1234 MB");

        lbl_progress.setText("Progress:");

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        lbl_status.setText("Status:");

        lbl_statusValue.setText("Waiting...");

        lbl_timeLeft.setText("Time left:");

        lbl_timeLeftValue.setText("??:??:??");

        lbl_note.setText("<html><b>Note:</b> At least one of you has to have an open port for the transfer to work. Your port is: "+ Settings.getFiletTansferPort());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_note, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addComponent(pgb_progress, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_progress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                        .addComponent(lbl_timeLeft)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_timeLeftValue, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_status, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_file, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_reciever, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(lbl_size, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_recieverValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                            .addComponent(lbl_fileValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                            .addComponent(lbl_sizeValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                            .addComponent(lbl_statusValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)))
                    .addComponent(btn_cancel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_reciever)
                    .addComponent(lbl_recieverValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_file)
                    .addComponent(lbl_fileValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_size)
                    .addComponent(lbl_sizeValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_status)
                    .addComponent(lbl_statusValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_progress)
                    .addComponent(lbl_timeLeftValue)
                    .addComponent(lbl_timeLeft))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pgb_progress, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_note)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        if (btn_cancel.getText().equals("Cancel")) {
            sending = false;
            Client.send(Protocol.cancelTransfer(lbl_recieverValue.getText(), lbl_fileValue.getText()), null);
            TabOrganizer.closeFileTransferSendPanel(this);
        } else {
            TabOrganizer.closeFileTransferSendPanel(this);
        }

}//GEN-LAST:event_btn_cancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JLabel lbl_file;
    private javax.swing.JLabel lbl_fileValue;
    private javax.swing.JLabel lbl_note;
    private javax.swing.JLabel lbl_progress;
    private javax.swing.JLabel lbl_reciever;
    private javax.swing.JLabel lbl_recieverValue;
    private javax.swing.JLabel lbl_size;
    private javax.swing.JLabel lbl_sizeValue;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JLabel lbl_statusValue;
    private javax.swing.JLabel lbl_timeLeft;
    private javax.swing.JLabel lbl_timeLeftValue;
    private javax.swing.JProgressBar pgb_progress;
    // End of variables declaration//GEN-END:variables
}