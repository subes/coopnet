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

package coopnetclient.coloring;

import coopnetclient.Settings;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RoomStatusListCR extends JLabel implements ListCellRenderer {

    /**
     * Renders the elements in the user list of a room
     */
    public static Vector<String> readylist = new Vector<String>();
    public static Vector<String> playinglist = new Vector<String>();

    public RoomStatusListCR() {
        setOpaque(true);
    }

    public static void updateName(String oldname, String newname) {
        if (readylist.remove(oldname)) {
            readylist.add(newname);
        }
        if (playinglist.remove(oldname)) {
            playinglist.add(newname);
        }
    }

    public static void setPlaying(String playername) {
        if (!playinglist.contains(playername)) {
            playinglist.add(playername);
        }
    }

    public static void readyPlayer(String playername) {
        if (!readylist.contains(playername)) {
            readylist.add(playername);
        }
    }

    public static void unReadyPlayer(String playername) {
        readylist.remove(playername);
        playinglist.remove(playername);
    }

    public static void launch() {
        for (String playername : readylist) {
            if (!playinglist.contains(playername)) {
                playinglist.add(playername);
            }
        }
    }

    public static void gameClosed(String playername) {
        playinglist.remove(playername);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        //set foreground
        setForeground(Color.black);
        if (isSelected) {
            setText("<html><pre><b><i>" + value.toString() + "</i></b></pre></html>");

        } else {
            setText("<html><pre>" + value.toString() + "</pre></html>");
        }
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
        return this;
    }
}
