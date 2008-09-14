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
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.protocol.Protocol;
import coopnetclient.modules.models.SortedListModel;
import java.awt.event.MouseEvent;

public class ChannelListFrame extends javax.swing.JFrame {

    private SortedListModel channels = new SortedListModel();
    
    /** Creates new form ChannelListFrame */
    public ChannelListFrame() {
        initComponents();
        lst_channelList.removeAll();
        for (String st : GameDatabase.gameNamesAsStringArray()) {
            if (st.length() > 0) {
                channels.add(st);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_filter = new javax.swing.JLabel();
        tf_filter = new javax.swing.JTextField();
        scrl_channelList = new javax.swing.JScrollPane();
        lst_channelList = new javax.swing.JList();
        jb_joinChannelButton = new javax.swing.JButton();
        lbl_channellist = new javax.swing.JLabel();
        btn_cancel = new javax.swing.JButton();

        setTitle("Join channel");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbl_filter.setText("Filter:");

        tf_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_filterActionPerformed(evt);
            }
        });

        lst_channelList.setModel(channels);
        lst_channelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_channelListMouseClicked(evt);
            }
        });
        scrl_channelList.setViewportView(lst_channelList);

        jb_joinChannelButton.setText("Join selected channel");
        jb_joinChannelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jb_joinChannelButtonActionPerformed(evt);
            }
        });

        lbl_channellist.setText("Available Channels:");

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrl_channelList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lbl_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_filter, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jb_joinChannelButton)
                        .addGap(10, 10, 10)
                        .addComponent(btn_cancel))
                    .addComponent(lbl_channellist, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_filter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_channellist, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_channelList, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel)
                    .addComponent(jb_joinChannelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jb_joinChannelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jb_joinChannelButtonActionPerformed
        Client.send(Protocol.JoinChannel((String) lst_channelList.getSelectedValue()), null);
        Globals.closeChannelListFrame();
}//GEN-LAST:event_jb_joinChannelButtonActionPerformed

    private void tf_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_filterActionPerformed
        lst_channelList.removeAll();
        channels.clear();
        String filter = tf_filter.getText();
        for (String st : GameDatabase.gameNamesAsStringArray()) {
            if (st.toLowerCase().contains(filter.toLowerCase())) {
                channels.add(st);
            }
        }
        this.repaint();
}//GEN-LAST:event_tf_filterActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        Globals.closeChannelListFrame();
}//GEN-LAST:event_btn_cancelActionPerformed

private void lst_channelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_channelListMouseClicked
    if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
        jb_joinChannelButton.doClick();
    }
}//GEN-LAST:event_lst_channelListMouseClicked

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeChannelListFrame();
}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton jb_joinChannelButton;
    private javax.swing.JLabel lbl_channellist;
    private javax.swing.JLabel lbl_filter;
    private javax.swing.JList lst_channelList;
    private javax.swing.JScrollPane scrl_channelList;
    private javax.swing.JTextField tf_filter;
    // End of variables declaration//GEN-END:variables

}
