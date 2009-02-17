/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.frames.listeners.ChatInputKeyListener;
import coopnetclient.frames.components.PlayerListPopupMenu;
import coopnetclient.frames.models.SortedListModel;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.interfaces.ClosableTab;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.components.ConnectingProgressBar;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.frames.renderers.RoomPlayerStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.ui.UserListFileDropHandler;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.launcher.launchinfos.DosboxLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.DropMode;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class RoomPanel extends javax.swing.JPanel implements ClosableTab {

    public static final String ROOMID_UNSUPPORTED = "ROOMID_UNSUPPORTED";
    private LaunchInfo launchInfo;
    private RoomData roomData;
    private SortedListModel users;
    private PlayerListPopupMenu popup;
    private HashMap<String, String> gamesettings = new HashMap<String, String>();
    private RoomPlayerStatusListCellRenderer roomStatusListCR;
    private boolean hamachiWasEnabled = false;
    private SwingWorker readyDisablerThread;
    private SwingWorker launchDisablerThread;
    private boolean wasReadyBeforeReInit = false;

    public RoomPanel(RoomData theRoomData) {
        this.roomData = theRoomData;
        this.users = new SortedListModel();
        users.add(Globals.getThisPlayerLoginName());

        initComponents();

        if (Client.getHamachiAddress().length() <= 0) {
            cb_useHamachi.setVisible(false);
        } else if (roomData.getHamachiIP().length() > 0) {
            hamachiWasEnabled = true;
            cb_useHamachi.setToolTipText("<html>Don't use this unless you have connection issues!<br>If you really need to use this consult with the room host!<br>Both you and the host have to be connected to <br>the same hamachi network!Otherwise it won't work!");
        }

        if (roomData.isHost()) {
            popup = new PlayerListPopupMenu(true, lst_userList);
            cb_useHamachi.setVisible(false);
            Hotkeys.bindHotKey(Hotkeys.ACTION_LAUNCH);
        } else {
            popup = new PlayerListPopupMenu(false, lst_userList);
        }
        lst_userList.setComponentPopupMenu(popup);

        roomStatusListCR = new RoomPlayerStatusListCellRenderer();
        lst_userList.setCellRenderer(roomStatusListCR);
        lst_userList.setDragEnabled(true);
        lst_userList.setDropMode(DropMode.USE_SELECTION);
        lst_userList.setTransferHandler(new UserListFileDropHandler());

        tp_chatInput.addKeyListener(new ChatInputKeyListener(ChatInputKeyListener.ROOM_CHAT_MODE, roomData.getChannel()));
        scrl_chatOutput.getTextPane().addMouseListener(new HyperlinkMouseListener());

        scrl_chatOutput.getTextPane().addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!evt.isControlDown()) {
                    tp_chatInput.setText(tp_chatInput.getText() + c);
                    tp_chatInput.requestFocusInWindow();
                    scrl_chatOutput.getTextPane().setSelectionStart(scrl_chatOutput.getTextPane().getDocument().getLength());
                    scrl_chatOutput.getTextPane().setSelectionEnd(scrl_chatOutput.getTextPane().getDocument().getLength());
                }
            }
        });

        if (!theRoomData.isHost()) {
            convertToJoinPanel();
        }

        Colorizer.colorize(this);

        chat("", theRoomData.getRoomName(), ChatStyles.USER);
        chat("", "room://" + theRoomData.getRoomID(), ChatStyles.USER);

        prgbar_connecting.setVisible(false);

        decideGameSettingsButtonVisility();
    }

    private void decideGameSettingsButtonVisility() {
        if (Launcher.isPlaying()) {
            btn_gameSettings.setEnabled(false);
        }

        if (GameDatabase.getLocalSettingCount(roomData.getChannel(), roomData.getModName()) + GameDatabase.getServerSettingCount(roomData.getChannel(), roomData.getModName()) == 0) {
            btn_gameSettings.setVisible(false);
        }
    }

    public ConnectingProgressBar getConnectingProgressBar() {
        return prgbar_connecting;
    }

    public boolean isHost() {
        return roomData.isHost();
    }

    public RoomData getRoomData() {
        return roomData;
    }

    public void initLauncher() {
        new Thread() {

            @Override
            public void run() {
                try {
                    LaunchMethods method = GameDatabase.getLaunchMethod(roomData.getChannel(), roomData.getModName());
                    if (method == LaunchMethods.PARAMETER) {
                        launchInfo = new ParameterLaunchInfo(roomData);
                    } else if (method == LaunchMethods.DOS) {
                        launchInfo = new DosboxLaunchInfo(roomData);
                    } else {
                        launchInfo = new DirectPlayLaunchInfo(roomData);
                    }

                    Launcher.initialize(launchInfo);
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void showSettings() {
        if (btn_gameSettings.isVisible()) {
            FrameOrganizer.openGameSettingsFrame(roomData);
        }
    }

    @Override
    public void requestFocus() {
        tp_chatInput.requestFocusInWindow();
    }

    public void disableGameSettingsFrameButton() {
        btn_gameSettings.setEnabled(false);
    }

    public void customCodeForColoring() {
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
            scrl_chatOutput.getTextPane().setBackground(coopnetclient.utils.Settings.getBackgroundColor());
        }
    }

    public void convertToJoinPanel() {
        btn_launch.setVisible(false);
        cb_useHamachi.setVisible(true);
    }

    public void setGameSetting(String key, String value) {
        gamesettings.put(key, value);
    }

    public String getGameSetting(String key) {
        return gamesettings.get(key);
    }

    public void addmember(String playername) {
        users.add(playername);
    }

    public void setAway(String playername) {
        roomStatusListCR.setAway(playername);
    }

    public void unSetAway(String playername) {
        roomStatusListCR.unSetAway(playername);
    }

    public void removeMember(String playername) {
        roomStatusListCR.removePlayer(playername);
        users.removeElement(playername);
        lst_userList.repaint();
    }

    public void chat(String name, String message, ChatStyles modeStyle) {
        scrl_chatOutput.printChatMessage(name, message, modeStyle);
    }

    public boolean updatePlayerName(String oldname, String newname) {
        roomStatusListCR.updateName(oldname, newname);
        if (users.removeElement(oldname)) {
            users.add(newname);
            return true;
        }
        return false;
    }

    public void unReadyPlayer(String playerName) {
        roomStatusListCR.unReadyPlayer(playerName);
    }

    public void readyPlayer(String playerName) {
        roomStatusListCR.readyPlayer(playerName);
    }

    public void setPlaying(String playerName) {
        roomStatusListCR.setPlaying(playerName);
    }

    public void gameClosed(String playerName) {
        roomStatusListCR.gameClosed(playerName);
    }

    public void pressLaunch() {
        btn_launch.doClick();
    }

    public void launch() {
        if (Launcher.isPlaying()) {
            Protocol.launch();
            return;
        }

        if (Launcher.predictSuccessfulLaunch() == false) {
            return;
        }

        new Thread() {

            @Override
            public void run() {
                try {
                    Launcher.launch();
                    Protocol.gameClosed(roomData.getChannel());
                    btn_gameSettings.setEnabled(true);
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void displayDelayedReinit() {
        btn_ready.setText("Waiting for game to exit...");
    }

    public void displayReInit() {
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                if (btn_ready.getText().equals("Unready")) {
                    flipReadyStatus();
                    wasReadyBeforeReInit = true;
                }
                btn_ready.setText("Reinitializing...");
                if (readyDisablerThread != null) {
                    readyDisablerThread.cancel(true);
                }
                btn_ready.setEnabled(false);
                if (launchDisablerThread != null) {
                    launchDisablerThread.cancel(true);
                }
                btn_launch.setEnabled(false);
                hamachiWasEnabled = cb_useHamachi.isEnabled();
                cb_useHamachi.setEnabled(false);
            }
        });
    }

    public void initDone() {
        btn_ready.setText("Ready");
        btn_ready.setEnabled(true);

        cb_useHamachi.setEnabled(hamachiWasEnabled);

        if (wasReadyBeforeReInit) {
            flipReadyStatus();
            wasReadyBeforeReInit = false;
        }
    }

    public void initDoneReadyDisabled() {
        btn_ready.setText("Ready");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btn_ready = new javax.swing.JButton();
        btn_launch = new javax.swing.JButton();
        sp_chatHorizontal = new javax.swing.JSplitPane();
        scrl_userList = new javax.swing.JScrollPane();
        lst_userList = new javax.swing.JList();
        sp_chatVertical = new javax.swing.JSplitPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        scrl_chatOutput = new coopnetclient.frames.components.ChatOutput();
        cb_useHamachi = new javax.swing.JCheckBox();
        btn_gameSettings = new javax.swing.JButton();
        prgbar_connecting = new coopnetclient.frames.components.ConnectingProgressBar();

        setFocusable(false);
        setNextFocusableComponent(tp_chatInput);
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        btn_ready.setMnemonic(KeyEvent.VK_R);
        btn_ready.setText("Initializing...");
        btn_ready.setEnabled(false);
        btn_ready.setFocusable(false);
        btn_ready.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clickedbtn_ready(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        add(btn_ready, gridBagConstraints);

        btn_launch.setMnemonic(KeyEvent.VK_L);
        btn_launch.setText("Launch");
        btn_launch.setEnabled(false);
        btn_launch.setFocusable(false);
        btn_launch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clickedbtn_launch(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        add(btn_launch, gridBagConstraints);

        sp_chatHorizontal.setBorder(null);
        sp_chatHorizontal.setDividerSize(3);
        sp_chatHorizontal.setResizeWeight(1.0);
        sp_chatHorizontal.setFocusable(false);

        scrl_userList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_userList.setFocusable(false);
        scrl_userList.setMinimumSize(new java.awt.Dimension(100, 50));
        scrl_userList.setPreferredSize(new java.awt.Dimension(150, 200));

        lst_userList.setModel(users);
        lst_userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_userList.setAutoscrolls(false);
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

        sp_chatHorizontal.setRightComponent(scrl_userList);

        sp_chatVertical.setBorder(null);
        sp_chatVertical.setDividerSize(3);
        sp_chatVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        sp_chatVertical.setResizeWeight(1.0);
        sp_chatVertical.setFocusable(false);
        sp_chatVertical.setMinimumSize(new java.awt.Dimension(22, 49));

        scrl_chatInput.setFocusable(false);

        tp_chatInput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatInput.setNextFocusableComponent(tp_chatInput);
        tp_chatInput.setPreferredSize(new java.awt.Dimension(6, 24));
        scrl_chatInput.setViewportView(tp_chatInput);

        sp_chatVertical.setRightComponent(scrl_chatInput);
        sp_chatVertical.setLeftComponent(scrl_chatOutput);

        sp_chatHorizontal.setLeftComponent(sp_chatVertical);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(sp_chatHorizontal, gridBagConstraints);

        cb_useHamachi.setMnemonic(KeyEvent.VK_H);
        cb_useHamachi.setText("use Hamachi");
        cb_useHamachi.setToolTipText("<html>The host doesn't have Hamachi installed!");
        cb_useHamachi.setEnabled(false);
        cb_useHamachi.setFocusable(false);
        cb_useHamachi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_useHamachiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        add(cb_useHamachi, gridBagConstraints);

        btn_gameSettings.setMnemonic(KeyEvent.VK_G);
        btn_gameSettings.setText("Game Settings");
        btn_gameSettings.setFocusable(false);
        btn_gameSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_gameSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        add(btn_gameSettings, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(prgbar_connecting, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void clickedbtn_launch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_launch
        if (roomData.isHost()) {
            btn_launch.setEnabled(false);
            launchDisablerThread = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }

                @Override
                protected void done() {
                    if (!isCancelled() && btn_ready.isEnabled()) {
                        btn_launch.setEnabled(true);
                    }
                }
            };
            launchDisablerThread.execute();

            launch();
        }
}//GEN-LAST:event_clickedbtn_launch

    private void clickedbtn_ready(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_ready
        btn_ready.setEnabled(false);
        readyDisablerThread = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                Thread.sleep(1000);
                return null;
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    btn_ready.setEnabled(true);
                }
            }
        };
        readyDisablerThread.execute();

        if (btn_ready.getText().equals("Ready") && !Launcher.predictSuccessfulLaunch()) {
            wasReadyBeforeReInit = true;
            return;
        }

        flipReadyStatus();
}//GEN-LAST:event_clickedbtn_ready

    private void flipReadyStatus() {
        Protocol.flipReadystatus();
        if (btn_ready.getText().equals("Ready")) {
            btn_ready.setText("Unready");
            btn_launch.setEnabled(true);
            SoundPlayer.playReadySound();
        } else {
            btn_ready.setText("Ready");
            if (launchDisablerThread != null) {
                launchDisablerThread.cancel(true);
            }
            btn_launch.setEnabled(false);
            SoundPlayer.playUnreadySound();
        }
    }

    private void lst_userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseClicked
        if (lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).equals(Globals.getThisPlayerLoginName())) {
            lst_userList.clearSelection();
        } else {
            lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
        }

        if (evt.getButton() == MouseEvent.BUTTON2) {
            String player = lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).toString();
            if (Globals.isHighlighted(player)) {
                Globals.unSetHighlightOn(player);
            } else {
                Globals.setHighlightOn(player);
            }
        } else if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            String name = (String) lst_userList.getSelectedValue();
            if (name != null && !name.equals("") && !name.equals(Globals.getThisPlayerLoginName())) {
                TabOrganizer.openPrivateChatPanel(name, true);
            }
        }
}//GEN-LAST:event_lst_userListMouseClicked

    private void cb_useHamachiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_useHamachiActionPerformed
        displayReInit();
        if (cb_useHamachi.isSelected()) {
            Logger.log(LogTypes.LOG, "Hamachi support turning on");
            cb_useHamachi.setEnabled(false);
            roomData.setUseHamachi(true);
            initLauncher();
            cb_useHamachi.setEnabled(true);
        } else {
            Logger.log(LogTypes.LOG, "Hamachi support turning off");
            cb_useHamachi.setEnabled(false);
            roomData.setUseHamachi(false);
            initLauncher();
            cb_useHamachi.setEnabled(true);
        }
}//GEN-LAST:event_cb_useHamachiActionPerformed

    private void btn_gameSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_gameSettingsActionPerformed
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                try {
                    showSettings();
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        });
}//GEN-LAST:event_btn_gameSettingsActionPerformed

private void lst_userListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseMoved
    if (!popup.isVisible()) {
        int idx = lst_userList.locationToIndex(evt.getPoint());
        Rectangle rec = lst_userList.getCellBounds(idx, idx);
        if (rec == null) {
            return;
        }
        if (!rec.contains(evt.getPoint())) {
            lst_userList.clearSelection();
            return;
        }
        if (idx == lst_userList.getSelectedIndex()) {
            return;
        }
        String selected = lst_userList.getModel().getElementAt(idx).toString();
        if (selected != null && selected.length() > 0) {
            if (!selected.equals(Globals.getThisPlayerLoginName())) {
                lst_userList.setSelectedIndex(idx);
            } else {
                lst_userList.clearSelection();
            }
        } else {
            lst_userList.clearSelection();
        }
    }
}//GEN-LAST:event_lst_userListMouseMoved

private void lst_userListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseExited
    if (!popup.isVisible()) {
        lst_userList.clearSelection();
    }
}//GEN-LAST:event_lst_userListMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_gameSettings;
    private javax.swing.JButton btn_launch;
    private javax.swing.JButton btn_ready;
    private javax.swing.JCheckBox cb_useHamachi;
    private javax.swing.JList lst_userList;
    private coopnetclient.frames.components.ConnectingProgressBar prgbar_connecting;
    private javax.swing.JScrollPane scrl_chatInput;
    private coopnetclient.frames.components.ChatOutput scrl_chatOutput;
    private javax.swing.JScrollPane scrl_userList;
    private javax.swing.JSplitPane sp_chatHorizontal;
    private javax.swing.JSplitPane sp_chatVertical;
    private javax.swing.JTextPane tp_chatInput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void closeTab() {
        if (roomData.isHost()) {
            Protocol.closeRoom();
        } else {
            Protocol.leaveRoom();
        }
    }

    @Override
    public boolean isCurrentlyClosable() {
        return true;
    }

    public void updateHighlights() {
        scrl_chatOutput.updateHighlights();
        lst_userList.repaint();
    }

    public void updateStyle() {
        scrl_chatOutput.updateStyle();
    }
}