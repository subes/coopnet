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
import coopnetclient.frames.models.VoiceChatChannelListModel;
import coopnetclient.frames.models.VoiceChatChannelListModel.Channel;
import coopnetclient.utils.Settings;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * Renders the elements in the contact list
 */
public class VoiceChatRenderer extends DefaultListCellRenderer {

    public static ImageIcon emptyIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/voicechat/empty.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon talkingIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/voicechat/talking.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon mutedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("data/icons/quicktab/voicechat/muted.png").getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    private VoiceChatChannelListModel model;

    public VoiceChatRenderer(VoiceChatChannelListModel model) {
        setOpaque(true);
        this.model = model;
        putClientProperty("html.disable", Boolean.TRUE);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected && !(value.toString().equals(Globals.getThisPlayer_loginName())), cellHasFocus);
        setFont(new Font(Settings.getNameStyle(), Font.PLAIN, 14));
        setToolTipText("<html><xmp>" + value.toString());
        setText(value.toString());

        //set foreground
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected && !(value.toString().equals(Globals.getThisPlayer_loginName()))) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        }
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        Channel c = model.getChannel(value.toString());
        if (c != null) {
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            setFont(new Font(Settings.getNameStyle(), Font.BOLD, 14));
        } else { //username
            if (model.isMuted(value.toString())) {
                setIcon(mutedIcon);
            } else {
                if (model.isTalking(value.toString())) {
                    setIcon(talkingIcon);
                } else {
                    setIcon(emptyIcon);
                }
            }
        }
        return this;
    }
}
