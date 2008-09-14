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

package coopnetclient.frames;

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;

public class CreateRoomFrame extends javax.swing.JFrame {

    private String channel;
    private Object[] modnames;
    private int modindex;
    private String passw;

    /** Creates new form CreateFrame */
    public CreateRoomFrame(String channel) {
        initComponents();
        this.channel = channel;
        this.modnames = GameDatabase.getGameModNames(channel);
        if (modnames != null && modnames.length > 0) {
            cb_mod.setModel(new DefaultComboBoxModel(modnames));
        } else {
            cb_mod.setVisible(false);
            lbl_mod.setVisible(false);
        }
        if (GameDatabase.getLaunchMethod(channel, null) != LaunchMethods.DIRECTPLAY) {
            cb_compatibility.setVisible(false);
        }
        if (!GameDatabase.isInstantLaunchable(channel)) {
            cb_instantroom.setVisible(false);
        }
        pnl_input.revalidate();
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

        pnl_input = new javax.swing.JPanel();
        tf_name = new javax.swing.JTextField();
        cb_compatibility = new javax.swing.JCheckBox();
        lbl_name = new javax.swing.JLabel();
        lbl_password = new javax.swing.JLabel();
        lbl_maxPlayers = new javax.swing.JLabel();
        pf_password = new javax.swing.JPasswordField();
        spn_maxPlayers = new javax.swing.JSpinner();
        lbl_limitNote = new javax.swing.JLabel();
        lbl_mod = new javax.swing.JLabel();
        cb_mod = new javax.swing.JComboBox();
        cb_instantroom = new javax.swing.JCheckBox();
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

        tf_name.setText("My room, come and play!");
        tf_name.setNextFocusableComponent(pf_password);
        tf_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_nameActionPerformed(evt);
            }
        });

        cb_compatibility.setText("Compatibility mode");
        cb_compatibility.setNextFocusableComponent(btn_create);

        lbl_name.setText("Name:");

        lbl_password.setText("Password:");

        lbl_maxPlayers.setText("Max players:");

        pf_password.setNextFocusableComponent(spn_maxPlayers);
        pf_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_passwordActionPerformed(evt);
            }
        });

        spn_maxPlayers.setModel(new javax.swing.SpinnerNumberModel(0, 0, 999, 1));
        spn_maxPlayers.setToolTipText("The maximum number of players. 0 = infinite");
        spn_maxPlayers.setNextFocusableComponent(cb_compatibility);

        lbl_limitNote.setText("( 0 == infinite )");

        lbl_mod.setText("Mod:");

        cb_instantroom.setText("Instant room");
        cb_instantroom.setToolTipText("There will be no lobby, the game launches immediately");
        cb_instantroom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_instantroomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_inputLayout = new javax.swing.GroupLayout(pnl_input);
        pnl_input.setLayout(pnl_inputLayout);
        pnl_inputLayout.setHorizontalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_inputLayout.createSequentialGroup()
                        .addComponent(lbl_name)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_name, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_inputLayout.createSequentialGroup()
                        .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_inputLayout.createSequentialGroup()
                                .addComponent(lbl_password)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pf_password, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnl_inputLayout.createSequentialGroup()
                                .addComponent(lbl_maxPlayers)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spn_maxPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(lbl_limitNote))
                            .addGroup(pnl_inputLayout.createSequentialGroup()
                                .addComponent(lbl_mod)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cb_compatibility)
                                    .addComponent(cb_mod, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cb_instantroom))))
                        .addGap(4, 4, 4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cb_compatibility, cb_instantroom, cb_mod, pf_password, tf_name});

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_maxPlayers, lbl_mod, lbl_name, lbl_password});

        pnl_inputLayout.setVerticalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_name)
                    .addComponent(tf_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_password)
                    .addComponent(pf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_maxPlayers)
                    .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(spn_maxPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnl_inputLayout.createSequentialGroup()
                            .addGap(2, 2, 2)
                            .addComponent(lbl_limitNote))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_mod)
                    .addComponent(cb_mod, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_compatibility)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_instantroom)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_name, tf_name});

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_password, pf_password});

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_limitNote, lbl_maxPlayers, spn_maxPlayers});

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cb_mod, lbl_mod});

        btn_create.setText("Create");
        btn_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create(evt);
            }
        });

        btn_cancel.setText("Cancel");
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
                .addContainerGap(298, Short.MAX_VALUE))
            .addComponent(pnl_input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnl_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_create, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void create(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create
        passw = new String(pf_password.getPassword());
        modindex = cb_mod.getSelectedIndex();
        if (modindex == 0) { //if none selected set index as -1
            modindex = -1;
        }
        if (btn_create.getText().equals("Create")) {
            //normal lobby stuff
            Protocol.createRoom(channel, tf_name.getText(), modindex, passw, (Integer) spn_maxPlayers.getValue(), cb_compatibility.isSelected(), cb_instantroom.isSelected());
            Globals.closeRoomCreationFrame();
        } else if (btn_create.getText().equals("Launch")) {
            //simple instantlaunch
            Protocol.createRoom(channel, channel, modindex, passw, (Integer) spn_maxPlayers.getValue(), cb_compatibility.isSelected(), true);
            Globals.closeRoomCreationFrame();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Client.initInstantLaunch(channel, GameDatabase.getModByIndex(channel, modindex),"", (Integer) spn_maxPlayers.getValue(), cb_compatibility.isSelected(),true);
                        Client.instantLaunch(channel);
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();
        } else if (btn_create.getText().equals("Setup & Launch")) {
            //show settings with launch button
            String modname = null;
            if (modnames.length > 0 && modindex > 0) {
                modname = modnames[modindex].toString();
            }
            
            final String finalmodname = modname;
            Globals.closeRoomCreationFrame();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Client.initInstantLaunch(channel, GameDatabase.getModByIndex(channel, modindex),"", (Integer) spn_maxPlayers.getValue(), cb_compatibility.isSelected(),true);
                        Globals.openGameSettingsFrame(channel, finalmodname, tf_name.getText(), passw, modindex, (Integer) spn_maxPlayers.getValue(), cb_compatibility.isSelected());
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_create

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        Globals.closeRoomCreationFrame();
    }//GEN-LAST:event_cancel

    private void tf_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nameActionPerformed
        btn_create.doClick();
}//GEN-LAST:event_tf_nameActionPerformed

    private void pf_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_passwordActionPerformed
        btn_create.doClick();
}//GEN-LAST:event_pf_passwordActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Globals.closeRoomCreationFrame();
    }//GEN-LAST:event_formWindowClosing

private void cb_instantroomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_instantroomActionPerformed
    if (cb_instantroom.isSelected()) {
        convertToInstantLaunch();
    } else {
        convertToLobby();
    }
}//GEN-LAST:event_cb_instantroomActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_create;
    private javax.swing.JCheckBox cb_compatibility;
    private javax.swing.JCheckBox cb_instantroom;
    private javax.swing.JComboBox cb_mod;
    private javax.swing.JLabel lbl_limitNote;
    private javax.swing.JLabel lbl_maxPlayers;
    private javax.swing.JLabel lbl_mod;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel lbl_password;
    private javax.swing.JPasswordField pf_password;
    private javax.swing.JPanel pnl_input;
    private javax.swing.JSpinner spn_maxPlayers;
    private javax.swing.JTextField tf_name;
    // End of variables declaration//GEN-END:variables
}
