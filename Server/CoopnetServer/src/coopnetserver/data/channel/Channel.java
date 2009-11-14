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
