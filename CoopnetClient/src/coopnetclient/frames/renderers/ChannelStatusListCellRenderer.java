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
import coopnetclient.enums.PlayerStatuses;
import coopnetclient.frames.models.ChannelStatusListModel;
import coopnetclient.utils.EscapeChars;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.Settings;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.StyledTextAction;

/**
 * Renders the elements in the user list of a room
 */
public class ChannelStatusListCellRenderer extends DefaultListCellRenderer {

    private ChannelStatusListModel model;

    public ChannelStatusListCellRenderer(ChannelStatusListModel model) {
        setOpaque(true);
        this.model = model;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, (value.toString().equals(Globals.getThisPlayer_loginName())) ? false : isSelected, cellHasFocus);
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        setToolTipText("<html><xmp>" + value.toString()+"</xmp><br>Press middle mouse button to toggle highlight.");

        String text = EscapeChars.forHTML(value.toString());
        if(Globals.isHighlighted(value.toString())){
            setText("<html><u>"+text+"</u></html>");
        }else{
            setText("<html>"+text+"</html>");
        }
        
        
        //set foreground        
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        }

        PlayerStatuses status = model.getPlayerStatus(value.toString());
        if (status != null) {
            switch (status) {
                case AWAY:
                    setIcon(Icons.awayIcon);
                    break;
                case CHATTING:
                    setIcon(Icons.chatIcon);
                    break;
                case IN_ROOM:
                    setIcon(Icons.lobbyIcon);
                    break;
                case PLAYING:
                    setIcon(Icons.gameIcon);
                    break;
            }
        }
        return this;
    }
}
