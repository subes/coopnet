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
package coopnetclient;

import coopnetclient.dialogs.TrayIconHintDialog;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.protocol.out.Message;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.threads.EdtRunner;
import coopnetclient.utils.Logger;
import coopnetclient.utils.OnlineClientData;
import coopnetclient.utils.Updater;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.settings.Settings;
import java.awt.SystemTray;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import javax.swing.JOptionPane;

public final class Client {

    private static final int QUIT_SLEEP = 500;
    private static final int STOPCONNECTION_SLEEP = 50;
    private static HandlerThread handlerThread;

    private Client() {
    }

    public static void send(Message message) {
        if (message.isSent()) {
            throw new IllegalArgumentException("A Message can only be sent once! They send themselves!");
        }

        Logger.logOutTraffic(message.getLogString());

        if (handlerThread != null) {
            handlerThread.addToOutQueue(message.getMessage());
        }
    }

    public static String getInterfaceAddress(String interfaceName) {
        String ip = null;
        try {
            Enumeration nifEnm = NetworkInterface.getNetworkInterfaces();
            while (nifEnm.hasMoreElements()) {
                NetworkInterface nif = (NetworkInterface) nifEnm.nextElement();
                if (nif.getDisplayName().toLowerCase().contains(interfaceName)) {
                    Enumeration<InetAddress> iadrs = nif.getInetAddresses();
                    while (iadrs.hasMoreElements()) {
                        InetAddress iadr = iadrs.nextElement();
                        String ipStr = iadr.getHostAddress();
                        //only want IPv4
                        if(ip == null || ipStr.length() < ip.length()){
                            ip = ipStr;
                        }
                    }
                    return ip;
                }
            }
        } catch (Exception ex) {
            Logger.log(ex);
        }
        return "";
    }

    /**
     * Initializes and starts the client.
     */
    public static void startup() {

        OnlineClientData.readServerAddress();
        OnlineClientData.checkAndUpdateGameData();
        new EdtRunner() {

            @Override
            public void handledRun() throws Throwable {
                FrameOrganizer.init();
                startConnection();
                Updater.checkAndUpdate();
            }
        }.invokeLater();
    }

    public static void startConnection() {
        if (!Globals.getConnectionStatus()) {
            TabOrganizer.closeAllTabs();
            TabOrganizer.openConnectingPanel();
            Globals.setConnectionStatus(true);
            handlerThread = new HandlerThread();
            handlerThread.start();
        } else {
            throw new IllegalStateException("Client is already connected, you shouldn't be able to reconnect!");
        }
    }

    private static void stopConnection() {
        Protocol.quit();
        Globals.setConnectionStatus(false);

        try {
            Thread.sleep(STOPCONNECTION_SLEEP);
        } catch (InterruptedException ex) {
            Logger.log(ex);
        }

        Globals.setLoggedInStatus(false);

        if (handlerThread != null) {
            handlerThread.stopThread();
        }
        handlerThread = null;
    }

    public static void disconnect() {
        if (Globals.getConnectionStatus()) {
            FrameOrganizer.getClientFrame().setQuickPanelVisibility(false);
            Client.stopConnection();
            TabOrganizer.closeAllTabs();
            FrameOrganizer.closeChannelListFrame();
            FrameOrganizer.closeChangePasswordFrame();
            FrameOrganizer.closeShowProfileFrame();
            FrameOrganizer.closeEditProfileFrame();
        } else {
            throw new IllegalStateException("Client is already disconnected, " +
                    "you shouldn't be able to disconnect again!");
        }
    }

    public static void quit(boolean overrideMinimizeToTray) {
        //hide the mainframe: trayicon enabled
        if (SystemTray.isSupported() && !overrideMinimizeToTray && Settings.
                getTrayIconEnabled()) {
            if (Settings.getShowMinimizeToTrayHint()) {
                new TrayIconHintDialog(null, false);
            }
            FrameOrganizer.getClientFrame().setVisible(false);
        } else {//trayicon disabled or overridden
            //show warning if there are running transfers
            if (Globals.getTransferModel().isAnyTransferActive()) {
                int option = JOptionPane.showConfirmDialog(null,
                        "<html>WARNING: There is one or more active filetransfer!<br>" +
                        "Do you really want to quit Coopnet?<br>" +
                        "If you quit, any active transfer will be cancelled!",
                        "WARNING: Active filetransfer(s)!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (option == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            //cancel any filesendings
            TabOrganizer.cancelFileSendingOnClose();
            try {
                Thread.sleep(QUIT_SLEEP);
            } catch (InterruptedException e) {
                Logger.log(e);
            }
            //close connection
            Client.stopConnection();
            //save sizes
            Settings.setMainFrameMaximised(FrameOrganizer.getClientFrame().
                    getExtendedState());

            if (FrameOrganizer.getClientFrame().getExtendedState() ==
                    javax.swing.JFrame.NORMAL) {
                Settings.setMainFrameHeight(FrameOrganizer.getClientFrame().
                        getHeight());
                Settings.setMainFrameWidth(FrameOrganizer.getClientFrame().
                        getWidth());
                Settings.setMainFrameMaximised(FrameOrganizer.getClientFrame().
                        getExtendedState());
            }
            //unbind hotkeys
            Hotkeys.cleanUp();
            FrameOrganizer.cleanUp();
            System.exit(0);
        }
    }
}
