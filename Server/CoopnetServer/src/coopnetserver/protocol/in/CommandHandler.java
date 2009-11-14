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
package coopnetserver.protocol.in;

import coopnetserver.enums.ClientProtocolCommands;
import coopnetserver.protocol.out.Protocol;
import coopnetserver.protocol.*;
import coopnetserver.*;
import coopnetserver.data.room.Room;
import coopnetserver.data.player.Player;
import coopnetserver.data.player.PlayerData;
import coopnetserver.data.channel.ChannelData;
import coopnetserver.data.channel.Channel;
import coopnetserver.data.connection.Connection;
import coopnetserver.enums.TaskTypes;
import coopnetserver.exceptions.VerificationException;
import coopnetserver.utils.Logger;
import coopnetserver.utils.Verification;
import coopnetserver.utils.Database;
import coopnetserver.utils.MailSender;
import coopnetserver.utils.PasswordEncrypter;
import java.sql.SQLException;
import java.util.Arrays;
import javax.mail.MessagingException;

/**
 * analises and executes incomming commands
 */
public class CommandHandler {
    
    public static void executeCommand(Connection con, String[] data) throws SQLException, MessagingException, VerificationException {
        Player thisPlayer = con.getPlayer();
        
        if (data[0].equals(Protocol.HEARTBEAT)) {
            Logger.logInTraffic(data, con);
            return;
        }
        
        try{
            ClientProtocolCommands command = null;
            try{
                command = ClientProtocolCommands.values()[Integer.parseInt(data[0])];
            } catch(Exception e){
                //DROP because server accepts no other messages
                Logger.logInTraffic(data, con);
                TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
                
                String exceptionString = "Client sent unknown command!" +
                        "\n\tData (length="+data.length+"):";
                for(int i = 0; i < data.length; i++){
                    exceptionString += "\n\t\t"+i+": "+data[i];
                }
                
                throw new VerificationException(exceptionString);
            }
            
            String[] information = new String[data.length-1];
            System.arraycopy(data, 1, information, 0, information.length);    

            Logger.logInTraffic(command, information, con);

            if (thisPlayer == null) {
                switch(command){
                    case NEW_PLAYER:
                        //registration                        
                        CommandMethods.createPlayer(con, information);
                        return;
                    case LOGIN:
                        //login                        
                        CommandMethods.login(con, information);
                        return;
                    case QUIT:
                        //Put connection off
                        TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
                        return;
                    case PASSWORD_RECOVERY:
                        String[] playerData = Database.getPlayerDataByName(information[0]);
                        
                        if(playerData == null){
                            return;
                        }
                        
                        String email = playerData[4];
                        if (email.equals(information[1])) {                            
                            String newPassword = getRandomString(10);
                            long pid = Long.parseLong(playerData[0]);
                            Database.updatePassword(pid, PasswordEncrypter.encryptPassword(newPassword));
                            String loginname = playerData[1];
                            MailSender.sendPasswordRecoveryMail(loginname, email, newPassword);                                      
                        }
                        return;
                    case CLIENTVERSION:
                        con.setClientVersion(information[0]);
                        break;
                    default:
                        //DROP because server accepts no other messages
                        TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
                        //this might also happen when the user is logged off 
                        //before the slow-task-proccessor executes it
                        //so this shouldnt be mailed as a server bug
                        /*String exceptionString = "Client sent a prohibitted command: "+ command.toString() +
                                "\n\tData (length="+data.length+"):";
                        for(int i = 0; i < data.length; i++){
                            exceptionString += "\n\t\t"+i+": "+data[i];
                        }
                        throw new VerificationException(exceptionString);*/
                }
            } else {
                switch(command){
                    case CLIENTVERSION:
                        con.setClientVersion(information[0]);
                        return;
                    case LOGIN:  //if the login takes long the user might press the button twice
                        //without this it will break the connection
                        //so we should just ignore any further logins
                        return;
                    case JOIN_CHANNEL:
                        Channel requestedchannel = null;
                        requestedchannel = ChannelData.getChannel(information[0]);
                        if (requestedchannel != null) {
                            if (!(Arrays.asList(requestedchannel.getPlayersInChannel()).contains(thisPlayer))) {
                                thisPlayer.joinChannel(requestedchannel);
                                CommandMethods.refreshRoomsAndPlayers(thisPlayer, requestedchannel);
                            }
                        }
                        return;
                    case QUIT:
                        TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
                        return;
                    case LEAVE_CHANNEL:
                        Channel currentChannel = ChannelData.getChannel(information[0]);
                        if (currentChannel != null) {
                            currentChannel.removePlayer(thisPlayer);
                        }
                        return;
                    case CHAT_MAIN:
                        if (thisPlayer.getSleepMode() == true) {
                            thisPlayer.setSleepMode(false);
                        }
                        thisPlayer.setAway(false);
                        Protocol.mainChat(ChannelData.getChannel(information[0]), thisPlayer, information[1]);
                        return;
                    case CHAT_ROOM:
                        if (thisPlayer.getCurrentRoom() != null) {
                            Protocol.roomChat(thisPlayer.getCurrentRoom(), thisPlayer, information[0]);
                            thisPlayer.setAway(false);
                        }
                        return;
                    case NUDGE:
                        Player nudgeTo = PlayerData.searchByName(information[0]);
                        if (nudgeTo != null) {
                            Protocol.nudge(nudgeTo.getConnection(), thisPlayer);
                            thisPlayer.setAway(false);
                        }
                        return;
                    case WHISPER:
                        Player whisperTo = PlayerData.searchByName(information[0]);
                        if(whisperTo != null){
                            Protocol.privateMessage(whisperTo.getConnection(), thisPlayer, information[1]);
                        }else{
                            Protocol.sendWhisperToOfflineUser(thisPlayer, information[0]);
                        }
                        thisPlayer.setAway(false);
                        return;
                    case KICK:
                        Player kickedone = PlayerData.searchByName(information[0]);
                        Player kicker = thisPlayer;
                        if (kickedone != null && kickedone.getCurrentRoom() != null && kicker != null && kicker.equals(kickedone.getCurrentRoom().getHost()) && !kickedone.equals(kickedone.getCurrentRoom().getHost())) {
                            Protocol.kick(kickedone.getConnection());
                            kickedone.getCurrentRoom().removePlayer(kickedone);
                            kickedone.setCurrentRoom(null);
                            Protocol.roomChat(kicker.getCurrentRoom(), null, kickedone.getLoginName()+" has been kicked!");
                        }
                        return;
                    case BAN:                        
                        Player bannedone = PlayerData.searchByName(information[0]);
                        if (bannedone != null && bannedone != thisPlayer) {
                            thisPlayer.ban(bannedone.getPid());
                        }else{
                            thisPlayer.ban(Database.getPID(information[0]));
                        }
                        Protocol.confirmBan(thisPlayer.getConnection(), information[0]);
                        return;
                    case UNBAN:
                        Player unbannedone = PlayerData.searchByName(information[0]);
                        if (unbannedone != null) {
                            thisPlayer.unBan(unbannedone.getPid());
                        }else{
                            thisPlayer.unBan(Database.getPID(information[0]));
                        }
                        Protocol.confirmUnBan(thisPlayer.getConnection(), information[0]);
                        return;
                    case MUTE:                        
                        Player mutedone = PlayerData.searchByName(information[0]);
                        if (mutedone != null) {
                            thisPlayer.mute(mutedone.getPid());
                        }else{
                            thisPlayer.mute(Database.getPID(information[0]));
                        }
                        Protocol.confirmMute(thisPlayer.getConnection(), information[0]);
                        return;
                    case UNMUTE:                        
                        Player unmutedone = PlayerData.searchByName(information[0]);
                        if (unmutedone != null) {
                            thisPlayer.unMute(unmutedone.getPid());
                        }else{
                            thisPlayer.unMute(Database.getPID(information[0]));
                        }
                        Protocol.confirmUnMute(thisPlayer.getConnection(), information[0]);
                        return;
                    case CREATE_ROOM:
                        CommandMethods.createRoom(thisPlayer, information);
                        return;
                    case JOIN_ROOM:
                        CommandMethods.joinRoom(thisPlayer, information);
                        return;
                    case JOIN_ROOM_BY_ID:
                        CommandMethods.joinRoom(thisPlayer, information);
                        return;
                    case LEAVE_ROOM:
                        if (thisPlayer.getCurrentRoom() != null) {
                            thisPlayer.getCurrentRoom().removePlayer(thisPlayer);
                            thisPlayer.setAway(false);
                        }                        
                        return;
                    case CLOSE_ROOM:
                        if (thisPlayer.getCurrentRoom() != null) {
                            thisPlayer.getCurrentRoom().close();
                            thisPlayer.setAway(false);
                        }
                        return;
                    case GAME_CLOSED:
                        Protocol.gameClosed(ChannelData.getChannel(information[0]), thisPlayer);
                        thisPlayer.setPlaying(false);                
                        thisPlayer.setSleepMode(false);
                        if(thisPlayer.getCurrentRoom() != null && thisPlayer.getCurrentRoom().isInstantLaunched()){
                            thisPlayer.getCurrentRoom().removePlayer(thisPlayer);
                        }
                        thisPlayer.sendMyContactStatusToMyContacts();
                        return;
                    case REFRESH_ROOMS_AND_PLAYERS:
                        CommandMethods.refreshRoomsAndPlayers(thisPlayer, ChannelData.getChannel(information[0]));
                        return;
                    case SET_GAMESETTING:
                        if (thisPlayer.getCurrentRoom()!= null && thisPlayer.getCurrentRoom().getHost().equals(thisPlayer)) {
                            thisPlayer.getCurrentRoom().setSetting(information[0], information[1]);
                        }
                        return;
                    case FLIP_READY:
                        thisPlayer.flipReady();
                        return;
                    case LAUNCH:
                        Room current = thisPlayer.getCurrentRoom();
                        if (current != null && current.getHost().equals(thisPlayer)) {
                            current.launch();                            
                        }
                        return;
                    case EDIT_PROFILE:
                        Protocol.editProfile(thisPlayer);
                        return;                    
                    case REQUEST_PROFILE:
                        Player viewed = PlayerData.searchByName(information[0]);
                        if (viewed == null) {
                            
                            String[] playerData = Database.getPlayerDataByName(information[0]);
                            
                            /* From Player.java
                            this.pid = Long.parseLong(playerData[0]);
                            this.loginName = playerData[1];
                            this.ingameName = playerData[2];
                            this.password = playerData[3];
                            this.email = playerData[4];                           
                            this.website = playerData[5];
                            this.country = playerData[6];
                            */
                            
                            Protocol.showProfile(
                                    thisPlayer.getConnection(),
                                    playerData[1],
                                    playerData[2],
                                    playerData[6],
                                    playerData[5]);
                        } else {
                            Protocol.showProfile(
                                    thisPlayer.getConnection(),
                                    viewed.getLoginName(),
                                    viewed.getIngameName(),
                                    viewed.getCountry(),
                                    viewed.getWebsite());
                        }
                        return;                    
                    case SAVE_PROFILE:                        
                        CommandMethods.saveProfile(con, thisPlayer, information);
                        return;
                    case SEND_FILE:
                        Player reciever = PlayerData.searchByName(information[0]);
                        if (reciever != null) {
                            Protocol.sendFile(reciever.getConnection(), thisPlayer, information[1], information[2], con.getIpAddress(), information[3]);
                        }
                        return;
                    case ACCEPT_FILE:
                        Player recv = PlayerData.searchByName(information[0]);
                        if (recv != null) {
                            Protocol.acceptFile(recv.getConnection(), thisPlayer, information[1], con.getIpAddress(), information[2], information[3]);
                        }
                        return;
                    case REFUSE_FILE:
                        Player recvv = PlayerData.searchByName(information[0]);
                        if (recvv != null) {
                            Protocol.refuseFile(recvv.getConnection(), thisPlayer, information[1]);
                        }
                        return;
                    case CANCEL_FILE:
                        Player recvvv = PlayerData.searchByName(information[0]);
                        if (recvvv != null) {
                            Protocol.cancelFile(recvvv.getConnection(), thisPlayer, information[1]);
                        }
                        return;
                    case TURN_AROUND_FILE:
                        Player turned = PlayerData.searchByName(information[0]);
                        if (turned != null) {
                            Protocol.turnAroundTransfer(turned.getConnection(), thisPlayer, information[1]);
                        }
                        return;
                    case SET_SLEEP:
                        thisPlayer.setSleepModeEnabled(Boolean.valueOf(information[0]));
                        return;
                    case CHANGE_PASSWORD:
                        if(!Verification.verifyPassword(information[1])){
                            throw new VerificationException("Failure on password! "+information[1]);
                        }

                        if (thisPlayer.getPassword().equals(information[0])) {
                            thisPlayer.setPassword(information[1]);
                            Protocol.confirmPasswordChange(con);
                        } else {
                            Protocol.errorIncorrectPassword(con);
                        }
                        return;
                    case INVITE_USER:
                        Player target = PlayerData.searchByName(information[0]);
                        if(target == null){
                            Protocol.sendWhisperToOfflineUser(thisPlayer, information[0]);
                            return;
                        }
                        if(thisPlayer.getCurrentRoom()!=null){
                            Protocol.inviteUser(target.getConnection(), thisPlayer, thisPlayer.getCurrentRoom());
                        } 
                        return;
                    case SEND_CONTACT_REQUEST:
                        Player sendTo = PlayerData.searchByName(information[0]);
                        long sendToID;
                        if(sendTo != null){
                            sendToID = sendTo.getPid();
                        }else{
                            sendToID = Long.valueOf(Database.getPlayerDataByName(information[0])[0]);
                        }
                        synchronized (thisPlayer.getContactList()) {
                            if (!thisPlayer.getContactList().isOnMyList(sendToID)) {
                                if (thisPlayer.getContactList().addPendingContact(sendToID)) {
                                    if (sendTo != null) {
                                        Protocol.sendContactRequest(sendTo.getConnection(), thisPlayer);
                                        sendTo.getContactList().addRequest(thisPlayer.getPid());
                                    }
                                    Protocol.acknowledgeContactRequest(con, null, information[0]);
                                }
                            }
                        }
                        return;
                    case ACCEPT_CONTACT_REQUEST:
                        Player accepted = PlayerData.searchByName(information[0]);
                        synchronized (thisPlayer.getContactList()) {
                            if (accepted != null) {
                                accepted.getContactList().contactAcceptedRequest(thisPlayer.getPid());
                                thisPlayer.getContactList().acceptRequest(accepted.getPid());
                                Protocol.sendRequestAcceptNotification(accepted.getConnection(), thisPlayer);
                                Protocol.sendContactStatus(accepted.getConnection(), thisPlayer, thisPlayer.getContactStatus());
                                Protocol.acknowledgeContactAccept(con, null, accepted.getLoginName());
                            } else {//even if accepted contact is offline we still msut do it
                                Long ID = Long.valueOf(Database.getPlayerDataByName(information[0])[0]); // idx 0 is the pid
                                Database.setContactAccepted(ID, thisPlayer.getPid());
                                thisPlayer.getContactList().acceptRequest(ID);
                                Protocol.acknowledgeContactAccept(con, null, Database.getLoginName(ID));
                            }
                        }
                        return;
                    case REFUSE_CONTACT_REQUEST:
                        Player refusedContact = PlayerData.searchByName(information[0]);
                        synchronized (thisPlayer.getContactList()) {
                            if (refusedContact != null) {
                                thisPlayer.getContactList().refuseRequest(refusedContact.getPid());
                                refusedContact.getContactList().contactRefusedRequest(thisPlayer.getPid());
                                Protocol.sendRequestRefuseNotification(refusedContact.getConnection(), thisPlayer);
                                Protocol.acknowledgeContactRefuse(con, null, refusedContact.getLoginName());
                            } else {//offline contact
                                Long ID = Long.valueOf(Database.getPlayerDataByName(information[0])[0]); // idx 0 is the pid
                                thisPlayer.getContactList().refuseRequest(ID);
                                Database.removeContact(ID, thisPlayer.getPid());
                                Protocol.acknowledgeContactRefuse(con, null, Database.getLoginName(ID));
                            }
                        }
                        return;
                    case CREATE_GROUP:
                        if(!Verification.verifyGroupName(information[0])){
                            throw new VerificationException("Failure on groupname! "+information[0]);
                        }
                        if(thisPlayer.getContactList().groupExists(information[0])){
                            throw new VerificationException("User wanted to create a group with a groupname that already exists! "+information[0]);
                        }
                        synchronized (thisPlayer.getContactList()) {
                            thisPlayer.getContactList().createGroup(information[0]);
                            Protocol.acknowledgeGroupCreate(con, null, information[0]);
                        }
                        return;
                    case RENAME_GROUP:
                        if(!Verification.verifyGroupName(information[1])){
                            throw new VerificationException("Failure on groupname! "+information[1]);
                        }
                        synchronized (thisPlayer.getContactList()) {
                            thisPlayer.getContactList().renameGroup(information[0], information[1]);
                            Protocol.acknowledgeGroupRename(con, null, information[0], information[1]);
                        }
                        return;
                    case DELETE_CONTACT:
                        Player contact = PlayerData.searchByName(information[0]);
                        synchronized (thisPlayer.getContactList()) {
                            if (contact != null) {
                                Long ID = contact.getPid();
                                thisPlayer.getContactList().removeContact(ID);//remove from the contactlsit
                                contact.getContactList().removeContactWhoKnowsMe(thisPlayer.getPid());//remove notification of contacts status changes
                            } else {//contact is logged off, but we must still remove contact
                                Long ID = Long.valueOf(Database.getPlayerDataByName(information[0])[0]); // idx 0 is the pid
                                thisPlayer.getContactList().removeContact(ID);//remove from the contactlsit
                            }
                        }
                        Protocol.acknowledgeContactRemove(con, null, information[0]);
                        return;
                    case DELETE_GROUP:
                        synchronized (thisPlayer.getContactList()) {
                            thisPlayer.getContactList().deleteGroup(information[0]);
                            Protocol.acknowledgeGroupDelete(con, null, information[0]);
                        }
                        return;
                    case MOVE_TO_GROUP:
                        Player moved = PlayerData.searchByName(information[0]);
                        synchronized (thisPlayer.getContactList()) {
                            if (moved != null) {
                                thisPlayer.getContactList().moveContact(moved.getPid(), information[1]);
                            } else {
                                Long ID = Long.valueOf(Database.getPlayerDataByName(information[0])[0]);
                                thisPlayer.getContactList().moveContact(ID, information[1]);
                            }
                        }
                        Protocol.acknowledgeContactMove(con, null, information[0], information[1]);
                        return;
                    case REFRESH_CONTACTS:
                        synchronized (thisPlayer.getContactList()) {
                            Protocol.sendContactData(thisPlayer, Boolean.parseBoolean(information[0]));
                        }
                        return;
                    case SETAWAYSTATUS:
                        thisPlayer.setAway(true);
                        break;
                    case UNSETAWAYSTATUS:
                        thisPlayer.setAway(false);
                        break;
                    default:
                        //DROP
                        TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
                        String exceptionString = "The command enum has a value that was not handled! "+command.toString() +
                                "\n\tData (length="+data.length+"):";
                        for(int i = 0; i < data.length; i++){
                            exceptionString += "\n\t\t"+i+": "+data[i];
                        }
                        throw new VerificationException(exceptionString);
                }
            }
        }catch(ArrayIndexOutOfBoundsException exc){
            //DROP because server accepts no other messages
            Logger.log(exc, con);
            TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,con,new String[] {} ));
            throw new VerificationException("Player sent command with missing information!");
        }
        catch(SQLException sqlexc){
            Protocol.sendCrippledModeNotification(con);
            Logger.log(sqlexc, con);
            return;
        }
    }
    
    private static String getRandomString(int length){
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int nextChar = (int) (Math.random() * 62);
            if (nextChar < 10) //0-9
            {
                s.append(nextChar);
            } else if (nextChar < 36) //a-z
            {
                s.append((char) (nextChar - 10 + 'a'));
            } else {
                s.append((char) (nextChar - 36 + 'A'));
            }
        }
        return s.toString();
    }
    
}
