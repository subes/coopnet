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

import coopnetclient.enums.LogTypes;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.BugReportFrame;
import coopnetclient.frames.ChangePasswordFrame;
import coopnetclient.frames.ChannelListFrame;
import coopnetclient.frames.CreateRoomFrame;
import coopnetclient.frames.EditProfileFrame;
import coopnetclient.frames.GameSettingsFrame;
import coopnetclient.frames.ManageGamesFrame;
import coopnetclient.frames.JoinRoomPasswordFrame;
import coopnetclient.frames.MuteBanListFrame;
import coopnetclient.frames.SettingsFrame;
import coopnetclient.frames.ShowProfileFrame;
import coopnetclient.frames.TextPreviewFrame;
import coopnetclient.utils.Settings;
import coopnetclient.frames.clientframe.ClientFrame;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.ui.SystemTrayPopup;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.frames.models.TransferTableModel;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.launcher.Launcher;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Globals {

    //Constants
    public static final String CLIENT_VERSION = "0.101.2";

    public static final int JDPLAY_MAXSEARCHRETRIES = 12; //1 try = 5 secs; 12 tries = 60 secs
    public static final int JDPLAY_SEARCHVALIDATIONCOUNT = 1; //set to 1 to be sure the joined session is not a temp one, though slower launch!

    //Set via static{}
    private static OperatingSystems operatingSystem;
    private static String lastOpenedDir;
    private static String wineCommand;
    //Preset value
    private static boolean debug = false;
    private static boolean connectionStatus = false;
    private static boolean loggedInStatus = false;
    private static boolean sleepModeStatus = false;
    //First set when known
    private static String thisPlayer_loginName;
    private static String thisPlayer_inGameName;
    private static String serverIP;
    private static int serverPort;
    //Objects
    private static ClientFrame clientFrame;
    private static ContactListModel contactList = new ContactListModel();
    private static ChangePasswordFrame changePasswordFrame;
    private static ChannelListFrame channelListFrame;
    private static GameSettingsFrame gameSettingsFrame;
    private static SettingsFrame settingsFrame;
    private static ManageGamesFrame manageGamesFrame;
    private static EditProfileFrame editProfileFrame;
    private static ShowProfileFrame showProfileFrame;
    private static JoinRoomPasswordFrame roomJoinPasswordFrame;
    private static CreateRoomFrame createRoomFrame;
    private static BugReportFrame bugReportFrame;
    private static TextPreviewFrame textPreviewFrame;
    private static MuteBanListFrame muteBanTableFrame = null;
    private static SystemTray tray = null;
    private static TrayIcon trayIcon = null;
    private static boolean trayAdded = false;
    private static String MyIP = null;
    private static String currentPath = "";
    private static TransferTableModel transferModel = new TransferTableModel();
    /*******************************************************************/


    public static void detectOperatingSystem(){
        if (System.getProperty("line.separator").equals("\r\n")) {
            operatingSystem = OperatingSystems.WINDOWS;
        }else{
            if(System.getProperty("line.separator").equals("\r")){
                //MacOS, currently treated as Linux coz unsupported
                operatingSystem = OperatingSystems.LINUX;
            }else{
                operatingSystem = OperatingSystems.LINUX;
            }
        }

        Logger.log(LogTypes.LOG, "Operating System is "+operatingSystem.toString() +" ("+System.getProperty("os.name")+")");

        if (operatingSystem == OperatingSystems.WINDOWS) {
            lastOpenedDir = System.getenv("USERPROFILE");
        } else {
            lastOpenedDir = System.getenv("HOME");
        }
    }

    public static void init(){
        try {
            currentPath = Client.getCurrentDirectory().getCanonicalPath();
        } catch (Exception ex) {
            Logger.log(ex);
        }
        
        //Set debug - do not disable again
        if(debug == false){
            debug = Settings.getDebugMode();
        }
        
        //initialise and add trayicon if needed
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            
            trayIcon = new TrayIcon(Icons.coopnetNormalIcon.getImage(), "Coopnet client", new SystemTrayPopup());
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                        }

                        @Override
                        public void mousePressed(java.awt.event.MouseEvent evt) {
                            if (evt.getClickCount() >= 2) {
                                Globals.getClientFrame().setVisible(true);
                            }
                        }
                    });
        }

        wineCommand = Settings.getWineCommand();
    }

    public static String getWineCommand(){
        return wineCommand;
    }

    public static TransferTableModel getTransferModel(){
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
            ioe.printStackTrace();
            return new File(name);
        }
    }

    public static String getResourceAsString(String name) {
        try {
            return getResource(name).getCanonicalPath();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return name;
        }
    }

    public static void addTrayIcon() {
        try {
            if (SystemTray.isSupported() && !trayAdded && !debug) {
                tray.add(trayIcon);
                trayAdded = true;
            }
        } catch (Exception e) {
        }
    }

    public static void removeTrayIcon() {
        if (SystemTray.isSupported()) {
            try {
                tray.remove(trayIcon);
                trayAdded = false;
            } catch (Exception e) {
            }
        }
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public static String getMyIP() {
        return MyIP;
    }

    public static void setMyIP(String IP) {
        MyIP = IP;
    }

    public static void recolorFrames() {
        Colorizer.colorize(clientFrame);
        Colorizer.colorize(changePasswordFrame);
        Colorizer.colorize(channelListFrame);
        Colorizer.colorize(gameSettingsFrame);
        Colorizer.colorize(settingsFrame);
        Colorizer.colorize(manageGamesFrame);

        Colorizer.colorize(editProfileFrame);
        Colorizer.colorize(showProfileFrame);

        Colorizer.colorize(roomJoinPasswordFrame);
        Colorizer.colorize(createRoomFrame);

        Colorizer.colorize(bugReportFrame);
        Colorizer.colorize(textPreviewFrame);
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
        getClientFrame().updateStatus();
    }

    public static boolean getConnectionStatus() {
        return connectionStatus;
    }

    public static void setLoggedInStatus(boolean value) {
        loggedInStatus = value;
        getClientFrame().updateStatus();
    }

    public static boolean getLoggedInStatus() {
        return loggedInStatus;
    }

    public static void setThisPlayer_loginName(String value) {
        thisPlayer_loginName = value;
    }

    public static String getThisPlayer_loginName() {
        return thisPlayer_loginName;
    }

    public static void setThisPlayer_inGameName(String value) {
        thisPlayer_inGameName = value;
        new Thread(){
            @Override
            public void run(){
                Launcher.updatePlayerName();
            }
        }.start();
    }

    public static String getThisPlayer_inGameName() {
        return thisPlayer_inGameName;
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
        if (Settings.getSleepEnabled() && sleepModeStatus != value ) {
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

    public static void openClientFrame() {
        if (clientFrame == null) {
            clientFrame = new ClientFrame();
            setupFrame(clientFrame);
        } else {
            Logger.log(LogTypes.WARNING, "ClientFrame is supposed to be created only once!");
        }
    }

    public static void closeClientFrame() {
        if (clientFrame != null) {
            clientFrame.dispose();
            clientFrame = null;
        }
    }

    public static ClientFrame getClientFrame() {
        return clientFrame;
    }

    public static void openShowProfileFrame(String name, String ingameName, String country, String webpage) {
        if (showProfileFrame != null) {
            Point prevLocation = showProfileFrame.getLocation();
            showProfileFrame.dispose();
            showProfileFrame = null;
            showProfileFrame = new ShowProfileFrame(name, ingameName, country, webpage);
            setupFrame(showProfileFrame, prevLocation);
        } else {
            showProfileFrame = new ShowProfileFrame(name, ingameName, country, webpage);
            setupFrame(showProfileFrame);
        }
    }

    public static void closeShowProfileFrame() {
        if (showProfileFrame != null) {
            showProfileFrame.dispose();
            showProfileFrame = null;
        }
    }

    public static MuteBanListFrame getMuteBanTableFrame() {
        return muteBanTableFrame;
    }

    public static void openMuteBanTableFrame() {
        if (muteBanTableFrame != null) {
            Point prevLocation = muteBanTableFrame.getLocation();
            muteBanTableFrame.dispose();
            muteBanTableFrame = null;
            muteBanTableFrame = new MuteBanListFrame();
            setupFrame(muteBanTableFrame, prevLocation);
        } else {
            muteBanTableFrame = new MuteBanListFrame();
            setupFrame(muteBanTableFrame);
        }
    }

    public static void closeMuteBanTableFrame() {
        if (muteBanTableFrame != null) {
            muteBanTableFrame.dispose();
            muteBanTableFrame = null;
        }
    }

    public static void openEditProfileFrame(String name, String ingamename, String email, String country, String webpage) {
        if (editProfileFrame != null) {
            Point prevLocation = editProfileFrame.getLocation();
            closeChangePasswordFrame();
            editProfileFrame.dispose();
            editProfileFrame = null;
            editProfileFrame = new EditProfileFrame(name, ingamename, email, country, webpage);
            setupFrame(editProfileFrame, prevLocation);
        } else {
            editProfileFrame = new EditProfileFrame(name, ingamename, email, country, webpage);
            setupFrame(editProfileFrame);
        }
    }

    public static void closeEditProfileFrame() {
        if (editProfileFrame != null) {
            closeChangePasswordFrame();
            editProfileFrame.dispose();
            editProfileFrame = null;
        }
    }

    public static EditProfileFrame getEditProfileFrame() {
        return editProfileFrame;
    }

    public static ChangePasswordFrame getChangePasswordFrame() {
        return changePasswordFrame;
    }

    public static void openChangePasswordFrame() {
        if (changePasswordFrame != null) {
            changePasswordFrame.setVisible(true);
        } else {
            changePasswordFrame = new ChangePasswordFrame();
            setupFrame(changePasswordFrame);
        }
    }

    public static void closeChangePasswordFrame() {
        if (changePasswordFrame != null) {
            changePasswordFrame.dispose();
            changePasswordFrame = null;
        }
    }

    public static void openChannelListFrame() {
        if (channelListFrame != null) {
            channelListFrame.setVisible(true);
        } else {
            channelListFrame = new ChannelListFrame();
            setupFrame(channelListFrame);
        }
    }

    public static void closeChannelListFrame() {
        if (channelListFrame != null) {
            channelListFrame.dispose();
            channelListFrame = null;
        }
    }

    public static void openGameSettingsFrame(final RoomData roomData) {
        //isHost depends

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        if (gameSettingsFrame != null) {
                            gameSettingsFrame.setVisible(true);
                        } else {
                            gameSettingsFrame = new GameSettingsFrame(roomData);
                            //Frame decides if visible
                        }
                    }
                });
    }

    public static GameSettingsFrame getGameSettingsFrame() {
        return gameSettingsFrame;
    }

    public static void closeGameSettingsFrame() {
        if (gameSettingsFrame != null) {
            gameSettingsFrame.dispose();
            gameSettingsFrame = null;
        }
    }

    public static void closeJoinRoomPasswordFrame() {
        if (roomJoinPasswordFrame != null) {
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
        }
    }

    public static void openJoinRoomPasswordFrame(String channel, String roomHost) {
        if (createRoomFrame != null) {
            createRoomFrame.dispose();
            createRoomFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two RoomJoinFrames! Closing the other one. (openJoinRoomPasswordFrame)");
        }
        if (roomJoinPasswordFrame != null) {
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two roomJoinPasswordFrames! Closing the other one. (openJoinRoomPasswordFrame)");
        }

        roomJoinPasswordFrame = new JoinRoomPasswordFrame(roomHost, channel);
        setupFrame(roomJoinPasswordFrame);
    }

    public static void openJoinRoomPasswordFrame(String ID) {
        if (createRoomFrame != null) {
            createRoomFrame.dispose();
            createRoomFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two createRoomFrames! Closing the other one. (openJoinRoomPasswordFrame)");
        }
        if (roomJoinPasswordFrame != null) {
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two roomJoinPasswordFrames! Closing the other one. (openJoinRoomPasswordFrame)");
        }

        roomJoinPasswordFrame = new JoinRoomPasswordFrame(ID);
        setupFrame(roomJoinPasswordFrame);
    }

    public static void showWrongPasswordNotification() {
        if (roomJoinPasswordFrame != null) {
            roomJoinPasswordFrame.showWrongPasswordNotification();
        }
    }

    public static void openCreateRoomFrame(String channel) {
        if (createRoomFrame != null) {
            createRoomFrame.dispose();
            createRoomFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two createRoomFrames! Closing the other one. (openCreateRoomFrame)");
        }
        if (roomJoinPasswordFrame != null) {
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
            Logger.log(LogTypes.WARNING, "It shouldn't be possible to create two roomJoinPasswordFrames! Closing the other one. (openCreateRoomFrame)");
        }

        createRoomFrame = new CreateRoomFrame(channel);
        setupFrame(createRoomFrame);
    }

    public static void closeRoomCreationFrame() {
        if (createRoomFrame != null) {
            createRoomFrame.dispose();
            createRoomFrame = null;
        }
        closeJoinRoomPasswordFrame();
    }

    public static void openSettingsFrame() {
        if (settingsFrame != null) {
            settingsFrame.setVisible(true);
        } else {
            settingsFrame = new SettingsFrame();
            setupFrame(settingsFrame);
        }
    }

    public static void closeSettingsFrame() {
        if (settingsFrame != null) {
            settingsFrame.dispose();
            settingsFrame = null;
        }
    }

    public static ManageGamesFrame getManageGamesFrame() {
        return manageGamesFrame;
    }

    public static void openManageGamesFrame() {
        if (manageGamesFrame != null) {
            manageGamesFrame.setVisible(true);
        } else {
            manageGamesFrame = new ManageGamesFrame();
            setupFrame(manageGamesFrame);
        }
    }

    public static void closeManageGamesFrame() {
        if (manageGamesFrame != null) {
            manageGamesFrame.dispose();
            manageGamesFrame = null;
        }
    }

    public static BugReportFrame getBugReportFrame() {
        return bugReportFrame;
    }

    public static void openBugReportFrame() {
        if (bugReportFrame != null) {
            bugReportFrame.setVisible(true);
        } else {
            bugReportFrame = new BugReportFrame();
            setupFrame(bugReportFrame);
        }
    }

    public static void openBugReportFrame(Throwable exception, String trafficLog) {
        if (bugReportFrame != null) {
            bugReportFrame.setVisible(true);
        } else {
            bugReportFrame = new BugReportFrame(exception, trafficLog);
            setupFrame(bugReportFrame);
        }
    }

    public static void closeBugReportFrame() {
        if (bugReportFrame != null) {
            closeTextPreviewFrame();
            bugReportFrame.dispose();
            bugReportFrame = null;
        }
    }

    public static void openTextPreviewFrame(String title, String text) {
        if (textPreviewFrame != null) {
            Point prevPosition = textPreviewFrame.getLocation();
            textPreviewFrame.dispose();
            textPreviewFrame = null;
            textPreviewFrame = new TextPreviewFrame(title, text);
            setupFrame(textPreviewFrame, prevPosition);
        } else {
            textPreviewFrame = new TextPreviewFrame(title, text);
            setupFrame(textPreviewFrame);
        }
    }

    public static void closeTextPreviewFrame() {
        if (textPreviewFrame != null) {
            textPreviewFrame.dispose();
            textPreviewFrame = null;
        }
    }

    private static void setupFrame(final JFrame frame) {
        frame.setLocationRelativeTo(null);
        Colorizer.colorize(frame);
        frame.pack();

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisible(true);
                    }
                });
    }

    private static void setupFrame(JFrame frame, Point position) {
        frame.setLocation(position);
        setupFrame(frame);
    }
}
