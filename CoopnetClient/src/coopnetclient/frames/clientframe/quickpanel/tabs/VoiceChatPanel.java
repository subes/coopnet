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
package coopnetclient.frames.clientframe.quickpanel.tabs;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.models.VoiceChatChannelListModel;
import coopnetclient.frames.renderers.VoiceChatRenderer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Settings;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.voicechat.VoiceClient;
import coopnetclient.voicechat.VoicePlayback;
import coopnetclient.voicechat.VoiceServer;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class VoiceChatPanel extends javax.swing.JPanel {

    private VoiceChatChannelListModel model;
    private VoiceServer server = null;
    private VoiceClient client = null;
    private boolean isClientConnected = false;

    public VoiceChatPanel() {
        initComponents();
        model = new VoiceChatChannelListModel();
        VoiceChatRenderer renderer = new VoiceChatRenderer(model);
        lst_channelList.setModel(model);
        lst_channelList.setCellRenderer(renderer);
    }

    public VoiceChatChannelListModel getModel() {
        return model;
    }

    public void startServer() {
        if (server == null && client == null) {
            server = new VoiceServer(Settings.getVoiceChatPort());
            server.start();
            client = new VoiceClient(Globals.getThisPlayer_loginName(), "localhost", String.valueOf(Settings.getVoiceChatPort()));
            btn_connect.setText("Stop service");
            lbl_currentStatus.setText("Status: running");
            Globals.getClientFrame().updateVoiceServerStatus(true);
        }
    }

    public void stopServer() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        
        if (server != null) {
            server.shutdown();
            server = null;
            isClientConnected = false;
            Hotkeys.unbindHotKey(Hotkeys.PUSH_TO_TALK);
            Globals.getClientFrame().updateVoiceServerStatus(false);
        }
        
        btn_connect.setText("Start service");
        lbl_currentStatus.setText("Status: not running");
        model.clear();
        VoicePlayback.cleanUp();
    }

    public void startconnect(String ip, String port) {
        if (server == null && client == null) {
            client = new VoiceClient(Globals.getThisPlayer_loginName(), ip, port);
        }
    }

    public void disconnect() {
        if (server == null && client != null) {
            client.disconnect();
            client = null;
            lbl_currentStatus.setText("Status: not running");
            btn_connect.setText("Start service");
            model.clear();
            isClientConnected = false;
            Hotkeys.unbindHotKey(Hotkeys.PUSH_TO_TALK);
            VoicePlayback.cleanUp();
            Globals.getClientFrame().updateVoiceClientStatus(false);
        }
    }

    public void connected() {
        if(!Settings.isVoiceActivated()){
                Hotkeys.bindHotKey(Hotkeys.PUSH_TO_TALK);
        }
        if (server == null) {
            SwingUtilities.invokeLater(
                    new Runnable() {

                        @Override
                        public void run() {
                            btn_connect.setText("Disconnect");
                            lbl_currentStatus.setText("Status: connected");
                        }
                    });
            isClientConnected = false;
            Globals.getClientFrame().updateVoiceClientStatus(true);
            Globals.getClientFrame().printToVisibleChatbox("System", "VoiceClient connected! For controlls open QuickTab!", ChatStyles.SYSTEM , false);
        }
    }

    public boolean isClientConnected (){
        return isClientConnected;
    }

    public void connectFailedOrBroken() {
        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        btn_connect.setText("Start service");
                        lbl_currentStatus.setText("Status: not running");
                    }
                });
        if (client != null) {
            client.disconnect();
            client = null;
            isClientConnected = false;
            Hotkeys.unbindHotKey(Hotkeys.PUSH_TO_TALK);
        }
        if (server != null) {
            server.shutdown();
            server = null;
        }
        Globals.getClientFrame().updateVoiceClientStatus(false);
        model.clear();
        VoicePlayback.cleanUp();
    }
    //public void setEnabled(boolean enabled){}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btn_connect = new javax.swing.JButton();
        lbl_currentStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lst_channelList = new javax.swing.JList();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        btn_connect.setText("Start service");
        btn_connect.setFocusable(false);
        btn_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_connectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btn_connect, gridBagConstraints);

        lbl_currentStatus.setText("Status: not running");
        lbl_currentStatus.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lbl_currentStatus, gridBagConstraints);

        lst_channelList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lst_channelList.setAutoscrolls(false);
        lst_channelList.setFocusable(false);
        lst_channelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lst_channelListMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lst_channelListMouseExited(evt);
            }
        });
        lst_channelList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lst_channelListMouseMoved(evt);
            }
        });
        jScrollPane1.setViewportView(lst_channelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void lst_channelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_channelListMouseClicked
    if (!(lst_channelList.getModel().getElementAt(lst_channelList.locationToIndex(evt.getPoint())).toString().equalsIgnoreCase(Globals.getThisPlayer_loginName()))) {
        lst_channelList.setSelectedIndex(lst_channelList.locationToIndex(evt.getPoint()));
    } else {
        lst_channelList.clearSelection();
        return;
    }
    if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
        String name = (String) lst_channelList.getSelectedValue();
        if (name != null && name.length() > 0 && !name.equals(Globals.getThisPlayer_loginName())) {
            if (model.getChannel(name) != null) {
                VoiceClient.send("cc" + Protocol.MESSAGE_DELIMITER + model.indexOfChannel(model.getChannel(name)));
            } else {//player
                if (model.isMuted(name)) {
                    model.unMute(name);
                } else {
                    model.mute(name);
                }
            }
        }
    }
}//GEN-LAST:event_lst_channelListMouseClicked

private void lst_channelListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_channelListMouseMoved
    int idx = lst_channelList.locationToIndex(evt.getPoint());
    Rectangle rec = lst_channelList.getCellBounds(idx, idx);
    if (rec == null) {
        return;
    }
    if (!rec.contains(evt.getPoint())) {
        lst_channelList.clearSelection();
        return;
    }
    if (idx == lst_channelList.getSelectedIndex()) {
        return;
    }
    String selected = lst_channelList.getModel().getElementAt(idx).toString();
    if (selected != null && selected.length() > 0) {
        if (!selected.equals(Globals.getThisPlayer_loginName())) {
            lst_channelList.setSelectedIndex(idx);
        } else {
            lst_channelList.clearSelection();
        }
    } else {
        lst_channelList.clearSelection();
    }
}//GEN-LAST:event_lst_channelListMouseMoved

private void lst_channelListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_channelListMouseExited
    lst_channelList.clearSelection();
}//GEN-LAST:event_lst_channelListMouseExited

private void btn_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_connectActionPerformed
    if (btn_connect.getText().equals("Start service")) {
        startServer();
        return;
    }
    if (btn_connect.getText().equals("Stop service")) {
        stopServer();
        return;
    }
    if (btn_connect.getText().equals("Disconnect")) {
        disconnect();
        return;
    }
}//GEN-LAST:event_btn_connectActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_connect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_currentStatus;
    private javax.swing.JList lst_channelList;
    // End of variables declaration//GEN-END:variables
}
