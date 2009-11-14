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
import coopnetclient.enums.LaunchMethods;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.RoomData;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.Launcher;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class CreateRoomFrame extends javax.swing.JFrame {

    private String channel;
    private Object[] modnames;
    private int modindex;
    private String passw;

    /** Creates new form CreateFrame */
    public CreateRoomFrame(String channel) {
        initComponents();
        tf_name.setText(Globals.getLastRoomName());
        this.channel = channel;
        this.modnames = GameDatabase.getGameModNames(channel);
        if (modnames != null && modnames.length > 0) {
            cmb_mod.setModel(new DefaultComboBoxModel(modnames));
        } else {
            cmb_mod.setVisible(false);
            lbl_mod.setVisible(false);
        }
        if (!GameDatabase.isInstantLaunchable(channel)) {
            cb_instantroom.setVisible(false);
        }
        pnl_input.revalidate();
        this.getRootPane().setDefaultButton(btn_create);

        if (GameDatabase.getLaunchMethod(channel, null) != LaunchMethods.DIRECTPLAY) {
            cb_searchEnabled.setVisible(false);
            lbl_search.setVisible(false);
        }

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
    }

    private void convertToInstantLaunch() {
        String modname = null;
        if (modnames.length > 0 && modindex > 0) {
            modname = modnames[modindex].toString();
        }
        ArrayList<GameSetting> settings = GameDatabase.getGameSettings(channel, modname);
        if (settings == null || settings.size() == 0) {
            btn_create.setText("Launch");
        } else {
            btn_create.setText("Setup & Launch");
        }
    }

    private void convertToLobby() {
        btn_create.setText("Create");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnl_input = new javax.swing.JPanel();
        tf_name = new javax.swing.JTextField();
        lbl_name = new javax.swing.JLabel();
        lbl_password = new javax.swing.JLabel();
        lbl_maxPlayers = new javax.swing.JLabel();
        pf_password = new javax.swing.JPasswordField();
        spn_maxPlayers = new javax.swing.JSpinner();
        lbl_limitNote = new javax.swing.JLabel();
        lbl_mod = new javax.swing.JLabel();
        cmb_mod = new javax.swing.JComboBox();
        cb_instantroom = new javax.swing.JCheckBox();
        cb_searchEnabled = new javax.swing.JCheckBox();
        lbl_search = new javax.swing.JLabel();
        btn_create = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();

        setTitle("Create room");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Create room"));
        pnl_input.setLayout(new java.awt.GridBagLayout());

        tf_name.setNextFocusableComponent(pf_password);
        tf_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_nameActionPerformed(evt);
            }
        });
        tf_name.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tf_nameFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(tf_name, gridBagConstraints);

        lbl_name.setDisplayedMnemonic(KeyEvent.VK_N);
        lbl_name.setLabelFor(tf_name);
        lbl_name.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(lbl_name, gridBagConstraints);

        lbl_password.setDisplayedMnemonic(KeyEvent.VK_P);
        lbl_password.setLabelFor(pf_password);
        lbl_password.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(lbl_password, gridBagConstraints);

        lbl_maxPlayers.setDisplayedMnemonic(KeyEvent.VK_M);
        lbl_maxPlayers.setLabelFor(spn_maxPlayers);
        lbl_maxPlayers.setText("Max players:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(lbl_maxPlayers, gridBagConstraints);

        pf_password.setNextFocusableComponent(spn_maxPlayers);
        pf_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_passwordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(pf_password, gridBagConstraints);

        spn_maxPlayers.setModel(new javax.swing.SpinnerNumberModel(0, 0, 999, 1));
        spn_maxPlayers.setToolTipText("The maximum number of players. 0 = infinite");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(spn_maxPlayers, gridBagConstraints);

        lbl_limitNote.setText("( 0 == infinite )");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(lbl_limitNote, gridBagConstraints);

        lbl_mod.setDisplayedMnemonic(KeyEvent.VK_O);
        lbl_mod.setLabelFor(cmb_mod);
        lbl_mod.setText("Mod:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(lbl_mod, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(cmb_mod, gridBagConstraints);

        cb_instantroom.setMnemonic(KeyEvent.VK_I);
        cb_instantroom.setText("Instant room");
        cb_instantroom.setToolTipText("There will be no lobby, the game launches immediately");
        cb_instantroom.setNextFocusableComponent(btn_create);
        cb_instantroom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_instantroomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_input.add(cb_instantroom, gridBagConstraints);

        cb_searchEnabled.setText("Search Enabled");
        cb_searchEnabled.setToolTipText("Search makes connecting slower but detects firewall blockage and makes the session compatible with other applications such as GameSpy");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnl_input.add(cb_searchEnabled, gridBagConstraints);

        lbl_search.setText("(No search might not work with some games)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnl_input.add(lbl_search, gridBagConstraints);

        btn_create.setText("Create");
        btn_create.setNextFocusableComponent(btn_cancel);
        btn_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.setNextFocusableComponent(tf_name);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_create)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel)
                .addContainerGap(334, Short.MAX_VALUE))
            .addComponent(pnl_input, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnl_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_create, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_create});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void create(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create
        passw = new String(pf_password.getPassword());
        modindex = cmb_mod.getSelectedIndex();
        if (modindex == 0) { //if none selected set index as -1
            modindex = -1;
        }
        btn_create.setEnabled(false);
        if (btn_create.getText().equals("Create")) {
            //normal lobby stuff
            RoomData rd = new RoomData(true, channel, modindex, "", "", (Integer) spn_maxPlayers.getValue(), "", tf_name.getText(), 0l, passw, cb_searchEnabled.isSelected(), cb_instantroom.isSelected());
            Protocol.createRoom(rd);
            FrameOrganizer.closeRoomCreationFrame();
            Globals.setLastRoomName(tf_name.getText());
            TabOrganizer.getChannelPanel(channel).disableButtons();
        } else if (btn_create.getText().equals("Launch")) {
            //simple instantlaunch

            FrameOrganizer.closeRoomCreationFrame();
            TabOrganizer.getChannelPanel(channel).disableButtons();
            new Thread() {

                @Override
                public void run() {
                    try {
                        RoomData rd = new RoomData(true, channel, modindex, "", "", (Integer) spn_maxPlayers.getValue(), "", tf_name.getText(), 0l, passw, cb_searchEnabled.isSelected(), cb_instantroom.isSelected());
                        boolean launch = Launcher.initInstantLaunch(rd);
                        if (launch) {
                            Launcher.instantLaunch();
                        }
                    } catch (Exception e) {
                        ErrorHandler.handle(e);
                    }
                }
            }.start();
        } else if (btn_create.getText().equals("Setup & Launch")) {
            //show settings with launch button
            FrameOrganizer.closeRoomCreationFrame();
            new Thread() {

                @Override
                public void run() {
                    try {
                        RoomData rd = new RoomData(true, channel, modindex, "", "", (Integer) spn_maxPlayers.getValue(), "", tf_name.getText(), 0l, passw, cb_searchEnabled.isSelected(), cb_instantroom.isSelected());
                        boolean launch = Launcher.initInstantLaunch(rd);
                        if (launch) {
                            FrameOrganizer.openGameSettingsFrame(rd);
                        }
                    } catch (Exception e) {
                        ErrorHandler.handle(e);
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_create

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        FrameOrganizer.closeRoomCreationFrame();
        Protocol.closeRoom();
    }//GEN-LAST:event_cancel

    private void tf_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nameActionPerformed
        btn_create.doClick();
}//GEN-LAST:event_tf_nameActionPerformed

    private void pf_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_passwordActionPerformed
        btn_create.doClick();
}//GEN-LAST:event_pf_passwordActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FrameOrganizer.closeRoomCreationFrame();
    }//GEN-LAST:event_formWindowClosing

private void cb_instantroomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_instantroomActionPerformed
    if (cb_instantroom.isSelected()) {
        convertToInstantLaunch();
    } else {
        convertToLobby();
    }
}//GEN-LAST:event_cb_instantroomActionPerformed

private void tf_nameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_nameFocusGained
    tf_name.setSelectionStart(0);
    tf_name.setSelectionEnd(tf_name.getText().length());
}//GEN-LAST:event_tf_nameFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_create;
    private javax.swing.JCheckBox cb_instantroom;
    private javax.swing.JCheckBox cb_searchEnabled;
    private javax.swing.JComboBox cmb_mod;
    private javax.swing.JLabel lbl_limitNote;
    private javax.swing.JLabel lbl_maxPlayers;
    private javax.swing.JLabel lbl_mod;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel lbl_password;
    private javax.swing.JLabel lbl_search;
    private javax.swing.JPasswordField pf_password;
    private javax.swing.JPanel pnl_input;
    private javax.swing.JSpinner spn_maxPlayers;
    private javax.swing.JTextField tf_name;
    // End of variables declaration//GEN-END:variables
}