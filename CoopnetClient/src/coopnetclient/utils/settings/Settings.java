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
package coopnetclient.utils.settings;

import coopnetclient.Globals;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.RegistryReader;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.awt.Color;
import java.awt.event.KeyEvent;
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
 *      - write setter and getter by using the read<TYPE>() and SettingsHelper.writeSetting() functions
 *
 */
public final class Settings {

    private final static String lastValidServerIP = "LastValidServerIP";
    private final static String def_lastValidServerIP = "80.190.240.58";
    private final static String lastValidServerPort = "LastValidServerPort";
    private final static int def_lastValidServerPort = 6667;
    private final static String recieveDest = "FileDestination";
    private static String def_recievedest;
    private final static String sleepEnabled = "SleepModeEnabled";
    private final static boolean def_sleepEnabled = true;
    private final static String firstRun = "FirstRun";
    private final static boolean def_firstRun = true;
    private final static String homeChannel = "HomeChannel";
    private final static String def_homeChannel = "Welcome";
    private final static String autoLogin = "AutoLogin";
    private final static boolean def_autoLogin = false;
    private final static String debugMode = "DebugMode";
    private final static boolean def_debugMode = false;
    private final static String selectedLookAndFeel = "SelectedLAF";
    private final static String def_selectedLookAndFeel = "Metal";
    private final static String useNativeLookAndFeel = "UseNativeLAF";
    private final static boolean def_useNativeLookAndFeel = true;
    private final static String bgColor = "BackgroundColor";
    private final static Color def_bgColor = new Color(240, 240, 240);
    private final static String fgColor = "ForegroundColor";
    private final static Color def_fgColor = Color.BLACK;
    private final static String yourUsernameColor = "YourUsernameColor";
    private final static Color def_UsernameColor = new Color(255, 153, 0);
    private final static String selectionColor = "SelectionColor";
    private final static Color def_SelectionColor = new Color(200, 200, 200);
    private final static String otherUsernamesColor = "OtherUsernamesColor";
    private final static Color def_otherUsernamesColor = new Color(0, 51, 255);
    private final static String friendUsernameColor = "FriendUsernameColor";
    private final static Color def_friendUsernameColor = Color.GREEN.darker();
    private final static String systemMessageColor = "SystemMessageColor";
    private final static Color def_systemMessageColor = new Color(200, 0, 0);
    private final static String whisperMessageColor = "WhisperMessageColor";
    private final static Color def_whisperMessageColor = new Color(0, 153, 204);
    private final static String friendMessageColor = "FriendMessageColor";
    private final static Color def_friendMessageColor = Color.GREEN.darker();
    private final static String nameStyle = "NameStyle";
    private final static String def_nameStyle = "Monospaced";
    private final static String nameSize = "NameSize";
    private final static int def_nameSize = 12;
    private final static String messageStyle = "MessageStyle";
    private final static String def_messageStyle = "Monospaced";
    private final static String messageSize = "MessageSize";
    private final static int def_messageSize = 12;
    private final static String colorizeBody = "ColorizeBody";
    private final static boolean def_colorizeBody = false;
    private final static String colorizeText = "ColorizeText";
    private final static boolean def_colorizeText = true;
    private final static String lastLoginName = "LastLoginName";
    private final static String def_lastLoginName = "";
    private final static String lastLoginPassword = "Style";
    private final static String def_lastLoginPassword = "";
    private final static String userMessageColor = "UserMessageColor";
    private final static Color def_userMessageColor = Color.BLACK;
    private final static String SoundEnabled = "SoundEnabled";
    private final static boolean def_soundEnabled = true;
    private final static String TimeStamps = "TimeStamps";
    private final static boolean def_timeStamps = false;
    private final static String mainFrameMaximised = "MainFrameMaximised";
    private final static int def_mainFrameMaximised = javax.swing.JFrame.NORMAL;
    private final static String mainFrameWidth = "MainFrameWidth";
    private final static int def_mainFrameWidth = 600;
    private final static String mainFrameHeight = "MainFrameHeight";
    private final static int def_mainFrameHeight = 400;
    private final static String channelVerticalSPPosition = "ChannelVerticalSPPosition";
    private final static int def_channelVerticalSPPosition = 150;
    private final static String channelChatHorizontalSPPosition = "ChannelChatHorizontalSPPosition";
    private final static int def_channelChatHorizontalSPPosition = 369;
    private final static String channelChatVerticalSPPosition = "ChannelChatVerticalSPPosition";
    private final static int def_channelChatVerticalSPPosition = 135;
    private final static String wineCommand = "WineCommand";
    private final static String def_wineComamnd = "wine";
    private final static String fileTransferPort = "FiletransferPort";
    private final static int def_fileTransferPort = 2300;
    private final static String quickPanelPostionIsLeft = "QuickPanelPositionIsLeft";
    private final static boolean def_quickPanelPostionIsLeft = true;
    private final static String quickPanelDividerWidth = "QuckPanelDividerWidth";
    private final static int def_quickPanelDividerWidth = 5;
    private final static String contactStatusChangeTextNotification = "ContactStatusChangeTextNotification";
    private final static boolean def_contactStatusChangeTextNotification = true;
    private final static String contactStatusChangeSoundNotification = "ContactStatusChangeSoundNotification";
    private final static boolean def_contactStatusChangeSoundNotification = true;
    private final static String quickPanelToggleBarWidth = "QuickPanelToggleBarWidth";
    private final static int def_quickPanelToggleBarWidth = 10;
    private final static String trayIconEnabled = "TrayIcon";
    private final static boolean def_trayIconEnabled = false;
    private final static String launchHotKeyMask = "HotKeyMask";
    private final static int def_launchHotKeyMask = 10;
    private final static String launchHotKey = "HotKey";
    private final static int def_launchHotKey = KeyEvent.VK_L;
    private final static String multiChannel = "MultiChannel";
    private final static boolean def_multiChannel = true;
    private final static String showOfflineContacts = "ShowOfflineContacts";
    private final static boolean def_showOfflineContacts = false;
    private final static String quickPanelIconSizeIsBig = "QuickPanelIconSizeIsBig";
    private final static boolean def_quickPanelIconSizeIsBig = true;
    private final static String rememberMainFrameSize = "RememberMainFrameSize";
    private final static boolean def_rememberMainFrameSize = false;
    private final static String logUserActivity = "LogUserActicvity";
    private final static boolean def_logUserActivity = true;
    private final static String DOSBoxExecutable = "DOSBox-Executable";
    private final static String def_DOSEmulatorExecutable = "";
    private final static String DOSBoxFullscreen = "DOSBox-Fullscreen";
    private final static boolean def_DOSBoxFullscreen = false;
    private final static String SHOW_MINIMIZE_TO_TRAY_HINT = "ShowMinimizeToTrayHint";
    private final static boolean DEF_SHOW_MINIMIZE_TO_TRAY_HINT = true;

    private Settings() {
    }

    public static void init() {
        if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS) {
            String recvdir = RegistryReader.read(
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows" +
                    "\\CurrentVersion\\Explorer\\Shell Folders\\Desktop");
            if (recvdir == null) {
                recvdir = System.getenv("HOMEPATH");
            }

            def_recievedest = recvdir;
        } else {
            final String home = System.getenv("HOME");
            def_recievedest = home;
        }
    }

    public static int getLaunchHotKey() {
        return SettingsHelper.readInteger(launchHotKey, def_launchHotKey);
    }

    public static void setLaunchHotKey(int key) {
        SettingsHelper.writeSetting(launchHotKey, String.valueOf(key));
    }

    public static int getLaunchHotKeyMask() {
        return SettingsHelper.readInteger(launchHotKeyMask, def_launchHotKeyMask);
    }

    public static void setLaunchHotKeymask(int keyMask) {
        SettingsHelper.writeSetting(launchHotKeyMask, String.valueOf(keyMask));
    }

    public static boolean getTrayIconEnabled() {
        return SettingsHelper.readBoolean(trayIconEnabled, def_trayIconEnabled);
    }

    public static void setTrayIconEnabled(boolean status) {
        SettingsHelper.writeSetting(trayIconEnabled, String.valueOf(status));
    }

    public static boolean getQuickPanelPostionisLeft() {
        return SettingsHelper.readBoolean(quickPanelPostionIsLeft, def_quickPanelPostionIsLeft);
    }

    public static void setQuickPanelPostionisLeft(boolean status) {
        SettingsHelper.writeSetting(quickPanelPostionIsLeft, String.valueOf(status));
    }

    public static boolean getLogUserActivity() {
        return SettingsHelper.readBoolean(logUserActivity, def_logUserActivity);
    }

    public static void setLogUserActivity(boolean status) {
        SettingsHelper.writeSetting(logUserActivity, String.valueOf(status));
    }

    public static int getQuickPanelToggleBarWidth() {
        return SettingsHelper.readInteger(quickPanelToggleBarWidth, def_quickPanelToggleBarWidth);
    }

    public static void setQuickPanelToggleBarWidth(int width) {
        SettingsHelper.writeSetting(quickPanelToggleBarWidth, String.valueOf(width));
    }

    public static int getQuickPanelDividerWidth() {
        return SettingsHelper.readInteger(quickPanelDividerWidth, def_quickPanelDividerWidth);
    }

    public static void setQuickPanelDividerWidth(int width) {
        SettingsHelper.writeSetting(quickPanelDividerWidth, String.valueOf(width));
    }

    public static boolean getContactStatusChangeTextNotification() {
        return SettingsHelper.readBoolean(contactStatusChangeTextNotification, def_contactStatusChangeTextNotification);
    }

    public static void setContactStatusChangeTextNotification(boolean status) {
        SettingsHelper.writeSetting(contactStatusChangeTextNotification, String.valueOf(status));
    }

    public static boolean getContactStatusChangeSoundNotification() {
        return SettingsHelper.readBoolean(contactStatusChangeSoundNotification,
                def_contactStatusChangeSoundNotification);
    }

    public static void setContactStatusChangeSoundNotification(boolean status) {
        SettingsHelper.writeSetting(contactStatusChangeSoundNotification, String.valueOf(status));
    }

    /**
     *  public getters and setters used by other classes
     */
    public static boolean getFirstRun() {
        return SettingsHelper.readBoolean(firstRun, def_firstRun);
    }

    public static void setFirstRun(boolean status) {
        SettingsHelper.writeSetting(firstRun, String.valueOf(status));
    }

    public static boolean getSleepEnabled() {
        return SettingsHelper.readBoolean(sleepEnabled, def_sleepEnabled);
    }

    public static void setSleepenabled(boolean enabled) {
        SettingsHelper.writeSetting(sleepEnabled, String.valueOf(enabled));
    }

    public static int getMainFrameMaximised() {
        return SettingsHelper.readInteger(mainFrameMaximised, def_mainFrameMaximised);
    }

    public static void setMainFrameMaximised(int status) {
        SettingsHelper.writeSetting(mainFrameMaximised, String.valueOf(status));
    }

    public static int getMainFrameWidth() {
        return SettingsHelper.readInteger(mainFrameWidth, def_mainFrameWidth);
    }

    public static void setMainFrameWidth(int width) {
        SettingsHelper.writeSetting(mainFrameWidth, String.valueOf(width));
    }

    public static int getMainFrameHeight() {
        return SettingsHelper.readInteger(mainFrameHeight, def_mainFrameHeight);
    }

    public static void setMainFrameHeight(int height) {
        SettingsHelper.writeSetting(mainFrameHeight, String.valueOf(height));
    }

    public static int getChannelVerticalSPPosition() {
        return SettingsHelper.readInteger(channelVerticalSPPosition, def_channelVerticalSPPosition);
    }

    public static void setChannelVerticalSPPosition(int position) {
        SettingsHelper.writeSetting(channelVerticalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatHorizontalSPPosition() {
        return SettingsHelper.readInteger(channelChatHorizontalSPPosition, def_channelChatHorizontalSPPosition);
    }

    public static void setChannelChatHorizontalSPPosition(int position) {
        SettingsHelper.writeSetting(channelChatHorizontalSPPosition, String.valueOf(position));
    }

    public static int getChannelChatVerticalSPPosition() {
        return SettingsHelper.readInteger(channelChatVerticalSPPosition, def_channelChatVerticalSPPosition);
    }

    public static void setChannelChatVerticalSPPosition(int position) {
        SettingsHelper.writeSetting(channelChatVerticalSPPosition, String.valueOf(position));
    }

    //sound options
    public static boolean getSoundEnabled() {
        return SettingsHelper.readBoolean(SoundEnabled, def_soundEnabled);
    }

    public static void setSoundEnabled(boolean bool) {
        SettingsHelper.writeSetting(SoundEnabled, String.valueOf(bool));
    }

    //timestamps
    public static boolean getTimeStampEnabled() {
        return SettingsHelper.readBoolean(TimeStamps, def_timeStamps);
    }

    public static void setTimeStampEnabled(boolean bool) {
        SettingsHelper.writeSetting(TimeStamps, String.valueOf(bool));
    }

    //serverIp
    public static String getLastValidServerIP() {
        return SettingsHelper.readString(lastValidServerIP, def_lastValidServerIP);
    }

    public static void setLastValidServerIP(String ip) {
        SettingsHelper.writeSetting(lastValidServerIP, ip);
    }

    //serverPort
    public static int getLastValidServerPort() {
        return SettingsHelper.readInteger(lastValidServerPort, def_lastValidServerPort);
    }

    public static void setLastValidServerPort(int port) {
        SettingsHelper.writeSetting(lastValidServerPort, String.valueOf(port));
    }

    public static int getFiletTansferPort() {
        return SettingsHelper.readInteger(fileTransferPort, def_fileTransferPort);
    }

    public static void setFiletTansferPort(int port) {
        SettingsHelper.writeSetting(fileTransferPort, String.valueOf(port));
    }

    //autoLogin
    public static boolean getAutoLogin() {
        return SettingsHelper.readBoolean(autoLogin, def_autoLogin);
    }

    public static void setAutoLogin(boolean bool) {
        SettingsHelper.writeSetting(autoLogin, String.valueOf(bool));
    }

    public static String getHomeChannel() {
        String ch = SettingsHelper.readString(homeChannel, def_homeChannel);
        if (ch.length() == 3) {
            return GameDatabase.getGameName(ch);
        } else {
            return ch;
        }
    }

    public static void setHomeChannel(String channel) {
        SettingsHelper.writeSetting(homeChannel, GameDatabase.getIDofGame(channel));
    }

    public static String getDOSBoxExecutable() {
        return SettingsHelper.readString(DOSBoxExecutable, def_DOSEmulatorExecutable);
    }

    public static void setDOSBoxExecutable(String path) {
        SettingsHelper.writeSetting(DOSBoxExecutable, path);
    }

    public static boolean getDOSBoxFullscreen() {
        return SettingsHelper.readBoolean(DOSBoxFullscreen, def_DOSBoxFullscreen);
    }

    public static void setDOSBoxFullscreen(boolean value) {
        SettingsHelper.writeSetting(DOSBoxFullscreen, String.valueOf(value));
    }

    //debugMode
    public static boolean getDebugMode() {
        return SettingsHelper.readBoolean(debugMode, def_debugMode);
    }

    public static void setDebugMode(boolean bool) {
        SettingsHelper.writeSetting(debugMode, String.valueOf(bool));
    }

    //selectedLookAndFeel
    public static String getSelectedLookAndFeel() {
        return SettingsHelper.readString(selectedLookAndFeel, def_selectedLookAndFeel);
    }

    public static void setSelectedLookAndFeel(String string) {
        SettingsHelper.writeSetting(selectedLookAndFeel, string);
    }

    //useNativeLookAndFeel
    public static boolean getUseNativeLookAndFeel() {
        return SettingsHelper.readBoolean(useNativeLookAndFeel, def_useNativeLookAndFeel);
    }

    public static void setUseNativeLookAndFeel(boolean bool) {
        SettingsHelper.writeSetting(useNativeLookAndFeel, String.valueOf(bool));
    }

    //bgColor
    public static Color getBackgroundColor() {
        return SettingsHelper.readColor(bgColor, def_bgColor);
    }

    public static void setBackgroundColor(Color color) {
        SettingsHelper.writeSetting(bgColor, String.valueOf(color.getRGB()));
    }

    //selection color
    public static Color getSelectionColor() {
        return SettingsHelper.readColor(selectionColor, def_SelectionColor);
    }

    public static void setSelectionColor(Color color) {
        SettingsHelper.writeSetting(selectionColor, String.valueOf(color.getRGB()));
    }

    //fgColor
    public static Color getForegroundColor() {
        return SettingsHelper.readColor(fgColor, def_fgColor);
    }

    public static void setForegroundColor(Color color) {
        SettingsHelper.writeSetting(fgColor, String.valueOf(color.getRGB()));
    }

    //yourUsernameColor
    public static Color getYourUsernameColor() {
        return SettingsHelper.readColor(yourUsernameColor, def_UsernameColor);
    }

    public static void setYourUsernameColor(Color color) {
        SettingsHelper.writeSetting(yourUsernameColor, String.valueOf(color.getRGB()));
    }

    //otherUsernamesColor
    public static Color getOtherUsernamesColor() {
        return SettingsHelper.readColor(otherUsernamesColor, def_otherUsernamesColor);
    }

    public static void setOtherUsernamesColor(Color color) {
        SettingsHelper.writeSetting(otherUsernamesColor, String.valueOf(color.getRGB()));
    }

    //systemMessageColor
    public static Color getSystemMessageColor() {
        return SettingsHelper.readColor(systemMessageColor, def_systemMessageColor);
    }

    public static void setSystemMessageColor(Color color) {
        SettingsHelper.writeSetting(systemMessageColor, String.valueOf(color.getRGB()));
    }

    //whisperMessageColor
    public static Color getWhisperMessageColor() {
        return SettingsHelper.readColor(whisperMessageColor, def_whisperMessageColor);
    }

    public static void setWhisperMessageColor(Color color) {
        SettingsHelper.writeSetting(whisperMessageColor, String.valueOf(color.getRGB()));
    }

    //nameStyle
    public static String getNameStyle() {
        return SettingsHelper.readString(nameStyle, def_nameStyle);
    }

    public static void setNameStyle(String style) {
        SettingsHelper.writeSetting(nameStyle, style);
    }

    //nameSize
    public static int getNameSize() {
        return SettingsHelper.readInteger(nameSize, def_nameSize);
    }

    public static void setNameSize(int size) {
        SettingsHelper.writeSetting(nameSize, String.valueOf(size));
    }

    //messageStyle
    public static String getMessageStyle() {
        return SettingsHelper.readString(messageStyle, def_messageStyle);
    }

    public static void setMessageStyle(String style) {
        SettingsHelper.writeSetting(messageStyle, style);
    }

    //messageSize
    public static int getMessageSize() {
        return SettingsHelper.readInteger(messageSize, def_messageSize);
    }

    public static void setMessageSize(int size) {
        SettingsHelper.writeSetting(messageSize, String.valueOf(size));
    }

    public static Color getFriendMessageColor() {
        return SettingsHelper.readColor(friendMessageColor, def_friendMessageColor);
    }

    public static void setFriendMessageColor(Color color) {
        SettingsHelper.writeSetting(friendMessageColor, String.valueOf(color.getRGB()));
    }

    public static Color getFriendUsernameColor() {
        return SettingsHelper.readColor(friendUsernameColor, def_friendUsernameColor);
    }

    public static void setFriendUsernameColor(Color color) {
        SettingsHelper.writeSetting(friendUsernameColor, String.valueOf(color.getRGB()));
    }

    public static Color getUserMessageColor() {
        return SettingsHelper.readColor(userMessageColor, def_userMessageColor);
    }

    public static void setUserMessageColor(Color color) {
        SettingsHelper.writeSetting(userMessageColor, String.valueOf(color.getRGB()));
    }

    //colorizeBody
    public static boolean getColorizeBody() {
        return SettingsHelper.readBoolean(colorizeBody, def_colorizeBody);
    }

    public static void setColorizeBody(boolean bool) {
        SettingsHelper.writeSetting(colorizeBody, String.valueOf(bool));
    }

    //colorizeText
    public static boolean getColorizeText() {
        return SettingsHelper.readBoolean(colorizeText, def_colorizeText);
    }

    public static void setColorizeText(boolean bool) {
        SettingsHelper.writeSetting(colorizeText, String.valueOf(bool));
    }

    //recieved file destination
    public static String getRecieveDestination() {
        return SettingsHelper.readString(recieveDest, def_recievedest);
    }

    public static void setRecieveDestination(String path) {
        SettingsHelper.writeSetting(recieveDest, path);
    }

    public static String getWineCommand() {
        return SettingsHelper.readString(wineCommand, def_wineComamnd);
    }

    public static void setWineCommand(String path) {
        SettingsHelper.writeSetting(wineCommand, path);
    }

    //lastLoginName
    public static String getLastLoginName() {
        return SettingsHelper.readString(lastLoginName, def_lastLoginName);
    }

    public static void setLastLoginName(String name) {
        SettingsHelper.writeSetting(lastLoginName, name);
    }

    //lastLoginPassword
    public static String getLastLoginPassword() {
        return PasswordEncrypter.decodePassword(
                SettingsHelper.readString(lastLoginPassword, def_lastLoginPassword));
    }

    public static void setLastLoginPassword(String pw) {
        SettingsHelper.writeSetting(lastLoginPassword,
                PasswordEncrypter.encodePassword(PasswordEncrypter.encryptPassword(pw)));
    }

    public static void setMultiChannel(boolean enabled) {
        SettingsHelper.writeSetting(multiChannel, String.valueOf(enabled));
    }

    public static boolean getMultiChannel() {
        return SettingsHelper.readBoolean(multiChannel, def_multiChannel);
    }

    public static boolean isQuickPanelIconSizeBig() {
        return SettingsHelper.readBoolean(quickPanelIconSizeIsBig, def_quickPanelIconSizeIsBig);
    }

    public static void setIsQuickPanelIconSizeBig(boolean bool) {
        SettingsHelper.writeSetting(quickPanelIconSizeIsBig, String.valueOf(bool));
    }

    public static void setShowOfflineContacts(boolean enabled) {
        boolean refreshContacts = !Settings.getShowOfflineContacts() && enabled;

        SettingsHelper.writeSetting(showOfflineContacts, String.valueOf(enabled));

        if (refreshContacts) {
            Protocol.refreshContacts();
        } else {
            Globals.getContactList().updateShowOfflineContacts();
        }

    }

    public static boolean getShowOfflineContacts() {
        return SettingsHelper.readBoolean(showOfflineContacts, def_showOfflineContacts);
    }

    public static void setRememberMainFrameSize(boolean enabled) {
        SettingsHelper.writeSetting(rememberMainFrameSize, String.valueOf(enabled));
    }

    public static boolean getRememberMainFrameSize() {
        return SettingsHelper.readBoolean(rememberMainFrameSize, def_rememberMainFrameSize);
    }

    public static void setShowMinimizeToTrayHint(boolean value) {
        SettingsHelper.writeSetting(SHOW_MINIMIZE_TO_TRAY_HINT,
                String.valueOf(value));
    }

    public static boolean getShowMinimizeToTrayHint() {
        return SettingsHelper.readBoolean(SHOW_MINIMIZE_TO_TRAY_HINT,
                DEF_SHOW_MINIMIZE_TO_TRAY_HINT);
    }
}
