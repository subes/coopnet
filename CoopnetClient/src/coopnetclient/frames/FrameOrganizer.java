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

package coopnetclient.frames;

import coopnetclient.ErrorHandler;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.popupmenus.TrayPopupMenu;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.ui.CoopnetTrayIcon;
import coopnetclient.utils.ui.Icons;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class FrameOrganizer {

    private static ClientFrame clientFrame;
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
    private static MuteBanListFrame muteBanTableFrame;
    private static CoopnetTrayIcon trayIcon;

    private FrameOrganizer() {
    }

    public static void init(){
        openClientFrame();
        if (Settings.getFirstRun()) {
            TabOrganizer.openBrowserPanel("http://coopnet.sourceforge.net/guide.html");
            Settings.setFirstRun(false);
        }
        initTrayIcon();
    }

    public static void cleanUp(){
        if(trayIcon != null){
            trayIcon.removeTrayIcon();
            trayIcon = null;
        }
    }

    public static void updateSettings(){
        getClientFrame().updateSettings();

        initTrayIcon();

        if (trayIcon != null){
            trayIcon.updateSettings();
        }
    }

    private static void initTrayIcon() {
        try{
            if(SystemTray.isSupported() && Settings.getTrayIconEnabled()){
                trayIcon = new CoopnetTrayIcon();
                trayIcon.addTrayIcon();
            }else{
                if(trayIcon != null){
                    trayIcon.removeTrayIcon();
                    trayIcon = null;
                }
            }
        }catch(AWTException e){
            ErrorHandler.handle(e);
        }
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
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

    public static void openClientFrame() {
        if (clientFrame == null) {
            clientFrame = new ClientFrame();
            setupFrame(clientFrame);

            if (Settings.getRememberMainFrameSize()) {
                //load the size from settings
                int width = Settings.getMainFrameWidth();
                int height = Settings.getMainFrameHeight();
                clientFrame.setSize(new Dimension(width, height));
                clientFrame.setPreferredSize(new Dimension(width, height));
                //maximise if needed
                int status = Settings.getMainFrameMaximised();
                if (status == JFrame.MAXIMIZED_BOTH) {
                    clientFrame.setExtendedState(status);
                }
            }
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

    public static void showClientFrame() {
        if(clientFrame != null){
            FrameOrganizer.getClientFrame().setVisible(true);
            FrameOrganizer.getClientFrame().requestFocus();
        }
    }

    public static void hideClientFrame() {
        if(clientFrame != null){
            FrameOrganizer.getClientFrame().setVisible(false);
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

    private static void checkRoomCreationFramesAlreadyOpen(){
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
    }

    public static void openJoinRoomPasswordFrame(String channel, String roomHost) {
        checkRoomCreationFramesAlreadyOpen();

        roomJoinPasswordFrame = new JoinRoomPasswordFrame(roomHost, channel);
        setupFrame(roomJoinPasswordFrame);
    }

    public static void openJoinRoomPasswordFrame(String id) {
        checkRoomCreationFramesAlreadyOpen();

        roomJoinPasswordFrame = new JoinRoomPasswordFrame(id);
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
