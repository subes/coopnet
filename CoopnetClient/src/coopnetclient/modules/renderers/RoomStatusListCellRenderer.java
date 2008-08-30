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

package coopnetclient.modules.renderers;

import coopnetclient.Globals;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.Colorizer;
import coopnetclient.modules.Settings;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;


/**
 * Renders the elements in the user list of a room
 */
public class RoomStatusListCellRenderer extends JLabel implements ListCellRenderer {

    private Vector<String> readylist = new Vector<String>();
    private Vector<String> playinglist = new Vector<String>();
    
    private static Border selectionBorder = BorderFactory.createLineBorder(Colorizer.getSelectionColor() , 2);

    public RoomStatusListCellRenderer() {
        setOpaque(true);
    }
    
    public void removePlayer(String playerName){
        readylist.remove(playerName);
        playinglist.remove(playerName);
        Globals.getClientFrame().repaint();
    }

    public void updateName(String oldname, String newname) {
        if (playinglist.remove(oldname)) {
            playinglist.add(newname);
        }
        if (readylist.remove(oldname)) {
            readylist.add(newname);
        }        
        Globals.getClientFrame().repaint();
    }

    public void setPlaying(String playername) {
        if (!playinglist.contains(playername)) {
            playinglist.add(playername);
        }
        Globals.getClientFrame().repaint();
    }

    public void readyPlayer(String playername) {
        if (!readylist.contains(playername)) {
            readylist.add(playername);
        }
        Globals.getClientFrame().repaint();
    }

    public void unReadyPlayer(String playername) {
        readylist.remove(playername);
        playinglist.remove(playername);
        Globals.getClientFrame().repaint();
    }

    public void launch() {
        for (String playername : readylist) {
            if (!playinglist.contains(playername)) {
                playinglist.add(playername);
            }
        }
        Globals.getClientFrame().repaint();
    }

    public void gameClosed(String playername) {
        playinglist.remove(playername);
        Globals.getClientFrame().repaint();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        //set foreground
        setForeground(Color.black);
        setBorder(null);
        //set background color
        if (readylist.contains(value.toString())) {
            if (playinglist.contains(value.toString())) {
                setBackground(Color.yellow);
            } else {
                setBackground(Color.green);
            }
        } else {
            setBackground(Color.red);
        }
        if (isSelected && !(value.toString().equals(Globals.getThisPlayer_loginName()))) {
            setText("<html><pre>&nbsp;" + value.toString() + "</pre></html>");
            setBorder(selectionBorder);

        } else {
            setText("<html><pre>&nbsp;" + value.toString() + "</pre></html>");
        }
        if(value.toString().equals( TabOrganizer.getRoomPanel().hostName )){
            setFont(new Font(Settings.getNameStyle(), Font.BOLD, 14));
        }
        
        return this;
    }
}
