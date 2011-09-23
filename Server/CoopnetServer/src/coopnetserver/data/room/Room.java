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
package coopnetserver.data.room;

import coopnetserver.data.channel.*;
import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.player.Player;
import coopnetserver.data.player.PlayerData;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * Class implementing game rooms
 */
public class Room {

    public static final int NORMAL_UNPASSWORDED_ROOM = 0;
    public static final int NORMAL_PASSWORDED_ROOM = 1;
    public static final int INSTANT_UNPASSWORDED_ROOM = 2;
    public static final int INSTANT_PASSWORDED_ROOM = 3;
    public static final int LIMIT = 999;
    //INSTANCE
    private String name;
    private long ID;
    private Player host;
    private String password;
    private Vector<Player> players = new Vector<Player>();
    private int maxPlayers = LIMIT;
    public Channel parent;
    private String hamachiIp;
    private boolean searchEnabled;
    private String modIndex;
    private boolean instantlaunchenabled = false;
    private HashMap<String, String> settings = new HashMap<String, String>();

    /**
     * sends the launch command to every ready player in the room
     */
    public void launch() {
        for (Player player : players) {
            if (player.isReady() && !player.isPlaying()) {
                if (player.getSleepModeEnabled()) {
                    player.setSleepMode(true);
                }
                player.setAway(false);
                player.setPlaying(true);
                if (!player.equals(host)) {
                    Protocol.launch(player);
                }
                Protocol.sendRoomPlayingStatusToRoom(this, player);
                if (parent.containsPlayer(player)) {
                    Protocol.sendChannelPlayingStatus(parent, player);
                }
                player.sendMyContactStatusToMyContacts();
            }
        }
    }

    /**
     * returns if the password is valid for this room
     */
    public boolean passwordCheck(String pw) {
        return pw.equals(this.password);
    }

    public int getType() {
        if (isPasswordProtected()) {
            if (instantlaunchenabled) {
                return INSTANT_PASSWORDED_ROOM;
            } else {
                return NORMAL_PASSWORDED_ROOM;
            }
        } else {
            if (instantlaunchenabled) {
                return INSTANT_UNPASSWORDED_ROOM;
            } else {
                return NORMAL_UNPASSWORDED_ROOM;
            }
        }
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public boolean isInstantLaunched() {
        return instantlaunchenabled;
    }

    public void setInstantLaunchable(boolean value) {
        this.instantlaunchenabled = value;
    }

    public void setSetting(String name, String value) {
        settings.put(name, value);

        for (Player connection : players) {
            Protocol.sendSetting(connection.getConnection(), name, value);
        }
    }

    public String getSetting(String name) {
        return settings.get(name);
    }

    public Set<Entry<String, String>> getSettings() {
        return settings.entrySet();
    }

    public String getModIndex() {
        return modIndex;
    }

    public void setModIndex(String mod) {
        this.modIndex = mod;
    }

    /**
     * returns the rooms name
     */
    public String getName() {
        return name;
    }

    public String getHamachiIp() {
        return hamachiIp;
    }

    /**
     * returns the rooms player limit
     */
    public int getLimit() {
        return this.maxPlayers;
    }

    /**
     * returns the rooms current noumber of players
     */
    public int getCurrentPlayers() {
        return this.players.size();
    }

    /**
     * sets the rooms name
     */
    public void setName(String newname) {
        this.name = newname;
    }

    /**
     * returns the rooms host
     */
    public Player getHost() {
        return host;
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    /**
     * returns if the room is password protected
     */
    public boolean isPasswordProtected() {
        if (password.equals("")) {
            return false;
        }
        return true;
    }

    public String getPassword() {
        return password;
    }

    /**
     * adds a new member to the room
     */
    public void addPlayer(Player player) {
        if (players.size() == maxPlayers) {
            player.setCurrentRoom(null);
            Protocol.errorRoomIsFull(player.getConnection());
            return;
        }
        if (player.getCurrentRoom() != null && player.getCurrentRoom().equals(this)) {
            return;
        }

        if (host.isBanned(player.getPid())) {// is the player banned?
            Protocol.errorYouAreBanned(player.getConnection());
            return;
        }

        Protocol.addMemberToRoom(this, player);
        if (!players.contains(player)) {
            players.add(player);
        }

        player.setCurrentRoom(this);
        player.setNotReady();
        //joined, send data to client
        if (isInstantLaunched()) {
            //instant launch
            Protocol.sendInstantLaunchCommand(player.getConnection(), this);
            Protocol.sendJoinNotification(parent, host, player);
            Protocol.sendChannelPlayingStatus(parent, player);
        } else {
            //normal join
            Protocol.joinRoom(player.getConnection(), parent, this);
            Protocol.sendJoinNotification(parent, host, player);
            //send settings to newcommer
            for (Entry<String, String> entry : getSettings()) {
                Protocol.sendSetting(player.getConnection(), entry.getKey(), entry.getValue());
            }
            //refresh players list of room
            for (Player member : player.getCurrentRoom().getPlayers()) {
                if (member.isReady()) {
                    Protocol.sendReadyStatus(player.getConnection(), member);
                }
                if (member.isPlaying()) {
                    Protocol.sendRoomPlayingStatus(player.getConnection(), member);
                }
                if (member.isAway()) {
                    Protocol.setAwayStatus(player, member);
                }
            }
        }
    }

    /**
     * removes the member from the room, if it was the host the room is closed.
     * members are notifyed
     */
    public void removePlayer(Player player) {
        if (PlayerData.getOnlinePlayers().contains(player) && !isInstantLaunched()) {
            Protocol.leaveRoom(player.getConnection());
        }
        if (host.getLoginName().equals(player.getLoginName())) {
            this.close();
        } else {
            players.remove(player);
            if (!isInstantLaunched()) {
                Protocol.removeMemberFromRoom(this, player);
                Protocol.removePlayerFromRoomInList(parent, player.getCurrentRoom().host, player);
            }
        }

        player.setCurrentRoom(null);
    }

    /**
     * returns a copy array with the members 
     */
    public Player[] getPlayers() {
        Player[] copy = new Player[players.size()];
        return players.toArray(copy);
    }

    /**
     * closes this room.
     * all members are notifyed.
     * the room is removed from its channel(parent).
     */
    public void close() {
        Protocol.closeRoom(this, parent);
        for (Player member : players) {
            Protocol.removePlayerFromRoomInList(parent, host, member);
            member.setCurrentRoom(null);
        }
        parent.closeRoom(this);
        RoomData.unRegisterRoom(this);
        Protocol.removeRoom(parent, host);
    }

    /**
     * creates a new room
     */
    public Room(String name, Player host, String password, int limit, Channel channel, boolean instantLaunch, String hamachiip, boolean doSearch) {
        this.name = name;
        this.host = host;
        if (limit > LIMIT || limit < 0) {
            limit = LIMIT;
        }
        this.maxPlayers = limit;
        this.password = password;
        players.add(host);
        parent = channel;
        this.instantlaunchenabled = instantLaunch;
        this.hamachiIp = hamachiip;
        this.searchEnabled = doSearch;
        RoomData.registerRoom(this);
    }
}
