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

package coopnetclient.utils;

import coopnetclient.*;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.awt.Color;
import java.awt.event.KeyEvent;
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
import passwordencrypter.PasswordEncrypter;

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
 */
public class Settings {

    private static java.util.Properties data;    // Load the settings at first usage
    
	private static String optionsDir; //Gets set OS-Specific
	private static String favouritesFile; //Complete Path to the file
	private static String settingsFile;

    static {
        if(Globals.getOperatingSystem() == OperatingSystems.WINDOWS){
            optionsDir = System.getenv("APPDATA")+"/Coopnet";
            def_recievedest = GameDatabase.readRegistry("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\\Desktop");
        }else{
            optionsDir = System.getenv("HOME")+"/.coopnet";
            def_recievedest = System.getenv("HOME");
        }
		
        favouritesFile = optionsDir+"/favourites";
        settingsFile = optionsDir+"/settings";
		
        data = new java.util.Properties();
        load();
        favourites = new Vector<String>();
        loadFavourites();
    }
    
    /**********************************************************************/
    
    //Settings names in variable form
    private final static String 
            lastValidServerIP = "LastValidServerIP",
            lastValidServerPort = "LastValidServerPort",  
            recieveDest="FileDestination", 
            sleepEnabled="SleepModeEnabled" , 
            firstRun = "FirstRun",  
            homeChannel = "HomeChannel",
            autoLogin = "AutoLogin",  
            debugMode = "DebugMode", 
            selectedLookAndFeel = "SelectedLAF",
            useNativeLookAndFeel = "UseNativeLAF",
            bgColor = "BackgroundColor",  
            fgColor = "ForegroundColor",  
            yourUsernameColor = "YourUsernameColor",  
            selectionColor = "SelectionColor",  
            otherUsernamesColor = "OtherUsernamesColor",  
            systemMessageColor = "SystemMessageColor",  
            whisperMessageColor = "WhisperMessageColor",  
            nameStyle = "NameStyle",  
            nameSize = "NameSize",  
            messageStyle = "MessageStyle",  
            messageSize = "MessageSize",  
            colorizeBody = "ColorizeBody",  
            colorizeText = "ColorizeText",  
            lastLoginName = "LastLoginName",  
            lastLoginPassword = "Style",  
            userMessageColor = "UserMessageColor",  
            SoundEnabled = "SoundEnabled",  
            TimeStamps = "TimeStamps",  
            mainFrameMaximised = "MainFrameMaximised",  
            mainFrameWidth = "MainFrameWidth",  
            mainFrameHeight = "MainFrameHeight",  
            channelVerticalSPPosition = "ChannelVerticalSPPosition",  
            channelChatHorizontalSPPosition = "ChannelChatHorizontalSPPosition",  
            channelChatVerticalSPPosition = "ChannelChatVerticalSPPosition",
            wineCommand="WineCommand",
            fileTransferPort="FiletransferPort",
            quickPanelPostionisLeft = "QuickPanelPositionIsLeft",
            quickPanelDividerWidth = "QuckPanelDividerWidth",
            contactStatusChangeTextNotification = "ContactStatusChangeTextNotification",
            contactStatusChangeSoundNotification = "ContactStatusChangeSoundNotification",
            quickPanelToggleBarWidth = "QuickPanelToggleBarWidth",
            trayIconEnabled = "TrayIcon",
            launchHotKeyMask = "HotKeyMask",
            launchHotKey = "HotKey",
            multiChannel = "MultiChannel",
            showOfflineContacts = "ShowOfflineContacts",
            quickTabIconSizeIsBig="QuickTabIconSizeIsBig",
            captureDeviceIndex = "CaptureDeviceIndex",
            capturePortIndex = "CapturePortIndex",
            playbackDeviceIndex="PlaybackDeviceIndex",
            voiceChatPort= "VoiceChatPort",
            voiceChatIsVoiceActivated = "VoiceChatIsVoiceActivated",
            voiceSensitivity="VoiceSensitivity",
            pushToTalkHotKey = "PushToTalkHotKey",
            pushToTalkHotKeyMask = "PushToTalkHotKeyMask";;
   
    //Default
    private final static String def_lastValidServerIP = "subes.dyndns.org";
    private final static int def_lastValidServerPort = 6667;
    private final static int def_capturePortIndex = -1;
    private final static int def_captureDeviceIndex = -1;
    private final static int def_playbackDeviceIndex = -1;
    private final static int def_voiceChatPort = 2301;
    private final static int def_voiceSensitivity = 20;
    private final static boolean def_firstRun = true;
    private final static boolean def_voiceChatIsVoiceActivated = true;
    private final static boolean def_sleepEnabled = true;
    private final static boolean def_autoLogin = false;
    private final static boolean def_debugMode = false; // new Color(new Integer(""));
    private final static String def_selectedLookAndFeel = "Metal";
    private final static boolean def_useNativeLookAndFeel = true;
    private final static Color def_bgColor = new Color(240,240,240);
    private final static Color def_fgColor = Color.BLACK;
    private final static Color def_yourUsernameColor = new Color(255,153,0);
    private final static Color def_otherUsernamesColor = new Color(0,51,255);
    private final static Color def_systemMessageColor = new Color(200,0,0);
    private final static Color def_whisperMessageColor = new Color(0,153,204);
    private final static Color def_userMessageColor = Color.BLACK;
    private final static Color def_SelectionColor = new Color(200,200,200);
    private final static String def_nameStyle = "Monospaced";
    private final static String def_recievedest;
    private final static int def_nameSize = 12;
    private final static String def_messageStyle = "Monospaced";
    private final static String def_homeChannel = "Welcome";
    private final static int def_messageSize = 12;
    private final static boolean def_colorizeBody = false;
    private final static boolean def_colorizeText = true;
    private final static String def_lastLoginName = "";
    private final static String def_lastLoginPassword = "";
    private final static boolean def_soundEnabled = true;
    private final static boolean def_timeStamps = false;
    private final static boolean def_quickPanelPostionIsLeft = true;
    private final static int def_mainFrameMaximised = javax.swing.JFrame.NORMAL;
    private final static int def_mainFrameWidth = 465;
    private final static int def_mainFrameHeight = 400;
    private final static int def_channelVerticalSPPosition = 150;
    private final static int def_channelChatHorizontalSPPosition = 369;
    private final static int def_channelChatVerticalSPPosition = 135;
    private final static String def_wineComamnd = "wine";
    private final static int def_fileTransferPort = 2300;
    private final static int def_quickPanelDividerWidth = 5;
    private final static int def_quickPanelToggleBarWidth = 10;
    private final static boolean def_contactStatusChangeTextNotification = true;
    private final static boolean def_contactStatusChangeSoundNotification = true;
    private final static boolean def_trayIconEnabled = false;
    private static Vector<String> favourites;
    private final static int def_launchHotKeyMask = 10;
    private final static int def_launchHotKey = KeyEvent.VK_L;
    private final static int def_pushToTalkHotKey = KeyEvent.VK_F2;
    private final static int def_pushToTalkHotKeyMask = 0;
    private final static boolean def_multiChannel = true;
    private final static boolean def_showOfflineContacts = false;
    private final static boolean def_quickTabIconSize = true;

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

    public static int getVoiceSensitivity(){
        return readInteger(voiceSensitivity, def_voiceSensitivity);
    }
    
    public static void setVoiceSensitivity(int value){
        writeSetting(voiceSensitivity, String.valueOf(value));
    }
    
    public static boolean isVoiceActivated() {
         return readBoolean(voiceChatIsVoiceActivated, def_voiceChatIsVoiceActivated);
    }
    
    public static void setVoiceActivated(boolean bool) {
        writeSetting(voiceChatIsVoiceActivated, String.valueOf(bool));
    }
    
    public static int getVoiceChatPort() {
        return readInteger(voiceChatPort, def_voiceChatPort);
    }
    
    public static void setVoiceChatPort(int key) {
        writeSetting(voiceChatPort, String.valueOf(key));
    }
    
    public static int getCaptureDeviceIndex() {
        return readInteger(captureDeviceIndex, def_captureDeviceIndex);
    }
    
    public static void setCaptureDeviceIndex(int key) {
        writeSetting(captureDeviceIndex, String.valueOf(key));
    }
    
    public static int getCapturePortIndex() {
        return readInteger(capturePortIndex, def_capturePortIndex);
    }
    
    public static void setCapturePortIndex(int key) {
        writeSetting(capturePortIndex, String.valueOf(key));
    }
    
    public static int getPlaybackDeviceIndex() {
        return readInteger(playbackDeviceIndex, def_playbackDeviceIndex);
    }
    
    public static void setPlaybackDeviceIndex(int key) {
        writeSetting(playbackDeviceIndex, String.valueOf(key));
    }
    
    public static int getLaunchHotKey() {
        return readInteger(launchHotKey, def_launchHotKey);
    }
    
    public static void setLaunchHotKey(int key) {
        writeSetting(launchHotKey, String.valueOf(key));
    }

    public static int getPushToTalkHotKey() {
        return readInteger(pushToTalkHotKey, def_pushToTalkHotKey);
    }

    public static void setPushToTalkHotKey(int key) {
        writeSetting(pushToTalkHotKey, String.valueOf(key));
    }

    public static int getPushToTalkHotKeyMask() {
        return readInteger(pushToTalkHotKeyMask, def_pushToTalkHotKeyMask);
    }

    public static void setPushToTalkHotKeyMask(int key) {
        writeSetting(pushToTalkHotKeyMask, String.valueOf(key));
    }
    
    public static int getLaunchHotKeyMask() {
        return readInteger(launchHotKeyMask, def_launchHotKeyMask);
    }
    
    public static void setLaunchHotKeymask(int keyMask) {
        writeSetting(launchHotKeyMask, String.valueOf(keyMask));
    }
    
    public static boolean getTrayIconEnabled() {
        return readBoolean(trayIconEnabled, def_trayIconEnabled);
    }

    public static void setTrayIconEnabled(boolean status) {
        writeSetting(trayIconEnabled, String.valueOf(status));
    }
            
    public static boolean getQuickPanelPostionisLeft() {
        return readBoolean(quickPanelPostionisLeft, def_quickPanelPostionIsLeft);
    }

    public static void setQuickPanelPostionisLeft(boolean status) {
        writeSetting(quickPanelPostionisLeft, String.valueOf(status));
    }
    
    
    public static int getQuickPanelToggleBarWidth() {
        return readInteger(quickPanelToggleBarWidth, def_quickPanelToggleBarWidth);
    }
    
    public static void setQuickPanelToggleBarWidth(int width) {
        writeSetting(quickPanelToggleBarWidth, String.valueOf(width));
    }
            
    public static int getQuickPanelDividerWidth() {
        return readInteger(quickPanelDividerWidth, def_quickPanelDividerWidth);
    }
    
    public static void setQuickPanelDividerWidth(int width) {
        writeSetting(quickPanelDividerWidth, String.valueOf(width));
    }
    
    public static boolean getContactStatusChangeTextNotification() {
        return readBoolean(contactStatusChangeTextNotification, def_contactStatusChangeTextNotification);
    }

    public static void setContactStatusChangeTextNotification(boolean status) {
        writeSetting(contactStatusChangeTextNotification, String.valueOf(status));
    }
    
    public static boolean getContactStatusChangeSoundNotification() {
        return readBoolean(contactStatusChangeSoundNotification, def_contactStatusChangeSoundNotification);
    }

    public static void setContactStatusChangeSoundNotification(boolean status) {
        writeSetting(contactStatusChangeSoundNotification, String.valueOf(status));
    }
    
    /**
     *  public getters and setters used by other classes
     */
    public static boolean getFirstRun() {
        return readBoolean(firstRun, def_firstRun);
    }

    public static void setFirstRun(boolean status) {
        writeSetting(firstRun, String.valueOf(status));
    }
    
    public static boolean getSleepEnabled() {
        return readBoolean(sleepEnabled,def_sleepEnabled );
    }

    public static void setSleepenabled(boolean enabled) {
        writeSetting(sleepEnabled, String.valueOf(enabled));
    }

    public static int getMainFrameMaximised() {
        return readInteger(mainFrameMaximised, def_mainFrameMaximised);
    }

    public static void setMainFrameMaximised(int status) {
        writeSetting(mainFrameMaximised, String.valueOf(status));
    }

    public static int getMainFrameWidth() {
        return readInteger(mainFrameWidth, def_mainFrameWidth);
    }

    public static void setMainFrameWidth(int width) {
        writeSetting(mainFrameWidth, String.valueOf(width));
    }

    public static int getMainFrameHeight() {
        return readInteger(mainFrameHeight, def_mainFrameHeight);
    }

    public static void setMainFrameHeight(int height) {
        writeSetting(mainFrameWidth, String.valueOf(height));
    }

    public static int getChannelVerticalSPPosition() {
        return readInteger(channelVerticalSPPosition, def_channelVerticalSPPosition);
    }

    public static void setChannelVerticalSPPosition(int position) {
        writeSetting(channelVerticalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatHorizontalSPPosition() {
        return readInteger(channelChatHorizontalSPPosition, def_channelChatHorizontalSPPosition);
    }

    public static void setChannelChatHorizontalSPPosition(int position) {
        writeSetting(channelChatHorizontalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatVerticalSPPosition() {
        return readInteger(channelChatVerticalSPPosition, def_channelChatVerticalSPPosition);
    }

    public static void setChannelChatVerticalSPPosition(int position) {
        writeSetting(channelChatVerticalSPPosition, String.valueOf(position));
    }

    //sound options
    public static boolean getSoundEnabled() {
        return readBoolean(SoundEnabled, def_soundEnabled);
    }

    public static void setSoundEnabled(boolean bool) {
        writeSetting(SoundEnabled, String.valueOf(bool));
    }

    //timestamps
    public static boolean getTimeStampEnabled() {
        return readBoolean(TimeStamps, def_timeStamps);
    }

    public static void setTimeStampEnabled(boolean bool) {
        writeSetting(TimeStamps, String.valueOf(bool));
    }

    //serverIp
    public static String getLastValidServerIP() {
        return readString(lastValidServerIP, def_lastValidServerIP);
    }

    public static void setLastValidServerIP(String ip) {
        writeSetting(lastValidServerIP, ip);
    }

    //serverPort
    public static int getLastValidServerPort() {
        return readInteger(lastValidServerPort, def_lastValidServerPort);
    }

    public static void setLastValidServerPort(int port) {
        writeSetting(lastValidServerPort, String.valueOf(port));
    }
     
      public static int getFiletTansferPort() {
        return readInteger(fileTransferPort, def_fileTransferPort);
    }

    public static void setFiletTansferPort(int port) {
        writeSetting(fileTransferPort, String.valueOf(port));
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
    
    //selectedLookAndFeel
    public static String getSelectedLookAndFeel(){
        return readString(selectedLookAndFeel, def_selectedLookAndFeel);
    }
    
    public static void setSelectedLookAndFeel(String string){
        writeSetting(selectedLookAndFeel, string);
    }
    
    //useNativeLookAndFeel
    public static boolean getUseNativeLookAndFeel(){
        return readBoolean(useNativeLookAndFeel, def_useNativeLookAndFeel);
    }
    
    public static void setUseNativeLookAndFeel(boolean bool){
        writeSetting(useNativeLookAndFeel, String.valueOf(bool));
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

    //recieved file destination
     public static String getRecieveDestination() {
        return readString(recieveDest, def_recievedest);
    }

    public static void setRecieveDestination(String path) {
        writeSetting(recieveDest, path);
    }
    
    public static String getWineCommand() {
        return readString(wineCommand, def_wineComamnd);
    }

    public static void setWineCommand(String path) {
        writeSetting(wineCommand, path);
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
        return PasswordEncrypter.decodePassword(readString(lastLoginPassword, def_lastLoginPassword));
    }

    public static void setLastLoginPassword(String pw) {
        writeSetting(lastLoginPassword, PasswordEncrypter.encodePassword(PasswordEncrypter.encryptPassword(pw)));
    }
    
    public static void setMultiChannel(boolean enabled){
        writeSetting(multiChannel, String.valueOf(enabled));
    }
    
    public static boolean getMultiChannel(){
        return readBoolean(multiChannel, def_multiChannel);
    }
    
    public static boolean isquickTabIconSizeBig() {
        return readBoolean(quickTabIconSizeIsBig, def_quickTabIconSize);
    }

    public static void setIsquickTabIconSizeBig(boolean bool) {
        writeSetting(quickTabIconSizeIsBig, String.valueOf(bool));
    }
    
    public static void setShowOfflineContacts(boolean enabled){
        boolean refreshContacts = Settings.getShowOfflineContacts() == false && enabled == true;
        
        writeSetting(showOfflineContacts, String.valueOf(enabled));
        
        if(refreshContacts){
            Protocol.refreshContacts();
        }else{
            Globals.getContactList().updateShowOfflineContacts();
        }
        
    }
    
    public static boolean getShowOfflineContacts(){
        return readBoolean(showOfflineContacts, def_showOfflineContacts);
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
            pw = new PrintWriter(new FileWriter(favouritesFile));
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
            br = new BufferedReader(new FileReader(favouritesFile));
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

    
}
