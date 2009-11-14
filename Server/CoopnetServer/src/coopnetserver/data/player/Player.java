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
package coopnetserver.data.player;

import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.channel.Channel;
import coopnetserver.data.connection.Connection;
import coopnetserver.data.room.Room;
import coopnetserver.enums.ContactListElementTypes;
import coopnetserver.utils.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class Player {

    //Data which is from Database
    private long pid;
    private String loginName;
    private String ingameName;
    private String password;
    private String email;
    private String website;
    private String country;
    private boolean sleepMode;
    private boolean sleepModeEnabled = true;
    private boolean isAway;
    private Room currentroom;
    private boolean ready;
    private boolean isPlaying;
    private Channel playingOn;
    private Vector<Long> muteList;
    private Vector<Long> banList;
    private ContactList contactList;
    private Connection con;
    private Vector<Channel> joinedChannels = new Vector<Channel>();

    public Player(Connection con, String loginName) throws SQLException {
        initialize(con, Database.getPlayerDataByName(loginName));
    }

    public Player(Connection con, long pid) throws SQLException {
        initialize(con, Database.getPlayerDataByID(pid));
    }

    public void sendMuteBanData() throws SQLException {
        ArrayList<String> data = new ArrayList<String>();
        Player currentPlayer = null;
        //mutelist
        for (Long userID : muteList) {
            currentPlayer = PlayerData.searchByID(userID);
            if (currentPlayer != null) {
                data.add(currentPlayer.getLoginName());
            } else {
                data.add(Database.getLoginName(userID));
            }
        }

        data.add("");

        //banlist
        for (Long userID : banList) {
            currentPlayer = PlayerData.searchByID(userID);
            if (currentPlayer != null) {
                data.add(currentPlayer.getLoginName());
            } else {
                data.add(Database.getLoginName(userID));
            }
        }

        String[] d = new String[data.size()];
        data.toArray(d);

        Protocol.sendMuteBanData(this.getConnection(), d);
    }

    private void initialize(Connection con, String[] playerData) throws SQLException {
        this.con = con;
        this.pid = Long.parseLong(playerData[0]);
        this.loginName = playerData[1];
        this.ingameName = playerData[2];
        this.password = playerData[3];
        this.email = playerData[4];
        this.website = playerData[5];
        this.country = playerData[6];

        muteList = Database.getMuteList(this);
        banList = Database.getBanList(this);
        contactList = new ContactList(this);

        this.isAway = false;
        Database.updateLastLogin(this);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
        if (isPlaying) {
            if (currentroom != null) {
                playingOn = currentroom.parent;
            }
        } else {
            playingOn = null;
        }
    }

    public void setPlayingOnChannel(Channel ch) {
        playingOn = ch;
    }

    public void setCurrentRoom(Room room) {
        this.currentroom = room;
        sendMyContactStatusToMyContacts();
    }

    public Room getCurrentRoom() {
        return this.currentroom;
    }

    /**
     * joins the specified channel
     * removes ghostrooms and sends commands to client
     */
    public void joinChannel(Channel ch) {
        if (ch == null) {
            return;
        }
        joinedChannels.add(ch);
        //add this player to the channel
        ch.addPlayer(this);
        Protocol.joinChannel(this.getConnection(), ch);
        if (isPlaying && ch.equals(playingOn)) {
            Protocol.sendChannelPlayingStatus(ch, this);
        }
    }

    /**
     * unregisters from the channel
     */
    public void leaveChannel(Channel ch) {
        ch.removePlayer(this);
        joinedChannels.remove(ch);
    }

    /**
     * negates the ready status
     */
    public void flipReady() {
        if (currentroom == null) {
            return;
        }
        ready = !ready;
        if (!ready) {
            setPlaying(false);
        }
        Protocol.sendReadyStatus(currentroom, this);
    }

    /**
     * returns the ready status
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * sets the ready status to false
     */
    public void setNotReady() {
        ready = false;
    }

    /**
     * returns the email adress of the player (if any) or empty string
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email adress
     */
    public void setEmail(String email) throws SQLException {
        Database.updateEmail(this, email);
        this.email = email;
    }

    public long getPid() {
        return this.pid;
    }

    /**
     * returns the webpage of the player (if any) or empty string
     */
    public String getWebsite() {
        return website;
    }

    /**
     * sets the webpage
     */
    public void setWebsite(String website) throws SQLException {
        Database.updateWebsite(this, website);
        this.website = website;
    }

    /**
     * returns the country of the player. if its not set (old version) returns empty string
     */
    public String getCountry() {
        return country;
    }

    /**
     * sets the country
     */
    public void setCountry(String country) throws SQLException {
        Database.updateCountry(this, country);
        this.country = country;
    }

    /**
     * returns the players password
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     */
    public void setPassword(String password) throws SQLException {
        Database.updatePassword(pid, password);
        this.password = password;
    }

    /**
     * returns the players ingame-name
     */
    public String getIngameName() {
        return ingameName;
    }

    /**
     * sets the ingame-name
     */
    public void setIngameName(String ingameName) throws SQLException {
        Database.updateIngameName(this, ingameName);
        this.ingameName = ingameName;
    }

    /**
     * returns the players login name
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * sets the login name
     */
    public void updateLoginName(String oldName, String loginName) throws SQLException {
        Database.updateLoginName(this, loginName);

        for (Channel ch : joinedChannels) {
            for (Player p : ch.getPlayersInChannel()) {
                Protocol.updateName(p.getConnection(), oldName, loginName);
            }
        }

        Player currentContact;
        for (Long playerID : contactList.getContactsWhoKnowMe()) {
            currentContact = PlayerData.searchByID(playerID);
            if (currentContact != null) {
                Protocol.updateName(currentContact.getConnection(), this.loginName, loginName);
            }
        }
        this.loginName = loginName;
    }

    /**
     * returns that this player muted the user in parameter or not
     */
    public boolean isMuted(Player user) {
        return muteList.contains(user.getPid());
    }

    public Object[] getMuteList() {
        return muteList.toArray();
    }

    public Object[] getBanList() {
        return banList.toArray();
    }

    public void mute(long playerID) throws SQLException {
        if (!muteList.contains(playerID)) {
            Database.mutePlayer(this, playerID);
            muteList.add(playerID);
        }
    }

    /**
     * removes the player in parameter from the mutelist
     */
    public void unMute(long playerID) throws SQLException {
        Database.unMutePlayer(this, playerID);
        muteList.remove(playerID);
    }

    /**
     * adds the player in parameter to the banlist
     */
    public void ban(long playerID) throws SQLException {
        if (!banList.contains(playerID)) {
            Database.banPlayer(this, playerID);
            banList.add(playerID);
        }
    }

    /**
     * removes the player in parameter from the banlist
     */
    public void unBan(long playerID) throws SQLException {
        Database.unBanPlayer(this, playerID);
        banList.remove(playerID);
    }

    /**
     * returns that this player banned the user in parameter or not
     */
    public boolean isBanned(long pid) {
        return banList.contains(pid);
    }

    /**
     * sets the players contactlist
     */
    /*public void setContactList(ContactList list) {
    contactList = list;
    }*/
    /**
     * returnrs the players contactlist
     */
    public ContactList getContactList() {
        return contactList;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            Player p = (Player) o;
            if (p.getPid() == (this.getPid())) {
                return true;
            }
        }
        return false;
    }
//CHECKSTYLE:OFF

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.pid ^ (this.pid >>> 32));
        return hash;
    }
//CHECKSTYLE:ON

    public ContactListElementTypes getContactStatus() {
        if (isAway) {
            return ContactListElementTypes.AWAY;
        } else if (isPlaying) {
            return ContactListElementTypes.PLAYING;
        } else {
            if (currentroom != null) {
                return ContactListElementTypes.IN_ROOM;
            } else {
                return ContactListElementTypes.CHATTING;
            }
        }
    }

    public void sendContactData(boolean showOffline) throws SQLException {
        contactList.sendContactData(showOffline);
    }

    public void sendLogOffNotificationToMyContacts() {
        synchronized (contactList) {
            Player currentContact;
            for (Long playerID : contactList.getContactsWhoKnowMe()) {
                currentContact = PlayerData.searchByID(playerID);
                if (currentContact != null) {
                    Protocol.sendContactStatus(currentContact.getConnection(), this, ContactListElementTypes.OFFLINE);
                }
            }
        }
    }

    public void sendMyContactStatusToMyContacts() {
        synchronized (contactList) {
            Player currentContact = null;
            for (Long playerID : contactList.getContactsWhoKnowMe()) {
                currentContact = PlayerData.searchByID(playerID);
                if (currentContact != null) {
                    Protocol.sendContactStatus(currentContact.getConnection(), this, this.getContactStatus());
                }
            }
        }
    }

    public void setSleepModeEnabled(boolean enabled) {
        this.sleepModeEnabled = enabled;
    }

    public boolean getSleepModeEnabled() {
        return sleepModeEnabled;
    }

    public void setSleepMode(boolean enabled) {
        if (sleepModeEnabled) {
            this.sleepMode = enabled;
        } else {
            this.sleepMode = false;
        }
    }

    public boolean getSleepMode() {
        if (sleepModeEnabled) {
            return sleepMode;
        } else {
            return false;
        }
    }

    public boolean isAway() {
        return isAway;
    }

    public void setAway(boolean away) {
        if (away != isAway) {
            isAway = away;
            ArrayList<Player> targets = new ArrayList<Player>();
            for (Channel ch : joinedChannels) {
                for (Player p : ch.getPlayersInChannel()) {
                    if (!targets.contains(p)) {
                        targets.add(p);
                    }
                }
            }
            for (Player p : targets) {
                if (away) {
                    Protocol.setAwayStatus(p, this);
                } else {
                    Protocol.unSetAwayStatus(p, this);
                }
            }
            sendMyContactStatusToMyContacts();
        }
    }

    public Connection getConnection() {
        return con;
    }
}
