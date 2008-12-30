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
package coopnetclient;

import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.Colorizer;
import coopnetclient.utils.FileDownloader;
import coopnetclient.utils.Verification;
import coopnetclient.protocol.out.Message;
import coopnetclient.utils.InactivityWatcher;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.Logger;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.awt.AWTEvent;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Contains the global  fields of the client, these fields are used by most of the other classes.<br>
 * Also has the StartUp method that will initialize and start the client
 */
public class Client {

    private static HandlerThread handlerThread;

    public static void send(Message message) {
        if (message.isSent()) {
            throw new IllegalArgumentException("A Message can only be sent once! They send themselves!");
        }

        Logger.logOutTraffic(message.getLogString());

        if (handlerThread != null) {
            handlerThread.addToOutQueue(message.getMessage());
        }
    }

    public static String getHamachiAddress() {
        String ip = "";
        try {
            Enumeration nifEnm = NetworkInterface.getNetworkInterfaces();
            while (nifEnm.hasMoreElements()) {
                NetworkInterface nif = (NetworkInterface) nifEnm.nextElement();
                if (nif.getDisplayName().contains("Hamachi")) {
                    Enumeration<InetAddress> iadrs = nif.getInetAddresses();
                    while (iadrs.hasMoreElements()) {
                        InetAddress iadr = iadrs.nextElement();
                        ip = iadr.getHostAddress();
                        return ip;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     *Initializes and starts the client
     * 
     */
    public static void startup() {
    
        Toolkit.getDefaultToolkit().addAWTEventListener(new InactivityWatcher(),
                 AWTEvent.MOUSE_EVENT_MASK
                |AWTEvent.MOUSE_MOTION_EVENT_MASK
                |AWTEvent.MOUSE_WHEEL_EVENT_MASK
                |AWTEvent.KEY_EVENT_MASK);

        readServerAddress();        
        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                try {
                    Colorizer.initLAF();
                    GameDatabase.loadVersion();
                    GameDatabase.load("", GameDatabase.dataFilePath);
                    GameDatabase.detectGames();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            Globals.openClientFrame();                            
                            if (Settings.getFirstRun()) {
                                TabOrganizer.openBrowserPanel("http://coopnet.sourceforge.net/guide.html");
                                Settings.setFirstRun(false);
                            }
                            startConnection();
                            checkAndUpdateClient();
                        }
                    });
                    checkAndUpdateGameData();
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        });
    }

    public static void startConnection() {
        if(!Globals.getConnectionStatus()){
            TabOrganizer.closeAllTabs();
            Globals.setConnectionStatus(true);
            handlerThread = new HandlerThread();
            handlerThread.start();
        }else{
            throw new IllegalStateException("Client is already connected, you shouldn't be able to reconnect!");
        }
    }

    private static void stopConnection() {
        Protocol.quit();
        Globals.setConnectionStatus(false);

        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
        }

        Globals.setLoggedInStatus(false);

        if (handlerThread != null) {
            handlerThread.stopThread();
        }
        handlerThread = null;
    }

    public static void disconnect() {
        if(Globals.getConnectionStatus()){
            Globals.getClientFrame().setQuickPanelVisibility(false);
            Client.stopConnection();
            TabOrganizer.closeAllTabs();
            Globals.closeChannelListFrame();
            Globals.closeChangePasswordFrame();
            Globals.closeShowProfileFrame();
            Globals.closeEditProfileFrame();
        }else{
            throw new IllegalStateException("Client is already disconnected, you shouldn't be able to disconnect again!");
        }
    }

    public static void quit(boolean override) {
        //hide the mainframe: trayicon enabled
        if (SystemTray.isSupported() && !override && Settings.getTrayIconEnabled() && !Globals.getDebug()) {
            Globals.getClientFrame().setVisible(false);
        } else {
            //trayicon disabled or overridden
            //cancel any filesendings
            TabOrganizer.cancelFileSendingOnClose();
            try{Thread.sleep(500);}catch(Exception e){}
            //close connection
            Client.stopConnection();
            //save sizes
            coopnetclient.utils.Settings.setMainFrameMaximised(Globals.getClientFrame().getExtendedState());

            if (Globals.getClientFrame().getExtendedState() == javax.swing.JFrame.NORMAL) {
                coopnetclient.utils.Settings.setMainFrameHeight(Globals.getClientFrame().getHeight());
                coopnetclient.utils.Settings.setMainFrameWidth(Globals.getClientFrame().getWidth());
            }
            //unbind hotkeys
            Hotkeys.cleanUp();
            System.exit(0);
        }
    }

    public static void initInstantLaunch(final String gameName, final String mod, final String hostIP, final int maxPlayers, final boolean isHost, final String roomName, String password) {
        Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                "Initializing game ...",
                ChatStyles.SYSTEM, false);

        LaunchInfo launchInfo;

        LaunchMethods method = GameDatabase.getLaunchMethod(gameName, mod);

        if (method == LaunchMethods.PARAMETER) {
            launchInfo = new ParameterLaunchInfo(gameName, mod, hostIP, isHost, true, roomName, password);
        } else if (method == LaunchMethods.CHAT_ONLY) {
            throw new IllegalArgumentException("You can't launch from CHAT_ONLY channel! GameName: " + gameName + " ChildName: " + mod);
        } else {
            launchInfo = new DirectPlayLaunchInfo(gameName, mod, hostIP, isHost, true, password);
        }

        Launcher.initialize(launchInfo);

        if (!Launcher.isInitialized()) {
            Protocol.closeRoom();
            Protocol.gameClosed(gameName);
            TabOrganizer.getChannelPanel(gameName).enablebuttons();
        }
    }

    public static void instantLaunch(String channel) {
        if (Launcher.isInitialized()) {
            TabOrganizer.getChannelPanel(channel).disableButtons();
            Launcher.launch();
            Protocol.gameClosed(channel);
            TabOrganizer.getChannelPanel(channel).enablebuttons();
            Launcher.deInitialize();
        }
    }

    private static void readServerAddress() {
        String server = null;
        //Determine server IP and Port
        if (Globals.getServerIP() == null) {
            try {
                URL sourceforge = new URL("http://coopnet.sourceforge.net/CoopnetServer.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(sourceforge.openStream()));

                StringBuilder temp = new StringBuilder();
                int c;
                while ((c = br.read()) != -1) {
                    temp.append((char) c);
                }
                br.close();
                br = null;
                server = temp.toString().trim();
                Logger.log(LogTypes.LOG, "Server address read: " + server);
            } catch (Exception e) {
                e.printStackTrace();
                server = null;
            }

            if (server != null) {
                String ip = server.substring(0, server.indexOf(":"));
                Globals.setServerIP(ip);
                int port = Integer.parseInt(server.substring(server.indexOf(":") + 1));
                Globals.setServerPort(port);
                Settings.setLastValidServerIP(ip);
                Settings.setLastValidServerPort(port);
            } else {
                Globals.setServerIP(Settings.getLastValidServerIP());
                Globals.setServerPort(Settings.getLastValidServerPort());
            }
        }
    }

    public static void checkAndUpdateGameData() {
        new Thread() {

            @Override
            public void run() {
                BufferedReader br = null;
                try {
                    try {
                        URL url = new URL("http://coopnet.sourceforge.net/gamedata.xml");
                        br = new BufferedReader(new InputStreamReader(url.openStream()));
                    } catch (java.net.UnknownHostException e) {
                        return;
                    } catch (java.io.FileNotFoundException e) {
                        return;
                    } catch (java.net.ConnectException e) {
                        return;
                    }
                    int lastversion = 0;
                    String readHeader1 = br.readLine();
                    String readHeader2 = br.readLine();
                    String[] parts = readHeader2.split(" ");
                    lastversion = new Integer(parts[1]);
                    GameDatabase.loadVersion();
                    if (GameDatabase.version < lastversion) {
                        Logger.log(LogTypes.LOG, "Downloading new gamedata");
                        BufferedOutputStream bo = null;
                        File destfile = new File(GameDatabase.dataFilePath);
                        if (!destfile.createNewFile()) {
                            destfile.delete();
                            destfile.createNewFile();
                        }
                        bo = new BufferedOutputStream(new FileOutputStream(destfile));
                        //save version
                        bo.write((readHeader1 + "\n").getBytes());
                        bo.write((readHeader2 + "\n").getBytes());
                        //save the rest
                        int readedbyte;
                        while ((readedbyte = br.read()) != -1) {
                            bo.write(readedbyte);
                        }
                        bo.flush();
                        try {
                            br.close();
                            bo.close();
                        } catch (Exception ex) {
                        }
                        GameDatabase.loadVersion();
                        GameDatabase.load("", GameDatabase.dataFilePath);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Globals.getClientFrame(), "You have an outdated version of the gamedata, but couldn't update it!", "Gamedata outdated", JOptionPane.INFORMATION_MESSAGE);
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

    public static File getCurrentDirectory() throws URISyntaxException {
        File location = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (location.isFile()) {
            return location.getParentFile();
        } else {
            return location.getParentFile().getParentFile();//loc is build/classes
        }
    }

    public static void checkAndUpdateClient() {
        new Thread() {

            @Override
            public void run() {
                URL url;
                BufferedReader br = null;
                try {
                    url = new URL("http://coopnet.sourceforge.net/lastclientversion");
                    br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String version = br.readLine();
                    if (!Verification.verifyClientVersion(version)) {
                        Globals.getClientFrame().enableUpdate();
                        new Thread() {

                            @Override
                            public void run() {
                                try {
                                    int n = JOptionPane.showConfirmDialog(null,
                                            "<html>You have an outdated version of the client!<br>" +
                                            "Would you like to update now?<br>(The client will close and update itself)",
                                            "Client outdated", JOptionPane.YES_NO_OPTION);
                                    if (n == JOptionPane.YES_OPTION) {
                                        try {
                                            FileDownloader.downloadFile("http://coopnet.sourceforge.net/latestUpdater.php", Globals.getResourceAsString("CoopnetUpdater.jar"));
                                            Runtime rt = Runtime.getRuntime();
                                            rt.exec("java -jar CoopnetUpdater.jar",null,getCurrentDirectory()  );
                                            quit(true);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    ErrorHandler.handleException(e);
                                }
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }
}
