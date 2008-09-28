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

import coopnetclient.utils.Settings;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class KeyGrabberTextField extends JTextField implements FocusListener, KeyListener {

    private int key = 0;
    private int modifiers = 0;
    
    public KeyGrabberTextField() {
        addFocusListener(this);
        addKeyListener(this);
    }
    
    public void setKey(int key, int modifiers){
        this.key = key;
        this.modifiers = modifiers;
        printText();
    }
                
    public int getKey(){
        return key;
    }
    
    public int getModifiers(){
        return modifiers;
    }
    
    private void printText(){
        if(hasFocus()){
            setText("Hit a combination of keys ...");
        }else{
            if(key != KeyEvent.VK_UNDEFINED){
                setText(KeyEvent.getKeyModifiersText(modifiers)+"+"+KeyEvent.getKeyText(key));
            }else{
                setText("Disabled");
            }
        }
    }


    public void focusGained(FocusEvent e) {
        if (Settings.getColorizeBody()) {
            setBackground(Settings.getSelectionColor());
        } else {
            setBackground((Color) UIManager.get("List.selectionBackground"));
        }
        printText();
    }

    public void focusLost(FocusEvent e) {
        if (Settings.getColorizeBody()) {
            setBackground(Settings.getBackgroundColor());
        } else {
            setBackground((Color) UIManager.get("TextArea.background"));
        }
        printText();
    }

    public void keyTyped(KeyEvent e) {
        if(e.getModifiers() != 0){
            modifiers = e.getModifiers();
            setFocusable(false);
            setFocusable(true);
        }
        e.consume();
        printText();
    }

    public void keyPressed(KeyEvent e) {
        if(e.getModifiers() == 0 && e.getKeyCode() == e.VK_BACK_SPACE){
            key = e.VK_UNDEFINED;
            modifiers = 0;
            setFocusable(false);
            setFocusable(true);
        }else{
            if(        e.getKeyCode() != KeyEvent.VK_SHIFT
                    && e.getKeyCode() != KeyEvent.VK_CONTROL
                    && e.getKeyCode() != KeyEvent.VK_META
                    && e.getKeyCode() != KeyEvent.VK_ALT
                    && e.getKeyCode() != KeyEvent.VK_ALT_GRAPH){
                key = e.getKeyCode();
            }
        }
        e.consume();
    }

    public void keyReleased(KeyEvent e) {
        e.consume();
    }
    
}
