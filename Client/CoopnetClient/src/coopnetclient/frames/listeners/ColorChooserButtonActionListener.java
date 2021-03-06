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

package coopnetclient.frames.listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

public class ColorChooserButtonActionListener implements ActionListener {

    JButton parent;
    JDialog dialog;
    JColorChooser chooser;

    public ColorChooserButtonActionListener(JButton parent) {
        super();
        this.parent = parent;

        //Set text
        Color fg = parent.getForeground();
        parent.setText(fg.getRed() + "." + fg.getGreen() + "." + fg.getBlue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        chooser = new JColorChooser();
        
        ActionListener okListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color clr = chooser.getColor();
                parent.setForeground(clr);
                parent.setText(clr.getRed() + "." + clr.getGreen() + "." + clr.getBlue());
                dialog.dispose();
            }
        };

        ActionListener cancelListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };
        
        dialog = JColorChooser.createDialog(null, "Pick a color", true, chooser, okListener, cancelListener);
        coopnetclient.utils.ui.Colorizer.colorize(dialog);
        chooser.setColor(parent.getForeground());
        
        dialog.setVisible(true);
    }
}
