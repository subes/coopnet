/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.frames.clientframe.panels;

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.modules.listeners.ChatInputKeyListener;
import coopnetclient.modules.components.PlayerListPopupMenu;
import coopnetclient.modules.models.SortedListModel;
import coopnetclient.Protocol;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.Settings;
import coopnetclient.modules.SoundPlayer;
import coopnetclient.modules.ColoredChatHandler;
import coopnetclient.modules.renderers.RoomStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.modules.listeners.HyperlinkMouseListener;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

public class RoomPanel extends javax.swing.JPanel {

    private boolean isHost = false;
    private boolean compatible = true;
    private SortedListModel users;
    private PlayerListPopupMenu mypopup;
    public String channel;
    public String modname;
    private String ip;
    private String hamachiIp;
    private int maxPlayers;
    private HashMap<String, String> gamesettings = new HashMap<String, String>();
    private RoomStatusListCellRenderer roomStatusListCR;

    public RoomPanel(boolean isHost, String channel, String modindex, String ip, boolean compatible, String hamachiIp, int maxPlayers) {
        this.channel = channel;
        this.maxPlayers = maxPlayers;
        this.isHost = isHost;
        this.ip = ip;
        this.hamachiIp = hamachiIp;
        this.users = new SortedListModel();
        users.add(Globals.getThisPlayer_loginName());
        this.compatible = compatible;
        
        if (Integer.valueOf(modindex) == -1) {
            this.modname = null;
        } else {
            this.modname = GameDatabase.getGameModNames(channel)[Integer.valueOf(modindex)].toString();
        }
        initComponents();
        
        if (isHost) {
            mypopup = new PlayerListPopupMenu(PlayerListPopupMenu.HOST_MODE, lst_userList);
            cb_useHamachi.setVisible(false);
        } else {
            mypopup = new PlayerListPopupMenu(PlayerListPopupMenu.GENERAL_MODE, lst_userList);
        }
        lst_userList.setComponentPopupMenu(mypopup);
        
        roomStatusListCR = new RoomStatusListCellRenderer();
        lst_userList.setCellRenderer(roomStatusListCR);

        tp_chatInput.addKeyListener(new ChatInputKeyListener(ChatInputKeyListener.ROOM_CHAT_MODE, channel));
        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());

        if (!isHost) {
            convertToJoinPanel();
        }

        coopnetclient.modules.Colorizer.colorize(this);

        initLauncher();

        ArrayList<GameSetting> settings = GameDatabase.getGameSettings(channel, modname);
        if (settings == null || settings.size() == 0) {
            btn_gameSettings.setVisible(false);
        }
    }

    private void initLauncher() {
        new Thread() {

            @Override
            public void run() {
                try{
                    Globals.getLauncher().initialize(channel, modname, isHost, ip, compatible, maxPlayers);
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void showSettings() {
        if (btn_gameSettings.isVisible()) {
            Globals.openGameSettingsFrame(channel, modname);
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

    public void convertToJoinPanel() {
        btn_launch.setVisible(false);
        btn_close.setText("Leave");
        cb_useHamachi.setVisible(true);
        btn_gameSettings.setVisible(false);
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

    public void removeMember(String playername) {
        roomStatusListCR.readyPlayer(playername);
        users.removeElement(playername);
        lst_userList.repaint();
    }

    public void chat(String name, String message, int modeStyle) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();
        coopnetclient.modules.ColoredChatHandler.addColoredText(name, message,
                modeStyle, doc, scrl_chatOutput, tp_chatOutput);
    }

    public void updatePlayerName(String oldname, String newname) {
        roomStatusListCR.updateName(oldname, newname);
        users.removeElement(oldname);
        users.add(newname);
    }
    
    public void unReadyPlayer(String playerName){
        roomStatusListCR.unReadyPlayer(playerName);
    }
    
    public void readyPlayer(String playerName){
        roomStatusListCR.readyPlayer(playerName);
    }
    
    public void setPlaying(String playerName){
        roomStatusListCR.setPlaying(playerName);
    }
    
    public void gameClosed(String playerName){
        roomStatusListCR.gameClosed(playerName);
    }

    public void launch() {
        if (Globals.getIsPlayingStatus()) {
            return;
        }

        new Thread() {

            @Override
            public void run() {
                try{
                    Globals.setIsPlayingStatus(true);

                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", 
                            "Game launching... please wait!", 
                            coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
                    //play sound
                    SoundPlayer.playLaunchSound();

                    if (Settings.getSleepEnabled()) {
                        Globals.setSleepModeStatus(true);
                    }


                    String _channel = channel;
                    boolean launched = Globals.getLauncher().launch();

                    if (!launched) {
                        Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed to start the game!", ColoredChatHandler.SYSTEM_STYLE);
                    }

                    Globals.setIsPlayingStatus(false);
                    Client.send(Protocol.gameClosed(), _channel);
                    Globals.setSleepModeStatus(false);
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void setGameName(String ingamename) {
        Globals.getLauncher().setIngameName(ingamename);
    }

    public void enableButtons() {
        btn_ready.setEnabled(true);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_ready = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        btn_launch = new javax.swing.JButton();
        sp_chatHorizontal = new javax.swing.JSplitPane();
        scrl_userList = new javax.swing.JScrollPane();
        lst_userList = new javax.swing.JList();
        sp_chatVertical = new javax.swing.JSplitPane();
        scrl_chatOutput = new javax.swing.JScrollPane();
        tp_chatOutput = new javax.swing.JTextPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        cb_useHamachi = new javax.swing.JCheckBox();
        btn_gameSettings = new javax.swing.JButton();

        setFocusable(false);
        setNextFocusableComponent(tp_chatInput);
        setPreferredSize(new java.awt.Dimension(350, 400));
        setRequestFocusEnabled(false);

        btn_ready.setText("Ready");
        btn_ready.setEnabled(false);
        btn_ready.setFocusable(false);
        btn_ready.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clickedbtn_ready(evt);
            }
        });

        btn_close.setText("Close");
        btn_close.setFocusable(false);
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_close(evt);
            }
        });

        btn_launch.setText("Launch");
        btn_launch.setEnabled(false);
        btn_launch.setFocusable(false);
        btn_launch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clickedbtn_launch(evt);
            }
        });

        sp_chatHorizontal.setDividerSize(3);
        sp_chatHorizontal.setResizeWeight(1.0);
        sp_chatHorizontal.setFocusable(false);

        scrl_userList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_userList.setFocusable(false);
        scrl_userList.setMinimumSize(new java.awt.Dimension(100, 50));

        lst_userList.setModel(users);
        lst_userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst_userList.setFixedCellHeight(20);
        lst_userList.setFixedCellWidth(100);
        lst_userList.setFocusable(false);
        lst_userList.setMinimumSize(new java.awt.Dimension(30, 50));
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
        sp_chatVertical.setMinimumSize(new java.awt.Dimension(22, 49));

        scrl_chatOutput.setFocusable(false);

        tp_chatOutput.setEditable(false);
        tp_chatOutput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatOutput.setNextFocusableComponent(tp_chatInput);
        tp_chatOutput.setPreferredSize(new java.awt.Dimension(6, 24));
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

        sp_chatVertical.setLeftComponent(scrl_chatOutput);

        scrl_chatInput.setFocusable(false);

        tp_chatInput.setMinimumSize(new java.awt.Dimension(6, 24));
        tp_chatInput.setNextFocusableComponent(tp_chatInput);
        tp_chatInput.setPreferredSize(new java.awt.Dimension(6, 24));
        scrl_chatInput.setViewportView(tp_chatInput);

        sp_chatVertical.setRightComponent(scrl_chatInput);

        sp_chatHorizontal.setLeftComponent(sp_chatVertical);

        cb_useHamachi.setText("use Hamachi");
        cb_useHamachi.setFocusable(false);
        cb_useHamachi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_useHamachiActionPerformed(evt);
            }
        });

        btn_gameSettings.setText("Game Settings");
        btn_gameSettings.setFocusable(false);
        btn_gameSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_gameSettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btn_ready, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_launch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cb_useHamachi)
                .addGap(18, 18, 18)
                .addComponent(btn_gameSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_close))
            .addComponent(sp_chatHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_close)
                    .addComponent(btn_ready)
                    .addComponent(btn_launch)
                    .addComponent(cb_useHamachi)
                    .addComponent(btn_gameSettings))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_chatHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void tp_chatOutputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tp_chatOutputFocusLost
        StyledDocument doc = tp_chatOutput.getStyledDocument();

        tp_chatOutput.setSelectionStart(doc.getLength());
        tp_chatOutput.setSelectionEnd(doc.getLength());
}//GEN-LAST:event_tp_chatOutputFocusLost

    private void btn_close(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_close
        if (isHost) {
            Client.send(Protocol.closeRoom(), channel);
        } else {
            Client.send(Protocol.leaveRoom(), channel);
        }
}//GEN-LAST:event_btn_close

    private void clickedbtn_launch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_launch
        if (isHost) {
            Client.send(Protocol.Launch(), null);
        }
}//GEN-LAST:event_clickedbtn_launch

    private void clickedbtn_ready(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_ready
        Client.send(Protocol.flipReadystatus(), null);
        if (btn_ready.getText().equals("Ready")) {
            btn_ready.setText("Unready");
            btn_launch.setEnabled(true);
            SoundPlayer.playReadySound();
        } else {
            btn_ready.setText("Ready");
            btn_launch.setEnabled(false);
            SoundPlayer.playUnreadySound();
        }
}//GEN-LAST:event_clickedbtn_ready

    private void lst_userListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_userListMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3){
            lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
            lst_userList.getComponentPopupMenu().show(lst_userList, evt.getX(), evt.getY());
        }else
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            String name = (String) lst_userList.getSelectedValue();
            if(name != null && !name.equals("") && !name.equals(Globals.getThisPlayer_loginName())){
                TabOrganizer.openPrivateChatPanel(name, true);
            }
        }
}//GEN-LAST:event_lst_userListMouseClicked

    private void cb_useHamachiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_useHamachiActionPerformed
        if (btn_ready.getText().equals("Unready")) {
            btn_ready.doClick();
        }
        btn_ready.setEnabled(false);
        if (cb_useHamachi.isSelected()) {
            System.out.println("Hamachi support turning on");
            Globals.getLauncher().initialize(channel, modname, isHost, hamachiIp, compatible, maxPlayers);
        } else {
            System.out.println("Hamachi support turning off");
            Globals.getLauncher().initialize(channel, modname, isHost, ip, compatible, maxPlayers);
        }
}//GEN-LAST:event_cb_useHamachiActionPerformed

    private void btn_gameSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_gameSettingsActionPerformed
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                try{
                    showSettings();
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        });
}//GEN-LAST:event_btn_gameSettingsActionPerformed

private void tp_chatOutputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tp_chatOutputKeyTyped
    char c = evt.getKeyChar();
    if (!evt.isControlDown()) {
        tp_chatInput.setText(tp_chatInput.getText() + c);
        tp_chatInput.requestFocus();
    }
}//GEN-LAST:event_tp_chatOutputKeyTyped
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_gameSettings;
    private javax.swing.JButton btn_launch;
    private javax.swing.JButton btn_ready;
    private javax.swing.JCheckBox cb_useHamachi;
    private javax.swing.JList lst_userList;
    private javax.swing.JScrollPane scrl_chatInput;
    private javax.swing.JScrollPane scrl_chatOutput;
    private javax.swing.JScrollPane scrl_userList;
    private javax.swing.JSplitPane sp_chatHorizontal;
    private javax.swing.JSplitPane sp_chatVertical;
    private javax.swing.JTextPane tp_chatInput;
    private javax.swing.JTextPane tp_chatOutput;
    // End of variables declaration//GEN-END:variables
}
