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

package coopnetclient.protocol;

import coopnetclient.*;
import coopnetclient.enums.ProtocolCommands;
import coopnetclient.modules.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
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
    
    public static String RequestContact(String contactname) {
        return ProtocolCommands.SEND_REQUEST.toString() + INFORMATION_DELIMITER
                 + contactname;
    }
    
    public static String acceptRequest(String contactname) {
        return ProtocolCommands.ACCEPT_REQUEST.toString() + INFORMATION_DELIMITER
                 + contactname;
    }
    
    public static String refuseRequest(String contactname) {
        return ProtocolCommands.REFUSE_REQUEST.toString() + INFORMATION_DELIMITER
                 + contactname;
    }
    
    public static String createGroup(String groupName) {
        return ProtocolCommands.MAKE_GROUP.toString() + INFORMATION_DELIMITER
                 + groupName;
    }

    public static String removeContact(String contactname) {
        return ProtocolCommands.DELETE_CONTACT.toString() + INFORMATION_DELIMITER
                 + contactname;
    }
    
    public static String renameGroup(String groupName,String newgroupName) {
        return ProtocolCommands.RENAME_GROUP.toString() + INFORMATION_DELIMITER
                 + groupName + INFORMATION_DELIMITER 
                 + newgroupName;
    }
    
    public static String deleteGroup(String groupName) {
        return ProtocolCommands.DELETE_GROUP.toString() + INFORMATION_DELIMITER
                 + groupName;
    }
    
    public static String moveToGroup(String contact,String newgroupName) {
        return ProtocolCommands.MOVE_TO_GROUP.toString() + INFORMATION_DELIMITER
                 + contact + INFORMATION_DELIMITER 
                 + newgroupName;
    }
    
    public static String refreshContacts(boolean showOffline) {
        return ProtocolCommands.RESEND_CONTACTS.toString() + INFORMATION_DELIMITER
                 + ( showOffline ? "1" : "0" ) ;
    }
    
    public static String AcceptTransfer(String sender, String filename, long firstByteToSend) {
        return ProtocolCommands.ACCEPT_FILE.toString() + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + Settings.getFiletTansferPort() + INFORMATION_DELIMITER 
                + firstByteToSend;
    }

    public static String RefuseTransfer(String sender, String filename) {
        return ProtocolCommands.REFUSE_FILE.toString() + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename;
    }

    public static String cancelTransfer(String sender, String filename) {
        return ProtocolCommands.CANCEL_FILE.toString()+ INFORMATION_DELIMITER
                + sender + INFORMATION_DELIMITER
                + filename;
    }

    public static String createRoom(String name, String modIndex, String password, String limit, boolean compatible, boolean instantLaunch) {
        return ProtocolCommands.CREATE_ROOM.toString() + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + password + INFORMATION_DELIMITER 
                + limit + INFORMATION_DELIMITER 
                + (compatible ? "1" : "0") + INFORMATION_DELIMITER 
                + (instantLaunch ? "1" : "0") + INFORMATION_DELIMITER 
                + Client.getHamachiAddress() + INFORMATION_DELIMITER 
                + modIndex ;
    }

    public static String SendSetting(String name,String value) {
        return ProtocolCommands.SET_GAMESETTING.toString() + INFORMATION_DELIMITER
                 + name + INFORMATION_DELIMITER 
                 + value;
    }

    public static String SetSleep(boolean enabled) {
        return ProtocolCommands.SET_SLEEP.toString() + INFORMATION_DELIMITER
                 + enabled;
    }

    public static String joinRoom(String hostname, String password) {
        return ProtocolCommands.JOIN_ROOM.toString() + INFORMATION_DELIMITER
                 + hostname + INFORMATION_DELIMITER 
                 + password;
    }
    
    public static String joinRoomByID(String ID, String password) {
        return ProtocolCommands.JOIN_ROOM_BY_ID.toString() + INFORMATION_DELIMITER
                 + ID + INFORMATION_DELIMITER 
                 + password;
    }

    public static String refresh() {
        return ProtocolCommands.REFRESH.toString();
    }

    public static String ChannelList() {
        return ProtocolCommands.LIST_CHANNELS.toString();
    }

    public static String JoinChannel(String channelname) {
        return ProtocolCommands.JOIN_CHANNEL.toString() + INFORMATION_DELIMITER
                 + GameDatabase.IDofGame(channelname);
    }

    public static String leaveChannel() {
        return ProtocolCommands.LEAVE_CHANNEL.toString();
    }

    public static String changePassword(String oldpassword, String newpassword) {
        return ProtocolCommands.CHANGE_PASSWORD + INFORMATION_DELIMITER 
                + PasswordEncrypter.encryptPassword(oldpassword) + INFORMATION_DELIMITER 
                + PasswordEncrypter.encryptPassword(newpassword);
    }

    public static String editProfile() {
        return ProtocolCommands.EDIT_PROFILE.toString();
    }

    public static String quit() {
        return ProtocolCommands.QUIT.toString();
    }

    public static String login(String name, String password) {
        return ProtocolCommands.LOGIN + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + PasswordEncrypter.encryptPassword(password);
    }
    
    public static String login() {
        return ProtocolCommands.LOGIN + INFORMATION_DELIMITER 
                + Settings.getLastLoginName() + INFORMATION_DELIMITER 
                + Settings.getLastLoginPassword();
    }

    public static String register(String name, String password) {
        return ProtocolCommands.NEW_PLAYER + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + PasswordEncrypter.encryptPassword(password);
    }

    public static String mainChat(String message) {
        return ProtocolCommands.CHAT_MAIN.toString() + INFORMATION_DELIMITER
                 + message;
    }

    public static String roomChat(String message) {
        return ProtocolCommands.CHAT_ROOM.toString() + INFORMATION_DELIMITER
                 + message;
    }

    public static String privatechat(String message, String sendto) {
        return ProtocolCommands.WHISPER + INFORMATION_DELIMITER 
                + sendto + INFORMATION_DELIMITER 
                + message;
    }

    public static String kick(String playername) {
        return ProtocolCommands.KICK.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String ban(String playername) {
        return ProtocolCommands.BAN.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String sendInvite(String subject) {
        return ProtocolCommands.INVITE_USER.toString() + INFORMATION_DELIMITER
                + subject;
    }

    public static String turnTransferAround(String username,String filename) {
        return ProtocolCommands.TURN_AROUND  + INFORMATION_DELIMITER 
                + username + INFORMATION_DELIMITER 
                + filename ;
    }

    public static String unban(String playername) {
        return ProtocolCommands.UNBAN.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String mute(String playername) {
        return ProtocolCommands.MUTE.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String unmute(String playername) {
        return ProtocolCommands.UNMUTE.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String requestProfile(String playername) {
        return ProtocolCommands.REQUEST_PROFILE.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String nudge(String playername) {
        return ProtocolCommands.NUDGE.toString() + INFORMATION_DELIMITER
                 + playername;
    }

    public static String setEmail(String email) {
        return ProtocolCommands.SET_EMAIL.toString() + INFORMATION_DELIMITER
                 + email;
    }

    public static String setEmailPublicity(boolean ispublic) {
        return ProtocolCommands.SET_EMAIL_IS_PUBLIC.toString() + INFORMATION_DELIMITER
                 + ispublic;
    }

    public static String setCountry(String country) {
        return ProtocolCommands.SET_COUNTRY.toString() + INFORMATION_DELIMITER
                 + country;
    }

    public static String setWebPage(String webpage) {
        return ProtocolCommands.SET_WEBSITE.toString() + INFORMATION_DELIMITER
                 + webpage;
    }

    public static String setGameName(String gamename) {
        return ProtocolCommands.SET_INGAMENAME.toString() + INFORMATION_DELIMITER
                 + gamename;
    }

    public static String changeName(String name) {
        return ProtocolCommands.SET_LOGINNAME.toString() + INFORMATION_DELIMITER
                + name;
    }

    public static String gameClosed() {
        return ProtocolCommands.GAME_CLOSED.toString();
    }

    public static String closeRoom() {
        return ProtocolCommands.CLOSE.toString();
    }

    public static String leaveRoom() {
        return ProtocolCommands.LEAVE_ROOM.toString();
    }

    public static String Launch() {
        return ProtocolCommands.LAUNCH.toString();
    }

    public static String flipReadystatus() {
        return ProtocolCommands.FLIP_READY.toString();
    }

    public static String Sendfile(String reciever, String filename, String sizeinbytes,String port) {
        return ProtocolCommands.SEND_FILE.toString() + INFORMATION_DELIMITER 
                + reciever + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + sizeinbytes+ INFORMATION_DELIMITER                 
                + port;
    }
}
