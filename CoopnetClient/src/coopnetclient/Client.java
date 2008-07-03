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

import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.launchers.LinuxLauncher;
import coopnetclient.launchers.Launcher;
import coopnetclient.launchers.WindowsLauncher;
import coopnetclient.modules.Colorizer;
import coopnetclient.frames.clientframe.ClientFrame;
import coopnetclient.frames.clientframe.RoomPanel;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
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

            if (command.length() > HandlerThread.WRITEBUFFER_SIZE) {
                for (String piece : cutToLength(command, HandlerThread.WRITEBUFFER_SIZE)) {
                    handlerThread.addToOutQueue(piece);
                }
            } else {
                handlerThread.addToOutQueue(command);
            }
        }
        TrafficLogger.append("OUT: " + command);
        if (Globals.debug) {
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
        if (Globals.os.equals("windows")) {
            Globals.launcher = new WindowsLauncher();
            //Settings.setWineCommand(""); launcher = new LinuxLauncher(); 
            //launcher = new DummyLauncher(); 
            //LOAD LIBRARIES
            try {
                System.loadLibrary("lib/JDPlay_jni");
                System.loadLibrary("lib/ICE_JNIRegistry");
                Globals.registryOK = true;
                Class.forName("com.ice.jni.registry.Registry");
                Class.forName("com.ice.jni.registry.RegistryKey");
            } catch (UnsatisfiedLinkError er) {
                System.out.println("Error while loading external dlls");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else { //linux stuff
            Globals.launcher = new LinuxLauncher();
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
                Colorizer.initLAF();
                Globals.clientFrame = new ClientFrame();
                startConnection();
                
                try {
                    sleep(100);
                } catch (Exception e) {
                }
                
                if (Settings.getFirstRun()) {
                    Globals.clientFrame.addGuideTab();
                    Settings.setFirstRun(false);
                }
            }
        });
    }

    public static void startConnection() {
        handlerThread = new HandlerThread();
        handlerThread.start();
    }

    public static void stopConnection() {
        Globals.loggedIn = false;
        Globals.currentRoomPanel = null;
        if (handlerThread != null) {
            handlerThread.stopThread();
        }
        handlerThread = null;
    }

    private static String[] cutToLength(String stringtocut, int size) {
        Vector<String> dataarray = new Vector<String>();

        int begin, end;
        begin = 0;
        end = 0;

        while (end < stringtocut.length()) {
            end += size;
            if (end > stringtocut.length()) {
                end = stringtocut.length();
            }
            dataarray.add(stringtocut.substring(begin, end));
            begin = end;
        }
        return dataarray.toArray(new String[1]);
    }
}
