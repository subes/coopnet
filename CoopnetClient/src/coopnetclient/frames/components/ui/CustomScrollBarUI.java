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

package coopnetclient.frames.components.ui;

import coopnetclient.frames.components.CustomScrollBarArrowButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollBarUI extends BasicScrollBarUI {
    
    Color btnbg = coopnetclient.utils.Colorizer.getButtonBackgroundColor(),
          bg = coopnetclient.utils.Colorizer.getTextfieldBackgroundColor(), 
          fg = coopnetclient.utils.Colorizer.getForegroundColor();
    
// this draws scroller
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(btnbg);
        g.fillRect((int)trackBounds.getX(),(int)trackBounds.getY(),
                (int)trackBounds.getWidth(),(int)trackBounds.getHeight());
        g.setColor(fg);
        g.drawRect((int)trackBounds.getX(),(int)trackBounds.getY(),
                (int)trackBounds.getWidth()-1,(int)trackBounds.getHeight()-1);
    }
    
// this draws scroller background
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(bg);
        g.fillRect((int)trackBounds.getX(),(int)trackBounds.getY(),
                (int)trackBounds.getWidth(),(int)trackBounds.getHeight());
    }
    
// and methods creating scrollbar buttons
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new CustomScrollBarArrowButton(orientation);
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new CustomScrollBarArrowButton(orientation);
    }
    
}
