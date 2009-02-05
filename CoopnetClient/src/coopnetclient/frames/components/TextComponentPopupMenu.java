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

package coopnetclient.frames.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class TextComponentPopupMenu extends JPopupMenu implements ActionListener {

    private JTextComponent parent;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;

    public TextComponentPopupMenu(JTextComponent parent){
        super();
        this.parent = parent;

        cut = new JMenuItem("Cut");
        cut.addActionListener(this);
        copy = new JMenuItem("Copy");
        copy.addActionListener(this);
        paste = new JMenuItem("Paste");
        paste.addActionListener(this);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);

        if(parent.getSelectedText() != null && parent.getSelectedText().length() > 0){
            cut.setEnabled(parent.isEditable());
            copy.setEnabled(true);
        }else{
            cut.setEnabled(false);
            copy.setEnabled(false);
        }
        
        paste.setEnabled(parent.isEditable());
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        System.out.println("VISIBLE "+b);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cut){
            parent.cut();
        }else if(e.getSource() == copy){
            parent.copy();
        }else if(e.getSource() == paste){
            parent.paste();
        }
    }

}
