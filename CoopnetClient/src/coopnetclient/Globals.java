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

import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.BugReportFrame;
import coopnetclient.frames.ChangePasswordFrame;
import coopnetclient.frames.ChannelListFrame;
import coopnetclient.frames.CreateRoomFrame;
import coopnetclient.frames.EditProfileFrame;
import coopnetclient.frames.FavouritesFrame;
import coopnetclient.frames.GameSettingsFrame;
import coopnetclient.frames.ManageGamesFrame;
import coopnetclient.frames.JoinRoomPasswordFrame;
import coopnetclient.frames.SettingsFrame;
import coopnetclient.frames.ShowProfileFrame;
import coopnetclient.frames.TextPreviewFrame;
import coopnetclient.modules.Settings;
import coopnetclient.frames.clientframe.ClientFrame;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.launcher.OLDLauncher;
import coopnetclient.modules.Colorizer;
import coopnetclient.modules.models.ContactListModel;
import coopnetclient.utils.launcher.Launcher;
import java.awt.Point;
import javax.swing.JFrame;

public class Globals {

    //Constants
    public static final int JDPLAY_MAXSEARCHRETRIES = 10;
    
    //Set via static{}
    private static OperatingSystems operatingSystem;
    private static String lastOpenedDir;
    
    //Preset value
    private static boolean debug = false;
    private static final String clientVersion = "0.96.1";
    private static boolean loggedInStatus = false;
    private static boolean sleepModeStatus = false;
    private static boolean isPlayingStatus = false;
    
    //First set when known
    private static String thisPlayer_loginName;
    private static String thisPlayer_inGameName;
    
    //Objects
    private static OLDLauncher launcher;
    
    private static ClientFrame clientFrame;
    
    private static ContactListModel contacts = new ContactListModel();
    
    private static ChangePasswordFrame changePasswordFrame;
    private static ChannelListFrame channelListFrame;
    private static FavouritesFrame favouritesFrame;
    private static GameSettingsFrame gameSettingsFrame;
    private static SettingsFrame settingsFrame;
    private static ManageGamesFrame manageGamesFrame;
    
    private static EditProfileFrame editProfileFrame;
    private static ShowProfileFrame showProfileFrame;
    
    private static JoinRoomPasswordFrame roomJoinPasswordFrame;
    private static CreateRoomFrame createRoomFrame;
    
    private static BugReportFrame  bugReportFrame;
    private static TextPreviewFrame textPreviewFrame;
    
    /*******************************************************************/
    
    static {
        //Detect OS
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            operatingSystem = OperatingSystems.WINDOWS;
            lastOpenedDir = System.getenv("USERPROFILE");
        } else {
            operatingSystem = OperatingSystems.LINUX;
            lastOpenedDir = System.getenv("HOME");
        }
        //Set debug
        debug = Settings.getDebugMode();
    }
    
    public static void recolorFrames(){
        Colorizer.colorize(clientFrame);
        Colorizer.colorize(changePasswordFrame);
        Colorizer.colorize(channelListFrame);
        Colorizer.colorize(favouritesFrame);
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
    
    public static void enableDebug(){
        debug = true;
    }
    
    public static boolean getDebug(){
        return debug;
    }
    
    public static String getClientVersion(){
        return clientVersion;
    }
    
    public static ContactListModel getContactList(){
        return contacts;
    }
    
    public static void setLoggedInStatus(boolean value){
        loggedInStatus = value;
        getClientFrame().updateLoggedInStatus();
    }
    
    public static boolean getLoggedInStatus(){
        return loggedInStatus;
    }
    
    public static void setThisPlayer_loginName(String value){
        thisPlayer_loginName = value;
    }
    
    public static String getThisPlayer_loginName(){
        return thisPlayer_loginName;
    }
    
    public static void setThisPlayer_inGameName(String value){
        thisPlayer_inGameName = value;
        Launcher.updatePlayerName();
    }
    
    public static String getThisPlayer_inGameName(){
        return  thisPlayer_inGameName;
    }
    
    public static OperatingSystems getOperatingSystem(){
        return operatingSystem;
    }
    
    public static void setSleepModeStatus(boolean value){
        if(Settings.getSleepEnabled()){
            sleepModeStatus = value;
            TabOrganizer.updateSleepMode();
        }
    }
    
    public static boolean getSleepModeStatus(){
        return sleepModeStatus;
    }
    
    public static void setIsPlayingStatus(boolean value){
        isPlayingStatus = value;
    }
    
    public static boolean getIsPlayingStatus(){
        return isPlayingStatus;
    }
    
    public static void setLastOpenedDir(String value){
        lastOpenedDir = value;
    }
    
    public static String getLastOpenedDir(){
        return lastOpenedDir;
    }
    
    public static void setLauncher(OLDLauncher value){
        launcher = value;
    }
    
    public static OLDLauncher getLauncher(){
        return launcher;
    }
    
    public static void openClientFrame(){
        if(clientFrame == null){
            clientFrame = new ClientFrame();
            setupFrame(clientFrame);
        }else{
            if(getDebug()){
                System.out.println("[W]\tClientFrame is supposed to be created only once!");
            }
        }
    }
    
    public static void closeClientFrame(){
        if(clientFrame != null){
            clientFrame.dispose();
            clientFrame = null;
        }
    }
    
    public static ClientFrame getClientFrame(){
        return clientFrame;
    }
    
    public static void openShowProfileFrame(String name, String email, String country, String webpage){
        if(showProfileFrame != null){
            Point prevLocation = showProfileFrame.getLocation();
            showProfileFrame.dispose();
            showProfileFrame = null;
            showProfileFrame = new ShowProfileFrame(name, email, country, webpage);
            setupFrame(showProfileFrame, prevLocation);
        }else{
            showProfileFrame = new ShowProfileFrame(name, email, country, webpage);
            setupFrame(showProfileFrame);
        }
    }
    
    public static void closeShowProfileFrame(){
        if(showProfileFrame != null){
            showProfileFrame.dispose();
            showProfileFrame = null;
        }
    }
    
    public static void openEditProfileFrame(String name, String ingamename, String email, String emailpublicity, String country, String webpage){
        if(editProfileFrame != null){
            Point prevLocation = editProfileFrame.getLocation();
            closeChangePasswordFrame();
            editProfileFrame.dispose();
            editProfileFrame = null;
            editProfileFrame = new EditProfileFrame(name, ingamename, email, emailpublicity, country, webpage);
            setupFrame(editProfileFrame, prevLocation);
        }else{
            editProfileFrame = new EditProfileFrame(name, ingamename, email, emailpublicity, country, webpage);
            setupFrame(editProfileFrame);
        }
    }
    
    public static void closeEditProfileFrame(){
        if(editProfileFrame != null){
            closeChangePasswordFrame();
            editProfileFrame.dispose();
            editProfileFrame = null;
        }
    }
    
    public static void openChangePasswordFrame(){
        if(changePasswordFrame != null){
            changePasswordFrame.setVisible(true);
        }else{
            changePasswordFrame = new ChangePasswordFrame();
            setupFrame(changePasswordFrame);
        }
    }
    
    public static void closeChangePasswordFrame(){
        if(changePasswordFrame != null){
            changePasswordFrame.dispose();
            changePasswordFrame = null;
        }
    }
    
    public static void openChannelListFrame(){
        if(channelListFrame != null){
            channelListFrame.setVisible(true);
        }else{
            channelListFrame = new ChannelListFrame();
            setupFrame(channelListFrame);
        }
    }
    
    public static void closeChannelListFrame(){
        if(channelListFrame != null){
            channelListFrame.dispose();
            channelListFrame = null;
        }
    }
    
    public static void openGameSettingsFrame(String gameName, String modName){
        if(gameSettingsFrame != null){
            gameSettingsFrame.setVisible(true);
        }else{
            gameSettingsFrame = new GameSettingsFrame(gameName, modName);
            setupFrame(gameSettingsFrame);
        }
    }
    
    public static void openGameSettingsFrame(String gameName, String modName, String roomName , String password, int modIndex, int maxPlayers, boolean compatible){
        if(gameSettingsFrame != null){
            gameSettingsFrame.setVisible(true);
        }else{
            gameSettingsFrame = new GameSettingsFrame(gameName, modName, roomName, password, modIndex, maxPlayers, compatible);
            setupFrame(gameSettingsFrame);
        }
    }
    
    public static void closeGameSettingsFrame(){
        if(gameSettingsFrame != null){
            gameSettingsFrame.dispose();
            gameSettingsFrame = null;
        }
    }
    
    public static void openJoinRoomPasswordFrame(String channel, String roomHost){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
            createRoomFrame = null;
            if(getDebug()){
                System.out.println("[W]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (CreateRoomFrame)");
            }
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
            if(getDebug()){
                System.out.println("[W]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (RoomJoinPasswordFrame)");
            }
        }
        
        roomJoinPasswordFrame = new JoinRoomPasswordFrame(roomHost, channel);
        setupFrame(roomJoinPasswordFrame);
    }
    
    public static void openCreateRoomFrame(String channel){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
            createRoomFrame = null;
            if(getDebug()){
                System.out.println("[W]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (CreateRoomFrame)");
            }
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
            if(getDebug()){
                System.out.println("[W]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (RoomJoinPasswordFrame)");
            }
        }
        
        createRoomFrame = new CreateRoomFrame(channel);
        setupFrame(createRoomFrame);
    }
    
    public static void closeRoomCreationFrame(){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
            createRoomFrame = null;
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
            roomJoinPasswordFrame = null;
        }
    }
    
    public static void openFavouritesFrame(){
        if(favouritesFrame != null){
            favouritesFrame.setVisible(true);
        }else{
            favouritesFrame = new FavouritesFrame();
            setupFrame(favouritesFrame);
        }
    }
    
    public static void closeFavouritesFrame(){
        if(favouritesFrame != null){
            favouritesFrame.dispose();
            favouritesFrame = null;
        }
    }
    
    public static void openSettingsFrame(){
        if(settingsFrame != null){
            settingsFrame.setVisible(true);
        }else{
            settingsFrame = new SettingsFrame();
            setupFrame(settingsFrame);
        }
    }
    
    public static void closeSettingsFrame(){
        if(settingsFrame != null){
            settingsFrame.dispose();
            settingsFrame = null;
        }
    }
    
    public static void openManageGamesFrame(){
        if(manageGamesFrame != null){
            manageGamesFrame.setVisible(true);
        }else{
            manageGamesFrame = new ManageGamesFrame();
            setupFrame(manageGamesFrame);
        }
    }
    
    public static void closeManageGamesFrame(){
        if(manageGamesFrame != null){
            manageGamesFrame.dispose();
            manageGamesFrame = null;
        }
    }
    
    public static void openBugReportFrame(){
        if(bugReportFrame != null){
            bugReportFrame.setVisible(true);
        }else{
            bugReportFrame = new BugReportFrame();
            setupFrame(bugReportFrame);
        }
    }
    
    public static void openBugReportFrame(Exception exception, String trafficLog){
        if(bugReportFrame != null){
            bugReportFrame.setVisible(true);
        }else{
            bugReportFrame = new BugReportFrame(exception, trafficLog);
            setupFrame(bugReportFrame);
        }
    }
    
    public static void closeBugReportFrame(){
        if(bugReportFrame != null){
            closeTextPreviewFrame();
            bugReportFrame.dispose();
            bugReportFrame = null;
        }
    }
    
    public static void openTextPreviewFrame(String title, String text){
        if(textPreviewFrame != null){
            Point prevPosition = textPreviewFrame.getLocation();
            textPreviewFrame.dispose();
            textPreviewFrame = null;
            textPreviewFrame = new TextPreviewFrame(title, text);
            setupFrame(textPreviewFrame, prevPosition);
        }else{
            textPreviewFrame = new TextPreviewFrame(title, text);
            setupFrame(textPreviewFrame);
        }
        
    }
    
    public static void closeTextPreviewFrame(){
        if(textPreviewFrame != null){
            textPreviewFrame.dispose();
            textPreviewFrame = null;
        }
    }
    
    private static void setupFrame(JFrame frame){
        frame.setLocationRelativeTo(null);
        Colorizer.colorize(frame);
        frame.pack();
        frame.setVisible(true);
    }
    
    private static void setupFrame(JFrame frame, Point position){
        frame.setLocation(position);
        Colorizer.colorize(frame);
        frame.pack();
        frame.setVisible(true);
    }
}
