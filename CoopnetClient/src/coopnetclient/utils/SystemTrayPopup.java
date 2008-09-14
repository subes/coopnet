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


package coopnetclient.utils;

import coopnetclient.Globals;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SystemTrayPopup extends PopupMenu{

    public SystemTrayPopup(){
        MenuItem mi_next = new MenuItem("Show Client-frame");
        MenuItem mi_exit = new MenuItem("Exit");
        mi_exit.addActionListener(exitListener);
        mi_next.addActionListener(showListener);
        add(mi_next);
        add(mi_exit);
    }
    
    private static ActionListener exitListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Globals.getClientFrame().quit(true);
        }
    };
    private static ActionListener showListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Globals.getClientFrame().setVisible(true);
        }
    };
}
