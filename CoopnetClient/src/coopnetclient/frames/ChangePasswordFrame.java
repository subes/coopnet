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

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Settings;
import coopnetclient.utils.Verification;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class ChangePasswordFrame extends javax.swing.JFrame {

    /** Creates new form ChangePassword */
    public ChangePasswordFrame() {
        initComponents();
        this.getRootPane().setDefaultButton(btn_save);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_input = new javax.swing.JPanel();
        lbl_oldPassword = new javax.swing.JLabel();
        lbl_newPassword1 = new javax.swing.JLabel();
        tf_newPassword1 = new javax.swing.JPasswordField();
        lbl_newPassword2 = new javax.swing.JLabel();
        tf_newPassword2 = new javax.swing.JPasswordField();
        tf_oldPassword = new javax.swing.JPasswordField();
        btn_save = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        lbl_error = new javax.swing.JLabel();

        setTitle("Change password");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Change password"));

        lbl_oldPassword.setDisplayedMnemonic(KeyEvent.VK_O);
        lbl_oldPassword.setLabelFor(tf_oldPassword);
        lbl_oldPassword.setText("Old password:");

        lbl_newPassword1.setDisplayedMnemonic(KeyEvent.VK_N);
        lbl_newPassword1.setLabelFor(tf_newPassword1);
        lbl_newPassword1.setText("New password:");

        tf_newPassword1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        tf_newPassword1.setNextFocusableComponent(tf_newPassword2);
        tf_newPassword1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_newPassword1CaretUpdate(evt);
            }
        });

        lbl_newPassword2.setDisplayedMnemonic(KeyEvent.VK_N);
        lbl_newPassword2.setLabelFor(tf_newPassword2);
        lbl_newPassword2.setText("Confirm password:");

        tf_newPassword2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        tf_newPassword2.setNextFocusableComponent(btn_save);
        tf_newPassword2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_newPassword2CaretUpdate(evt);
            }
        });

        tf_oldPassword.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        tf_oldPassword.setNextFocusableComponent(tf_newPassword1);
        tf_oldPassword.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tf_oldPasswordCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout pnl_inputLayout = new javax.swing.GroupLayout(pnl_input);
        pnl_input.setLayout(pnl_inputLayout);
        pnl_inputLayout.setHorizontalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_newPassword1)
                    .addComponent(lbl_oldPassword)
                    .addComponent(lbl_newPassword2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_oldPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addComponent(tf_newPassword1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addComponent(tf_newPassword2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_inputLayout.setVerticalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_oldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_oldPassword))
                .addGap(17, 17, 17)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_newPassword1)
                    .addComponent(tf_newPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_newPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_newPassword2))
                .addContainerGap())
        );

        btn_save.setText("Save");
        btn_save.setEnabled(false);
        btn_save.setNextFocusableComponent(btn_cancel);
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.setNextFocusableComponent(lbl_oldPassword);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel(evt);
            }
        });

        lbl_error.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel)
                .addContainerGap(366, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_error, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_error)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_cancel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_save});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        String tmp1 = new String(tf_newPassword1.getPassword());
        String tmp2 = new String(tf_newPassword2.getPassword());
        if (!(tmp1.equals(tmp2))) {
            showError("Passwords don't match'", Color.red);
        } else {
            if (Verification.verifyPassword(tmp1)) {
                Protocol.changePassword(new String(tf_oldPassword.getPassword()), tmp2);
                Settings.setAutoLogin(false);
                Settings.setLastLoginPassword("");
                showError(" ", Color.red);
                btn_save.setEnabled(false);
            } else {
                showError("Your password must have 5 to 30 characters!", Color.red);
            }
        }
}//GEN-LAST:event_btn_saveActionPerformed

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        Globals.closeChangePasswordFrame();
    }//GEN-LAST:event_cancel

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Globals.closeChangePasswordFrame();
    }//GEN-LAST:event_formWindowClosing

    private void tf_oldPasswordCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_oldPasswordCaretUpdate
        String oldpassword = new String(tf_oldPassword.getPassword());
        String password1 = new String(tf_newPassword1.getPassword());
        String password2 = new String(tf_newPassword2.getPassword());
        if (Verification.verifyPassword(oldpassword) 
                && Verification.verifyPassword(password1)
                && Verification.verifyPassword(password2)) {
            btn_save.setEnabled(true);
        } else {
            btn_save.setEnabled(false);
        }
    }//GEN-LAST:event_tf_oldPasswordCaretUpdate

    private void tf_newPassword1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_newPassword1CaretUpdate
        String oldpassword = new String(tf_oldPassword.getPassword());
        String password1 = new String(tf_newPassword1.getPassword());
        String password2 = new String(tf_newPassword2.getPassword());
        if (Verification.verifyPassword(oldpassword)
                && Verification.verifyPassword(password1)
                && Verification.verifyPassword(password2)) {
            btn_save.setEnabled(true);
        } else {
            btn_save.setEnabled(false);
        }
    }//GEN-LAST:event_tf_newPassword1CaretUpdate

    private void tf_newPassword2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tf_newPassword2CaretUpdate
        String oldpassword = new String(tf_oldPassword.getPassword());
        String password1 = new String(tf_newPassword1.getPassword());
        String password2 = new String(tf_newPassword2.getPassword());
        if (Verification.verifyPassword(oldpassword)
                && Verification.verifyPassword(password1)
                && Verification.verifyPassword(password2)) {
            btn_save.setEnabled(true);
        } else {
            btn_save.setEnabled(false);
        }
    }//GEN-LAST:event_tf_newPassword2CaretUpdate

    private void showError(String message, Color color) {
        lbl_error.setForeground(color);
        lbl_error.setText(message);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_save;
    private javax.swing.JLabel lbl_error;
    private javax.swing.JLabel lbl_newPassword1;
    private javax.swing.JLabel lbl_newPassword2;
    private javax.swing.JLabel lbl_oldPassword;
    private javax.swing.JPanel pnl_input;
    private javax.swing.JPasswordField tf_newPassword1;
    private javax.swing.JPasswordField tf_newPassword2;
    private javax.swing.JPasswordField tf_oldPassword;
    // End of variables declaration//GEN-END:variables
}
