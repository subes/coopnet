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

package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.util.Map;

public class RoomData {

    private boolean isHost;
    private boolean doSearch;
    private boolean isInstant;
    private String interfaceKey = Globals.INTERNET_INTERFACE_NAME;
    private String channel;    
    private String modName;
    private Map<String,String> interfaceIPs;
    private String hostName;
    private String roomName;
    private String password;
    private int maxPlayers;
    private int modIndex;
    private long roomID;

    public RoomData(boolean isHost, String channel, int modIndex, Map<String,String> interfaceIPs, int maxPlayers , String hostName,String roomName,long ID,String password, boolean doSearch,boolean isInstant){
        this.isHost = isHost;
        this.doSearch = doSearch;
        this.channel = channel;
        this.modIndex = modIndex;
        this.interfaceIPs = interfaceIPs;
        this.hostName = hostName;
        this.roomName = roomName;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.roomID = ID;
        if ( modIndex == -1) {
            this.modName = null;
        } else {
            this.modName = GameDatabase.getGameModNames(channel)[modIndex].toString();
        }
        this.isInstant = isInstant;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    public boolean isDoSearch() {
        return doSearch;
    }

    public void setIsDoSearch(boolean doSearch) {
        this.doSearch = doSearch;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getModIndex() {
        return modIndex;
    }

    public void setModIndex(int modIndex) {
        this.modIndex = modIndex;
    }

    public String getIP() {
        return interfaceIPs.get(getInterfaceKey());
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public boolean isInstant() {
        return isInstant;
    }

    public void setIsInstant(boolean isInstant) {
        this.isInstant = isInstant;
    }

    public String getInterfaceKey() {
        return interfaceKey;
    }

    public void setInterfaceKey(String interfaceKey) {
        this.interfaceKey = interfaceKey;
    }

    public Map<String, String> getInterfaceIPs() {
        return interfaceIPs;
    }
}
