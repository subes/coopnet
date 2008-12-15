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
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.ServerProtocolCommands;
import coopnetclient.frames.GameSettingsFrame;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.protocol.out.Message;
import coopnetclient.utils.Settings;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.utils.FrameIconFlasher;
import coopnetclient.utils.Logger;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.launcher.TempGameSettings;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * Searches for known command, gets the parameters and executes it
 * 
 */
public class CommandHandler {

    public static void execute(String[] data) {
        ServerProtocolCommands command = null;

        //Answer heartbeat
        if (data[0].equals(Protocol.HEARTBEAT)) {
            Logger.logInTraffic(data);
            new Message(Protocol.HEARTBEAT);
            return;
        }

        try {
            command = ServerProtocolCommands.values()[Integer.parseInt(data[0])];
        } catch (Exception e) {
            Logger.logInTraffic(data);
            Logger.log(LogTypes.ERROR, "Server sent an unknown command!");
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Unknown command recieved! Please make sure you use the latest client!", ChatStyles.SYSTEM, true);
            return;
        }

        String[] information = new String[data.length - 1];
        System.arraycopy(data, 1, information, 0, information.length);

        Logger.logInTraffic(command, information);

        if (!Globals.getLoggedInStatus()) {//not-logged-in commands
            switch (command) {
                case OK_LOGIN:
                    Globals.setLoggedInStatus(true);
                    Globals.setThisPlayer_loginName(information[0]);
                    TabOrganizer.closeLoginPanel();
                    Protocol.setSleep(Settings.getSleepEnabled());
                    Protocol.refreshContacts();
                    String s = coopnetclient.utils.Settings.getHomeChannel();
                    if (s.length() > 0) {
                        Protocol.joinChannel(s);
                    }
                    break;
                case LOGIN_INCORRECT:
                    TabOrganizer.getLoginPanel().showError("Wrong username/password, please try again!", Color.red);
                    TabOrganizer.getLoginPanel().enableButtons();
                    break;
                case OK_REGISTER:
                    TabOrganizer.closeRegisterPanel();
                    TabOrganizer.openLoginPanel();
                    JOptionPane.showMessageDialog(Globals.getClientFrame(), "<html><b>Thank you for registering!</b>\n" +
                            "You may login now.", "Successfully registered", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case LOGINNAME_IN_USE:
                    TabOrganizer.getRegisterPanel().showLoginNameUsedError();
                    TabOrganizer.getRegisterPanel().enableButtons();
                    break;
                case CRIPPLED_SERVER_MODE:
                    JOptionPane.showMessageDialog(Globals.getClientFrame(), "The server is running in maintenance mode,\nlogging in and registering is impossible!\nPlease try again later.", "Server Maintenance", JOptionPane.ERROR_MESSAGE);
                    if (TabOrganizer.getLoginPanel() != null) {
                        TabOrganizer.getLoginPanel().enableButtons();
                    }
                    if (TabOrganizer.getRegisterPanel() != null) {
                        TabOrganizer.getRegisterPanel().enableButtons();
                    }
                    break;
                case SERVER_SHUTTING_DOWN:
                    Client.disconnect();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Server is shutting down!", ChatStyles.SYSTEM, true);
                    break;
            }
        } else {//logged-in commands
            switch (command) {
                case CHAT_MAIN:
                    if (information.length < 2) {
                        return;
                    }
                    Globals.getClientFrame().printMainChatMessage(
                            GameDatabase.getGameName(information[0]) //aka currentchannel
                            , information[1], information[2], ChatStyles.USER);
                    if (Settings.getSleepEnabled() && Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                    break;
                case CHAT_ROOM:
                    if (information.length < 2) {
                        return;
                    }
                    TabOrganizer.getRoomPanel().chat(information[0], information[1], ChatStyles.USER);
                    break;
                case ADD_TO_PLAYERS:
                    Globals.getClientFrame().addPlayerToChannel(GameDatabase.getGameName(information[0]), information[1]);
                    break;
                case SET_GAMESETTING:
                    if(information[0].equals("map")){
                        TempGameSettings.setMap(information[1]);
                    }else{
                        TempGameSettings.setGameSetting(information[0], information[1], false);
                    }
                    GameSettingsFrame gf = Globals.getGameSettingsFrame();
                    if(gf!= null){
                        gf.updateValues();
                    }
                    break;
                case JOIN_CHANNEL:
                    GameDatabase.load(GameDatabase.getGameName(information[0]), GameDatabase.dataFilePath);
                    TabOrganizer.openChannelPanel(GameDatabase.getGameName(information[0]));
                    break;
                case JOIN_ROOM:
                    TabOrganizer.openRoomPanel(false,
                            GameDatabase.getGameName(information[0]), //channel
                            information[5],//modindex
                            information[1],//ip
                            information[2].equals("true"),//compatible
                            information[3],//hamachip
                            new Integer(information[4]),//maxplayers
                            information[6],//hostname
                            information[7],//roomname
                            information[8],//roomID
                            information[9]); //password
                    break;
                case MUTE_BAN_LIST:
                    int i = 0;
                    for (; i < information.length && !information[i].equals(""); i++) {
                        MuteBanList.mute(information[i]);
                    }
                    i++;
                    for (; i < information.length; i++) {
                        MuteBanList.ban(information[i]);
                    }
                    break;
                case NUDGE:
                    FrameIconFlasher.flash("data/icons/nudge.png", information[0] + " sent you a nudge!", false);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " sent you a nudge!", ChatStyles.SYSTEM, false);
                    SoundPlayer.playNudgeSound();
                    break;
                case ERROR_YOU_ARE_BANNED:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "You are banned by the rooms host!", ChatStyles.SYSTEM, true);
                    break;
                case ERROR_ROOM_IS_FULL:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "The room is full!", ChatStyles.SYSTEM, true);
                    break;
                case ERROR_ROOM_DOES_NOT_EXIST:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "The room doesn't exist", ChatStyles.SYSTEM, true);
                    break;
                case ERROR_LOGINNAME_IS_ALREADY_USED:
                    Globals.getEditProfileFrame().loginAlreadyUsed();
                    break;
                case REQUEST_PASSWORD:
                    Globals.openJoinRoomPasswordFrame(information[0]);
                    break;
                case WRONG_ROOM_PASSWORD:
                    Globals.showWrongPasswordNotification();
                    break;
                case CREATE_ROOM:
                    TabOrganizer.openRoomPanel(true,
                            GameDatabase.getGameName(information[0]),
                            information[3],//modindex
                            "",//ip
                            information[1].equals("true"),//compatible
                            "",//hamachi ip
                            new Integer(information[2]),//maxplayers
                            Globals.getThisPlayer_loginName(),
                            information[4],//roomname
                            information[5],//roomid
                            information[6]);//password
                    break;
                case LEAVE_ROOM:
                    TabOrganizer.closeRoomPanel();
                    break;
                case REMOVE_ROOM:
                    Globals.getClientFrame().removePlayerFromRoom(GameDatabase.getGameName(information[0]), information[1], information[1]);
                    Globals.getClientFrame().removeRoomFromTable(GameDatabase.getGameName(information[0]), information[1]);
                    break;
                case CLOSE_ROOM:
                    TabOrganizer.closeRoomPanel();
                    Globals.getClientFrame().printMainChatMessage(GameDatabase.getGameName(information[0]), "SYSTEM", "The Room has been closed!", ChatStyles.SYSTEM);
                    break;
                case KICKED:
                    TabOrganizer.closeRoomPanel();
                    Globals.getClientFrame().printMainChatMessage(TabOrganizer.getRoomPanel().gameName, "SYSTEM", "You have been kicked by the host!", ChatStyles.SYSTEM);
                    break;
                case ADD_MEMBER_TO_ROOM:
                    TabOrganizer.getRoomPanel().addmember(information[0]);
                    break;
                case REMOVE_MEMBER_FROM_ROOM:
                    TabOrganizer.getRoomPanel().removeMember(information[0]);
                    break;
                case ADD_ROOM:
                    Globals.getClientFrame().addRoomToTable(GameDatabase.getGameName(information[0]),
                            information[1],
                            information[2],
                            new Integer(information[3]),
                            new Integer(information[4]));
                    if (Settings.getSleepEnabled() && Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                    break;
                case LEFT_CHANNEL:
                    Globals.getClientFrame().removePlayerFromChannel(GameDatabase.getGameName(information[0]), information[1]);
                    break;
                case CHAT_PRIVATE:
                    Globals.getClientFrame().printPrivateChatMessage(information[0], information[1]);
                    FrameIconFlasher.flash("data/icons/nudge.png", information[0] + " sent you a nudge!", false);
                    break;
                case ERROR_WHISPER_TO_OFFLINE_USER:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0]+" is currently offline, he won't receive your whisper messages", ChatStyles.SYSTEM, true);
                    break;
                case SERVER_SHUTTING_DOWN:
                    Client.disconnect();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Server is shutting down!", ChatStyles.SYSTEM, true);
                    break;
                case ECHO_NO_SUCH_PLAYER:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Error:No such Player!", ChatStyles.SYSTEM, false);
                    break;
                case ECHO_BANNED:
                    MuteBanList.ban(information[0]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " has been banned!", ChatStyles.SYSTEM, false);
                    break;
                case ECHO_UNBANNED:
                    MuteBanList.unBan(information[0]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " has been unbanned!", ChatStyles.SYSTEM, false);
                    break;
                case ECHO_MUTED:
                    MuteBanList.mute(information[0]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " has been muted!", ChatStyles.SYSTEM, false);
                    break;
                case ECHO_UNMUTED:
                    MuteBanList.unMute(information[0]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " has been unmuted!", ChatStyles.SYSTEM, false);
                    break;
                case NOT_READY_STATUS:
                    TabOrganizer.getRoomPanel().unReadyPlayer(information[0]);
                    break;
                case READY_STATUS:
                    TabOrganizer.getRoomPanel().readyPlayer(information[0]);
                    break;
                case ROOM_PLAYING_STATUS:
                    TabOrganizer.getRoomPanel().setPlaying(information[0]);
                    break;
                case GAME_CLOSED:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().gameClosed(information[1]);
                    }
                    Globals.getClientFrame().gameClosed(GameDatabase.getGameName(information[0]), information[1]);
                    Globals.getClientFrame().repaint();
                    break;
                case LAUNCH:
                    TabOrganizer.getRoomPanel().launch();
                    break;
                case CHANNEL_PLAYING_STATUS:
                    Globals.getClientFrame().setPlayingStatus(GameDatabase.getGameName(information[0]), information[1]);
                    break;
                case PASSWORD_CHANGED:
                    Globals.closeChangePasswordFrame();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Password has been changed!", ChatStyles.SYSTEM, false);
                    break;
                case PROFILE_SAVED:
                    Globals.closeEditProfileFrame();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Profile has been saved!", ChatStyles.SYSTEM, false);
                    break;
                case EDIT_PROFILE:
                    Globals.openEditProfileFrame(information[0],
                            information[1],
                            information[2],
                            information[3],
                            information[4]);
                    break;
                case SHOW_PROFILE:
                    Globals.openShowProfileFrame(information[0],
                            information[1],
                            information[2],
                            information[3]);
                    break;
                case JOINED_ROOM:
                    Globals.getClientFrame().addPlayerToRoom(GameDatabase.getGameName(information[0]), information[1], information[2]);
                    break;
                case LEFT_ROOM:
                    Globals.getClientFrame().removePlayerFromRoom(GameDatabase.getGameName(information[0]), information[1], information[2]);
                    break;
                case INGAMENAME:
                    Globals.setThisPlayer_inGameName(information[0]);
                    break;
                case UPDATE_PLAYERNAME:
                    boolean found = Globals.getClientFrame().updatePlayerName(information[0], information[1]);
                    if (Globals.getThisPlayer_loginName().equals(information[0])) {
                        Globals.setThisPlayer_loginName(information[1]);
                    } else {
                        if(found){
                            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " is now known as " + information[1], ChatStyles.SYSTEM, false);
                        }
                    }
                    Globals.getClientFrame().repaint();
                    break;
                case SENDING_FILE:
                    TabOrganizer.openFileTransferReceivePanel(information[0], information[1], information[2], information[3], information[4]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " wants to send you a file!", ChatStyles.SYSTEM, false);
                    break;
                case ACCEPTED_FILE:
                    Globals.getClientFrame().startSending(information[0], information[1], information[2], information[3], new Long(information[4]));
                    break;
                case REFUSED_FILE:
                    Globals.getClientFrame().refusedTransfer(information[0], information[1]);
                    break;
                case CANCELED_FILE:
                    Globals.getClientFrame().cancelledTransfer(information[0], information[1]);
                    break;
                case TURN_AROUND_FILE:
                    Globals.getClientFrame().turnAroundTransfer(information[0], information[1]);
                    break;
                case CONTACT_REQUESTED:
                    Globals.getContactList().addContact(information[0], "", ContactListElementTypes.PENDING_REQUEST);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " wants to add you to his/her contact list", ChatStyles.SYSTEM, true);
                    Globals.getClientFrame().flashQuickPanelToggler();
                    break;
                case SET_CONTACTSTATUS:
                    ContactListElementTypes status = null;
                    ContactListElementTypes previousstatus = null;
                    String name = information[0];
                    int statuscode = Integer.valueOf(information[1]);
                    status = ContactListElementTypes.values()[statuscode];
                    previousstatus = Globals.getContactList().getStatus(name);
                    Globals.getContactList().setStatus(name, status);
                    //notifications                 
                    switch (status) {
                        case OFFLINE:
                            if (Settings.getContactStatusChangeSoundNotification()) {
                                SoundPlayer.playLogoutSound();
                            }
                            if (Settings.getContactStatusChangeTextNotification()) {
                                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", name + " is now offline", ChatStyles.SYSTEM, false);
                            }
                            break;
                        case CHATTING:
                            if (Settings.getContactStatusChangeSoundNotification() && previousstatus == ContactListElementTypes.OFFLINE ) {
                                SoundPlayer.playLoginSound();
                            }
                            if (Settings.getContactStatusChangeTextNotification() && previousstatus == ContactListElementTypes.OFFLINE ) {
                                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", name + " is now online", ChatStyles.SYSTEM, false);
                            }
                            break;
                    }
                    break;
                case ACCEPTED_CONTACT_REQUEST:
                    Globals.getContactList().setStatus(information[0], ContactListElementTypes.OFFLINE);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " accepted your contact request", ChatStyles.SYSTEM, true);
                    Globals.getClientFrame().flashQuickPanelToggler();
                    break;
                case REFUSED_CONTACT_REQUEST:
                    Globals.getContactList().removeContact(information[0]);
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " refused your contact request", ChatStyles.SYSTEM, true);
                    Globals.getClientFrame().flashQuickPanelToggler();
                    break;
                case CONTACT_LIST:
                    Globals.getContactList().buildFrom(information);
                    break;
                case INSTANT_LAUNCH:
                    final String tmp[] = new String[information.length];
                    System.arraycopy(information, 0, tmp, 0, tmp.length);
                    tmp[0] = GameDatabase.getGameName(information[0]);
                    new Thread() {

                        @Override
                        public void run() {
                            Client.initInstantLaunch(tmp[0], GameDatabase.getModByIndex(tmp[0], new Integer(tmp[1])), tmp[2], new Integer(tmp[3]), tmp[4].equals("true"), false,tmp[5],tmp[6]);
                            Client.instantLaunch(tmp[0]);
                        }
                    }.start();
                    break;
                case VERIFICATION_ERROR:
                    break;//do nothing, should never come anyways
                case CRIPPLED_SERVER_MODE:
                    JOptionPane.showMessageDialog(Globals.getClientFrame(), "The server is running in maintenance mode,\nediting permanent data is impossible!\nPlease try again later.", "Server Maintenance", JOptionPane.ERROR_MESSAGE);
                    break;
                case CONTACT_REQUEST_ACKNOWLEDGE:
                    Globals.getContactList().addContact(information[0], ContactListModel.DEFAULT_GROUP, ContactListElementTypes.PENDING_CONTACT);
                    break;
                case CONTACT_ACCEPT_ACKNOWLEDGE:
                    Globals.getContactList().removePending(information[0]);
                    break;
                case CONTACT_REFUSE_ACKNOWLEDGE:
                     Globals.getContactList().removePending(information[0]);
                    break;
                case CONTACT_REMOVE_ACKNOWLEDGE:
                    Globals.getContactList().removeContact(information[0]);
                    break;
                case GROUP_CREATE_ACKNOWLEDGE:
                    Globals.getContactList().createNewGroup(information[0]);
                    break;
                case GROUP_DELETE_ACKNOWLEDGE:
                    Globals.getContactList().removeGroup(information[0]);
                    break;
                case GROUP_RENAME_ACKNOWLEDGE:
                    Globals.getContactList().renameGroup(information[0],information[1]);                    
                    break;
                case CONTACT_MOVE_ACKNOWLEDGE:
                    Globals.getContactList().moveContact(information[0],information[1]);     
                    break;
                case REMOVE_CONTACT_REQUEST:
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", information[0] + " doesn't want to add you anymore", ChatStyles.SYSTEM, true);
                    Globals.getContactList().removePendingRequest(information[0]);
                    break;
                case ROOM_INVITE:
                    Globals.getClientFrame().printPrivateChatMessage(information[0] , "Come to my room room://"+information[1]+ " at "+ GameDatabase.getGameName(information[2]) );
                    break;
                case YOUR_IP_IS:
                    Globals.setMyIP(information[0]);
                    break;
                default:
                    Logger.log(LogTypes.ERROR, "Server sent a command which wasn't handled!");
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Unknown command recieved! Please make sure you use the latest client!", ChatStyles.SYSTEM, true);
            }
        }
    }
}
