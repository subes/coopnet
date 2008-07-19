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
import static coopnetclient.modules.models.RoomTableModel.*;

public class RoomPasswordPicTableCellRenderer extends DefaultTableCellRenderer{
    
    /**
     * this renders the picture in the room list showing if its public or password protected
     */
    
    private static ImageIcon normalOpenRoomIcon = new ImageIcon("data/icons/rooms/lobby.gif");
    private static ImageIcon normalPasswordedRoomIcon = new ImageIcon("data/icons/rooms/lobby_private.gif");
    private static ImageIcon normalOpenRoomLaunchedIcon = new ImageIcon("data/icons/rooms/lobby_busy.gif");
    private static ImageIcon normalPasswordedRoomLaucnhedIcon = new ImageIcon("data/icons/rooms/lobby_private_busy.gif");
    private static ImageIcon instantOpenRoomIcon = new ImageIcon("data/icons/rooms/instantlaunch.gif");
    private static ImageIcon instantPasswordedRoomIcon = new ImageIcon("data/icons/rooms/instantlaunch_private.gif");
    /** Creates a new instance of MyPasswordrenderer */
    public RoomPasswordPicTableCellRenderer() {
    }
    
    @Override
     public void setValue(Object value) {
        int intvalue = (Integer)value;
        
        switch(intvalue){
           case NORMAL_UNPASSWORDED_ROOM:
                setIcon(normalOpenRoomIcon);
                setToolTipText("Lobby is not password protected");  
                break;   
                
           case NORMAL_UNPASSWORDED_ROOM_LAUNCHED:
                setIcon(normalOpenRoomLaunchedIcon);
                setToolTipText("Lobby is launched");  
                break;
                
           case NORMAL_PASSWORDED_ROOM:
                setIcon(normalPasswordedRoomIcon);
                setToolTipText("Password protected lobby");  
                break;
                
           case NORMAL_PASSWORDED_ROOM_LAUNCHED:
                setIcon(normalPasswordedRoomLaucnhedIcon);
                setToolTipText("Lobby is password protected and launched");  
                break;
                
           case INSTANT_UNPASSWORDED_ROOM:
                setIcon(instantOpenRoomIcon);
                setToolTipText("<html>Not password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
                
           case INSTANT_PASSWORDED_ROOM:
                setIcon(instantPasswordedRoomIcon);
                setToolTipText("<html>Password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
        }
    }
}
