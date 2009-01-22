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
package coopnetclient.frames.clientframe.tabs;

import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
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
        for (String gameName : Settings.getFavouritesByName()) {
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
        listModel = new DefaultListModel();
        for(int i = 0; i < commentSenders.length;++i){
            listModel.addElement("<html><xmp>"+commentSenders[i] + "</xmp><br>" + commentTexts[i]);
        }
        lst_comments.setModel(listModel);
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

        lbl_description = new javax.swing.JLabel();
        sp_games = new javax.swing.JScrollPane();
        tr_games = new JTree(root);
        sp_description = new javax.swing.JScrollPane();
        ta_description = new javax.swing.JTextArea();
        sp_users = new javax.swing.JScrollPane();
        lst_users = new javax.swing.JList();
        lbl_comments = new javax.swing.JLabel();
        sp_comments = new javax.swing.JScrollPane();
        lst_comments = new javax.swing.JList();
        lbl_users = new javax.swing.JLabel();
        sp_commentInput = new javax.swing.JScrollPane();
        tp_postComment = new javax.swing.JTextPane();
        pnl_buttons = new javax.swing.JPanel();
        btn_post = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_signUp = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        pnl_filter = new javax.swing.JPanel();
        lbl_dateFilter = new javax.swing.JLabel();
        lbl_dateFilterFrom = new javax.swing.JLabel();
        cmb_from = new JComboBox(getDateArray());
        lbl_dateFilterTo = new javax.swing.JLabel();
        cmb_to = new JComboBox(getDateArray());

        setLayout(new java.awt.GridBagLayout());

        lbl_description.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_description, gridBagConstraints);

        sp_games.setViewportView(tr_games);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        add(sp_games, gridBagConstraints);

        ta_description.setColumns(20);
        ta_description.setRows(5);
        sp_description.setViewportView(ta_description);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(sp_description, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(sp_users, gridBagConstraints);

        lbl_comments.setText("Comments:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_comments, gridBagConstraints);

        lst_comments.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "comment 1", "comment 2", "comment 3", "comment 4", "comment 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        sp_comments.setViewportView(lst_comments);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 4);
        add(sp_comments, gridBagConstraints);

        lbl_users.setText("Participants:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_users, gridBagConstraints);

        tp_postComment.setText("<Your comment here>");
        sp_commentInput.setViewportView(tp_postComment);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sp_commentInput, gridBagConstraints);

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(pnl_buttons, gridBagConstraints);

        pnl_filter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lbl_dateFilter.setText("Date filter:");
        pnl_filter.add(lbl_dateFilter);

        lbl_dateFilterFrom.setText("From:");
        pnl_filter.add(lbl_dateFilterFrom);

        cmb_from.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_from.setMinimumSize(new java.awt.Dimension(100, 20));
        cmb_from.setPreferredSize(new java.awt.Dimension(100, 20));
        pnl_filter.add(cmb_from);

        lbl_dateFilterTo.setText("To:");
        pnl_filter.add(lbl_dateFilterTo);

        cmb_to.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_to.setMinimumSize(new java.awt.Dimension(100, 20));
        cmb_to.setPreferredSize(new java.awt.Dimension(100, 20));
        pnl_filter.add(cmb_to);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(pnl_filter, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_post;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_signUp;
    private javax.swing.JComboBox cmb_from;
    private javax.swing.JComboBox cmb_to;
    private javax.swing.JLabel lbl_comments;
    private javax.swing.JLabel lbl_dateFilter;
    private javax.swing.JLabel lbl_dateFilterFrom;
    private javax.swing.JLabel lbl_dateFilterTo;
    private javax.swing.JLabel lbl_description;
    private javax.swing.JLabel lbl_users;
    private javax.swing.JList lst_comments;
    private javax.swing.JList lst_users;
    private javax.swing.JPanel pnl_buttons;
    private javax.swing.JPanel pnl_filter;
    private javax.swing.JScrollPane sp_commentInput;
    private javax.swing.JScrollPane sp_comments;
    private javax.swing.JScrollPane sp_description;
    private javax.swing.JScrollPane sp_games;
    private javax.swing.JScrollPane sp_users;
    private javax.swing.JTextArea ta_description;
    private javax.swing.JTextPane tp_postComment;
    private javax.swing.JTree tr_games;
    // End of variables declaration//GEN-END:variables
}
