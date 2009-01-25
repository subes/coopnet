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

import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.Settings;
import coopnetclient.utils.hotkeys.Hotkeys;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class KeyGrabberTextField extends JTextField implements FocusListener, KeyListener {

    private int action = 0;
    private int key = 0;
    private int modifiers = 0;
    private boolean singleKeyEnabled = false;

    public KeyGrabberTextField(){
        super();
    }

    public KeyGrabberTextField(int action,boolean enableSingleKey) {
        super();
        addFocusListener(this);
        addKeyListener(this);
        singleKeyEnabled = enableSingleKey;
        this.action = action;
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
                String text = KeyEvent.getKeyModifiersText(modifiers);
                if(text.length() > 0){
                    text += "+";
                }
                text += KeyEvent.getKeyText(key);
                
                setText(text);
            }else{
                setText("Disabled");
            }
        }
    }


    @Override
    public void focusGained(FocusEvent e) {
        Hotkeys.unbindHotKey(action);
        
        if (Settings.getColorizeBody()) {
            setBackground(Colorizer.getSelectionColor());
            setForeground(Colorizer.getForegroundColor());
        } else {
            setBackground((Color) UIManager.get("List.selectionBackground"));
            setForeground((Color) UIManager.get("List.selectionForeground"));
        }
        
        getCaret().setSelectionVisible(false);
        getCaret().setVisible(false);
        
        printText();
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (Settings.getColorizeBody()) {
            setBackground(Colorizer.getTextfieldBackgroundColor());
            setForeground(Colorizer.getForegroundColor());
        } else {
            setBackground((Color) UIManager.get("TextArea.background"));
            setForeground((Color) UIManager.get("TextArea.foreground"));
        }
        printText();        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            key = KeyEvent.VK_UNDEFINED;
            modifiers = 0;
            setFocusable(false);
            setFocusable(true);
        }else{
            if(        e.getKeyCode() != KeyEvent.VK_SHIFT
                    && e.getKeyCode() != KeyEvent.VK_CONTROL
                    && e.getKeyCode() != KeyEvent.VK_META
                    && e.getKeyCode() != KeyEvent.VK_ALT
                    && e.getKeyCode() != KeyEvent.VK_ALT_GRAPH
                    && (e.getModifiers() != 0 || singleKeyEnabled)){
                key = e.getKeyCode();
                modifiers = e.getModifiers();
                setFocusable(false);
                setFocusable(true);
                printText();
            }
        }
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        e.consume();
    }
    
}
