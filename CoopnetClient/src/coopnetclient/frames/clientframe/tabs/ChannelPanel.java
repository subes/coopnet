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

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.modules.listeners.ChatInputKeyListener;
import coopnetclient.modules.renderers.RoomPasswordPicTableCellRenderer;
import coopnetclient.modules.renderers.UsersInRoomTableCellRenderer;
import coopnetclient.modules.models.RoomTableModel;
import coopnetclient.modules.components.PlayerListPopupMenu;
import coopnetclient.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.renderers.ChannelStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.modules.models.ChannelStatusListModel;
import coopnetclient.modules.listeners.HyperlinkMouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.StyledDocument;

public class ChannelPanel extends javax.swing.JPanel {

    public String ID;
    private ChannelStatusListModel users = new ChannelStatusListModel();
    private RoomTableModel rooms;
    private PlayerListPopupMenu mypopup;
    public String name;
    public boolean isLaunchable = false;
    private ChannelStatusListCellRenderer renderer = new ChannelStatusListCellRenderer(users);

    /** Creates new form ChannelPanel */
    public ChannelPanel(String name) {
        this.name = name;
        ID = GameDatabase.IDofGame(name);
        initComponents();
        btn_leaveChannel1.setVisible(false);
        coopnetclient.modules.Colorizer.colorize(this);

        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());

        //Table
        rooms = new RoomTableModel(tbl_roomList);
        tbl_roomList.setModel(rooms);
        tbl_roomList.setAutoCreateRowSorter(true);
        tbl_roomList.setRowHeight(35);
                
        RoomPasswordPicTableCellRenderer picrend = new RoomPasswordPicTableCellRenderer();
        picrend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_roomList.setDefaultRenderer(Integer.class, picrend);
        
        DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
        rend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_roomList.setDefaultRenderer(String.class, rend);
        
        TableColumn col = tbl_roomList.getColumnModel().getColumn(3);
        UsersInRoomTableCellRenderer userrend = new UsersInRoomTableCellRenderer(rooms);
        userrend.setHorizontalAlignment(SwingConstants.CENTER);
        col.setCellRenderer(userrend);

        tp_chatInput.addKeyListener(new ChatInputKeyListener(ChatInputKeyListener.CHANNEL_CHAT_MODE, this.name));

        mypopup = new PlayerListPopupMenu(PlayerListPopupMenu.GENERAL_MODE, lst_userList);
        lst_userList.setComponentPopupMenu(mypopup);
        
        disableButtons();
        enablebuttons();
        if(ID.equals("TST")){
            TabOrganizer.openGameDataEditor();
        }
    }

    public void gameClosed(String playername) {
        users.playerClosedGame(playername);
        rooms.setLaunchedStatus(playername, false);
    }

    public void hideRoomList() {
        pnl_roomActions.setVisible(false);
        sp_vertical.setDividerSize(0);
        btn_leaveChannel1.setVisible(true);
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
        tp_chatInput.requestFocus();
    }

    public void customCodeForColorizer() {
        if (coopnetclient.modules.Settings.getColorizeText()) {
            tp_chatInput.setForeground(coopnetclient.modules.Settings.getUserMessageColor());
        }

        //Fix color of current/next input
        if (tp_chatInput.getText().length() > 0) {
            tp_chatInput.setText(tp_chatInput.getText());
        } else {
            tp_chatInput.setText("\n");
            tp_chatInput.setText("");
        }

        if (coopnetclient.modules.Settings.getColorizeBody()) {
            tp_chatOutput.setBackground(coopnetclient.modules.Settings.getBackgroundColor());
        }
    }

    public void setLaunchable(boolean value) {
        this.isLaunchable = value;
        enablebuttons();
    }

    public void addPlayerToRoom(String hostname, String playername) {
        rooms.addPlayerToRoom(hostname, playername);
        users.playerEnteredRoom(playername);
    }

    public void addRoomToTable(String roomname, String hostname, int maxplayers, int type) {
        rooms.addRoomToTable(roomname, hostname, maxplayers, type);
        users.playerEnteredRoom(hostname);
    }

    public void addPlayerToChannel(String name) {
        users.playerEnteredChannel(name);
    }

    public void disableButtons() {
        btn_create.setEnabled(false);
        btn_join.setEnabled(false);
    }

    public void enablebuttons() {
        if (this.isLaunchable) {
            btn_create.setEnabled(true);
            btn_join.setEnabled(true);
        }
    }

    public int getSelectedRoomListRowIndex() {
        return tbl_roomList.getSelectedRow();
    }

    public void printMainChatMessage(String name, String message, ChatStyles modeStyle) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();

        coopnetclient.modules.ColoredChatHandler.addColoredText(name, message, modeStyle, doc, scrl_chatOutput, tp_chatOutput);
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
        String _users = rooms.getUserList(idx);
        String tmp[] = _users.split("<br>");
        for (String s : tmp) {
            users.playerLeftRoom(s);
        }
    }

    public void updatePlayerName(String oldname, String newname) {
        rooms.updateName(oldname, newname);
        users.updatename(oldname, newname);
        if (TabOrganizer.getRoomPanel() != null) {
            TabOrganizer.getRoomPanel().updatePlayerName(oldname, newname);
        }
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
        jPanel1 = new javax.swing.JPanel();
        scrl_userList = new javax.swing.JScrollPane();
        lst_userList = new javax.swing.JList();
        btn_leaveChannel1 = new javax.swing.JButton();
        pnl_roomActions = new javax.swing.JPanel();
        btn_create = new javax.swing.JButton();
        btn_join = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        scrl_roomList = new javax.swing.JScrollPane();
        tbl_roomList = new javax.swing.JTable();
        btn_leaveChannel = new javax.swing.JButton();

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(350, 400));

        sp_vertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_vertical.setResizeWeight(0.3);
        sp_vertical.setFocusable(false);
        sp_vertical.setOneTouchExpandable(true);

        sp_chatHorizontal.setDividerSize(3);
        sp_chatHorizontal.setResizeWeight(1.0);
        sp_chatHorizontal.setFocusable(false);
        sp_chatHorizontal.setPreferredSize(new java.awt.Dimension(200, 100));

        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);
        sp_chatVertical.setPreferredSize(new java.awt.Dimension(350, 100));

        scrl_chatOutput.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_chatOutput.setEnabled(false);
        scrl_chatOutput.setFocusable(false);
        scrl_chatOutput.setMinimumSize(new java.awt.Dimension(150, 50));
        scrl_chatOutput.setPreferredSize(new java.awt.Dimension(150, 150));
        scrl_chatOutput.setRequestFocusEnabled(false);
        scrl_chatOutput.setVerifyInputWhenFocusTarget(false);

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

        jPanel1.setLayout(new java.awt.GridBagLayout());

        scrl_userList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(scrl_userList, gridBagConstraints);

        btn_leaveChannel1.setText("Leave");
        btn_leaveChannel1.setFocusable(false);
        btn_leaveChannel1.setMinimumSize(new java.awt.Dimension(100, 25));
        btn_leaveChannel1.setPreferredSize(new java.awt.Dimension(100, 25));
        btn_leaveChannel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_leaveChannel1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(btn_leaveChannel1, gridBagConstraints);

        sp_chatHorizontal.setRightComponent(jPanel1);

        sp_vertical.setBottomComponent(sp_chatHorizontal);

        pnl_roomActions.setFocusable(false);
        pnl_roomActions.setMinimumSize(new java.awt.Dimension(100, 70));

        btn_create.setText("Create");
        btn_create.setFocusable(false);
        btn_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create(evt);
            }
        });

        btn_join.setText("Join");
        btn_join.setFocusable(false);
        btn_join.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                join(evt);
            }
        });

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
        tbl_roomList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_roomListMouseClicked(evt);
            }
        });
        scrl_roomList.setViewportView(tbl_roomList);

        btn_leaveChannel.setText("Leave");
        btn_leaveChannel.setFocusable(false);
        btn_leaveChannel.setMaximumSize(new java.awt.Dimension(100, 25));
        btn_leaveChannel.setMinimumSize(new java.awt.Dimension(100, 25));
        btn_leaveChannel.setPreferredSize(new java.awt.Dimension(100, 25));
        btn_leaveChannel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_leaveChannelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_roomActionsLayout = new javax.swing.GroupLayout(pnl_roomActions);
        pnl_roomActions.setLayout(pnl_roomActionsLayout);
        pnl_roomActionsLayout.setHorizontalGroup(
            pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_roomActionsLayout.createSequentialGroup()
                .addComponent(btn_create)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_join)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(btn_leaveChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrl_roomList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        pnl_roomActionsLayout.setVerticalGroup(
            pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_roomActionsLayout.createSequentialGroup()
                .addGroup(pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_create)
                    .addComponent(btn_join)
                    .addComponent(btn_refresh)
                    .addComponent(btn_leaveChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_roomList, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
        );

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
                Client.send(Protocol.joinRoom(tmp, ""), this.name);
            }
        } catch (Exception g) {
            g.printStackTrace();
        }
    }//GEN-LAST:event_join

    private void refresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh
        Client.send(Protocol.refresh(), this.name);
        rooms.clear();
        users.refresh();
        Globals.setSleepModeStatus(false);
        
        //Disable button for some secs, so that user cant spam refresh
        new Thread() {

            @Override
            public void run() {
                try{
                    btn_refresh.setEnabled(false);
                    try {
                        sleep(3000);
                    } catch (InterruptedException ex) {}
                    btn_refresh.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }//GEN-LAST:event_refresh

    private void lst_userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseClicked
        if( !lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).equals(Globals.getThisPlayer_loginName())){
                lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
            }else{
                lst_userList.clearSelection();
            }
        if(evt.getButton() == MouseEvent.BUTTON3){
            lst_userList.getComponentPopupMenu().show(lst_userList, evt.getX(), evt.getY());
        }else
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

    private void btn_leaveChannelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_leaveChannelActionPerformed
        Client.send(Protocol.leaveChannel(), this.name);
        TabOrganizer.closeChannelPanel(this);
        if(ID.equals("TST")){
            TabOrganizer.closeGameDataEditor();
        }
}//GEN-LAST:event_btn_leaveChannelActionPerformed

private void tp_chatOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tp_chatOutputKeyTyped
    char c = evt.getKeyChar();
    if (!evt.isControlDown()) {
        tp_chatInput.setText(tp_chatInput.getText() + c);
        tp_chatInput.requestFocus();
    }
}//GEN-LAST:event_tp_chatOutputKeyTyped

private void btn_leaveChannel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_leaveChannel1ActionPerformed
    btn_leaveChannelActionPerformed(evt);
}//GEN-LAST:event_btn_leaveChannel1ActionPerformed

private void lst_userListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseMoved
    if(!Globals.getPlayerListPopupIsUp()){
        if( !lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).equals(Globals.getThisPlayer_loginName())){
            lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
        }else{
           lst_userList.clearSelection();
        }
    }
}//GEN-LAST:event_lst_userListMouseMoved

private void lst_userListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseExited
    if(!Globals.getPlayerListPopupIsUp()){
        lst_userList.clearSelection();
    }
}//GEN-LAST:event_lst_userListMouseExited

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
    private javax.swing.JButton btn_leaveChannel;
    private javax.swing.JButton btn_leaveChannel1;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JList lst_userList;
    private javax.swing.JPanel pnl_roomActions;
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

}