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

import coopnetclient.Globals;
import coopnetclient.enums.TransferStatuses;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.models.TransferTableModel;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.settings.Settings;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TransferStatusButtonComponent extends javax.swing.JPanel  {

    /** Creates new form TransferStatusButtonRenderer */
    public TransferStatusButtonComponent() {
        initComponents();
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

        btn_accept = new javax.swing.JButton();
        btn_refuse = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        lbl_text = new javax.swing.JLabel();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        btn_accept.setIcon(Icons.acceptIcon);
        btn_accept.setMaximumSize(new java.awt.Dimension(20, 20));
        btn_accept.setMinimumSize(new java.awt.Dimension(20, 20));
        btn_accept.setPreferredSize(new java.awt.Dimension(20, 20));
        btn_accept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_acceptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(btn_accept, gridBagConstraints);

        btn_refuse.setIcon(Icons.refuseIcon);
        btn_refuse.setMaximumSize(new java.awt.Dimension(20, 20));
        btn_refuse.setMinimumSize(new java.awt.Dimension(20, 20));
        btn_refuse.setPreferredSize(new java.awt.Dimension(20, 20));
        btn_refuse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refuseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(btn_refuse, gridBagConstraints);

        btn_cancel.setIcon(Icons.cancelIcon);
        btn_cancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_cancel.setMaximumSize(new java.awt.Dimension(20, 20));
        btn_cancel.setMinimumSize(new java.awt.Dimension(20, 20));
        btn_cancel.setPreferredSize(new java.awt.Dimension(20, 20));
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(btn_cancel, gridBagConstraints);

        lbl_text.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_text.setText("text");
        lbl_text.setMaximumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(lbl_text, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_acceptActionPerformed
        Globals.getTransferModel().acceptFile(TabOrganizer.getTransferPanel().getSelectedIndex());
    }//GEN-LAST:event_btn_acceptActionPerformed

    private void btn_refuseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refuseActionPerformed
        Globals.getTransferModel().refuseFile(TabOrganizer.getTransferPanel().getSelectedIndex());
    }//GEN-LAST:event_btn_refuseActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        Globals.getTransferModel().cancel(TabOrganizer.getTransferPanel().getSelectedIndex());
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        for (Component comp : this.getComponents()) {
            if (comp.isVisible() && comp.getBounds().contains(evt.getPoint())) {
                for (MouseListener ml : comp.getMouseListeners()) {
                    ml.mousePressed(SwingUtilities.convertMouseEvent((Component) evt.getSource(), evt, comp));
                }
            }
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        for (Component comp : this.getComponents()) {
            if (comp.isVisible() && comp.getBounds().contains(evt.getPoint())) {
                for (MouseListener ml : comp.getMouseListeners()) {
                    ml.mouseReleased(SwingUtilities.convertMouseEvent((Component) evt.getSource(), evt, comp));
                }
            }
        }
    }//GEN-LAST:event_formMouseReleased

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        for (Component comp : this.getComponents()) {
            if (comp.isVisible() && comp.getBounds().contains(evt.getPoint())) {
                for (MouseListener ml : comp.getMouseListeners()) {
                    ml.mouseClicked(SwingUtilities.convertMouseEvent((Component) evt.getSource(), evt, comp));
                }
            }
        }
    }//GEN-LAST:event_formMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_accept;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_refuse;
    private javax.swing.JLabel lbl_text;
    // End of variables declaration//GEN-END:variables

    public Component prepare(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int idx = row;
        TransferStatuses status = Globals.getTransferModel().getTransferStatus(idx);
        lbl_text.setText(status.toString());
        int type = Globals.getTransferModel().getTransferType(idx);
        switch (status) {
            case Waiting:
                if (type == TransferTableModel.RECIEVE_TYPE) {
                    btn_accept.setVisible(true);
                    btn_refuse.setVisible(true);
                    btn_cancel.setVisible(false);
                } else {
                    btn_accept.setVisible(false);
                    btn_refuse.setVisible(false);
                    btn_cancel.setVisible(true);
                }
                break;
            case Starting:
            case Transferring:
            case Retrying:
                btn_accept.setVisible(false);
                btn_refuse.setVisible(false);
                btn_cancel.setVisible(true);
                break;
            default:
                btn_accept.setVisible(false);
                btn_refuse.setVisible(false);
                btn_cancel.setVisible(false);
                break;
        }
        return this;
    }

    
}
