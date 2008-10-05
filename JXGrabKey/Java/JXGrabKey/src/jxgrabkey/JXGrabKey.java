/*	Copyright 2008  Edwin Stang (edwinstang@gmail.com), 
 *
 *  This file is part of JXGrabKey.
 *
 *  JXGrabKey is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JXGrabKey is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JXGrabKey.  If not, see <http://www.gnu.org/licenses/>.
 */

package jxgrabkey;

import java.util.ArrayList;

public class JXGrabKey {

    private static JXGrabKey instance;
    private static ArrayList<HotkeyListener> listeners;
    
    private JXGrabKey() {
        listeners = new ArrayList<HotkeyListener>();
        new Thread(){
            @Override
            public void run() {
                listen();
            }
        }.start();
    }
    
    public static JXGrabKey getInstance(){
        if(instance == null){
            instance = new JXGrabKey();
        }        
        return instance;
    }
    
    public void addHotkeyListener(HotkeyListener listener){
        JXGrabKey.listeners.add(listener);
    }
    
    public void removeHotkeyListener(HotkeyListener listener){
        JXGrabKey.listeners.remove(listener);
    }
    
    public void cleanUp(){
        clean();
        listeners.clear();
    }
    
    private native void clean();
    
    public native void registerHotkey(int id, int mask, int key);
    
    public void registerSwingHotkey(int id, int mask, int key){
        
    }
    
    public native void unregisterHotKey(int id);
    
    public native void listen();
    
    public static void fireKeyEvent(int id){
        for(int i = 0; i < listeners.size(); i++){
            if(listeners.get(i) != null){
                listeners.get(i).onHotkey(id);
            }else{
                listeners.remove(i);
            }
        }
    }
    
}
