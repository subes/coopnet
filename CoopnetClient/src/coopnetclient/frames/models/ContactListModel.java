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

import coopnetclient.Client;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.components.mutablelist.EditableListModel;
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
    private static boolean showOffline = false;

    public static class Group {

        String name;
        boolean closed = false;
        HashMap<String, ContactListElementTypes> contacts;
        TreeSet<String> offlinecontacts;

        public Group(String name) {
            this.name = name;
            contacts = new HashMap<String, ContactListElementTypes>();
            offlinecontacts = new TreeSet<String>();
        }

        public int size() {
            if (name.equals(DEFAULT_GROUP) && contacts.size() == 0 && (offlinecontacts.size() == 0  || !showOffline)  ) {
                return 0;
            }
            if (closed) {
                return 1;
            } else {
                return (1 + contacts.size() + (showOffline ? offlinecontacts.size() : 0));
            }
        }

        public Object getElementAt(int index) {
            if (index == 0) {
                return name;
            } else {
                index--;
            }
            if (index >= contacts.size()) {
                return offlinecontacts.toArray()[(index - contacts.size())];
            } else {
                //sorted by name
                Set<String> names = contacts.keySet();
                String[] namesarray = names.toArray(new String[0]);
                Collections.sort(Arrays.asList(namesarray));
                return namesarray[index];
            }
        }
    }
    private HashMap<String, ContactListElementTypes> pendingList = new HashMap<String, ContactListElementTypes>();
    private ArrayList<Group> groups = new ArrayList<Group>();

    public ContactListModel() {
        super();
    //groups.add(new Group(NO_GROUP));
    }

    public void updateName(String oldname, String newName) {
        Group group = groupOfContact(oldname);
        ContactListElementTypes status = null;
        if (group != null) {
            status = group.contacts.remove(oldname);
            if (status != null) {
                group.contacts.put(newName, status);
            }
            if (group.offlinecontacts.remove(oldname)) {
                group.offlinecontacts.add(newName);
            }
        }
        status = pendingList.remove(oldname);
        if (status != null) {
            pendingList.put(newName, status);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void buildFrom(String data) {
        clear();
        String currentname = null;
        Integer currentstatusindex = null;
        String[] rows = data.split("\n");
        int currentrow = 0;
        for (String row : rows) {
            String[] fields = row.split(Protocol.INFORMATION_DELIMITER);
            if (currentrow == 0) {
                for (int i = 1; i < fields.length; i++) {
                    addContact(fields[i], "", ContactListElementTypes.PENDING_REQUEST);
                }
            } else {
                addGroup(fields[0]);
                for (int i = 1; i < fields.length; i++) {
                    currentname = fields[i].substring(1);
                    currentstatusindex = Integer.valueOf(fields[i].substring(0, 1));
                    addContact(currentname, fields[0], ContactListElementTypes.values()[currentstatusindex]);
                }
            }
            currentrow++;
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

    private Group groupOfContact(String contact) {
        for (Group g : groups) {
            if (g.contacts.keySet().contains(contact)) {
                return g;
            }
            if (g.offlinecontacts.contains(contact)) {
                return g;
            }
        }
        return null;
    }

    public void createNewGroup(String name) {
        addGroup(name);
        Protocol.createGroup(name);
        fireContentsChanged(this, 0, getSize());
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
            nogroup.offlinecontacts.addAll(groups.get(idx).offlinecontacts);
            groups.remove(idx);
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void renameGroup(String groupName, String newName) {
        if (indexOfGroup(newName) == -1) {
            groups.get(indexOfGroup(groupName)).name = newName;
            fireContentsChanged(this, 0, getSize());
            Protocol.renameGroup(groupName, newName);
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

    public void toggleShowOfflineStatus() {
        showOffline = !showOffline;
        fireContentsChanged(this, 0, getSize());
    }
    
    public boolean isOfflineShown(){
        return showOffline;
    }
    //other methods

    public void addContact(String contactname, String groupName, ContactListElementTypes status) {
        switch (status) {
            case PENDING_REQUEST:
                pendingList.put(contactname, ContactListElementTypes.PENDING_REQUEST);
                break;            
            case OFFLINE:
                groups.get(indexOfGroup(groupName)).offlinecontacts.add(contactname);
                break;
            default:
                groups.get(indexOfGroup(groupName)).contacts.put(contactname, status);
                break;
        }
        fireContentsChanged(this, 0, getSize());
    }

    public void removecontact(String contactName) {
        Group source = groupOfContact(contactName);
        if (source != null) {
            source.contacts.remove(contactName);
            source.offlinecontacts.remove(contactName);
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
        source.offlinecontacts.remove(contactName);
        addContact(contactName, tartgetGroup, status);
        fireContentsChanged(this, 0, getSize());
        Protocol.moveToGroup(contactName, tartgetGroup);
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
                group.offlinecontacts.add(contactName);
                pendingList.remove(contactName);
            } else {
                group.contacts.put(contactName, status);
            }
        } else {
            group = groupOfContact(contactName);
        }
        if (group == null) {
            Protocol.refreshContacts(showOffline);
            return;
        }
        if (group.offlinecontacts.contains(contactName)) {//was offline
            group.contacts.put(contactName, status);
            group.offlinecontacts.remove(contactName);
        } else {//was online and valid contact
            if (status == ContactListElementTypes.OFFLINE) {
                group.offlinecontacts.add(contactName);
                group.contacts.remove(contactName);
            } else {
                group.contacts.put(contactName, status);//override status
            }
        }
        fireContentsChanged(this, 0, getSize());
    }

    public ContactListElementTypes getStatus(String itemName) {
        if (pendingList.containsKey(itemName)) {
            return pendingList.get(itemName);
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
                if (group.offlinecontacts.contains(itemName)) {
                    return ContactListElementTypes.OFFLINE;
                }
            } else {
                return status;
            }
        }
        return null;
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
                    renameGroup(g.name, value.toString());
                    return;
                }
            } else {
                sizethisfar += g.size();
            }
        }
        fireContentsChanged(this, 0, getSize());
    }
}