package coopnetclient;

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
import coopnetclient.frames.clientframe.RoomPanel;
import coopnetclient.launchers.Launcher;
import coopnetclient.modules.Colorizer;
import java.awt.Point;

public class Globals {

    //Constants
    public static final int OS_WINDOWS = 0;
    public static final int OS_LINUX = 1;
    
    //Set via static{}
    private static int operatingSystem;
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
    private static Launcher launcher;
    private static ClientFrame clientFrame;
    private static RoomPanel roomPanel; //TODO: move handling of that to ClientFrame
    
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
    
    static {
        //Detect OS
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            operatingSystem = OS_WINDOWS;
            lastOpenedDir = System.getenv("USERPROFILE");
        } else {
            operatingSystem = OS_LINUX;
            lastOpenedDir = System.getenv("HOME");
        }
        
        //Set debug
        debug = Settings.getDebugMode();
    }
    
    public static void recolorFrames(){
        Colorizer.colorize(clientFrame);
        //colorise other open windows too
        Colorizer.colorize(channelListFrame);
        Colorizer.colorize(changePasswordFrame);
        Colorizer.colorize(showProfileFrame);
        Colorizer.colorize(editProfileFrame);
        Colorizer.colorize(gameSettingsFrame);
        //dont color settingsframe cuz it fucks it up
        //TODO: add more frames to this
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
    
    public static void setLoggedInStatus(boolean value){
        loggedInStatus = value;
    }
    
    public static boolean getLoggedInStatus(){
        return loggedInStatus;
    }
    
    public static void setThisPlayer_loginName(String value){
        thisPlayer_loginName = value;
        //System.out.println("newLoginName: "+value);
    }
    
    public static String getThisPlayer_loginName(){
        return thisPlayer_loginName;
    }
    
    public static void setThisPlayer_inGameName(String value){
        thisPlayer_inGameName = value;
        //System.out.println("newIngameName: "+value);
    }
    
    public static String getThisPlayer_inGameName(){
        return  thisPlayer_inGameName;
    }
    
    public static int getOperatingSystem(){
        return operatingSystem;
    }
    
    public static void setSleepModeStatus(boolean value){
        sleepModeStatus = value;
        Globals.getClientFrame().updateSleepMode();
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
    
    public static void setLauncher(Launcher value){
        launcher = value;
    }
    
    public static Launcher getLauncher(){
        return launcher;
    }
    
    public static void openClientFrame(){
        if(clientFrame == null){
            clientFrame = new ClientFrame();
            clientFrame.setVisible(true);
        }else{
            if(getDebug()){
                System.out.println("[WARNING]\tClientFrame is supposed to be created only once!");
            }
        }
    }
    
    public static void closeClientFrame(){
        if(clientFrame != null){
            clientFrame.dispose();
        }
    }
    
    public static ClientFrame getClientFrame(){
        return clientFrame;
    }
    
    public static void openRoomPanel(boolean isHost, String channel, String modindex, String ip, boolean compatible, String hamachiIp, int maxPlayers){
        if(roomPanel == null){
            roomPanel = new RoomPanel(isHost, channel, modindex, ip, compatible, hamachiIp, maxPlayers);
            clientFrame.addRoomPanelTab();
        }else{
            if(getDebug()){
                System.out.println("[WARNING]\tClose the current RoomPanel before opening a new one!");
            }
        }
    }
    
    public static void closeRoomPanel(){
        if(roomPanel != null){
            getLauncher().stop();
            closeGameSettingsFrame();
            clientFrame.removeRoomPanelTab();
            roomPanel = null;
        }
    }
    
    public static RoomPanel getRoomPanel(){
        return roomPanel;
    }
    
    public static void openShowProfileFrame(String name, String email, String country, String webpage){
        if(showProfileFrame != null){
            Point prevLocation = showProfileFrame.getLocation();
            showProfileFrame.dispose();
            showProfileFrame = new ShowProfileFrame(name, email, country, webpage);
            showProfileFrame.setLocation(prevLocation);
            showProfileFrame.setVisible(true);
        }else{
            showProfileFrame = new ShowProfileFrame(name, email, country, webpage);
            showProfileFrame.setVisible(true);
        }
    }
    
    public static void closeShowProfileFrame(){
        if(showProfileFrame != null){
            showProfileFrame.dispose();
        }
    }
    
    public static void openEditProfileFrame(String name, String ingamename, String email, String emailpublicity, String country, String webpage){
        if(editProfileFrame != null){
            Point prevLocation = editProfileFrame.getLocation();
            closeChangePasswordFrame();
            editProfileFrame.dispose();
            editProfileFrame = new EditProfileFrame(name, ingamename, email, emailpublicity, country, webpage);
            editProfileFrame.setLocation(prevLocation);
            editProfileFrame.setVisible(true);
        }else{
            editProfileFrame = new EditProfileFrame(name, ingamename, email, emailpublicity, country, webpage);
            editProfileFrame.setVisible(true);
        }
    }
    
    public static void closeEditProfileFrame(){
        if(editProfileFrame != null){
            closeChangePasswordFrame();
            editProfileFrame.dispose();
        }
    }
    
    public static void openChangePasswordFrame(){
        if(changePasswordFrame != null){
            changePasswordFrame.setVisible(true);
        }else{
            changePasswordFrame = new ChangePasswordFrame();
            changePasswordFrame.setVisible(true);
        }
    }
    
    public static void closeChangePasswordFrame(){
        if(changePasswordFrame != null){
            changePasswordFrame.dispose();
        }
    }
    
    public static void openChannelListFrame(){
        if(channelListFrame != null){
            channelListFrame.setVisible(true);
        }else{
            channelListFrame = new ChannelListFrame();
            channelListFrame.setVisible(true);
        }
    }
    
    public static void closeChannelListFrame(){
        if(channelListFrame != null){
            channelListFrame.dispose();
        }
    }
    
    public static void openGameSettingsFrame(String gameName, String modName){
        if(gameSettingsFrame != null){
            gameSettingsFrame.setVisible(true);
        }else{
            gameSettingsFrame = new GameSettingsFrame(gameName, modName);
            gameSettingsFrame.setVisible(true);
        }
    }
    
    public static void closeGameSettingsFrame(){
        if(gameSettingsFrame != null){
            gameSettingsFrame.dispose();
        }
    }
    
    public static void openJoinRoomPasswordFrame(String channel, String roomHost){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
            if(getDebug()){
                System.out.println("[WARNING]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (CreateRoomFrame)");
            }
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
            if(getDebug()){
                System.out.println("[WARNING]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (RoomJoinPasswordFrame)");
            }
        }
        
        roomJoinPasswordFrame = new JoinRoomPasswordFrame(roomHost, channel);
        roomJoinPasswordFrame.setVisible(true);
    }
    
    public static void openCreateRoomFrame(String channel){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
            if(getDebug()){
                System.out.println("[WARNING]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (CreateRoomFrame)");
            }
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
            if(getDebug()){
                System.out.println("[WARNING]\tIt shouldn't be possible to create two RoomCreationFrames! Closing the other one. (RoomJoinPasswordFrame)");
            }
        }
        
        createRoomFrame = new CreateRoomFrame(channel);
        createRoomFrame.setVisible(true); 
    }
    
    public static void closeRoomCreationFrame(){
        if(createRoomFrame != null){
            createRoomFrame.dispose();
        }
        if(roomJoinPasswordFrame != null){
            roomJoinPasswordFrame.dispose();
        }
    }
    
    public static void openFavouritesFrame(){
        if(favouritesFrame != null){
            favouritesFrame.setVisible(true);
        }else{
            favouritesFrame = new FavouritesFrame();
            favouritesFrame.setVisible(true);
        }
    }
    
    public static void closeFavouritesFrame(){
        if(favouritesFrame != null){
            favouritesFrame.dispose();
        }
    }
    
    public static void openSettingsFrame(){
        if(settingsFrame != null){
            settingsFrame.setVisible(true);
        }else{
            settingsFrame = new SettingsFrame();
            settingsFrame.setVisible(true);
        }
    }
    
    public static void closeSettingsFrame(){
        if(settingsFrame != null){
            settingsFrame.dispose();
        }
    }
    
    public static void openManageGamesFrame(){
        if(manageGamesFrame != null){
            manageGamesFrame.setVisible(true);
        }else{
            manageGamesFrame = new ManageGamesFrame();
            manageGamesFrame.setVisible(true);
        }
    }
    
    public static void closeManageGamesFrame(){
        if(manageGamesFrame != null){
            settingsFrame.dispose();
        }
    }
    
    public static void openBugReportFrame(){
        if(bugReportFrame != null){
            bugReportFrame.setVisible(true);
        }else{
            bugReportFrame = new BugReportFrame();
            bugReportFrame.setVisible(true);
        }
    }
    
    public static void openBugReportFrame(Exception exception, String trafficLog){
        if(bugReportFrame != null){
            bugReportFrame.setVisible(true);
        }else{
            bugReportFrame = new BugReportFrame(exception, trafficLog);
            bugReportFrame.setVisible(true);
        }
    }
    
    public static void closeBugReportFrame(){
        if(bugReportFrame != null){
            closeTextPreviewFrame();
            bugReportFrame.dispose();
        }
    }
    
    public static void openTextPreviewFrame(String title, String text){
        if(textPreviewFrame != null){
            Point prevPosition = textPreviewFrame.getLocation();
            textPreviewFrame.dispose();
            textPreviewFrame = new TextPreviewFrame(title, text);
            textPreviewFrame.setLocation(prevPosition);
            textPreviewFrame.setVisible(true);
        }else{
            textPreviewFrame = new TextPreviewFrame(title, text);
            textPreviewFrame.setVisible(true);
        }
        
    }
    
    public static void closeTextPreviewFrame(){
        if(textPreviewFrame != null){
            textPreviewFrame.dispose();
        }
    }
}
