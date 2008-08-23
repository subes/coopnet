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
package coopnetclient.frames.clientframe.panels;

import coopnetclient.Client;
import coopnetclient.Protocol;
import coopnetclient.enums.ContactStatuses;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.Settings;
import coopnetclient.modules.components.ContactListPopupMenu;
import coopnetclient.modules.components.mutablelist.DefaultListCellEditor;
import coopnetclient.modules.models.ContactListModel;
import coopnetclient.modules.renderers.ContactListRenderer;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ContactListPanel extends javax.swing.JPanel {

    public static ImageIcon ContactListIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/contacts.png"));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon FavouritesIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/favourites.png"));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    private static ContactListPopupMenu popup;
    ContactListModel model;

    /** Creates new form PlayerListPanel */
    public ContactListPanel() {
        initComponents();
    }

    public ContactListPanel(ContactListModel model) {
        this.model = model;
        initComponents();
        ContactList.setModel(model);
        ContactList.setCellRenderer(new ContactListRenderer(model));
        ContactList.setListCellEditor(new DefaultListCellEditor(new JTextField()));
        popup = new ContactListPopupMenu(ContactList);
        ContactList.setComponentPopupMenu(popup);
        popup.refreshMoveToMenu();
        refreshFavourites();
        jl_FavouritesList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    public void setTabAlignment(boolean left){
        if(left){
            tp_QuickPanel.setTabPlacement(JTabbedPane.LEFT);
        }
        else{
            tp_QuickPanel.setTabPlacement(JTabbedPane.RIGHT);
        }
    }

    public void refreshFavourites() {
        final Vector<String> favs = Settings.getFavourites();
        jl_FavouritesList.setModel(new javax.swing.AbstractListModel() {

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
        jl_FavouritesList.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tp_QuickPanel = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ContactList = new coopnetclient.modules.components.mutablelist.EditableJlist();
        jp_FavouritesPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jl_FavouritesList = new javax.swing.JList();

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(200, 200));

        tp_QuickPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tp_QuickPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tp_QuickPanel.setDoubleBuffered(true);
        tp_QuickPanel.setFocusable(false);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFocusable(false);
        jScrollPane1.setMinimumSize(null);

        ContactList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ContactList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ContactList.setFocusable(false);
        ContactList.setMaximumSize(null);
        ContactList.setMinimumSize(null);
        ContactList.setPreferredSize(new java.awt.Dimension(150, 200));
        ContactList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContactListMouseClicked(evt);
            }
        });
        ContactList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ContactListMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(ContactList);

        tp_QuickPanel.addTab("", ContactListIcon, jScrollPane1, "Contact List");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jl_FavouritesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jl_FavouritesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_FavouritesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jl_FavouritesListMouseClicked(evt);
            }
        });
        jl_FavouritesList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jl_FavouritesListMouseMoved(evt);
            }
        });
        jScrollPane2.setViewportView(jl_FavouritesList);

        javax.swing.GroupLayout jp_FavouritesPanelLayout = new javax.swing.GroupLayout(jp_FavouritesPanel);
        jp_FavouritesPanel.setLayout(jp_FavouritesPanelLayout);
        jp_FavouritesPanelLayout.setHorizontalGroup(
            jp_FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        jp_FavouritesPanelLayout.setVerticalGroup(
            jp_FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );

        tp_QuickPanel.addTab("", FavouritesIcon, jp_FavouritesPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tp_QuickPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tp_QuickPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void ContactListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContactListMouseClicked
    if (evt.getClickCount() == 2 && ContactList.getSelectedIndex() > -1) {
        String selected = ContactList.getSelectedValue().toString();
        if (model.getGroupNames().contains(selected)) {
            model.toggleGroupClosedStatus(selected);
        } else {
            if (model.getStatus(selected) != ContactStatuses.OFFLINE) {
                TabOrganizer.openPrivateChatPanel(selected, true);
            }
        }
    }
}//GEN-LAST:event_ContactListMouseClicked

private void jl_FavouritesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jl_FavouritesListMouseClicked
    Client.send(Protocol.JoinChannel(jl_FavouritesList.getSelectedValue().toString()), null);
}//GEN-LAST:event_jl_FavouritesListMouseClicked

private void ContactListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContactListMouseMoved
    ContactList.setSelectedIndex(ContactList.locationToIndex(evt.getPoint()));
}//GEN-LAST:event_ContactListMouseMoved

private void jl_FavouritesListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jl_FavouritesListMouseMoved
    jl_FavouritesList.setSelectedIndex(jl_FavouritesList.locationToIndex(evt.getPoint()));
}//GEN-LAST:event_jl_FavouritesListMouseMoved
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private coopnetclient.modules.components.mutablelist.EditableJlist ContactList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jl_FavouritesList;
    private javax.swing.JPanel jp_FavouritesPanel;
    private javax.swing.JTabbedPane tp_QuickPanel;
    // End of variables declaration//GEN-END:variables
}
