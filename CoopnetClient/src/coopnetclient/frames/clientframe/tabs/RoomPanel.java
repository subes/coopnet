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
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.frames.listeners.ChatInputKeyListener;
import coopnetclient.frames.components.PlayerListPopupMenu;
import coopnetclient.frames.models.SortedListModel;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.frames.clientframe.ClosableTab;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.frames.renderers.RoomPlayerStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import coopnetclient.utils.UserListFileDropHandler;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DropMode;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

public class RoomPanel extends javax.swing.JPanel implements ClosableTab {

    private LaunchInfo launchInfo;
    
    private boolean isHost = false;
    private SortedListModel users;
    private PlayerListPopupMenu popup;
    public String gameName;
    public String roomName;
    public String ID;
    public String password;
    public String childName;
    public String hostName;
    private String hostIP;
    private String hamachiHostIP;
    private int maxPlayers;
    private HashMap<String, String> gamesettings = new HashMap<String, String>();
    private RoomPlayerStatusListCellRenderer roomStatusListCR;

    private boolean wasReadyBeforeReInit = false;

    public RoomPanel(boolean isHost, String channel, String modindex, String hostIP, String hamachiIp, int maxPlayers,String hostName,String roomName,String ID,String password) {
        this.gameName = channel;
        this.maxPlayers = maxPlayers;
        this.isHost = isHost;
        this.hostIP = hostIP;
        this.hamachiHostIP = hamachiIp;
        this.users = new SortedListModel();
        this.hostName = hostName;
        this.roomName = roomName;
        this.ID = ID;
        this.password = password;
        users.add(Globals.getThisPlayer_loginName());

        initComponents();

        if (Integer.valueOf(modindex) == -1) {
            this.childName = null;
        } else {
            this.childName = GameDatabase.getGameModNames(channel)[Integer.valueOf(modindex)].toString();
        }

        if(Client.getHamachiAddress().length() <= 0){
            cb_useHamachi.setVisible(false);
        } else if(hamachiIp.length() > 0 ) {
            cb_useHamachi.setEnabled(true);
            cb_useHamachi.setToolTipText("<html>Don't use this unless you have connection issues!<br>If you really need to use this consult with the room host!<br>Both you and the host have to be connected to <br>the same hamachi network!Otherwise it won't work!");
        }
        
        ArrayList<GameSetting> settings = GameDatabase.getGameSettings(channel, childName);
        if (settings == null || settings.size() == 0) {
            btn_gameSettings.setVisible(false);
        }

        if (isHost) {
            popup = new PlayerListPopupMenu(PlayerListPopupMenu.HOST_MODE, lst_userList);
            cb_useHamachi.setVisible(false);
            Hotkeys.bindHotKey(Hotkeys.ACTION_LAUNCH);
        } else {
            popup = new PlayerListPopupMenu(PlayerListPopupMenu.GENERAL_MODE, lst_userList);
        }
        lst_userList.setComponentPopupMenu(popup);

        roomStatusListCR = new RoomPlayerStatusListCellRenderer();
        lst_userList.setCellRenderer(roomStatusListCR);
        lst_userList.setDragEnabled(true);
        lst_userList.setDropMode(DropMode.USE_SELECTION);
        lst_userList.setTransferHandler(new UserListFileDropHandler());

        tp_chatInput.addKeyListener(new ChatInputKeyListener(ChatInputKeyListener.ROOM_CHAT_MODE, channel));
        tp_chatOutput.addMouseListener(new HyperlinkMouseListener());

        if (!isHost) {
            convertToJoinPanel();
        }

        coopnetclient.utils.Colorizer.colorize(this);

        chat("", roomName, ChatStyles.USER);
        chat("", "room://"+ID, ChatStyles.USER);

        if(Launcher.isPlaying()){
            btn_gameSettings.setEnabled(false);
        }
    }

    public boolean isHost(){
        return isHost;
    }
    
    public void initLauncher() {
        new Thread() {

            @Override
            public void run() {
                try{
                    String ip = null;
                    if(cb_useHamachi.isSelected()){
                        ip = hamachiHostIP;
                    }else{
                        ip = hostIP;
                    }
                    
                    LaunchMethods method = GameDatabase.getLaunchMethod(gameName, childName);
                    if(method == LaunchMethods.PARAMETER){
                        launchInfo = new ParameterLaunchInfo(gameName, childName, ip, isHost, false,roomName,password);
                    }else{
                        launchInfo = new DirectPlayLaunchInfo(gameName, childName, ip, isHost, false, password);
                    }

                    Launcher.initialize(launchInfo);
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void showSettings() {
        if (btn_gameSettings.isVisible()) {
            Globals.openGameSettingsFrame(gameName, childName,isHost);
        }
    }

    @Override
    public void requestFocus() {
        tp_chatInput.requestFocusInWindow();
    }

    public void disableGameSettingsFrameButton(){
        btn_gameSettings.setEnabled(false);
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

    public void convertToJoinPanel() {
        btn_launch.setVisible(false);
        cb_useHamachi.setVisible(true);
        //btn_gameSettings.setVisible(false);
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

    public void setAway(String playername){
        roomStatusListCR.setAway(playername);
    }

    public void unSetAway(String playername){
        roomStatusListCR.unSetAway(playername);
    }

    public void removeMember(String playername) {
        roomStatusListCR.removePlayer(playername);
        users.removeElement(playername);
        lst_userList.repaint();
    }

    public void chat(String name, String message, ChatStyles modeStyle) {
        StyledDocument doc = tp_chatOutput.getStyledDocument();
        coopnetclient.utils.ColoredChatHandler.addColoredText(name, message,
                modeStyle, doc, scrl_chatOutput, tp_chatOutput);
    }

    public boolean updatePlayerName(String oldname, String newname) {
        roomStatusListCR.updateName(oldname, newname);
        if(users.removeElement(oldname)){
            users.add(newname);
            return true;
        }
        return false;
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
    
    public void pressLaunch(){
        btn_launch.doClick();
    }

    public void launch() {
        if (Launcher.isPlaying() ) {
            Protocol.launch();
            return;
        }
        
        if(Launcher.predictSuccessfulLaunch() == false){
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try{                 
                    Launcher.launch();
                    Protocol.gameClosed(gameName);
                    btn_gameSettings.setEnabled(true);
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public void displayReInit(){
        if (btn_ready.getText().equals("Unready")) {
            btn_ready.doClick();
            wasReadyBeforeReInit = true;
        }
        btn_ready.setText("Reinitializing...");
        btn_ready.setEnabled(false);
        btn_launch.setEnabled(false);
    }

    public void initDone(){
        btn_ready.setText("Ready");
        btn_ready.setEnabled(true);
        if(wasReadyBeforeReInit){
            btn_ready.doClick();
            wasReadyBeforeReInit = false;
        }
    }

    public void initDoneReadyDisabled(){
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
        scrl_chatOutput = new javax.swing.JScrollPane();
        tp_chatOutput = new javax.swing.JTextPane();
        scrl_chatInput = new javax.swing.JScrollPane();
        tp_chatInput = new javax.swing.JTextPane();
        cb_useHamachi = new javax.swing.JCheckBox();
        btn_gameSettings = new javax.swing.JButton();

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
        scrl_userList.setPreferredSize(new java.awt.Dimension(100, 200));

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

        scrl_chatOutput.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrl_chatOutput.setFocusable(false);
        scrl_chatOutput.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scrl_chatOutputComponentResized(evt);
            }
        });

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
    }// </editor-fold>//GEN-END:initComponents
    private void tp_chatOutputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tp_chatOutputFocusLost
        StyledDocument doc = tp_chatOutput.getStyledDocument();

        tp_chatOutput.setSelectionStart(doc.getLength());
        tp_chatOutput.setSelectionEnd(doc.getLength());
}//GEN-LAST:event_tp_chatOutputFocusLost

    private void clickedbtn_launch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_launch
        if (isHost) {
            //Protocol.launch();
            launch();
            new Thread(){
                @Override
                public void run() {
                    try {
                        btn_launch.setEnabled(false);
                        sleep(1000);
                        if(btn_ready.isEnabled()){
                            btn_launch.setEnabled(true);
                        }
                    } catch (InterruptedException ex) {}
                } 
            }.start();            
        }
}//GEN-LAST:event_clickedbtn_launch

    private void clickedbtn_ready(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clickedbtn_ready
        if(btn_ready.getText().equals("Ready") && !Launcher.predictSuccessfulLaunch()){
            wasReadyBeforeReInit = true;
            return;
        }
        
        Protocol.flipReadystatus();
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
        if( !lst_userList.getModel().getElementAt(lst_userList.locationToIndex(evt.getPoint())).equals(Globals.getThisPlayer_loginName())){
            lst_userList.setSelectedIndex(lst_userList.locationToIndex(evt.getPoint()));
        } else{
            lst_userList.clearSelection();
        }
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            String name = (String) lst_userList.getSelectedValue();
            if(name != null && !name.equals("") && !name.equals(Globals.getThisPlayer_loginName())){
                TabOrganizer.openPrivateChatPanel(name, true);
            }
        }
}//GEN-LAST:event_lst_userListMouseClicked

    private void cb_useHamachiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_useHamachiActionPerformed
        displayReInit();
        if (cb_useHamachi.isSelected()) {
            System.out.println("Hamachi support turning on");
            cb_useHamachi.setEnabled(false);
            initLauncher();
            cb_useHamachi.setEnabled(true);
        } else {
            System.out.println("Hamachi support turning off");
            cb_useHamachi.setEnabled(false);
            initLauncher();
            cb_useHamachi.setEnabled(true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
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

    @Override
    public void closeTab() {
        if (isHost) {
            Protocol.closeRoom();
        } else {
            Protocol.leaveRoom();
        }
    }

    @Override
    public boolean isCurrentlyClosable() {
        return true;
    }
}
