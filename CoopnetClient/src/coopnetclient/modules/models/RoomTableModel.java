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

package coopnetclient.modules.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class RoomTableModel extends DefaultTableModel {

    public static final int NORMAL_UNPASSWORDED_ROOM = 0;
    public static final int NORMAL_PASSWORDED_ROOM = 1;
    public static final int INSTANT_UNPASSWORDED_ROOM = 2;
    public static final int INSTANT_PASSWORDED_ROOM = 3;

    private static class  Room {

        private int type;
        private String name;
        private String hostName;
        private int maxplayers;
        private ArrayList<String> playersInRoom = new ArrayList<String>();

        public Room(int type, String name, String hostName, int maxplayers) {
            this.type = type;
            this.name = name;
            this.hostName = hostName;
            this.maxplayers = maxplayers;
            playersInRoom.add(hostName);
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
            return playersInRoom.size() + "/" + maxplayers;
        }

        public String getUserlist() {
            String userlist = "";
            for (String username : playersInRoom) {
                userlist += username + "<br>";
            }
            return userlist;
        }

        public void updatename(String oldname, String newname) {
            if (playersInRoom.remove(oldname)) {
                playersInRoom.add(newname);
            }
        }

        public void addPlayer(String name) {
            playersInRoom.add(name);
        }

        public boolean removePlayer(String name) {
            return playersInRoom.remove(name);
        }
    }
    //end of inner class
    private String[] columnNames = {"Type", "Name", "Host", "Players"};
    private ArrayList<Room> rooms;
    private javax.swing.JTable parent;
    

    {
        rooms = new ArrayList<Room>();
    }

    /** Creates a new instance of MyTableModel */
    public RoomTableModel(javax.swing.JTable parent) {
        super();
        this.parent = parent;
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
                return rooms.get(row).getType();
            case 1:
                return rooms.get(row).getHostName();
            case 2:
                return rooms.get(row).getName();
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

    public String getroomname(String hostName) {
        int index = indexOf(hostName);
        if (index >= 0) {
            rooms.remove(index);
        }
        return null;
    }

    public boolean selectedRoomIsPassworded() {
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

    public void updateName(String oldName, String newName) {
        for (Room room : rooms) {
            room.updatename(oldName, newName);
        }
        fireTableDataChanged();
    }

    public String getUserList(int row) {
        return rooms.get(row).getUserlist();
    }
}
