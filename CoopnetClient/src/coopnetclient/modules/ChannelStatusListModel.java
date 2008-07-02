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

package coopnetclient.modules;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class ChannelStatusListModel extends AbstractListModel {

    private TreeSet chattinglist = new TreeSet();
    private TreeSet inroomlist = new TreeSet();
    private TreeSet playinglist = new TreeSet();
    private Vector<String> leftroom = new Vector<String>(); //left room while playing

    public ChannelStatusListModel() {
    }

    private void removeplayer(String playername) {
        chattinglist.remove(playername);
        playinglist.remove(playername);
        inroomlist.remove(playername);
        leftroom.remove(playername);
    }

    public void playerEnteredChannel(String playername) {
        if (!chattinglist.contains(playername)) {
            chattinglist.add(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftChannel(String playername) {
        removeplayer(playername);
        fireContentsChanged(this, 0, getSize());
    }

    public void playerEnteredRoom(String playername) {
        if (playinglist.contains(playername)) {
            if (leftroom.contains(playername)) {
                leftroom.remove(playername);
                fireContentsChanged(this, 0, getSize());
                return;
            }
        } else {
            inroomlist.add(playername);
            chattinglist.remove(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLeftRoom(String playername) {
        if (playinglist.contains(playername)) {
            leftroom.add(playername);
        } else {
            if (inroomlist.remove(playername)) {
                chattinglist.add(playername);
            }
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerLaunchedGame(String playername) {
        if (inroomlist.remove(playername)) {
            playinglist.add(playername);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void playerClosedGame(String playername) {
        if (playinglist.contains(playername)) {
            if (leftroom.contains(playername)) {
                playinglist.remove(playername);
                leftroom.remove(playername);
                chattinglist.add(playername);
                fireContentsChanged(this, 0, getSize());
                return;
            } else {
                inroomlist.add(playername);
                playinglist.remove(playername);
            }
        } else {
        }
        fireContentsChanged(this, 0, getSize());
    }

    public boolean isInRoom(Object value) {
        return inroomlist.contains(value);
    }

    public boolean isPlaying(Object value) {
        return playinglist.contains(value);
    }

    public void updatename(String oldname, String playername) {
        if (chattinglist.remove(oldname)) {
            chattinglist.add(playername);
        }
        if (playinglist.remove(oldname)) {
            playinglist.add(playername);
        }
        if (inroomlist.remove(oldname)) {
            inroomlist.add(playername);
        }
    }

    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        return chattinglist.size() + inroomlist.size() + playinglist.size();
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        if (index < chattinglist.size()) {
            return chattinglist.toArray()[index];
        } else if (index < chattinglist.size() + inroomlist.size()) {
            index -= chattinglist.size();
            return inroomlist.toArray()[index];
        } else {
            index -= chattinglist.size() + inroomlist.size();
            return playinglist.toArray()[index];
        }
    }

    // Other methods
    public void add(Object element) {
    }

    public void addAll(Object elements[]) {
    }

    public void refresh() {
        chattinglist.addAll(inroomlist);
        //chattinglist.addAll(playinglist);
        inroomlist.clear();
        //playinglist.clear();
        leftroom.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        chattinglist.clear();
        inroomlist.clear();
        playinglist.clear();
        leftroom.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return chattinglist.contains(element) || inroomlist.contains(element) || playinglist.contains(element);
    }

    public Object firstElement() {
        // Return the appropriate element
        return chattinglist.first();
    }

    public Iterator iterator() {
        return chattinglist.iterator();
    }

    public Object lastElement() {
        // Return the appropriate element
        return playinglist.last();
    }

    public boolean removeElement(Object element) {
        return false;
    }
}
