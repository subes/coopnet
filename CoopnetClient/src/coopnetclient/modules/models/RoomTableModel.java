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

import coopnetclient.exceptions.NoRoomsException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class RoomTableModel extends DefaultTableModel {

    /** Creates a new instance of MyTableModel */
    public RoomTableModel(javax.swing.JTable parent) {
        super();
        this.parent = parent;
        password = new Vector<Boolean>();
        name = new Vector<String>();
        host = new Vector<String>();
        players = new Vector<String>();
        playersinroom = new Vector<Vector<String>>();
    }
    private String[] columnNames = {"Password", "Name", "Host", "Players"};
    private Vector<Boolean> password;
    private Vector<String> name;
    private Vector<String> host;
    private Vector<String> players;
    private Vector<Vector<String>> playersinroom;
    private javax.swing.JTable parent;

    public int indexOf(String hostname) {
        return host.indexOf(hostname);
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getRowCount() {
        if (name == null) {
            return 0;
        } else {
            return name.size();
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (name.size() == 0) {
            return "";
        }
        if (col == 0) {
            return password.get(row);
        }
        if (col == 1) {
            return name.get(row);
        }
        if (col == 2) {
            return host.get(row);
        }
        if (col == 3) {
            return players.get(row);
        }
        return null;
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            password.set(row, (Boolean) value);
        }
        if (col == 1) {
            name.set(row, value.toString());
        }
        if (col == 2) {
            host.set(row, value.toString());
        }
        if (col == 3) {
            players.set(row, value.toString());
        }
        fireTableCellUpdated(row, col);
    }

    public void removeElement(String hostname) {
        for (int i = 0; i < name.size(); i++) {
            if (host.get(i).equals(hostname)) {
                name.remove(i);
                password.remove(i);
                host.remove(i);
                players.remove(i);
                playersinroom.remove(i);
                fireTableDataChanged();
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void clear() {
        name.clear();
        password.clear();
        host.clear();
        players.clear();
        playersinroom.clear();
        fireTableDataChanged();
    }

    public String getselectedhost() throws NoRoomsException {
        int i = parent.getSelectedRow();
        if (i == -1) {
            throw new NoRoomsException();
        }
        return host.get(i);
    }

    public String getselectedroomname() throws NoRoomsException {
        int i = parent.getSelectedRow();
        if (i == -1) {
            throw new NoRoomsException();
        }
        return name.get(i);
    }

    public String getroomname(String host) {
        return name.get(indexOf(host));
    }

    public boolean selectedispassworded() throws NoRoomsException {
        int i = parent.getSelectedRow();
        if (i == -1) {
            throw new NoRoomsException();
        }
        return password.get(i);

    }

    public void addnewroom(String _name, String _host, String _playerlimitstr, boolean _passw) {
        name.add(_name);
        host.add(_host);
        players.add(_playerlimitstr);
        password.add(_passw);
        Vector<String> v = new Vector<String>();
        v.add(_host);
        playersinroom.add(v);
        fireTableDataChanged();
    }

    //user joined the room, add him to the list
    public void joinedroom(String host_name, String user) {
        int i = host.indexOf(host_name);
        String[] tmp = players.get(i).split("/");
        int j = new Integer(tmp[0]);

        if (!playersinroom.get(i).contains(user)) {
            playersinroom.get(i).add(user);
            j++;
        }
        players.set(i, j + "/" + tmp[1]); 
        // players shows the current/max numbers of players, like 1/32
        fireTableDataChanged();
    }

    public void leftroom(String host_name, String user) {
        int i = host.indexOf(host_name);
        String[] tmp = players.get(i).split("/");
        int j = new Integer(tmp[0]);
        j--;
        players.set(i, j + "/" + tmp[1]);
        playersinroom.get(i).remove(user);
        fireTableDataChanged();
    }

    public void updatename(String oldname, String newname) {
        int i = host.indexOf(oldname);
        if (i == -1) {
            return;
        }
        host.set(i, newname);

        for (Vector<String> vc : playersinroom) {
            for (String s : vc) {
                if (s.equals(oldname)) {
                    vc.remove(s);
                    vc.add(newname);
                    break;
                }
            }
        }

        fireTableDataChanged();
    }

    public String getuserslist(int row) {
        String tmp = "";
        Vector<String> v = playersinroom.get(row);
        if (v != null) {
            for (String s : v) {
                tmp += s + "<br>";
            }
        }
        return tmp;
    }
}
