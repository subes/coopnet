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
package coopnetclient.utils.hotkeys;

import coopnetclient.enums.LogTypes;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.utils.Logger;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListenerDebugEnabled;
import jxgrabkey.JXGrabKey;

public class JXGrabKeyHandler extends HotkeyHandler implements HotkeyListenerDebugEnabled {

    public JXGrabKeyHandler() {
        JXGrabKey.setDebugOutput(true);
        JXGrabKey.getInstance().addHotkeyListener(this);
    }

    @Override
    public void registerHotkey(int id, final int mask, final int key) {
        try {
            JXGrabKey.getInstance().registerAWTHotkey(id, mask, key);
        } catch (HotkeyConflictException e) {
            new Thread() {

                @Override
                public void run() {
                    String hotkey = KeyEvent.getKeyModifiersText(mask);
                    if (hotkey.length() > 0) {
                        hotkey += "+";
                    }
                    hotkey += KeyEvent.getKeyText(key);

                    String title = "Unable to register hotkey";
                    String message = "<html>Coopnet was unable to register the hotkey <b>" + hotkey + "</b> on your system." +
                            "<br>Another application might already use this combination," +
                            "<br><b>please reassign the hotkey</b> either there or here.";
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(), message, title, JOptionPane.WARNING_MESSAGE);
                }
            }.start();
        }
    }

    @Override
    public void unregisterHotkey(int id) {
        JXGrabKey.getInstance().unregisterHotKey(id);
    }

    @Override
    public void cleanUp() {
        JXGrabKey.getInstance().cleanUp();
    }

    @Override
    public void onHotkey(int id) {
        Hotkeys.onHotkey(id);
    }

    @Override
    public void debugCallback(String message) {
        Logger.log(LogTypes.HOTKEYS, message);
    }
}
