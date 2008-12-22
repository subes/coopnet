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
package coopnetclient.frames.models;

import coopnetclient.enums.PlayerStatuses;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class ChannelStatusListModel extends AbstractListModel {

    private class PlayerStatusData {

        String name;
        boolean isInRoom = false;
        boolean isPlaying = false;
        boolean isAway = false;

        PlayerStatusData(String name) {
            this.name = name;
        }

        public PlayerStatuses getStatus() {
            if (isAway) {
                return PlayerStatuses.AWAY;
            }
            if (isPlaying) {
                return PlayerStatuses.PLAYING;
            }
            if (isInRoom) {
                return PlayerStatuses.IN_ROOM;
            }
            return PlayerStatuses.CHATTING;
        }
    }
    private ArrayList<PlayerStatusData> statuses = new ArrayList<PlayerStatusData>();
    private TreeSet chattingList = new TreeSet();
    private TreeSet inRoomList = new TreeSet();
    private TreeSet playingList = new TreeSet();
    private TreeSet awayList = new TreeSet();

    public ChannelStatusListModel() {
    }

    private int statusIndexOf(String playerName) {
        for (int i = 0; i < statuses.size(); ++i) {
            if (statuses.get(i).name.equals(playerName)) {
                return i;
            }
        }
        return -1;
    }

    public PlayerStatuses getPlayerStatus(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return null;
        }
        PlayerStatusData player = statuses.get(idx);
        return player.getStatus();
    }

    boolean isPlaying(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return false;
        }
        PlayerStatusData player = statuses.get(idx);
        return player.isPlaying;
    }

    private void relocatePlayer(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        PlayerStatuses status = player.getStatus();
        removeplayer(playerName);
        switch (status) {
            case AWAY:
                awayList.add(playerName);
                break;
            case CHATTING:
                chattingList.add(playerName);
                break;
            case IN_ROOM:
                inRoomList.add(playerName);
                break;
            case PLAYING:
                playingList.add(playerName);
                break;
        }
        statuses.add(player);
    }

    private void removeplayer(String playerName) {
        chattingList.remove(playerName);
        playingList.remove(playerName);
        inRoomList.remove(playerName);
        awayList.remove(playerName);
        statuses.remove(playerName);
    }

    public void playerEnteredChannel(String playerName) {
        if (!chattingList.contains(playerName)) {
            chattingList.add(playerName);
            statuses.add(new PlayerStatusData(playerName));
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void setAway(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isAway = true;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void unSetAway(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isAway = false;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftChannel(String playerName) {
        removeplayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerEnteredRoom(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isInRoom = true;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftRoom(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isInRoom = false;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLaunchedGame(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isPlaying = true;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerClosedGame(String playerName) {
        int idx = statusIndexOf(playerName);
        if (idx < 0) {
            return;
        }
        PlayerStatusData player = statuses.get(idx);
        player.isPlaying = false;
        relocatePlayer(playerName);
        fireContentsChanged(this, 0, getSize());
    }

    public boolean updateName(String oldname, String playerName) {
        boolean found = false;
        if (chattingList.remove(oldname)) {
            chattingList.add(playerName);
            found = true;
        }
        if (playingList.remove(oldname)) {
            playingList.add(playerName);
            found = true;
        }
        if (inRoomList.remove(oldname)) {
            inRoomList.add(playerName);
            found = true;
        }
        if (awayList.remove(oldname)) {
            awayList.add(playerName);
            found = true;
        }
        int idx = statusIndexOf(oldname);
        if(idx >-1){
            PlayerStatusData player = statuses.get(idx);
            player.name = playerName;
        }
        return found;
    }

    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        return chattingList.size() + inRoomList.size() + playingList.size() + awayList.size();
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        if (index < chattingList.size()) {
            return chattingList.toArray()[index];
        } else if (index < chattingList.size() + inRoomList.size()) {
            index -= chattingList.size();
            return inRoomList.toArray()[index];
        } else if (index < chattingList.size() + inRoomList.size() + playingList.size()) {
            index -= chattingList.size() + inRoomList.size();
            return playingList.toArray()[index];
        } else {
            index -= chattingList.size() + inRoomList.size() + playingList.size();
            return awayList.toArray()[index];
        }
    }

    public void refresh() {
        chattingList.addAll(inRoomList);
        inRoomList.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        chattingList.clear();
        inRoomList.clear();
        playingList.clear();
        awayList.clear();
        statuses.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return chattingList.contains(element) || inRoomList.contains(element) || playingList.contains(element) || awayList.contains(element);
    }
}
