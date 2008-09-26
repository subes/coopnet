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
package coopnetclient.frames.clientframe;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Settings;
import coopnetclient.frames.components.ContactListPopupMenu;
import coopnetclient.frames.components.mutablelist.DefaultListCellEditor;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.frames.renderers.ContactListRenderer;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class QuickPanel extends javax.swing.JPanel {

    public static ImageIcon ContactListIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/contacts.png"));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon FavouritesIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/favourites.png"));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    private static ContactListPopupMenu popup;
    ContactListModel model;

    /** Creates new form PlayerListPanel */
    public QuickPanel() {
        initComponents();
    }

    public QuickPanel(ContactListModel model) {
        this.model = model;
        initComponents();
        lst_contactList.setModel(model);
        lst_contactList.setCellRenderer(new ContactListRenderer(model));
        lst_contactList.setListCellEditor(new DefaultListCellEditor(new JTextField()));
        popup = new ContactListPopupMenu(lst_contactList);
        lst_contactList.setComponentPopupMenu(popup);
        popup.refreshMoveToMenu();
        refreshFavourites();
        lst_favouritesList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public void setTabAlignment(boolean left){
        if(left){
            tp_quickPanel.setTabPlacement(JTabbedPane.LEFT);
        }
        else{
            tp_quickPanel.setTabPlacement(JTabbedPane.RIGHT);
        }
    }

    public void refreshFavourites() {
        final Vector<String> favs = Settings.getFavourites();
        lst_favouritesList.setModel(new javax.swing.AbstractListModel() {

            Vector<String> strings = favs;

            @Override
            public int getSize() {
                return strings.size();
            }

            @Override
            public Object getElementAt(int i) {
                return strings.get(i);
            }
        });
        lst_favouritesList.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tp_quickPanel = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        lst_contactList = new coopnetclient.frames.components.mutablelist.EditableJlist();
        pnl_favouritesList = new javax.swing.JPanel();
        scrl_favouritesList = new javax.swing.JScrollPane();
        lst_favouritesList = new javax.swing.JList();

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(200, 200));

        tp_quickPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tp_quickPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tp_quickPanel.setDoubleBuffered(true);
        tp_quickPanel.setFocusable(false);

        lst_contactList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lst_contactList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_contactList.setFocusable(false);
        lst_contactList.setMaximumSize(null);
        lst_contactList.setMinimumSize(new java.awt.Dimension(50, 10));
        lst_contactList.setPreferredSize(null);
        lst_contactList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_contactListMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lst_contactListMouseExited(evt);
            }
        });
        lst_contactList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lst_contactListMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(lst_contactList);

        tp_quickPanel.addTab("", ContactListIcon, jScrollPane1);

        lst_favouritesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lst_favouritesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_favouritesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_favouritesListMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lst_favouritesListMouseExited(evt);
            }
        });
        lst_favouritesList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lst_favouritesListMouseMoved(evt);
            }
        });
        scrl_favouritesList.setViewportView(lst_favouritesList);

        javax.swing.GroupLayout pnl_favouritesListLayout = new javax.swing.GroupLayout(pnl_favouritesList);
        pnl_favouritesList.setLayout(pnl_favouritesListLayout);
        pnl_favouritesListLayout.setHorizontalGroup(
            pnl_favouritesListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrl_favouritesList, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        pnl_favouritesListLayout.setVerticalGroup(
            pnl_favouritesListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrl_favouritesList, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
        );

        tp_quickPanel.addTab("", FavouritesIcon, pnl_favouritesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tp_quickPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tp_quickPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void lst_contactListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_contactListMouseClicked
    if (evt.getClickCount() == 2 && lst_contactList.getSelectedIndex() > -1) {
        if(lst_contactList.getSelectedValue() == null){
            return;
        }
        String selected = lst_contactList.getSelectedValue()+"";        
        if (model.getGroupNames().contains(selected)) {
            model.toggleGroupClosedStatus(selected);
        } else {
            if (model.getStatus(selected) != ContactListElementTypes.OFFLINE) {
                TabOrganizer.openPrivateChatPanel(selected, true);
            }
        }
    }
}//GEN-LAST:event_lst_contactListMouseClicked

private void lst_favouritesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseClicked
    Protocol.joinChannel(lst_favouritesList.getSelectedValue().toString());
}//GEN-LAST:event_lst_favouritesListMouseClicked

private void lst_contactListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_contactListMouseMoved
    if(!Globals.getContactListPopupIsUp()){
        lst_contactList.setSelectedIndex(lst_contactList.locationToIndex(evt.getPoint()));
    }
}//GEN-LAST:event_lst_contactListMouseMoved

private void lst_favouritesListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseMoved
     if(!Globals.getContactListPopupIsUp()){
        lst_favouritesList.setSelectedIndex(lst_favouritesList.locationToIndex(evt.getPoint()));
     }
}//GEN-LAST:event_lst_favouritesListMouseMoved

private void lst_contactListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_contactListMouseExited
    if(!Globals.getContactListPopupIsUp()){
        lst_contactList.clearSelection();
    }    
}//GEN-LAST:event_lst_contactListMouseExited

private void lst_favouritesListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseExited
    lst_favouritesList.clearSelection();
}//GEN-LAST:event_lst_favouritesListMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private coopnetclient.frames.components.mutablelist.EditableJlist lst_contactList;
    private javax.swing.JList lst_favouritesList;
    private javax.swing.JPanel pnl_favouritesList;
    private javax.swing.JScrollPane scrl_favouritesList;
    private javax.swing.JTabbedPane tp_quickPanel;
    // End of variables declaration//GEN-END:variables
}
