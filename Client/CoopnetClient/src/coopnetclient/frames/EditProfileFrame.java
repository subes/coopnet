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

import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Verification;
import coopnetclient.utils.ui.GuiUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditProfileFrame extends JFrame {

    public static final String[] COUNTRIES = new String[]{
        "Select your country", "Abkhazia", "Afghanistan", "Akrotiri and Dhekelia",
        "Ĺland Islands", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",
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
        "Slovenia", "Solomon Islands", "Somalia", "Somaliland", "South Africa", "South Ossetia",
        "Spain", "SriLanka", "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden",
        "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor",
        "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tristan da Cunha",
        "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Tuvalu", "Uganda",
        "Ukraine", "United Arab Emirates", "United Kingdom", "United States",
        "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
        "Virgin Islands", "Wallis and Futuna", "Western Sahara", "Yemen",
        "Zambia", "Zimbabwe"
    };
    private final String initialLoginName;
    private final String initialIngameName;
    private final String initialEmail;
    private final String initialCountry;
    private final String initialWebsite;

    public EditProfileFrame(String loginName, String ingameName, String email, String country, String website) {
        initComponents();

        this.initialLoginName = loginName;
        this.initialIngameName = ingameName;
        this.initialEmail = email;
        this.initialCountry = country;
        this.initialWebsite = website;

        cmb_country.setSelectedIndex(indexOfCountry(country));
        tf_emailAddress.setText(email);
        tf_loginName.setText(loginName);
        tf_website.setText(website);
        tf_inGameName.setText(ingameName);

        DirtyListener dirtyListener = new DirtyListener();
        tf_loginName.getDocument().addDocumentListener(dirtyListener);
        tf_inGameName.getDocument().addDocumentListener(dirtyListener);
        tf_emailAddress.getDocument().addDocumentListener(dirtyListener);
        tf_website.getDocument().addDocumentListener(dirtyListener);
        cmb_country.addItemListener(dirtyListener);

        this.getRootPane().setDefaultButton(btn_save);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_cancel.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    private int indexOfCountry(String name) {
        for (int index = 0; index < COUNTRIES.length; index++) {
            if (COUNTRIES[index].equals(name)) {
                return index;
            }
        }
        return 0;
    }

    public void loginAlreadyUsed() {
        tf_loginName.showErrorMessage("Name already used!");
        btn_save.setEnabled(true);
    }

    public boolean isDirty() {
        boolean dataIsSame = initialLoginName.equals(tf_loginName.getText());
        dataIsSame = dataIsSame && initialIngameName.equals(tf_inGameName.getText());
        dataIsSame = dataIsSame && initialEmail.equals(tf_emailAddress.getText());
        dataIsSame = dataIsSame && initialCountry.equals(getCountry());
        dataIsSame = dataIsSame && initialWebsite.equals(tf_website.getText());
        return !dataIsSame;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_save = new javax.swing.JButton();
        btn_changePassword = new javax.swing.JButton();
        pnl_input = new javax.swing.JPanel();
        lbl_loginName = new javax.swing.JLabel();
        lbl_inGameName = new javax.swing.JLabel();
        lbl_emailAddress = new javax.swing.JLabel();
        lbl_country = new javax.swing.JLabel();
        lbl_website = new javax.swing.JLabel();
        cmb_country = new javax.swing.JComboBox();
        tf_loginName = new coopnetclient.frames.components.ValidatorJTextField();
        tf_inGameName = new coopnetclient.frames.components.ValidatorJTextField();
        tf_emailAddress = new coopnetclient.frames.components.ValidatorJTextField();
        tf_website = new coopnetclient.frames.components.ValidatorJTextField();
        btn_cancel = new javax.swing.JButton();

        setTitle("Edit profile");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_save.setText("Save");
        btn_save.setEnabled(false);
        btn_save.setNextFocusableComponent(btn_cancel);
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_changePassword.setMnemonic(KeyEvent.VK_H);
        btn_changePassword.setText("Change password");
        btn_changePassword.setNextFocusableComponent(tf_loginName);
        btn_changePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changepassword(evt);
            }
        });

        pnl_input.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit profile"));

        lbl_loginName.setDisplayedMnemonic(KeyEvent.VK_L);
        lbl_loginName.setLabelFor(tf_loginName);
        lbl_loginName.setText("Login name:");

        lbl_inGameName.setDisplayedMnemonic(KeyEvent.VK_I);
        lbl_inGameName.setLabelFor(tf_inGameName);
        lbl_inGameName.setText("Ingame name:");

        lbl_emailAddress.setDisplayedMnemonic(KeyEvent.VK_E);
        lbl_emailAddress.setLabelFor(tf_emailAddress);
        lbl_emailAddress.setText("E-Mail address:");
        lbl_emailAddress.setToolTipText("Only used to send password reminders and such, no spam or advertisement");

        lbl_country.setDisplayedMnemonic(KeyEvent.VK_O);
        lbl_country.setLabelFor(cmb_country);
        lbl_country.setText("Country:");

        lbl_website.setDisplayedMnemonic(KeyEvent.VK_W        );
        lbl_website.setLabelFor(tf_website);
        lbl_website.setText("Website:");

        cmb_country.setModel(new javax.swing.DefaultComboBoxModel(COUNTRIES));
        cmb_country.setNextFocusableComponent(tf_website);

        tf_loginName.setNextFocusableComponent(tf_inGameName);

        tf_inGameName.setNextFocusableComponent(tf_emailAddress);

        tf_emailAddress.setNextFocusableComponent(cmb_country);

        tf_website.setNextFocusableComponent(btn_save);

        javax.swing.GroupLayout pnl_inputLayout = new javax.swing.GroupLayout(pnl_input);
        pnl_input.setLayout(pnl_inputLayout);
        pnl_inputLayout.setHorizontalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_emailAddress)
                    .addComponent(lbl_inGameName)
                    .addComponent(lbl_loginName)
                    .addComponent(lbl_country)
                    .addComponent(lbl_website))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_website, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(cmb_country, javax.swing.GroupLayout.Alignment.TRAILING, 0, 304, Short.MAX_VALUE)
                    .addComponent(tf_loginName, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(tf_inGameName, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(tf_emailAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnl_inputLayout.setVerticalGroup(
            pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_inputLayout.createSequentialGroup()
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_loginName)
                    .addComponent(tf_loginName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_inGameName)
                    .addComponent(tf_inGameName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_emailAddress)
                    .addComponent(tf_emailAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_country)
                    .addComponent(cmb_country, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_website)
                    .addComponent(tf_website, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_cancel.setText("Cancel");
        btn_cancel.setNextFocusableComponent(btn_changePassword);
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
                .addComponent(btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
                .addComponent(btn_changePassword)
                .addContainerGap())
            .addComponent(pnl_input, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnl_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_cancel)
                        .addComponent(btn_changePassword))
                    .addComponent(btn_save))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_cancel, btn_changePassword, btn_save});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void changepassword(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changepassword
        FrameOrganizer.openChangePasswordFrame();
    }//GEN-LAST:event_changepassword

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        if (!Verification.verifyLoginName(tf_loginName.getText())) {
            tf_loginName.showErrorMessage("Must be 3-30 characters, only ASCII allowed!");
            return;
        }
        if (!Verification.verifyEMail(tf_emailAddress.getText())) {
            tf_emailAddress.showErrorMessage("Invalid email!");
            return;
        }

        if (!Verification.verifyIngameName(tf_inGameName.getText())) {
            tf_inGameName.showErrorMessage("Must be 1 to 30 characters!");
            return;
        }
        if (!Verification.verifyWebsite(tf_website.getText())) {
            tf_website.showErrorMessage("Value too long!");
            return;
        }

        //sending data
        Protocol.saveProfile(tf_loginName.getText(),
                tf_inGameName.getText(),
                tf_emailAddress.getText(),
                getCountry(),
                tf_website.getText());
        GuiUtils.setControlsEnabled(this, false);
}//GEN-LAST:event_btn_saveActionPerformed

    private String getCountry(){
        if(cmb_country.getSelectedIndex() == 0){
            return "";
        }else{
            return cmb_country.getSelectedItem().toString();
        }
    }

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        FrameOrganizer.closeEditProfileFrame();
    }//GEN-LAST:event_cancel

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FrameOrganizer.closeEditProfileFrame();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_changePassword;
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox cmb_country;
    private javax.swing.JLabel lbl_country;
    private javax.swing.JLabel lbl_emailAddress;
    private javax.swing.JLabel lbl_inGameName;
    private javax.swing.JLabel lbl_loginName;
    private javax.swing.JLabel lbl_website;
    private javax.swing.JPanel pnl_input;
    private coopnetclient.frames.components.ValidatorJTextField tf_emailAddress;
    private coopnetclient.frames.components.ValidatorJTextField tf_inGameName;
    private coopnetclient.frames.components.ValidatorJTextField tf_loginName;
    private coopnetclient.frames.components.ValidatorJTextField tf_website;
    // End of variables declaration//GEN-END:variables

    private class DirtyListener implements ItemListener, DocumentListener{

        private void updateDirty(){
            boolean dataChanged = isDirty();
            btn_save.setEnabled(dataChanged);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            updateDirty();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateDirty();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateDirty();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //Do nothing
        }

    }

}
