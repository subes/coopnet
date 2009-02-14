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
package coopnetclient.frames.models;

import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.components.mutablelist.EditableListModel;
import coopnetclient.utils.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class ContactListModel extends AbstractListModel implements EditableListModel {

    public static final String DEFAULT_GROUP = "Default Group";
    public static boolean isNewGroup = false;
    private HashMap<String, ContactListElementTypes> pendingList = new HashMap<String, ContactListElementTypes>();
    private ArrayList<Group> groups = new ArrayList<Group>();
    
    public static class Group {

        private String name;
        private boolean closed = false;
        private HashMap<String, ContactListElementTypes> contacts;
        private TreeSet<String> offlineContacts;

        public Group(String name) {
            this.name = name;
            contacts = new HashMap<String, ContactListElementTypes>();
            offlineContacts = new TreeSet<String>();
        }

        public int size() {
            if (name.equals(DEFAULT_GROUP) && contacts.size() == 0 && (offlineContacts.size() == 0 || !Settings.getShowOfflineContacts())) {
                return 0;
            }
            if (closed) {
                return 1;
            } else {
                return (1 + contacts.size() + (Settings.getShowOfflineContacts() ? offlineContacts.size() : 0));
            }
        }

        public Object getElementAt(int index) {
            if (index == 0) {
                return name;
            } else {
                index--;
            }
            if (index >= contacts.size()) {
                return offlineContacts.toArray()[(index - contacts.size())];
            } else {
                //sorted by name
                Set<String> names = contacts.keySet();
                String[] namesarray = names.toArray(new String[0]);
                Collections.sort(Arrays.asList(namesarray));
                return namesarray[index];
            }
        }
    }

    public ContactListModel() {
        super();
    //groups.add(new Group(NO_GROUP));
    }

    public boolean updateName(String oldname, String newName) {
        boolean found = false ;
        Group group = groupOfContact(oldname);
        ContactListElementTypes status = null;
        if (group != null) {
            status = group.contacts.remove(oldname);
            if (status != null) {
                group.contacts.put(newName, status);
                found = true;
            }
            if (group.offlineContacts.remove(oldname)) {
                group.offlineContacts.add(newName);
                found = true;
            }
        }
        status = pendingList.remove(oldname);
        if (status != null) {
            pendingList.put(newName, status);
            found = true;
        }
        fireContentsChanged(this, 0, getSize());
        return found;
    }

    public void buildFrom(String[] data) {
        clear();
        String currentname = null;
        Integer currentstatusindex = null;
        int index = 0;
        for (; index < data.length && !(data[index].length() == 0); index++) {
            addContact(data[index], "", ContactListElementTypes.PENDING_REQUEST);
        }
        index++;
        while (index < data.length) {
            String groupname = data[index];
            addGroup(groupname);
            index++;
            for (; index < data.length && !(data[index].length() == 0); index++) {
                currentname = data[index].substring(1);
                currentstatusindex = Integer.valueOf(data[index].substring(0, 1));
                addContact(currentname, groupname, ContactListElementTypes.values()[currentstatusindex]);
            }
            index++;
        }
        fireContentsChanged(this, 0, getSize());
    }
    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        int size = 0;
        size += pendingList.size();
        for (Group g : groups) {
            size += g.size();
        }
        return size;
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        if (index < pendingList.size()) {
            Set<String> names = pendingList.keySet();
            String[] namesarray = names.toArray(new String[0]);
            Collections.sort(Arrays.asList(namesarray));
            return namesarray[index];
        } else {
            //get the correct element from the corrent group
            int sizethisfar = pendingList.size();
            for (Group g : groups) {
                if ((sizethisfar + g.size()) > index) { //element is in this group
                    return g.getElementAt(index - sizethisfar);
                } else {
                    sizethisfar += g.size();
                }
            }
        }
        return null;
    }

    private int indexOfGroup(String groupname) {
        int i = 0;
        for (Group g : groups) {
            if (g.name.equals(groupname)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Group groupOfContact(String contact) {
        for (Group g : groups) {
            if (g.contacts.keySet().contains(contact)) {
                return g;
            }
            if (g.offlineContacts.contains(contact)) {
                return g;
            }
        }
        return null;
    }

    public void createNewGroup(String name) {
        addGroup(name);
        //dont send anything to server cuz user has to edit the name first
        fireContentsChanged(this, 0, getSize());
    }
    
    public void renameGroup(String groupName, String newName) {
        if (indexOfGroup(newName) == -1) {
            groups.get(indexOfGroup(groupName)).name = newName;
            fireContentsChanged(this, 0, getSize());            
        }
    }

    public void addGroup(String groupName) {
        groups.add(new Group(groupName));
        fireContentsChanged(this, 0, getSize());
    }

    public void removeGroup(String groupName) {
        Group nogroup = groups.get(indexOfGroup(DEFAULT_GROUP));
        int idx = indexOfGroup(groupName);
        if (idx > -1 && !groups.get(idx).name.equals(DEFAULT_GROUP)) {
            nogroup.contacts.putAll(groups.get(idx).contacts);
            nogroup.offlineContacts.addAll(groups.get(idx).offlineContacts);
            groups.remove(idx);
            fireContentsChanged(this, 0, getSize());
        }
    }

    public Collection getGroupNames() {
        ArrayList<String> groupnames = new ArrayList<String>();
        for (Group g : groups) {
            groupnames.add(g.name);
        }
        return groupnames;
    }

    public void toggleGroupClosedStatus(String groupName) {
        groups.get(indexOfGroup(groupName)).closed = !(groups.get(indexOfGroup(groupName)).closed);
        fireContentsChanged(this, 0, getSize());
    }

    public void updateShowOfflineContacts() {
        fireContentsChanged(this, 0, getSize());
    }
    
    public void removePendingRequest(String who){
        pendingList.remove(who);
        fireContentsChanged(this, 0, getSize());
    }

    //other methods
    public void addContact(String contactname, String groupName, ContactListElementTypes status) {
        int idx = -1;
        switch (status) {
            case PENDING_REQUEST:
                pendingList.put(contactname, ContactListElementTypes.PENDING_REQUEST);
                break;
            case OFFLINE:
                 idx = indexOfGroup(groupName);
                if(idx > -1){
                    groups.get(idx).offlineContacts.add(contactname);
                }
                break;
            default:
                 idx = indexOfGroup(groupName);
                if(idx > -1){
                    groups.get(indexOfGroup(groupName)).contacts.put(contactname, status);
                }
                break;
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void removeContact(String contactName) {
        Group source = groupOfContact(contactName);
        if (source != null) {
            source.contacts.remove(contactName);
            source.offlineContacts.remove(contactName);
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void moveContact(String contactName, String tartgetGroup) {
        Group source = groupOfContact(contactName);
        Group target = groups.get(indexOfGroup(tartgetGroup));
        if (source == null || target == null || source.name.equals(target.name)) {
            return;     //dont do shit
        }
        ContactListElementTypes status = getStatus(contactName);
        source.contacts.remove(contactName);
        source.offlineContacts.remove(contactName);
        addContact(contactName, tartgetGroup, status);
        fireContentsChanged(this, 0, getSize());
    }

    public void removePending(String contactname) {
        pendingList.remove(contactname);
        fireContentsChanged(this, 0, getSize());
    }

    public void setStatus(String contactName, ContactListElementTypes status) {
        //if it was pending
        Group group = null;
        if (pendingList.containsKey(contactName)) {
            group = groups.get(indexOfGroup(DEFAULT_GROUP));
            if (status == ContactListElementTypes.OFFLINE) {
                group.offlineContacts.add(contactName);
            } else {
                group.contacts.put(contactName, status);
            }
        } else {
            group = groupOfContact(contactName);
        }
        if (group == null) {
            Protocol.refreshContacts();
            return;
        }
        if (group.offlineContacts.contains(contactName)) {//was offline
            group.contacts.put(contactName, status);
            group.offlineContacts.remove(contactName);
        } else {//was online and valid contact
            if (status == ContactListElementTypes.OFFLINE) {
                group.offlineContacts.add(contactName);
                group.contacts.remove(contactName);
            } else {
                group.contacts.put(contactName, status);//override status
            }
        }
        fireContentsChanged(this, 0, getSize());
    }

    public ContactListElementTypes getStatus(String itemName) {
        if (pendingList.containsKey(itemName)) {
            if(groupOfContact(itemName) == null){
                return pendingList.get(itemName);
            }
        }
        if (getGroupNames().contains(itemName)) {
            Group currentgroup = groups.get(indexOfGroup(itemName));
            if (currentgroup.closed) {
                return ContactListElementTypes.GROUPNAME_CLOSED;
            } else {
                return ContactListElementTypes.GROUPNAME_OPEN;
            }
        }
        Group group = groupOfContact(itemName);
        if (group != null) {
            ContactListElementTypes status = group.contacts.get(itemName);
            if (status == null) {
                if (group.offlineContacts.contains(itemName)) {
                    return ContactListElementTypes.OFFLINE;
                }
            } else {
                return status;
            }
        }
        return null;
    }
    
    public boolean isPending(int index){
        if(pendingList.containsKey(getElementAt(index)) && getFirstGroupIndex() > index){
            return true;
        }
        return false;
    }
    
    public ArrayList<String> getMoveToGroups(String userName){
        ArrayList<String> ret = new ArrayList<String>();
        
        Group groupOfContact = groupOfContact(userName);
        
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i) != groupOfContact){
                ret.add(groups.get(i).name);
            }
        }
        
        return ret;
    }
    
    public int getFirstGroupIndex(){
        for(int i = 0; i < getSize(); i++){
            Object obj = getElementAt(i);
            String itemName;
            if(obj instanceof Group){
                itemName = ((Group) obj).name;
            }else{
                itemName = (String) obj;
            }
            
            for(int j = 0; j < groups.size(); j++){
                if(groups.get(j).name.equals(itemName)){
                    return i;
                }
            }
        }
        
        return -1;
    }

    public void clear() {
        pendingList.clear();
        groups.clear();
        //groups.add(new Group(NO_GROUP));
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        if (groupOfContact(element.toString()) != null) {
            return true;
        }
        return pendingList.containsKey(element);
    }

    @Override
    public boolean isCellEditable(int index) {
        if (index < pendingList.size()) {
            return false;
        } else {
            //get the correct element from the corrent group
            int sizethisfar = pendingList.size();
            for (Group g : groups) {
                if ((sizethisfar + g.size()) > index) { //element is in this group
                    return (index - sizethisfar) == 0;
                } else {
                    sizethisfar += g.size();
                }
            }
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int index) {
        int sizethisfar = pendingList.size();
        for (Group g : groups) {
            if ((sizethisfar + g.size()) > index) { //element is in this group
                if ((index - sizethisfar) == 0) {   // is group's index
                    //
                    if (isNewGroup) {
                        isNewGroup = false;
                        removeGroup(g.name);//remove temporary data from model
                        Protocol.createGroup(value.toString());
                    } else {
                        if (indexOfGroup(value.toString()) == -1) {//dont send if the groupname is already used
                            Protocol.renameGroup(g.name, value.toString());
                        }
                    }
                    return;
                }
            } else {
                sizethisfar += g.size();
            }
        }
        fireContentsChanged(this, 0, getSize());
    }
}
