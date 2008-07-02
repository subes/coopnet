/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of Coopnet.

    Coopnet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Coopnet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient;

import coopnetclient.gamedatabase.GameDatabase;
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
    
    static{
        try{
            CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
            ENCODED_MESSAGE_DELIMITER = encoder.encode(CharBuffer.wrap(MESSAGE_DELIMITER)).array();
        }catch(CharacterCodingException ex){
            ex.printStackTrace();
        }
    }
    
    public static String AcceptTransfer(String sender, String filename) {
        return "AcceptFile" + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + Settings.getFiletTansferPort();
    }

    public static String RefuseTransfer(String sender, String filename) {
        return "RefuseFile" + INFORMATION_DELIMITER 
                + sender + INFORMATION_DELIMITER 
                + filename;
    }

    public static String CancelTransfer(String sender, String filename) {
        return "CancelFile" + INFORMATION_DELIMITER
                + sender + INFORMATION_DELIMITER
                + filename;
    }

    public static String SendMod(String mod) {
        return "setmod " + mod;
    }

    public static String createRoom(String name, String modIndex, String password, String limit, boolean compatible) {
        return "create" + INFORMATION_DELIMITER 
                + "NAME" + INFORMATION_DELIMITER + name + INFORMATION_DELIMITER 
                + "PASSWORD" + INFORMATION_DELIMITER + password + INFORMATION_DELIMITER 
                + "LIMIT" + INFORMATION_DELIMITER + limit + INFORMATION_DELIMITER 
                + "COMPATIBLE" + INFORMATION_DELIMITER 
                + (compatible ? "true" : "false") + INFORMATION_DELIMITER 
                + "HAMACHI" + INFORMATION_DELIMITER + Client.hamachiAddress() 
                + INFORMATION_DELIMITER 
                + "MOD" + INFORMATION_DELIMITER + modIndex 
                + INFORMATION_DELIMITER + "END";
    }

    public static String SendPort(int port) {
        return "setport " + port;
    }

    public static String SetSleep(boolean enabled) {
        return "setsleep " + String.valueOf(enabled);
    }

    public static String joinRoom(String hostname, String password) {
        return "join " + hostname + INFORMATION_DELIMITER + password;
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

    public static String Sendfile(String reciever, String filename, String sizeinbytes) {
        return "SendFile" + INFORMATION_DELIMITER 
                + reciever + INFORMATION_DELIMITER 
                + filename + INFORMATION_DELIMITER 
                + sizeinbytes;
    }
}
