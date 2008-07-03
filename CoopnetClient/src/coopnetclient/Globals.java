package coopnetclient;

import coopnetclient.frames.clientframe.ClientFrame;
import coopnetclient.frames.clientframe.RoomPanel;
import coopnetclient.launchers.Launcher;
import javax.swing.JFrame;

public class Globals {

    public static boolean debug;
    public static final String clientVersion = "0.96.1";
    public static boolean loggedIn = false;
    public static ClientFrame clientFrame;
    public static RoomPanel currentRoomPanel;
    public static JFrame profileFrame = null;
    public static JFrame changePasswordFrame = null;
    public static JFrame channelListFrame = null;
    public static JFrame gameSettingsFrame = null;
    public static JFrame roomCreationFrame = null;
    public static String thisPlayer_loginName;
    public static String thisPlayer_inGameName;
    public static String os;
    public static boolean registryOK = false;
    public static boolean sleepMode = false;
    public static boolean isPlaying = false;
    public static Launcher launcher;
    public static String lastOpenedDir;
    
    static {
        //Detect OS
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            System.out.println("windows detected");
            os = "windows";
            lastOpenedDir = ".";
        } else {
            System.out.println("linux detected");
            os = "linux";
            lastOpenedDir = System.getenv("HOME");
        }
        
        //Set debug
        debug = Settings.getDebugMode();
    }
    
}
