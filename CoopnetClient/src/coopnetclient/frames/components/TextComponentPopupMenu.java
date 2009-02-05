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
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class TextComponentPopupMenu extends JPopupMenu implements ActionListener {

    protected JTextComponent parent;

    protected JMenuItem selectAll;
    protected JMenuItem cut;
    protected JMenuItem copy;
    protected JMenuItem paste;
    protected JMenuItem delete;

    public TextComponentPopupMenu(JTextComponent parent){
        super();
        this.parent = parent;

        selectAll = new JMenuItem("Select All");
        selectAll.addActionListener(this);
        cut = new JMenuItem("Cut");
        cut.addActionListener(this);
        copy = new JMenuItem("Copy");
        copy.addActionListener(this);
        paste = new JMenuItem("Paste");
        paste.addActionListener(this);
        delete = new JMenuItem("Delete");
        delete.addActionListener(this);

        this.add(cut);
        this.add(copy);
        this.add(paste);
        this.add(delete);
        this.addSeparator();
        this.add(selectAll);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);

        if(parent.getSelectedText() != null && parent.getSelectedText().length() > 0){
            if(parent instanceof JPasswordField){
                cut.setEnabled(false);
                copy.setEnabled(false);
            }else{
                cut.setEnabled(parent.isEditable());
                copy.setEnabled(true);
            }
            delete.setEnabled(parent.isEditable());
        }else{
            cut.setEnabled(false);
            copy.setEnabled(false);
            delete.setEnabled(false);
        }

        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
        paste.setEnabled(parent.isEditable() && contents.isDataFlavorSupported(DataFlavor.stringFlavor));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cut){
            parent.cut();
        }else if(e.getSource() == copy){
            parent.copy();
        }else if(e.getSource() == paste){
            parent.paste();
        }else if(e.getSource() == delete){
            parent.replaceSelection(null);
        }else if(e.getSource() == selectAll){
            parent.selectAll();
        }
    }

}
