/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.EscapeChars;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.Settings;
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
public class RoomPlayerStatusListCellRenderer extends JLabel implements ListCellRenderer {

    private Vector<String> readyList = new Vector<String>();
    private Vector<String> playingList = new Vector<String>();
    private Vector<String> awayList = new Vector<String>();
    
    private static Border selectionBorder = BorderFactory.createLineBorder(Colorizer.getSelectionColor() , 2);
    private static Border normalBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    
    public RoomPlayerStatusListCellRenderer() {
        setOpaque(true);
    }
        
    public void removePlayer(String playerName){
        readyList.remove(playerName);
        playingList.remove(playerName);
        awayList.remove(playerName);
        Globals.getClientFrame().repaint();
    }

    public void setAway(String playername) {
        if(!awayList.contains(playername)){
            awayList.add(playername);
            Globals.getClientFrame().repaint();
        }
    }

    public void unSetAway(String playername) {
        awayList.remove(playername);
        Globals.getClientFrame().repaint();
    }

    public void updateName(String oldname, String newname) {
        if (playingList.remove(oldname)) {
            playingList.add(newname);
        }
        if (readyList.remove(oldname)) {
            readyList.add(newname);
        }
        if (awayList.remove(oldname)) {
            awayList.add(newname);
        }
        Globals.getClientFrame().repaint();
    }

    public void setPlaying(String playername) {
        if (!playingList.contains(playername)) {
            playingList.add(playername);
        }
        Globals.getClientFrame().repaint();
    }

    public void readyPlayer(String playername) {
        if (!readyList.contains(playername)) {
            readyList.add(playername);
        }
        Globals.getClientFrame().repaint();
    }

    public void unReadyPlayer(String playername) {
        readyList.remove(playername);
        playingList.remove(playername);
        awayList.remove(playername);
        Globals.getClientFrame().repaint();
    }

    public void launch() {
        for (String playername : readyList) {
            if (!playingList.contains(playername)) {
                playingList.add(playername);
            }
        }
        Globals.getClientFrame().repaint();
    }

    public void gameClosed(String playername) {
        playingList.remove(playername);
        Globals.getClientFrame().repaint();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        setHorizontalTextPosition(JLabel.RIGHT);
        setHorizontalAlignment(JLabel.LEFT);
        setIconTextGap(0);
        //set foreground
        setForeground(Color.black);
        setBorder(normalBorder);
        setToolTipText("<html><xmp>"+value.toString()+"</xmp><br>Press middle mouse button to toggle highlight.");
        //set background color        
        if (readyList.contains(value.toString())) {
            if (playingList.contains(value.toString())) {
                setBackground(Color.yellow);
            } else {
                setBackground(Color.green);
            }
        } else {
            if (playingList.contains(value.toString())) {
                setBackground(Color.yellow);
            } else {
                setBackground(Color.red);
            }
        }


        String text = EscapeChars.forHTML(value.toString());
        if(Globals.isHighlighted(value.toString())){
            setText("<html>&nbsp;<u>"+text+"</u></html>");
        }else{
            setText("<html>&nbsp;"+text+"</html>");
        }

        if (isSelected && !(value.toString().equals(Globals.getThisPlayer_loginName()))) {
            setBorder(selectionBorder);
        }
        //is away?
        if(awayList.contains(value.toString())){
            setIcon(Icons.awayIcon);
        }else{
            setIcon(null);
        }
        if(value.toString().equals( TabOrganizer.getRoomPanel().getRoomData().getHostName() )){
            setFont(new Font(Settings.getNameStyle(), Font.BOLD, 14));
        }
        
        return this;
    }
}
