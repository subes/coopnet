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

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author subes
 */
public class FrameIconFlasher extends Thread {

    private static boolean isActive = false;
    private JFrame parent;
    private Image flashIcon;
    private String flashTitle;
    private final int flashInterval = 1000;

    public FrameIconFlasher(JFrame flashOn, String flashIcon, String flashTitle) {
        if (FrameIconFlasher.isActive == false) {
            parent = flashOn;
            this.flashIcon = Toolkit.getDefaultToolkit().getImage(flashIcon);
            this.flashTitle = flashTitle;
            start();
            FrameIconFlasher.isActive = true;
        }
    }

    @Override
    public void run() {
        try{
            Image prevIcon = parent.getIconImage();
            String prevTitle = parent.getTitle();

            while (!parent.isActive()) {
                parent.setIconImage(flashIcon);
                parent.setTitle(flashTitle);
                if(SystemTray.isSupported() && Settings.getTrayIconEnabled()){
                    Globals.getTrayIcon().setImage(flashIcon);
                }
                try {
                    sleep(flashInterval);
                } catch (InterruptedException ex) {
                }
                parent.setIconImage(prevIcon);
                parent.setTitle(prevTitle);
                if(SystemTray.isSupported() && Settings.getTrayIconEnabled()){
                    Globals.getTrayIcon().setImage(prevIcon);
                }
                try {
                    sleep(flashInterval);
                } catch (InterruptedException ex) {
                }
            }
            FrameIconFlasher.isActive = false;
        }catch(Exception e){
            ErrorHandler.handleException(e);
        }
    }
}
