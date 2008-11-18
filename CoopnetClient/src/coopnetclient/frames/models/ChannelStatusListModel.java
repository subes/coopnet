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

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class ChannelStatusListModel extends AbstractListModel {

    private TreeSet chattingList = new TreeSet();
    private TreeSet inRoomList = new TreeSet();
    private TreeSet playingList = new TreeSet();
    private Vector<String> leftRoom = new Vector<String>(); //left room while playing

    public ChannelStatusListModel() {
    }

    private void removeplayer(String playername) {
        chattingList.remove(playername);
        playingList.remove(playername);
        inRoomList.remove(playername);
        leftRoom.remove(playername);
    }

    public void playerEnteredChannel(String playername) {
        if (!chattingList.contains(playername)) {
            chattingList.add(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftChannel(String playername) {
        removeplayer(playername);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerEnteredRoom(String playername) {
        if (playingList.contains(playername)) {
            if (leftRoom.contains(playername)) {
                leftRoom.remove(playername);
                fireContentsChanged(this, 0, getSize());
                return;
            }
        } else {
            inRoomList.add(playername);
            chattingList.remove(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftRoom(String playername) {
        if (playingList.contains(playername)) {
            leftRoom.add(playername);
        } else {
            if (inRoomList.remove(playername)) {
                chattingList.add(playername);
            }
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLaunchedGame(String playername) {
        if (inRoomList.remove(playername)) {
            playingList.add(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerClosedGame(String playername) {
        if (playingList.contains(playername)) {
            if (leftRoom.contains(playername)) {
                playingList.remove(playername);
                leftRoom.remove(playername);
                chattingList.add(playername);
                fireContentsChanged(this, 0, getSize());
                return;
            } else {
                inRoomList.add(playername);
                playingList.remove(playername);
            }
        } else {
        }
        fireContentsChanged(this, 0, getSize());
    }

    public boolean isInRoom(Object value) {
        return inRoomList.contains(value);
    }

    public boolean isPlaying(Object value) {
        return playingList.contains(value);
    }

    public boolean updateName(String oldname, String playername) {
        boolean found = false;
        if (chattingList.remove(oldname)) {
            chattingList.add(playername);
            found = true;
        }
        if (playingList.remove(oldname)) {
            playingList.add(playername);
            found = true;
        }
        if (inRoomList.remove(oldname)) {
            inRoomList.add(playername);
            found = true;
        }
        return found;
    }

    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        return chattingList.size() + inRoomList.size() + playingList.size();
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        if (index < chattingList.size()) {
            return chattingList.toArray()[index];
        } else if (index < chattingList.size() + inRoomList.size()) {
            index -= chattingList.size();
            return inRoomList.toArray()[index];
        } else {
            index -= chattingList.size() + inRoomList.size();
            return playingList.toArray()[index];
        }
    }

    // Other methods
    public void add(Object element) {
    }

    public void addAll(Object elements[]) {
    }

    public void refresh() {
        chattingList.addAll(inRoomList);
        //chattinglist.addAll(playinglist);
        inRoomList.clear();
        //playinglist.clear();
        leftRoom.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        chattingList.clear();
        inRoomList.clear();
        playingList.clear();
        leftRoom.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return chattingList.contains(element) || inRoomList.contains(element) || playingList.contains(element);
    }

    public Object firstElement() {
        // Return the appropriate element
        return chattingList.first();
    }

    public Iterator iterator() {
        return chattingList.iterator();
    }

    public Object lastElement() {
        // Return the appropriate element
        return playingList.last();
    }

    public boolean removeElement(Object element) {
        return false;
    }
}
