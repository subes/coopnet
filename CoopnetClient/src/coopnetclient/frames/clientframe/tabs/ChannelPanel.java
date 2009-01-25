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

import coopnetclient.Globals;
import coopnetclient.frames.listeners.ChatInputKeyListener;
import coopnetclient.frames.renderers.ChannelRoomStatusRenderer;
import coopnetclient.frames.renderers.UsersInRoomTableCellRenderer;
import coopnetclient.frames.models.RoomTableModel;
import coopnetclient.frames.components.PlayerListPopupMenu;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.ClosableTab;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.renderers.ChannelStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.frames.models.ChannelStatusListModel;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import coopnetclient.frames.renderers.RoomNameRenderer;
import coopnetclient.utils.Settings;
import coopnetclient.utils.ui.UserListFileDropHandler;
import coopnetclient.utils.launcher.Launcher;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.StyledDocument;

public class ChannelPanel extends javax.swing.JPanel implements ClosableTab {

    public String ID;
    private ChannelStatusListModel users;
    private RoomTableModel rooms;
    private PlayerListPopupMenu popup;
    public String name;
    public boolean isLaunchable = false;
    private ChannelStatusListCellRenderer renderer;

    /** Creates new form ChannelPanel */
    public ChannelPanel(String name) {
        this.name = name;
        ID = GameDatabase.getIDofGame(name);
        users = new ChannelStatusListModel();
        renderer = new ChannelStatusListCellRenderer(users);
        initComponents();
        coopnetclient.utils.ui.Colorizer.colorize(this);

        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());

        //Table
        rooms = new RoomTableModel(tbl_roomList,users);
        tbl_roomList.setModel(rooms);
        tbl_roomList.setAutoCreateRowSorter(true);
        tbl_roomList.setRowHeight(35);
        tbl_roomList.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        tbl_roomList.getColumnModel().getColumn(0).setMinWidth(45);
        tbl_roomList.getColumnModel().getColumn(0).setMaxWidth(46);
        tbl_roomList.getColumnModel().getColumn(0).setPreferredWidth(45);
        tbl_roomList.getColumnModel().getColumn(1).setPreferredWidth(800);
        tbl_roomList.getColumnModel().getColumn(2).setPreferredWidth(300);
        tbl_roomList.getColumnModel().getColumn(3).setMinWidth(65);
        tbl_roomList.getColumnModel().getColumn(3).setMaxWidth(66);
        tbl_roomList.getColumnModel().getColumn(3).setPreferredWidth(66);
                
        ChannelRoomStatusRenderer picrend = new ChannelRoomStatusRenderer();
        picrend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_roomList.setDefaultRenderer(RoomTableModel.RoomType.class, picrend);
        
        DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
        rend.setHorizontalAlignment(SwingConstants.CENTER);
        rend.putClientProperty("html.disable", Boolean.TRUE);
        tbl_roomList.setDefaultRenderer(String.class, rend);

        UsersInRoomTableCellRenderer userrend = new UsersInRoomTableCellRenderer(rooms);
        userrend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_roomList.setDefaultRenderer(RoomTableModel.PlayersInRoom.class, userrend);

        RoomNameRenderer roomnamerenderer = new RoomNameRenderer(rooms);
        tbl_roomList.setDefaultRenderer(RoomTableModel.RoomName.class, roomnamerenderer);

        tp_chatInput.addKeyListener(new ChatInputKeyListener(ChatInputKeyListener.CHANNEL_CHAT_MODE, this.name));

        popup = new PlayerListPopupMenu(PlayerListPopupMenu.GENERAL_MODE, lst_userList);
        lst_userList.setComponentPopupMenu(popup);
        lst_userList.setDragEnabled(true);
        lst_userList.setDropMode(DropMode.USE_SELECTION);
        lst_userList.setTransferHandler(new UserListFileDropHandler());
 
        disableButtons();
        enableButtons();
        
        String message = GameDatabase.getWelcomeMessage(name);
        if(message != null && message.length()> 0){
            printMainChatMessage(name, message, ChatStyles.DEFAULT);
        }
    }

    public void setAway(String playername){
        users.setAway(playername);
    }

    public void unSetAway(String playername){
        users.unSetAway(playername);
    }

    public void gameClosed(String playername) {
        users.playerClosedGame(playername);
        rooms.setLaunchedStatus(playername, false);
    }

    public void hideRoomList() {
        pnl_roomActions.setVisible(false);
        sp_vertical.setDividerSize(0);
    }

    public void setPlayingStatus(String player) {
        users.playerLaunchedGame(player);
        rooms.setLaunchedStatus(player, true);
    }

    public void updateSleepMode() {
        tp_chatOutput.setEnabled(!Globals.getSleepModeStatus());
        if (Globals.getSleepModeStatus()) {
            tp_chatOutput.setToolTipText("<html>Sleep mode: Channel chat is inactive!<br>Press refresh button or write a chat message to exit sleep mode.");
        } else {
            tp_chatOutput.setToolTipText(null);
        }
    }

    @Override
    public void requestFocus() {        
        tp_chatInput.requestFocusInWindow();
    }

    public void customCodeForColorizer() {
        if (coopnetclient.utils.Settings.getColorizeText()) {
            tp_chatInput.setForeground(coopnetclient.utils.Settings.getUserMessageColor());
        }

        //Fix color of current/next input
        if (tp_chatInput.getText().length() > 0) {
            tp_chatInput.setText(tp_chatInput.getText());
        } else {
            tp_chatInput.setText("\n");
            tp_chatInput.setText("");
        }

        if (coopnetclient.utils.Settings.getColorizeBody()) {
            tp_chatOutput.setBackground(coopnetclient.utils.Settings.getBackgroundColor());
        }
    }

    public void setLaunchable(boolean value) {
        this.isLaunchable = value;
        enableButtons();
    }

    public void addPlayerToRoom(String hostname, String playername) {
        rooms.addPlayerToRoom(hostname, playername);
        users.playerEnteredRoom(playername);
    }

    public void addRoomToTable(String roomname,String modName, String hostname, int maxplayers, int type) {
        rooms.addRoomToTable(roomname, modName, hostname, maxplayers, type);
        users.playerEnteredRoom(hostname);
    }

    public void addPlayerToChannel(String name) {
        users.playerEnteredChannel(name);
    }

    public void disableButtons() {
        btn_create.setEnabled(false);
        btn_join.setEnabled(false);
    }

    public void enableButtons() {
        if(this.isLaunchable){
            if(Launcher.isPlaying()){
                 if(TabOrganizer.getRoomPanel()==null && Launcher.getLaunchedGame().equals(name) && !Launcher.isPlayingInstantLaunch()){
                    btn_create.setEnabled(true);
                    if(tbl_roomList.getSelectedRow() != -1){
                        btn_join.setEnabled(true);
                    }else{
                        btn_join.setEnabled(false);
                    }
                 }
            }else{
                if(TabOrganizer.getRoomPanel()==null){
                    btn_create.setEnabled(true);
                    if(tbl_roomList.getSelectedRow() != -1){
                        btn_join.setEnabled(true);
                    }else{
                        btn_join.setEnabled(false);
                    }
                }
            }
        }
    }

    public int getSelectedRoomListRowIndex() {
        return tbl_roomList.getSelectedRow();
    }

    public void printMainChatMessage(String name, String message, ChatStyles modeStyle) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();
        coopnetclient.utils.ui.ColoredChatHandler.addColoredText(name, message, modeStyle, doc, scrl_chatOutput, tp_chatOutput);
    }

    public void removePlayerFromChannel(String playername) {
        users.playerLeftChannel(playername);
    }

    public void removePlayerFromRoom(String hostname, String playername) {
        rooms.removePlayerFromRoom(hostname, playername);
        users.playerLeftRoom(playername);
    }

    public void removeRoomFromTable(String hostname) {
        statusSetOnRoomClose(hostname);
        rooms.removeElement(hostname);
    }

    private void statusSetOnRoomClose(String hostname) {
        int idx = rooms.indexOf(hostname);
        if (idx == -1) {
            return;
        }
        if(idx == tbl_roomList.getSelectedRow()){
            tbl_roomList.clearSelection();
            enableButtons();
        }

        String _users = rooms.getUserList(idx);
        String tmp[] = _users.split("<br>");
        for (String s : tmp) {
            users.playerLeftRoom(s);
        }
    }

    public boolean updatePlayerName(String oldname, String newname) {
        boolean found = false;
        found = rooms.updateName(oldname, newname) ||found;
        found = users.updateName(oldname, newname) ||found ;        
        return found;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sp_vertical = new javax.swing.JSplitPane();
        sp_chatHorizontal = new javax.swing.JSplitPane();
        sp_chatVertical = new javax.swing.JSplitPane();
        scrl_chatOutput = new javax.swing.JScrollPane();
        tp_chatOutput = new javax.swing.JTextPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        pnl_userList = new javax.swing.JPanel();
        scrl_userList = new javax.swing.JScrollPane();
        lst_userList = new javax.swing.JList();
        pnl_roomActions = new javax.swing.JPanel();
        btn_create = new javax.swing.JButton();
        btn_join = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        scrl_roomList = new javax.swing.JScrollPane();
        tbl_roomList = new javax.swing.JTable();

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(350, 400));

        sp_vertical.setDividerSize(10);
        sp_vertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_vertical.setResizeWeight(0.5);
        sp_vertical.setFocusable(false);
        sp_vertical.setOneTouchExpandable(true);
        sp_vertical.setPreferredSize(new java.awt.Dimension(350, 400));

        sp_chatHorizontal.setBorder(null);
        sp_chatHorizontal.setDividerSize(3);
        sp_chatHorizontal.setResizeWeight(1.0);
        sp_chatHorizontal.setFocusable(false);
        sp_chatHorizontal.setPreferredSize(new java.awt.Dimension(350, 200));

        sp_chatVertical.setBorder(null);
        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);
        sp_chatVertical.setPreferredSize(new java.awt.Dimension(350, 100));

        scrl_chatOutput.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_chatOutput.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrl_chatOutput.setEnabled(false);
        scrl_chatOutput.setFocusable(false);
        scrl_chatOutput.setMinimumSize(new java.awt.Dimension(150, 50));
        scrl_chatOutput.setPreferredSize(new java.awt.Dimension(150, 150));
        scrl_chatOutput.setRequestFocusEnabled(false);
        scrl_chatOutput.setVerifyInputWhenFocusTarget(false);
        scrl_chatOutput.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scrl_chatOutputComponentResized(evt);
            }
        });

        tp_chatOutput.setEditable(false);
        tp_chatOutput.setAutoscrolls(false);
        tp_chatOutput.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        tp_chatOutput.setMinimumSize(new java.awt.Dimension(150, 50));
        tp_chatOutput.setNextFocusableComponent(tp_chatInput);
        tp_chatOutput.setPreferredSize(new java.awt.Dimension(150, 50));
        tp_chatOutput.setVerifyInputWhenFocusTarget(false);
        tp_chatOutput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tp_chatOutputFocusLost(evt);
            }
        });
        tp_chatOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tp_chatOutputKeyTyped(evt);
            }
        });
        scrl_chatOutput.setViewportView(tp_chatOutput);

        sp_chatVertical.setTopComponent(scrl_chatOutput);

        scrl_chatInput.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_chatInput.setFocusable(false);
        scrl_chatInput.setMinimumSize(new java.awt.Dimension(7, 24));

        tp_chatInput.setFocusCycleRoot(false);
        tp_chatInput.setPreferredSize(new java.awt.Dimension(6, 24));
        scrl_chatInput.setViewportView(tp_chatInput);

        sp_chatVertical.setRightComponent(scrl_chatInput);

        sp_chatHorizontal.setLeftComponent(sp_chatVertical);

        pnl_userList.setFocusable(false);
        pnl_userList.setPreferredSize(new java.awt.Dimension(150, 80));
        pnl_userList.setLayout(new java.awt.GridBagLayout());

        scrl_userList.setFocusable(false);
        scrl_userList.setMinimumSize(new java.awt.Dimension(100, 50));

        lst_userList.setModel(users);
        lst_userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_userList.setAutoscrolls(false);
        lst_userList.setCellRenderer(renderer);
        lst_userList.setFixedCellHeight(20);
        lst_userList.setFocusable(false);
        lst_userList.setMinimumSize(new java.awt.Dimension(30, 50));
        lst_userList.setPreferredSize(null);
        lst_userList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_userListMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lst_userListMouseExited(evt);
            }
        });
        lst_userList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lst_userListMouseMoved(evt);
            }
        });
        scrl_userList.setViewportView(lst_userList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnl_userList.add(scrl_userList, gridBagConstraints);

        sp_chatHorizontal.setRightComponent(pnl_userList);

        sp_vertical.setBottomComponent(sp_chatHorizontal);

        pnl_roomActions.setFocusable(false);
        pnl_roomActions.setMinimumSize(new java.awt.Dimension(100, 70));
        pnl_roomActions.setPreferredSize(new java.awt.Dimension(350, 200));

        btn_create.setMnemonic(KeyEvent.VK_C);
        btn_create.setText("Create");
        btn_create.setFocusable(false);
        btn_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create(evt);
            }
        });

        btn_join.setMnemonic(KeyEvent.VK_J);
        btn_join.setText("Join");
        btn_join.setFocusable(false);
        btn_join.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                join(evt);
            }
        });

        btn_refresh.setMnemonic(KeyEvent.VK_R);
        btn_refresh.setText("Refresh");
        btn_refresh.setFocusable(false);
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh(evt);
            }
        });

        scrl_roomList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_roomList.setAutoscrolls(true);
        scrl_roomList.setFocusable(false);
        scrl_roomList.setMaximumSize(null);
        scrl_roomList.setMinimumSize(null);
        scrl_roomList.setPreferredSize(new java.awt.Dimension(100, 50));

        tbl_roomList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_roomList.setFillsViewportHeight(true);
        tbl_roomList.setFocusable(false);
        tbl_roomList.setMaximumSize(null);
        tbl_roomList.setMinimumSize(null);
        tbl_roomList.setPreferredSize(null);
        tbl_roomList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_roomList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_roomListMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_roomListMousePressed(evt);
            }
        });
        scrl_roomList.setViewportView(tbl_roomList);

        javax.swing.GroupLayout pnl_roomActionsLayout = new javax.swing.GroupLayout(pnl_roomActions);
        pnl_roomActions.setLayout(pnl_roomActionsLayout);
        pnl_roomActionsLayout.setHorizontalGroup(
            pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_roomActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_create)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_join)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_refresh)
                .addContainerGap(236, Short.MAX_VALUE))
            .addComponent(scrl_roomList, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
        );
        pnl_roomActionsLayout.setVerticalGroup(
            pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_roomActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_create)
                    .addComponent(btn_join)
                    .addComponent(btn_refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrl_roomList, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
        );

        pnl_roomActionsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_create, btn_join, btn_refresh});

        sp_vertical.setLeftComponent(pnl_roomActions);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_vertical, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_vertical, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private void create(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create
        Globals.openCreateRoomFrame(this.name);
    }//GEN-LAST:event_create

    private void join(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_join
        try {
            if (rooms.isSelectedRoomPassworded()) {
                Globals.openJoinRoomPasswordFrame(this.name, rooms.getSelectedHostName());
                return;
            }
            String tmp = null;
            tmp = rooms.getSelectedHostName();
            if (tmp != null) {
                Protocol.joinRoom(this.name, tmp, "");
                disableButtons();
            }
        } catch (Exception g) {
            g.printStackTrace();
        }
    }//GEN-LAST:event_join

    private void refresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh
        rooms.clear();
        users.refresh();
        Protocol.refreshRoomsAndPlayers(this.name);
        if(Settings.getSleepEnabled() && Globals.getSleepModeStatus()){
            Globals.setSleepModeStatus(false);
        }
        
        //Disable button for some secs, so that user cant spam refresh
        btn_refresh.setEnabled(false);
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(3000);
                return null;
            }
            @Override
            protected void done() {
                btn_refresh.setEnabled(true);
            }
        }.execute();
        //Same via invokeLater -- for reference
        /*new Thread(){
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Thread(){
                    @Override
                    public void run() {
                        btn_refresh.setEnabled(false);
                    }
                });
                try {
                    sleep(3000);
                } catch (InterruptedException ex) {}
                SwingUtilities.invokeLater(new Thread(){
                    @Override
                    public void run() {
                        btn_refresh.setEnabled(true);
                    }
                });
            }
        }.start();*/
    }//GEN-LAST:event_refresh

    private void lst_userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseClicked
        if( !lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).equals(Globals.getThisPlayer_loginName())){
            lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
        }else{
            lst_userList.clearSelection();
        }
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            String selectedname = (String) lst_userList.getSelectedValue();
            if(selectedname != null && !selectedname.equals("") && !selectedname.equals(Globals.getThisPlayer_loginName())){
                TabOrganizer.openPrivateChatPanel(selectedname, true);
                TabOrganizer.putFocusOnTab(selectedname);
            }
        }
}//GEN-LAST:event_lst_userListMouseClicked

    private void tbl_roomListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_roomListMouseClicked
        if (evt.getClickCount() == 2 && btn_join.isEnabled() && evt.getButton() == MouseEvent.BUTTON1) {
            btn_join.doClick();
        }
}//GEN-LAST:event_tbl_roomListMouseClicked

    private void tp_chatOutputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tp_chatOutputFocusLost
        StyledDocument doc = tp_chatOutput.getStyledDocument();
        tp_chatOutput.setSelectionStart(doc.getLength());
        tp_chatOutput.setSelectionEnd(doc.getLength());
}//GEN-LAST:event_tp_chatOutputFocusLost

private void tp_chatOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tp_chatOutputKeyTyped
    char c = evt.getKeyChar();
    if (!evt.isControlDown()) {
        tp_chatInput.setText(tp_chatInput.getText() + c);
        tp_chatInput.requestFocusInWindow();
    }
}//GEN-LAST:event_tp_chatOutputKeyTyped

private void lst_userListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseMoved
    if (!popup.isVisible()) {
        int idx = lst_userList.locationToIndex(evt.getPoint());
        Rectangle rec = lst_userList.getCellBounds(idx, idx);
        if(rec == null){
            return;
        }
        if(!rec.contains(evt.getPoint())){
            lst_userList.clearSelection();
            return;
        }
        if(idx == lst_userList.getSelectedIndex()){
            return;
        }
        String selected = lst_userList.getModel().getElementAt(idx).toString();
        if (selected !=null && selected.length()>0) {
            if (!selected.equals(Globals.getThisPlayer_loginName())) {
                lst_userList.setSelectedIndex(idx);
            }else{
                lst_userList.clearSelection();
            }
        } else {
            lst_userList.clearSelection();
        }
    }
}//GEN-LAST:event_lst_userListMouseMoved

private void lst_userListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseExited
    if(!popup.isVisible()){
        lst_userList.clearSelection();
    }
}//GEN-LAST:event_lst_userListMouseExited

private void scrl_chatOutputComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_scrl_chatOutputComponentResized
    int start, end;
    start = tp_chatOutput.getSelectionStart();
    end = tp_chatOutput.getSelectionEnd();
    tp_chatOutput.setSelectionStart(start-1);
    tp_chatOutput.setSelectionEnd(end-1);
    tp_chatOutput.setSelectionStart(start);
    tp_chatOutput.setSelectionEnd(end);
}//GEN-LAST:event_scrl_chatOutputComponentResized

private void tbl_roomListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_roomListMousePressed
    enableButtons();
}//GEN-LAST:event_tbl_roomListMousePressed

    public int getChannelChatHorizontalposition() {
        return sp_chatHorizontal.getDividerLocation();
    }

    public int getChannelChatVerticalposition() {
        return sp_chatVertical.getDividerLocation();
    }

    public int getChannelVerticalposition() {
        return sp_vertical.getDividerLocation();
    }

    public RoomTableModel getTableModel() {
        return rooms;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_create;
    private javax.swing.JButton btn_join;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JList lst_userList;
    private javax.swing.JPanel pnl_roomActions;
    private javax.swing.JPanel pnl_userList;
    private javax.swing.JScrollPane scrl_chatInput;
    private javax.swing.JScrollPane scrl_chatOutput;
    private javax.swing.JScrollPane scrl_roomList;
    private javax.swing.JScrollPane scrl_userList;
    private javax.swing.JSplitPane sp_chatHorizontal;
    private javax.swing.JSplitPane sp_chatVertical;
    private javax.swing.JSplitPane sp_vertical;
    private javax.swing.JTable tbl_roomList;
    private javax.swing.JTextPane tp_chatInput;
    private javax.swing.JTextPane tp_chatOutput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void closeTab() {
        TabOrganizer.closeChannelPanel(this);
    }

    @Override
    public boolean isCurrentlyClosable() {
        return true;
    }

}
