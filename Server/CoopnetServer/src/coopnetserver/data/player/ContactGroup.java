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
package coopnetserver.data.player;

import coopnetserver.utils.Database;
import coopnetserver.utils.Logger;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactGroup {
    private Player player;
    private byte gid;
    private String name;
    private ArrayList<Long> contacts;
    
    public ContactGroup(Player player, byte gid, String name, ArrayList<Long> contacts){
        this.player = player;
        this.gid = gid;
        this.name = name;
        this.contacts = contacts;
    }
    
    public ContactGroup(Player player, byte gid, String name){
        this.player = player;
        this.gid = gid;
        this.name = name;
        this.contacts = new ArrayList<Long>();
    }
    
    public Long[] getContacts(){
        Long[] ret = new Long[contacts.size()];
        contacts.toArray(ret);
        return ret;
    }
    
    public void deleteGroup() throws SQLException{       
        Database.deleteGroup(player, gid);
        contacts.clear();
        name = null;
        gid = -1; //aint possible in db
    }
    
    public void refreshGroup() throws SQLException{
        contacts = Database.getContactGroup(player, gid);
    }
    
    public String getGroupName(){
        return name;
    }
    
    public void setGroupName(String name) throws SQLException{
        Database.updateGroupName(player, gid, name);
        this.name = name;
    }
    
    public boolean removeContact(long contactid) throws SQLException{
        Database.removeContact(player.getPid(), contactid);
        boolean ret = contacts.remove(contactid);
        return ret;
    }
    
    public void addContactRequest(long contactid) throws SQLException{
        Database.addContactRequest(player, contactid, gid);
        contacts.add(contactid);
    }
    
    public void moveContactHere(long contactid) throws SQLException{
        Database.moveContactToGroup(player, contactid, gid);
        contacts.add(contactid);
    }
    
    public boolean hasContact(long contactid){
        return contacts.contains(contactid);
    }
}