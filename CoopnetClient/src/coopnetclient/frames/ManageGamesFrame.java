/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of Coopnet.

    Coopnet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Coopnet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.frames;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.launchers.WindowsLauncher;
import coopnetclient.modules.Colorizer;
import coopnetclient.modules.models.SortedListModel;
import coopnetclient.utils.filechooser.FileChooser;
import java.io.File;
import javax.swing.JOptionPane;

public class ManageGamesFrame extends javax.swing.JFrame {

    private SortedListModel channels = new SortedListModel();
    
    /** Creates new form ManageGamesFrame */
    public ManageGamesFrame() {
        initComponents();
        for (String st : GameDatabase.gameNamesAsStringArray()) {
            if (st.length() > 0) {
                channels.add(st);
            }
        }
        setLocationRelativeTo(null);
        //installpath only required on linux:
        if (Globals.os.equals("windows")) {
            tf_installPath.setVisible(false);
            lbl_installPath.setVisible(false);
            btn_browseInstallPath.setVisible(false);            
        }
        Colorizer.colorize(this);
        pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_games = new javax.swing.JLabel();
        scrl_games = new javax.swing.JScrollPane();
        lst_games = new javax.swing.JList();
        btn_save = new javax.swing.JButton();
        tf_path = new javax.swing.JTextField();
        lbl_path = new javax.swing.JLabel();
        btn_browsePath = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        lbl_noteText = new javax.swing.JLabel();
        lbl_installPath = new javax.swing.JLabel();
        tf_installPath = new javax.swing.JTextField();
        btn_browseInstallPath = new javax.swing.JButton();
        tf_filter = new javax.swing.JTextField();
        lbl_filter = new javax.swing.JLabel();
        lbl_note = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manage games");
        setResizable(false);

        lbl_games.setText("Games:");

        lst_games.setModel(channels);
        lst_games.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lst_gamesMousePressed(evt);
            }
        });
        scrl_games.setViewportView(lst_games);

        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        lbl_path.setText("Executable:");

        btn_browsePath.setText("Browse");
        btn_browsePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browsePathActionPerformed(evt);
            }
        });

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        lbl_noteText.setText("<html>DirectPlay games need a registry entry in order to be launched.<br> If you get a DirectPlay error while launching,<br> then please check that the registry entry is available.<br> Maybe run dxdiag.exe and look at the network tab.");

        lbl_installPath.setText("Install directory:");

        btn_browseInstallPath.setText("Browse");
        btn_browseInstallPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseInstallPathActionPerformed(evt);
            }
        });

        tf_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_filterActionPerformed(evt);
            }
        });

        lbl_filter.setText("Filter:");

        lbl_note.setText("<html><b>Note:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_filter, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                    .addComponent(lbl_games)
                    .addComponent(scrl_games, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_installPath)
                            .addComponent(lbl_path))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tf_path, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_browsePath))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tf_installPath, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_browseInstallPath))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_note)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_noteText))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_close)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_filter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_games)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_games, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_path)
                    .addComponent(btn_browsePath)
                    .addComponent(tf_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_installPath)
                    .addComponent(tf_installPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_browseInstallPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_note)
                    .addComponent(lbl_noteText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_close))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void lst_gamesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lst_gamesMousePressed
        if (lst_games.getSelectedValue() != null) {
            String path = Globals.launcher.getExecutablePath(lst_games.getSelectedValue().toString());
            tf_path.setText(path);
            String installpath = Globals.launcher.getInstallPath(lst_games.getSelectedValue().toString());
            tf_installPath.setText(installpath);
        }
}//GEN-LAST:event_lst_gamesMousePressed

    private void btn_browsePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browsePathActionPerformed
        if (lst_games.getSelectedValue() != null) {
            new Thread() {

                @Override
                public void run() {
                    FileChooser mfc = new FileChooser(FileChooser.FILES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.lastOpenedDir);

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        File file = mfc.getSelectedFile();
                        tf_path.setText(file.getAbsolutePath());
                    }//else cancelled

                }
            }.start();
        }
}//GEN-LAST:event_btn_browsePathActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        if (Globals.launcher instanceof WindowsLauncher) {
            if (lst_games.getSelectedValue() != null && tf_path.getText().length() > 0) {
                GameDatabase.setLocalExecutablePath(lst_games.getSelectedValue().toString(), tf_path.getText());
                GameDatabase.setLocalInstallPath(lst_games.getSelectedValue().toString(), tf_installPath.getText());
                Globals.clientFrame.setLaunchable(lst_games.getSelectedValue().toString(), true);
                GameDatabase.saveLocalPaths();
            } else {
                JOptionPane.showMessageDialog(null, "Please set the path correctly!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (lst_games.getSelectedValue() != null && tf_path.getText().length() > 0 && tf_installPath.getText().length() > 0) {
                GameDatabase.setLocalExecutablePath(lst_games.getSelectedValue().toString(), tf_path.getText());
                GameDatabase.setLocalInstallPath(lst_games.getSelectedValue().toString(), tf_installPath.getText());
                Globals.clientFrame.setLaunchable(lst_games.getSelectedValue().toString(), true);
                GameDatabase.saveLocalPaths();
            } else {
                JOptionPane.showMessageDialog(null, "Please set both paths correctly!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


}//GEN-LAST:event_btn_saveActionPerformed

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
        this.setVisible(false);
        dispose();
}//GEN-LAST:event_btn_closeActionPerformed

    private void btn_browseInstallPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseInstallPathActionPerformed
        if (lst_games.getSelectedValue() != null) {
            new Thread() {

                @Override
                public void run() {
                    FileChooser mfc = new FileChooser(FileChooser.DIRECTORIES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.lastOpenedDir);

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        File file = mfc.getSelectedFile();
                        tf_installPath.setText(file.getAbsolutePath());
                    }//else cancelled

                }
            }.start();
        }
}//GEN-LAST:event_btn_browseInstallPathActionPerformed

private void tf_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_filterActionPerformed
    lst_games.removeAll();
    channels.clear();
    String filter = tf_filter.getText();
    for (String st : GameDatabase.gameNamesAsStringArray()) {
        if (st.toLowerCase().contains(filter.toLowerCase())) {
            channels.add(st);
        }
    }
    this.repaint();
}//GEN-LAST:event_tf_filterActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_browseInstallPath;
    private javax.swing.JButton btn_browsePath;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_save;
    private javax.swing.JLabel lbl_filter;
    private javax.swing.JLabel lbl_games;
    private javax.swing.JLabel lbl_installPath;
    private javax.swing.JLabel lbl_note;
    private javax.swing.JLabel lbl_noteText;
    private javax.swing.JLabel lbl_path;
    private javax.swing.JList lst_games;
    private javax.swing.JScrollPane scrl_games;
    private javax.swing.JTextField tf_filter;
    private javax.swing.JTextField tf_installPath;
    private javax.swing.JTextField tf_path;
    // End of variables declaration//GEN-END:variables

}

