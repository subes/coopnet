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

public class RegisterPanel extends javax.swing.JPanel {

    /** Creates new form LoginPanel */
    public RegisterPanel() {
        initComponents();
        coopnetclient.utils.Colorizer.colorize(this);
    }
    
    public RegisterPanel(String loginname){
        initComponents();
        tf_name.setText(loginname);
        tf_ingameName.setText(loginname);
        coopnetclient.utils.Colorizer.colorize(this);
    }

    @Override
    public void requestFocus() {
            tf_name.requestFocus();
        }

    private void register() {
        String name = tf_name.getText();        
        String password1 = new String(pf_password1.getPassword());
        String password2 = new String(pf_password2.getPassword());
        String email = tf_email.getText();
        String ingameName = tf_ingameName.getText();
        String country = cmb_country.getSelectedItem().toString();
        String website = tf_website.getText();

        boolean error = false;
        
        if(!Verification.verifyLoginName(name)){
            tf_name.showErrorMessage("Invalid login name!");
            error = true;
        }
        
        if(!Verification.verifyEMail(email)){
            tf_email.showErrorMessage("Invalid email adress!");
            error = true;
        }
        
        if(!Verification.verifyIngameName(ingameName)){
            tf_ingameName.showErrorMessage("Invalid name!");
            error = true;
        }
        
        if(!Verification.verifyWebsite(website)){
            tf_website.showErrorMessage("Invalid website!");
            error = true;
        } 
        
        if(!Verification.verifyPassword(password1)){
            lbl_error.setForeground(Color.red);
            lbl_error.setText("Invalid password!");
            pf_password1.setText("");
            pf_password2.setText("");
            error = true;
        } else        
        if(!password1.equals(password2)){
            lbl_error.setForeground(Color.red);
            lbl_error.setText("Passwords don't match!");
            error = true;
        }else{
            lbl_error.setText(" ");
        }
        
        if(!error){            
            Protocol.register(name,password1,email,ingameName,(cmb_country.getSelectedIndex() == 0) ? "" : country,website);
            coopnetclient.utils.Settings.setLastLoginName(name);
        }
    }
    
    public void showLoginNameUsedError(){
        tf_name.showErrorMessage("Name is already in use!");
    }
    
    private void disableButtons(){
        if(btn_send.isEnabled()){
            new Thread(){
                @Override
                public void run() {
                    btn_send.setEnabled(false);
                    btn_cancel.setEnabled(false);
                    try {
                        sleep(1000);                    
                    } catch (InterruptedException ex) {}
                    if(btn_send != null){ 
                        btn_send.setEnabled(true);
                        btn_cancel.setEnabled(true);
                    }
                }
            }.start();
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        pnl_input4 = new javax.swing.JPanel();
        lbl_name4 = new javax.swing.JLabel();
        lbl_email4 = new javax.swing.JLabel();
        btn_send = new javax.swing.JButton();
        lbl_Info = new javax.swing.JLabel();
        tf_name = new coopnetclient.frames.components.AdvancedJTextField();
        tf_email = new coopnetclient.frames.components.AdvancedJTextField();
        lbl_ingameName4 = new javax.swing.JLabel();
        tf_ingameName = new coopnetclient.frames.components.AdvancedJTextField();
        lbl_password9 = new javax.swing.JLabel();
        lbl_password10 = new javax.swing.JLabel();
        pf_password1 = new javax.swing.JPasswordField();
        pf_password2 = new javax.swing.JPasswordField();
        lbl_country4 = new javax.swing.JLabel();
        cmb_country = new javax.swing.JComboBox();
        lbl_website4 = new javax.swing.JLabel();
        tf_website = new coopnetclient.frames.components.AdvancedJTextField();
        lbl_error = new javax.swing.JLabel();
        btn_cancel = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        pnl_input4.setBorder(javax.swing.BorderFactory.createTitledBorder("Register"));
        pnl_input4.setFocusable(false);
        pnl_input4.setMaximumSize(new java.awt.Dimension(300, 300));
        pnl_input4.setName(""); // NOI18N

        lbl_name4.setText("*Login name:");
        lbl_name4.setFocusable(false);

        lbl_email4.setText("*E-Mail:");
        lbl_email4.setFocusable(false);

        btn_send.setText("Register");
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        lbl_Info.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_Info.setText("<html>Please fill in your account information.<br>Fields marked by \"*\" are required information.");
        lbl_Info.setFocusable(false);

        tf_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_nameActionPerformed(evt);
            }
        });

        tf_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_emailActionPerformed(evt);
            }
        });

        lbl_ingameName4.setText("*Ingame name:");

        tf_ingameName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_ingameNameActionPerformed(evt);
            }
        });

        lbl_password9.setText("*Password:");

        lbl_password10.setText("*Confirm password:");

        pf_password1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_password11ActionPerformed(evt);
            }
        });

        pf_password2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pf_password2ActionPerformed(evt);
            }
        });

        lbl_country4.setText("Country:");

        cmb_country.setModel(new javax.swing.DefaultComboBoxModel(CountryList));

        lbl_website4.setText("Website:");

        tf_website.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_websiteActionPerformed(evt);
            }
        });

        lbl_error.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_error.setText(" ");

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_input4Layout = new javax.swing.GroupLayout(pnl_input4);
        pnl_input4.setLayout(pnl_input4Layout);
        pnl_input4Layout.setHorizontalGroup(
            pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_input4Layout.createSequentialGroup()
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_input4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_send)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                        .addComponent(btn_cancel))
                    .addGroup(pnl_input4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_Info, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
                    .addGroup(pnl_input4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_input4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbl_ingameName4))
                            .addComponent(lbl_name4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_ingameName, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(tf_name, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                    .addGroup(pnl_input4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_email4)
                            .addComponent(lbl_country4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_website4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_email, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(cmb_country, javax.swing.GroupLayout.Alignment.TRAILING, 0, 208, Short.MAX_VALUE)
                            .addComponent(tf_website, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                    .addGroup(pnl_input4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_password10)
                            .addComponent(lbl_password9, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pf_password2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(pf_password1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_input4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_error, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnl_input4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_country4, lbl_name4, lbl_password10, lbl_password9, lbl_website4});

        pnl_input4Layout.setVerticalGroup(
            pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_input4Layout.createSequentialGroup()
                .addComponent(lbl_Info)
                .addGap(7, 7, 7)
                .addComponent(lbl_error)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_name4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_ingameName4)
                    .addComponent(tf_ingameName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pf_password1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_password9))
                .addGap(10, 10, 10)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pf_password2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_password10))
                .addGap(12, 12, 12)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_email4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_country, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_country4))
                .addGap(10, 10, 10)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_website, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_website4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(pnl_input4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel)
                    .addComponent(btn_send))
                .addContainerGap())
        );

        pnl_input4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_send});

        jPanel1.add(pnl_input4, new java.awt.GridBagConstraints());

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
    TabOrganizer.closeRegisterPanel();
    TabOrganizer.openLoginPanel();
}//GEN-LAST:event_btn_cancelActionPerformed

private void tf_websiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_websiteActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_tf_websiteActionPerformed

private void pf_password2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_password2ActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_pf_password2ActionPerformed

private void pf_password11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pf_password11ActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_pf_password11ActionPerformed

private void tf_ingameNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_ingameNameActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_tf_ingameNameActionPerformed

private void tf_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_emailActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_tf_emailActionPerformed

private void tf_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_nameActionPerformed
    btn_send.doClick();
}//GEN-LAST:event_tf_nameActionPerformed

private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed
    disableButtons();
    register();
}//GEN-LAST:event_btn_sendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_send;
    private javax.swing.JComboBox cmb_country;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_Info;
    private javax.swing.JLabel lbl_country4;
    private javax.swing.JLabel lbl_email4;
    private javax.swing.JLabel lbl_error;
    private javax.swing.JLabel lbl_ingameName4;
    private javax.swing.JLabel lbl_name4;
    private javax.swing.JLabel lbl_password10;
    private javax.swing.JLabel lbl_password9;
    private javax.swing.JLabel lbl_website4;
    private javax.swing.JPasswordField pf_password1;
    private javax.swing.JPasswordField pf_password2;
    private javax.swing.JPanel pnl_input4;
    private coopnetclient.frames.components.AdvancedJTextField tf_email;
    private coopnetclient.frames.components.AdvancedJTextField tf_ingameName;
    private coopnetclient.frames.components.AdvancedJTextField tf_name;
    private coopnetclient.frames.components.AdvancedJTextField tf_website;
    // End of variables declaration//GEN-END:variables
    public static String[] CountryList = new String[]{"Select your country", "Abkhazia", "Afghanistan", "Akrotiri and Dhekelia", "Ĺland Islands", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",
        "Antigua and Barbuda", "Argentina ", "Armenia ", "Aruba", "Ascension Island",
        "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
        "Belarus", "Belgium", "Belize", "Benin ", "Bermuda", "Bhutan", "Bolivia", " Bosnia", "Botswana", "Brazil",
        "Brunei", " Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",
        "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "ChristmasIsland",
        "Cocos", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Côte d'Ivoire",
        "Croatia", "Cuba", "Cyprus", "Czech", "Denmark", "Djibouti", "Dominica", "Ecuador", "Egypt",
        "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands",
        "Faroe Islands", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany",
        "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea",
        "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia",
        "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey",
        "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia",
        "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao",
        "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
        "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
        "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Nagorno-Karabakh", "Namibia", "Nauru",
        "Nepal", "Netherlands", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island",
        "Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru",
        "Philippines", "Pitcairn", "Poland", "Portugal", "Pridnestrovie", "Puerto Rico", "Qatar",
        "Romania", "Russia", "Rwanda", "Saint-Barthélemy", "Saint Helena", "Saint Kitts and Nevis",
        "Saint Lucia", "Saint Martin", "Saint-Pierre and	Miquelon",
        "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Săo Tomé and Príncipe",
        "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia",
        "Slovenia", "Solomon	Islands", "Somalia", "Somaliland", "South Africa", "South Ossetia",
        "Spain", "SriLanka", "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden",
        "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor",
        "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tristan da Cunha",
        "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Tuvalu", "Uganda",
        "Ukraine", "United Arab Emirates", "United Kingdom", "United States",
        "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
        "Virgin Islands", "Wallis and Futuna", "Western Sahara", "Yemen",
        "Zambia", "Zimbabwe"
    };
}
