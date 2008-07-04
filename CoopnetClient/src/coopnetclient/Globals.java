package coopnetclient;

import coopnetclient.frames.ChangePasswordFrame;
import coopnetclient.frames.ChannelListFrame;
import coopnetclient.frames.GameSettingsFrame;
import coopnetclient.modules.Settings;
import coopnetclient.frames.clientframe.ClientFrame;
import coopnetclient.frames.clientframe.RoomPanel;
import coopnetclient.launchers.Launcher;
import javax.swing.JFrame;

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
    private static RoomPanel roomPanel;
    private static JFrame profileFrame; //TODO frames handle properly
    private static ChangePasswordFrame changePasswordFrame;
    private static ChannelListFrame channelListFrame;
    private static GameSettingsFrame gameSettingsFrame;
    private static JFrame roomCreationFrame;
    
    
    static {
        //Detect OS
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            operatingSystem = OS_WINDOWS;
            lastOpenedDir = ".";
        } else {
            operatingSystem = OS_LINUX;
            lastOpenedDir = System.getenv("HOME");
        }
        
        //Set debug
        debug = Settings.getDebugMode();
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
        sleepModeStatus = true;
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
    
    public static void createClientFrame(){
        if(clientFrame == null){
            clientFrame = new ClientFrame();
        }else{
            if(getDebug()){
                System.out.println("[WARNING]\tClientFrame is supposed to be created only once!");
            }
        }
    }
    
    public static ClientFrame getClientFrame(){
        return clientFrame;
    }
    
    public static void createRoomPanel(boolean isHost, String channel, String modindex, String ip, boolean compatible, String hamachiIp, int maxPlayers){
        if(roomPanel == null){
            roomPanel = new RoomPanel(isHost, channel, modindex, ip, compatible, hamachiIp, maxPlayers);
            clientFrame.addRoomPanelTab();
        }else{
            if(getDebug()){
                System.out.println("[WARNING]\tClose the current RoomPanel before opening a new one!");
            }
        }
    }
    
    public static void removeRoomPanel(){
        if(roomPanel != null){
            Globals.getLauncher().stop();
            clientFrame.removeRoomPanelTab();
            roomPanel = null;
        }
    }
    
    public static RoomPanel getRoomPanel(){
        return roomPanel;
    }
    
    public static void setProfileFrame(JFrame value){
        profileFrame = value;
    }
    
    public static JFrame getProfileFrame(){
        return profileFrame;
    }
    
    public static void setChangePasswordFrame(ChangePasswordFrame value){
        changePasswordFrame = value;
    }
    
    public static ChangePasswordFrame getChangePasswordFrame(){
        return changePasswordFrame;
    }
    
    public static void setChannelListFrame(ChannelListFrame value){
        channelListFrame = value;
    }
    
    public static ChannelListFrame getChannelListFrame(){
        return channelListFrame;
    }
    
    public static void setGameSettingsFrame(GameSettingsFrame value){
        gameSettingsFrame = value;
    }
    
    public static GameSettingsFrame getGameSettingsFrame(){
        return gameSettingsFrame;
    }
    
    public static void setRoomCreationFrame(JFrame value){
        roomCreationFrame = value;
    }
    
    public static JFrame getRoomCreationFrame(){
        return roomCreationFrame;
    }
}
