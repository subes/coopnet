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

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import coopnetclient.Globals;
import coopnetclient.frames.clientframe.TabOrganizer;
import java.awt.event.KeyEvent;

public class HotKeyControl implements HotkeyListener {

    private static int ACTION_LAUNCH = 1;
    private static HotKeyControl controllerObject = new HotKeyControl();

    private HotKeyControl() {
        super();
    }

    public static void bindKeys() {
        switch (Globals.getOperatingSystem()) {
            case WINDOWS:
                if(Settings.getLaunchHotKey() != KeyEvent.VK_UNDEFINED){
                    JIntellitype.getInstance().registerSwingHotKey(ACTION_LAUNCH, Settings.getLaunchHotKeyMask(), Settings.getLaunchHotKey());
                    JIntellitype.getInstance().addHotKeyListener(controllerObject);
                }
                break;
            case LINUX:
                break;
        }
    }

    public static void unbindKeys() {
        switch (Globals.getOperatingSystem()) {
            case WINDOWS:
                JIntellitype.getInstance().unregisterHotKey(ACTION_LAUNCH);
                break;
            case LINUX:
                break;
        }
    }

    public static void reBind() {
        unbindKeys();
        bindKeys();
    }

    public static void cleanUp() {
        switch (Globals.getOperatingSystem()) {
            case WINDOWS:
                JIntellitype.getInstance().cleanUp();
                break;
            case LINUX:
                break;
        }
    }

    @Override
    public void onHotKey(int arg0) {
        //System.out.println("hotkey event recieved:" +arg0);
        //launch action
        if (arg0 == ACTION_LAUNCH) {
            if (TabOrganizer.getRoomPanel() != null ) {
               TabOrganizer.getRoomPanel().PressLaunch();
            }
        }
    }
}
