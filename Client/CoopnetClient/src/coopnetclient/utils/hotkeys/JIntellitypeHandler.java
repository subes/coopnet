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

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class JIntellitypeHandler extends HotkeyHandler implements HotkeyListener{

    public JIntellitypeHandler() {
        JIntellitype.getInstance().addHotKeyListener(this);
    }
    
    @Override
    public void registerHotkey(int id, int mask, int key) {
        JIntellitype.getInstance().registerSwingHotKey(id, mask, key);
    }

    @Override
    public void unregisterHotkey(int id) {
        JIntellitype.getInstance().unregisterHotKey(id);
    }

    @Override
    public void cleanUp() {
        JIntellitype.getInstance().cleanUp();
    }

    @Override
    public void onHotKey(int id) {
        Hotkeys.onHotkey(id);
    }

}
