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

package coopnetclient.panels;

import coopnetclient.Client;
import coopnetclient.Protocol;
import coopnetclient.Settings;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class FileTransferSend extends javax.swing.JPanel {

    File data;
    boolean sending = true;
    long totalbytes = 0;
    private ServerSocket serverSocket = null;
    String reciever;
    String filename;

    /** Creates new form FileTransfer */
    public FileTransferSend(String reciever, File sentfile) {
        initComponents();
        this.reciever= reciever;
        this.filename=sentfile.getName();
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
            lbl_sizeValue.setText(lbl_sizeValue.getText() + "bytes");
        }
        if (i == 1) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + "kilobytes");
        }
        if (i == 2) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + "megabytes");
        }
        if (i == 3) {
            lbl_sizeValue.setText(lbl_sizeValue.getText() + "gigabytes!!!");
        }
        if (i > 3) {
            lbl_sizeValue.setText("TOO BIG!!! Please cancel!");
        }
        coopnetclient.coloring.Colorizer.colorize(this);
    }

    public String getReciever() {
        return lbl_recieverValue.getText();
    }

    public String getFilename() {
        return lbl_fileValue.getText();
    }

    public void Refused() {
        lbl_statusValue.setText("Peer refused transfer!");
        btn_cancel.setText("Close");
    }

    public void setTImeLeft(long time) {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        //time=time/1000;//scale to seconds
        seconds = (int) (time % 60);
        time = time / 60;   //scale to minutes
        minutes = (int) (time % 60);
        time = time / 60;
        hours = (int) time;
        lbl_timeLeftValue.setText(hours + ":" + minutes + ":" + seconds);
    }
    
    public void TurnAround(){
        
    }

    public void startsending(final String ip, final String port) {
        new Thread() {

            long starttime;

            @Override
            public void run() {
                long sent = 0;
                sending = true;
                lbl_statusValue.setText("Connecting...");
                try {
                    SocketChannel socket=null;
                    try{
                        socket = SocketChannel.open();
                        socket.connect(new InetSocketAddress(ip, new Integer(port)));
                    }
                    catch(Exception e){
                        Client.send(Protocol.turnTransferAround(reciever,filename), null);
                        startsending2(ip,port);
                    }
                    lbl_statusValue.setText("Transferring...");
                    starttime = System.currentTimeMillis();

                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    BufferedInputStream bi = new BufferedInputStream(new FileInputStream(data));
                    int readedbyte;
                    long currenttime;
                    long timeelapsed;
                    long j = 0;

                    while ((readedbyte = bi.read()) != -1 && sending) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                            sent++;
                        } else {
                            temp.flip();
                            socket.write(temp);
                            temp.rewind();
                            //dont forge to sent the new byte aswell :S
                            temp.put((byte) readedbyte);
                            sent++;
                            pgb_progress.setValue((int) (((sent * 1.0) / totalbytes) * 100));
                        }
                        j++;
                        if (j % 20000 == 0) {
                            currenttime = System.currentTimeMillis();
                            timeelapsed = currenttime - starttime;
                            timeelapsed = timeelapsed / 1000;
                            setTImeLeft((long) ((totalbytes - sent) / (sent * 1.0) * timeelapsed));
                        }
                    }
                    //send last chunk
                    if (temp.position() != 0) {
                        temp.flip();
                        socket.write(temp);
                        temp.rewind();
                    }
                    socket.close();
                    lbl_statusValue.setText("Transfer complete!");
                    pgb_progress.setValue(100);
                    btn_cancel.setText("Close");
                    sending = false;

                } catch (Exception e) {
                    //set error message
                    lbl_statusValue.setText("Error: " + e.getLocalizedMessage());
                }
            }
        }.start();
    }
    
    //turns around the connection, connect to the reciever
    public void startsending2(final String ip, final String port) {
        new Thread() {

            long starttime;

            @Override
            public void run() {
                long sent = 0;
                sending = true;
                lbl_statusValue.setText("Connecting...");
                try {
                    serverSocket = new ServerSocket(Settings.getFiletTansferPort());
                    Socket socket = serverSocket.accept();
                    SocketChannel socketChannel = socket.getChannel();
                    lbl_statusValue.setText("Transferring...");
                    starttime = System.currentTimeMillis();

                    ByteBuffer temp = ByteBuffer.allocate(1000);
                    BufferedInputStream bi = new BufferedInputStream(new FileInputStream(data));
                    int readedbyte;
                    long currenttime;
                    long timeelapsed;
                    long j = 0;

                    while ((readedbyte = bi.read()) != -1 && sending) {
                        if (temp.position() < temp.capacity()) {
                            temp.put((byte) readedbyte);
                            sent++;
                        } else {
                            temp.flip();
                            socketChannel.write(temp);
                            temp.rewind();
                            //dont forge to sent the new byte aswell :S
                            temp.put((byte) readedbyte);
                            sent++;
                            pgb_progress.setValue((int) (((sent * 1.0) / totalbytes) * 100));
                        }
                        j++;
                        if (j % 20000 == 0) {
                            currenttime = System.currentTimeMillis();
                            timeelapsed = currenttime - starttime;
                            timeelapsed = timeelapsed / 1000;
                            setTImeLeft((long) ((totalbytes - sent) / (sent * 1.0) * timeelapsed));
                        }
                    }
                    //send last chunk
                    if (temp.position() != 0) {
                        temp.flip();
                        socketChannel.write(temp);
                        temp.rewind();
                    }
                    serverSocket.close();
                    lbl_statusValue.setText("Transfer complete!");
                    pgb_progress.setValue(100);
                    btn_cancel.setText("Close");
                    sending = false;

                } catch (Exception e) {
                    //set error message
                    lbl_statusValue.setText("Error: " + e.getLocalizedMessage());
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pgb_progress, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_progress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
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
                            .addComponent(lbl_recieverValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(lbl_fileValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(lbl_sizeValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(lbl_statusValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
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
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        if (btn_cancel.getText().equals("Cancel")) {
            sending = false;
            Client.send(Protocol.CancelTransfer(lbl_recieverValue.getText(), lbl_fileValue.getText()), null);
            Client.mainFrame.removeTransferTab(lbl_recieverValue.getText(), lbl_fileValue.getText());
        } else {
            Client.mainFrame.removeTransferTab(lbl_recieverValue.getText(), lbl_fileValue.getText());
        }

}//GEN-LAST:event_btn_cancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JLabel lbl_file;
    private javax.swing.JLabel lbl_fileValue;
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
