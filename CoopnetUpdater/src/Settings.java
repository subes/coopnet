/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/



import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * stores/loads the clients settings
 * 
 *  - Variables for any setting name and default value
 *  - everything static only!
 *  - getters and setters for each variable
 *  - getters and setters automatically cast to the expected type
 *  - getters load default value, if error occurs and save the default value to restore file integrity
 *  - each setter saves the file // maybe change that behaviour for performance tuning
 *
 *  this ensures easy integration of new settings or easy change of default values / file entry names
 *
 *  How to add new settings:
 *      - add entries to private fields (entry name and default value)
 *      - write setter and getter by using the read<TYPE>() and writeSetting() functions
 *
 *  TODO:
 *      set good default values, i forgot the right values
 *
 */
public class Settings {

    private static java.util.Properties data;
    
    private static String optionsDir; //Gets set OS-Specific
	private static String settingsFile;

    static {
        //detect OS
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            optionsDir = System.getenv("APPDATA")+"/CoopNet";
        } else {
            optionsDir = System.getenv("HOME")+"/.coopnet";
        }
		
		settingsFile = optionsDir+"/settings";
		
        data = new java.util.Properties();
        load();
        favourites = new Vector<String>();
        loadFavourites();
    }
    
    //Settings names in variable form
    private final static String serverIp = "ServerIp",firstrun="FirstRun" , homeChannel = "HomeChannel",  serverPort = "ServerPort",updateURL="UpdateURL" ,  autoLogin = "AutoLogin",  debugMode = "DebugMode",  bgColor = "BackgroundColor",  fgColor = "ForegroundColor",  yourUsernameColor = "YourUsernameColor",  selectionColor = "SelectionColor",  otherUsernamesColor = "OtherUsernamesColor",  systemMessageColor = "SystemMessageColor",  whisperMessageColor = "WhisperMessageColor",  nameStyle = "NameStyle",  nameSize = "NameSize",  messageStyle = "MessageStyle",  messageSize = "MessageSize",  colorizeBody = "ColorizeBody",  colorizeText = "ColorizeText",  lastLoginName = "LastLoginName",  lastLoginPassword = "Color",  userMessageColor = "UserMessageColor",  SoundEnabled = "SoundEnabled",  TimeStamps = "TimeStamps",  mainFrameMaximised = "MainFrameMaximised",  mainFrameWidth = "MainFrameWidth",  mainFrameHeight = "MainFrameHeight",  channelVerticalSPPosition = "ChannelVerticalSPPosition",  channelChatHorizontalSPPosition = "ChannelChatHorizontalSPPosition",  channelChatVerticalSPPosition = "ChannelChatVerticalSPPosition";
    //Default values for settings
    private final static String def_serverIp = "subes.dyndns.org";
    private final static int def_serverPort = 6667;
     private final static String def_updateURL = "http://coopnet.sourceforge.net/latest.php";
    private final static boolean def_firstrun=true;
    private final static boolean def_autoLogin = false;
    private final static boolean def_debugMode = false; // new Color(new Integer(""));
    private final static Color def_bgColor = new Color(new Integer("-16777216"));
    private final static Color def_fgColor = new Color(new Integer("-16711885"));
    private final static Color def_yourUsernameColor = new Color(new Integer("-3355648"));
    private final static Color def_otherUsernamesColor = new Color(new Integer("-3355648"));
    private final static Color def_systemMessageColor = new Color(new Integer("-65536"));
    private final static Color def_whisperMessageColor = new Color(new Integer("-16711732"));
    private final static Color def_userMessageColor = new Color(new Integer("-16711885"));
    private final static Color def_SelectionColor = new Color(new Integer("-16751053"));
    private final static String def_nameStyle = "Monospaced";
    private final static int def_nameSize = 12;
    private final static String def_messageStyle = "Monospaced";
    private final static String def_homeChannel = "Fallout Tactics";
    private final static int def_messageSize = 12;
    private final static boolean def_colorizeBody = false;
    private final static boolean def_colorizeText = false;
    private final static String def_lastLoginName = "";
    private final static String def_lastLoginPassword = "";
    private final static boolean def_SoundEnabled = true;
    private final static boolean def_TimeStamps = false;
    private final static int def_MainFrameMaximised = javax.swing.JFrame.NORMAL;
    private final static int def_MainFrameWidth = 465;
    private final static int def_MainFrameHeight = 400;
    private final static int def_ChannelVerticalSPPosition = 150;
    private final static int def_ChannelChatHorizontalSPPosition = 369;
    private final static int def_ChannelChatVerticalSPPosition = 135;
    private static Vector<String> favourites;

    /**
     * store the settings in options file
     */
    private static void save() {
        try {
            data.store(new FileOutputStream(settingsFile), "Coopnet settings");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load the settings from the options file, or create one with default values if it doesn exist
     */
    private static void load() {
        try {
            if (!new File(optionsDir).exists()) {
                new File(optionsDir).mkdir();
            }
            data.load(new FileInputStream(settingsFile));
        } catch (Exception ex) {
        //settings will be restored to default when they cant be read via a getter

        }
    }

    /**
     * settings readers used by the real getters
     */
    //Generic getter for Strings. Private, coz used by the real getters
    private static String readString(String entry, String defaultValue) {
        String ret = data.getProperty(entry);

        if (ret == null) {
            //reset setting to default value
            writeSetting(entry, defaultValue);
            ret = defaultValue;
        }

        return ret;
    }

    //Generic getter for integer. Private, coz used by the real getters
    private static int readInteger(String entry, int defaultValue) {
        boolean error = false;

        int ret = 0;
        String get = data.getProperty(entry);

        if (get != null) {
            try {
                ret = Integer.parseInt(get);
            } catch (NumberFormatException e) {
                error = true;
            }
        }

        if (error || get == null) {
            //reset setting to default value
            writeSetting(entry, String.valueOf(defaultValue));
            return defaultValue;
        }

        return ret;
    }

    //Generic getter for boolean. Private, coz used by the real getters
    private static boolean readBoolean(String entry, boolean defaultValue) {
        boolean error = false;

        boolean ret = false;
        String get = data.getProperty(entry);

        if (get != null) {
            //Specially testing for "true" and "false", to reset value if garbage was found
            if (get.equalsIgnoreCase("true")) {
                ret = true;
            } else if (get.equalsIgnoreCase("false")) {
                ret = false;
            } else {
                //Garbage found
                error = true;
            }
        }

        if (error || get == null) {
            //reset setting to default value
            writeSetting(entry, String.valueOf(defaultValue));
            return defaultValue;
        }

        return ret;
    }

    //Generic getter for Color. Private, coz used by the real getters
    private static Color readColor(String entry, Color defaultValue) {
        boolean error = false;

        Color ret = null;
        try {
            ret = new Color(Integer.parseInt(data.getProperty(entry)));
        } catch (Exception e) {
            error = true;
        }

        if (error || ret == null) {
            //reset setting to default value
            ret = defaultValue;
            writeSetting(entry, String.valueOf(defaultValue.getRGB()));
        }

        return ret;
    }

    /**
     * settings writer used by the real setters
     */
    private static void writeSetting(String entry, String value) {
        //using this one, so everytime the settings get saved on a change
        data.setProperty(entry, value);
        save();
    }

    /**
     *  public getters and setters used by other classes
     */
    public static boolean getFirstRun() {
        return readBoolean(firstrun, def_firstrun);
    }

    public static void setFirstRun(boolean status) {
        writeSetting(firstrun, String.valueOf(status));
    }
    
    public static int getMainFrameMaximised() {
        return readInteger(mainFrameMaximised, def_MainFrameMaximised);
    }

    public static void setMainFrameMaximised(int status) {
        writeSetting(mainFrameMaximised, String.valueOf(status));
    }

    public static int getMainFrameWidth() {
        return readInteger(mainFrameWidth, def_MainFrameWidth);
    }

    public static void setMainFrameWidth(int width) {
        writeSetting(mainFrameWidth, String.valueOf(width));
    }

    public static int getMainFrameHeight() {
        return readInteger(mainFrameHeight, def_MainFrameHeight);
    }

    public static void setMainFrameHeight(int height) {
        writeSetting(mainFrameWidth, String.valueOf(height));
    }

    public static int getChannelVerticalSPPosition() {
        return readInteger(channelVerticalSPPosition, def_ChannelVerticalSPPosition);
    }

    public static void setChannelVerticalSPPosition(int position) {
        writeSetting(channelVerticalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatHorizontalSPPosition() {
        return readInteger(channelChatHorizontalSPPosition, def_ChannelChatHorizontalSPPosition);
    }

    public static void setChannelChatHorizontalSPPosition(int position) {
        writeSetting(channelChatHorizontalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatVerticalSPPosition() {
        return readInteger(channelChatVerticalSPPosition, def_ChannelChatVerticalSPPosition);
    }

    public static void setChannelChatVerticalSPPosition(int position) {
        writeSetting(channelChatVerticalSPPosition, String.valueOf(position));
    }

    //sound options
    public static boolean getSoundEnabled() {
        return readBoolean(SoundEnabled, def_SoundEnabled);
    }

    public static void setSoundEnabled(boolean bool) {
        writeSetting(SoundEnabled, String.valueOf(bool));
    }

    //timestamps
    public static boolean getTimeStampEnabled() {
        return readBoolean(TimeStamps, def_TimeStamps);
    }

    public static void setTimeStampEnabled(boolean bool) {
        writeSetting(TimeStamps, String.valueOf(bool));
    }

    //serverIp
    public static String getServerIp() {
        return readString(serverIp, def_serverIp);
    }

    public static void setServerIp(String ip) {
        writeSetting(serverIp, ip);
    }

    //serverPort
    public static int getServerPort() {
        return readInteger(serverPort, def_serverPort);
    }

    public static void setServerPort(int port) {
        writeSetting(serverPort, String.valueOf(port));
    }
    //
    public static String getupdateURL() {
        return readString(updateURL, def_updateURL);
    }

    public static void setupdateURL(String url) {
        writeSetting(updateURL, url);
    }
    

    //autoLogin
    public static boolean getAutoLogin() {
        return readBoolean(autoLogin, def_autoLogin);
    }

    public static void setAutoLogin(boolean bool) {
        writeSetting(autoLogin, String.valueOf(bool));
    }

    public static String getHomeChannel() {
        return readString(homeChannel, def_homeChannel);
    }

    public static void setHomeChannel(String channel) {
        writeSetting(homeChannel, channel);
    }

    //debugMode
    public static boolean getDebugMode() {
        return readBoolean(debugMode, def_debugMode);
    }

    public static void setDebugMode(boolean bool) {
        writeSetting(debugMode, String.valueOf(bool));
    }

    //bgColor
    public static Color getBackgroundColor() {
        return readColor(bgColor, def_bgColor);
    }

    public static void setBackgroundColor(Color color) {
        writeSetting(bgColor, String.valueOf(color.getRGB()));
    }

    //selection color
    public static Color getSelectionColor() {
        return readColor(selectionColor, def_SelectionColor);
    }

    public static void setSelectionColor(Color color) {
        writeSetting(selectionColor, String.valueOf(color.getRGB()));
    }

    //fgColor
    public static Color getForegroundColor() {
        return readColor(fgColor, def_fgColor);
    }

    public static void setForegroundColor(Color color) {
        writeSetting(fgColor, String.valueOf(color.getRGB()));
    }

    //yourUsernameColor
    public static Color getYourUsernameColor() {
        return readColor(yourUsernameColor, def_yourUsernameColor);
    }

    public static void setYourUsernameColor(Color color) {
        writeSetting(yourUsernameColor, String.valueOf(color.getRGB()));
    }

    //otherUsernamesColor
    public static Color getOtherUsernamesColor() {
        return readColor(otherUsernamesColor, def_otherUsernamesColor);
    }

    public static void setOtherUsernamesColor(Color color) {
        writeSetting(otherUsernamesColor, String.valueOf(color.getRGB()));
    }

    //systemMessageColor
    public static Color getSystemMessageColor() {
        return readColor(systemMessageColor, def_systemMessageColor);
    }

    public static void setSystemMessageColor(Color color) {
        writeSetting(systemMessageColor, String.valueOf(color.getRGB()));
    }

    //whisperMessageColor
    public static Color getWhisperMessageColor() {
        return readColor(whisperMessageColor, def_whisperMessageColor);
    }

    public static void setWhisperMessageColor(Color color) {
        writeSetting(whisperMessageColor, String.valueOf(color.getRGB()));
    }

    //nameStyle
    public static String getNameStyle() {
        return readString(nameStyle, def_nameStyle);
    }

    public static void setNameStyle(String style) {
        writeSetting(nameStyle, style);
    }

    //nameSize
    public static int getNameSize() {
        return readInteger(nameSize, def_nameSize);
    }

    public static void setNameSize(int size) {
        writeSetting(nameSize, String.valueOf(size));
    }

    //messageStyle
    public static String getMessageStyle() {
        return readString(messageStyle, def_messageStyle);
    }

    public static void setMessageStyle(String style) {
        writeSetting(messageStyle, style);
    }

    //messageSize
    public static int getMessageSize() {
        return readInteger(messageSize, def_messageSize);
    }

    public static void setMessageSize(int size) {
        writeSetting(messageSize, String.valueOf(size));
    }
    //messagecolor
    public static Color getUserMessageColor() {
        return readColor(userMessageColor, def_userMessageColor);
    }

    public static void setUserMessageColor(Color color) {
        writeSetting(userMessageColor, String.valueOf(color.getRGB()));
    }

    //colorizeBody
    public static boolean getColorizeBody() {
        return readBoolean(colorizeBody, def_colorizeBody);
    }

    public static void setColorizeBody(boolean bool) {
        writeSetting(colorizeBody, String.valueOf(bool));
    }

    //colorizeText
    public static boolean getColorizeText() {
        return readBoolean(colorizeText, def_colorizeText);
    }

    public static void setColorizeText(boolean bool) {
        writeSetting(colorizeText, String.valueOf(bool));
    }

    //lastLoginName
    public static String getLastLoginName() {
        return readString(lastLoginName, def_lastLoginName);
    }

    public static void setLastLoginName(String name) {
        writeSetting(lastLoginName, name);
    }

    //lastLoginPassword
    public static String getLastLoginPassword() {
        return decodePassword(readString(lastLoginPassword, def_lastLoginPassword));
    }

    public static void setLastLoginPassword(String pw) {
        writeSetting(lastLoginPassword, encodePassword(pw));
    }

    public static Color getTitledBorderColor() {
        if (readBoolean(colorizeBody, def_colorizeBody)) {
            System.out.println("coloring titleborders fgcolor");
            return getForegroundColor();
        } else {
            return Color.BLACK;
        }
    }

    public static void addFavourite(String channel) {
        if (!favourites.contains(channel)) {
            favourites.add(channel);
            saveFavourites();
        }
    }

    public static Vector<String> getFavourites() {
        return favourites;
    }

    public static void removeFavourite(String channel) {
        favourites.remove(channel);
        saveFavourites();
    }

    private static void saveFavourites() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("options/favourites"));
        } catch (Exception ex) {
        }
        for (String s : favourites) {
            pw.println(s);
        }
        pw.close();
    }

    public static void loadFavourites() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("options/favourites"));
        } catch (FileNotFoundException ex) {
            return;
        }
        favourites.clear();
        //reading data
        Boolean done = false;
        String input;
        while (!done) {
            try {
                input = br.readLine();
                if (input == null) {
                    done = true;
                    continue;
                }
            } catch (IOException ex) {
                return;
            }
            favourites.add(input);
        }
        try {
            br.close();
        } catch (Exception e) {
        }
    }

    /**
     * encodes the password
     */
    private static String encodePassword(String input) {
        String tmp = "";
        for (char c : input.toCharArray()) {

            if (c == '\n') {
                break;
            }
            int i = c;
            i += 1000;
            char ch = (char) i;
            tmp += ch;

        }

        return tmp;
    }

    /**
     * decodes the password
     */
    private static String decodePassword(String input) {
        String tmp = "";

        for (char c : input.toCharArray()) {

            if (c == '\n') {
                break;
            }
            int i = c;
            i -= 1000;
            char ch = (char) i;
            tmp += ch;

        }
        return tmp;
    }
}
