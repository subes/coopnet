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

package coopnetclient;

import coopnetclient.modules.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.launchers.LinuxLauncher;
import coopnetclient.launchers.WindowsLauncher;
import coopnetclient.modules.Colorizer;
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
        if (Globals.getOperatingSystem() == Globals.OS_WINDOWS) {
            if(Globals.getDebug()){
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
            if(Globals.getDebug()){
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
                try{
                    Colorizer.initLAF();
                    Globals.openClientFrame();
                    startConnection();

                    try {
                        sleep(100);
                    } catch (Exception e) {
                    }

                    if (Settings.getFirstRun()) {
                        Globals.getClientFrame().addGuideTab();
                        Settings.setFirstRun(false);
                    }
                }catch(Exception e){
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
        Globals.closeRoomPanel();
        if (handlerThread != null) {
            handlerThread.stopThread();
        }
        handlerThread = null;
    }
}
