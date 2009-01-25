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
package coopnetclient.frames.clientframe.tabs;

import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Verification;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class LoginPanel extends javax.swing.JPanel {

    /** Creates new form LoginPanel */
    public LoginPanel() {

        initComponents();
        tf_name.setText(coopnetclient.utils.Settings.getLastLoginName());
        cb_autoLogin.setSelected(coopnetclient.utils.Settings.getAutoLogin());
        coopnetclient.utils.ui.Colorizer.colorize(this);

        lbl_loginError.setText(" ");
    }

    @Override
    public void requestFocus() {
        tf_name.requestFocusInWindow();
    }

    private void login() {
        String name = tf_name.getText();
        String passw = new String(pf_password.getPassword());

        boolean error = false;
        if (!Verification.verifyLoginName(name)) {
            tf_name.showErrorMessage("Invalid username!");
            error = true;
        }

        if (!Verification.verifyPassword(passw)) {
            showError("Invalid password!", Color.red);
            error = true;
        }

        if(error){
            return;
        }
        
        Protocol.login(name, passw);
        disableButtons();
        coopnetclient.utils.Settings.setLastLoginName(name);
        coopnetclient.utils.Settings.setAutoLogin(cb_autoLogin.isSelected());

        if (coopnetclient.utils.Settings.getAutoLogin()) {
            coopnetclient.utils.Settings.setLastLoginPassword(passw);
        } else {
            coopnetclient.utils.Settings.setLastLoginPassword("");
        }
        showError(" ", Color.red);
    }

    public void showError(String msg, Color clr) {
        lbl_loginError.setForeground(clr);
        lbl_loginError.setText(msg);
    }

    private void disableButtons() {
        btn_login.setEnabled(false);
        btn_register.setEnabled(false);
        btn_passwordRecovery.setEnabled(false);
    }

    public void enableButtons() {
        btn_login.setEnabled(true);
        btn_register.setEnabled(true);
        btn_passwordRecovery.setEnabled(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_input = new javax.swing.JPanel();
        lbl_name = new javax.swing.JLabel();
        lbl_password = new javax.swing.JLabel();
        pf_password = new javax.swing.JPasswordField();
        btn_login = new javax.swing.JButton();
        btn_register = new javax.swing.JButton();
        cb_autoLogin = new javax.swing.JCheckBox();
        lbl_loginError = new javax.swing.JLabel();
        tf_name = new coopnetclient.frames.components.ValidatorJTextField();
        btn_passwordRecovery = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Login"));
        pnl_input.setFocusable(false);
        pnl_input.setMaximumSize(null);

        lbl_name.setDisplayedMnemonic(KeyEvent.VK_N);
        lbl_name.setLabelFor(tf_name);
        lbl_name.setText("Name:");
        lbl_name.setFocusable(false);

        lbl_password.setDisplayedMnemonic(KeyEvent.VK_P);
        lbl_password.setLabelFor(pf_password);
        lbl_password.setText("Password:");
        lbl_password.setFocusable(false);

        pf_password.setNextFocusableComponent(cb_autoLogin);
        pf_password.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                pf_passwordCaretUpdate(evt);
            }
        });
        pf_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_passwordActionPerformed(evt);
            }
        });
        pf_password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pf_passwordFocusGained(evt);
            }
        });

        btn_login.setMnemonic(KeyEvent.VK_L);
        btn_login.setText("Login");
        btn_login.setEnabled(false);
        btn_login.setNextFocusableComponent(btn_passwordRecovery);
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });

        btn_register.setMnemonic(KeyEvent.VK_R);
        btn_register.setText("Register");
        btn_register.setNextFocusableComponent(tf_name);
        btn_register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_registerActionPerformed(evt);
            }
        });

        cb_autoLogin.setMnemonic(KeyEvent.VK_A);
        cb_autoLogin.setText("Automatically login");
        cb_autoLogin.setNextFocusableComponent(btn_login);

        lbl_loginError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_loginError.setText(" ");
        lbl_loginError.setFocusable(false);

        tf_name.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_nameCaretUpdate(evt);
            }
        });
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

        btn_passwordRecovery.setMnemonic(KeyEvent.VK_C);
        btn_passwordRecovery.setText("Recover password");
        btn_passwordRecovery.setNextFocusableComponent(btn_register);
        btn_passwordRecovery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_passwordRecoveryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_inputLayout = new javax.swing.GroupLayout(pnl_input);
        pnl_input.setLayout(pnl_inputLayout);
        pnl_inputLayout.setHorizontalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_loginError, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_name, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnl_inputLayout.createSequentialGroup()
                        .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_password, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_login))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(pf_password, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tf_name, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_inputLayout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addComponent(cb_autoLogin)))
                            .addGroup(pnl_inputLayout.createSequentialGroup()
                                .addComponent(btn_passwordRecovery)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_register)))))
                .addContainerGap())
        );

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pf_password, tf_name});

        pnl_inputLayout.setVerticalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addComponent(lbl_loginError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_name)
                    .addComponent(tf_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_password)
                    .addComponent(pf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cb_autoLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_login)
                    .addComponent(btn_register)
                    .addComponent(btn_passwordRecovery))
                .addContainerGap())
        );

        pnl_inputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_login, btn_passwordRecovery, btn_register});

        add(pnl_input, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents
    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        login();
}//GEN-LAST:event_btn_loginActionPerformed

    private void pf_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_passwordActionPerformed
        btn_login.doClick();
}//GEN-LAST:event_pf_passwordActionPerformed

    private void btn_registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_registerActionPerformed
        TabOrganizer.closeLoginPanel();
        TabOrganizer.openRegisterPanel(tf_name.getText());
}//GEN-LAST:event_btn_registerActionPerformed

    private void tf_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nameActionPerformed
        if(!btn_login.isEnabled()){
            pf_password.requestFocusInWindow();
        }else{
            btn_login.doClick();
        }
    }//GEN-LAST:event_tf_nameActionPerformed

private void btn_passwordRecoveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_passwordRecoveryActionPerformed
    TabOrganizer.closeLoginPanel();
    TabOrganizer.openPasswordRecoveryPanel();
}//GEN-LAST:event_btn_passwordRecoveryActionPerformed

private void pf_passwordCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_pf_passwordCaretUpdate
    String password = new String(pf_password.getPassword());
    if( Verification.verifyLoginName(tf_name.getText())
            && Verification.verifyPassword(password) )
            {
        btn_login.setEnabled(true);
    }else{
        btn_login.setEnabled(false);
    }
}//GEN-LAST:event_pf_passwordCaretUpdate

private void tf_nameCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_nameCaretUpdate
    String password = new String(pf_password.getPassword());
    if( Verification.verifyLoginName(tf_name.getText())
            && Verification.verifyPassword(password) )
            {
        btn_login.setEnabled(true);
    }else{
        btn_login.setEnabled(false);
    }
}//GEN-LAST:event_tf_nameCaretUpdate

private void tf_nameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_nameFocusGained
    tf_name.setSelectionStart(0);
    tf_name.setSelectionEnd(tf_name.getText().length());
}//GEN-LAST:event_tf_nameFocusGained

private void pf_passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pf_passwordFocusGained
    pf_password.setSelectionStart(0);
    pf_password.setSelectionEnd(pf_password.getPassword().length);
}//GEN-LAST:event_pf_passwordFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_login;
    private javax.swing.JButton btn_passwordRecovery;
    private javax.swing.JButton btn_register;
    private javax.swing.JCheckBox cb_autoLogin;
    private javax.swing.JLabel lbl_loginError;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel lbl_password;
    private javax.swing.JPasswordField pf_password;
    private javax.swing.JPanel pnl_input;
    private coopnetclient.frames.components.ValidatorJTextField tf_name;
    // End of variables declaration//GEN-END:variables
}
