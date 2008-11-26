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
import javax.swing.table.DefaultTableModel;

public class RoomTableModel extends DefaultTableModel {
    
    /*
     * launcehd status is indicated by +10 intvalue
     * rooms that can have launched status msut be 
     * lower than instant-unpassworded room value
     * (it is ok to increase the value of previous to add new types)
     */
    public static final int NORMAL_UNPASSWORDED_ROOM = 0;
    public static final int NORMAL_UNPASSWORDED_ROOM_LAUNCHED = 10;
    public static final int NORMAL_PASSWORDED_ROOM = 1;
    public static final int NORMAL_PASSWORDED_ROOM_LAUNCHED = 11;
    public static final int INSTANT_UNPASSWORDED_ROOM = 2;
    public static final int INSTANT_PASSWORDED_ROOM = 3;
    

    private class  Room {

        private int type;
        private String name;
        private String hostName;
        private int maxPlayers;
        private boolean launched;
        private ArrayList<String> playersInRoom = new ArrayList<String>();

        public Room(int type, String name, String hostName, int maxplayers) {
            this.type = type;
            this.name = name;
            this.hostName = hostName;
            this.maxPlayers = maxplayers;
            this.launched= false;
            playersInRoom.add(hostName);
        }

        public boolean isLaunched(){
            return userDataModel.isPlaying(hostName);
        }
        
        public void setLaunched(boolean state){
            launched=state;
        }
        
        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public String getHostName() {
            return hostName;
        }

        public String getLimitString() {
            return playersInRoom.size() + "/" + maxPlayers;
        }

        public String getUserlist() {
            String userlist = "";
            for (String username : playersInRoom) {
                userlist += "<xmp>"+username+"</xmp><br>";
            }
            return userlist;
        }

        public boolean updatename(String oldname, String newname) {
            if (playersInRoom.remove(oldname)) {
                playersInRoom.add(newname);
                return true;
            }
            return false;
        }

        public void addPlayer(String name) {
            if(!playersInRoom.contains(name)){
                playersInRoom.add(name);
            }
        }

        public boolean removePlayer(String name) {
            return playersInRoom.remove(name);
        }
    }
    //end of inner class
    private String[] columnNames = {"Type", "Name", "Host", "Players"};
    private ArrayList<Room> rooms;
    private javax.swing.JTable parent;
    private ChannelStatusListModel userDataModel;
    

    {
        rooms = new ArrayList<Room>();
    }

    /** Creates a new instance of MyTableModel */
    public RoomTableModel(javax.swing.JTable parent,ChannelStatusListModel userdatamodel) {
        super();
        this.parent = parent;
        this.userDataModel = userdatamodel;
    }

    public int indexOf(String hostName) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getHostName().equals(hostName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        if (rooms != null) {
            return rooms.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                if(rooms.get(row).isLaunched() && rooms.get(row).getType()< INSTANT_UNPASSWORDED_ROOM ){
                    return rooms.get(row).getType()+10;
                }
                else
                return rooms.get(row).getType();
            case 1:
                return rooms.get(row).getName();
            case 2:
                return rooms.get(row).getHostName();
            case 3:
                return rooms.get(row).getLimitString();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
        }
        return String.class;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //table is not editable !
        fireTableCellUpdated(row, col);
    }

    public void removeElement(String hostName) {
        int index = indexOf(hostName);
        if (index >= 0) {
            rooms.remove(index);
        }
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void clear() {
        rooms.clear();
        fireTableDataChanged();
    }

    public String getSelectedHostName(){
        int i = parent.getSelectedRow();
        if (i == -1) {
            return null;
        }
        return rooms.get(i).getHostName();
    }

    public String getSelectedRoomName() {
        int i = parent.getSelectedRow();
        if (i == -1) {
            return null;
        }
        return rooms.get(i).getName();
    }

    public String getRoomName(String hostName) {
        int index = indexOf(hostName);
        if (index >= 0) {
            rooms.remove(index);
        }
        return null;
    }

    public boolean isSelectedRoomPassworded() {
        int i = parent.getSelectedRow();
        if (i != -1) {
            return (rooms.get(i).getType() == NORMAL_PASSWORDED_ROOM) || (rooms.get(i).getType() == INSTANT_PASSWORDED_ROOM);
        }
        return false;
    }

    public void addRoomToTable(String name, String hostName, int maxPlayers, int type) {
        rooms.add(new Room(type, name, hostName, maxPlayers));
        fireTableDataChanged();
    }

    //user joined the room, add him to the list
    public void addPlayerToRoom(String hostName, String playerName) {
        int index = indexOf(hostName);
        if (index >= 0) {
            rooms.get(index).addPlayer(playerName);
        }
        fireTableDataChanged();
    }

    public void removePlayerFromRoom(String hostName, String playerName) {
        int index = indexOf(hostName);
        if (index >= 0) {
            rooms.get(index).removePlayer(playerName);
            fireTableDataChanged();
        }
    }

    public boolean updateName(String oldName, String newName) {
        boolean found = false;
        for (Room room : rooms) {
            found = room.updatename(oldName, newName) || found;
        }
        fireTableDataChanged();
        return found;
    }

    public String getUserList(int row) {
        return rooms.get(row).getUserlist();
    }
    
    public void setLaunchedStatus(String possibleHost, boolean newLaunchedState){
        int index = indexOf(possibleHost);
        if (index >= 0) {
            rooms.get(index).setLaunched(newLaunchedState);
        }
        fireTableDataChanged();
    }
}
