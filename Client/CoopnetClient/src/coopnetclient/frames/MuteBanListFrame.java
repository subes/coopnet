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
package coopnetclient.frames;

import coopnetclient.Globals;
import coopnetclient.enums.MuteBanStatuses;
import coopnetclient.frames.renderers.TableTextCellRenderer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.MuteBanList;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class MuteBanListFrame extends javax.swing.JFrame {

    private static CustomeTableModel tablemodel;

    /** Creates new form MuteBanList */
    public MuteBanListFrame() {
        tablemodel = new CustomeTableModel();
        initComponents();
        tbl_UserTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tbl_UserTable.getColumnModel().getColumn(0).setPreferredWidth(1000);
        tbl_UserTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        SelectionListener listener = new SelectionListener(tbl_UserTable);
        tbl_UserTable.getSelectionModel().addListSelectionListener(listener);
        tbl_UserTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        tbl_UserTable.getColumnModel().getColumn(0).setCellRenderer(new TableTextCellRenderer());
        this.getRootPane().setDefaultButton(btn_close);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_close.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    public void updateTable() {
        tablemodel.update();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrl_table = new javax.swing.JScrollPane();
        tbl_UserTable = new javax.swing.JTable();
        btn_close = new javax.swing.JButton();
        btn_unMute = new javax.swing.JButton();
        btn_unBan = new javax.swing.JButton();
        btn_showProfile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mute/Ban List");
        setFocusable(false);
        setResizable(false);

        scrl_table.setFocusable(false);

        tbl_UserTable.setAutoCreateRowSorter(true);
        tbl_UserTable.setModel(tablemodel);
        tbl_UserTable.setFillsViewportHeight(true);
        tbl_UserTable.setNextFocusableComponent(btn_close);
        tbl_UserTable.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                tbl_UserTableCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        scrl_table.setViewportView(tbl_UserTable);

        btn_close.setText("Close");
        btn_close.setNextFocusableComponent(btn_unMute);
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        btn_unMute.setMnemonic(KeyEvent.VK_M);
        btn_unMute.setText("UnMute");
        btn_unMute.setEnabled(false);
        btn_unMute.setNextFocusableComponent(btn_unBan);
        btn_unMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_unMuteActionPerformed(evt);
            }
        });

        btn_unBan.setMnemonic(KeyEvent.VK_B);
        btn_unBan.setText("UnBan");
        btn_unBan.setEnabled(false);
        btn_unBan.setNextFocusableComponent(btn_showProfile);
        btn_unBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_unBanActionPerformed(evt);
            }
        });

        btn_showProfile.setMnemonic(KeyEvent.VK_P);
        btn_showProfile.setText("Show Profile");
        btn_showProfile.setEnabled(false);
        btn_showProfile.setNextFocusableComponent(tbl_UserTable);
        btn_showProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_showProfileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_close)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_unMute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_unBan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_showProfile)
                .addContainerGap(192, Short.MAX_VALUE))
            .addComponent(scrl_table, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrl_table, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_close)
                    .addComponent(btn_unMute)
                    .addComponent(btn_unBan)
                    .addComponent(btn_showProfile))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_close, btn_showProfile, btn_unBan, btn_unMute});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
        FrameOrganizer.closeMuteBanTableFrame();
}//GEN-LAST:event_btn_closeActionPerformed

    private void btn_unMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_unMuteActionPerformed
        int viewRow = tbl_UserTable.getSelectedRow();
        if (viewRow > -1) {
            int selected = tbl_UserTable.convertRowIndexToModel(viewRow);
            String subject = tablemodel.getValueAt(selected, 0).toString();
            Protocol.unMute(subject);
        }
}//GEN-LAST:event_btn_unMuteActionPerformed

    private void btn_unBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_unBanActionPerformed
        int viewRow = tbl_UserTable.getSelectedRow();
        if (viewRow > -1) {
            int selected = tbl_UserTable.convertRowIndexToModel(viewRow);
            String subject = tablemodel.getValueAt(selected, 0).toString();
            Protocol.unBan(subject);
        }
}//GEN-LAST:event_btn_unBanActionPerformed

    private void btn_showProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_showProfileActionPerformed
        int viewRow = tbl_UserTable.getSelectedRow();
        if (viewRow > -1) {
            int selected = tbl_UserTable.convertRowIndexToModel(viewRow);
            String subject = tablemodel.getValueAt(selected, 0).toString();
            Protocol.requestProfile(subject);
        }
}//GEN-LAST:event_btn_showProfileActionPerformed

    private void tbl_UserTableCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tbl_UserTableCaretPositionChanged
    }//GEN-LAST:event_tbl_UserTableCaretPositionChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_showProfile;
    private javax.swing.JButton btn_unBan;
    private javax.swing.JButton btn_unMute;
    private javax.swing.JScrollPane scrl_table;
    private javax.swing.JTable tbl_UserTable;
    // End of variables declaration//GEN-END:variables

    private class CustomeTableModel extends AbstractTableModel {

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Name" : "Status";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public int getRowCount() {
            return MuteBanList.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return MuteBanList.getElementAt(rowIndex);
            } else {
                return MuteBanList.getMuteBanStatus((MuteBanList.getElementAt(rowIndex)));
            }
        }

        public void update() {
            fireTableDataChanged();
        }
    }

    private class SelectionListener implements ListSelectionListener {

        private JTable table;

        SelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int viewRow = tbl_UserTable.getSelectedRow();
            if (viewRow > -1) {
                int selected = tbl_UserTable.convertRowIndexToModel(viewRow);
                String status = tablemodel.getValueAt(selected, 1).toString();
                if (status.equals(MuteBanStatuses.BANNED.toString())) {
                    btn_unBan.setEnabled(true);
                    btn_unMute.setEnabled(false);
                } else if (status.equals(MuteBanStatuses.MUTED.toString())) {
                    btn_unBan.setEnabled(false);
                    btn_unMute.setEnabled(true);
                } else {
                    btn_unBan.setEnabled(true);
                    btn_unMute.setEnabled(true);
                }

                btn_showProfile.setEnabled(true);
            } else {
                btn_unBan.setEnabled(false);
                btn_unMute.setEnabled(false);
                btn_showProfile.setEnabled(false);
            }
        }
    }
}
