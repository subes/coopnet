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

package coopnetclient.frames;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import java.awt.Color;

public class JoinRoomPasswordFrame extends javax.swing.JFrame {

    private String host_name = null;
    private String channel = null;
    private String ID = null;
    
    /** Creates new form RoomJoinPasswordFrame */
    public JoinRoomPasswordFrame(String host_name,String channel) {
        initComponents();        
        hideWrongPasswordNotification();
        this.host_name = host_name;
        this.channel=channel;
    }
    
    public JoinRoomPasswordFrame(String ID) {
        initComponents();
        hideWrongPasswordNotification();
        this.ID = ID;        
    }
    
    public void showWrongPasswordNotification(){
        lbl_errormsg.setForeground(Color.red);
        lbl_errormsg.setText("Wrong Password!");
    }
    
    public void hideWrongPasswordNotification(){
        lbl_errormsg.setText(" ");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_join = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        pnl_input = new javax.swing.JPanel();
        lbl_roomPassword = new javax.swing.JLabel();
        pf_roomPassword = new javax.swing.JPasswordField();
        lbl_errormsg = new javax.swing.JLabel();

        setTitle("Enter password");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_join.setText("Join");
        btn_join.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                join(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter password"));

        lbl_roomPassword.setText("<html>This room is password protected,<br> please enter the correct password:");

        pf_roomPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_roomPasswordActionPerformed(evt);
            }
        });

        lbl_errormsg.setForeground(new java.awt.Color(255, 0, 0));
        lbl_errormsg.setText("Wrong Password!");

        javax.swing.GroupLayout pnl_inputLayout = new javax.swing.GroupLayout(pnl_input);
        pnl_input.setLayout(pnl_inputLayout);
        pnl_inputLayout.setHorizontalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pf_roomPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(lbl_roomPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(lbl_errormsg, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_inputLayout.setVerticalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addComponent(lbl_roomPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pf_roomPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_errormsg))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_join)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel)
                .addContainerGap(262, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_join)
                    .addComponent(btn_cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void join(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_join
        String passw = new String(pf_roomPassword.getPassword());
        if (host_name != null) {
            Client.send(Protocol.joinRoom(host_name, passw),channel);
        }else if(ID != null){
            Client.send(Protocol.joinRoomByID(ID, passw));
        }
        hideWrongPasswordNotification();
    }//GEN-LAST:event_join

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        Globals.closeRoomCreationFrame();
}//GEN-LAST:event_btn_cancelActionPerformed

private void pf_roomPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_roomPasswordActionPerformed
    btn_join.doClick();
}//GEN-LAST:event_pf_roomPasswordActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeRoomCreationFrame();
}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_join;
    private javax.swing.JLabel lbl_errormsg;
    private javax.swing.JLabel lbl_roomPassword;
    private javax.swing.JPasswordField pf_roomPassword;
    private javax.swing.JPanel pnl_input;
    // End of variables declaration//GEN-END:variables

}
