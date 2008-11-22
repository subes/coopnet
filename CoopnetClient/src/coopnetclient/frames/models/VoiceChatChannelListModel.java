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

import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class VoiceChatChannelListModel extends AbstractListModel {

    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private ArrayList<String> mutelist = new ArrayList<String>();
    private ArrayList<String> talkinglist = new ArrayList<String>();

    public static class Channel {

        private String name;
        private boolean closed = false;
        private TreeSet<String> users;

        public Channel(String name) {
            this.name = name;
            users = new TreeSet<String>();
        }
 
        public void toggleOpenStatus(){
            closed = !closed;
        }
        
        public boolean isClosed(){
            return closed;
        }
               
        public int size() {
            if (closed) {
                return 1;
            } else {
                return (1 + users.size());
            }
        }

        public Object getElementAt(int index) {
            if (index == 0) {
                return name;
            } else {
                index--;
            }
            if (index < users.size()) {
                //sorted by name               
                return users.toArray()[index];
            } else {
                return null;
            }
        }
    }

    public VoiceChatChannelListModel() {
        super();
        channels.add(new Channel("Channel 1"));
        channels.add(new Channel("Channel 2"));
        channels.add(new Channel("Channel 3"));
        channels.add(new Channel("Channel 4"));
        channels.add(new Channel("Channel 5"));
        channels.add(new Channel("Channel 6"));
        channels.add(new Channel("Channel 7"));
        channels.add(new Channel("Channel 8"));
    }
    
    public void refresh(){
        fireContentsChanged(this, 0, getSize());
    }

    public void addUserToChannel(String user, int channelIndex){
        Channel c = channels.get(channelIndex);
        c.users.add(user);
        fireContentsChanged(this, 0, getSize());
    }
    
    public void removeUser(String user){
        for(Channel c : channels){
            c.users.remove(user);
        }
        fireContentsChanged(this, 0, getSize());
    }
    
    public int indexOfChannel(Channel c){
        return channels.indexOf(c);
    }
    
    public void moveUserToChannel(String user, String channel){
        removeUser(user);
        Channel c = getChannel(channel);
        c.users.add(user);
        fireContentsChanged(this, 0, getSize());
    }
    
    public void moveUserToChannel(String user, int channelIndex){
        removeUser(user);
        Channel c = channels.get(channelIndex);
        c.users.add(user);
        fireContentsChanged(this, 0, getSize());
    }
    
    public void mute(String user) {
        mutelist.add(user);
        fireContentsChanged(this, 0, getSize());
    }

    public void unMute(String user) {
        mutelist.remove(user);
        fireContentsChanged(this, 0, getSize());
    }

    public boolean isMuted(String user) {
        return mutelist.contains(user);        
    }
    
    public void setTalking(String user){
        talkinglist.add(user);
        fireContentsChanged(this, 0, getSize());
    }
    
    public void setNotTalking(String user){
        talkinglist.remove(user);
        fireContentsChanged(this, 0, getSize());
    }
    
    public boolean isTalking(String user){
        return talkinglist.contains(user);
    }

    public Channel getChannel(String value) {
        for (Channel c : channels) {
            if (c.name.equals(value)) {
                return c;
            }
        }
        return null;
    }

    public boolean updateName(String oldname, String newName) {
        Channel group = groupOfContact(oldname);
        if (group != null) {
            group.users.remove(oldname);
            group.users.add(newName);
            fireContentsChanged(this, 0, getSize());
            return true;
        }
        return false;
    }
    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        int size = 0;
        for (Channel g : channels) {
            size += g.size();
        }
        return size;
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        //get the correct element from the corrent group
        int sizethisfar = 0;
        for (Channel g : channels) {
            if ((sizethisfar + g.size()) > index) { //element is in this group
                return g.getElementAt(index - sizethisfar);
            } else {
                sizethisfar += g.size();
            }
        }
        return null;
    }

    public Channel groupOfContact(String contact) {
        for (Channel g : channels) {
            if (g.users.contains(contact)) {
                return g;
            }
        }
        return null;
    }

    public void toggleGroupClosedStatus(String groupName) {
        Channel c = getChannel(groupName);
        c.closed = !c.closed;
        fireContentsChanged(this, 0, getSize());
    }

    public void clear() {
        for (Channel c : channels) {
            c.users.clear();
        }
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        if (groupOfContact(element.toString()) != null) {
            return true;
        }
        if(getChannel(element.toString())!=null){
            return true;
        }
        return false;
    }
}
