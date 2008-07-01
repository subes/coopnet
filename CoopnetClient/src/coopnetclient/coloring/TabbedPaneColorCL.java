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

import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabbedPaneColorCL implements ChangeListener {

    JTabbedPane parent;
    Color unselectedBG;

    public TabbedPaneColorCL(JTabbedPane parent) {
        this.parent = parent;
        unselectedBG = coopnetclient.coloring.Colorizer.getSelectionColor();

        updateBG();
    }

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
