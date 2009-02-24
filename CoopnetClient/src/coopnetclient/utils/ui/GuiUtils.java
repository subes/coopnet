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

package coopnetclient.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public final class GuiUtils {

    private GuiUtils(){}

    public static void setControlsEnabled(Container root, boolean enabled){
        if(root == null){
            return;
        }
        setControlsEnabledRecursively(root, enabled);
    }

    private static void setControlsEnabledRecursively(Container root, boolean enabled){
        Component[] components;

        if (root instanceof JMenuBar) {
            JMenuBar mbar = (JMenuBar) root;
            components = new Component[mbar.getMenuCount()];
            for (int i = 0; i < components.length; i++) {
                components[i] = mbar.getMenu(i);
            }
        } else if (root instanceof JMenu) {
            JMenu m = (JMenu) root;
            components = m.getMenuComponents();
        } else {
            //Treat it normally
            components = root.getComponents();
        }

        for (Component c : components) {
            c.setEnabled(enabled);

            if(c instanceof Container){
                setControlsEnabledRecursively((Container)c, enabled);
            }
        }
    }
}
