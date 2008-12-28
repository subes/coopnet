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
package coopnetclient.frames.clientframe.quickpanel;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.clientframe.quickpanel.tabs.VoiceChatPanel;
import coopnetclient.utils.Settings;
import coopnetclient.frames.components.ContactListPopupMenu;
import coopnetclient.frames.components.mutablelist.DefaultListCellEditor;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.frames.renderers.ContactListRenderer;
import coopnetclient.utils.ContactListFileDropHandler;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class QuickPanel extends javax.swing.JPanel {

    public static ImageIcon ContactListIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/contacts.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon FavouritesIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/favourites.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon ContactListIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/contacts_small.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon FavouritesIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/favourites_small.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon VoiceChatIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/voicechat.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    public static ImageIcon VoiceChatIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/voicechat_small.png")));//.getScaledInstance(56, 56, Image.SCALE_SMOOTH));
    private static ContactListPopupMenu popup;
    ContactListModel model;
    VoiceChatPanel voiceChatPanel;

    /** Creates new form PlayerListPanel */
    public QuickPanel() {
        initComponents();
    }

    public QuickPanel(ContactListModel model) {
        this.model = model;
        initComponents();
        voiceChatPanel = new VoiceChatPanel();
        tp_quickPanel.add("",voiceChatPanel);
        lst_contactList.setModel(model);
        lst_contactList.setCellRenderer(new ContactListRenderer(model));
        lst_contactList.setListCellEditor(new DefaultListCellEditor(new JTextField()));
        lst_contactList.setDragEnabled(true);
        lst_contactList.setDropMode(DropMode.USE_SELECTION);
        lst_contactList.setTransferHandler(new ContactListFileDropHandler());
        popup = new ContactListPopupMenu(lst_contactList);
        lst_contactList.setComponentPopupMenu(popup);
        refreshFavourites();
        lst_favouritesList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));        
        setTabIcons();
    }
        
    public VoiceChatPanel getVoiceChatPanel(){
        return voiceChatPanel;
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
    
    private void setTabIcons(){
        if(Settings.isquickTabIconSizeBig()){
            tp_quickPanel.setIconAt(0, ContactListIconBig);
            tp_quickPanel.setIconAt(1, FavouritesIconBig);           
            tp_quickPanel.setIconAt(2, VoiceChatIconBig);            
        }else{//small icons
            tp_quickPanel.setIconAt(0, ContactListIconSmall);
            tp_quickPanel.setIconAt(1, FavouritesIconSmall);
            tp_quickPanel.setIconAt(2, VoiceChatIconSmall);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        IconSizeSelector = new javax.swing.JPopupMenu();
        rbmi_bigIcons = new javax.swing.JRadioButtonMenuItem();
        rbmi_smallIcons = new javax.swing.JRadioButtonMenuItem();
        IconSizes = new javax.swing.ButtonGroup();
        tp_quickPanel = new javax.swing.JTabbedPane();
        scrl_contactlist = new javax.swing.JScrollPane();
        lst_contactList = new coopnetclient.frames.components.mutablelist.EditableJlist();
        pnl_favouritesList = new javax.swing.JPanel();
        scrl_favouritesList = new javax.swing.JScrollPane();
        lst_favouritesList = new javax.swing.JList();

        IconSizes.add(rbmi_bigIcons);
        rbmi_bigIcons.setSelected(Settings.isquickTabIconSizeBig());
        rbmi_bigIcons.setText("Big Icons");
        rbmi_bigIcons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmi_bigIconsActionPerformed(evt);
            }
        });
        IconSizeSelector.add(rbmi_bigIcons);

        IconSizes.add(rbmi_smallIcons);
        rbmi_smallIcons.setSelected(!Settings.isquickTabIconSizeBig());
        rbmi_smallIcons.setText("Small Icons");
        rbmi_smallIcons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmi_smallIconsActionPerformed(evt);
            }
        });
        IconSizeSelector.add(rbmi_smallIcons);

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(200, 200));

        tp_quickPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tp_quickPanel.setComponentPopupMenu(IconSizeSelector);
        tp_quickPanel.setDoubleBuffered(true);
        tp_quickPanel.setFocusable(false);
        tp_quickPanel.setMinimumSize(new java.awt.Dimension(0, 0));

        scrl_contactlist.setMinimumSize(new java.awt.Dimension(0, 0));

        lst_contactList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lst_contactList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_contactList.setFixedCellHeight(20);
        lst_contactList.setFocusable(false);
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
        scrl_contactlist.setViewportView(lst_contactList);

        tp_quickPanel.addTab("", scrl_contactlist);

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

        tp_quickPanel.addTab("", pnl_favouritesList);

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
    if(!popup.isVisible()){
        lst_contactList.setSelectedIndex(lst_contactList.locationToIndex(evt.getPoint()));
    }
    
    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1 && lst_contactList.getSelectedIndex() > -1) {
        if(popup.isClosing()){
            //Dont want to close/open a group accidentially!
            return;
        }
        
        if(lst_contactList.getSelectedValue() == null){
            return;
        }
        String selected = lst_contactList.getSelectedValue()+"";        
        if (model.getGroupNames().contains(selected)) {
            model.toggleGroupClosedStatus(selected);
        }
    }else{    
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2 && lst_contactList.getSelectedIndex() > -1) {
            if(lst_contactList.getSelectedValue() == null){
                return;
            }
            String selected = lst_contactList.getSelectedValue()+"";
            if (model.getStatus(selected) != ContactListElementTypes.OFFLINE 
                    && model.getStatus(selected) != ContactListElementTypes.GROUPNAME_OPEN 
                    && model.getStatus(selected) != ContactListElementTypes.GROUPNAME_CLOSED) {
                TabOrganizer.openPrivateChatPanel(selected, true);
            }
        }
    }
}//GEN-LAST:event_lst_contactListMouseClicked

private void lst_favouritesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseClicked
    if(!popup.isVisible()){
        lst_favouritesList.setSelectedIndex(lst_favouritesList.locationToIndex(evt.getPoint()));
    }
    
    if(evt.getButton() == MouseEvent.BUTTON1){
        Protocol.joinChannel(lst_favouritesList.getSelectedValue().toString());
    }
}//GEN-LAST:event_lst_favouritesListMouseClicked

private void lst_contactListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_contactListMouseMoved
    if (!popup.isVisible() ) {
        int idx = lst_contactList.locationToIndex(evt.getPoint());
        Rectangle rec = lst_contactList.getCellBounds(idx, idx);
        if(rec == null){
            return;
        }
        if(!rec.contains(evt.getPoint())){
            lst_contactList.clearSelection();
            return;
        }
        if(idx == lst_contactList.getSelectedIndex()){
            return;
        }
        String selected = lst_contactList.getModel().getElementAt(idx).toString();
        if (selected !=null && selected.length()>0) {
                lst_contactList.setSelectedIndex(idx);
        } else {
            lst_contactList.clearSelection();
        }
    }
}//GEN-LAST:event_lst_contactListMouseMoved

private void lst_favouritesListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseMoved
     if (!popup.isVisible()) {
        int idx = lst_favouritesList.locationToIndex(evt.getPoint());
        Rectangle rec = lst_favouritesList.getCellBounds(idx, idx);
        if(rec == null){
            return;
        }
        if(!rec.contains(evt.getPoint())){
            lst_favouritesList.clearSelection();
            return;
        }
        if(idx == lst_favouritesList.getSelectedIndex()){
            return;
        }
        String selected = lst_favouritesList.getModel().getElementAt(idx).toString();
        if (selected !=null && selected.length()>0) {
                lst_favouritesList.setSelectedIndex(idx);
        } else {
            lst_favouritesList.clearSelection();
        }
    }
}//GEN-LAST:event_lst_favouritesListMouseMoved

private void lst_contactListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_contactListMouseExited
    if(!popup.isVisible()){
        lst_contactList.clearSelection();
    }    
}//GEN-LAST:event_lst_contactListMouseExited

private void lst_favouritesListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_favouritesListMouseExited
    lst_favouritesList.clearSelection();
}//GEN-LAST:event_lst_favouritesListMouseExited

private void rbmi_bigIconsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmi_bigIconsActionPerformed
    Settings.setIsquickTabIconSizeBig(true);
    setTabIcons();
}//GEN-LAST:event_rbmi_bigIconsActionPerformed

private void rbmi_smallIconsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmi_smallIconsActionPerformed
    Settings.setIsquickTabIconSizeBig(false);
    setTabIcons();
}//GEN-LAST:event_rbmi_smallIconsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu IconSizeSelector;
    private javax.swing.ButtonGroup IconSizes;
    private coopnetclient.frames.components.mutablelist.EditableJlist lst_contactList;
    private javax.swing.JList lst_favouritesList;
    private javax.swing.JPanel pnl_favouritesList;
    private javax.swing.JRadioButtonMenuItem rbmi_bigIcons;
    private javax.swing.JRadioButtonMenuItem rbmi_smallIcons;
    private javax.swing.JScrollPane scrl_contactlist;
    private javax.swing.JScrollPane scrl_favouritesList;
    private javax.swing.JTabbedPane tp_quickPanel;
    // End of variables declaration//GEN-END:variables
}
