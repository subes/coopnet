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

import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.*;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.frames.FrameOrganizer;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author subes
 */
public class FrameIconFlasher extends Thread {

    private static FrameIconFlasher flasher = null;
    private JFrame parent;
    private Image flashIcon,  prevIcon;
    private String flashTitle,  prevTitle;
    private final int flashInterval = 1000;

    private FrameIconFlasher(String flashIcon, String flashTitle) {
        this.parent = FrameOrganizer.getClientFrame();
        changeIconAndTitle(flashIcon, flashTitle);
        start();
    }

    private void changeIconAndTitle(String flashIcon, String flashTitle) {
        this.flashIcon = Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString(flashIcon));
        this.flashTitle = flashTitle;
    }

    public static void flash(String flashIcon, String flashTitle, boolean override) {
        if (flasher == null) {
            flasher = new FrameIconFlasher(flashIcon, flashTitle);
        } else {
            if (override) {
                //Replace current flasher icon and title
                flasher.changeIconAndTitle(flashIcon, flashTitle);
            }
        }
    }

    @Override
    public void run() {
        try {
            prevIcon = parent.getIconImage();
            prevTitle = parent.getTitle();

            while (!parent.isActive()) {
                parent.setIconImage(flashIcon);
                parent.setTitle(flashTitle);
                if (SystemTray.isSupported() && Settings.getTrayIconEnabled()) {
                    FrameOrganizer.getTrayIcon().setImage(flashIcon);
                }
                try {
                    sleep(flashInterval);
                } catch (InterruptedException ex) {
                }
                parent.setIconImage(prevIcon);
                parent.setTitle(prevTitle);
                if (SystemTray.isSupported() && Settings.getTrayIconEnabled()) {
                    FrameOrganizer.getTrayIcon().setImage(prevIcon);
                }
                try {
                    sleep(flashInterval);
                } catch (InterruptedException ex) {
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e);
        }
        flasher = null;
    }
}
