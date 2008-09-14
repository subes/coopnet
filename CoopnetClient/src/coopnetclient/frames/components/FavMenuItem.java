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

package coopnetclient.frames.components;

import coopnetclient.Client;
import coopnetclient.protocol.out.Protocol;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class FavMenuItem extends JMenuItem {

    public FavMenuItem() {
        super();
    }

    public FavMenuItem(String name) {
        super();
        setText(name);
        addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Protocol.joinChannel(e.getSource().toString());
            }
        });
    }

    @Override
    public String toString() {
        return getText();
    }
}
