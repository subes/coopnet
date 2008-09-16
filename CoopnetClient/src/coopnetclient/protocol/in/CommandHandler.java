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

package coopnetclient.protocol.in;

import coopnetclient.protocol.out.Protocol;
import coopnetclient.protocol.*;
import coopnetclient.*;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.enums.ServerProtocolCommands;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.protocol.out.Message;
import coopnetclient.utils.Verification;
import coopnetclient.utils.Settings;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.utils.FrameIconFlasher;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.FileDownloader;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.launcher.TempGameSettings;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * Searches for known command, gets the parameters and executes it
 * 
 */
public class CommandHandler {
    
    public static void execute(String input) {        
        if (input == null) {
            return;
        }
        String currentchannel = null;
        TrafficLogger.append("IN: " + input); // does nothing if its not initialized
        if (Globals.getDebug()) {
            System.out.println("[T]\tIN: " + input);
        }

        //Heartbeat
        if (input.equals(Protocol.HEARTBEAT)) {
            new Message(Protocol.HEARTBEAT);
        }else{
             if (!Globals.getLoggedInStatus()) {
                if (input.startsWith(ServerProtocolCommands.OK_LOGIN)) {
                    //logged in, start the client
                    Globals.setLoggedInStatus(true);
                    TabOrganizer.closeLoginPanel();
                    Protocol.setSleep(Settings.getSleepEnabled());
                } else if (input.startsWith(ServerProtocolCommands.LOGIN_INCORRECT)) {
                    TabOrganizer.getLoginPanel().showError("Wrong username/password, please try again!", Color.red);
                } else if (input.startsWith("Already logged in!")) {
                    TabOrganizer.getLoginPanel().showError("Error: "+input, Color.red);   
                } else if (input.startsWith(ServerProtocolCommands.OK_REGISTER)) {
                    TabOrganizer.getLoginPanel().showError("Registration successful!", Color.green.darker());   
                    JOptionPane.showMessageDialog(Globals.getClientFrame(), "<html><b>Thank you for registering!</b>\n" +
                            "If you want to be able to do password recovery in the future,\n" +
                            "please fill in a valid E-Mail address in your player profile.\n" +
                            "\n" +
                            "You may login now.", "Successfully registered", JOptionPane.INFORMATION_MESSAGE);
                } else if (input.startsWith(ServerProtocolCommands.LOGINNAME_IN_USE)) {
                    TabOrganizer.getLoginPanel().showError("Error: "+input, Color.red);   
                }

            } else {//logged-in commands

                if (input.startsWith(ServerProtocolCommands.ON_CHANNEL)) {
                    currentchannel = input.substring(3, 6);
                    currentchannel = GameDatabase.getGameName(currentchannel); //decode ID
                    input = input.substring(7);
                }

                if (input.startsWith(ServerProtocolCommands.SET_GAMESETTING)) {
                    String[] setting = input.substring(14).split(Protocol.INFORMATION_DELIMITER);
                    TempGameSettings.setGameSetting(setting[0],setting[1],false);                
                } else if (input.startsWith(ServerProtocolCommands.JOIN_CHANNEL)) {
                    String tmp = input.substring(12);
                    GameDatabase.load(tmp,GameDatabase.datafilepath);
                    TabOrganizer.openChannelPanel(tmp);
                } else if (input.startsWith(ServerProtocolCommands.MUTE_BAN_LIST)) {
                    String[] muteAndBan = input.substring(14).split("\n");
                    String[] mutedUserNames = muteAndBan.length>0 ? muteAndBan[0].split(Protocol.INFORMATION_DELIMITER) : new String[]{};
                    String[] bannedUserNames = muteAndBan.length>1 ?  muteAndBan[1].split(Protocol.INFORMATION_DELIMITER) : new String[]{};
                    for(String username : mutedUserNames){
                        MuteBanList.mute(username);
                    }
                    for(String username : bannedUserNames){
                        MuteBanList.ban(username);
                    }
                } else if (input.startsWith(ServerProtocolCommands.NUDGE)) {
                    String tmp = input.substring(6);
                    new FrameIconFlasher(Globals.getClientFrame(), "data/icons/nudge.png", tmp + " sent you a nudge!");
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", tmp + " sent you a nudge!", ChatStyles.SYSTEM,false);
                    SoundPlayer.playNudgeSound();
                } else if (input.startsWith(ServerProtocolCommands.ERROR)) {
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", input.substring(6), ChatStyles.SYSTEM,true);
                } else 
                //mainchat command
                if (input.startsWith(ServerProtocolCommands.CHAT_MAIN)) {
                    String[] tmp = input.substring(5).split(Protocol.INFORMATION_DELIMITER);
                    if (tmp.length == 1) {
                        return;
                    }
                    Globals.getClientFrame().printMainChatMessage(currentchannel, tmp[0], tmp[1], ChatStyles.USER);
                    if (Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                } else 
                //adds a palyer to the playerlist in main window
                if (input.startsWith(ServerProtocolCommands.ADD_TO_PLAYERS)) {
                    Globals.getClientFrame().addPlayerToChannel(currentchannel, input.substring(13));
                } else 
                //prints message to the room-chat
                if (input.startsWith(ServerProtocolCommands.CHAT_ROOM)) {
                    String[] tmp = input.substring(5).split(Protocol.INFORMATION_DELIMITER);
                    TabOrganizer.getRoomPanel().chat(tmp[0], tmp[1], ChatStyles.USER);
                } else 
                //the server accepted the join request, must create a new room tab now in client mode
                if (input.startsWith(ServerProtocolCommands.JOIN_ROOM)) {
                    String[] tmp = input.split(Protocol.INFORMATION_DELIMITER); // ip,compatibility,hamachiip, maxplayers, modindex, hostname
                    TabOrganizer.openRoomPanel(false, currentchannel, tmp[5], tmp[1], tmp[2].equals("true"), tmp[3], new Integer(tmp[4]), tmp[6]);
                } else 
                if (input.startsWith(ServerProtocolCommands.REQUEST_PASSWORD)) {
                    String ID = input.split(Protocol.INFORMATION_DELIMITER)[1];
                    Globals.openJoinRoomPasswordFrame(ID);
                } else 
                if (input.startsWith(ServerProtocolCommands.WRONG_ROOM_PASSWORD)) {                
                    Globals.showWrongPasswordNotification();
                } else 
                //the server accepted the room creation request, must create a new room tab in server mode
                if (input.startsWith(ServerProtocolCommands.CREATE_ROOM)) {
                    String[] tmp = input.substring(7).split(Protocol.INFORMATION_DELIMITER);
                    boolean compatible = Boolean.valueOf(tmp[1]);
                    int maxplayers = Integer.valueOf(tmp[2]);
                    String modindex = tmp.length>3?tmp[3]:"";
                    TabOrganizer.openRoomPanel(true, currentchannel, modindex, "", compatible, "", maxplayers, Globals.getThisPlayer_loginName());
                } else 
                //server accepted leave request, must delete room tab
                if (input.startsWith(ServerProtocolCommands.LEAVE_ROOM)) {
                    TabOrganizer.closeRoomPanel();
                } else 
                //the owner of the room closed it, must remove from room list
                if (input.startsWith(ServerProtocolCommands.REMOVE_ROOM)) {
                    Globals.getClientFrame().removeRoomFromTable(currentchannel, input.substring(11));
                } else             
                //the currently joined room was closed, must delete room tab
                if (input.startsWith(ServerProtocolCommands.CLOSE_ROOM)) {
                    TabOrganizer.closeRoomPanel();
                    Globals.getClientFrame().printMainChatMessage(currentchannel, "SYSTEM", "The Room has been closed!", ChatStyles.SYSTEM);

                } else 
                //been kicked of the current room, must delete room tab
                if (input.startsWith(ServerProtocolCommands.KICKED)) {
                    TabOrganizer.closeRoomPanel();
                    Globals.getClientFrame().printMainChatMessage(currentchannel, "SYSTEM", "You have been kicked by the host!", ChatStyles.SYSTEM);
                } else 
                //add a player to the rooms player list
                if (input.startsWith(ServerProtocolCommands.ADD_MEMBER_TO_ROOM)) {
                    TabOrganizer.getRoomPanel().addmember(input.substring(10));
                } else 
                // remove a player from the rooms player list
                if (input.startsWith(ServerProtocolCommands.REMOVE_MEMBER_FROM_ROOM)) {
                    TabOrganizer.getRoomPanel().removeMember(input.substring(13));
                } else 
                //add a new room to the room list
                if (input.startsWith(ServerProtocolCommands.ADD_ROOM)) {
                    String[] tmp = input.split(Protocol.INFORMATION_DELIMITER);//0adroom  1roomname 2hostname 3maxplayers 4type
                    Globals.getClientFrame().addRoomToTable(currentchannel, tmp[1], tmp[2], new Integer(tmp[3]), new Integer(tmp[4]));
                    if (Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                } else 
                //remove a user from the player list in main window
                if (input.startsWith(ServerProtocolCommands.LEFT_CHANNEL)) {
                    Globals.getClientFrame().removePlayerFromChannel(currentchannel, input.substring(7));
                } else 
                //show a private message
                if (input.startsWith(ServerProtocolCommands.CHAT_PRIVATE)) {
                    String[] tmp = input.substring(8).split(Protocol.INFORMATION_DELIMITER);//0. sender 1.message
                    Globals.getClientFrame().printPrivateChatMessage(tmp[0], tmp[1]);
                } else 
                //print a message from the server
                if (input.startsWith(ServerProtocolCommands.SERVER_SHUTTING_DOWN)) {
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", input.substring(5), ChatStyles.SYSTEM,true);
                } else 
                if (input.startsWith(ServerProtocolCommands.ECHO_NO_SUCH_PLAYER)) {
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", input.substring(5), ChatStyles.SYSTEM,true);
                } else
                    /*
                        ECHO_BANNED,
                        ECHO_UNBANNED,
                        ECHO_MUTED,
                        ECHO_UNMUTED,
                     * */
                //set players(name in parameter) ready status to not ready
                if (input.startsWith(ServerProtocolCommands.NOT_READY_STATUS)) {
                    TabOrganizer.getRoomPanel().unReadyPlayer(input.substring(8));
                } else 
                //set players(name in parameter) ready status to ready
                if (input.startsWith(ServerProtocolCommands.READY_STATUS)) {
                    TabOrganizer.getRoomPanel().readyPlayer(input.substring(6));
                } else 
                //set players(name in parameter) status to playing
                if (input.startsWith(ServerProtocolCommands.ROOM_PLAYING_STATUS)) {
                    TabOrganizer.getRoomPanel().setPlaying(input.substring(8));
                } else 
                //set the players(name in parameter) status to not playing
                if (input.startsWith(ServerProtocolCommands.GAME_CLOSED)) {
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().gameClosed(input.substring(11));
                    }
                    Globals.getClientFrame().gameClosed(currentchannel, input.substring(11));
                    Globals.getClientFrame().repaint();
                } else 
                //launch the game if not running already
                if (input.startsWith(ServerProtocolCommands.LAUNCH)) {
                    TabOrganizer.getRoomPanel().launch();
                } else 
                if (input.startsWith(ServerProtocolCommands.CHANNEL_PLAYING_STATUS)) {
                    String host = input.substring(17);
                    Globals.getClientFrame().setPlayingStatus(currentchannel, host);
                } else 
                //confirmation message from the server. the password was changed successfully
                if (input.startsWith(ServerProtocolCommands.PASSWORD_CHANGED)) {
                    Globals.closeChangePasswordFrame();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Password changed!", ChatStyles.SYSTEM,false);
                } else 
                //confirmation message from the server. the name was changed successfully(if changed)
                if (input.startsWith(ServerProtocolCommands.PROFILE_SAVED)) {
                    Globals.closeEditProfileFrame();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Profile saved!", ChatStyles.SYSTEM,false);
                } else 
                //show the profile-editing window with the data in parameters
                if (input.startsWith(ServerProtocolCommands.EDIT_PROFILE)) {
                    String[] tmp = input.split(Protocol.INFORMATION_DELIMITER); //0 showprofile 1 name 2 ingamename 3 email 4 ispublic 5 country 6 webpage
                    Globals.openEditProfileFrame(tmp[1],
                            tmp[2],
                            tmp[3],
                            tmp[4],
                            tmp[5],
                            tmp[6]);
                } else 
                //show the profile view window with the data in parameters
                if (input.startsWith(ServerProtocolCommands.SHOW_PROFILE)) {
                    String[] tmp = input.split(Protocol.INFORMATION_DELIMITER); //0 showprofile 1 name 2 email 3 country 4 webpage
                    Globals.openShowProfileFrame(tmp[1],
                            tmp[2],
                            tmp[3],
                            tmp[4]);
                } else 
                //add the player in parameter to the player-list of the room in parameter (shows as tooltiptext)
                if (input.startsWith(ServerProtocolCommands.JOINED_ROOM)) {
                    String[] tmp = input.substring(7).split(Protocol.INFORMATION_DELIMITER); //0 rooms hosts name 1 playername
                    Globals.getClientFrame().addPlayerToRoom(currentchannel, tmp[0], tmp[1]);
                } else 
                //remove player in parameter from the player-list of the room in parameter (shows as tooltiptext)
                if (input.startsWith(ServerProtocolCommands.LEFT_ROOM)) {
                    String[] tmp = input.substring(9).split(Protocol.INFORMATION_DELIMITER); //0 rooms hosts name 1 playername
                    Globals.getClientFrame().removePlayerFromRoom(currentchannel, tmp[0], tmp[1]);
                } else 
                //set the players in-game name
                if (input.startsWith(ServerProtocolCommands.INGAMENAME)) {
                    Globals.setThisPlayer_inGameName(input.substring(9));
                } else 
                //a player changed its name, msut update in player list and room list
                if (input.startsWith(ServerProtocolCommands.UPDATE_PLAYERNAME)) {
                    String[] tmp = input.substring(11).split(Protocol.INFORMATION_DELIMITER);      // 0 oldname 1 new name
                    Globals.getClientFrame().updatePlayerName(currentchannel, tmp[0], tmp[1]);
                    //update global if this name changes
                    if(Globals.getThisPlayer_loginName().equals(tmp[0]) ){
                        Globals.setThisPlayer_loginName(tmp[1]);
                    }else{
                        Globals.getClientFrame().printToVisibleChatbox("SYSTEM", tmp[0] + " is now known as " + tmp[1], ChatStyles.SYSTEM,false);
                    }
                    Globals.getContactList().updateName(tmp[0], tmp[1]);
                    Globals.getClientFrame().repaint();
                } else if (input.startsWith(ServerProtocolCommands.SENDING_FILE)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2file 3size 4 ip 5 port
                    TabOrganizer.openFileTransferReceivePanel(tmp[1], tmp[3], tmp[2],tmp[4],tmp[5]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", tmp[1] + " wants to send you a file!", ChatStyles.SYSTEM,false);
                } else if (input.startsWith(ServerProtocolCommands.ACCEPTED_FILE)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//0command 1reciever  2filename 3 ip 4 port 5 firstbyte
                    Globals.getClientFrame().startSending(tmp[3], tmp[1], tmp[2], tmp[4], new Long(tmp[5]));
                } else if (input.startsWith(ServerProtocolCommands.REFUSED_FILE)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1reciever  2filename
                    Globals.getClientFrame().refusedTransfer(tmp[1], tmp[2]);
                } else if (input.startsWith(ServerProtocolCommands.CANCELED_FILE)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2filename
                    Globals.getClientFrame().cancelledTransfer(tmp[1], tmp[2]);
                } else
                    //turn around transfer connection direction
                    if (input.startsWith(ServerProtocolCommands.TURN_AROUND_FILE)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2filename
                    Globals.getClientFrame().turnAroundTransfer(tmp[1], tmp[2]);
                }// contact list commands
                    else if (input.startsWith(ServerProtocolCommands.CONTACT_REQUESTED)) {
                    String name = input.substring(15);
                    Globals.getContactList().addContact(name, "", ContactListElementTypes.PENDING_REQUEST);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", name +" wants to add you to his/her contactlist", ChatStyles.SYSTEM,true);
                } else if (input.startsWith(ServerProtocolCommands.SET_CONTACTSTATUS)) {
                    String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1contact  2status
                    ContactListElementTypes status = null;
                    String name = tmp[1];
                    int statuscode = Integer.valueOf(tmp[2]);
                    status = ContactListElementTypes.values()[statuscode];
                    Globals.getContactList().setStatus(name, status);
                     //notifications                 
                     switch(status){
                         case OFFLINE:
                             if(Settings.getContactStatusChangeSoundNotification()){
                                 //TODO play sound
                             }
                             if(Settings.getContactStatusChangeTextNotification()){
                                 Globals.getClientFrame().printToVisibleChatbox("SYSTEM", name +" is offline", ChatStyles.SYSTEM,false);
                             }
                             break;
                         case CHATTING: 
                             if(Settings.getContactStatusChangeSoundNotification()){
                                 //TODO play sound
                             }
                             if(Settings.getContactStatusChangeTextNotification()){
                                 Globals.getClientFrame().printToVisibleChatbox("SYSTEM", name +" is online", ChatStyles.SYSTEM,false);
                             }
                             break;
                     }
                } else if (input.startsWith(ServerProtocolCommands.ACCEPTED_CONTACT_REQUEST)) {
                    String name = input.substring(15);
                    Globals.getContactList().setStatus(name,ContactListElementTypes.OFFLINE);
                } else if (input.startsWith(ServerProtocolCommands.REFUSED_CONTACT_REQUEST)) {
                    String name = input.substring(14);
                    Globals.getContactList().removecontact(name);
                } else if (input.startsWith(ServerProtocolCommands.CONTACT_LIST)) {
                    String data = input.substring(12);
                    Globals.getContactList().buildFrom(data);
                }             
                    else 
                    if(input.startsWith(ServerProtocolCommands.INSTANT_LAUNCH)){
                        final String tmp[] = input.substring(7).split(Protocol.INFORMATION_DELIMITER);
                        new Thread(){
                            public void run(){
                                Client.initInstantLaunch(tmp[0], GameDatabase.getModByIndex(tmp[0], new Integer(tmp[1])),tmp[2], new Integer(tmp[3]), tmp[4].equals("true"),false);
                                Client.instantLaunch(tmp[0]);
                            }
                        }.start();                    
                    }

            //else if(input.startsWith("")){	NEW COMMANDS	}
            }
        }        
    }
}
