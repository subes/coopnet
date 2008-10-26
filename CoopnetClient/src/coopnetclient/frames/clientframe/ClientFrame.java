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
package coopnetclient.frames.clientframe;

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.frames.clientframe.tabs.ChannelPanel;
import coopnetclient.frames.clientframe.tabs.PrivateChatPanel;
import coopnetclient.frames.clientframe.tabs.RoomPanel;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.QuickPanel;
import coopnetclient.utils.Settings;
import coopnetclient.frames.components.FavMenuItem;
import coopnetclient.frames.clientframe.tabs.FileTransferRecievePanel;
import coopnetclient.frames.clientframe.tabs.FileTransferSendPanel;
import coopnetclient.frames.clientframe.tabs.LoginPanel;
import coopnetclient.utils.FileDownloader;
import coopnetclient.utils.FrameIconFlasher;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class ClientFrame extends javax.swing.JFrame {

    private static final int DIVIDERWIDTH = Settings.getQuickPanelDividerWidth();
    private static boolean quickPanelVisibility = false;
    private static Integer lastdividerposition = null;
    private static boolean quickPanelOrientationIsLeft = true;
    private static boolean quickPanelFlashing = false;
    private static Border openQuickbarBorder = BorderFactory.createLoweredBevelBorder();
    private static Border closedQuickbarBorder = BorderFactory.createRaisedBevelBorder();
    private static QuickPanel pnl_contactList;

    /** Creates new form ClientFrame */
    public ClientFrame() {
        pnl_contactList = new coopnetclient.frames.clientframe.QuickPanel(Globals.getContactList());
        pnl_contactList.setPreferredSize(new Dimension(200, 10));
        initComponents();       
        pnl_toggleQuickBarLeft.setPreferredSize(new Dimension(Settings.getQuickPanelToggleBarWidth(), 10));
        pnl_toggleQuickBarRight.setPreferredSize(new Dimension(Settings.getQuickPanelToggleBarWidth(), 10));
        pnl_toggleQuickBarLeft.setMinimumSize(new Dimension(Settings.getQuickPanelToggleBarWidth(), 10));
        pnl_toggleQuickBarRight.setMinimumSize(new Dimension(Settings.getQuickPanelToggleBarWidth(), 10));
        setQuickPanelPosition(Settings.getQuickPanelPostionisLeft());
        //pnl_contactList.setVisible(false);
        slp_mainSplitPanel.setDividerSize(0);

        refreshFavourites();

        //load the size from options
        int width = Settings.getMainFrameWidth();
        int height = Settings.getMainFrameHeight();
        this.setSize(width, height);
        //maximise if needed
        int status = Settings.getMainFrameMaximised();
        if (status == JFrame.MAXIMIZED_BOTH) {
            this.setExtendedState(status);
        }
     
        updateMenu();
        pack();
        if (Settings.getTrayIconEnabled()) {
            Globals.addTrayIcon();
        }
    }
    //Callback for Globals
    public void updateStatus() {
        if(Globals.getLoggedInStatus() == false){
            setQuickBarVisibility(false);
            lastdividerposition = null;
        }
        
        if(!Globals.getConnectionStatus()){
            mi_connection.setText("Connect");
        }else{
            mi_connection.setText("Disconnect");
        }
        
        m_user.setEnabled(Globals.getLoggedInStatus());
        m_channels.setEnabled(Globals.getLoggedInStatus());
    }

    public void flashQuickPanelToggler() {
        if (!quickPanelFlashing && !quickPanelVisibility) {
            quickPanelFlashing = true;
            new Thread() {

                @Override
                public void run() {
                    while (quickPanelFlashing) {
                        if (Settings.getColorizeBody()) {
                            pnl_toggleQuickBarLeft.setBackground(Settings.getSelectionColor());
                            pnl_toggleQuickBarRight.setBackground(Settings.getSelectionColor());
                        } else {
                            pnl_toggleQuickBarLeft.setBackground(getHoverEffectColor());
                            pnl_toggleQuickBarRight.setBackground(getHoverEffectColor());
                        }
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                        }
                        if (Settings.getColorizeBody()) {
                            pnl_toggleQuickBarRight.setBackground(Settings.getBackgroundColor());
                            pnl_toggleQuickBarLeft.setBackground(Settings.getBackgroundColor());
                        } else {
                            pnl_toggleQuickBarRight.setBackground((Color) UIManager.get("Panel.background"));
                            pnl_toggleQuickBarLeft.setBackground((Color) UIManager.get("Panel.background"));
                        }
                        try {
                            sleep(1000);
                        } catch (Exception e) {
                        }
                    }
                    if (Settings.getColorizeBody()) {
                        pnl_toggleQuickBarRight.setBackground(Settings.getBackgroundColor());
                        pnl_toggleQuickBarLeft.setBackground(Settings.getBackgroundColor());
                    } else {
                        pnl_toggleQuickBarRight.setBackground((Color) UIManager.get("Panel.background"));
                        pnl_toggleQuickBarLeft.setBackground((Color) UIManager.get("Panel.background"));
                    }
                }
            }.start();
        }
    }

    public void turnAroundTransfer(String peer, String filename) {
        FileTransferSendPanel sendPanel = TabOrganizer.getFileTransferSendPanel(peer, filename);
        FileTransferRecievePanel recvPanel = TabOrganizer.getFileTransferReceivePanel(peer, filename);

        if (sendPanel != null) {
            sendPanel.turnAround();
        }
        if (recvPanel != null) {
            recvPanel.turnAround();
        }
    }

    public void startSending(String ip, String reciever, String filename, String port, long firstByte) {
        FileTransferSendPanel sendPanel = TabOrganizer.getFileTransferSendPanel(reciever, filename);

        if (sendPanel != null) {
            sendPanel.startSending(ip, port, firstByte);
        }
    }

    public void refusedTransfer(String reciever, String filename) {
        FileTransferSendPanel sendPanel = TabOrganizer.getFileTransferSendPanel(reciever, filename);

        if (sendPanel != null) {
            sendPanel.refused();
        }
    }

    public void cancelledTransfer(String sender, String filename) {
        FileTransferRecievePanel recvPanel = TabOrganizer.getFileTransferReceivePanel(sender, filename);

        if (recvPanel != null) {
            recvPanel.cancelled();
        }
    }

    public void gameClosed(String channel, String playername) {
        TabOrganizer.getChannelPanel(channel).gameClosed(playername);
    }

    public void setPlayingStatus(String channel, String player) {
        ChannelPanel cp = TabOrganizer.getChannelPanel(channel);
        cp.setPlayingStatus(player);
    }

    public void setLaunchable(String channelname, boolean value) {
        ChannelPanel channel = TabOrganizer.getChannelPanel(channelname);
        if (channel != null) {
            channel.setLaunchable(value);
        }
    }

    public void addRoomToTable(String channel, String roomname, String hostname, int maxplayers, int type) {
        TabOrganizer.getChannelPanel(channel).addRoomToTable(roomname,
                hostname, maxplayers, type);
    }

    public void removeRoomFromTable(String channel, String hostname) {
        TabOrganizer.getChannelPanel(channel).removeRoomFromTable(hostname);
    }

    public void addPlayerToChannel(String channel, String name) {
        TabOrganizer.getChannelPanel(channel).addPlayerToChannel(name);
    }

    public int getSelectedRoomListRowIndex(String channel) {
        return TabOrganizer.getChannelPanel(channel).getSelectedRoomListRowIndex();
    }

    public void addPlayerToRoom(String channel, String hostname, String playername) {
        TabOrganizer.getChannelPanel(channel).addPlayerToRoom(hostname, playername);
    }

    public void removePlayerFromRoom(String channel, String hostname, String playername) {
        TabOrganizer.getChannelPanel(channel).removePlayerFromRoom(hostname, playername);
    }

    public void printMainChatMessage(String channel, String name, String message, ChatStyles modeStyle) {
        ChannelPanel cp = TabOrganizer.getChannelPanel(channel);
        if (cp != null) {
            cp.printMainChatMessage(name, message, modeStyle);
        }
    }

    public void printPrivateChatMessage(String sender, String message) {

        PrivateChatPanel privatechat = TabOrganizer.getPrivateChatPanel(sender);
        if (privatechat == null) {//tab doesnt exist, must add it
            TabOrganizer.openPrivateChatPanel(sender, false);
            privatechat = TabOrganizer.getPrivateChatPanel(sender);
        }

        privatechat.append(sender, message, ChatStyles.WHISPER);

        if (!privatechat.isVisible()) {
            printToVisibleChatbox(sender, message, ChatStyles.WHISPER_NOTIFICATION, false);
        }
    }

    public void removePlayerFromChannel(String channel, String playername) {
        TabOrganizer.getChannelPanel(channel).removePlayerFromChannel(playername);
    }

    public void updatePlayerName(String oldname, String newname) {
        //update name in the channels
        for (int i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
            TabOrganizer.getChannelPanel(i).updatePlayerName(oldname, newname);
        }
        //update name in the room tab
        if (TabOrganizer.getRoomPanel() != null) {
            TabOrganizer.getRoomPanel().updatePlayerName(oldname, newname);
        }
        //update the pm tab title too
        TabOrganizer.updateTitleOnTab(oldname, newname);
        //update the contactlist
        Globals.getContactList().updateName(oldname, newname);
    }

    public void setQuickBarVisibility(boolean visibility) {
        
        if (visibility) {
            if (lastdividerposition == null) {
                if (quickPanelOrientationIsLeft) {
                    lastdividerposition = pnl_contactList.getPreferredSize().width;
                } else {
                    lastdividerposition = slp_mainSplitPanel.getWidth() - (pnl_contactList.getPreferredSize().width + DIVIDERWIDTH + slp_mainSplitPanel.getInsets().right);
                }
            }
            //pnl_contactList.setSize( lastdividerposition - DIVIDERWIDTH ,pnl_contactList.getHeight() );
            slp_mainSplitPanel.setDividerSize(DIVIDERWIDTH);
            slp_mainSplitPanel.setDividerLocation(lastdividerposition);
            pnl_toggleQuickBarLeft.setBorder(openQuickbarBorder);
            pnl_toggleQuickBarRight.setBorder(openQuickbarBorder);
        } else {
           // pnl_contactList.setSize( 0,pnl_contactList.getHeight() );
            lastdividerposition = slp_mainSplitPanel.getDividerLocation();
            slp_mainSplitPanel.setDividerSize(0);
            pnl_toggleQuickBarLeft.setBorder(closedQuickbarBorder);
            pnl_toggleQuickBarRight.setBorder(closedQuickbarBorder);
            if (quickPanelOrientationIsLeft) {
                slp_mainSplitPanel.setDividerLocation(0);
            } else {
                slp_mainSplitPanel.setDividerLocation(slp_mainSplitPanel.getWidth());
            }

        }
        pnl_contactList.setVisible(visibility);
        quickPanelVisibility = visibility;
    }

    /**
     * positions the quickbar on the left or right accordint to the boolean value
     */
    public void setQuickPanelPosition(boolean left) {
        if (left) {//put on the left
            slp_mainSplitPanel.setLeftComponent(pnl_contactList);
            slp_mainSplitPanel.setRightComponent(tabpn_tabs);
            slp_mainSplitPanel.setResizeWeight(0.0);
            pnl_contactList.setTabAlignment(true);
            quickPanelOrientationIsLeft = true;
            pnl_toggleQuickBarLeft.setVisible(true);
            pnl_toggleQuickBarRight.setVisible(false);
            slp_mainSplitPanel.setDividerLocation(0);
        } else { //put to the right
            slp_mainSplitPanel.setLeftComponent(tabpn_tabs);
            slp_mainSplitPanel.setRightComponent(pnl_contactList);
            slp_mainSplitPanel.setResizeWeight(1.0);
            pnl_contactList.setTabAlignment(false);
            quickPanelOrientationIsLeft = false;
            pnl_toggleQuickBarLeft.setVisible(false);
            pnl_toggleQuickBarRight.setVisible(true);
            slp_mainSplitPanel.setDividerLocation(slp_mainSplitPanel.getWidth());
        }
        pnl_contactList.setSize( 0,pnl_contactList.getHeight() );
        slp_mainSplitPanel.revalidate();
        slp_mainSplitPanel.resetToPreferredSizes();
        this.pack();
        this.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabpn_tabs = new javax.swing.JTabbedPane();
        slp_mainSplitPanel = new javax.swing.JSplitPane();
        pnl_toggleQuickBarLeft = new javax.swing.JPanel();
        pnl_toggleQuickBarRight = new javax.swing.JPanel();
        mbar = new javax.swing.JMenuBar();
        m_main = new javax.swing.JMenu();
        mi_connection = new javax.swing.JMenuItem();
        mi_update = new javax.swing.JMenuItem();
        mi_quit = new javax.swing.JMenuItem();
        m_user = new javax.swing.JMenu();
        mi_profile = new javax.swing.JMenuItem();
        mi_showMuteBanList = new javax.swing.JMenuItem();
        mi_showQuickbar = new javax.swing.JCheckBoxMenuItem();
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

        tabpn_tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabpn_tabs.setFocusable(false);
        tabpn_tabs.setMinimumSize(new java.awt.Dimension(200, 350));
        tabpn_tabs.setPreferredSize(new java.awt.Dimension(400, 350));
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

        setTitle("CoopnetClient "+ Globals.getClientVersion());
        setMinimumSize(new java.awt.Dimension(600, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        slp_mainSplitPanel.setBorder(null);
        slp_mainSplitPanel.setResizeWeight(0.5);
        slp_mainSplitPanel.setFocusable(false);
        slp_mainSplitPanel.setMinimumSize(new java.awt.Dimension(600, 400));
        slp_mainSplitPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(slp_mainSplitPanel, gridBagConstraints);

        pnl_toggleQuickBarLeft.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnl_toggleQuickBarLeft.setFocusable(false);
        pnl_toggleQuickBarLeft.setMinimumSize(new java.awt.Dimension(5, 10));
        pnl_toggleQuickBarLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarLeftMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarLeftMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarLeftMousePressed(evt);
            }
        });

        javax.swing.GroupLayout pnl_toggleQuickBarLeftLayout = new javax.swing.GroupLayout(pnl_toggleQuickBarLeft);
        pnl_toggleQuickBarLeft.setLayout(pnl_toggleQuickBarLeftLayout);
        pnl_toggleQuickBarLeftLayout.setHorizontalGroup(
            pnl_toggleQuickBarLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        pnl_toggleQuickBarLeftLayout.setVerticalGroup(
            pnl_toggleQuickBarLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnl_toggleQuickBarLeft, gridBagConstraints);

        pnl_toggleQuickBarRight.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnl_toggleQuickBarRight.setFocusable(false);
        pnl_toggleQuickBarRight.setMinimumSize(new java.awt.Dimension(5, 10));
        pnl_toggleQuickBarRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarRightMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarRightMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnl_toggleQuickBarRightMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout pnl_toggleQuickBarRightLayout = new javax.swing.GroupLayout(pnl_toggleQuickBarRight);
        pnl_toggleQuickBarRight.setLayout(pnl_toggleQuickBarRightLayout);
        pnl_toggleQuickBarRightLayout.setHorizontalGroup(
            pnl_toggleQuickBarRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        pnl_toggleQuickBarRightLayout.setVerticalGroup(
            pnl_toggleQuickBarRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnl_toggleQuickBarRight, gridBagConstraints);

        mbar.setFocusable(false);

        m_main.setText("Client");

        mi_connection.setText("Disconnect");
        mi_connection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_connectionActionPerformed(evt);
            }
        });
        m_main.add(mi_connection);

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

        m_user.setText("User");
        m_user.setEnabled(false);

        mi_profile.setText("Edit Profile");
        mi_profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_profileActionPerformed(evt);
            }
        });
        m_user.add(mi_profile);

        mi_showMuteBanList.setText("Edit Mute/Ban List");
        mi_showMuteBanList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_showMuteBanListActionPerformed(evt);
            }
        });
        m_user.add(mi_showMuteBanList);

        mi_showQuickbar.setText("Show Quickbar");
        mi_showQuickbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mi_showQuickbarActionPerformed(evt);
            }
        });
        m_user.add(mi_showQuickbar);

        mbar.add(m_user);

        m_channels.setText("Channels");
        m_channels.setEnabled(false);

        mi_channelList.setText("Channel List");
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

        mi_addCurrentToFav.setText("Add Current to Favourites");
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

        mi_clientSettings.setText("Edit Settings");
        mi_clientSettings.setActionCommand("Client settings");
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

        getAccessibleContext().setAccessibleName("Client");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void mi_profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_profileActionPerformed
        Protocol.editProfile();
}//GEN-LAST:event_mi_profileActionPerformed

    private void mi_quitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_quitActionPerformed
        Client.quit(true);
}//GEN-LAST:event_mi_quitActionPerformed

    private void mi_clientSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_clientSettingsActionPerformed
        Globals.openSettingsFrame();
}//GEN-LAST:event_mi_clientSettingsActionPerformed

    private void mi_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_aboutActionPerformed
        String aboutMessage =
                "This software was developed by people who played " +
                "\nFallout Tactics multiplayer on Gamespy Arcade." +
                "\n\nIt aims to be a free GSA-like application that doesn't " +
                "\nannoy the player with advertisements and bugs." +
                "\n\nFuture plans include support for many different games" +
                "\nand added functionality to meet the gamers needs." +
                "\n\nVisit us at:" +
                "\n<html>&nbsp;&nbsp;&nbsp;&nbsp;<a href='http://coopnet.sourceforge.net'><font size='2'>http://coopnet.sourceforge.net</font></a></html>" +
                "\n\n\n<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "<i>Thank you for choosing Coopnet!</i></html>\n ";

        JOptionPane.showMessageDialog(Globals.getClientFrame(), aboutMessage, "About Coopnet", JOptionPane.PLAIN_MESSAGE);

}//GEN-LAST:event_mi_aboutActionPerformed

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

    public void refreshFavourites() {
        clearFavourites();
        for (String s : Settings.getFavourites()) {
            addFavourite(s);
        }
        m_channels.revalidate();
        pnl_contactList.refreshFavourites();
    }

    /**
     * Prints the message to a currently visible chatbox(room or main window)<P>
     * Usage:<ul> 
     * <li> name - the name of the sender
     * <li> message - the message to be printed
     * <li> mode : defines the style of the printed text, can be system or chat or whisper
     * 
     */
    public void printToVisibleChatbox(String name, String message, ChatStyles modeStyle, boolean popupEnabled) {

        Component tc = tabpn_tabs.getSelectedComponent();

        if (tc == null || tc instanceof LoginPanel) {
            if (popupEnabled) {
                JOptionPane.showMessageDialog(Globals.getClientFrame(), message, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (tc instanceof ChannelPanel) {
            ChannelPanel cp = (ChannelPanel) tc;
            cp.printMainChatMessage(name, message, modeStyle);
        } else if (tc instanceof RoomPanel) {
            RoomPanel rp = (RoomPanel) tc;
            rp.chat(name, message, modeStyle);
        } else if (tc instanceof PrivateChatPanel){
            PrivateChatPanel pcp = (PrivateChatPanel) tc;
            pcp.append(name, message, modeStyle);
        } else {
            ChannelPanel cp = TabOrganizer.getChannelPanel(0);
            if (cp != null) {
                cp.printMainChatMessage(name, message, modeStyle);
            }
        }
    }

    public void updateMenu() {
        mi_Sounds.setSelected(Settings.getSoundEnabled());
    }
    //Only used by ClientFramePanelHandler!
    protected JTabbedPane getTabHolder() {
        return tabpn_tabs;
    }
    
    

    private void mi_channelListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_channelListActionPerformed
        Globals.openChannelListFrame();
}//GEN-LAST:event_mi_channelListActionPerformed

    private void tabpn_tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabpn_tabsStateChanged
        TabOrganizer.putFocusOnTab(null);
}//GEN-LAST:event_tabpn_tabsStateChanged

    private void tabpn_tabsComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tabpn_tabsComponentAdded
        TabOrganizer.putFocusOnTab(null);
}//GEN-LAST:event_tabpn_tabsComponentAdded

    private void tabpn_tabsComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tabpn_tabsComponentRemoved
        TabOrganizer.putFocusOnTab(null);
}//GEN-LAST:event_tabpn_tabsComponentRemoved

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Client.quit(false);
    }//GEN-LAST:event_formWindowClosing

    private void mi_SoundsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_SoundsActionPerformed
        coopnetclient.utils.Settings.setSoundEnabled(mi_Sounds.isSelected());
}//GEN-LAST:event_mi_SoundsActionPerformed

    private void mi_manageFavsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_manageFavsActionPerformed
        Globals.openFavouritesFrame();
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
        Globals.openManageGamesFrame();
}//GEN-LAST:event_mi_manageGamesActionPerformed

    private void mi_guideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_guideActionPerformed
        TabOrganizer.openBrowserPanel("http://coopnet.sourceforge.net/guide.html");
}//GEN-LAST:event_mi_guideActionPerformed

private void mi_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_updateActionPerformed
    new Thread() {

        @Override
        public void run() {
            try {
                int n = JOptionPane.showConfirmDialog(null,
                        "<html>Would you like to update your CoopnetClient now?<br>" +
                        "(The client will close and update itself)", "Client outdated",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    try {
                        FileDownloader.downloadFile("http://coopnet.sourceforge.net/latestUpdater.php", "./CoopnetUpdater.jar");
                        Runtime rt = Runtime.getRuntime();
                        rt.exec("java -jar CoopnetUpdater.jar");
                        Client.quit(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handleException(e);
            }
        }
    }.start();
}//GEN-LAST:event_mi_updateActionPerformed

private void mi_bugReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_bugReportActionPerformed
    Globals.openBugReportFrame();
}//GEN-LAST:event_mi_bugReportActionPerformed

private void pnl_toggleQuickBarLeftMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarLeftMouseEntered
    if (Globals.getLoggedInStatus()) {
        if (Settings.getColorizeBody()) {
            pnl_toggleQuickBarLeft.setBackground(Settings.getSelectionColor());
        } else {
            pnl_toggleQuickBarLeft.setBackground(getHoverEffectColor());  
        }
    }
}//GEN-LAST:event_pnl_toggleQuickBarLeftMouseEntered

private void pnl_toggleQuickBarLeftMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarLeftMouseExited
    if (Globals.getLoggedInStatus()) {
        if (Settings.getColorizeBody()) {
            pnl_toggleQuickBarLeft.setBackground(Settings.getBackgroundColor());
        } else {
            pnl_toggleQuickBarLeft.setBackground((Color) UIManager.get("Panel.background"));
        }
    }
}//GEN-LAST:event_pnl_toggleQuickBarLeftMouseExited

private void pnl_toggleQuickBarLeftMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarLeftMousePressed
    if (Globals.getLoggedInStatus()) {
        quickPanelVisibility = !quickPanelVisibility;
        setQuickBarVisibility(quickPanelVisibility);
        quickPanelFlashing = false;
    }
}//GEN-LAST:event_pnl_toggleQuickBarLeftMousePressed

private void pnl_toggleQuickBarRightMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarRightMouseEntered
    if (Globals.getLoggedInStatus()) {
        if (Settings.getColorizeBody()) {
            pnl_toggleQuickBarRight.setBackground(Settings.getSelectionColor());
        } else {
            pnl_toggleQuickBarRight.setBackground(getHoverEffectColor());
        }
    }
}//GEN-LAST:event_pnl_toggleQuickBarRightMouseEntered

private void pnl_toggleQuickBarRightMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarRightMouseExited
    if (Globals.getLoggedInStatus()) {
        if (Settings.getColorizeBody()) {
            pnl_toggleQuickBarRight.setBackground(Settings.getBackgroundColor());
        } else {
            pnl_toggleQuickBarRight.setBackground((Color) UIManager.get("Panel.background"));
        }
    }
}//GEN-LAST:event_pnl_toggleQuickBarRightMouseExited

private void pnl_toggleQuickBarRightMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnl_toggleQuickBarRightMousePressed
    if (Globals.getLoggedInStatus()) {
        quickPanelVisibility = !quickPanelVisibility;
        setQuickBarVisibility(quickPanelVisibility);
        quickPanelFlashing = false;
    }
}//GEN-LAST:event_pnl_toggleQuickBarRightMousePressed

private void mi_showMuteBanListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_showMuteBanListActionPerformed
    if (Globals.getLoggedInStatus()) {
        Globals.openMuteBanTableFrame();
    }
}//GEN-LAST:event_mi_showMuteBanListActionPerformed

private void mi_showQuickbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_showQuickbarActionPerformed
    setQuickBarVisibility(mi_showQuickbar.isSelected());
}//GEN-LAST:event_mi_showQuickbarActionPerformed

private void mi_connectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mi_connectionActionPerformed
    if(!Globals.getConnectionStatus()){
        Client.startConnection();
    }else{
        setQuickBarVisibility(false);
        Client.disconnect();        
    }
}//GEN-LAST:event_mi_connectionActionPerformed

private Color getHoverEffectColor(){
    Color clr = null;
    clr = (Color) UIManager.get("List.selectionBackground"); 
    if(clr == null){
        clr = (Color) UIManager.get("List[Selected].textBackground");
    }
    return clr;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu m_channels;
    private javax.swing.JMenu m_help;
    private javax.swing.JMenu m_main;
    private javax.swing.JMenu m_options;
    private javax.swing.JMenu m_user;
    private javax.swing.JMenuBar mbar;
    private javax.swing.JCheckBoxMenuItem mi_Sounds;
    private javax.swing.JMenuItem mi_about;
    private javax.swing.JMenuItem mi_addCurrentToFav;
    private javax.swing.JMenuItem mi_bugReport;
    private javax.swing.JMenuItem mi_channelList;
    private javax.swing.JMenuItem mi_clientSettings;
    private javax.swing.JMenuItem mi_connection;
    private javax.swing.JMenuItem mi_favourites;
    private javax.swing.JMenuItem mi_guide;
    private javax.swing.JMenuItem mi_manageFavs;
    private javax.swing.JMenuItem mi_manageGames;
    private javax.swing.JMenuItem mi_profile;
    private javax.swing.JMenuItem mi_quit;
    private javax.swing.JSeparator mi_seperator;
    private javax.swing.JMenuItem mi_showMuteBanList;
    private javax.swing.JCheckBoxMenuItem mi_showQuickbar;
    private javax.swing.JMenuItem mi_update;
    private javax.swing.JPanel pnl_toggleQuickBarLeft;
    private javax.swing.JPanel pnl_toggleQuickBarRight;
    private javax.swing.JSplitPane slp_mainSplitPanel;
    private javax.swing.JTabbedPane tabpn_tabs;
    // End of variables declaration//GEN-END:variables
}
