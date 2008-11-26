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
import coopnetclient.frames.models.RoomTableModel;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

public class ChannelRoomStatusRenderer extends DefaultTableCellRenderer{
    
    /**
     * this renders the picture in the room list showing if its public or password protected
     */
    private static ImageIcon normalOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static ImageIcon normalPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static ImageIcon normalOpenRoomLaunchedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static ImageIcon normalPasswordedRoomLaucnhedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static ImageIcon instantOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static ImageIcon instantPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    /** Creates a new instance of MyPasswordrenderer */
    public ChannelRoomStatusRenderer() {
    }
    
    @Override
     public void setValue(Object value) {
        int intvalue = (Integer)value;
        
        switch(intvalue){
           case RoomTableModel.NORMAL_UNPASSWORDED_ROOM:
                setIcon(normalOpenRoomIcon);
                setToolTipText("Lobby is not password protected");  
                break;   
                
           case RoomTableModel.NORMAL_UNPASSWORDED_ROOM_LAUNCHED:
                setIcon(normalOpenRoomLaunchedIcon);
                setToolTipText("Lobby is launched");  
                break;
                
           case RoomTableModel.NORMAL_PASSWORDED_ROOM:
                setIcon(normalPasswordedRoomIcon);
                setToolTipText("Password protected lobby");  
                break;
                
           case RoomTableModel.NORMAL_PASSWORDED_ROOM_LAUNCHED:
                setIcon(normalPasswordedRoomLaucnhedIcon);
                setToolTipText("Lobby is password protected and launched");  
                break;
                
           case RoomTableModel.INSTANT_UNPASSWORDED_ROOM:
                setIcon(instantOpenRoomIcon);
                setToolTipText("<html>Not password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
                
           case RoomTableModel.INSTANT_PASSWORDED_ROOM:
                setIcon(instantPasswordedRoomIcon);
                setToolTipText("<html>Password protected instant-launch-room <br>(directly launches game for every player joining the room)");  
                break;
        }
    }
}
