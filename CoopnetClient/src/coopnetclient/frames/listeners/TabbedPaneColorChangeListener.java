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

package coopnetclient.frames.listeners;

import coopnetclient.utils.ui.Colors;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabbedPaneColorChangeListener implements ChangeListener {

    private JTabbedPane parent;
    private Color unselectedBG;

    public TabbedPaneColorChangeListener(JTabbedPane parent) {
        this.parent = parent;
        unselectedBG = Colors.getSelectionColor();

        updateBG();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateBG();
    }

    public void updateBG() {
        if (parent.getTabCount() > 0) {
            for (int i = 0; i < parent.getTabCount(); i++) {
                parent.setBackgroundAt(i, unselectedBG);
            }
        }
    }
}
