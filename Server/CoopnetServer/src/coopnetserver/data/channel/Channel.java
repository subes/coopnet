package coopnetserver.data.channel;

import coopnetserver.data.room.Room;
import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.player.Player;
import java.util.Vector;

/**
 * implements channels, each game has its own channel
 */
public class Channel {

    public String ID = "";
    private Vector<Player> playersInChannel = new Vector<Player>();
    private Vector<Room> roomsInChannel = new Vector<Room>();
    public String name;

    /** Creates a new instance of Channel */
    public Channel(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }

    public Room[] getGhostRooms(Player host) {
        Vector<Room> rooms = new Vector<Room>();
        for (Room room : roomsInChannel) {
            if (room.getHost().equals(host)) {
                rooms.add(room);
            }
        }
        if (rooms.size() == 0) {
            return null;
        } else {
            return rooms.toArray(new Room[1]);
        }
    }

    /**
     * returns a copy array of the players in the channel 
     */
    public Player[] getPlayersInChannel() {
        Player[] copy = new Player[playersInChannel.size()];
        return playersInChannel.toArray(copy);
    }

    /**
     * adds a new player to the channel
     */
    public void addPlayer(Player player) {
        Protocol.addPlayerToChannel(this, player);
        playersInChannel.add(player);        
    }

    /**
     * removes a player from the channel
     */
    public void removePlayer(Player player) {
        if (playersInChannel.remove(player)) {
            Protocol.removePlayerFromChannel(this, player);            
        }
    }

    public boolean containsPlayer(Player player) {
        return playersInChannel.contains(player);
    }

    /**
     * adds a room to the channel
     */
    public void addRoom(Room room) {
        roomsInChannel.add(room);
        Protocol.addNewRoom(this, room);
    }

    /**
     * returns a vector of the current rooms
     */
    public Vector<Room> getRooms() {
        return roomsInChannel;
    }

    /**
     * removes a room from the channel
     */
    public void closeRoom(Room room) {
        roomsInChannel.remove(room);
    }

    /**
     * searches for with matching name or host.
     * may return null
     */
    public Room search(String name, String host) {
        for (Room room : roomsInChannel) {
            if (room.getName().equals(name) && room.getHost().getLoginName().equals(host)) {
                return room;
            }
        }
        return null;
    }
}
