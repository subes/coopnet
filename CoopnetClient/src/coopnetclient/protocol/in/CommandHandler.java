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
package coopnetclient.protocol.in;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.ServerProtocolCommands;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.GameSettingsFrame;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.clientframetabs.ChannelPanel;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.protocol.out.Message;
import coopnetclient.utils.Settings;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.utils.ui.FrameIconFlasher;
import coopnetclient.utils.Logger;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.launcher.TempGameSettings;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * Searches for known command, gets the parameters and executes it
 */
public final class CommandHandler {

    public static void execute(String[] data) {
        ServerProtocolCommands command = null;
        ChannelPanel cp;

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
            FrameOrganizer.getClientFrame().printSystemMessage("Unknown command recieved! Please make sure you use the latest client!", true);
            return;
        }

        String[] information = new String[data.length - 1];
        System.arraycopy(data, 1, information, 0, information.length);

        Logger.logInTraffic(command, information);

        if (!Globals.getLoggedInStatus()) {//not-logged-in commands
            switch (command) {
                case COMPATIBILITY_VERSION:
                    CommandMethods.checkProtocolVersion(information[0]);
                    break;
                case OK_LOGIN:
                    Globals.setLoggedInStatus(true);
                    Globals.setThisPlayerLoginName(information[0]);
                    TabOrganizer.closeConnectingPanel();
                    TabOrganizer.closeLoginPanel();
                    Protocol.setSleep(Settings.getSleepEnabled());
                    Protocol.refreshContacts();
                    String s = coopnetclient.utils.Settings.getHomeChannel();
                    if (s.length() > 0) {
                        Protocol.joinChannel(s);
                    }
                    break;
                case LOGIN_INCORRECT:
                    if (TabOrganizer.getLoginPanel() != null) {
                        TabOrganizer.getLoginPanel().showError("Wrong username/password, please try again!", Color.red);
                        TabOrganizer.getLoginPanel().enableButtons();
                    } else {
                        TabOrganizer.openLoginPanel();
                    }
                    break;
                case OK_REGISTER:
                    TabOrganizer.closeRegisterPanel();
                    TabOrganizer.openLoginPanel();
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(),
                            "<html><b>Thank you for registering!</b>\n" +
                            "You may login now.", "Successfully registered",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                case LOGINNAME_IN_USE:
                    TabOrganizer.getRegisterPanel().showLoginNameUsedError();
                    TabOrganizer.getRegisterPanel().enableButtons();
                    break;
                case CRIPPLED_SERVER_MODE:
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(), "The server is running in maintenance mode,\nlogging in and registering is impossible!\nPlease try again later.", "Server Maintenance", JOptionPane.ERROR_MESSAGE);
                    if (TabOrganizer.getLoginPanel() != null) {
                        TabOrganizer.getLoginPanel().enableButtons();
                    }
                    if (TabOrganizer.getRegisterPanel() != null) {
                        TabOrganizer.getRegisterPanel().enableButtons();
                    }
                    break;
                case SERVER_SHUTTING_DOWN:
                    Client.disconnect();
                    FrameOrganizer.getClientFrame().printSystemMessage("Server is shutting down!", true);
                    break;
                default:break;
            }
        } else {//logged-in commands
            String currentChannelName = null; //current channelname , correct where its expected, garbage otherwise
            if (information.length > 0) {
                currentChannelName = GameDatabase.getGameName(information[0]);
            }
            switch (command) {
                case COMPATIBILITY_VERSION:
                    CommandMethods.checkProtocolVersion(information[0]);
                    break;
                case CHAT_MAIN:
                    if (information.length < 2) {
                        return;
                    }
                    FrameOrganizer.getClientFrame().printMainChatMessage(currentChannelName, information[1], information[2], ChatStyles.USER);
                    if (Settings.getSleepEnabled() && Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                    break;
                case CHAT_ROOM:
                    if (information.length < 2) {
                        return;
                    }
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().chat(information[0], information[1], ChatStyles.USER);
                    }
                    break;
                case ADD_TO_PLAYERS:
                    TabOrganizer.getChannelPanel(currentChannelName).addPlayerToChannel(information[1]);
                    if (Settings.getLogUserActivity() && !Globals.getThisPlayerLoginName().equals(information[1])) {
                        TabOrganizer.getChannelPanel(currentChannelName).printMainChatMessage("", information[1] + " has entered the channel.", ChatStyles.SYSTEM);
                    }
                    break;
                case SET_GAMESETTING:
                    if (information[0].equals("map")) {
                        TempGameSettings.setMap(information[1]);
                    } else {
                        TempGameSettings.setGameSetting(information[0], information[1], false);
                    }
                    GameSettingsFrame gf = FrameOrganizer.getGameSettingsFrame();
                    if (gf != null) {
                        gf.updateValues();
                    }
                    break;
                case JOIN_CHANNEL:
                    GameDatabase.load(currentChannelName, GameDatabase.dataFilePath);
                    TabOrganizer.openChannelPanel(currentChannelName);
                    cp = TabOrganizer.getChannelPanel(currentChannelName);
                    for(int i = 1; i < information.length;++i){
                        cp.addPlayerToChannel(information[i]);
                    }
                    break;
                case JOIN_ROOM:
                    RoomData rd = new RoomData(false, currentChannelName, Integer.valueOf(information[4]), information[1], information[2], Integer.valueOf(information[3]), information[5], information[6], Long.valueOf(information[7]), information[8], Boolean.valueOf(information[9]), false);
                    TabOrganizer.openRoomPanel(rd);
                    for(int i = 10;i < information.length;++i){
                        TabOrganizer.getRoomPanel().addmember(information[i]);
                    }
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
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " sent you a nudge!", false);
                    SoundPlayer.playNudgeSound();
                    break;
                case ERROR_YOU_ARE_BANNED:
                    FrameOrganizer.getClientFrame().printSystemMessage("You are banned by the rooms host!", true);
                    for (i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                        TabOrganizer.getChannelPanel(i).enableButtons();
                    }
                    break;
                case ERROR_ROOM_IS_FULL:
                    FrameOrganizer.getClientFrame().printSystemMessage("The room is full!", true);
                    for (i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                        TabOrganizer.getChannelPanel(i).enableButtons();
                    }
                    break;
                case ERROR_ROOM_DOES_NOT_EXIST:
                    FrameOrganizer.getClientFrame().printSystemMessage("The room doesn't exist", true);
                    for (i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                        TabOrganizer.getChannelPanel(i).enableButtons();
                    }
                    break;
                case ERROR_LOGINNAME_IS_ALREADY_USED:
                    FrameOrganizer.getEditProfileFrame().loginAlreadyUsed();
                    break;
                case REQUEST_PASSWORD:
                    FrameOrganizer.openJoinRoomPasswordFrame(information[0]);
                    break;
                case WRONG_ROOM_PASSWORD:
                    FrameOrganizer.showWrongPasswordNotification();
                    break;
                case CREATE_ROOM:
                    rd = new RoomData(true, currentChannelName, Integer.valueOf(information[2]), Globals.getClientIP(), Client.getHamachiAddress(), Integer.valueOf(information[1]), Globals.getThisPlayerLoginName(), information[3], Long.valueOf(information[4]), information[5], Boolean.valueOf(information[6]), false);
                    TabOrganizer.openRoomPanel(rd);
                    break;
                case LEAVE_ROOM:
                    TabOrganizer.closeRoomPanel();
                    break;
                case REMOVE_ROOM:
                    TabOrganizer.getChannelPanel(currentChannelName).removePlayerFromRoom(information[1], information[1]);
                    TabOrganizer.getChannelPanel(currentChannelName).removeRoomFromTable(information[1]);
                    break;
                case CLOSE_ROOM:
                    TabOrganizer.closeRoomPanel();
                    FrameOrganizer.getClientFrame().printMainChatMessage(currentChannelName, "SYSTEM", "The Room has been closed!", ChatStyles.SYSTEM);
                    break;
                case KICKED:
                    FrameOrganizer.getClientFrame().printMainChatMessage(TabOrganizer.getRoomPanel().getRoomData().getChannel(), "SYSTEM", "You have been kicked by the host!", ChatStyles.SYSTEM);
                    TabOrganizer.closeRoomPanel();
                    break;
                case ADD_MEMBER_TO_ROOM:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().addmember(information[0]);
                        if (Settings.getLogUserActivity() && !Globals.getThisPlayerLoginName().equals(information[0])) {
                            TabOrganizer.getRoomPanel().chat("", information[0] + " has entered the room.", ChatStyles.SYSTEM);
                        }
                    }
                    break;
                case REMOVE_MEMBER_FROM_ROOM:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().removeMember(information[0]);
                        if (Settings.getLogUserActivity() && !Globals.getThisPlayerLoginName().equals(information[0])) {
                            TabOrganizer.getRoomPanel().chat("", information[0] + " has left the room.", ChatStyles.SYSTEM);
                        }
                    }
                    break;
                case ADD_ROOM:
                    TabOrganizer.getChannelPanel(currentChannelName).addRoomToTable(
                            information[1],
                            GameDatabase.getModByIndex(currentChannelName, Integer.valueOf(information[5])),
                            information[2],
                            new Integer(information[3]),
                            new Integer(information[4]));
                    if (Settings.getSleepEnabled() && Globals.getSleepModeStatus()) {
                        Globals.setSleepModeStatus(false);
                    }
                    break;
                case LEFT_CHANNEL:
                    TabOrganizer.getChannelPanel(currentChannelName).removePlayerFromChannel(information[1]);
                    if (Settings.getLogUserActivity() && !Globals.getThisPlayerLoginName().equals(information[1])) {
                        TabOrganizer.getChannelPanel(currentChannelName).printMainChatMessage("", information[1] + " has left the channel.", ChatStyles.SYSTEM);
                    }
                    break;
                case CHAT_PRIVATE:
                    FrameOrganizer.getClientFrame().printPrivateChatMessage(information[0], information[1]);
                    TabOrganizer.markTab(TabOrganizer.getPrivateChatPanel(information[0]));
                    if (!FrameOrganizer.getClientFrame().isActive()) {
                        FrameIconFlasher.flash("data/icons/nudge.png", information[0] + " sent you a private message!", false);
                    }
                    break;
                case ERROR_WHISPER_TO_OFFLINE_USER:
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " is currently offline, he won't receive your whisper messages", true);
                    break;
                case SERVER_SHUTTING_DOWN:
                    Client.disconnect();
                    FrameOrganizer.getClientFrame().printSystemMessage("Server is shutting down!", true);
                    break;
                case ECHO_NO_SUCH_PLAYER:
                    FrameOrganizer.getClientFrame().printSystemMessage("Error:No such Player!", false);
                    break;
                case ECHO_BANNED:
                    MuteBanList.ban(information[0]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " has been banned!", false);
                    break;
                case ECHO_UNBANNED:
                    MuteBanList.unBan(information[0]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " has been unbanned!", false);
                    break;
                case ECHO_MUTED:
                    MuteBanList.mute(information[0]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " has been muted!", false);
                    break;
                case ECHO_UNMUTED:
                    MuteBanList.unMute(information[0]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " has been unmuted!", false);
                    break;
                case NOT_READY_STATUS:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().unReadyPlayer(information[0]);
                    }
                    break;
                case READY_STATUS:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().readyPlayer(information[0]);
                    }
                    break;
                case ROOM_PLAYING_STATUS:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().setPlaying(information[0]);
                    }
                    break;
                case GAME_CLOSED:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().gameClosed(information[1]);
                    }
                    TabOrganizer.getChannelPanel(currentChannelName).gameClosed(information[1]);
                    break;
                case LAUNCH:
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().launch();
                    }
                    break;
                case CHANNEL_PLAYING_STATUS:
                    TabOrganizer.getChannelPanel(currentChannelName).setPlayingStatus(information[1]);
                    break;
                case PASSWORD_CHANGED:
                    FrameOrganizer.closeChangePasswordFrame();
                    FrameOrganizer.getClientFrame().printSystemMessage("Password has been changed!", false);
                    break;
                case PROFILE_SAVED:
                    FrameOrganizer.closeEditProfileFrame();
                    FrameOrganizer.getClientFrame().printSystemMessage("Profile has been saved!", false);
                    break;
                case EDIT_PROFILE:
                    FrameOrganizer.openEditProfileFrame(information[0],
                            information[1],
                            information[2],
                            information[3],
                            information[4]);
                    break;
                case SHOW_PROFILE:
                    FrameOrganizer.openShowProfileFrame(information[0],
                            information[1],
                            information[2],
                            information[3]);
                    break;
                case JOINED_ROOM:
                    if (TabOrganizer.getChannelPanel(currentChannelName) != null) {
                        TabOrganizer.getChannelPanel(currentChannelName).addPlayerToRoom(information[1], information[2]);
                    }
                    break;
                case LEFT_ROOM:
                    TabOrganizer.getChannelPanel(currentChannelName).removePlayerFromRoom(information[1], information[2]);
                    break;
                case INGAMENAME:
                    Globals.setThisPlayerInGameName(information[0]);
                    break;
                case UPDATE_PLAYERNAME:
                    boolean found = FrameOrganizer.getClientFrame().updatePlayerName(information[0], information[1]);
                    if (Globals.getThisPlayerLoginName().equals(information[0])) {
                        Globals.setThisPlayerLoginName(information[1]);
                    } else {
                        if (found) {
                            FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " is now known as " + information[1], false);
                        }
                    }
                    FrameOrganizer.getClientFrame().repaint();
                    break;
                case SENDING_FILE:
                    TabOrganizer.recieveFile(information[0], information[1], information[2], information[3], information[4]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " wants to send you a file!", false);
                    break;
                case ACCEPTED_FILE:
                    Globals.getTransferModel().startSending(information[0], information[1], information[2], information[3], new Long(information[4]));
                    break;
                case REFUSED_FILE:
                    Globals.getTransferModel().peerRefusedTransfer(information[0], information[1]);
                    break;
                case CANCELED_FILE:
                    Globals.getTransferModel().peerCancelledTransfer(information[0], information[1]);
                    break;
                case TURN_AROUND_FILE:
                    Globals.getTransferModel().turnAroundTransfer(information[0], information[1]);
                    break;
                case CONTACT_REQUESTED:
                    Globals.getContactList().addContact(information[0], "", ContactListElementTypes.PENDING_REQUEST);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " wants to add you to his/her contact list", true);
                    FrameOrganizer.getClientFrame().flashQuickPanelToggler();
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
                                FrameOrganizer.getClientFrame().printSystemMessage(name + " is now offline", false);
                            }
                            break;
                        case CHATTING:
                            if (Settings.getContactStatusChangeSoundNotification() && previousstatus == ContactListElementTypes.OFFLINE) {
                                SoundPlayer.playLoginSound();
                            }
                            if (Settings.getContactStatusChangeTextNotification() && previousstatus == ContactListElementTypes.OFFLINE) {
                                FrameOrganizer.getClientFrame().printSystemMessage(name + " is now online", false);
                            }
                            break;
                    }
                    break;
                case ACCEPTED_CONTACT_REQUEST:
                    Globals.getContactList().setStatus(information[0], ContactListElementTypes.OFFLINE);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " accepted your contact request", true);
                    FrameOrganizer.getClientFrame().flashQuickPanelToggler();
                    break;
                case REFUSED_CONTACT_REQUEST:
                    Globals.getContactList().removeContact(information[0]);
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " refused your contact request", true);
                    FrameOrganizer.getClientFrame().flashQuickPanelToggler();
                    break;
                case CONTACT_LIST:
                    Globals.getContactList().buildFrom(information);
                    break;
                case INSTANT_LAUNCH:
                    final String tmp[] = new String[information.length];
                    System.arraycopy(information, 0, tmp, 0, tmp.length);
                    new Thread() {

                        @Override
                        public void run() {
                            String gameName = GameDatabase.getGameName(tmp[0]);
                            String modName = GameDatabase.getModByIndex(gameName, new Integer(tmp[1]));
                            RoomData rdt = new RoomData(false, gameName, Integer.valueOf(tmp[1]), tmp[2], tmp[2], -1, "", "", 0l, tmp[3], false, true);
                            boolean launch = Launcher.initInstantLaunch(rdt);
                            if (launch) {
                                int idx = 5;//start of settigns
                                while (idx < tmp.length) {
                                    if (tmp[idx].equals("map")) {
                                        TempGameSettings.setMap(tmp[idx + 1]);
                                    } else {
                                        TempGameSettings.setGameSetting(tmp[idx], tmp[idx + 1], false);
                                    }
                                    GameSettingsFrame gf = FrameOrganizer.getGameSettingsFrame();
                                    if (gf != null) {
                                        gf.updateValues();
                                    }
                                    idx += 2;
                                }
                                Launcher.instantLaunch();
                            }
                        }
                    }.start();
                    break;
                case VERIFICATION_ERROR:
                    break;//do nothing, should never come anyways
                case CRIPPLED_SERVER_MODE:
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(), "The server is running in maintenance mode,\nediting permanent data is impossible!\nPlease try again later.", "Server Maintenance", JOptionPane.ERROR_MESSAGE);
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
                    Globals.getContactList().renameGroup(information[0], information[1]);
                    break;
                case CONTACT_MOVE_ACKNOWLEDGE:
                    Globals.getContactList().moveContact(information[0], information[1]);
                    break;
                case REMOVE_CONTACT_REQUEST:
                    FrameOrganizer.getClientFrame().printSystemMessage(information[0] + " doesn't want to add you anymore", true);
                    Globals.getContactList().removePendingRequest(information[0]);
                    break;
                case ROOM_INVITE:
                    FrameOrganizer.getClientFrame().printPrivateChatMessage(information[0], "Come to my room room://" + information[1] + " at " + GameDatabase.getGameName(information[2]));
                    break;
                case YOUR_IP_IS:
                    Globals.setClientIP(information[0]);
                    break;
                case SETAWAYSTATUS:
                    for (int j = 0; (cp = TabOrganizer.getChannelPanel(j)) != null; ++j) {
                        cp.setAway(information[0]);
                    }
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().setAway(information[0]);
                    }
                    break;
                case UNSETAWAYSTATUS:
                    for (int j = 0; (cp = TabOrganizer.getChannelPanel(j)) != null; ++j) {
                        cp.unSetAway(information[0]);
                    }
                    if (TabOrganizer.getRoomPanel() != null) {
                        TabOrganizer.getRoomPanel().unSetAway(information[0]);
                    }
                    break;
                default:
                    Logger.log(LogTypes.ERROR, "Server sent a command which wasn't handled!");
                    FrameOrganizer.getClientFrame().printSystemMessage("Unknown command recieved! Please make sure you use the latest client!", true);
            }
        }
    }
}
