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

package coopnetclient.protocol.out;

import coopnetclient.protocol.*;
import coopnetclient.*;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.ClientProtocolCommands;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.Settings;
import coopnetclient.utils.Verification;
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
    
    //Heartbeat character
    public static final String HEARTBEAT = "\u2665";

    static{
        try{
            CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
            ENCODED_MESSAGE_DELIMITER = encoder.encode(CharBuffer.wrap(MESSAGE_DELIMITER)).array();
        }catch(CharacterCodingException ex){
            ex.printStackTrace();
        }
    }

    public static void sendVersion() {
        new Message(ClientProtocolCommands.CLIENTVERSION, Globals.CLIENT_VERSION);
    }

    public static void addToContacts(String contactName) {
        new Message(ClientProtocolCommands.SEND_CONTACT_REQUEST, contactName);
    }
    
    public static void acceptRequest(String contactName) {
        new Message(ClientProtocolCommands.ACCEPT_CONTACT_REQUEST, contactName);
    }

    public static void passwordRecovery(String name, String email) {
        String[] info = {name, email};
        new Message(ClientProtocolCommands.PASSWORD_RECOVERY, info);
    }
    
    public static void refuseRequest(String contactName) {
        new Message(ClientProtocolCommands.REFUSE_CONTACT_REQUEST, contactName);
    }
    
    public static void createGroup(String groupName) {
        if(Verification.verifyGroupName(groupName) && !Globals.getContactList().getGroupNames().contains(groupName)){
            new Message(ClientProtocolCommands.CREATE_GROUP, groupName);
        }
    }

    public static void register(String name, String password, String email, String ingameName, String country, String website) {
        String[] info = {name,PasswordEncrypter.encryptPassword(password),email,ingameName,country,website};
        new Message(ClientProtocolCommands.NEW_PLAYER, info);
    }

    public static void removeContact(String contactName) {
        new Message(ClientProtocolCommands.DELETE_CONTACT, contactName);
    }
    
    public static void renameGroup(String groupName,String newGroupName) {
        if(Verification.verifyGroupName(newGroupName) && !Globals.getContactList().getGroupNames().contains(newGroupName)){
            String[] info = {groupName, newGroupName};
            new Message(ClientProtocolCommands.RENAME_GROUP, info);
        }
    }
    
    public static void deleteGroup(String groupName) {
        new Message(ClientProtocolCommands.DELETE_GROUP, groupName);
    }
    
    public static void moveToGroup(String contact,String newGroupName) {
        String[] info = {contact, newGroupName};
        new Message(ClientProtocolCommands.MOVE_TO_GROUP, info);
    }
    
    public static void refreshContacts() {
        new Message(ClientProtocolCommands.REFRESH_CONTACTS, String.valueOf(Settings.getShowOfflineContacts()));
    }

    public static void setAwayStatus() {
        new Message(ClientProtocolCommands.SETAWAYSTATUS);
    }

    public static void unSetAwayStatus() {
        new Message(ClientProtocolCommands.UNSETAWAYSTATUS);
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

    public static void createRoom(RoomData rd) {
        final String[] info = {GameDatabase.getIDofGame(rd.getChannel()), rd.getRoomName(), rd.getPassword(), String.valueOf(rd.getMaxPlayers()), String.valueOf(rd.isInstant()), Client.getHamachiAddress(), String.valueOf(rd.getModIndex()), String.valueOf(rd.isDoSearch())};
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
        String[] info = {GameDatabase.getIDofGame(channelName), hostname, password};
        new Message(ClientProtocolCommands.JOIN_ROOM, info);
    }
    
    public static void joinRoomByID(String ID, String password) {
        String[] info = {ID, password};
        new Message(ClientProtocolCommands.JOIN_ROOM_BY_ID, info);
    }

    public static void refreshRoomsAndPlayers(String channelName) {
        new Message(ClientProtocolCommands.REFRESH_ROOMS_AND_PLAYERS, GameDatabase.getIDofGame(channelName));
    }

    public static void joinChannel(String channelName) {
        if(TabOrganizer.getChannelPanel(channelName) == null){
            new Message(ClientProtocolCommands.JOIN_CHANNEL, GameDatabase.getIDofGame(channelName));
        }else{
            TabOrganizer.openChannelPanel(channelName);
        }
    }

    public static void leaveChannel(String channelName) {
        new Message(ClientProtocolCommands.LEAVE_CHANNEL, GameDatabase.getIDofGame(channelName));
    }

    public static void changePassword(String oldpassword, String newpassword) {
        String[] info = {PasswordEncrypter.encryptPassword(oldpassword), PasswordEncrypter.encryptPassword(newpassword)};
        new Message(ClientProtocolCommands.CHANGE_PASSWORD,info);
    }

    public static void editProfile() {
        new Message(ClientProtocolCommands.EDIT_PROFILE);
    }

    public static void saveProfile(String loginName, String ingameName, String email, String country, String website ) {
        String[] info = { loginName, ingameName,  email,  country,  website };
        new Message(ClientProtocolCommands.SAVE_PROFILE,info);
    }
    
    public static void quit() {
        new Message(ClientProtocolCommands.QUIT);
    }

    public static void login(String name, String password) {
        String[] info = {name,
                PasswordEncrypter.encryptPassword(password),
                String.valueOf(Launcher.isPlaying()),
                String.valueOf(GameDatabase.getIDofGame(Launcher.getLaunchedGame()))
        };
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
        String[] info = {GameDatabase.getIDofGame(channelName), message};
        new Message(ClientProtocolCommands.CHAT_MAIN, info);
    }

    public static void roomChat(String message) {
        new Message(ClientProtocolCommands.CHAT_ROOM, message);
    }

    public static void privateChat(String sendTo ,String message ) {
        String[] info = {sendTo, message};
        new Message(ClientProtocolCommands.WHISPER, info);
    }

    public static void kick(String playerName) {
        new Message(ClientProtocolCommands.KICK, playerName);
    }

    public static void sendRoomInvite(String subject) {
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
        Globals.getClientFrame().printToVisibleChatbox("System", "You have nudged "+playerName +"!", ChatStyles.SYSTEM , false);
    }

    public static void gameClosed(String channelName) {
        new Message(ClientProtocolCommands.GAME_CLOSED, GameDatabase.getIDofGame(channelName));
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
