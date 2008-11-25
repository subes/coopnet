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
package coopnetclient.utils.hotkeys;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import coopnetclient.voicechat.VoicePlayback;
import java.awt.event.KeyEvent;
import java.io.File;

public class Hotkeys {

    public static int ACTION_LAUNCH = 1;
    public static int PUSH_TO_TALK = 2;
    private static HotkeyHandler handler;

    static {
        switch (Globals.getOperatingSystem()) {
            case WINDOWS:
                System.loadLibrary("lib/JIntellitype");
                handler = new JIntellitypeHandler();
                break;
            case LINUX:
                File curDir = new File(".");
                System.load(curDir.getAbsolutePath() + "/lib/libJXGrabKey.so");
                handler = new JXGrabKeyHandler();
                break;
        }
    }

    private Hotkeys() {
    }

    public static void bindHotKey(int action) {
        if (action == ACTION_LAUNCH) {
            Logger.log(LogTypes.LOG, "Binding hotkey");
            if (Settings.getLaunchHotKey() != KeyEvent.VK_UNDEFINED) {
                handler.registerHotkey(ACTION_LAUNCH, Settings.getLaunchHotKeyMask(), Settings.getLaunchHotKey());
            }
        }

        if (action == ACTION_LAUNCH) {
            if (Settings.getPushToTalkHotKey() != KeyEvent.VK_UNDEFINED) {
                handler.registerHotkey(PUSH_TO_TALK, Settings.getPushToTalkHotKeyMask(), Settings.getPushToTalkHotKey());
            }
        }
    }

    public static void reBindHotKey(int action) {
        unbindHotKey(action);
        bindHotKey(action);
    }

    public static void unbindHotKey(int action) {
        Logger.log(LogTypes.LOG, "UnBinding hotkey");
        handler.unregisterHotkey(action);
    }

    public static void cleanUp() {
        handler.cleanUp();
    }

    protected static void onHotkey(int id) {
        if (id == ACTION_LAUNCH) {
            if (TabOrganizer.getRoomPanel() != null) {
                TabOrganizer.getRoomPanel().pressLaunch();
            }
        } else if (id == PUSH_TO_TALK) {
            VoicePlayback.pushToTalk();
        }
    }
}
