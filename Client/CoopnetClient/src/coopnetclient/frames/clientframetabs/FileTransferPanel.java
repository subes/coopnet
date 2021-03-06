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
package coopnetclient.frames.clientframetabs;

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.enums.TransferStatuses;
import coopnetclient.frames.components.TransferStatusButtonComponent;
import coopnetclient.frames.interfaces.ClosableTab;
import coopnetclient.frames.models.TransferTableModel;
import coopnetclient.frames.popupmenus.TransferPopupMenu;
import coopnetclient.frames.renderers.TableTextCellRenderer;
import coopnetclient.frames.renderers.TransferProgressRenderer;
import coopnetclient.frames.renderers.TransferStatusRenderer;
import coopnetclient.frames.renderers.TransferTypeRenderer;
import coopnetclient.utils.Verification;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

public class FileTransferPanel extends javax.swing.JPanel implements ClosableTab {

    private TransferTableModel model;

    /** Creates new form TransferPanel */
    public FileTransferPanel() {
        initComponents();
    }

    public FileTransferPanel(TransferTableModel transferModel) {
        initComponents();
        this.model = transferModel;
        tbl_transfers.setModel(transferModel);
        //progress renderer
        TransferProgressRenderer renderer = new TransferProgressRenderer(tbl_transfers);
        tbl_transfers.setDefaultRenderer(Float.class, renderer);
        //type renderer
        TransferTypeRenderer renderer2 = new TransferTypeRenderer();
        tbl_transfers.setDefaultRenderer(Integer.class, renderer2);
        //status render
        TransferStatusRenderer renderer3 = new TransferStatusRenderer();
        tbl_transfers.setDefaultRenderer(TransferStatusButtonComponent.class, renderer3);
        //align center
        TableTextCellRenderer rend = new TableTextCellRenderer();
        rend.setHorizontalAlignment(SwingConstants.CENTER);
        tbl_transfers.setDefaultRenderer(String.class, rend);
        //set column widths
        tbl_transfers.getColumnModel().getColumn(0).setMinWidth(40);
        tbl_transfers.getColumnModel().getColumn(0).setMaxWidth(40);
        tbl_transfers.getColumnModel().getColumn(1).setMinWidth(80);
        tbl_transfers.getColumnModel().getColumn(1).setMaxWidth(80);
        tbl_transfers.getColumnModel().getColumn(2).setPreferredWidth(50);
        tbl_transfers.getColumnModel().getColumn(2).setMaxWidth(100);
        tbl_transfers.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_transfers.getColumnModel().getColumn(4).setMinWidth(70);
        tbl_transfers.getColumnModel().getColumn(4).setMaxWidth(70);
        tbl_transfers.getColumnModel().getColumn(5).setMinWidth(60);
        tbl_transfers.getColumnModel().getColumn(5).setMaxWidth(60);
        tbl_transfers.getColumnModel().getColumn(6).setMinWidth(70);
        tbl_transfers.getColumnModel().getColumn(6).setMaxWidth(70);

        tbl_transfers.setComponentPopupMenu(new TransferPopupMenu(tbl_transfers));
        JTableButtonMouseListener mlsitener = new JTableButtonMouseListener(tbl_transfers);
        tbl_transfers.addMouseListener(mlsitener);
        tbl_transfers.getTableHeader().setReorderingAllowed(false);

        cb_resume.setVisible(false);
        btn_browse.setVisible(false);
        tf_savePath.setVisible(false);
        lbl_saveto.setVisible(false);
    }

    public int getSelectedIndex(){
        return tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow());
    }

    public void rowUpdated(int row) {
        if (tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow()) == row) {
            UpdateDetails();
        }
    }

    private String getFileSize(long size) {
        int i = 0;
        while (size > 1024) {
            size = size / 1024;
            i++;
        }

        if (i == 0) {
            return size + " B";
        }
        if (i == 1) {
            return size + " kB";
        }
        if (i == 2) {
            return size + " MB";
        }
        if (i == 3) {
            return size + " GB";
        }
        return null;
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

        pnl_details = new javax.swing.JPanel();
        lbl_senderlabel = new javax.swing.JLabel();
        lbl_sendername = new javax.swing.JLabel();
        lbl_filenamelabel = new javax.swing.JLabel();
        lbl_filename = new javax.swing.JLabel();
        lbl_sizelabel = new javax.swing.JLabel();
        lbl_sizeValue = new javax.swing.JLabel();
        lbl_saveto = new javax.swing.JLabel();
        btn_browse = new javax.swing.JButton();
        cb_resume = new javax.swing.JCheckBox();
        tf_savePath = new coopnetclient.frames.components.ValidatorJTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_transfers = new javax.swing.JTable();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        pnl_details.setBorder(javax.swing.BorderFactory.createTitledBorder("Transfer details"));
        pnl_details.setFocusable(false);
        pnl_details.setLayout(new java.awt.GridBagLayout());

        lbl_senderlabel.setText("Peer:");
        lbl_senderlabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_details.add(lbl_senderlabel, gridBagConstraints);

        lbl_sendername.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnl_details.add(lbl_sendername, gridBagConstraints);

        lbl_filenamelabel.setText("Filename:");
        lbl_filenamelabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnl_details.add(lbl_filenamelabel, gridBagConstraints);

        lbl_filename.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnl_details.add(lbl_filename, gridBagConstraints);

        lbl_sizelabel.setText("Size:");
        lbl_sizelabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnl_details.add(lbl_sizelabel, gridBagConstraints);

        lbl_sizeValue.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnl_details.add(lbl_sizeValue, gridBagConstraints);

        lbl_saveto.setText("Save to:");
        lbl_saveto.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnl_details.add(lbl_saveto, gridBagConstraints);

        btn_browse.setMnemonic(KeyEvent.VK_B);
        btn_browse.setText("Browse");
        btn_browse.setFocusable(false);
        btn_browse.setNextFocusableComponent(cb_resume);
        btn_browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnl_details.add(btn_browse, gridBagConstraints);

        cb_resume.setMnemonic(KeyEvent.VK_R);
        cb_resume.setSelected(true);
        cb_resume.setText("Resume file");
        cb_resume.setFocusable(false);
        cb_resume.setNextFocusableComponent(tbl_transfers);
        cb_resume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_resumeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnl_details.add(cb_resume, gridBagConstraints);

        tf_savePath.setNextFocusableComponent(btn_browse);
        tf_savePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_savePathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnl_details.add(tf_savePath, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(pnl_details, gridBagConstraints);

        jScrollPane1.setFocusable(false);

        tbl_transfers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"recieve", "waiting", "user1", "file1.txt", "0%", "??:??:??", "0 KB/s"},
                {"send", "transferring", "user2", "file2.txt", "56%", "00:01:21", "500 KB/s"},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Type", "Status", "Peer", "Filename", "Progress", "Time left", "Speed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_transfers.setFillsViewportHeight(true);
        tbl_transfers.setNextFocusableComponent(tf_savePath);
        tbl_transfers.setRowHeight(22);
        tbl_transfers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_transfers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_transfersMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_transfers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseActionPerformed
        final int idx = tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow());
        if (idx >= 0) {
            new Thread() {

                @Override
                public void run() {
                    try {
                        File inputfile = null;
                        FileChooser fc = new FileChooser(FileChooser.DIRECTORIES_ONLY_MODE);
                        int returnVal = fc.choose(Globals.getLastOpenedDir());

                        if (returnVal == FileChooser.SELECT_ACTION) {
                            inputfile = fc.getSelectedFile();
                            if (inputfile != null) {
                                tf_savePath.setText(inputfile.getPath());
                                model.setSavePath(idx, tf_savePath.getText());
                            }
                        }
                    } catch (Exception e) {
                        ErrorHandler.handle(e);
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_btn_browseActionPerformed

    private void cb_resumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_resumeActionPerformed
        int idx = tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow());
        if (idx >= 0) {
            cb_resume.setSelected(model.setresume(idx, !cb_resume.isSelected()));
        } else {
            UpdateDetails();
        }
    }//GEN-LAST:event_cb_resumeActionPerformed

    private void tf_savePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_savePathActionPerformed
        int idx = tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow());
        if (idx >= 0) {
            if (!Verification.verifyDirectory(tf_savePath.getText())) {
                tf_savePath.showErrorMessage("Invalid directory!");
            } else {
                model.setSavePath(idx, tf_savePath.getText());
            }
        } else {
            UpdateDetails();
        }
}//GEN-LAST:event_tf_savePathActionPerformed

    private void tbl_transfersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_transfersMouseReleased
        UpdateDetails();
    }//GEN-LAST:event_tbl_transfersMouseReleased

    public void UpdateDetails() {
        int idx = tbl_transfers.convertRowIndexToModel(tbl_transfers.getSelectedRow());
        if (idx >= 0) {
            lbl_filename.setText(model.getValueAt(idx, 3).toString());
            lbl_sendername.setText(model.getValueAt(idx, 2).toString());
            lbl_sizeValue.setText(getFileSize(model.getFileSize(idx)));
            tf_savePath.setText(model.getSavePath(idx));
            cb_resume.setSelected(model.getResume(idx));
            int type = model.getTransferType(idx);
            if (type == TransferTableModel.RECIEVE_TYPE) {
                cb_resume.setVisible(true);
                btn_browse.setVisible(true);
                tf_savePath.setVisible(true);
                lbl_saveto.setVisible(true);
                TransferStatuses status = model.getTransferStatus(idx);
                switch (status) {
                    case Waiting:
                        cb_resume.setEnabled(model.destFileExists(idx));//if file already exists
                        btn_browse.setEnabled(true);
                        tf_savePath.setEnabled(true);
                        break;
                    default:
                        cb_resume.setEnabled(false);
                        btn_browse.setEnabled(false);
                        tf_savePath.setEnabled(false);
                        break;
                }
            } else {
                cb_resume.setVisible(false);
                btn_browse.setVisible(false);
                tf_savePath.setVisible(false);
                lbl_saveto.setVisible(false);
            }
        } else {
            cb_resume.setVisible(false);
            btn_browse.setVisible(false);
            tf_savePath.setVisible(false);
            lbl_saveto.setVisible(false);
            lbl_filename.setText("");
            lbl_sendername.setText("");
            lbl_sizeValue.setText("");
            tf_savePath.setText("");
        }
    }

    @Override
    public void closeTab() {
        TabOrganizer.closeTransferPanel();
    }

    private class JTableButtonMouseListener implements MouseListener {

        private JTable __table;

        private void __forwardEventToButton(MouseEvent e) {
            TableColumnModel columnModel = __table.getColumnModel();
            int column = columnModel.getColumnIndexAtX(e.getX());
            int row = e.getY() / __table.getRowHeight();
            if (column != 1) {
                return;
            }
            Object value;
            TransferStatusButtonComponent cell;
            MouseEvent buttonEvent;

            if (row >= __table.getRowCount() || row < 0 ||
                    column >= __table.getColumnCount() || column < 0) {
                return;
            }

            value = __table.getValueAt(row, column);
            if (!(value instanceof TransferStatusButtonComponent)) {
                return;
            }

            cell = (TransferStatusButtonComponent) value;
            Rectangle rect = __table.getCellRect(row, column, false);

            buttonEvent = new MouseEvent(cell, e.getID(),e.getWhen(),e.getModifiers()
                    , (int)(e.getX() - rect.getMinX())
                    , (int)(e.getY() - rect.getMinY())
                    ,e.getClickCount(),e.isPopupTrigger());
            cell.dispatchEvent(buttonEvent);
            // This is necessary so that when a button is pressed and released
            // it gets rendered properly.  Otherwise, the button may still appear
            // pressed down when it has been released.
            __table.repaint();
        }

        public JTableButtonMouseListener(JTable table) {
            __table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            __forwardEventToButton(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //__forwardEventToButton(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //__forwardEventToButton(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            __forwardEventToButton(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            __forwardEventToButton(e);
        }       
    }

    @Override
    public boolean isCurrentlyClosable() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_browse;
    private javax.swing.JCheckBox cb_resume;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_filename;
    private javax.swing.JLabel lbl_filenamelabel;
    private javax.swing.JLabel lbl_saveto;
    private javax.swing.JLabel lbl_senderlabel;
    private javax.swing.JLabel lbl_sendername;
    private javax.swing.JLabel lbl_sizeValue;
    private javax.swing.JLabel lbl_sizelabel;
    private javax.swing.JPanel pnl_details;
    private javax.swing.JTable tbl_transfers;
    private coopnetclient.frames.components.ValidatorJTextField tf_savePath;
    // End of variables declaration//GEN-END:variables
}
