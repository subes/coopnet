/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.coloring;

import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicArrowButton;

public class CustomScrollBarArrowButton extends BasicArrowButton {

    public CustomScrollBarArrowButton(int direction) {
        super(direction,
                coopnetclient.coloring.Colorizer.getButtonBackgroundColor(),
                coopnetclient.coloring.Colorizer.getButtonBackgroundColor(),
                coopnetclient.coloring.Colorizer.getForegroundColor(),
                coopnetclient.coloring.Colorizer.getButtonBackgroundColor());

        setBorder(BorderFactory.createLineBorder(coopnetclient.coloring.Colorizer.getForegroundColor(), 1));
    }

    public void customCodeForColorizer() {
        //Disabling hover effect by this
        setForeground(coopnetclient.coloring.Colorizer.getButtonBackgroundColor());
    }
}
