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
package coopnetclient;

import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.launchers.Launcher;
import coopnetclient.modules.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.launchers.LinuxLauncher;
import coopnetclient.launchers.WindowsLauncher;
import coopnetclient.modules.ColoredChatHandler;
import coopnetclient.modules.Colorizer;
import coopnetclient.modules.SoundPlayer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import javax.swing.SwingUtilities;

/**
 * Contains the global  fields of the client, these fields are used by most of the other classes.<br>
 * Also has the StartUp method that will initialize and start the client
 */
public class Client {

    private static HandlerThread handlerThread;
    private static final int LAUNCH_TIMEOUT = 10;

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
        if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS) {
            if (Globals.getDebug()) {
                System.out.println("[L]\tOS: windows");
            }
            Globals.setLauncher(new WindowsLauncher());
            //LOAD LIBRARIES
            try {
                System.loadLibrary("lib/JDPlay_jni");
            } catch (UnsatisfiedLinkError er) {
                er.printStackTrace();
            }
        } else { //linux stuff
            if (Globals.getDebug()) {
                System.out.println("[L]\tOS: linux");
            }
            Globals.setLauncher(new LinuxLauncher());
        }
        //Load Registry library
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        //INITIALISE FIELDS
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

    public static void initInstantLaunch(final String channel, final int modindex, final int maxPlayers, final boolean compatible,final boolean isHost) {
        new Thread() {

            @Override
            public void run() {
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                        "Initializing...",
                        ChatStyles.SYSTEM);
                String modname = null;
                if (modindex > 0) {
                    modname = GameDatabase.getGameModNames(channel)[Integer.valueOf(modindex)].toString();
                }
                Globals.getLauncher().initialize(channel, modname, isHost, "", compatible, maxPlayers);
            }
        }.start();
    }

    public static void instantLaunch(final String channel, final int modindex, final int maxPlayers, final boolean compatible) {
        new Thread() {

            @Override
            public void run() {
                Launcher launcher = Globals.getLauncher();
                int i = 0;
                while (!launcher.isInitialised() && i < LAUNCH_TIMEOUT) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    i++;
                }

                if (!launcher.isInitialised()) {
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed to start the game!", ChatStyles.SYSTEM);
                    Client.send(Protocol.closeRoom(), channel);
                    Globals.setIsPlayingStatus(false);
                    Client.send(Protocol.gameClosed(), channel);
                    Globals.setSleepModeStatus(false);
                    TabOrganizer.getChannelPanel(channel).enablebuttons();
                    return;
                }

                Globals.setIsPlayingStatus(true);
                TabOrganizer.getChannelPanel(channel).disablebuttons();
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                        "Game launching... please wait!",
                        ChatStyles.SYSTEM);
                //play sound
                SoundPlayer.playLaunchSound();

                if (Settings.getSleepEnabled()) {
                    Globals.setSleepModeStatus(true);
                }

                boolean launched = Globals.getLauncher().launch();
                if (!launched) {
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed to start the game!", ChatStyles.SYSTEM);
                }
                Client.send(Protocol.closeRoom(), channel);
                Globals.setIsPlayingStatus(false);
                Client.send(Protocol.gameClosed(), channel);
                Globals.setSleepModeStatus(false);
                TabOrganizer.getChannelPanel(channel).enablebuttons();
            }
        }.start();
    }
}
