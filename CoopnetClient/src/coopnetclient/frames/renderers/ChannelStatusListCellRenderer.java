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
import coopnetclient.frames.models.ChannelStatusListModel;
import coopnetclient.utils.Settings;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Renders the elements in the user list of a room
 */
public class ChannelStatusListCellRenderer extends JLabel implements ListCellRenderer {

    public static ImageIcon chatIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/inchat.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon lobbyIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/inlobby.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon gameIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/ingame.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    private ChannelStatusListModel model;

    public ChannelStatusListCellRenderer(ChannelStatusListModel model) {
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
            if (isSelected && !(value.toString().equals(Globals.getThisPlayer_loginName()))) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
        }

        if (model.isPlaying(value)) {
            setIcon(gameIcon);
        } else if (model.isInRoom(value)) {
            setIcon(lobbyIcon);
        } else {
            setIcon(chatIcon);
        }

        return this;
    }
}
