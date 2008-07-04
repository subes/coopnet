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

import coopnetclient.frames.*;
import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.frames.clientframe.ChannelPanel;
import coopnetclient.frames.clientframe.LoginPanel;
import coopnetclient.frames.clientframe.PrivateChatPanel;
import coopnetclient.frames.clientframe.RoomPanel;
import coopnetclient.Protocol;
import coopnetclient.modules.Settings;
import coopnetclient.modules.ColoredChatHandler;
import coopnetclient.modules.renderers.RoomStatusListCellRenderer;
import coopnetclient.modules.listeners.TabbedPaneColorChangeListener;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.modules.components.FavMenuItem;
import coopnetclient.frames.clientframe.BrowserPanel;
import coopnetclient.frames.clientframe.ErrorPanel;
import coopnetclient.frames.clientframe.FileTransferRecieve;
import coopnetclient.frames.clientframe.FileTransferSend;
import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

public class ClientFrame extends javax.swing.JFrame {

    private Vector<ChannelPanel> channels = new Vector<ChannelPanel>();
    
    /** Creates new form ClientFrame */
    public ClientFrame() {
        initComponents();

        mi_disconnect.setEnabled(true);
        mi_connect.setEnabled(false);
        mi_profile.setEnabled(false);
        mi_channelList.setEnabled(false);

        refreshFavourites();

        coopnetclient.modules.Colorizer.colorize(this);

        //load the size from options
        int width = coopnetclient.modules.Settings.getMainFrameWidth();
        int height = coopnetclient.modules.Settings.getMainFrameHeight();
        this.setSize(width, height);
        //maximise if needed
        int status = coopnetclient.modules.Settings.getMainFrameMaximised();
        if(status ==  javax.swing.JFrame.MAXIMIZED_BOTH ){
            this.setExtendedState(status);
        }

        setLocationRelativeTo(null);
        //sound options init
        updateMenu();
        
        setVisible(true);
    }

    public void turnAroundTransfer(String peer, String filename) {
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferSend && ((FileTransferSend) c).getFilename().equals(filename) && ((FileTransferSend) c).getReciever().equals(peer)) {
                ((FileTransferSend) c).TurnAround();
            }
        }
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferRecieve && ((FileTransferRecieve) c).getFilename().equals(filename) && ((FileTransferRecieve) c).getSender().equals(peer)) {
                ((FileTransferRecieve) c).TurnAround();
            }
        }
    }

    public void addTransferTab_Send(String reciever, File file) {
        tabpn_tabs.add("Send File to " + reciever, new FileTransferSend(reciever, file));
    }

    public void addTransferTab_Recieve(String sender, String size, String filename,String ip,String port) {
        tabpn_tabs.add("Recieve file from " + sender, new FileTransferRecieve(sender, new Long(size), filename,ip,port));
    }

    public void startSending(String ip, String reciever, String filename, String port) {
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferSend && ((FileTransferSend) c).getFilename().equals(filename) && ((FileTransferSend) c).getReciever().equals(reciever)) {
                ((FileTransferSend) c).startsending(ip, port);
            }
        }
    }

    public void refusedTransfer(String reciever, String filename) {
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferSend && ((FileTransferSend) c).getFilename().equals(filename) && ((FileTransferSend) c).getReciever().equals(reciever)) {
                ((FileTransferSend) c).Refused();
            }
        }
    }

    public void cancelledTransfer(String sender, String filename) {
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferRecieve && ((FileTransferRecieve) c).getFilename().equals(filename) && ((FileTransferRecieve) c).getSender().equals(sender)) {
                ((FileTransferRecieve) c).Cancelled();
            }
        }
    }

    public void removeTransferTab(String user, String filename) {
        for (Component c : tabpn_tabs.getComponents()) {
            if (c instanceof FileTransferRecieve && ((FileTransferRecieve) c).getFilename().equals(filename) && ((FileTransferRecieve) c).getSender().equals(user)) {
                tabpn_tabs.remove(c);
            }
            if (c instanceof FileTransferSend && ((FileTransferSend) c).getFilename().equals(filename) && ((FileTransferSend) c).getReciever().equals(user)) {
                tabpn_tabs.remove(c);
            }
        }
    }

    public void gameClosed(String channel, String playername) {
        ChannelPanel cp = getChannel(channel);
        cp.GameClosed(playername);
    }

    public void setPlayingStatus(String channel, String player) {
        ChannelPanel cp = getChannel(channel);
        cp.SetPlayingStatus(player);
    }

    public void addGuideTab() {
        if (indexOfTab("Beginner's Guide") == -1) {
            tabpn_tabs.addTab("Beginner's Guide", new BrowserPanel());
        }
        this.repaint();
    }

    public void removeGuideTab() {
        tabpn_tabs.remove(indexOfTab("Beginner's Guide"));
    }

    public void addErrorTab(int mode, Exception e) {
        ErrorPanel err = new ErrorPanel(mode, e);
        tabpn_tabs.addTab("Error", err);
        tabpn_tabs.setSelectedComponent(err);
        this.repaint();
    }

    public ChannelPanel getChannel(String channelname) {
        int index = -1;
        index = indexOfTab(channelname);
        if (index != -1) {
            return (ChannelPanel) tabpn_tabs.getComponentAt(index);
        } else {
            if (tabpn_tabs.getComponentCount() != 0) {
                if (tabpn_tabs.getComponentAt(0) instanceof ChannelPanel) {
                    return (ChannelPanel) tabpn_tabs.getComponentAt(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public void addLoginTab() {
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                tabpn_tabs.addTab("Login", new LoginPanel());
                mi_profile.setEnabled(false);
            }
        });
    }

    public void removeLoginTab() {
        int index = -1;
        index = indexOfTab("Login");
        if (index != -1) {
            tabpn_tabs.remove(index);
        }
        mi_profile.setEnabled(true);
        mi_channelList.setEnabled(true);
    }

    public void addChannel(String channelname) {
        int index = -1;
        index = indexOfTab(channelname);
        if (index != -1) {
            return;
        }
        ChannelPanel currentchannel = new ChannelPanel(channelname);
        tabpn_tabs.add(currentchannel, 0);
        tabpn_tabs.setTitleAt(0, channelname);
        channels.add(currentchannel);
        this.repaint();
        //check if the game is installed
        if (!Globals.getLauncher().isLaunchable(channelname)) {
            currentchannel.disablebuttons();
            currentchannel.mainchat("SYSTEM",
                    "The game couldn't be detected, please set the path manually at " +
                    "options/manage games to enable playing this game!",
                    ColoredChatHandler.SYSTEM_STYLE);
        } else {
            currentchannel.setLaunchable(true);
        }
        if (GameDatabase.isBeta(channelname)) {
            currentchannel.mainchat("SYSTEM", "Support for this game is experimental," +
                    " email coopnetbugs@gmail.com if you have problems!",
                    ColoredChatHandler.SYSTEM_STYLE);
        }
        tabpn_tabs.setSelectedIndex(0);
    }

    public void setLaunchable(String channelname, boolean value) {
        ChannelPanel channel = getChannel(channelname);
        if (channel != null) {
            channel.setLaunchable(value);
        }
    }

    public void removeChannel(String channelname) {
        channels.remove(getChannel(channelname));
        tabpn_tabs.remove(getChannel(channelname));
    }

    public boolean mainTabIsVisible() {
        return channels.get(0).isVisible();
    }

    public void addNewRoomToList(String channel, String roomname, String hostname, String limitstr, boolean passworded) {
        getChannel(channel).addnewroom(roomname,
                hostname, limitstr, passworded);
    }

    public void addUser(String channel, String name) {
        getChannel(channel).adduser(name);
    }

    public void closeRoom(String channel, String hostname) { //close room tab

        Globals.getLauncher().stop();
        tabpn_tabs.remove(Globals.getCurrentRoomPanel());
        int index = indexOfTab(Globals.getCurrentRoomPanel().channel);
        if (index != -1) {
            tabpn_tabs.setSelectedIndex(index);
        }
        Globals.setCurrentRoomPanel(null);
        for (ChannelPanel cp : channels) {
            cp.enablebuttons();
        }
        this.repaint();
    }

    public void createRoom(String channel, String modindex, boolean compatible, int maxplayers) {
        RoomPanel room = new RoomPanel(true, channel, modindex, "", compatible, "", maxplayers);
        int index = channels.size();
        tabpn_tabs.insertTab("Room", null, room, null, index);
        Globals.setCurrentRoomPanel(room);
        for (ChannelPanel cp : channels) {
            cp.disablebuttons();
        }

        tabpn_tabs.setSelectedIndex(index);
        this.repaint();
    //call it here to DO remember settings
    //Client.currentroom.showsettings();
    //DO NOT REMOVE ABOVE COMMENT
    }

    public int getSelectedRoomListRowIndex(String channel) {
        return getChannel(channel).getSelectedRoomListRowIndex();
    }

    public void joinRoom(String hostip, String channel, String modname, boolean compatible, String launchinfo, String hamachiIp, int maxplayers) {
        final String ip = hostip;
        RoomPanel room = new RoomPanel(false, channel, modname, ip, compatible, hamachiIp, maxplayers);
        int index = channels.size();
        tabpn_tabs.insertTab("Room", null, room, null, index);
        Globals.setCurrentRoomPanel(room);
        for (ChannelPanel cp : channels) {
            cp.disablebuttons();
        }
        tabpn_tabs.setSelectedIndex(index);
    }

    public void addMemberToRoom(String channel, String hostname, String playername) {
        getChannel(channel).addmembertoroom(hostname, playername);
    }

    public void leave() {
        tabpn_tabs.remove(Globals.getCurrentRoomPanel());
        int index = indexOfTab(Globals.getCurrentRoomPanel().channel);
        if (index != -1) {
            tabpn_tabs.setSelectedIndex(index);
        }
        Globals.setCurrentRoomPanel(null);
        for (ChannelPanel cp : channels) {
            cp.enablebuttons();
        }
    }

    public void leftRoom(String channel, String hostname, String playername) {
        getChannel(channel).removeplayerfromroomonlist(hostname, playername);
    }

    public void mainChat(String channel, String name, String message, int modeStyle) {
        ChannelPanel cp = getChannel(channel);
        if (cp != null) {
            cp.mainchat(name, message, modeStyle);
        }
    }

    public void newPrivateChat(String title) {
        int idx = indexOfTab(title);
        if (idx == -1) {
            PrivateChatPanel pc = new PrivateChatPanel(title);
            tabpn_tabs.add(title, pc);
            pc.requestFocus();
        }
    }

    public void privateChat(String sender, String message) {

        PrivateChatPanel privatechat = null;
        int index = tabpn_tabs.indexOfTab(sender);
        if (index == -1) {//tab doesnt exist, must add it

            privatechat = new PrivateChatPanel(sender);
            tabpn_tabs.add(sender, privatechat);

            //Workaround for wrong color @ new tab that doesnt get focus
            if (Settings.getColorizeBody()) {
                ChangeListener[] listeners = tabpn_tabs.getChangeListeners();
                for (int i = 0; i < listeners.length; i++) {
                    if (listeners[i] instanceof TabbedPaneColorChangeListener) {
                        TabbedPaneColorChangeListener cl = (TabbedPaneColorChangeListener) listeners[i];
                        cl.updateBG();
                        break;
                    }
                }
            }
        } else {
            privatechat = (PrivateChatPanel) tabpn_tabs.getComponentAt(index);
        }
        privatechat.append(sender, message);
        if (!privatechat.isVisible()) {
            printToVisibleChatbox(sender, message, coopnetclient.modules.ColoredChatHandler.PRIVATE_NOTIFICATION_STYLE);
        }
    }

    public void showPMTab(String name) {
        int index = tabpn_tabs.indexOfTab(name);
        if (index != -1) {
            tabpn_tabs.setSelectedIndex(index);
            ((PrivateChatPanel) tabpn_tabs.getSelectedComponent()).requestFocus();
        }
    }

    public void removePMTab(PrivateChatPanel pmtab) {
        tabpn_tabs.remove(pmtab);
    }

    public void removeRoomFromList(String channel, String hostname) {
        getChannel(channel).removeroomfromlist(hostname);
    }

    public void removeUser(String channel, String playername) {
        getChannel(channel).removeplayerfromplayerlist(playername);
    }

    public void updateName(String channel, String oldname, String newname) {
        //update name in the main tab
        getChannel(channel).updatename(oldname, newname);
        //update name in the room tab
        if (Globals.getCurrentRoomPanel() != null && Globals.getCurrentRoomPanel().channel.equals(channel)) {
            Globals.getCurrentRoomPanel().updatename(oldname, newname);
            RoomStatusListCellRenderer.updateName(oldname, newname);
        }
        //update the pm tab title too
        int index = tabpn_tabs.indexOfTab(oldname);
        if (index != -1) {
            tabpn_tabs.setTitleAt(index, newname);
        }
    }

    public Component getTabComponentAt(int index) {
        return tabpn_tabs.getComponentAt(index);
    }

    public int indexOfTab(String title) {
        return tabpn_tabs.indexOfTab(title);
    }

    public void removeAllTabs() {
        tabpn_tabs.removeAll();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabpn_tabs = new javax.swing.JTabbedPane();
        mbar = new javax.swing.JMenuBar();
        m_main = new javax.swing.JMenu();
        mi_profile = new javax.swing.JMenuItem();
        mi_connect = new javax.swing.JMenuItem();
        mi_disconnect = new javax.swing.JMenuItem();
        mi_update = new javax.swing.JMenuItem();
        mi_quit = new javax.swing.JMenuItem();
        m_channels = new javax.swing.JMenu();
        mi_channelList = new javax.swing.JMenuItem();
        mi_manageFavs = new javax.swing.JMenuItem();
        mi_addCurrentToFav = new javax.swing.JMenuItem();
        mi_seperator = new javax.swing.JSeparator();
        mi_favourites = new javax.swing.JMenuItem();
        m_options = new javax.swing.JMenu();
        mi_clientSettings = new javax.swing.JMenuItem();
        mi_manageGames = new javax.swing.JMenuItem();
        mi_Sounds = new javax.swing.JCheckBoxMenuItem();
        m_help = new javax.swing.JMenu();
        mi_guide = new javax.swing.JMenuItem();
        mi_bugReport = new javax.swing.JMenuItem();
        mi_about = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CoopnetClient "+ Globals.getClientVersion());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabpn_tabs.setFocusable(false);
        tabpn_tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabpn_tabsStateChanged(evt);
            }
        });
        tabpn_tabs.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                tabpn_tabsComponentAdded(evt);
            }
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                tabpn_tabsComponentRemoved(evt);
            }
        });

        mbar.setFocusable(false);

        m_main.setText("Client");

        mi_profile.setText("Edit profile");
        mi_profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_profileActionPerformed(evt);
            }
        });
        m_main.add(mi_profile);

        mi_connect.setText("Connect");
        mi_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_connectActionPerformed(evt);
            }
        });
        m_main.add(mi_connect);

        mi_disconnect.setText("Disconnect");
        mi_disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_disconnectActionPerformed(evt);
            }
        });
        m_main.add(mi_disconnect);

        mi_update.setText("Update Client");
        mi_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_updateActionPerformed(evt);
            }
        });
        m_main.add(mi_update);

        mi_quit.setText("Quit");
        mi_quit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_quitActionPerformed(evt);
            }
        });
        m_main.add(mi_quit);

        mbar.add(m_main);

        m_channels.setText("Channels");

        mi_channelList.setText("Channel list");
        mi_channelList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_channelListActionPerformed(evt);
            }
        });
        m_channels.add(mi_channelList);

        mi_manageFavs.setText("Manage Favourites");
        mi_manageFavs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_manageFavsActionPerformed(evt);
            }
        });
        m_channels.add(mi_manageFavs);

        mi_addCurrentToFav.setText("Add current to Favourites");
        mi_addCurrentToFav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_addCurrentToFavActionPerformed(evt);
            }
        });
        m_channels.add(mi_addCurrentToFav);
        m_channels.add(mi_seperator);

        mi_favourites.setText("Favourites:");
        mi_favourites.setEnabled(false);
        m_channels.add(mi_favourites);

        mbar.add(m_channels);

        m_options.setText("Options");

        mi_clientSettings.setActionCommand("Client settings");
        mi_clientSettings.setLabel("Client settings");
        mi_clientSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_clientSettingsActionPerformed(evt);
            }
        });
        m_options.add(mi_clientSettings);

        mi_manageGames.setText("Manage Games");
        mi_manageGames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_manageGamesActionPerformed(evt);
            }
        });
        m_options.add(mi_manageGames);

        mi_Sounds.setSelected(true);
        mi_Sounds.setText("Sounds");
        mi_Sounds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_SoundsActionPerformed(evt);
            }
        });
        m_options.add(mi_Sounds);

        mbar.add(m_options);

        m_help.setText("Help");

        mi_guide.setText("Beginner's Guide");
        mi_guide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_guideActionPerformed(evt);
            }
        });
        m_help.add(mi_guide);

        mi_bugReport.setText("Report a Bug");
        mi_bugReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_bugReportActionPerformed(evt);
            }
        });
        m_help.add(mi_bugReport);

        mi_about.setText("About");
        mi_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_aboutActionPerformed(evt);
            }
        });
        m_help.add(mi_about);

        mbar.add(m_help);

        setJMenuBar(mbar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabpn_tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabpn_tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Client");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void mi_profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_profileActionPerformed
        Client.send(Protocol.editProfile(), null);
}//GEN-LAST:event_mi_profileActionPerformed

    private void mi_quitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_quitActionPerformed
        quit();
}//GEN-LAST:event_mi_quitActionPerformed

    public void quit() {
        Client.send(Protocol.quit(), null);
        Client.stopConnection();
        //save sizes
        coopnetclient.modules.Settings.setMainFrameMaximised(this.getExtendedState());
        
        if (this.getExtendedState() == javax.swing.JFrame.NORMAL) {
        coopnetclient.modules.Settings.setMainFrameHeight(this.getHeight());
        coopnetclient.modules.Settings.setMainFrameWidth(this.getWidth());
        }
        /*
        for (Component c : tabpn_Tabs.getComponents()) {
        if (c instanceof ChannelPanel) {
        ChannelPanel cp = (ChannelPanel) c;
        coopnetclient.Settings.setChannelChatHorizontalSPPosition(cp.getChannelChatHorizontalposition());
        coopnetclient.Settings.setChannelChatVerticalSPPosition(cp.getChannelChatVerticalposition());
        coopnetclient.Settings.setChannelVerticalSPPosition(cp.getChannelVerticalposition());
        break;
        }
        }
        }*/
        System.exit(0);
    }

    private void mi_clientSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_clientSettingsActionPerformed
        SettingsFrame frame = new SettingsFrame();
        frame.setVisible(true);
}//GEN-LAST:event_mi_clientSettingsActionPerformed

    private void mi_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_aboutActionPerformed
        String aboutMessage =
                "This software was developed by people who played " +
                "\nFallout Tactics multiplayer on Gamespy Arcade." +
                "\n\nIt aims to be a free GSA-like application that doesn't " +
                "\nannoy the player with advertisements and bugs." +
                "\n\nFuture plans include support for many different games" +
                "\nand added functionality to meet the gamers needs." +
                "\n\n<html><font size='2'>Download latest version from:</font></html>" +
                "\n<html>&nbsp;&nbsp;&nbsp;&nbsp;<a href='http://www.coopnet.tk'><font size='2'>http://www.coopnet.tk</font></a></html>" +
                "\n\n<html><font size='2'>Main developers can be contacted via MSN or E-Mail at:</font></html>" +
                "\n<html>&nbsp;&nbsp;&nbsp;&nbsp;<a href='mailto://kecske.85@hotmail.com'><font size='2'>kecske.85@hotmail.com</font></a></html>" +
                "\n<html>&nbsp;&nbsp;&nbsp;&nbsp;<a href='mailto://subes@hotmail.de'><font size='2'>subes@hotmail.de</font></a></html>" +
                "\n\n\n<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<i>Thank you for choosing Coopnet!</i></html>\n ";

        JOptionPane.showMessageDialog(null, aboutMessage, "About Coopnet", JOptionPane.PLAIN_MESSAGE);

}//GEN-LAST:event_mi_aboutActionPerformed

    private void mi_disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_disconnectActionPerformed
        disconnect();
}//GEN-LAST:event_mi_disconnectActionPerformed

    public void disconnect() {
        Client.send(Protocol.quit(), null);
        Client.stopConnection();
        removeAllTabs();
        mi_disconnect.setEnabled(false);
        mi_profile.setEnabled(false);
        mi_connect.setEnabled(true);
        mi_channelList.setEnabled(false);

        if (Globals.getChannelListFrame() != null) {
            Globals.getChannelListFrame().dispose();
        }
        if (Globals.getChangePasswordFrame() != null) {
            Globals.getChangePasswordFrame().dispose();
        }
        if (Globals.getProfileFrame() != null) {
            Globals.getProfileFrame().dispose();
        }
        if (Globals.getRoomCreationFrame() != null) {
            Globals.getRoomCreationFrame().dispose();
        }
    }

    private void clearFavourites() {
        for (Component mi : m_channels.getMenuComponents()) {
            if (mi instanceof FavMenuItem) {
                m_channels.remove(mi);
            }
        }
    }

    private void addFavourite(String channelname) {
        m_channels.add(new FavMenuItem(channelname));
    }

    public void updateSleepMode() {
        for (ChannelPanel cp : channels) {
            cp.updateSleepMode();
        }
    }

    public void refreshFavourites() {
        clearFavourites();
        for (String s : Settings.getFavourites()) {
            addFavourite(s);
        }
        m_channels.revalidate();
    }

    /**
     * Prints the message to a currently visible chatbox(room or main window)<P>
     * Usage:<ul> 
     * <li> name - the name of the sender
     * <li> message - the message to be printed
     * <li> mode : defines the style of the printed text, can be system or chat or whisper
     * 
     */
    public void printToVisibleChatbox(String name, String message, int modeStyle) {
        if (message.equals("Server is shutting down")) {
            mi_disconnect.doClick();
        }

        Component tc = tabpn_tabs.getSelectedComponent();

        if (tc == null) {
            JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
        } else if (tc instanceof ChannelPanel) {
            ChannelPanel cp = (ChannelPanel) tc;
            cp.mainchat(name, message, modeStyle);
        } else if (tc instanceof RoomPanel) {
            RoomPanel rp = (RoomPanel) tc;
            rp.chat(name, message, modeStyle);
        } else {
            ChannelPanel cp = channels.get(0);
            if (cp != null) {
                cp.mainchat(name, message, modeStyle);
            }
        }
    }
    
        private void relocateFocus() {
        Component comp = tabpn_tabs.getSelectedComponent();
        if (comp != null) {
            comp.requestFocus();
        }
    }

    public void updateMenu() {
        mi_Sounds.setSelected(Settings.getSoundEnabled());
    }

    private void mi_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_connectActionPerformed
        removeAllTabs();
        Client.startConnection();
        mi_disconnect.setEnabled(true);
        mi_connect.setEnabled(false);
}//GEN-LAST:event_mi_connectActionPerformed

    private void mi_channelListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_channelListActionPerformed
        Globals.setChannelListFrame(new ChannelListFrame());
        Globals.getChannelListFrame().setVisible(true);
}//GEN-LAST:event_mi_channelListActionPerformed

    private void tabpn_tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabpn_tabsStateChanged
        relocateFocus();
}//GEN-LAST:event_tabpn_tabsStateChanged

    private void tabpn_tabsComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tabpn_tabsComponentAdded
        relocateFocus();
}//GEN-LAST:event_tabpn_tabsComponentAdded

    private void tabpn_tabsComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tabpn_tabsComponentRemoved
        relocateFocus();
}//GEN-LAST:event_tabpn_tabsComponentRemoved

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        quit();
    }//GEN-LAST:event_formWindowClosing

    private void mi_SoundsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_SoundsActionPerformed
        boolean enabled = mi_Sounds.isSelected();
        coopnetclient.modules.Settings.setSoundEnabled(enabled);
}//GEN-LAST:event_mi_SoundsActionPerformed

    private void mi_manageFavsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_manageFavsActionPerformed
        new FavouritesFrame().setVisible(true);
}//GEN-LAST:event_mi_manageFavsActionPerformed

    private void mi_addCurrentToFavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_addCurrentToFavActionPerformed
        Component c = tabpn_tabs.getSelectedComponent();
        if (c instanceof ChannelPanel) {
            ChannelPanel cp = (ChannelPanel) c;
            Settings.addFavourite(cp.name);
            refreshFavourites();
        }
}//GEN-LAST:event_mi_addCurrentToFavActionPerformed

    private void mi_manageGamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_manageGamesActionPerformed
        new ManageGamesFrame().setVisible(true);
}//GEN-LAST:event_mi_manageGamesActionPerformed

    private void mi_guideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_guideActionPerformed
        addGuideTab();
        tabpn_tabs.setSelectedIndex(indexOfTab("Beginner's Guide"));
}//GEN-LAST:event_mi_guideActionPerformed

private void mi_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_updateActionPerformed
    new Thread() {

        @Override
        public void run() {
            int n = JOptionPane.showConfirmDialog(null, 
                    "<html>Would you like to update your Coopnet-client now?<br>" +
                    "(The client will close and update itself)", "Client outdated",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec("java -jar CoopnetUpdater.jar");
                    Globals.getClientFrame().quit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }.start();
}//GEN-LAST:event_mi_updateActionPerformed

private void mi_bugReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_bugReportActionPerformed
    new BugReport();
}//GEN-LAST:event_mi_bugReportActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu m_channels;
    private javax.swing.JMenu m_help;
    private javax.swing.JMenu m_main;
    private javax.swing.JMenu m_options;
    private javax.swing.JMenuBar mbar;
    private javax.swing.JCheckBoxMenuItem mi_Sounds;
    private javax.swing.JMenuItem mi_about;
    private javax.swing.JMenuItem mi_addCurrentToFav;
    private javax.swing.JMenuItem mi_bugReport;
    private javax.swing.JMenuItem mi_channelList;
    private javax.swing.JMenuItem mi_clientSettings;
    private javax.swing.JMenuItem mi_connect;
    private javax.swing.JMenuItem mi_disconnect;
    private javax.swing.JMenuItem mi_favourites;
    private javax.swing.JMenuItem mi_guide;
    private javax.swing.JMenuItem mi_manageFavs;
    private javax.swing.JMenuItem mi_manageGames;
    private javax.swing.JMenuItem mi_profile;
    private javax.swing.JMenuItem mi_quit;
    private javax.swing.JSeparator mi_seperator;
    private javax.swing.JMenuItem mi_update;
    private javax.swing.JTabbedPane tabpn_tabs;
    // End of variables declaration//GEN-END:variables

}
