/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

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

package coopnetclient.coloring;

import coopnetclient.Settings;
import coopnetclient.modules.ChannelStatusListModel;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class ChannelStatusListCR extends JLabel implements ListCellRenderer {

    /**
     * Renders the elements in the user list of a room
     */
    public static ImageIcon chaticon = new ImageIcon("data/icons/chaticon.gif");
    public static ImageIcon roomicon = new ImageIcon("data/icons/roomicon.gif");
    public static ImageIcon gameicon = new ImageIcon("data/icons/gameicon.gif");
    private ChannelStatusListModel model;

    public ChannelStatusListCR(ChannelStatusListModel model) {
        setOpaque(true);
        this.model = model;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        setToolTipText(value.toString());
        //set foreground
        setText(value.toString());
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        } else {
            if (isSelected) {
                setForeground(UIManager.getColor("List.selectionForeground"));
                setBackground(UIManager.getColor("List.selectionBackground"));
            } else {
                setForeground(null);
                setBackground(null);
            }
        }

        if (model.isPlaying(value)) {
            setIcon(gameicon);
        } else if (model.isInRoom(value)) {
            setIcon(roomicon);
        } else {
            setIcon(chaticon);
        }

        return this;
    }
}
