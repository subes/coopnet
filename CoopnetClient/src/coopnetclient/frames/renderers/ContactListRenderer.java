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

import coopnetclient.frames.models.ContactListModel;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.Settings;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * Renders the elements in the contact list
 */
public class ContactListRenderer extends DefaultListCellRenderer {

    private ContactListModel model;

    public ContactListRenderer(ContactListModel model) {
        setOpaque(true);
        this.model = model;
        putClientProperty("html.disable", Boolean.TRUE);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        setToolTipText("<html><xmp>"+value.toString());
        setText(value.toString());
        
        //set foreground
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        }
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        if(index < model.getFirstGroupIndex()){
            //Pendingrequests are always shown above a group!
            setIcon(Icons.pendingRequestIcon);
            return this;
        }
        
        switch (model.getStatus(value.toString())) {
            case CHATTING:
                setIcon(Icons.chatIcon);
                break;
            case IN_ROOM:
                setIcon(Icons.lobbyIcon);
                break;
            case PENDING_REQUEST:
                setIcon(Icons.pendingRequestIcon);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                break;
            case PENDING_CONTACT:
                setIcon(Icons.pendingContactIcon);
                break;
            case PLAYING:
                setIcon(Icons.gameIcon);
                break;
            case OFFLINE:
                setIcon(Icons.offlineIcon);
                break;
            case GROUPNAME_OPEN:
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                setIcon(UIManager.getIcon("Tree.expandedIcon"));
                setFont(new Font(Settings.getNameStyle(), Font.BOLD, 14));
                break;
            case GROUPNAME_CLOSED:
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                setIcon(UIManager.getIcon("Tree.collapsedIcon"));
                setFont(new Font(Settings.getNameStyle(), Font.BOLD, 14));
                break;
            case AWAY:
                setIcon(Icons.awayIcon);
                break;
        }
        return this;
    }
}
