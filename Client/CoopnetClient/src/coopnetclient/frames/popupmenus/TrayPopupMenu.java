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
package coopnetclient.frames.popupmenus;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.utils.settings.Settings;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TrayPopupMenu extends PopupMenu implements ActionListener, ItemListener {

    private CheckboxMenuItem mi_show;
    private MenuItem mi_quit;
    private CheckboxMenuItem mi_sounds;

    public TrayPopupMenu() {
        mi_show = new CheckboxMenuItem("Show Main Frame");
        mi_sounds = new CheckboxMenuItem("Sounds");
        mi_quit = new MenuItem("Quit");
        mi_quit.addActionListener(this);
        mi_sounds.addItemListener(this);
        mi_show.addItemListener(this);
        add(mi_show);
        addSeparator();
        add(mi_sounds);
        addSeparator();
        add(mi_quit);

        updateSettings();
    }

    public void updateSettings(){
        mi_sounds.setState(Settings.getSoundEnabled());
        if(FrameOrganizer.getClientFrame() != null){
            mi_show.setState(FrameOrganizer.getClientFrame().isVisible());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(mi_quit)){
            Client.quit(true);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource().equals(mi_sounds)){
            Settings.setSoundEnabled(mi_sounds.getState());
            Globals.updateSettings();
        }else if (e.getSource().equals(mi_show)){
            if(mi_show.getState()){
                FrameOrganizer.showClientFrame();
            }else{
                FrameOrganizer.hideClientFrame();
            }
        }
    }

}
