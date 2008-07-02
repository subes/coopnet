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

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

public class RoomPasswordPicTableCR extends DefaultTableCellRenderer{
    
    /**
     * this renders the picture in the room list showing if its public or password protected
     */
    
    /** Creates a new instance of MyPasswordrenderer */
    public RoomPasswordPicTableCR() {
    }
    
    @Override
     public void setValue(Object value) {
        if(!(Boolean)value){
            setIcon(new ImageIcon("data/icons/door.gif"));
            setToolTipText("Room is not password protected");   
        }
        else{
            setIcon(new ImageIcon("data/icons/cl_door.gif"));
            setToolTipText("Room is password protected");      
        }  
    }
}
