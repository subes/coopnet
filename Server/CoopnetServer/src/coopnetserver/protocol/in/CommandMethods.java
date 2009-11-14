package coopnetserver.protocol.in;

import coopnetserver.data.channel.ChannelData;
import coopnetserver.data.player.PlayerData;
import coopnetserver.data.room.RoomData;
import coopnetserver.data.channel.Channel;
import coopnetserver.data.connection.Connection;
import coopnetserver.data.room.Room;
import coopnetserver.data.player.Player;
import coopnetserver.exceptions.VerificationException;
import coopnetserver.utils.Verification;
import coopnetserver.utils.Database;
import coopnetserver.protocol.out.Protocol;
import java.sql.SQLException;
import java.util.Map.Entry;

public class CommandMethods {

    /**
     * sends the list of the rooms and their members
     */
    protected static void refreshRoomsAndPlayers(Player thisPlayer, Channel currentchannel) {
        if (thisPlayer.getSleepMode()) {
            thisPlayer.setSleepMode(false);
        }
        //send rooms list
        for (Room room : currentchannel.getRooms()) {
            Protocol.addNewRoom(thisPlayer.getConnection(), currentchannel, room);
            for (Player p : room.getPlayers()) {
                if (!p.equals(room.getHost())) {
                    Protocol.sendJoinNotification(currentchannel, thisPlayer.getConnection(), room.getHost(), p);
                }
                if(p.isPlaying()){
                    Protocol.sendChannelPlayingStatus(currentchannel, p);
                }
            }
        }
        for(Player p : currentchannel.getPlayersInChannel() ){
            if(p.isAway()){
                Protocol.setAwayStatus(thisPlayer, p);
            }
        }
    }

    /**
     * register new player
     */
    protected static void createPlayer(Connection con, String[] information) throws SQLException, VerificationException {
       // String[] info = {name,password,email,ingameName,country,website}; from client.protocol
            if (!Database.loginNameExists(information[0])) {
                if(information[3].length() == 0 ){
                    information[3] = information[0];
                }
                if (!Verification.verifyLoginName(information[0])) {
                    throw new VerificationException("Failure on loginname! " + information[0]);
                }
                if (!Verification.verifyPassword(information[1])) {
                    throw new VerificationException("Failure on password! " + information[1]);
                }
                if (!Verification.verifyEMail(information[2])) {
                    throw new VerificationException("Failure on email! " + information[2]);
                }
                if (!Verification.verifyIngameName(information[3])) {
                    throw new VerificationException("Failure on ingame name! " + information[3]);
                }
                if (!Verification.verifyCountry(information[4])) {
                    throw new VerificationException("Failure on country! " + information[4]);
                }
                if (!Verification.verifyWebsite(information[5])) {
                    throw new VerificationException("Failure on website! " + information[5]);
                }
                Database.createPlayer(information[0], information[1], information[2], information[3], information[4], information[5]);
                
                Protocol.confirmRegister(con);
            } else {
                Protocol.nameAlreadyUsed(con);
            }        
    }

    /**
     * log in the player
     */
    protected static void login(Connection  con, String[] information) throws SQLException, VerificationException {
        Player logined = null;

        if (!Verification.verifyLoginName(information[0])) {
            throw new VerificationException("Failure on loginname! " + information[0]);
        }
        if (!Verification.verifyPassword(information[1])) {
            throw new VerificationException("Failure on password! " + information[1]);
        }

        if (Database.verifyLogin(information[0], information[1])) {
            logined = new Player(con, information[0]);

            if (PlayerData.logIn(logined, con)) {
                con.setPlayer(logined);
                //reply
                Protocol.confirmLogin(logined.getConnection(),logined.getLoginName());
                //send players gamename
                Protocol.setInGameName(logined);
                logined.sendMyContactStatusToMyContacts();
                //send the muta/ban data
                Protocol.sendMuteBanData(logined);
                Protocol.sendIP(con, con.getIpAddress());
                if(information.length > 3){ //playing status sent
                    logined.setPlaying(Boolean.valueOf(information[2]));
                    logined.setPlayingOnChannel(ChannelData.getChannel(information[3]));
                }
            }
        } else {
            Protocol.wrongLogin(con);
        }
    }

    protected static void joinRoom(Player thisPlayer, String information[]) {
        thisPlayer.setNotReady();
        Player host = null;
        Room theroom = null;
        String password = null;
        Channel currentchannel = ChannelData.getChannel(information[0]);
        if (currentchannel == null) {
            theroom = RoomData.getRoomByID(Long.valueOf(information[0]));
            password = information.length == 2 ? information[1] : "";
            if (theroom == null) {
                //notidy user that the room doesnt exist anymore
                Protocol.errorRoomDoesNotExist(thisPlayer.getConnection());
                return;
            }
            host = theroom.getHost();
            currentchannel = theroom.parent;
            if (thisPlayer.getCurrentRoom() != null && thisPlayer.getCurrentRoom().equals(theroom)) {
                return;
            }
            if (theroom.isPasswordProtected() && password.length() == 0) {
                //send notification to user to give the password(send ID too)
                Protocol.requestPassword(thisPlayer.getConnection(), information[0]);
                return;
            }
        } else {
            String hostname = information[1];
            host = PlayerData.searchByName(hostname);
            password = information.length == 3 ? information[2] : "";
        }

        if (host != null && host.getCurrentRoom() != null) {//ROOM EXISTS
            theroom = host.getCurrentRoom();

            if (theroom.getHost().isBanned(thisPlayer.getPid())) {// is the player banned?
                Protocol.errorYouAreBanned(thisPlayer.getConnection());
                return;
            }

            if (!theroom.passwordCheck(password)) {
                Protocol.wrongPasswordAtRoomJoin(thisPlayer.getConnection());
                return;
            }
            try {
                host.getCurrentRoom().addPlayer(thisPlayer);
                thisPlayer.setCurrentRoom(theroom);
            } catch (Exception ex) {
                //Room is full
                thisPlayer.setCurrentRoom(null);
                Protocol.errorRoomIsFull(thisPlayer.getConnection());
                return;
            }

            thisPlayer.setNotReady();
            //thisPlayer.setPlaying(false); dont set so playing status is preserved? what about errors?
            //joined, send data to client
            if (theroom.isInstantLaunched()) {
                //instant launch
                Protocol.sendInstantLaunchCommand(thisPlayer.getConnection(), theroom);
                Protocol.sendJoinNotification(currentchannel, host, thisPlayer);
                Protocol.sendChannelPlayingStatus(currentchannel, thisPlayer);
            } else {
                //normal join
                Protocol.joinRoom(thisPlayer.getConnection(), currentchannel, theroom);
                Protocol.sendJoinNotification(currentchannel, host, thisPlayer);
                //send settings to newcommer
                for (Entry<String, String> entry : theroom.getSettings()) {
                    Protocol.sendSetting(thisPlayer.getConnection(), entry.getKey(), entry.getValue());
                }
                //refresh players list of room
                for (Player member : thisPlayer.getCurrentRoom().getPlayers()) {
                    if (member.isReady()) {
                        Protocol.sendReadyStatus(thisPlayer.getConnection(), member);
                    }

                    if (member.isPlaying()) {
                        Protocol.sendRoomPlayingStatus(thisPlayer.getConnection(), member);
                    }
                    if (member.isAway()) {
                        Protocol.setAwayStatus(thisPlayer, member);
                    }
                }
            }
        } else {
            Protocol.errorRoomDoesNotExist(thisPlayer.getConnection());
        }
    }

    /**
     * handles room creating
     */
    protected static void createRoom(Player thisPlayer, String[] information) {
        //close previous rooms made by player
        Channel currentChannel = ChannelData.getChannel(information[0]);

        Room[] ghostrooms = currentChannel.getGhostRooms(thisPlayer);
        if (ghostrooms != null) {
            for (Room g : ghostrooms) {
                g.close();
            }
        }

        if(thisPlayer.getCurrentRoom() != null){
            thisPlayer.getCurrentRoom().close();
        }

        thisPlayer.setNotReady();

        String name = information[1];
        String password = information[2];
        int limit = Room.LIMIT;
        try {
            limit = Integer.parseInt(information[3]);
        } catch (Exception e) {
        }
        boolean instantLaunch = Boolean.parseBoolean(information[4]);
        boolean doSearch = Boolean.parseBoolean(information[7]);
        String hamachiip = information[5];
        String modindex = information[6];

        Room newroom = new Room(name, //name
                thisPlayer, // host
                password, //password
                limit, //maxplayers
                currentChannel,//the channel that the room is in
                instantLaunch,
                hamachiip,
                doSearch); // ip on hamachi

        newroom.setModIndex(modindex);
        currentChannel.addRoom(newroom);

        if (!instantLaunch) {
            Protocol.createRoom(thisPlayer.getConnection(), currentChannel, newroom);
            if (thisPlayer.isPlaying()) {
                Protocol.sendRoomPlayingStatus(thisPlayer.getConnection(), thisPlayer);
            }
        } else {
            Protocol.sendChannelPlayingStatus(currentChannel, thisPlayer);
        }
        thisPlayer.setCurrentRoom(newroom);
    }

    /**
     * returns the command needed to send to add the room
     */
    public static void saveProfile(Connection con, Player thisPlayer, String[] information) throws SQLException, VerificationException {
        //info = { loginName, ingameName,  email,  country,  website };
        if (!thisPlayer.getLoginName().equals(information[0])) { //name changed
            if (!Verification.verifyLoginName(information[0])) {
                throw new VerificationException("Failure on loginname! " + information[0]);
            }
            synchronized (Database.class) { //this has to be threadsafe, because playernames may collide
                boolean nameExists = Database.loginNameExists(information[0]);
                if (!nameExists) {
                    String oldname = thisPlayer.getLoginName();
                    thisPlayer.updateLoginName(oldname,information[0]);
                    PlayerData.updateName(oldname, information[0]);
                } else {
                    Protocol.errorLoginnameIsAlreadyUsed(thisPlayer.getConnection());
                    return;
                }
            }
        }
        if (!Verification.verifyIngameName(information[1])) {
            throw new VerificationException("Failure on ingamename! " + information[1]);
        }
        if (!thisPlayer.getIngameName().equals(information[1])) { //name changed
            thisPlayer.setIngameName(information[1]);
            Protocol.setInGameName(thisPlayer);
        }

        if (!Verification.verifyEMail(information[2])) {
            throw new VerificationException("Failure on email! " + information[2]);
        }
        //player might want to set empty email
        thisPlayer.setEmail(information[2]);

        String website = information[4];
        if (!Verification.verifyWebsite(website)) {
            throw new VerificationException("Failure on website! " + information[4]);
        }
        thisPlayer.setWebsite(website);
        String country = information[3];
        if (!Verification.verifyCountry(country)) {
            throw new VerificationException("Failure on country! " + information[3]);
        }       
        thisPlayer.setCountry(country);        
        Protocol.confirmProfileChange(con);
    }
}
