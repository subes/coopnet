/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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
package coopnetclient.modules.models;

import coopnetclient.enums.ContactStatuses;
import coopnetclient.modules.components.mutablelist.EditableListModel;
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

    public static final String NO_GROUP = "No-Group";
    private static boolean showoffline = true;

    public static class Group {

        String name;
        boolean closed = false;
        HashMap<String, ContactStatuses> contacts;
        TreeSet<String> offlinecontacts;

        public Group(String name) {
            this.name = name;
            contacts = new HashMap<String, ContactStatuses>();
            offlinecontacts = new TreeSet<String>();
        }

        public int size() {
            if (closed) {
                return 1;
            } else {
                return (1 + contacts.size() + (showoffline ? offlinecontacts.size() : 0));
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
    private HashMap<String, ContactStatuses> pendingList = new HashMap<String, ContactStatuses>();
    private ArrayList<Group> groups = new ArrayList<Group>();

    public ContactListModel() {
        super();
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
        return "ERROR";
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

    public void addGroup(String groupName) {
        groups.add(new Group(groupName));
        fireContentsChanged(this, 0, getSize());
    }

    public void removeGroup(String groupName) {
        groups.remove(indexOfGroup(groupName));
        fireContentsChanged(this, 0, getSize());
    }

    public void renameGroup(String groupName, String newName) {
        groups.get(indexOfGroup(groupName)).name = newName;
        fireContentsChanged(this, 0, getSize());
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
        showoffline = !showoffline;
        fireContentsChanged(this, 0, getSize());
    }
    //other methods
    public void addContact(String contactname, String groupName, ContactStatuses status) {
        switch (status) {
            case PENDING_REQUEST:
                pendingList.put(contactname, ContactStatuses.PENDING_REQUEST);
                break;
            case PENDING_CONTACT:
                pendingList.put(contactname, ContactStatuses.PENDING_CONTACT);
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

    public void removecontact(String contactname, String groupName) {
        if (groupName != null && groupName.length() > 0) {
            groups.get(indexOfGroup(groupName)).contacts.remove(contactname);
            groups.get(indexOfGroup(groupName)).offlinecontacts.remove(contactname);
            fireContentsChanged(this, 0, getSize());
        }
    }

    public void moveContact(String contactName, String tartgetGroup) {
        Group source = groupOfContact(contactName);
        Group target = groups.get(indexOfGroup(tartgetGroup));
        if (source == null || target == null || source.name.equals(target.name)) {
            return;     //dont do shit
        }
        ContactStatuses status = getStatus(contactName);
        source.contacts.remove(contactName);
        source.offlinecontacts.remove(contactName);
        addContact(contactName, tartgetGroup, status);
        fireContentsChanged(this, 0, getSize());
    //TODO send to server
    }

    public void removePending(String contactname) {
        pendingList.remove(contactname);
        fireContentsChanged(this, 0, getSize());
    }

    public void setStatus(String contactName, ContactStatuses status) {
        //if it was pending
        Group group = null;
        if (pendingList.containsKey(contactName)) {
            group = groups.get(indexOfGroup(NO_GROUP));
            if (status == ContactStatuses.OFFLINE) {
                group.offlinecontacts.add(contactName);
                pendingList.remove(contactName);
            } else {
                group.contacts.put(contactName, status);
            }
        } else {
            group = groupOfContact(contactName);
        }
        if (group.offlinecontacts.contains(contactName)) {//was offline
            group.contacts.put(contactName, status);
            group.offlinecontacts.remove(contactName);
        } else {//was online and valid contact
            group.contacts.put(contactName, status);//override status
        }
        fireContentsChanged(this, 0, getSize());
    }

    public ContactStatuses getStatus(String itemName) {
        if (pendingList.containsKey(itemName)) {
            return pendingList.get(itemName);
        }
        if (getGroupNames().contains(itemName)) {
            Group currentgroup = groups.get(indexOfGroup(itemName));
            if (currentgroup.closed) {
                return ContactStatuses.GROUPNAME_CLOSED;
            } else {
                return ContactStatuses.GROUPNAME_OPEN;
            }
        }
        Group group = groupOfContact(itemName);
        if (group != null) {
            ContactStatuses status = group.contacts.get(itemName);
            if (status == null) {
                if (group.offlinecontacts.contains(itemName)) {
                    return ContactStatuses.OFFLINE;
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
        //TODO send rename command to server

        int sizethisfar = pendingList.size();
        for (Group g : groups) {
            if ((sizethisfar + g.size()) > index) { //element is in this group
                if ((index - sizethisfar) == 0) {
                    g.name = value.toString();
                    return;
                }
            } else {
                sizethisfar += g.size();
            }
        }
    }
}
