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

import coopnetclient.Globals;
import coopnetclient.frames.components.TransferStatusButtonComponent;
import coopnetclient.utils.Settings;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class TransferStatusRenderer implements TableCellRenderer{

    public TransferStatusRenderer(){
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TransferStatusButtonComponent renderer = (TransferStatusButtonComponent) Globals.getTransferModel().getValueAt(row, column);
        renderer.prepare(table, value, isSelected, hasFocus, row, column);
        renderer.setOpaque(true);
        if (Settings.getColorizeBody()) {
            renderer.setForeground(Settings.getForegroundColor());
            if (isSelected) {
                renderer.setBackground(Settings.getSelectionColor());
            } else {
                renderer.setBackground(Settings.getBackgroundColor());
            }
        } else {
            if (isSelected) {
                renderer.setBackground(table.getSelectionBackground());
            } else {
                if (row % 2 == 1) {
                    renderer.setBackground(Color.WHITE);
                } else {
                    renderer.setBackground(table.getBackground());
                }
            }
        }
        
        return renderer;
    }

}
