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

import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.modules.Colorizer;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.SwingUtilities;

/**
 * Contains the global  fields of the client, these fields are used by most of the other classes.<br>
 * Also has the StartUp method that will initialize and start the client
 */
public class Client {

    private static HandlerThread handlerThread;

    /**
     * Sends the command to the server
     */
    public static void send(String command, String channel) {
        if (channel != null && channel.length() > 0) {
            command = "on " + GameDatabase.IDofGame(channel) + ":" + command;
        }
        if (handlerThread != null) {
            command += Protocol.MESSAGE_DELIMITER;
            handlerThread.addToOutQueue(command);
        }
        TrafficLogger.append("OUT: " + command);
        if (Globals.getDebug()) {
            System.out.println("[T]\tOUT: " + command);
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
                        System.out.println("Hamachi found!");
                    }
                }
            }
        } catch (Exception ex) {
        }
        return ip;
    }

    /**
     *Initializes and starts the client
     * 
     */
    public static void startup() {

        readServerAddress();

        SwingUtilities.invokeLater(new Thread() {

            @Override
            public void run() {
                try {
                    Colorizer.initLAF();
                    Globals.openClientFrame();
                    startConnection();

                    try {
                        sleep(100);
                    } catch (Exception e) {
                    }

                    if (Settings.getFirstRun()) {
                        TabOrganizer.openBrowserPanel("http://coopnet.sourceforge.net/guide.html");
                        Settings.setFirstRun(false);
                    }
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }

            }
        });
    }

    public static void startConnection() {
        handlerThread = new HandlerThread();
        handlerThread.start();
    }

    public static void stopConnection() {
        Globals.setLoggedInStatus(false);

        if (handlerThread != null) {
            handlerThread.stopThread();
        }
        handlerThread = null;
    }

    public static void initInstantLaunch(final String game, final String mod, final String hostIP, final int maxPlayers, final boolean compatible, final boolean isHost) {
        Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                "Initializing game ...",
                ChatStyles.SYSTEM,false);

        LaunchInfo launchInfo;

        LaunchMethods method = GameDatabase.getLaunchMethod(game, mod);

        if (method == LaunchMethods.PARAMETER) {
            launchInfo = new ParameterLaunchInfo(game, mod, hostIP, isHost, true);
        } else if (method == LaunchMethods.CHAT_ONLY) {
            throw new IllegalArgumentException("You can't launch from CHAT_ONLY channel! GameName: " + game + " ChildName: " + mod);
        } else {
            launchInfo = new DirectPlayLaunchInfo(game, mod, hostIP, isHost, true, compatible);
        }

        Launcher.initialize(launchInfo);

        if (!Launcher.isInitialized()) {
            Client.send(Protocol.closeRoom(), game);
            Client.send(Protocol.gameClosed(), game);
            TabOrganizer.getChannelPanel(game).enablebuttons();
        }
    }

    public static void instantLaunch(String channel) {
        if (Launcher.isInitialized()) {
            TabOrganizer.getChannelPanel(channel).disableButtons();

            Launcher.launch();

            Client.send(Protocol.gameClosed(), channel);
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
                if(Globals.getDebug()){
                    System.out.println("[L]\tServer address read: "+server);
                }
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
}
