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

package coopnetclient.modules.renderers;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

public class RoomPasswordPicTableCellRenderer extends DefaultTableCellRenderer{
    
    /**
     * this renders the picture in the room list showing if its public or password protected
     */
    
    private static ImageIcon normalOpenRoomIcon = new ImageIcon("data/icons/door.gif");
    private static ImageIcon normalPasswordedRoomIcon = new ImageIcon("data/icons/cl_door.gif");
    
    /** Creates a new instance of MyPasswordrenderer */
    public RoomPasswordPicTableCellRenderer() {
    }
    
    @Override
     public void setValue(Object value) {
        int intvalue = (Integer)value;
        
        switch(intvalue){
           case coopnetclient.modules.models.RoomTableModel.NORMAL_UNPASSWORDED_ROOM:
                setIcon(normalOpenRoomIcon);
                setToolTipText("Room is not password protected");  
                break;
           case coopnetclient.modules.models.RoomTableModel.NORMAL_PASSWORDED_ROOM:
                setIcon(normalPasswordedRoomIcon);
                setToolTipText("Room is not password protected");  
                break;
        }
    }
}
