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

package filechooser;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

class FileChooserTableModel extends DefaultTableModel {

    /** Creates a new instance of MyTableModel */
    public FileChooserTableModel(javax.swing.JTable parent) {
        super();
        this.parent = parent;
        name = new Vector<String>();
        type = new Vector<String>();
        size = new Vector<String>();
        date = new Vector<String>();
        isHidden = new Vector<Boolean>();
    }
    private String[] columnNames = {"Name", "Type", "Size", "Date"};
    private Vector<String> name;
    private Vector<String> type;
    private Vector<String> size;
    private Vector<String> date;
    private Vector<Boolean> isHidden;
    private javax.swing.JTable parent;

    protected boolean fileIsHidden(int row){
        return isHidden.get(row);
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
        if (col == 0) {
            return name.get(row);
        }
        if (col == 1) {
            return type.get(row);
        }
        if (col == 2) {
            return size.get(row);
        }
        if (col == 3) {
            return date.get(row);
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
            name.set(row, value.toString());
        }
        if (col == 1) {
            type.set(row, value.toString());
        }
        if (col == 2) {
            size.set(row, value.toString());
        }
        if (col == 3) {
            date.set(row, value.toString());
        }
        fireTableCellUpdated(row, col);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void clear() {
        type.clear();
        name.clear();
        size.clear();
        date.clear();
        isHidden.clear();
        fireTableDataChanged();
    }

    public String getSelectedFile() {
        int i = parent.getSelectedRow();
        if (i == -1) {
            return null;
        }
        return name.get(i);
    }

    public int indexOf(String _name) {
        return name.indexOf(_name);
    }

    public void addNewFile(String name, String type, long size, String date, boolean isHidden) {
        this.type.add(type);
        this.date.add(date);
        this.name.add(name);
        this.isHidden.add(isHidden);
        int i = 0;
        String unit = "";
        if (size > 0) {
            while (size > 1024) {
                size = size / 1024;
                i++;
            }
            if (i == 0) {
                unit = "bytes";
            }
            if (i == 1) {
                unit = "K";
            }
            if (i == 2) {
                unit = "M";
            }
            if (i == 3) {
                unit = "G";
            }
            this.size.add(size + unit);
        } else {
            this.size.add("");
        }
        fireTableDataChanged();
    }
}
