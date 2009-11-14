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

import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.popupmenus.TrayPopupMenu;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author subes
 */
public class CoopnetTrayIcon extends TrayIcon implements MouseListener {
    private TrayPopupMenu popup;

    public CoopnetTrayIcon(){
        super(Icons.coopnetNormalIcon.getImage(), "CoopnetClient");
        popup = new TrayPopupMenu();
        setPopupMenu(popup);
        setImageAutoSize(true);
        addMouseListener(this);
    }

    public void addTrayIcon() throws AWTException {
        SystemTray.getSystemTray().add(this);
    }

    public void removeTrayIcon(){
        SystemTray.getSystemTray().remove(this);
    }

    public void updateSettings(){
        popup.updateSettings();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3){
            popup.updateSettings();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            if(FrameOrganizer.getClientFrame().isVisible()){
                FrameOrganizer.hideClientFrame();
            }else{
                FrameOrganizer.showClientFrame();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
