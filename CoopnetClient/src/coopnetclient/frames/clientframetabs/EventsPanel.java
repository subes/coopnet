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
package coopnetclient.frames.clientframetabs;

import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.settings.Favourites;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

public class EventsPanel extends javax.swing.JPanel {

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Event browser");
    private DefaultMutableTreeNode favsNode = new DefaultMutableTreeNode("Favourites");
    private DefaultMutableTreeNode installedNode = new DefaultMutableTreeNode("Installed Games");
    private DefaultMutableTreeNode allGamesNode = new DefaultMutableTreeNode("All Games");
    private Long currentlyDisplayedEventID = null;

    private static class EventNode extends DefaultMutableTreeNode {

        private Long ID;

        public EventNode(Long ID, String title) {
            super(title);
            this.ID = ID;
        }

        public Long getID() {
            return ID;
        }
    }

    /** Creates new form EventsPanel */
    public EventsPanel() {
        initComponents();
        initNodes();
        tr_games.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tr_games.setShowsRootHandles(true);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        //custom icons possible
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        tr_games.setCellRenderer(renderer);
        tr_games.putClientProperty("JTree.lineStyle", "Angled");//None for no lines
    }

    private String[] getDateArray(){
        String[] dates = new String[30];
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MMM.d");
        String date = sdf.format(calendar.getTime());
        dates[0] = date;
        for(int i = 1; i < 30;++i){
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date = sdf.format(calendar.getTime());
            dates[i] = date;
        }
        return dates;
    }

    private void initNodes() {
        root.removeAllChildren();
        root.add(favsNode);
        root.add(installedNode);
        root.add(allGamesNode);
        updateFavouritesNode();
        allGamesNode.removeAllChildren();
        for (String gameName : GameDatabase.getAllGameNamesAsStringArray()) {
            allGamesNode.add(new DefaultMutableTreeNode(gameName));
        }
        installedNode.removeAllChildren();
        for (String gameName : GameDatabase.getInstalledGameNames()) {
            installedNode.add(new DefaultMutableTreeNode(gameName));
        }
    }

    public synchronized void updateFavouritesNode() {
        favsNode.removeAllChildren();
        for (String gameName : Favourites.getFavouritesByName()) {
            favsNode.add(new DefaultMutableTreeNode(gameName));
        }
    }

    public void addEvent(String gameName, Long eventID, String eventTitle) {
        EventNode newEvent = new EventNode(eventID, eventTitle);
        Enumeration e = installedNode.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof EventNode) {
                EventNode oldEvent = (EventNode) o;
                if (oldEvent.getID().equals(eventID)) {
                    installedNode.remove(oldEvent);
                    installedNode.add(newEvent);
                }
            }
        }
        e = favsNode.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof EventNode) {
                EventNode oldEvent = (EventNode) o;
                if (oldEvent.getID().equals(eventID)) {
                    favsNode.remove(oldEvent);
                    favsNode.add(newEvent);
                }
            }
        }
        e = allGamesNode.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof EventNode) {
                EventNode oldEvent = (EventNode) o;
                if (oldEvent.getID().equals(eventID)) {
                    allGamesNode.remove(oldEvent);
                    allGamesNode.add(newEvent);
                }
            }
        }
    }

    public void displayDetails(Long ID, String author,String title,String description, String[] users,String[] commentSenders,String[] commentTexts){
        currentlyDisplayedEventID = ID;
        //description
        String fullDescription = "";
        fullDescription += "Author: "+author;
        fullDescription += "\nTitle: " + title;
        fullDescription += "\nFull desciption: " +description;
        ta_description.setText(fullDescription);
        //users
        DefaultListModel listModel = new DefaultListModel();
        for(String user : users ){
            listModel.addElement(user);
        }
        lst_users.setModel(listModel);
        //comments
        //TODO new way to print chat messages
        /*listModel = new DefaultListModel();
        for(int i = 0; i < commentSenders.length;++i){
            listModel.addElement("<html><xmp>"+commentSenders[i] + "</xmp><br>" + commentTexts[i]);
        }
        lst_comments.setModel(listModel);*/
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        pnl_leftHalf = new javax.swing.JPanel();
        lbl_dateFilter = new javax.swing.JLabel();
        lbl_dateFilterFrom = new javax.swing.JLabel();
        cmb_from = new JComboBox(getDateArray());
        lbl_dateFilterTo = new javax.swing.JLabel();
        cmb_to = new JComboBox(getDateArray());
        sp_games = new javax.swing.JScrollPane();
        tr_games = new JTree(root);
        pnl_rightHalf = new javax.swing.JPanel();
        pnl_buttons = new javax.swing.JPanel();
        btn_post = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_signUp = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        sp_rightSide = new javax.swing.JSplitPane();
        pnl_topRightQuarter = new javax.swing.JPanel();
        lbl_description = new javax.swing.JLabel();
        sp_description = new javax.swing.JScrollPane();
        ta_description = new javax.swing.JTextArea();
        sp_users = new javax.swing.JScrollPane();
        lst_users = new javax.swing.JList();
        lbl_users = new javax.swing.JLabel();
        pnel_bottomRightrQuarter = new javax.swing.JPanel();
        lbl_comments = new javax.swing.JLabel();
        sp_commentInput = new javax.swing.JScrollPane();
        tp_postComment = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tp_comments = new javax.swing.JTextPane();

        pnl_leftHalf.setLayout(new java.awt.GridBagLayout());

        lbl_dateFilter.setText("Date filter:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_leftHalf.add(lbl_dateFilter, gridBagConstraints);

        lbl_dateFilterFrom.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 6);
        pnl_leftHalf.add(lbl_dateFilterFrom, gridBagConstraints);

        cmb_from.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_from.setMinimumSize(new java.awt.Dimension(100, 20));
        cmb_from.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_leftHalf.add(cmb_from, gridBagConstraints);

        lbl_dateFilterTo.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 6);
        pnl_leftHalf.add(lbl_dateFilterTo, gridBagConstraints);

        cmb_to.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_to.setMinimumSize(new java.awt.Dimension(100, 20));
        cmb_to.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnl_leftHalf.add(cmb_to, gridBagConstraints);

        sp_games.setViewportView(tr_games);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnl_leftHalf.add(sp_games, gridBagConstraints);

        jSplitPane1.setLeftComponent(pnl_leftHalf);

        pnl_rightHalf.setLayout(new java.awt.GridBagLayout());

        pnl_buttons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btn_post.setText("Post new");
        pnl_buttons.add(btn_post);

        btn_edit.setText("Edit");
        pnl_buttons.add(btn_edit);

        btn_signUp.setText("Participate");
        pnl_buttons.add(btn_signUp);

        btn_refresh.setText("Refresh");
        pnl_buttons.add(btn_refresh);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnl_rightHalf.add(pnl_buttons, gridBagConstraints);

        sp_rightSide.setBorder(null);
        sp_rightSide.setDividerLocation(200);
        sp_rightSide.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnl_topRightQuarter.setLayout(new java.awt.GridBagLayout());

        lbl_description.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_topRightQuarter.add(lbl_description, gridBagConstraints);

        ta_description.setColumns(20);
        ta_description.setRows(5);
        sp_description.setViewportView(ta_description);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnl_topRightQuarter.add(sp_description, gridBagConstraints);

        lst_users.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lst_users.setMaximumSize(null);
        lst_users.setMinimumSize(null);
        lst_users.setPreferredSize(null);
        sp_users.setViewportView(lst_users);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pnl_topRightQuarter.add(sp_users, gridBagConstraints);

        lbl_users.setText("Participants:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_topRightQuarter.add(lbl_users, gridBagConstraints);

        sp_rightSide.setTopComponent(pnl_topRightQuarter);

        pnel_bottomRightrQuarter.setLayout(new java.awt.GridBagLayout());

        lbl_comments.setText("Comments:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnel_bottomRightrQuarter.add(lbl_comments, gridBagConstraints);

        tp_postComment.setText("<Your comment here>");
        sp_commentInput.setViewportView(tp_postComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnel_bottomRightrQuarter.add(sp_commentInput, gridBagConstraints);

        jScrollPane1.setViewportView(tp_comments);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        pnel_bottomRightrQuarter.add(jScrollPane1, gridBagConstraints);

        sp_rightSide.setRightComponent(pnel_bottomRightrQuarter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnl_rightHalf.add(sp_rightSide, gridBagConstraints);

        jSplitPane1.setRightComponent(pnl_rightHalf);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_post;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_signUp;
    private javax.swing.JComboBox cmb_from;
    private javax.swing.JComboBox cmb_to;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbl_comments;
    private javax.swing.JLabel lbl_dateFilter;
    private javax.swing.JLabel lbl_dateFilterFrom;
    private javax.swing.JLabel lbl_dateFilterTo;
    private javax.swing.JLabel lbl_description;
    private javax.swing.JLabel lbl_users;
    private javax.swing.JList lst_users;
    private javax.swing.JPanel pnel_bottomRightrQuarter;
    private javax.swing.JPanel pnl_buttons;
    private javax.swing.JPanel pnl_leftHalf;
    private javax.swing.JPanel pnl_rightHalf;
    private javax.swing.JPanel pnl_topRightQuarter;
    private javax.swing.JScrollPane sp_commentInput;
    private javax.swing.JScrollPane sp_description;
    private javax.swing.JScrollPane sp_games;
    private javax.swing.JSplitPane sp_rightSide;
    private javax.swing.JScrollPane sp_users;
    private javax.swing.JTextArea ta_description;
    private javax.swing.JTextPane tp_comments;
    private javax.swing.JTextPane tp_postComment;
    private javax.swing.JTree tr_games;
    // End of variables declaration//GEN-END:variables
}
