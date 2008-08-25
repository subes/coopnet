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

package coopnetclient;

import coopnetclient.modules.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Protocol {
    
    //See http://en.wikipedia.org/wiki/Control_character
    //Moved these to some hopefully unused area of Unicode -> See http://www.utf8-chartable.de/unicode-utf8-table.pl
    //The private area beyond F0000 cannot be used by us, also we cant use any char that in too low in value, 
    //because these might be interpreted as normal numbers in length 1
    public static final String INFORMATION_DELIMITER = "\uAB17"; // 0x17 = ETB = End Of Transmission Block
    public static final String MESSAGE_DELIMITER = "\uAB04"; // 0x04 = EOT = End Of Transmission
    public static byte[] ENCODED_MESSAGE_DELIMITER;
    
    //Heartbeat characters
    public static final String HEARTBEAT_REQUEST = "\u2661";
    public static final String HEARTBEAT_RESPONSE = "\u2665";
    
    static{
        try{
            CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
            ENCODED_MESSAGE_DELIMITER = encoder.encode(CharBuffer.wrap(MESSAGE_DELIMITER)).array();
        }catch(CharacterCodingException ex){
            ex.printStackTrace();
        }
    }
    
    public static String RequestContact(String contactname) {
        return "sendrequest" + contactname;
    }
    
    public static String acceptRequest(String contactname) {
        return "acceptrequest" + contactname;
    }
    
    public static String refuseRequest(String contactname) {
        return "refuserequest" + contactname;
    }
    
    public static String createGroup(String groupName) {
        return "makegroup" + groupName;
    }

    public static String removeContact(String contactname) {
        return "deletecontact" + contactname;
    }
    
    public static String renameGroup(String groupName,String newgroupName) {
        return "renamegroup" + groupName + INFORMATION_DELIMITER +newgroupName;
    }
    
    public static String deleteGroup(String groupName) {
        return "deletegroup" + groupName;
    }
    
    public static String moveToGroup(String contact,String newgroupName) {
        return "movetogroup" + contact + INFORMATION_DELIMITER +newgroupName;
    }
    
    public static String refreshContacts() {
        return "resendcontacts";
    }
    
    public static String AcceptTransfer(String sender, String filename, long firstByteToSend) {
        return "AcceptFile" + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + Settings.getFiletTansferPort() + INFORMATION_DELIMITER 
                + firstByteToSend;
    }

    public static String RefuseTransfer(String sender, String filename) {
        return "RefuseFile" + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename;
    }

    public static String cancelTransfer(String sender, String filename) {
        return "CancelFile" + INFORMATION_DELIMITER
                + sender + INFORMATION_DELIMITER
                + filename;
    }

    public static String createRoom(String name, String modIndex, String password, String limit, boolean compatible, boolean instantLaunch) {
        return "create" + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + password + INFORMATION_DELIMITER 
                + limit + INFORMATION_DELIMITER 
                + (compatible ? "1" : "0") + INFORMATION_DELIMITER 
                + (instantLaunch ? "1" : "0") + INFORMATION_DELIMITER 
                + Client.getHamachiAddress() + INFORMATION_DELIMITER 
                + modIndex ;
    }

    public static String SendSetting(String name,String value) {
        return "setgamesetting" + name + INFORMATION_DELIMITER + value;
    }

    public static String SetSleep(boolean enabled) {
        return "setsleep " + String.valueOf(enabled);
    }

    public static String joinRoom(String hostname, String password) {
        return "join " + hostname + INFORMATION_DELIMITER + password;
    }
    
    public static String joinRoomByID(String ID, String password) {
        return "joinID" + ID + INFORMATION_DELIMITER + password;
    }

    public static String refresh() {
        return "refresh";
    }

    public static String ChannelList() {
        return "listchannels";
    }

    public static String JoinChannel(String channelname) {
        return "joinchannel " + GameDatabase.IDofGame(channelname);
    }

    public static String leaveChannel() {
        return "leavechannel";
    }

    public static String changePassword(String oldpassword, String newpassword) {
        return "changepassword " + INFORMATION_DELIMITER 
                + oldpassword + INFORMATION_DELIMITER 
                + newpassword + INFORMATION_DELIMITER 
                + ".";
    }

    public static String editProfile() {
        return "editprofile";
    }

    public static String quit() {
        return "quit";
    }

    public static String login(String name, String password) {
        return "login" + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + password + INFORMATION_DELIMITER;
    }

    public static String register(String name, String password) {
        return "newplayer" + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER 
                + password + INFORMATION_DELIMITER 
                + name + INFORMATION_DELIMITER;
    }

    public static String mainChat(String message) {
        return "msay " + message;
    }

    public static String roomChat(String message) {
        return "rsay " + message;
    }

    public static String privatechat(String message, String sendto) {
        return "whisper" + INFORMATION_DELIMITER 
                + sendto + INFORMATION_DELIMITER 
                + message;
    }

    public static String kick(String playername) {
        return "kick " + playername;
    }

    public static String ban(String playername) {
        return "ban " + playername;
    }

    public static String turnTransferAround(String username,String filename) {
        return "TurnAround"  + INFORMATION_DELIMITER +username + INFORMATION_DELIMITER + filename ;
    }

    public static String unban(String playername) {
        return "unban " + playername;
    }

    public static String mute(String playername) {
        return "mute " + playername;
    }

    public static String unmute(String playername) {
        return "unmute " + playername;
    }

    public static String requestProfile(String playername) {
        return "requestprofile " + playername;
    }

    public static String nudge(String playername) {
        return "nudge " + playername;
    }

    public static String setEmail(String email) {
        return "setemail " + email;
    }

    public static String setEmailPublicity(boolean ispublic) {
        return "setemailpublicity " + (ispublic ? "true" : "false");
    }

    public static String setCountry(String country) {
        return "setcountry " + country;
    }

    public static String setWebPage(String webpage) {
        return "setwebpage " + webpage;
    }

    public static String setGameName(String gamename) {
        return "setgamename " + gamename;
    }

    public static String changeName(String name) {
        return "changename " + name;
    }

    public static String gameClosed() {
        return "gameclosed";
    }

    public static String closeRoom() {
        return "close";
    }

    public static String leaveRoom() {
        return "leave ";
    }

    public static String Launch() {
        return "gogogo";
    }

    public static String flipReadystatus() {
        return "flipready";
    }

    public static String Sendfile(String reciever, String filename, String sizeinbytes,String port) {
        return "SendFile" + INFORMATION_DELIMITER 
                + reciever + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + sizeinbytes+ INFORMATION_DELIMITER                 
                +port;
    }
}
