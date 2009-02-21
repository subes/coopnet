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
import coopnetclient.frames.renderers.ChannelListFavouriteCellRenderer;
import coopnetclient.frames.renderers.ChannelListInstalledCellRenderer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.settings.Favourites;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ChannelListFrame extends javax.swing.JFrame {

    private DefaultTableModel model;

    /** Creates new form ChannelListFrame */
    public ChannelListFrame() {
        initComponents();

        model = (DefaultTableModel) tbl_list.getModel();

        initData(true);

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0 && e.getFirstRow() == e.getLastRow()){
                    String changedFav = (String) model.getValueAt(e.getFirstRow(), 1);
                    if((Boolean)model.getValueAt(e.getFirstRow(), 0)){
                        Favourites.addFavouriteByName(changedFav);
                    }else{
                        Favourites.removeFavourite(GameDatabase.getIDofGame(changedFav));
                    }
                }
            }
        });

        tbl_list.getColumnModel().getColumn(1).setPreferredWidth(tbl_list.getWidth());

        DefaultCellEditor editor = new DefaultCellEditor(new JCheckBox()){

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                row = tbl_list.convertRowIndexToModel(row);
                column = tbl_list.convertColumnIndexToModel(column);
                model.setValueAt(!((Boolean)value), row, column);
                model.fireTableCellUpdated(row, column);
                return null;
            }


        };

        tbl_list.getColumnModel().getColumn(0).setCellEditor(editor);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_cancel.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");

        tf_filter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                initData(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                initData(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void initData(boolean resetFilter){
        int rows = model.getRowCount();
        for(int i = 0; i < rows; i++){
            model.removeRow(0);
        }

        Vector<String> favs = Favourites.getFavouritesByName();
        for (String gameName : GameDatabase.getAllGameNamesAsStringArray()) {
            if (gameName.length() > 0) {
                if (resetFilter || gameName.toLowerCase().contains(tf_filter.getText().toLowerCase())) {
                    Object[] rowData = {favs.contains(gameName), gameName, GameDatabase.getInstalledGameNames().contains(gameName)};
                    model.addRow(rowData);
                }
            }
        }

        if(resetFilter){
            tf_filter.setText("");
            ArrayList<SortKey> keys = new ArrayList<SortKey>();
            keys.add(new SortKey(0, SortOrder.DESCENDING));
            keys.add(new SortKey(2, SortOrder.DESCENDING));
            keys.add(new SortKey(1, SortOrder.ASCENDING));
            tbl_list.getRowSorter().setSortKeys(keys);
            ((TableRowSorter)tbl_list.getRowSorter()).sort();
            tbl_list.clearSelection();
            btn_joinChannelButton.setEnabled(tbl_list.getSelectedRow() > -1);
        }

        tbl_list.getColumnModel().getColumn(1).sizeWidthToFit();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_filter = new javax.swing.JLabel();
        tf_filter = new javax.swing.JTextField();
        btn_joinChannelButton = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        scrl_list = new javax.swing.JScrollPane();
        tbl_list = new javax.swing.JTable();
        btn_reset = new javax.swing.JButton();

        setTitle("Join channel");
        setFocusable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lbl_filter.setDisplayedMnemonic(KeyEvent.VK_F);
        lbl_filter.setLabelFor(tf_filter);
        lbl_filter.setText("Filter:");
        lbl_filter.setFocusable(false);

        btn_joinChannelButton.setText("Join selected channel");
        btn_joinChannelButton.setEnabled(false);
        btn_joinChannelButton.setNextFocusableComponent(btn_cancel);
        btn_joinChannelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_joinChannelButtonActionPerformed(evt);
            }
        });

        btn_cancel.setText("Close");
        btn_cancel.setNextFocusableComponent(tf_filter);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        tbl_list.setAutoCreateRowSorter(true);
        tbl_list.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "Welcome", null},
                {null, "FOT", null}
            },
            new String [] {
                "Favourite", "Name", "Installed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_list.getTableHeader().setReorderingAllowed(false);
        tbl_list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_listMouseClicked(evt);
            }
        });
        scrl_list.setViewportView(tbl_list);
        tbl_list.getColumnModel().getColumn(0).setCellRenderer(new ChannelListFavouriteCellRenderer());
        tbl_list.getColumnModel().getColumn(2).setCellRenderer(new ChannelListInstalledCellRenderer());

        btn_reset.setText("Reset");
        btn_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_resetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrl_list, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btn_joinChannelButton)
                        .addGap(10, 10, 10)
                        .addComponent(btn_cancel))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lbl_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_filter, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_reset)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_filter)
                    .addComponent(btn_reset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_list, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel)
                    .addComponent(btn_joinChannelButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_joinChannelButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_joinChannelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_joinChannelButtonActionPerformed
        Protocol.joinChannel((String) model.getValueAt(tbl_list.convertRowIndexToModel(tbl_list.getSelectedRow()), 1));
        FrameOrganizer.closeChannelListFrame();
}//GEN-LAST:event_btn_joinChannelButtonActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        FrameOrganizer.closeChannelListFrame();
}//GEN-LAST:event_btn_cancelActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    FrameOrganizer.closeChannelListFrame();
}//GEN-LAST:event_formWindowClosing

private void tbl_listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_listMouseClicked
    btn_joinChannelButton.setEnabled(tbl_list.getSelectedRow() > -1);
    
    if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2){
        btn_joinChannelButton.doClick();
    }
}//GEN-LAST:event_tbl_listMouseClicked

private void btn_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_resetActionPerformed
    initData(true);
    tf_filter.requestFocus();
}//GEN-LAST:event_btn_resetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_joinChannelButton;
    private javax.swing.JButton btn_reset;
    private javax.swing.JLabel lbl_filter;
    private javax.swing.JScrollPane scrl_list;
    private javax.swing.JTable tbl_list;
    private javax.swing.JTextField tf_filter;
    // End of variables declaration//GEN-END:variables

}
