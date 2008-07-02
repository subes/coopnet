/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.modules;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class UsersInRoomTableCR extends DefaultTableCellRenderer {
    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.
    RoomTableModel model;

    public UsersInRoomTableCR(RoomTableModel model_) {
        model = model_;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        // 'value' is value contained in the cell located at
        // (rowIndex, vColIndex)

        // Configure the component with the specified value
        setText(value.toString());

        // Set tool tip if desired
        setToolTipText("<html>" +
                model.getuserslist(rowIndex) +
                "</html>");

        // Since the renderer is a component, return itself
        return this;
    }
    // The following methods override the defaults for performance reasons
    @Override
    public void validate() {
    }

    @Override
    public void revalidate() {
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}
