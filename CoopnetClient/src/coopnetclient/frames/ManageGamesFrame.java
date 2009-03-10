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

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.clientframetabs.ChannelPanel;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.frames.models.SortedListModel;
import coopnetclient.threads.ErrThread;
import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.ui.Icons;
import coopnetclient.utils.Verification;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ManageGamesFrame extends javax.swing.JFrame {

    private SortedListModel channels = new SortedListModel();
    private HashMap<String, String> tempExePath = new HashMap<String, String>();
    private HashMap<String, String> tempInstallPath = new HashMap<String, String>();
    private HashMap<String, String> tempParams = new HashMap<String, String>();

    /** Creates new form ManageGamesFrame */
    public ManageGamesFrame() {
        initComponents();
        for (String st : GameDatabase.getNonDPlayGameNames()) {
            if (st.length() > 0) {
                channels.add(st);
            }
        }

        if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS) {
            cmb_winEnv.setEnabled(false);
        }
        cmb_winEnv.setSelectedItem(Globals.getWineCommand());
        cb_dosboxFullscreen.setSelected(Settings.getDOSBoxFullscreen());
        tf_dosboxExe.setText(Settings.getDOSBoxExecutable());

        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_cancel.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        pack();

        tf_filter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    /**
    saves any unsaved temporary data and returns if it was succesfull
     */
    private boolean saveTempData() {
        boolean error = false;
        if (lst_games.getSelectedValue() != null) {
            if (tf_exePath.getText().length() > 0) {
                if (Verification.verifyFile(tf_exePath.getText())) {
                    tempExePath.put(lst_games.getSelectedValue().toString(), tf_exePath.getText());
                } else {
                    tf_exePath.showErrorMessage("Please set the path correctly!");
                    error = true;
                }
            } else {
                tempExePath.put(lst_games.getSelectedValue().toString(), tf_exePath.getText());
            }
            if (tf_installPath.getText().length() > 0) {
                if (Verification.verifyDirectory(tf_installPath.getText())) {
                    tempInstallPath.put(lst_games.getSelectedValue().toString(), tf_installPath.getText());
                } else {
                    tf_installPath.showErrorMessage("Please set the path correctly!");
                    error = true;
                }
            } else {
                tempInstallPath.put(lst_games.getSelectedValue().toString(), tf_installPath.getText());
            }

            if (tf_parameters.getText().length() > 0) {
                tempParams.put(lst_games.getSelectedValue().toString(), tf_parameters.getText());
            } else {
                tempParams.put(lst_games.getSelectedValue().toString(), tf_parameters.getText());
            }
        }

        return !error;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnl_games = new javax.swing.JPanel();
        scrl_games = new javax.swing.JScrollPane();
        lst_games = new javax.swing.JList();
        tf_filter = new javax.swing.JTextField();
        lbl_filter = new javax.swing.JLabel();
        cb_showInstalledOnly = new javax.swing.JCheckBox();
        pnl_settings = new javax.swing.JPanel();
        lbl_path = new javax.swing.JLabel();
        tf_exePath = new coopnetclient.frames.components.ValidatorJTextField();
        btn_browsePath = new javax.swing.JButton();
        lbl_installPath = new javax.swing.JLabel();
        tf_installPath = new coopnetclient.frames.components.ValidatorJTextField();
        btn_browseInstallPath = new javax.swing.JButton();
        lbl_params = new javax.swing.JLabel();
        tf_parameters = new javax.swing.JTextField();
        lbl_note = new javax.swing.JLabel();
        lbl_noteText = new javax.swing.JLabel();
        btn_revert = new javax.swing.JButton();
        lbl_installDirIcon = new javax.swing.JLabel();
        lbl_exePathIcon = new javax.swing.JLabel();
        pnl_environment = new javax.swing.JPanel();
        pnl_windowsenvironment = new javax.swing.JPanel();
        lbl_dplayEnv = new javax.swing.JLabel();
        cmb_winEnv = new javax.swing.JComboBox();
        lbl_dplayEnvNote = new javax.swing.JLabel();
        pnl_dosbox = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tf_dosboxExe = new javax.swing.JTextField();
        btn_browseDosboxExecutable = new javax.swing.JButton();
        cb_dosboxFullscreen = new javax.swing.JCheckBox();
        btn_save = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();

        setTitle("Manage games");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lst_games.setModel(channels);
        lst_games.setNextFocusableComponent(tf_exePath);
        lst_games.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lst_gamesValueChanged(evt);
            }
        });
        scrl_games.setViewportView(lst_games);

        tf_filter.setNextFocusableComponent(cb_showInstalledOnly);

        lbl_filter.setDisplayedMnemonic(KeyEvent.VK_F);
        lbl_filter.setLabelFor(tf_filter);
        lbl_filter.setText("Filter:");

        cb_showInstalledOnly.setMnemonic(KeyEvent.VK_H);
        cb_showInstalledOnly.setText("Show installed games only");
        cb_showInstalledOnly.setNextFocusableComponent(lst_games);
        cb_showInstalledOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_showInstalledOnlyActionPerformed(evt);
            }
        });

        pnl_settings.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit Game Settings"));
        pnl_settings.setLayout(new java.awt.GridBagLayout());

        lbl_path.setDisplayedMnemonic(KeyEvent.VK_E);
        lbl_path.setLabelFor(tf_exePath);
        lbl_path.setText("Executable:");
        lbl_path.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(lbl_path, gridBagConstraints);

        tf_exePath.setEnabled(false);
        tf_exePath.setNextFocusableComponent(tf_installPath);
        tf_exePath.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_exePathCaretUpdate(evt);
            }
        });
        tf_exePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_exePathActionPerformed(evt);
            }
        });
        tf_exePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_exePathFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(tf_exePath, gridBagConstraints);

        btn_browsePath.setText("Browse");
        btn_browsePath.setEnabled(false);
        btn_browsePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browsePathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(btn_browsePath, gridBagConstraints);

        lbl_installPath.setDisplayedMnemonic(KeyEvent.VK_I);
        lbl_installPath.setLabelFor(tf_installPath);
        lbl_installPath.setText("Install Directory:");
        lbl_installPath.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(lbl_installPath, gridBagConstraints);

        tf_installPath.setEnabled(false);
        tf_installPath.setNextFocusableComponent(btn_save);
        tf_installPath.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_installPathCaretUpdate(evt);
            }
        });
        tf_installPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_installPathActionPerformed(evt);
            }
        });
        tf_installPath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_installPathFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(tf_installPath, gridBagConstraints);

        btn_browseInstallPath.setText("Browse");
        btn_browseInstallPath.setEnabled(false);
        btn_browseInstallPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseInstallPathActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(btn_browseInstallPath, gridBagConstraints);

        lbl_params.setDisplayedMnemonic(KeyEvent.VK_A);
        lbl_params.setLabelFor(tf_parameters);
        lbl_params.setText("Additional Parameters:");
        lbl_params.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(lbl_params, gridBagConstraints);

        tf_parameters.setEnabled(false);
        tf_parameters.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_parametersCaretUpdate(evt);
            }
        });
        tf_parameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_parametersActionPerformed(evt);
            }
        });
        tf_parameters.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_parametersFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(tf_parameters, gridBagConstraints);

        lbl_note.setText("<html><b>Note:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(lbl_note, gridBagConstraints);

        lbl_noteText.setText("<html>DirectPlay games must be properly installed in order to be launched.<br>Run dxdiag.exe and look at the network tab to find out!<br>If the game is installed properly <br> it should be present in the middle list with status \"OK\".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_settings.add(lbl_noteText, gridBagConstraints);

        btn_revert.setMnemonic(KeyEvent.VK_R);
        btn_revert.setText("Revert");
        btn_revert.setEnabled(false);
        btn_revert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_revertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        pnl_settings.add(btn_revert, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnl_settings.add(lbl_installDirIcon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnl_settings.add(lbl_exePathIcon, gridBagConstraints);

        javax.swing.GroupLayout pnl_gamesLayout = new javax.swing.GroupLayout(pnl_games);
        pnl_games.setLayout(pnl_gamesLayout);
        pnl_gamesLayout.setHorizontalGroup(
            pnl_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_gamesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_gamesLayout.createSequentialGroup()
                        .addComponent(lbl_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_filter, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_showInstalledOnly))
                    .addComponent(scrl_games, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(pnl_settings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_gamesLayout.setVerticalGroup(
            pnl_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_gamesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_filter)
                    .addComponent(cb_showInstalledOnly))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_games, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_settings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
        );

        jTabbedPane1.addTab("Games", pnl_games);

        pnl_windowsenvironment.setBorder(javax.swing.BorderFactory.createTitledBorder("Windows enviromnent (Linux only)"));

        lbl_dplayEnv.setText("Windows environment:");

        cmb_winEnv.setEditable(true);
        cmb_winEnv.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "wine", "cedega" }));
        cmb_winEnv.setSelectedItem(Settings.getWineCommand());

        lbl_dplayEnvNote.setText("<html><table><tr><td><b>Note:</b></td><td>Changes to the Windows enviroment take effect after restarting Coopnet.");

        javax.swing.GroupLayout pnl_windowsenvironmentLayout = new javax.swing.GroupLayout(pnl_windowsenvironment);
        pnl_windowsenvironment.setLayout(pnl_windowsenvironmentLayout);
        pnl_windowsenvironmentLayout.setHorizontalGroup(
            pnl_windowsenvironmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_windowsenvironmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_windowsenvironmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_windowsenvironmentLayout.createSequentialGroup()
                        .addComponent(lbl_dplayEnv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_winEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_dplayEnvNote))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        pnl_windowsenvironmentLayout.setVerticalGroup(
            pnl_windowsenvironmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_windowsenvironmentLayout.createSequentialGroup()
                .addGroup(pnl_windowsenvironmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_dplayEnv)
                    .addComponent(cmb_winEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_dplayEnvNote)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_dosbox.setBorder(javax.swing.BorderFactory.createTitledBorder("DOSBox settings"));

        jLabel1.setText("DOSBox executable:");

        btn_browseDosboxExecutable.setText("Browse");
        btn_browseDosboxExecutable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseDosboxExecutableActionPerformed(evt);
            }
        });

        cb_dosboxFullscreen.setText("Fullscreen");

        javax.swing.GroupLayout pnl_dosboxLayout = new javax.swing.GroupLayout(pnl_dosbox);
        pnl_dosbox.setLayout(pnl_dosboxLayout);
        pnl_dosboxLayout.setHorizontalGroup(
            pnl_dosboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_dosboxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dosboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_dosboxLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_dosboxExe, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_browseDosboxExecutable))
                    .addComponent(cb_dosboxFullscreen))
                .addContainerGap())
        );
        pnl_dosboxLayout.setVerticalGroup(
            pnl_dosboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_dosboxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_dosboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tf_dosboxExe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_browseDosboxExecutable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cb_dosboxFullscreen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_environmentLayout = new javax.swing.GroupLayout(pnl_environment);
        pnl_environment.setLayout(pnl_environmentLayout);
        pnl_environmentLayout.setHorizontalGroup(
            pnl_environmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_environmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_environmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_dosbox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_windowsenvironment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_environmentLayout.setVerticalGroup(
            pnl_environmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_environmentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_dosbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnl_windowsenvironment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Environment", pnl_environment);

        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.setNextFocusableComponent(tf_filter);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel)
                .addContainerGap(529, Short.MAX_VALUE))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_cancel, btn_save});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_cancel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_save});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_browsePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browsePathActionPerformed
        if (lst_games.getSelectedValue() != null) {
            new ErrThread() {
                @Override
                public void handledRun() throws Throwable {
                    FileChooser mfc = new FileChooser(FileChooser.FILES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.getLastOpenedDir());

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        File file = mfc.getSelectedFile();
                        tf_exePath.setText(file.getAbsolutePath());
                        saveTempData();
                    }//else cancelled
                }
            }.start();
        }
}//GEN-LAST:event_btn_browsePathActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        if (saveTempData()) {
            for (String gamename : tempExePath.keySet()) {
                String ID = GameDatabase.getIDofGame(gamename);
                GameDatabase.setLocalExecutablePath(ID, tempExePath.get(gamename));
            }
            for (String gamename : tempInstallPath.keySet()) {
                String ID = GameDatabase.getIDofGame(gamename);
                GameDatabase.setLocalInstallPath(ID, tempInstallPath.get(gamename));
            }
            for (String gamename : tempParams.keySet()) {
                String ID = GameDatabase.getIDofGame(gamename);
                GameDatabase.setAdditionalParameters(ID, tempParams.get(gamename));
            }
            GameDatabase.saveLocalPaths();
            for (String gamename : tempExePath.keySet()) {
                ChannelPanel cp = TabOrganizer.getChannelPanel(gamename);
                if (cp != null) {
                    cp.setLaunchable(true);
                }
            }

            FrameOrganizer.getClientFrame().refreshInstalledGames();
            FrameOrganizer.closeManageGamesFrame();
        }
        Settings.setWineCommand(cmb_winEnv.getSelectedItem().toString());
        Settings.setDOSBoxExecutable(tf_dosboxExe.getText());
        Settings.setDOSBoxFullscreen(cb_dosboxFullscreen.isSelected());
}//GEN-LAST:event_btn_saveActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        FrameOrganizer.closeManageGamesFrame();
}//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_browseInstallPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseInstallPathActionPerformed
        if (lst_games.getSelectedValue() != null) {
            new ErrThread() {

                @Override
                public void handledRun() throws Throwable {
                    FileChooser mfc = new FileChooser(FileChooser.DIRECTORIES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.getLastOpenedDir());

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        File file = mfc.getSelectedFile();
                        tf_installPath.setText(file.getAbsolutePath());
                        saveTempData();
                    }//else cancelled
                }
            }.start();
        }
}//GEN-LAST:event_btn_browseInstallPathActionPerformed

    private void filter(){
        lst_games.removeAll();
        channels.clear();
        Vector<String> installedgames = GameDatabase.getInstalledGameNames();
        String filter = tf_filter.getText();
        for (String gameName : GameDatabase.getNonDPlayGameNames()) {
            if (gameName.toLowerCase().contains(filter.toLowerCase())) {
                if (cb_showInstalledOnly.isSelected()) {
                    if (installedgames.contains(gameName)) {
                        channels.add(gameName);
                    }
                } else {
                    channels.add(gameName);
                }
            }
        }
        this.repaint();
        lst_games.clearSelection();
    }

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    FrameOrganizer.closeManageGamesFrame();
}//GEN-LAST:event_formWindowClosing

private void lst_gamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lst_gamesValueChanged
    if (lst_games.getSelectedValue() != null) {
        tf_exePath.setEnabled(true);
        tf_installPath.setEnabled(true);
        tf_parameters.setEnabled(true);
        lbl_installPath.setEnabled(true);
        lbl_params.setEnabled(true);
        lbl_path.setEnabled(true);
        btn_browseInstallPath.setEnabled(true);
        btn_browsePath.setEnabled(true);
        btn_revert.setEnabled(true);
        lbl_installDirIcon.setIcon(null);
        lbl_exePathIcon.setIcon(null);

        String path = tempExePath.get(lst_games.getSelectedValue().toString());
        if (path == null) {
            path = GameDatabase.getLaunchPathWithExe(lst_games.getSelectedValue().toString(), null);
        }
        tf_exePath.setText(path);
        String installpath = tempInstallPath.get(lst_games.getSelectedValue().toString());
        if (installpath == null) {
            installpath = GameDatabase.getInstallPath(lst_games.getSelectedValue().toString());
        }
        tf_installPath.setText(installpath);
        String params = tempParams.get(lst_games.getSelectedValue().toString());
        if (params == null) {
            params = GameDatabase.getAdditionalParameters(GameDatabase.getIDofGame(lst_games.getSelectedValue().toString()));
        }
        if (params != null) {
            tf_parameters.setText(params);
        } else {
            tf_parameters.setText("");
        }
    } else {
        tf_exePath.setEnabled(false);
        tf_exePath.setText("");
        tf_installPath.setEnabled(false);
        tf_installPath.setText("");
        tf_parameters.setEnabled(false);
        tf_parameters.setText("");
        lbl_installPath.setEnabled(false);
        lbl_params.setEnabled(false);
        lbl_path.setEnabled(false);
        btn_browseInstallPath.setEnabled(false);
        btn_browsePath.setEnabled(false);
        btn_revert.setEnabled(false);
        lbl_installDirIcon.setIcon(null);
        lbl_exePathIcon.setIcon(null);
    }
}//GEN-LAST:event_lst_gamesValueChanged

private void cb_showInstalledOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_showInstalledOnlyActionPerformed
    filter();
    lst_games.clearSelection();
}//GEN-LAST:event_cb_showInstalledOnlyActionPerformed

private void tf_installPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_installPathActionPerformed
    saveTempData();
}//GEN-LAST:event_tf_installPathActionPerformed

private void tf_installPathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_installPathFocusLost
    saveTempData();
}//GEN-LAST:event_tf_installPathFocusLost

private void tf_exePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_exePathActionPerformed
    saveTempData();
}//GEN-LAST:event_tf_exePathActionPerformed

private void tf_exePathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_exePathFocusLost
    saveTempData();
}//GEN-LAST:event_tf_exePathFocusLost

private void tf_parametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_parametersActionPerformed
    saveTempData();
}//GEN-LAST:event_tf_parametersActionPerformed

private void tf_parametersFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_parametersFocusLost
    saveTempData();
}//GEN-LAST:event_tf_parametersFocusLost

private void btn_revertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_revertActionPerformed
    if (lst_games.getSelectedValue() != null) {
        tempExePath.remove(lst_games.getSelectedValue().toString());
        tempInstallPath.remove(lst_games.getSelectedValue().toString());
        tempParams.remove(lst_games.getSelectedValue().toString());
        lst_gamesValueChanged(null);
    }
}//GEN-LAST:event_btn_revertActionPerformed

private void tf_installPathCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_installPathCaretUpdate
    if (tf_installPath.getText().length() > 0) {
        if (Verification.verifyDirectory(tf_installPath.getText())) {
            lbl_installDirIcon.setIcon(Icons.acceptIcon);
            tempInstallPath.put(lst_games.getSelectedValue().toString(), tf_installPath.getText());
        } else {
            lbl_installDirIcon.setIcon(Icons.refuseIcon);
        }
    } else {
        lbl_installDirIcon.setIcon(null);
        tempInstallPath.put(lst_games.getSelectedValue().toString(), tf_installPath.getText());
    }
}//GEN-LAST:event_tf_installPathCaretUpdate

private void tf_exePathCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_exePathCaretUpdate
    if (tf_exePath.getText().length() > 0) {
        if (Verification.verifyFile(tf_exePath.getText())) {
            lbl_exePathIcon.setIcon(Icons.acceptIcon);
            tempExePath.put(lst_games.getSelectedValue().toString(), tf_exePath.getText());
        } else {
            lbl_exePathIcon.setIcon(Icons.refuseIcon);
        }
    } else {
        lbl_exePathIcon.setIcon(null);
        tempExePath.put(lst_games.getSelectedValue().toString(), tf_exePath.getText());
    }
}//GEN-LAST:event_tf_exePathCaretUpdate

private void tf_parametersCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_parametersCaretUpdate
    tempParams.put(lst_games.getSelectedValue().toString(), tf_parameters.getText());
}//GEN-LAST:event_tf_parametersCaretUpdate

private void btn_browseDosboxExecutableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseDosboxExecutableActionPerformed
    new ErrThread() {
        @Override
        public void handledRun() throws Throwable {
            FileChooser mfc = new FileChooser(FileChooser.FILES_ONLY_MODE);
            int returnVal = mfc.choose(Globals.getLastOpenedDir());

            if (returnVal == FileChooser.SELECT_ACTION) {
                File file = mfc.getSelectedFile();
                tf_dosboxExe.setText(file.getAbsolutePath());
            }//else cancelled
        }
    }.start();
}//GEN-LAST:event_btn_browseDosboxExecutableActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_browseDosboxExecutable;
    private javax.swing.JButton btn_browseInstallPath;
    private javax.swing.JButton btn_browsePath;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_revert;
    private javax.swing.JButton btn_save;
    private javax.swing.JCheckBox cb_dosboxFullscreen;
    private javax.swing.JCheckBox cb_showInstalledOnly;
    private javax.swing.JComboBox cmb_winEnv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbl_dplayEnv;
    private javax.swing.JLabel lbl_dplayEnvNote;
    private javax.swing.JLabel lbl_exePathIcon;
    private javax.swing.JLabel lbl_filter;
    private javax.swing.JLabel lbl_installDirIcon;
    private javax.swing.JLabel lbl_installPath;
    private javax.swing.JLabel lbl_note;
    private javax.swing.JLabel lbl_noteText;
    private javax.swing.JLabel lbl_params;
    private javax.swing.JLabel lbl_path;
    private javax.swing.JList lst_games;
    private javax.swing.JPanel pnl_dosbox;
    private javax.swing.JPanel pnl_environment;
    private javax.swing.JPanel pnl_games;
    private javax.swing.JPanel pnl_settings;
    private javax.swing.JPanel pnl_windowsenvironment;
    private javax.swing.JScrollPane scrl_games;
    private javax.swing.JTextField tf_dosboxExe;
    private coopnetclient.frames.components.ValidatorJTextField tf_exePath;
    private javax.swing.JTextField tf_filter;
    private coopnetclient.frames.components.ValidatorJTextField tf_installPath;
    private javax.swing.JTextField tf_parameters;
    // End of variables declaration//GEN-END:variables
}

