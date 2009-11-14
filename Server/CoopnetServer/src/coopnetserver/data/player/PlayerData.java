package coopnetserver.data.player;

import coopnetserver.data.channel.ChannelData;
import coopnetserver.data.*;
import coopnetserver.data.connection.Connection;
import coopnetserver.data.connection.ConnectionData;
import coopnetserver.enums.LogTypes;
import coopnetserver.utils.Logger;
import java.util.HashMap;
import java.util.Vector;

/**
 * Handles the player database
 */
public class PlayerData {

    private static Vector<Player> onlinePlayers = new Vector<Player>();
    private static HashMap<Long, Player> playersByID = new HashMap<Long, Player>();
    private static HashMap<String, Player> playersByName = new HashMap<String, Player>();

    /**
     * returns a vector of online players
     */
    public static Vector<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * first checks if the player is already logged in, currently it logs off the existing session.
     * Then adds the player to the online players: adds to the vector in the database and also registeres in the server
     * always retruns true ( other policy(protecting existing session) required returning false )
     */
    public static boolean logIn(Player me, Connection con) {
        if (onlinePlayers.contains(me)) {
            int idx = onlinePlayers.indexOf(me);
            logOff(onlinePlayers.get(idx));
        }

        onlinePlayers.add(me);
        me.setPlaying(false);
        me.setNotReady();
        playersByID.put(me.getPid(), me);
        playersByName.put(me.getLoginName(), me);

        Logger.log(LogTypes.PLAYER, me.getLoginName() + " logged in!", con);

        return true;
    }

    /**
     * Attention: use the logoff task to log a player off!
     * Logs off the player:
     * removes it from the vector of online players and unregisteres in the server.
     * if player was in a room it is removed from it.
     * the player is removed from the channel aswell.
     */
    public static void logOff(Player user) {
        if (user.getCurrentRoom() != null) {
            user.getCurrentRoom().removePlayer(user);
        }

        ChannelData.removePlayerFromAllChannels(user);
        user.sendLogOffNotificationToMyContacts();

        if (onlinePlayers.contains(user)) {
            onlinePlayers.remove(user);
        }
        
        Logger.log(LogTypes.PLAYER, user.getLoginName() + " logged off!", user.getConnection());
        ConnectionData.removeConnection(user.getConnection());
        
        playersByID.remove(user.getPid());
        playersByName.remove(user.getLoginName());
    }

    public static void logOff(Connection con) {
        Player p = con.getPlayer();
        if(p != null){
            logOff(p);
        }else{
            ConnectionData.removeConnection(con);
        }
        con.setPlayer(null);
    }

    /**
     * returns a player with matching name
     */
    public static Player searchByName(String name) {
        return playersByName.get(name);
    }

    public static Player searchByID(long ID) {
        return playersByID.get(ID);
    }

    public static Player getOnlinePlayerAtIndex(int index) {
        return onlinePlayers.elementAt(index);
    }

    public static int onlineDataBaseSize() {
        return onlinePlayers.size();
    }

    public static void updateName(String oldName, String newName) {
        Player p = playersByName.remove(oldName);
        if (p != null) {
            playersByName.put(newName, p);
        }
    }
}