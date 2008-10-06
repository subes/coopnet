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

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Verification;
import java.awt.Color;

public class LoginPanel extends javax.swing.JPanel {

    private static String infoText = "<html>Your login name must consist of 3 to 30 characters.<br>Your password must be 5 characters or longer.<br>Login name is restricted to the following characters:<br> [A-Z] [a-z] [0-9] !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    
    /** Creates new form LoginPanel */
    public LoginPanel() {

        initComponents();
        tf_name.setText(coopnetclient.utils.Settings.getLastLoginName());
        cb_autoLogin.setSelected(coopnetclient.utils.Settings.getAutoLogin());
        coopnetclient.utils.Colorizer.colorize(this);
        
        lbl_loginError.setText(" ");
    }

    @Override
    public void requestFocus() {
        tf_name.requestFocus();
    }

    private void login() {
        String name = tf_name.getText();
        String passw = new String(pf_password.getPassword());
        
        if(!Verification.verifyLoginName(name)){
            showError("Wrong username/password, please try again!",Color.red);
            return;
        }
        
        if(!Verification.verifyPassword(passw)){
            showError("Wrong username/password, please try again!",Color.red);
            return;
        }
        
        if (tf_name.getText().length() > 0) {
            Protocol.login(name, passw);

            Globals.setThisPlayer_loginName(name);
            coopnetclient.utils.Settings.setLastLoginName(name);
            coopnetclient.utils.Settings.setAutoLogin(cb_autoLogin.isSelected());

            if (coopnetclient.utils.Settings.getAutoLogin()) {
                coopnetclient.utils.Settings.setLastLoginPassword(passw);
            } else {
                coopnetclient.utils.Settings.setLastLoginPassword("");
            }
            
            try{
                Thread.sleep(100);
            }catch(InterruptedException ex){}            
        }
        
        showError(" ", Color.red);
    }

    private void register() {
        String name = tf_name.getText();
        String passw = new String(pf_password.getPassword());

        if(!Verification.verifyLoginName(name)){
            showError("Invalid login name!",Color.red);
            lbl_info.setText(infoText);
            return;
        }
        
        if(!Verification.verifyPassword(passw)){
            showError("Invalid password!",Color.red);
            lbl_info.setText(infoText);
            return;
        }
        
        if (Verification.verifyLoginName(name) && Verification.verifyPassword(passw)) {
            Protocol.register(tf_name.getText(), passw);
        }
        
        showError(" ",Color.red);
    }
    
    public void showError(String msg, Color clr){
        lbl_loginError.setForeground(clr);
        lbl_loginError.setText(msg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnl_top = new javax.swing.JPanel();
        pnl_input = new javax.swing.JPanel();
        lbl_name = new javax.swing.JLabel();
        lbl_password = new javax.swing.JLabel();
        pf_password = new javax.swing.JPasswordField();
        btn_login = new javax.swing.JButton();
        btn_register = new javax.swing.JButton();
        cb_autoLogin = new javax.swing.JCheckBox();
        lbl_loginError = new javax.swing.JLabel();
        tf_name = new coopnetclient.frames.components.AdvancedJTextField();
        pnl_bottom = new javax.swing.JPanel();
        lbl_info = new javax.swing.JLabel();

        setFocusable(false);
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        pnl_top.setFocusable(false);

        javax.swing.GroupLayout pnl_topLayout = new javax.swing.GroupLayout(pnl_top);
        pnl_top.setLayout(pnl_topLayout);
        pnl_topLayout.setHorizontalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
        );
        pnl_topLayout.setVerticalGroup(
            pnl_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        add(pnl_top);

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Log in"));
        pnl_input.setFocusable(false);
        pnl_input.setMaximumSize(new java.awt.Dimension(300, 300));
        pnl_input.setMinimumSize(new java.awt.Dimension(300, 170));
        pnl_input.setPreferredSize(new java.awt.Dimension(300, 170));
        pnl_input.setLayout(new java.awt.GridBagLayout());

        lbl_name.setText("Name:");
        lbl_name.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        pnl_input.add(lbl_name, gridBagConstraints);

        lbl_password.setText("Password:");
        lbl_password.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        pnl_input.add(lbl_password, gridBagConstraints);

        pf_password.setNextFocusableComponent(cb_autoLogin);
        pf_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_passwordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnl_input.add(pf_password, gridBagConstraints);

        btn_login.setText("Login");
        btn_login.setNextFocusableComponent(btn_register);
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnl_input.add(btn_login, gridBagConstraints);

        btn_register.setText("Register me");
        btn_register.setNextFocusableComponent(tf_name);
        btn_register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_registerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnl_input.add(btn_register, gridBagConstraints);

        cb_autoLogin.setText("Log me in automatically");
        cb_autoLogin.setNextFocusableComponent(btn_login);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnl_input.add(cb_autoLogin, gridBagConstraints);

        lbl_loginError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_loginError.setText(" ");
        lbl_loginError.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnl_input.add(lbl_loginError, gridBagConstraints);

        tf_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_nameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnl_input.add(tf_name, gridBagConstraints);

        add(pnl_input);

        pnl_bottom.setFocusable(false);

        lbl_info.setFont(new java.awt.Font("Tahoma", 0, 10));
        lbl_info.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_info.setText("<html><pre> <br> <br>");
        lbl_info.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lbl_info.setFocusable(false);

        javax.swing.GroupLayout pnl_bottomLayout = new javax.swing.GroupLayout(pnl_bottom);
        pnl_bottom.setLayout(pnl_bottomLayout);
        pnl_bottomLayout.setHorizontalGroup(
            pnl_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_info, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        pnl_bottomLayout.setVerticalGroup(
            pnl_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bottomLayout.createSequentialGroup()
                .addComponent(lbl_info)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        add(pnl_bottom);
    }// </editor-fold>//GEN-END:initComponents
    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        login();
}//GEN-LAST:event_btn_loginActionPerformed

    private void pf_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_passwordActionPerformed
        btn_login.doClick();
}//GEN-LAST:event_pf_passwordActionPerformed

    private void btn_registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_registerActionPerformed
        register();
}//GEN-LAST:event_btn_registerActionPerformed

    private void tf_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nameActionPerformed
        btn_login.doClick();
    }//GEN-LAST:event_tf_nameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_login;
    private javax.swing.JButton btn_register;
    private javax.swing.JCheckBox cb_autoLogin;
    private javax.swing.JLabel lbl_info;
    private javax.swing.JLabel lbl_loginError;
    private javax.swing.JLabel lbl_name;
    private javax.swing.JLabel lbl_password;
    private javax.swing.JPasswordField pf_password;
    private javax.swing.JPanel pnl_bottom;
    private javax.swing.JPanel pnl_input;
    private javax.swing.JPanel pnl_top;
    private coopnetclient.frames.components.AdvancedJTextField tf_name;
    // End of variables declaration//GEN-END:variables
}
