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
import coopnetclient.utils.Settings;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Renders the elements in the contact list
 */
public class ContactListRenderer extends JLabel implements ListCellRenderer {

    public static ImageIcon chatIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/inchat.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon lobbyIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/inlobby.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon gameIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/ingame.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon pendingRequestIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/pending_request.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon pendingContactIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/pending_contact.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon offlineIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/playerstatus/offline.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    private ContactListModel model;

    public ContactListRenderer(ContactListModel model) {
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
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        if(index < model.getFirstGroupIndex()){
            //Pendingrequests are always shown above a group!
            setIcon(pendingRequestIcon);
            return this;
        }
        
        switch (model.getStatus(value.toString())) {
            case CHATTING:
                setIcon(chatIcon);
                break;
            case IN_ROOM:
                setIcon(lobbyIcon);
                break;
            case PENDING_REQUEST:
                setIcon(pendingRequestIcon);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                break;
            case PENDING_CONTACT:
                setIcon(pendingContactIcon);
                break;
            case PLAYING:
                setIcon(gameIcon);
                break;
            case OFFLINE:
                setIcon(offlineIcon);
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
        }
        return this;
    }
}
