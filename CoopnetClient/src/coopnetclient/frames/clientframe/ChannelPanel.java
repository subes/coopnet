/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of Coopnet.

    Coopnet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Coopnet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.frames.clientframe;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.frames.CreateRoomFrame;
import coopnetclient.frames.RoomJoinPasswordFrame;
import coopnetclient.modules.listeners.ChatInputKeyListener;
import coopnetclient.modules.renderers.RoomPasswordPicTableCellRenderer;
import coopnetclient.modules.renderers.UsersInRoomTableCellRenderer;
import coopnetclient.modules.models.RoomTableModel;
import coopnetclient.modules.components.PlayerListPopupMenu;
import coopnetclient.Protocol;
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
        coopnetclient.modules.Colorizer.colorize(this);

        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());

        //Table
        rooms = new RoomTableModel(tbl_roomList);
        tbl_roomList.setModel(rooms);
        tbl_roomList.setAutoCreateRowSorter(true);
        tbl_roomList.setRowHeight(35);
        
        //Set horizontal alignment for table cells
        /* This doesnt work, so i have to go the long way -> RommTableModel.java seems to suck, a finer approach would be nice ^^
        for(int i = 0; i < tbl_RoomList.getColumnCount(); i++){
            DefaultTableCellRenderer rend = (DefaultTableCellRenderer)((TableColumn)tbl_RoomList.getColumnModel().getColumn(i)).getCellRenderer();
            rend.setHorizontalAlignment(SwingConstants.CENTER);
        }*/
        
        RoomPasswordPicTableCellRenderer picrend = new RoomPasswordPicTableCellRenderer();
        picrend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_roomList.setDefaultRenderer(Boolean.class, picrend);
        
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
        
        disablebuttons();
        enablebuttons();
    }

    public void gameClosed(String playername) {
        users.playerClosedGame(playername);
    }

    public void SetPlayingStatus(String player) {
        users.playerLaunchedGame(player);
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

    public void addmembertoroom(String hostname, String playername) {
        rooms.joinedroom(hostname, playername);
        users.playerEnteredRoom(playername);
    }

    public void addnewroom(String roomname, String hostname, String limitstr, boolean passworded) {
        rooms.addnewroom(roomname, hostname, limitstr, passworded);
        users.playerEnteredRoom(hostname);
    }

    public void adduser(String name) {
        users.playerEnteredChannel(name);
    }

    public void disablebuttons() {
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

    public void mainchat(String name, String message, int modeStyle) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();

        coopnetclient.modules.ColoredChatHandler.addColoredText(name, message, modeStyle, doc, scrl_chatOutput, tp_chatOutput);
    }

    public void removeplayerfromplayerlist(String playername) {
        users.playerLeftChannel(playername);
    }

    public void removeplayerfromroomonlist(String hostname, String playername) {
        rooms.leftroom(hostname, playername);
        users.playerLeftRoom(playername);
    }

    public void removeroomfromlist(String hostname) {
        statusSetOnRoomClose(hostname);
        rooms.removeElement(hostname);
    }

    private void statusSetOnRoomClose(String hostname) {
        int idx = rooms.indexOf(hostname);
        if (idx == -1) {
            return;
        }
        String _users = rooms.getuserslist(idx);
        String tmp[] = _users.split("<br>");
        for (String s : tmp) {
            users.playerLeftRoom(s);
        }
    }

    public void updatename(String oldname, String newname) {
        rooms.updatename(oldname, newname);
        users.updatename(oldname, newname);
        if (Globals.getRoomPanel() != null) {
            Globals.getRoomPanel().updateName(oldname, newname);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp_vertical = new javax.swing.JSplitPane();
        sp_chatHorizontal = new javax.swing.JSplitPane();
        scrl_userList = new javax.swing.JScrollPane();
        lst_userList = new javax.swing.JList();
        sp_chatVertical = new javax.swing.JSplitPane();
        scrl_chatOutput = new javax.swing.JScrollPane();
        tp_chatOutput = new javax.swing.JTextPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
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

        scrl_userList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_userList.setFocusable(false);
        scrl_userList.setMinimumSize(new java.awt.Dimension(100, 50));

        lst_userList.setModel(users);
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
        });
        scrl_userList.setViewportView(lst_userList);

        sp_chatHorizontal.setRightComponent(scrl_userList);

        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);
        sp_chatVertical.setPreferredSize(new java.awt.Dimension(350, 100));

        scrl_chatOutput.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_chatOutput.setEnabled(false);
        scrl_chatOutput.setFocusable(false);
        scrl_chatOutput.setPreferredSize(new java.awt.Dimension(150, 150));
        scrl_chatOutput.setRequestFocusEnabled(false);
        scrl_chatOutput.setVerifyInputWhenFocusTarget(false);

        tp_chatOutput.setEditable(false);
        tp_chatOutput.setAutoscrolls(false);
        tp_chatOutput.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        tp_chatOutput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatOutput.setNextFocusableComponent(tp_chatInput);
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

        btn_leaveChannel.setText("Leave Channel");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(btn_leaveChannel))
            .addComponent(scrl_roomList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        pnl_roomActionsLayout.setVerticalGroup(
            pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_roomActionsLayout.createSequentialGroup()
                .addGroup(pnl_roomActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_create)
                    .addComponent(btn_join)
                    .addComponent(btn_refresh)
                    .addComponent(btn_leaveChannel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_roomList, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
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
        if (Globals.getRoomCreationFrame() != null) {
            Globals.getRoomCreationFrame().dispose();
        }
        Globals.setRoomCreationFrame(new CreateRoomFrame(this.name));
        Globals.getRoomCreationFrame().setVisible(true);
    }//GEN-LAST:event_create

    private void join(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_join
        try {
            if (rooms.selectedispassworded()) {
                if (Globals.getRoomCreationFrame() != null) {
                    Globals.getRoomCreationFrame().dispose();
                }
                Globals.setRoomCreationFrame(new RoomJoinPasswordFrame(rooms.getselectedhost(), this.name));
                Globals.getRoomCreationFrame().setVisible(true);
                return;
            }
            String tmp = null;
            tmp = rooms.getselectedhost();
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
                btn_refresh.setEnabled(false);
                try {
                    sleep(3000);
                } catch (InterruptedException ex) {}
                btn_refresh.setEnabled(true);
            }
        }.start();
    }//GEN-LAST:event_refresh

    private void lst_userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseClicked
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            String _name = (String) lst_userList.getSelectedValue();
            Globals.getClientFrame().newPrivateChat(_name);
            Globals.getClientFrame().showPMTab(_name);
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
        Globals.getClientFrame().removeChannel(this.name);
}//GEN-LAST:event_btn_leaveChannelActionPerformed

private void tp_chatOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tp_chatOutputKeyTyped
    char c = evt.getKeyChar();
    if (!evt.isControlDown()) {
        tp_chatInput.setText(tp_chatInput.getText() + c);
        tp_chatInput.requestFocus();
    }
}//GEN-LAST:event_tp_chatOutputKeyTyped

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
    private javax.swing.JButton btn_refresh;
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
