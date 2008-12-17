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

import coopnetclient.frames.models.RoomTableModel;
import coopnetclient.utils.Icons;
import javax.swing.table.DefaultTableCellRenderer;

public class ChannelRoomStatusRenderer extends DefaultTableCellRenderer{
    
    /**
     * this renders the picture in the room list showing if its public or password protected
     */
    /** Creates a new instance of MyPasswordrenderer */
    public ChannelRoomStatusRenderer() {
    }
    
    @Override
     public void setValue(Object value) {
        int intvalue = (Integer)value;
        
        switch(intvalue){
           case RoomTableModel.NORMAL_UNPASSWORDED_ROOM:
                setIcon(Icons.normalOpenRoomIcon);
                setToolTipText("Lobby is not password protected");  
                break;   
                
           case RoomTableModel.NORMAL_UNPASSWORDED_ROOM_LAUNCHED:
                setIcon(Icons.normalOpenRoomLaunchedIcon);
                setToolTipText("Lobby is launched");  
                break;
                
           case RoomTableModel.NORMAL_PASSWORDED_ROOM:
                setIcon(Icons.normalPasswordedRoomIcon);
                setToolTipText("Password protected lobby");  
                break;
                
           case RoomTableModel.NORMAL_PASSWORDED_ROOM_LAUNCHED:
                setIcon(Icons.normalPasswordedRoomLaucnhedIcon);
                setToolTipText("Lobby is password protected and launched");  
                break;
                
           case RoomTableModel.INSTANT_UNPASSWORDED_ROOM:
                setIcon(Icons.instantOpenRoomIcon);
                setToolTipText("<html>Not password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
                
           case RoomTableModel.INSTANT_PASSWORDED_ROOM:
                setIcon(Icons.instantPasswordedRoomIcon);
                setToolTipText("<html>Password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
        }
    }
}
