package coopnetserver.data.player;

import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.player.PlayerData;
import coopnetserver.enums.ContactListElementTypes;
import coopnetserver.exceptions.VerificationException;
import coopnetserver.utils.Logger;
import coopnetserver.utils.Database;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactList {

    private Player player;
    private ArrayList<ContactGroup> myGroups;           // users on this players list
    private ArrayList<Long> contactsWhoKnowMe;    //users who have this player on their list
    private ArrayList<Long> pendingContacts;      //users who are being requested to allow this player to add them to his list
    private ArrayList<Long> requests;             //users who want to add this player to their list

    public ContactList(Player player) throws SQLException {
        this.player = player;

        contactsWhoKnowMe = Database.getContactsWhoKnowMe(player);
        pendingContacts = Database.getPendingContacts(player);
        requests = Database.getContactRequests(player);
        myGroups = Database.getContactList(player);
    }

    public void renameGroup(String oldname, String newname) throws SQLException, VerificationException {
        if (indexOfGroup(newname) != -1) {
            throw new VerificationException("User shouldn't be able to rename a group if the groupname is already in use!");
        }
        if(indexOfGroup(oldname) == -1){
            throw new VerificationException("User shouldn't be able to rename a non existent group!");
        }
        if(indexOfGroup(oldname) == 0){
            throw new VerificationException("User shouldn't be able to rename the default group!");
        }
        
        int idx = indexOfGroup(oldname);
        if (idx > 0) {
            myGroups.get(idx).setGroupName(newname);
        }
    }

    public void moveContact(long ID, String groupName) throws SQLException, VerificationException {
        int idx = indexOfGroup(groupName);
        if (idx > -1) {
            for (ContactGroup g : myGroups) {
                if (g.hasContact(ID)) {
                    myGroups.get(idx).moveContactHere(ID);
                    g.refreshGroup();
                    return;
                }
            }
        }else{
            throw new VerificationException("Failure on target group! " + groupName);
        }
    }
    
    public boolean groupExists(String groupName){
        return indexOfGroup(groupName) != -1;
    }

    public void removeContactWhoKnowsMe(Long ID) {
        contactsWhoKnowMe.remove(ID);
    }

    public void sendContactData(boolean showOffline) throws SQLException {
        ArrayList<String> data = new ArrayList<String>();
        Player currentplayer = null;
        String DataItem = null;
        //add requests
        for (Long ID : requests) {
            currentplayer = PlayerData.searchByID(ID);
            if (currentplayer != null) {
                data.add(currentplayer.getLoginName());
            } else {
                data.add(Database.getLoginName(ID));
            }
        }
        data.add("");
        //add groups
        for (ContactGroup g : myGroups) {
            data.add(g.getGroupName());
            for (Long ID : g.getContacts()) {
                DataItem = "";
                currentplayer = PlayerData.searchByID(ID);
                String currentname = null;
                if (showOffline) {
                    if (currentplayer != null) {
                        currentname = currentplayer.getLoginName();
                    } else {
                        currentname = Database.getLoginName(ID);
                    }
                    if (pendingContacts.contains(ID)) {
                        DataItem+=(String.valueOf(ContactListElementTypes.PENDING_CONTACT.ordinal()));
                    } else {
                        if(currentplayer == null){
                            DataItem+=(String.valueOf(ContactListElementTypes.OFFLINE.ordinal()));
                        }else{
                            DataItem+=(String.valueOf(currentplayer.getContactStatus().ordinal()));
                        }
                    }
                    DataItem+= currentname;
                    data.add(DataItem);
                } else { //dont send offline
                    if (currentplayer != null) {
                        if (pendingContacts.contains(ID)) {
                            DataItem+=(String.valueOf(ContactListElementTypes.PENDING_CONTACT.ordinal()));
                        } else {
                            DataItem+=(String.valueOf(currentplayer.getContactStatus().ordinal()));
                        }
                        DataItem+=currentplayer.getLoginName();
                        data.add(DataItem);
                    }else if (pendingContacts.contains(ID)) {
                            DataItem+=(String.valueOf(ContactListElementTypes.PENDING_CONTACT.ordinal()));
                            DataItem+=(Database.getLoginName(ID));
                            data.add(DataItem);
                    }
                }
            }
            data.add("");
        }
        
        String[] d = new String[data.size()];
        data.toArray(d);
        
        Protocol.sendContactData(player.getConnection(), d);
    }

    private int indexOfGroup(String groupname) {
        int i = 0;
        for (ContactGroup g : myGroups) {
            if (g.getGroupName().equals(groupname)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void addRequest(Long ID) {
        requests.add(ID);
    }
    
    public void removeRequest(Player who){
        if(who == null){
            return;
        }
        
        if(requests.remove(who.getPid())){
            Protocol.sendRemoveRequest(player, who);
        }
    }

    public void contactAcceptedRequest(long ID) throws SQLException {
        if (pendingContacts.remove(ID)) {
            Database.setContactAccepted(player.getPid(), ID);
        }
    }

    public void contactRefusedRequest(Long ID) throws SQLException {
        for (ContactGroup g : myGroups) {
            if (g.removeContact(ID)) {
                return;
            }
        }
        pendingContacts.remove(ID);
    }

    public void acceptRequest(Long ID) {
        if (requests.remove(ID)) {
            addContactWhoKnowsMe(ID);
        }
    }

    public void removeContact(Long ID) throws SQLException {
        for (ContactGroup g : myGroups) {
            if (g.removeContact(ID)) {
                break;
            }
        }
        if(pendingContacts.remove(ID)){
            //tell him that im not interested in his friendship anymore
            Player removedFrom = PlayerData.searchByID(ID);
            if(removedFrom != null){
                //Only need to do this if he is online!
                removedFrom.getContactList().removeRequest(player);
            }
        }
    }

    public void refuseRequest(Long ID) throws SQLException {
        removeContact(ID);
        requests.remove(ID);        
    }

    public void addContactWhoKnowsMe(Long ID) {
        if (!contactsWhoKnowMe.contains(ID)) {
            contactsWhoKnowMe.add(ID);
        }
    }

    /**
     * returns true if the contact was newly added to the contactlist, false if it already is on the list
     */
    public boolean addPendingContact(Long ID) throws SQLException {
        if (!isOnMyList(ID) && !pendingContacts.contains(ID)) {
            myGroups.get(0).addContactRequest(ID);
            pendingContacts.add(ID);
            return true;
        }
        return false;
    }

    public void createGroup(String groupName) throws SQLException {
        myGroups.add(Database.createGroup(player, groupName));
    }

    /**
     * contacts are moved to No-Group first then group is removed
     **/
    public void deleteGroup(String groupname) throws SQLException, VerificationException {
        int idx = indexOfGroup(groupname);
        
        if (idx == 0) {
            throw new VerificationException("Client tried to delete the default Group!");
        }
        
        if (idx > -1 && idx != 0) {
            ContactGroup todelete = myGroups.get(idx);

            //moves contacts to default group automatically
            todelete.deleteGroup();
            myGroups.get(0).refreshGroup();
            myGroups.remove(idx);
        }
    }

    public boolean isOnMyList(Long ID) {
        for (ContactGroup g : myGroups) {
            if (g.hasContact(ID)) {
                return true;
            }
        }
        return false;
    }

    public boolean iAmOnHisList(Long ID) {
        return contactsWhoKnowMe.contains(ID);
    }

    public ArrayList<ContactGroup> getMyGroups() {
        return myGroups;
    }

    public ArrayList<Long> getContactsWhoKnowMe() {
        return contactsWhoKnowMe;
    }

    public ArrayList<Long> getPendingContacts() {
        return pendingContacts;
    }

    public ArrayList<Long> getRequests() {
        return requests;
    }
}
