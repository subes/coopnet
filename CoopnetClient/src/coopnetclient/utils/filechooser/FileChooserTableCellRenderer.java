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

package coopnetclient.utils.filechooser;

import coopnetclient.Globals;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class FileChooserTableCellRenderer extends DefaultTableCellRenderer {

    private static ImageIcon dirIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private static ImageIcon dirIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private static ImageIcon fileIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    private static ImageIcon fileIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    
    public FileChooserTableCellRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        setBorder(null);
        //set icon:
        setIcon(null);
        if (vColIndex == 0) {
            boolean isHidden = ((FileChooserTableModel)table.getModel()).fileIsHidden(rowIndex);
            
            if(value.equals("..") || value.equals(".") ||table.getModel().getValueAt(rowIndex, vColIndex+1).equals("dir")){
                if(isHidden){
                    setIcon(dirIconHidden);
                }else{
                    setIcon(dirIcon);
                }
            } else {
                if(isHidden){
                    setIcon(fileIconHidden);
                }else{
                    setIcon(fileIcon);
                }
            }
        }
        // Configure the component with the specified value
        setText(value.toString());
        setToolTipText(value.toString());
        // Since the renderer is a component, return itself
        return this;
    }
    // The following methods override the defaults for performance reasons
    @Override
    public void validate() {
    }

    @Override
    public void revalidate() {
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}
