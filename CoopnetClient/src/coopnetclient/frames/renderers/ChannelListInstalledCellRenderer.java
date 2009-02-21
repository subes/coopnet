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

package coopnetclient.frames.renderers;

import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.settings.Settings;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ChannelListInstalledCellRenderer extends DefaultTableCellRenderer {

    public ChannelListInstalledCellRenderer(){}

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        setText("");

        setHorizontalAlignment(SwingConstants.CENTER);

        if((Boolean)value == true){
            setIcon(Icons.acceptIcon);
        }else{
            setIcon(null);
        }
        
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        }
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
