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

import coopnetclient.enums.PlayerStatuses;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractListModel;

/**
 * Model of the user list which is sorted 
 */
public class ContactListModel extends AbstractListModel {

    private TreeSet<String> pendingList = new TreeSet<String>();
    private HashMap<String, PlayerStatuses> contactList = new HashMap<String, PlayerStatuses>();
    private TreeSet<String> offline = new TreeSet<String>();

    public ContactListModel() {
        super();
    }
    // ListModel methods
    @Override
    public int getSize() {
        // Return the model size
        return pendingList.size() + contactList.size() + offline.size();
    }

    @Override
    public Object getElementAt(int index) {
        // Return the appropriate element
        if (index < pendingList.size()) {
            return pendingList.toArray()[index];
        } else if (index < (pendingList.size() + contactList.size())) {
            index -= pendingList.size();
            Set<String> names = contactList.keySet();
            String[] namesarray = names.toArray(new String[0]);
            Collections.sort(Arrays.asList(namesarray));
            return namesarray[index];
        } else {
            index -= (pendingList.size() + contactList.size());
            return offline.toArray()[index];
        }
    }

    //other methods
    public void addContact(String contactname, PlayerStatuses status) {
        switch (status) {
            case PENDING_REQUEST:
                pendingList.add(contactname);
                break;
            case OFFLINE:
                offline.add(contactname);
                break;
            default:
                contactList.put(contactname, status);
                break;
        }
    }

    public void removecontact(String contactname) {
        contactList.remove(contactname);
    }

    public void removePending(String contactname) {
        pendingList.remove(contactname);
    }

    public void setStatus(String contactName, PlayerStatuses status) {
        //if it was pending
        if(pendingList.contains(contactName) ){
            if(status == PlayerStatuses.OFFLINE){
                offline.add(contactName);
                pendingList.remove(contactName);
            }else{
                contactList.put(contactName, status);
            }
        }else //was offline
            if(offline.contains(contactName)){
                contactList.put(contactName, status);
                offline.remove(contactName);
            }else{//was online and valid contact
                contactList.put(contactName, status);//overridfe status
            }
    }

    public PlayerStatuses getStatus(String contactName) {
        PlayerStatuses status = contactList.get(contactName);
        if (status == null) {
            if(offline.contains(contactName) ){
                return PlayerStatuses.OFFLINE;
            }else
            return PlayerStatuses.PENDING_REQUEST;
        } else {
            return contactList.get(contactName);
        }

    }

    public void clear() {
        pendingList.clear();
        contactList.clear();
        offline.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return pendingList.contains(element) || contactList.containsKey(element);
    }
}
