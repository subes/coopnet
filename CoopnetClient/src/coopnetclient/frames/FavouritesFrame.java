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

import coopnetclient.Globals;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.Settings;
import coopnetclient.frames.models.SortedListModel;
import java.util.Vector;
import javax.swing.DefaultListModel;

public class FavouritesFrame extends javax.swing.JFrame {
    
    private SortedListModel channels = new SortedListModel();
    
    /** Creates new form FavouritesFrame */
    public FavouritesFrame() {
        initComponents();
        for(String st:  GameDatabase.getAllGameNamesAsStringArray()){
            if(st.length()>0)
                channels.add(st);
        }
        lst_favourites.setListData(Settings.getFavourites());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_add = new javax.swing.JButton();
        btn_remove = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        lbl_filter = new javax.swing.JLabel();
        tf_filter = new javax.swing.JTextField();
        sp_lists = new javax.swing.JSplitPane();
        pnl_channels = new javax.swing.JPanel();
        lbl_channels = new javax.swing.JLabel();
        scrl_channels = new javax.swing.JScrollPane();
        lst_channels = new javax.swing.JList();
        pnl_favourites = new javax.swing.JPanel();
        lbl_favourites = new javax.swing.JLabel();
        scrl_favourites = new javax.swing.JScrollPane();
        lst_favourites = new javax.swing.JList();
        cb_showInstalledOnly = new javax.swing.JCheckBox();

        setTitle("Manage favourites");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_add.setText("Add to favourites");
        btn_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addActionPerformed(evt);
            }
        });

        btn_remove.setText("Remove from favourites");
        btn_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeActionPerformed(evt);
            }
        });

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        lbl_filter.setText("Filter:");

        tf_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_filterActionPerformed(evt);
            }
        });

        sp_lists.setDividerLocation(250);
        sp_lists.setResizeWeight(0.5);

        lbl_channels.setText("Channels:");

        lst_channels.setModel(channels);
        lst_channels.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_channelsMouseClicked(evt);
            }
        });
        scrl_channels.setViewportView(lst_channels);

        javax.swing.GroupLayout pnl_channelsLayout = new javax.swing.GroupLayout(pnl_channels);
        pnl_channels.setLayout(pnl_channelsLayout);
        pnl_channelsLayout.setHorizontalGroup(
            pnl_channelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_channelsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_channelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrl_channels, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .addGroup(pnl_channelsLayout.createSequentialGroup()
                        .addComponent(lbl_channels)
                        .addContainerGap(191, Short.MAX_VALUE))))
        );
        pnl_channelsLayout.setVerticalGroup(
            pnl_channelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_channelsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_channels)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_channels, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
        );

        sp_lists.setLeftComponent(pnl_channels);

        lbl_favourites.setText("Favourites:");

        lst_favourites.setModel(new DefaultListModel());
        scrl_favourites.setViewportView(lst_favourites);

        javax.swing.GroupLayout pnl_favouritesLayout = new javax.swing.GroupLayout(pnl_favourites);
        pnl_favourites.setLayout(pnl_favouritesLayout);
        pnl_favouritesLayout.setHorizontalGroup(
            pnl_favouritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_favouritesLayout.createSequentialGroup()
                .addGroup(pnl_favouritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_favourites)
                    .addComponent(scrl_favourites, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_favouritesLayout.setVerticalGroup(
            pnl_favouritesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_favouritesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_favourites)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_favourites, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
        );

        sp_lists.setRightComponent(pnl_favourites);

        cb_showInstalledOnly.setText("Show installed games only");
        cb_showInstalledOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_showInstalledOnlyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_close)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cb_showInstalledOnly)
                            .addComponent(tf_filter, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(sp_lists, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_add)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                .addComponent(btn_remove)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_filter)
                    .addComponent(tf_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_showInstalledOnly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_add)
                    .addComponent(btn_remove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_lists, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_close)
                .addGap(12, 12, 12))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_add, btn_close, btn_remove});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addActionPerformed
        if(lst_channels.getSelectedValue()!=null){
            Settings.addFavourite(lst_channels.getSelectedValue().toString());
            lst_favourites.setListData(Settings.getFavourites());
            Globals.getClientFrame().refreshFavourites();
        }
}//GEN-LAST:event_btn_addActionPerformed

    private void btn_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeActionPerformed
        if(lst_favourites.getSelectedValue()!=null){
            Settings.removeFavourite(lst_favourites.getSelectedValue().toString());
            lst_favourites.setListData(Settings.getFavourites());
            Globals.getClientFrame().refreshFavourites();
        }
}//GEN-LAST:event_btn_removeActionPerformed

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
        Globals.closeFavouritesFrame();
}//GEN-LAST:event_btn_closeActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Globals.getClientFrame().refreshFavourites();
        Globals.closeFavouritesFrame();
    }//GEN-LAST:event_formWindowClosing

    private void tf_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_filterActionPerformed
        lst_channels.removeAll();
        channels.clear();
        Vector<String> installedgames = GameDatabase.getInstalledGameNames();
        String filter = tf_filter.getText();
        for (String gameName : GameDatabase.getAllGameNamesAsStringArray()) {
            if (gameName.toLowerCase().contains(filter.toLowerCase())) {
                if (cb_showInstalledOnly.isSelected()) {
                    if (installedgames.contains(gameName)) {
                        channels.add(gameName);
                    }
                } else {
                    channels.add(gameName);
                }
            }
        }
        this.repaint();
}//GEN-LAST:event_tf_filterActionPerformed

private void lst_channelsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_channelsMouseClicked
    if(evt.getClickCount() == 2){
        btn_add.doClick();
    }
}//GEN-LAST:event_lst_channelsMouseClicked

private void cb_showInstalledOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_showInstalledOnlyActionPerformed
    tf_filterActionPerformed(null);
}//GEN-LAST:event_cb_showInstalledOnlyActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_add;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_remove;
    private javax.swing.JCheckBox cb_showInstalledOnly;
    private javax.swing.JLabel lbl_channels;
    private javax.swing.JLabel lbl_favourites;
    private javax.swing.JLabel lbl_filter;
    private javax.swing.JList lst_channels;
    private javax.swing.JList lst_favourites;
    private javax.swing.JPanel pnl_channels;
    private javax.swing.JPanel pnl_favourites;
    private javax.swing.JScrollPane scrl_channels;
    private javax.swing.JScrollPane scrl_favourites;
    private javax.swing.JSplitPane sp_lists;
    private javax.swing.JTextField tf_filter;
    // End of variables declaration//GEN-END:variables

}
