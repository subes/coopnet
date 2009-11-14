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


package coopnetclient.frames.renderers;

import coopnetclient.frames.models.RoomTableModel;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.settings.Settings;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class RoomNameRenderer extends javax.swing.JPanel implements TableCellRenderer {

    RoomTableModel model;
    
    public RoomNameRenderer(RoomTableModel model) {
        initComponents();
        this.model = model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbl_roomName = new javax.swing.JLabel();
        lbl_modName = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        lbl_roomName.setFont(lbl_roomName.getFont().deriveFont(lbl_roomName.getFont().getStyle() & ~java.awt.Font.BOLD));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lbl_roomName, gridBagConstraints);

        lbl_modName.setFont(lbl_modName.getFont().deriveFont(lbl_modName.getFont().getStyle() & ~java.awt.Font.BOLD));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(lbl_modName, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbl_modName;
    private javax.swing.JLabel lbl_roomName;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        lbl_roomName.setText(value.toString());
        String mod = model.getModName(table.convertRowIndexToModel(row)); 
        if(mod!= null && mod.length() > 0){
            lbl_modName.setVisible(true);
            lbl_modName.setText(mod);
            lbl_modName.setIcon(Icons.modIcon);
            setToolTipText("<html><xmp>" + value + "</xmp><br> Mod: "+mod);
        }else{
            setToolTipText("<html><xmp>" + value + "</xmp>");
            lbl_modName.setVisible(false);
        }
        setOpaque(true);
        if (Settings.getColorizeBody()) {
            setForeground(Settings.getForegroundColor());
            if (isSelected) {
                setBackground(Settings.getSelectionColor());
            } else {
                setBackground(Settings.getBackgroundColor());
            }
        } else {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                if (row % 2 == 1) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(table.getBackground());
                }
            }
        }

        return this;
    }

}