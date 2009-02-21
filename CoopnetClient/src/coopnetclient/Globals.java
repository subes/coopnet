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

import coopnetclient.enums.LogTypes;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.utils.settings.Settings;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.frames.models.TransferTableModel;
import coopnetclient.utils.Logger;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.settings.Favourites;
import coopnetclient.utils.settings.SettingsHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class Globals {

    //Constants
    public static final String CLIENT_VERSION = "0.102.0";
    //Increment this, when changes to the protocol commands have been done
    public static final int COMPATIBILITY_VERSION = 5;
    private static OperatingSystems operatingSystem;
    private static String lastOpenedDir;
    private static String wineCommand;
    private static String lastRoomName = "My room, come and play!";
    private static boolean debug;
    private static boolean connectionStatus;
    private static boolean loggedInStatus;
    private static boolean sleepModeStatus;
    private static String thisPlayerLoginName;
    private static String thisPlayerInGameName;
    private static String serverIP;
    private static int serverPort;
    private static ContactListModel contactList = new ContactListModel();
    private static String clientIP;
    private static String currentPath = "";
    private static TransferTableModel transferModel = new TransferTableModel();
    private static ArrayList<String> higlightList = new ArrayList<String>();

    private Globals() {
    }

    /**
     * @return the lastRoomName
     */
    public static String getLastRoomName() {
        return lastRoomName;
    }

    /**
     * @param aLastRoomName the lastRoomName to set
     */
    public static void setLastRoomName(String theLastRoomName) {
        lastRoomName = theLastRoomName;
    }

    /*******************************************************************/
    public static void detectOperatingSystem() {
        final String lineSeperator = System.getProperty("line.separator");

        if (lineSeperator.equals("\r\n")) {
            operatingSystem = OperatingSystems.WINDOWS;
        } else {
            if (lineSeperator.equals("\r")) {
                //MacOS, currently treated as Linux coz unsupported
                operatingSystem = OperatingSystems.LINUX;
            } else {
                operatingSystem = OperatingSystems.LINUX;
            }
        }

        Logger.log(LogTypes.LOG, "Operating System is " + operatingSystem.toString() + " (" + System.getProperty("os.name") + ")");

        if (operatingSystem == OperatingSystems.WINDOWS) {
            lastOpenedDir = System.getenv("USERPROFILE");
        } else {
            lastOpenedDir = System.getenv("HOME");
        }
    }

    public static void init() {
        try {
            currentPath = Client.getCurrentDirectory().getCanonicalPath();
        } catch (Exception ex) {
            Logger.log(ex);
        }

        SettingsHelper.init();
        Settings.init();
        GameDatabase.init();
        Favourites.init();

        //Set debug - do not disable again
        if (!debug) {
            debug = Settings.getDebugMode();
        }

        wineCommand = Settings.getWineCommand();

        FrameOrganizer.init();

    }

    public static boolean isHighlighted(String userName) {
        return higlightList.contains(userName);
    }

    public static void setHighlightOn(String userName) {
        if (!higlightList.contains(userName)) {
            higlightList.add(userName);
            TabOrganizer.updateHighlights();
        }
    }

    public static void unSetHighlightOn(String userName) {
        higlightList.remove(userName);
        TabOrganizer.updateHighlights();
    }

    public static void clearHighlights() {
        higlightList.clear();
        TabOrganizer.updateHighlights();
    }

    public static ArrayList<String> getHighlightList() {
        return higlightList;
    }

    public static String getWineCommand() {
        return wineCommand;
    }

    public static TransferTableModel getTransferModel() {
        return transferModel;
    }

    public static File getResource(String name) {
        try {
            String addedDivider = "";
            if ((!name.startsWith("/")) || (!name.startsWith("\\"))) {
                addedDivider = "/";
            }
            File absolute = new File(currentPath + addedDivider + name);
            return absolute.getCanonicalFile();
        } catch (IOException ioe) {
            Logger.log(ioe);
            return new File(name);
        }
    }

    public static String getResourceAsString(String name) {
        try {
            return getResource(name).getCanonicalPath();
        } catch (IOException ioe) {
            Logger.log(ioe);
            return name;
        }
    }

    public static String getClientIP() {
        return clientIP;
    }

    public static void setClientIP(String ip) {
        clientIP = ip;
    }

    public static void enableDebug() {
        debug = true;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static ContactListModel getContactList() {
        return contactList;
    }

    public static void setConnectionStatus(boolean value) {
        connectionStatus = value;
        FrameOrganizer.getClientFrame().updateStatus();
    }

    public static boolean getConnectionStatus() {
        return connectionStatus;
    }

    public static void setLoggedInStatus(boolean value) {
        loggedInStatus = value;
        FrameOrganizer.getClientFrame().updateStatus();
    }

    public static boolean getLoggedInStatus() {
        return loggedInStatus;
    }

    public static void setThisPlayerLoginName(String value) {
        thisPlayerLoginName = value;
    }

    public static String getThisPlayerLoginName() {
        return thisPlayerLoginName;
    }

    public static void setThisPlayerInGameName(String value) {
        thisPlayerInGameName = value;
        new Thread() {

            @Override
            public void run() {
                Launcher.updatePlayerName();
            }
        }.start();
    }

    public static String getThisPlayerInGameName() {
        return thisPlayerInGameName;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void setServerIP(String ip) {
        serverIP = ip;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int port) {
        serverPort = port;
    }

    public static OperatingSystems getOperatingSystem() {
        return operatingSystem;
    }

    public static void setSleepModeStatus(boolean value) {
        if (Settings.getSleepEnabled() && sleepModeStatus != value) {
            sleepModeStatus = value;
            TabOrganizer.updateSleepMode();
        }
    }

    public static boolean getSleepModeStatus() {
        return sleepModeStatus;
    }

    public static void setLastOpenedDir(String value) {
        lastOpenedDir = value;
    }

    public static String getLastOpenedDir() {
        return lastOpenedDir;
    }

    public static void updateSettings() {
        if (TabOrganizer.getRoomPanel() != null && TabOrganizer.getRoomPanel().isHost()) {
            Hotkeys.reBindHotKey(Hotkeys.ACTION_LAUNCH);
        }

        FrameOrganizer.updateSettings();
        TabOrganizer.updateSettings();
    }
}
