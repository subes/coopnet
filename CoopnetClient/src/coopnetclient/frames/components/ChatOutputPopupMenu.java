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

package coopnetclient.frames.components;

import coopnetclient.Globals;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

public class ChatOutputPopupMenu extends TextComponentPopupMenu {

    protected JMenuItem clearHighlight;

    public ChatOutputPopupMenu(JTextComponent parent) {
        super(parent);

        clearHighlight = new JMenuItem("Clear Highlighting");
        clearHighlight.addActionListener(this);
        this.add(clearHighlight);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        clearHighlight.setEnabled(Globals.getHighlightList().size() > 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if(e.getSource() == clearHighlight){
            Globals.clearHighlights();
        }
    }



}
