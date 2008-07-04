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

import coopnetclient.modules.Verification;
import coopnetclient.modules.Settings;
import coopnetclient.modules.SoundPlayer;
import coopnetclient.modules.FrameIconFlasher;
import coopnetclient.frames.EditProfileFrame;
import coopnetclient.frames.ShowProfileFrame;
import coopnetclient.modules.renderers.RoomStatusListCellRenderer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.modules.FileDownloader;
import javax.swing.JOptionPane;

/**
 * Searches for known command, gets the parameters and executes it
 * 
 */
public class CommandHandler {

    private static String gameDataUrl;
    
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
        if (input.equals(Protocol.HEARTBEAT_REQUEST)) {
            Client.send(Protocol.HEARTBEAT_RESPONSE, null);
        }

        if (input.equals("updateURL ")) {
            String url = input.substring(10);
            Settings.setUpdateURL(url);
        }

        if (input.startsWith("lastversion ")) {
            if (!Verification.verifyClientVersion(input.substring(12))) {
                new Thread() {

                    @Override
                    public void run() {
                        try{
                            int n = JOptionPane.showConfirmDialog(null,
                                    "<html>You have an outdated version of the client!<br>" +
                                    "Would you like to update now?<br>(The client will close and update itself)",
                                    "Client outdated", JOptionPane.YES_NO_OPTION);
                            if (n == JOptionPane.YES_OPTION) {
                                try {
                                    Runtime rt = Runtime.getRuntime();
                                    rt.exec("java -jar CoopnetUpdater.jar");
                                    Globals.getClientFrame().quit();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }catch(Exception e){
                            ErrorHandler.handleException(e);
                        }
                    }
                }.start();
            }
        }

        if (!Globals.getLoggedInStatus()) {
            if (input.startsWith("OK_LOGIN")) {
                //logged in, start the client
                Globals.setLoggedInStatus(true);
                Globals.getClientFrame().removeLoginTab();
            } else if (input.startsWith("no such user")) {
                JOptionPane.showMessageDialog(null, "No such user!", "Alert", JOptionPane.ERROR_MESSAGE);
            } else if (input.startsWith("incorrect passsword!try again")) {
                JOptionPane.showMessageDialog(null, "Incorrect password!", "Alert", JOptionPane.ERROR_MESSAGE);
            } else if (input.startsWith("Already logged in!")) {
                JOptionPane.showMessageDialog(null, "Already logged in", "Alert", JOptionPane.ERROR_MESSAGE);
            } else if (input.startsWith("OK_REGISTER")) {
                JOptionPane.showMessageDialog(null, "<html><b>Thank you for registering!</b>\n" +
                        "If you want to be able to do password recovery in the future,\n" +
                        "please fill in a valid E-Mail address in your player profile.\n" +
                        "\n" +
                        "You may login now.", "Successfully registered", JOptionPane.INFORMATION_MESSAGE);
            } else if (input.startsWith("name is already used")) {
                JOptionPane.showMessageDialog(null, "Name is already used!", "Alert", JOptionPane.ERROR_MESSAGE);
            }

        } else {

            if (input.startsWith("on ")) {
                currentchannel = input.substring(3, 6);
                currentchannel = GameDatabase.getGameName(currentchannel); //decode ID
                input = input.substring(7);
            }

            if (input.startsWith("setport ")) {
                int port = new Integer(input.substring(8));
                if (Globals.getRoomPanel() != null) {
                    Globals.getLauncher().setPort(port);
                }
            } else if (input.startsWith("setmod ")) {
                String mod = input.substring(7);
                if (Globals.getRoomPanel() != null) {
                    Globals.getLauncher().setMod(mod);
                }
            } else if (input.startsWith("joinchannel ")) {
                String tmp = input.substring(12);
                GameDatabase.load(tmp);
                Globals.getClientFrame().addChannel(tmp);
            } else if (input.startsWith("nudge ")) {
                String tmp = input.substring(6);
                new FrameIconFlasher(Globals.getClientFrame(), "data/icons/nudge.gif", tmp + " sent you a nudge!");
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", tmp + " sent you a nudge!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
                SoundPlayer.playNudgeSound();
            } else if (input.startsWith("error")) {
                JOptionPane.showMessageDialog(null, input.substring(6), "ERROR", JOptionPane.ERROR_MESSAGE);
            } else if (input.startsWith("gamedataurl ")) {
                String tmp = input.substring(12);
                gameDataUrl = tmp;
            } else if (input.startsWith("lastgamedataversion ")) {
                final int lastversion = new Integer(input.substring(20));
                GameDatabase.loadVersion();
                if (GameDatabase.version < lastversion) {
                    System.out.println("downloading new gamedata");
                    // download the file in the background
                    new Thread() {

                        @Override
                        public void run() {
                            try{
                                boolean ret = false;
                                ret = FileDownloader.downloadFile(gameDataUrl, GameDatabase.datafilepath);
                                if (!ret) {
                                    //give notice to user of failure
                                    JOptionPane.showMessageDialog(null, "You have an outdated version of the gamedata, but couldn't update it!", "Gamedata outdated", JOptionPane.INFORMATION_MESSAGE);
                                } else {//succesfull
                                    GameDatabase.load("");
                                }
                            }catch(Exception e){
                                ErrorHandler.handleException(e);
                            }
                        }
                    }.start();
                }
            } else 
            //mainchat command
            if (input.startsWith("main ")) {
                String[] tmp = input.substring(5).split(Protocol.INFORMATION_DELIMITER);
                if (tmp.length == 1) {
                    return;
                }
                Globals.getClientFrame().mainChat(currentchannel, tmp[0], tmp[1], coopnetclient.modules.ColoredChatHandler.USER_STYLE);
                if (Globals.getSleepModeStatus()) {
                    Globals.setSleepModeStatus(false);
                }
            } else 
            //adds a palyer to the playerlist in main window
            if (input.startsWith("addtoplayers")) {
                Globals.getClientFrame().addUser(currentchannel, input.substring(13));
            } else 
            //prints message to the room-chat
            if (input.startsWith("room ")) {
                String[] tmp = input.substring(5).split(Protocol.INFORMATION_DELIMITER);
                Globals.getRoomPanel().chat(tmp[0], tmp[1], coopnetclient.modules.ColoredChatHandler.USER_STYLE);
            } else 
            //the server accepted the join request, must create a new room tab now in client mode
            if (input.startsWith("join ")) {
                String[] tmp = input.substring(5).split(Protocol.INFORMATION_DELIMITER); // join, ip,compatibility,hamachiip, maxplayers, modindex
                Globals.createRoomPanel(false, currentchannel, tmp[5], tmp[1], tmp[2].equals("true"), tmp[3], new Integer(tmp[4]));
            } else 
            //the server accepted the room creation request, must create a new room tab in server mode
            if (input.startsWith("create ")) {
                String[] tmp = input.substring(7).split(Protocol.INFORMATION_DELIMITER);
                boolean compatible = Boolean.valueOf(tmp[1]);
                int maxplayers = Integer.valueOf(tmp[2]);
                String modindex = tmp[3];
                Globals.createRoomPanel(true, currentchannel, modindex, "", compatible, "", maxplayers);
            } else 
            //server accepted leave request, must delete room tab
            if (input.startsWith("leave")) {
                Globals.removeRoomPanel();
            } else 
            //the owner of the room closed it, must remove from room list
            if (input.startsWith("removeroom")) {
                Globals.getClientFrame().removeRoomFromList(currentchannel, input.substring(11));
            } else             
            //the currently joined room was closed, must delete room tab
            if (input.startsWith("close")) {
                Globals.removeRoomPanel();
                Globals.getClientFrame().mainChat(currentchannel, "SYSTEM", "The Room has been closed!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);

            } else 
            //been kicked of the current room, must delete room tab
            if (input.startsWith("kicked")) {
                Globals.removeRoomPanel();
                Globals.getClientFrame().mainChat(currentchannel, "SYSTEM", "You have been kicked by the host!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            } else 
            //add a player to the rooms player list
            if (input.startsWith("addmember ")) {
                Globals.getRoomPanel().addmember(input.substring(10));
            } else 
            // remove a player from the rooms player list
            if (input.startsWith("removemember")) {
                Globals.getRoomPanel().removeMember(input.substring(13));
            } else 
            //add a new room to the room list
            if (input.startsWith("addroom")) {
                String[] tmp = input.split(Protocol.INFORMATION_DELIMITER);//0adroom  1roomname 2hostname 3limit 4passworded
                Globals.getClientFrame().addNewRoomToList(currentchannel, tmp[1], tmp[2], tmp[3], tmp.length == 5 ? true : false);
                if (Globals.getSleepModeStatus()) {
                    Globals.setSleepModeStatus(false);
                }
            } else 
            //remove a user from the player list in main window
            if (input.startsWith("logoff")) {
                Globals.getClientFrame().removeUser(currentchannel, input.substring(7));
            } else 
            //show a private message
            if (input.startsWith("private ")) {
                String[] tmp = input.substring(8).split(Protocol.INFORMATION_DELIMITER);//0. sender 1.message
                Globals.getClientFrame().privateChat(tmp[0], tmp[1]);
            } else 
            //print a message from the server
            if (input.startsWith("echo ")) {
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", input.substring(5), coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            } else 
            //set players(name in parameter) ready status to not ready
            if (input.startsWith("unready ")) {
                Globals.getRoomPanel().unReadyPlayer(input.substring(8));
            } else 
            //set players(name in parameter) ready status to ready
            if (input.startsWith("ready ")) {
                Globals.getRoomPanel().readyPlayer(input.substring(6));
            } else 
            //set players(name in parameter) status to playing
            if (input.startsWith("playing ")) {
                Globals.getRoomPanel().setPlaying(input.substring(8));
            } else 
            //set the players(name in parameter) status to not playing
            if (input.startsWith("gameclosed ")) {
                if (Globals.getRoomPanel() != null) {
                    Globals.getRoomPanel().gameClosed(input.substring(11));
                }
                Globals.getClientFrame().gameClosed(currentchannel, input.substring(11));
                Globals.getClientFrame().repaint();
            } else 
            //launch the game if not running already
            if (input.startsWith("launch")) {
                Globals.getRoomPanel().launch();
            } else if (input.startsWith("setplayingstatus ")) {
                String host = input.substring(17);
                Globals.getClientFrame().setPlayingStatus(currentchannel, host);
            } else 
            //confirmation message from the server. the password was changed succesfully
            if (input.startsWith("passwordchanged")) {
                Globals.getChangePasswordFrame().setVisible(false);
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Password changed!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            } else 
            //confirmation message from the server. the name was changed successfully(if changed)
            if (input.startsWith("profilesaved")) {
                Globals.getProfileFrame().setVisible(false);
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Profile saved!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            } else 
            //show the profile-editing window with the data in parameters
            if (input.startsWith("editprofile")) {
                String[] tmp = input.split(Protocol.INFORMATION_DELIMITER); //0 showprofile 1 name 2 ingamename 3 email 4 ispublic 5 country 6 webpage
                Globals.setProfileFrame(new EditProfileFrame(
                        tmp[1].substring(6),
                        tmp[2].substring(10),
                        tmp[3].substring(7),
                        tmp[4].substring(8),
                        tmp[5].substring(9),
                        tmp[6].substring(9)));
                Globals.getProfileFrame().setVisible(true);
            } else 
            //show the profile view window with the data in parameters
            if (input.startsWith("showprofile")) {
                String[] tmp = input.split(Protocol.INFORMATION_DELIMITER); //0 showprofile 1 name 2 email 3 country 4 webpage
                Globals.setProfileFrame(new ShowProfileFrame(
                        tmp[1].substring(6),
                        tmp[2].substring(7),
                        tmp[3].substring(9),
                        tmp[4].substring(9)));
                Globals.getProfileFrame().setVisible(true);
            } else 
            //add the player in parameter to the player-list of the room in parameter (shows as tooltiptext)
            if (input.startsWith("joined ")) {
                String[] tmp = input.substring(7).split(Protocol.INFORMATION_DELIMITER); //0 rooms hosts name 1 playername
                Globals.getClientFrame().addMemberToRoom(currentchannel, tmp[0], tmp[1]);
            } else 
            //remove player in parameter from the player-list of the room in parameter (shows as tooltiptext)
            if (input.startsWith("leftroom ")) {
                String[] tmp = input.substring(9).split(Protocol.INFORMATION_DELIMITER); //0 rooms hosts name 1 playername
                Globals.getClientFrame().leftRoom(currentchannel, tmp[0], tmp[1]);
            } else 
            //set the players in-game name
            if (input.startsWith("gamename ")) {
                Globals.setThisPlayer_inGameName(input.substring(9));
                if (Globals.getRoomPanel() != null) {
                    Globals.getRoomPanel().setGameName(input.substring(9));
                }
            } else 
            //a player changed its name, msut update in player list and room list
            if (input.startsWith("updatename ")) {
                String[] tmp = input.substring(11).split(Protocol.INFORMATION_DELIMITER);      // 0 oldname 1 new name
                Globals.getClientFrame().mainChat(currentchannel, "SYSTEM", tmp[0] + " is now known as " + tmp[1], coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
                Globals.getClientFrame().updateName(currentchannel, tmp[0], tmp[1]);

                Globals.getClientFrame().repaint();
            } else if (input.startsWith("SendingFile")) {
                String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2file 3size 4 ip 5 port
                Globals.getClientFrame().addTransferTab_Recieve(tmp[1], tmp[3], tmp[2],tmp[4],tmp[5]);
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", tmp[1] + " wants to send you a file!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            } else if (input.startsWith("AcceptedFile")) {
                String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//0command 1reciever  2filename 3 ip 4 port
                Globals.getClientFrame().startSending(tmp[3], tmp[1], tmp[2], tmp[4]);
            } else if (input.startsWith("RefusedFile")) {
                String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1reciever  2filename
                Globals.getClientFrame().refusedTransfer(tmp[1], tmp[2]);
            } else if (input.startsWith("CancelledFile")) {
                String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2filename
                Globals.getClientFrame().cancelledTransfer(tmp[1], tmp[2]);
            } else
                //turn around transfer connection direction
                if (input.startsWith("TurnAround")) {
                String tmp[] = input.split(Protocol.INFORMATION_DELIMITER);//command 1sender  2filename
                Globals.getClientFrame().turnAroundTransfer(tmp[1], tmp[2]);
            }
            
        //else if(input.startsWith("")){	NEW COMMANDS	}
        }
    }
}
