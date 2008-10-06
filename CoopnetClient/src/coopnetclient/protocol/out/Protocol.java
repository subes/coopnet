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

package coopnetclient.protocol.out;

import coopnetclient.protocol.*;
import coopnetclient.*;
import coopnetclient.enums.ClientProtocolCommands;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.launcher.Launcher;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import passwordencrypter.PasswordEncrypter;

public class Protocol {
    
    //See http://en.wikipedia.org/wiki/Control_character
    //Moved these to some hopefully unused area of Unicode -> See http://www.utf8-chartable.de/unicode-utf8-table.pl
    //The private area beyond F0000 cannot be used by us, also we cant use any char that in too low in value, 
    //because these might be interpreted as normal numbers in length 1
    public static final String INFORMATION_DELIMITER = "\uAB17"; // 0x17 = ETB = End Of Transmission Block
    public static final String MESSAGE_DELIMITER = "\uAB04"; // 0x04 = EOT = End Of Transmission
    public static byte[] ENCODED_MESSAGE_DELIMITER;
    
    //Heartbeat characters
    public static final String HEARTBEAT = "\u2665";
    
    static{
        try{
            CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
            ENCODED_MESSAGE_DELIMITER = encoder.encode(CharBuffer.wrap(MESSAGE_DELIMITER)).array();
        }catch(CharacterCodingException ex){
            ex.printStackTrace();
        }
    }
    
    public static void RequestContact(String contactName) {
        new Message(ClientProtocolCommands.SEND_CONTACT_REQUEST, contactName);
    }
    
    public static void acceptRequest(String contactName) {
        new Message(ClientProtocolCommands.ACCEPT_CONTACT_REQUEST, contactName);
    }
    
    public static void refuseRequest(String contactName) {
        new Message(ClientProtocolCommands.REFUSE_CONTACT_REQUEST, contactName);
    }
    
    public static void createGroup(String groupName) {
        new Message(ClientProtocolCommands.CREATE_GROUP, groupName);
    }

    public static void removeContact(String contactName) {
        new Message(ClientProtocolCommands.DELETE_CONTACT, contactName);
    }
    
    public static void renameGroup(String groupName,String newGroupName) {
        String[] info = {groupName, newGroupName};
        new Message(ClientProtocolCommands.RENAME_GROUP, info);
    }
    
    public static void deleteGroup(String groupName) {
        new Message(ClientProtocolCommands.DELETE_GROUP, groupName);
    }
    
    public static void moveToGroup(String contact,String newGroupName) {
        String[] info = {contact, newGroupName};
        new Message(ClientProtocolCommands.MOVE_TO_GROUP, info);
    }
    
    public static void refreshContacts(boolean showOffline) {
        new Message(ClientProtocolCommands.REFRESH_CONTACTS, String.valueOf(showOffline));
    }
    
    public static void acceptTransfer(String sender, String fileName, long firstByteToSend) {
        String[] info = {sender, fileName, String.valueOf(Settings.getFiletTansferPort()), String.valueOf(firstByteToSend)};
        new Message(ClientProtocolCommands.ACCEPT_FILE, info);
    }

    public static void refuseTransfer(String sender, String fileName) {
        String[] info = {sender, fileName};
        new Message(ClientProtocolCommands.REFUSE_FILE, info);
    }

    public static void cancelTransfer(String sender, String fileName) {
        String[] info = {sender, fileName};
        new Message(ClientProtocolCommands.CANCEL_FILE, info);
    }

    public static void createRoom(String channelName, String name, int modIndex, String password, int maxPlayers, boolean compatible, boolean instantLaunch) {
        String[] info = {GameDatabase.IDofGame(channelName), name, password, String.valueOf(maxPlayers), String.valueOf(compatible), String.valueOf(instantLaunch), Client.getHamachiAddress(), String.valueOf(modIndex)};
        new Message(ClientProtocolCommands.CREATE_ROOM, info);
    }

    public static void sendSetting(String name,String value) {
        String[] info = {name, value};
        new Message(ClientProtocolCommands.SET_GAMESETTING, info);
    }

    public static void setSleep(boolean enabled) {
        new Message(ClientProtocolCommands.SET_SLEEP, String.valueOf(enabled));
    }

    public static void joinRoom(String channelName, String hostname, String password) {
        String[] info = {GameDatabase.IDofGame(channelName), hostname, password};
        new Message(ClientProtocolCommands.JOIN_ROOM, info);
    }
    
    public static void joinRoomByID(String ID, String password) {
        String[] info = {ID, password};
        new Message(ClientProtocolCommands.JOIN_ROOM_BY_ID, info);
    }

    public static void refreshRoomsAndPlayers(String channelName) {
        new Message(ClientProtocolCommands.REFRESH_ROOMS_AND_PLAYERS, GameDatabase.IDofGame(channelName));
    }

    public static void channelList() {
        new Message(ClientProtocolCommands.LIST_CHANNELS);
    }

    public static void joinChannel(String channelName) {
        if(TabOrganizer.getChannelPanel(channelName) == null){
            new Message(ClientProtocolCommands.JOIN_CHANNEL, GameDatabase.IDofGame(channelName));
        }else{
            TabOrganizer.openChannelPanel(channelName);
        }
    }

    public static void leaveChannel(String channelName) {
        new Message(ClientProtocolCommands.LEAVE_CHANNEL, GameDatabase.IDofGame(channelName));
    }

    public static void changePassword(String oldpassword, String newpassword) {
        String[] info = {PasswordEncrypter.encryptPassword(oldpassword), PasswordEncrypter.encryptPassword(newpassword)};
        new Message(ClientProtocolCommands.CHANGE_PASSWORD,info);
    }

    public static void editProfile() {
        new Message(ClientProtocolCommands.EDIT_PROFILE);
    }

    public static void saveProfile(String loginName, String ingameName, String email, String country, String website, boolean emailIsPublic ) {
        String[] info = { loginName, ingameName,  email,  country,  website,  emailIsPublic+"" };
        new Message(ClientProtocolCommands.SAVE_PROFILE,info);
    }
    
    public static void quit() {
        new Message(ClientProtocolCommands.QUIT);
    }

    public static void login(String name, String password) {
        String[] info = {name, PasswordEncrypter.encryptPassword(password)};
        new Message(ClientProtocolCommands.LOGIN, info);
    }
    
    public static void autoLogin() {
        String[] info = {Settings.getLastLoginName(), Settings.getLastLoginPassword()};
        new Message(ClientProtocolCommands.LOGIN, info);
    }

    public static void register(String name, String password) {
        String[] info = {name, PasswordEncrypter.encryptPassword(password)};
        new Message(ClientProtocolCommands.NEW_PLAYER, info);
    }

    public static void mainChat(String channelName, String message) {
        String[] info = {GameDatabase.IDofGame(channelName), message};
        new Message(ClientProtocolCommands.CHAT_MAIN, info);
    }

    public static void roomChat(String message) {
        new Message(ClientProtocolCommands.CHAT_ROOM, message);
    }

    public static void privateChat(String message, String sendTo) {
        String[] info = {sendTo, message};
        new Message(ClientProtocolCommands.WHISPER, info);
    }

    public static void kick(String playerName) {
        new Message(ClientProtocolCommands.KICK, playerName);
    }

    public static void sendInvite(String subject) {
        new Message(ClientProtocolCommands.INVITE_USER, subject);
    }

    public static void turnTransferAround(String playerName, String fileName) {
        String[] info = {playerName, fileName};
        new Message(ClientProtocolCommands.TURN_AROUND_FILE, info);
    }

    public static void ban(String playerNname) {
        new Message(ClientProtocolCommands.BAN, playerNname);
    }
    
    public static void unBan(String playerName) {
        new Message(ClientProtocolCommands.UNBAN, playerName);
    }

    public static void mute(String playerName) {
        new Message(ClientProtocolCommands.MUTE, playerName);
    }

    public static void unMute(String playerName) {
        new Message(ClientProtocolCommands.UNMUTE, playerName);
    }

    public static void requestProfile(String playerName) {
        new Message(ClientProtocolCommands.REQUEST_PROFILE, playerName);
    }

    public static void nudge(String playerName) {
        new Message(ClientProtocolCommands.NUDGE, playerName);
    }

    public static void setEmail(String email) {
        new Message(ClientProtocolCommands.SET_EMAIL, email);
    }

    public static void setEmailPublicity(boolean isPublic) {
        new Message(ClientProtocolCommands.SET_EMAIL_IS_PUBLIC, String.valueOf(isPublic));
    }

    public static void setCountry(String country) {
        new Message(ClientProtocolCommands.SET_COUNTRY, country);
    }

    public static void setWebsite(String website) {
        new Message(ClientProtocolCommands.SET_WEBSITE, website);
    }

    public static void setGameName(String ingameName) {
        new Message(ClientProtocolCommands.SET_INGAMENAME, ingameName);
    }

    public static void setLoginName(String loginName) {
        new Message(ClientProtocolCommands.SET_LOGINNAME, loginName);
    }

    public static void gameClosed(String channelName) {
        new Message(ClientProtocolCommands.GAME_CLOSED, GameDatabase.IDofGame(channelName));
    }

    public static void closeRoom() {
        new Message(ClientProtocolCommands.CLOSE_ROOM);
    }

    public static void leaveRoom() {
        new Message(ClientProtocolCommands.LEAVE_ROOM);
    }

    public static void launch() {
        new Message(ClientProtocolCommands.LAUNCH);
    }

    public static void flipReadystatus() {
        new Message(ClientProtocolCommands.FLIP_READY);
    }

    public static void sendFile(String reciever, String fileName, String sizeInBytes,String port) {
        String[] info = {reciever, fileName, sizeInBytes, port};
        new Message(ClientProtocolCommands.SEND_FILE, info);
    }
}
