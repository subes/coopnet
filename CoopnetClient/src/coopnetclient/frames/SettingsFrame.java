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

import coopnetclient.utils.filechooser.FileChooser;
import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.Protocol;
import coopnetclient.modules.Colorizer;
import java.awt.GraphicsEnvironment;
import javax.swing.JOptionPane;
import coopnetclient.modules.listeners.ColorChooserButtonActionListener;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SettingsFrame extends javax.swing.JFrame {

    /** Creates new form OptionsFrame */
    public SettingsFrame() {
        initComponents();
        cmb_homeChannel.insertItemAt("", 0);

        //FILL IN FIELDS
        tf_serverAddress.setText(coopnetclient.modules.Settings.getServerIp());
        tf_serverPort.setText(String.valueOf(coopnetclient.modules.Settings.getServerPort()));

        cmb_homeChannel.setSelectedItem(coopnetclient.modules.Settings.getHomeChannel());
        
        if(Globals.getOperatingSystem() == Globals.OS_WINDOWS){
            tf_dplayEnv.setVisible(false);
            lbl_dplayEnv.setVisible(false);
        }
        tf_dplayEnv.setText(coopnetclient.modules.Settings.getWineCommand());
        tf_transferPort.setText(coopnetclient.modules.Settings.getFiletTansferPort()+"");

        cb_autoLogin.setSelected(coopnetclient.modules.Settings.getAutoLogin());
        cb_timeStamps.setSelected(coopnetclient.modules.Settings.getTimeStampEnabled());

        tf_receiveDir.setText(coopnetclient.modules.Settings.getRecieveDestination());
        cb_colorizeBody.setSelected(coopnetclient.modules.Settings.getColorizeBody());
        cb_colorizeText.setSelected(coopnetclient.modules.Settings.getColorizeText());

        cb_sounds.setSelected(coopnetclient.modules.Settings.getSoundEnabled());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String fontNames[] = ge.getAvailableFontFamilyNames();
        cmb_playerNamesType.setModel(new javax.swing.DefaultComboBoxModel(fontNames));
        cmb_playerMessagesType.setModel(new javax.swing.DefaultComboBoxModel(fontNames));
        
        cmb_playerNamesType.setSelectedItem(coopnetclient.modules.Settings.getNameStyle());
        tf_playerNamesSize.setText(String.valueOf(coopnetclient.modules.Settings.getNameSize()));
        cmb_playerMessagesType.setSelectedItem(coopnetclient.modules.Settings.getMessageStyle());
        tf_playerMessagesSize.setText(String.valueOf(coopnetclient.modules.Settings.getMessageSize()));

        
        UIManager.LookAndFeelInfo infos[] = UIManager.getInstalledLookAndFeels();
        String styles[] = new String[infos.length];
        for(int i = 0; i < infos.length; i++){
            styles[i] = infos[i].getName();
        }
        cmb_style.setModel(new javax.swing.DefaultComboBoxModel(styles));
        cmb_style.setSelectedItem(coopnetclient.modules.Settings.getSelectedLookAndFeel());
        cb_nativeStyle.setSelected(coopnetclient.modules.Settings.getUseNativeLookAndFeel());
        
        //add action listener to color buttons
        colorizeColorButtons();
        btn_background.addActionListener(new ColorChooserButtonActionListener(btn_background));
        btn_foreground.addActionListener(new ColorChooserButtonActionListener(btn_foreground));
        btn_yourUsername.addActionListener(new ColorChooserButtonActionListener(btn_yourUsername));
        btn_otherUsernames.addActionListener(new ColorChooserButtonActionListener(btn_otherUsernames));
        btn_systemMessages.addActionListener(new ColorChooserButtonActionListener(btn_systemMessages));
        btn_whisperMessages.addActionListener(new ColorChooserButtonActionListener(btn_whisperMessages));
        btn_userMessages.addActionListener(new ColorChooserButtonActionListener(btn_userMessages));
        btn_selection.addActionListener(new ColorChooserButtonActionListener(btn_selection));

        //Toggle buttons corresponding to checkboxes
        toggleItemsOf_cb_NativeStyle();
        toggleItemsOf_cb_ColorizeBody();
        toggleItemsOf_cb_ColorizeText();
    }

    public void customCodeForColorizer() {
        colorizeColorButtons();
    }
    
    private void colorizeColorButtons(){
        btn_background.setForeground(coopnetclient.modules.Settings.getBackgroundColor());
        btn_foreground.setForeground(coopnetclient.modules.Settings.getForegroundColor());
        btn_yourUsername.setForeground(coopnetclient.modules.Settings.getYourUsernameColor());
        btn_otherUsernames.setForeground(coopnetclient.modules.Settings.getOtherUsernamesColor());
        btn_systemMessages.setForeground(coopnetclient.modules.Settings.getSystemMessageColor());
        btn_whisperMessages.setForeground(coopnetclient.modules.Settings.getWhisperMessageColor());
        btn_userMessages.setForeground(coopnetclient.modules.Settings.getUserMessageColor());
        btn_selection.setForeground(coopnetclient.modules.Settings.getSelectionColor());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabpn_settings = new javax.swing.JTabbedPane();
        pnl_general = new javax.swing.JPanel();
        cb_sounds = new javax.swing.JCheckBox();
        lbl_homeChannel = new javax.swing.JLabel();
        cmb_homeChannel = new javax.swing.JComboBox();
        cb_sleepMode = new javax.swing.JCheckBox();
        lbl_dplayEnv = new javax.swing.JLabel();
        tf_dplayEnv = new javax.swing.JTextField();
        pnl_network = new javax.swing.JPanel();
        lbl_serverAddress = new javax.swing.JLabel();
        tf_serverAddress = new javax.swing.JTextField();
        cb_autoLogin = new javax.swing.JCheckBox();
        tf_serverPort = new javax.swing.JTextField();
        lbl_serverPort = new javax.swing.JLabel();
        lbl_transferPort = new javax.swing.JLabel();
        tf_transferPort = new javax.swing.JTextField();
        lbl_receiveDir = new javax.swing.JLabel();
        tf_receiveDir = new javax.swing.JTextField();
        btn_browseReceiveDir = new javax.swing.JButton();
        pnl_text = new javax.swing.JPanel();
        pnl_textStyle = new javax.swing.JPanel();
        lbl_playerNames = new javax.swing.JLabel();
        lbl_playerNamesType = new javax.swing.JLabel();
        cmb_playerNamesType = new javax.swing.JComboBox();
        lbl_playerNamesSize = new javax.swing.JLabel();
        tf_playerNamesSize = new javax.swing.JTextField();
        lbl_playerMessages = new javax.swing.JLabel();
        lbl_playerMessagesType = new javax.swing.JLabel();
        lbl_playerMessagesSize = new javax.swing.JLabel();
        cmb_playerMessagesType = new javax.swing.JComboBox();
        tf_playerMessagesSize = new javax.swing.JTextField();
        pnl_textColors = new javax.swing.JPanel();
        lbl_yourUsername = new javax.swing.JLabel();
        btn_yourUsername = new javax.swing.JButton();
        lbl_otherUsernames = new javax.swing.JLabel();
        btn_otherUsernames = new javax.swing.JButton();
        lbl_systemMessages = new javax.swing.JLabel();
        btn_systemMessages = new javax.swing.JButton();
        lbl_whisperMessages = new javax.swing.JLabel();
        btn_whisperMessages = new javax.swing.JButton();
        lbl_userMessages = new javax.swing.JLabel();
        btn_userMessages = new javax.swing.JButton();
        cb_colorizeText = new javax.swing.JCheckBox();
        cb_timeStamps = new javax.swing.JCheckBox();
        pnl_body = new javax.swing.JPanel();
        pnl_bodyColors = new javax.swing.JPanel();
        lbl_background = new javax.swing.JLabel();
        lbl_foreground = new javax.swing.JLabel();
        btn_foreground = new javax.swing.JButton();
        btn_background = new javax.swing.JButton();
        cb_colorizeBody = new javax.swing.JCheckBox();
        lbl_selection = new javax.swing.JLabel();
        btn_selection = new javax.swing.JButton();
        pnl_lookAndFeel = new javax.swing.JPanel();
        cmb_style = new javax.swing.JComboBox();
        lbl_style = new javax.swing.JLabel();
        cb_nativeStyle = new javax.swing.JCheckBox();
        lbl_noteText = new javax.swing.JLabel();
        lbl_note = new javax.swing.JLabel();
        btn_save = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        btn_apply = new javax.swing.JButton();

        setTitle("Client settings");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        cb_sounds.setText("Sounds");

        lbl_homeChannel.setText("Home channel:");

        cmb_homeChannel.setModel(new javax.swing.DefaultComboBoxModel(GameDatabase.gameNames()));

        cb_sleepMode.setText("Sleep mode");

        lbl_dplayEnv.setText("DirectPlay environment:");

        javax.swing.GroupLayout pnl_generalLayout = new javax.swing.GroupLayout(pnl_general);
        pnl_general.setLayout(pnl_generalLayout);
        pnl_generalLayout.setHorizontalGroup(
            pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cb_sleepMode)
                    .addComponent(cb_sounds)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_generalLayout.createSequentialGroup()
                        .addComponent(lbl_homeChannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_homeChannel, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_generalLayout.createSequentialGroup()
                        .addComponent(lbl_dplayEnv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_dplayEnv, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_generalLayout.setVerticalGroup(
            pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_homeChannel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_homeChannel))
                .addGap(18, 18, 18)
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_dplayEnv)
                    .addComponent(tf_dplayEnv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                .addComponent(cb_sounds)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_sleepMode)
                .addContainerGap())
        );

        tabpn_settings.addTab("General", pnl_general);

        lbl_serverAddress.setText("Server address:");

        cb_autoLogin.setText("Automatically login");

        lbl_serverPort.setText("Server port:");

        lbl_transferPort.setText("Filetransfer port:");

        lbl_receiveDir.setText("Default file destination:");

        btn_browseReceiveDir.setText("Browse");
        btn_browseReceiveDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseReceiveDirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_networkLayout = new javax.swing.GroupLayout(pnl_network);
        pnl_network.setLayout(pnl_networkLayout);
        pnl_networkLayout.setHorizontalGroup(
            pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_networkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_networkLayout.createSequentialGroup()
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_serverPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_serverAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_networkLayout.createSequentialGroup()
                                .addComponent(tf_serverAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cb_autoLogin))
                            .addComponent(tf_serverPort, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_networkLayout.createSequentialGroup()
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_receiveDir)
                            .addComponent(lbl_transferPort, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_transferPort, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnl_networkLayout.createSequentialGroup()
                                .addComponent(tf_receiveDir, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_browseReceiveDir)))
                        .addGap(208, 208, 208)))
                .addGap(649, 649, 649))
        );
        pnl_networkLayout.setVerticalGroup(
            pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_networkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_serverAddress)
                    .addComponent(tf_serverAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_autoLogin))
                .addGap(11, 11, 11)
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_serverPort)
                    .addComponent(tf_serverPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_receiveDir)
                    .addComponent(tf_receiveDir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_browseReceiveDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_transferPort)
                    .addComponent(tf_transferPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(140, Short.MAX_VALUE))
        );

        tabpn_settings.addTab("Network", pnl_network);

        pnl_textStyle.setBorder(javax.swing.BorderFactory.createTitledBorder("Text style"));

        lbl_playerNames.setText("<html><u>Names</u></html>");

        lbl_playerNamesType.setText("Type:");

        cmb_playerNamesType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_playerNamesSize.setText("Size:");

        lbl_playerMessages.setText("<html><u>Messages</u></html>");

        lbl_playerMessagesType.setText("Type:");

        lbl_playerMessagesSize.setText("Size:");

        cmb_playerMessagesType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnl_textStyleLayout = new javax.swing.GroupLayout(pnl_textStyle);
        pnl_textStyle.setLayout(pnl_textStyleLayout);
        pnl_textStyleLayout.setHorizontalGroup(
            pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textStyleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_playerMessages)
                    .addComponent(lbl_playerNames)
                    .addGroup(pnl_textStyleLayout.createSequentialGroup()
                        .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_textStyleLayout.createSequentialGroup()
                                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_playerMessagesType)
                                    .addComponent(lbl_playerMessagesSize))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnl_textStyleLayout.createSequentialGroup()
                                        .addComponent(tf_playerMessagesSize, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(100, 100, 100))
                                    .addComponent(cmb_playerMessagesType, 0, 214, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_textStyleLayout.createSequentialGroup()
                                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_playerNamesType)
                                    .addComponent(lbl_playerNamesSize))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tf_playerNamesSize, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_playerNamesType, 0, 214, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addContainerGap())
        );
        pnl_textStyleLayout.setVerticalGroup(
            pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textStyleLayout.createSequentialGroup()
                .addComponent(lbl_playerNames)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerNamesType)
                    .addComponent(cmb_playerNamesType, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerNamesSize)
                    .addComponent(tf_playerNamesSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_playerMessages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerMessagesType)
                    .addComponent(cmb_playerMessagesType, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerMessagesSize)
                    .addComponent(tf_playerMessagesSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pnl_textColors.setBorder(javax.swing.BorderFactory.createTitledBorder("Text colors"));

        lbl_yourUsername.setText("Your username:");

        btn_yourUsername.setText(null);

        lbl_otherUsernames.setText("Other usernames:");

        btn_otherUsernames.setText(null);

        lbl_systemMessages.setText("System messages:");

        btn_systemMessages.setText(null);

        lbl_whisperMessages.setText("Whisper messages:");

        btn_whisperMessages.setText(null);

        lbl_userMessages.setText("User messages:");

        btn_userMessages.setText(null);

        cb_colorizeText.setText("enable");
        cb_colorizeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_colorizeTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_textColorsLayout = new javax.swing.GroupLayout(pnl_textColors);
        pnl_textColors.setLayout(pnl_textColorsLayout);
        pnl_textColorsLayout.setHorizontalGroup(
            pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textColorsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_textColorsLayout.createSequentialGroup()
                        .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_yourUsername, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_otherUsernames, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_userMessages, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_systemMessages, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_whisperMessages, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_whisperMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(btn_otherUsernames, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(btn_yourUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(btn_userMessages, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(btn_systemMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)))
                    .addComponent(cb_colorizeText))
                .addContainerGap())
        );
        pnl_textColorsLayout.setVerticalGroup(
            pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textColorsLayout.createSequentialGroup()
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_yourUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_yourUsername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_otherUsernames, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_otherUsernames))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_userMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_userMessages))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_systemMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_systemMessages))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_whisperMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_whisperMessages))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cb_colorizeText)
                .addContainerGap())
        );

        cb_timeStamps.setText("Timestamps");

        javax.swing.GroupLayout pnl_textLayout = new javax.swing.GroupLayout(pnl_text);
        pnl_text.setLayout(pnl_textLayout);
        pnl_textLayout.setHorizontalGroup(
            pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_textLayout.createSequentialGroup()
                        .addComponent(pnl_textStyle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnl_textColors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cb_timeStamps))
                .addContainerGap())
        );
        pnl_textLayout.setVerticalGroup(
            pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_textLayout.createSequentialGroup()
                        .addComponent(pnl_textColors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(80, 80, 80))
                    .addGroup(pnl_textLayout.createSequentialGroup()
                        .addComponent(pnl_textStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(cb_timeStamps)
                .addContainerGap())
        );

        tabpn_settings.addTab("Text", pnl_text);

        pnl_bodyColors.setBorder(javax.swing.BorderFactory.createTitledBorder("Body colors"));

        lbl_background.setText("Background:");

        lbl_foreground.setText("Foreground:");

        btn_foreground.setText(null);
        btn_foreground.setMaximumSize(null);
        btn_foreground.setMinimumSize(null);
        btn_foreground.setPreferredSize(null);

        btn_background.setText(null);
        btn_background.setMaximumSize(null);
        btn_background.setMinimumSize(null);
        btn_background.setPreferredSize(null);

        cb_colorizeBody.setText("enable");
        cb_colorizeBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_colorizeBodyActionPerformed(evt);
            }
        });

        lbl_selection.setText("Selection:");

        btn_selection.setText(null);

        javax.swing.GroupLayout pnl_bodyColorsLayout = new javax.swing.GroupLayout(pnl_bodyColors);
        pnl_bodyColors.setLayout(pnl_bodyColorsLayout);
        pnl_bodyColorsLayout.setHorizontalGroup(
            pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                        .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_background)
                                    .addComponent(lbl_foreground))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                                .addComponent(lbl_selection, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                .addGap(27, 27, 27)))
                        .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_foreground, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btn_background, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btn_selection, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)))
                    .addComponent(cb_colorizeBody))
                .addContainerGap())
        );
        pnl_bodyColorsLayout.setVerticalGroup(
            pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_background)
                    .addComponent(btn_background, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_foreground)
                    .addComponent(btn_foreground, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_selection)
                    .addComponent(btn_selection, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cb_colorizeBody)
                .addContainerGap())
        );

        pnl_lookAndFeel.setBorder(javax.swing.BorderFactory.createTitledBorder("Body style"));

        cmb_style.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_style.setText("Style:");

        cb_nativeStyle.setText("native style");
        cb_nativeStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_nativeStyleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_lookAndFeelLayout = new javax.swing.GroupLayout(pnl_lookAndFeel);
        pnl_lookAndFeel.setLayout(pnl_lookAndFeelLayout);
        pnl_lookAndFeelLayout.setHorizontalGroup(
            pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_lookAndFeelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_lookAndFeelLayout.createSequentialGroup()
                        .addComponent(lbl_style)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_style, 0, 200, Short.MAX_VALUE))
                    .addComponent(cb_nativeStyle))
                .addContainerGap())
        );
        pnl_lookAndFeelLayout.setVerticalGroup(
            pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_lookAndFeelLayout.createSequentialGroup()
                .addGroup(pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_style)
                    .addComponent(cmb_style, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cb_nativeStyle)
                .addContainerGap())
        );

        lbl_noteText.setText("<html>Custom body colors are only supported by the \"Metal\" style.<br>To enable a new style, Coopnet has to be restarted.</html>");

        lbl_note.setText("<html><b>Note:");

        javax.swing.GroupLayout pnl_bodyLayout = new javax.swing.GroupLayout(pnl_body);
        pnl_body.setLayout(pnl_bodyLayout);
        pnl_bodyLayout.setHorizontalGroup(
            pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyLayout.createSequentialGroup()
                .addGroup(pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_bodyLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnl_lookAndFeel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnl_bodyColors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_bodyLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(lbl_note)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_noteText)))
                .addContainerGap())
        );
        pnl_bodyLayout.setVerticalGroup(
            pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_bodyColors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_lookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_note)
                    .addComponent(lbl_noteText))
                .addGap(70, 70, 70))
        );

        tabpn_settings.addTab("Body", pnl_body);

        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicked(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel(evt);
            }
        });

        btn_apply.setText("Apply");
        btn_apply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_apply_ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_apply)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_cancel))
            .addComponent(tabpn_settings, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabpn_settings, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_cancel)
                    .addComponent(btn_apply))
                .addContainerGap())
        );

        tabpn_settings.getAccessibleContext().setAccessibleName("General");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicked
        //save the data
        saveSettings();
        Globals.closeSettingsFrame();
    }//GEN-LAST:event_clicked

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        Globals.closeSettingsFrame();
    }//GEN-LAST:event_cancel

    private void btn_apply_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_apply_ActionPerformed
        saveSettings();
        //Colorizer.colorize(this); //Needs fix
}//GEN-LAST:event_btn_apply_ActionPerformed

    private void btn_browseReceiveDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseReceiveDirActionPerformed
        new Thread() {

            @Override
            public void run() {
                try{
                    File inputfile = null;
                    FileChooser mfc =new FileChooser(FileChooser.DIRECTORIES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.getLastOpenedDir());

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        inputfile = mfc.getSelectedFile();
                        if (inputfile != null) {
                            tf_receiveDir.setText(inputfile.getPath());
                        }
                    }
                }catch(Exception e){
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
}//GEN-LAST:event_btn_browseReceiveDirActionPerformed

    //Enables or disables the corresponding buttons of Text Colors
    private void toggleItemsOf_cb_ColorizeText(){
        btn_yourUsername.setEnabled(cb_colorizeText.isSelected());
        btn_otherUsernames.setEnabled(cb_colorizeText.isSelected());
        btn_userMessages.setEnabled(cb_colorizeText.isSelected());
        btn_systemMessages.setEnabled(cb_colorizeText.isSelected());
        btn_whisperMessages.setEnabled(cb_colorizeText.isSelected());
    }

private void cb_colorizeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_colorizeTextActionPerformed
    toggleItemsOf_cb_ColorizeText();
}//GEN-LAST:event_cb_colorizeTextActionPerformed

    private void toggleItemsOf_cb_ColorizeBody(){
        btn_foreground.setEnabled(cb_colorizeBody.isSelected());
        btn_background.setEnabled(cb_colorizeBody.isSelected());
        btn_selection.setEnabled(cb_colorizeBody.isSelected());
    }

private void cb_colorizeBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_colorizeBodyActionPerformed
    toggleItemsOf_cb_ColorizeBody();
}//GEN-LAST:event_cb_colorizeBodyActionPerformed

    private void toggleItemsOf_cb_NativeStyle(){
        if(cb_nativeStyle.isSelected() == true){
            cb_colorizeBody.setSelected(false);
            toggleItemsOf_cb_ColorizeBody();

            //Set selection to current LAF
            UIManager.LookAndFeelInfo infos[] = UIManager.getInstalledLookAndFeels();
            for(int i = 0; i < infos.length; i++){
                if(UIManager.getSystemLookAndFeelClassName().equals(infos[i].getClassName())){
                    cmb_style.setSelectedItem(infos[i].getName());
                }
            }
        }else{
            //Set selection to current LAF
            UIManager.LookAndFeelInfo infos[] = UIManager.getInstalledLookAndFeels();
            for(int i = 0; i < infos.length; i++){
                if(UIManager.getLookAndFeel().getClass().getName().equals(infos[i].getClassName())){
                    cmb_style.setSelectedItem(infos[i].getName());
                }
            }
        }

        cmb_style.setEnabled(!cb_nativeStyle.isSelected());
        if(Colorizer.getCurrentLAFisSupportedForColoring()){
            cb_colorizeBody.setEnabled(!cb_nativeStyle.isSelected());
        }else{
            cb_colorizeBody.setEnabled(false);
        }
    }

    private void saveSettings() {
        boolean error = false;

        try {
            coopnetclient.modules.Settings.setServerIp(tf_serverAddress.getText());
            coopnetclient.modules.Settings.setServerPort(Integer.parseInt(tf_serverPort.getText()));

            coopnetclient.modules.Settings.setAutoLogin(cb_autoLogin.isSelected());
            coopnetclient.modules.Settings.setSoundEnabled(cb_sounds.isSelected());
            coopnetclient.modules.Settings.setTimeStampEnabled(cb_timeStamps.isSelected());

            if (cb_sleepMode.isSelected() != coopnetclient.modules.Settings.getSleepEnabled()) {
                coopnetclient.modules.Settings.setSleepenabled(cb_sleepMode.isSelected());
                Client.send(Protocol.SetSleep(cb_sleepMode.isSelected()), null);
            }
            coopnetclient.modules.Settings.setRecieveDestination(tf_receiveDir.getText());
            coopnetclient.modules.Settings.setHomeChannel(cmb_homeChannel.getSelectedItem().toString());
            coopnetclient.modules.Settings.setWineCommand(tf_dplayEnv.getText());
             coopnetclient.modules.Settings.setFiletTansferPort(new Integer(tf_transferPort.getText()));

            //Colors
            coopnetclient.modules.Settings.setBackgroundColor(btn_background.getForeground());
            coopnetclient.modules.Settings.setForegroundColor(btn_foreground.getForeground());

            coopnetclient.modules.Settings.setYourUsernameColor(btn_yourUsername.getForeground());
            coopnetclient.modules.Settings.setOtherUsernamesColor(btn_otherUsernames.getForeground());
            coopnetclient.modules.Settings.setSystemMessageColor(btn_systemMessages.getForeground());
            coopnetclient.modules.Settings.setWhisperMessageColor(btn_whisperMessages.getForeground());
            coopnetclient.modules.Settings.setUserMessageColor(btn_userMessages.getForeground());
            coopnetclient.modules.Settings.setSelectionColor(btn_selection.getForeground());
            
            coopnetclient.modules.Settings.setSelectedLookAndFeel((String)cmb_style.getSelectedItem());
            coopnetclient.modules.Settings.setUseNativeLookAndFeel(cb_nativeStyle.isSelected());
            
            coopnetclient.modules.Settings.setColorizeBody(cb_colorizeBody.isSelected());
            coopnetclient.modules.Settings.setColorizeText(cb_colorizeText.isSelected());

            coopnetclient.modules.Settings.setNameStyle(cmb_playerNamesType.getSelectedItem().toString());
            coopnetclient.modules.Settings.setNameSize(Integer.parseInt(tf_playerNamesSize.getText()));
            coopnetclient.modules.Settings.setMessageStyle(cmb_playerMessagesType.getSelectedItem().toString());
            coopnetclient.modules.Settings.setMessageSize(Integer.parseInt(tf_playerMessagesSize.getText()));
            
            Globals.getClientFrame().updateMenu();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please verify that you have entered valid information!\nFor example:\n  Serverport and textsizes need to be non-decimal numbers.", "Wrong input", JOptionPane.ERROR_MESSAGE);
            error = true;
        }

        if (!error) {
            SwingUtilities.invokeLater(new Thread() {

                @Override
                public void run() {
                    try{
                        Colorizer.initColors();
                        Globals.recolorFrames();
                    }catch(Exception e){
                        ErrorHandler.handleException(e);
                    }
                }
            });
        }
    }

private void cb_nativeStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_nativeStyleActionPerformed
    toggleItemsOf_cb_NativeStyle();
}//GEN-LAST:event_cb_nativeStyleActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeSettingsFrame();
}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_apply;
    private javax.swing.JButton btn_background;
    private javax.swing.JButton btn_browseReceiveDir;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_foreground;
    private javax.swing.JButton btn_otherUsernames;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_selection;
    private javax.swing.JButton btn_systemMessages;
    private javax.swing.JButton btn_userMessages;
    private javax.swing.JButton btn_whisperMessages;
    private javax.swing.JButton btn_yourUsername;
    private javax.swing.JCheckBox cb_autoLogin;
    private javax.swing.JCheckBox cb_colorizeBody;
    private javax.swing.JCheckBox cb_colorizeText;
    private javax.swing.JCheckBox cb_nativeStyle;
    private javax.swing.JCheckBox cb_sleepMode;
    private javax.swing.JCheckBox cb_sounds;
    private javax.swing.JCheckBox cb_timeStamps;
    private javax.swing.JComboBox cmb_homeChannel;
    private javax.swing.JComboBox cmb_playerMessagesType;
    private javax.swing.JComboBox cmb_playerNamesType;
    private javax.swing.JComboBox cmb_style;
    private javax.swing.JLabel lbl_background;
    private javax.swing.JLabel lbl_dplayEnv;
    private javax.swing.JLabel lbl_foreground;
    private javax.swing.JLabel lbl_homeChannel;
    private javax.swing.JLabel lbl_note;
    private javax.swing.JLabel lbl_noteText;
    private javax.swing.JLabel lbl_otherUsernames;
    private javax.swing.JLabel lbl_playerMessages;
    private javax.swing.JLabel lbl_playerMessagesSize;
    private javax.swing.JLabel lbl_playerMessagesType;
    private javax.swing.JLabel lbl_playerNames;
    private javax.swing.JLabel lbl_playerNamesSize;
    private javax.swing.JLabel lbl_playerNamesType;
    private javax.swing.JLabel lbl_receiveDir;
    private javax.swing.JLabel lbl_selection;
    private javax.swing.JLabel lbl_serverAddress;
    private javax.swing.JLabel lbl_serverPort;
    private javax.swing.JLabel lbl_style;
    private javax.swing.JLabel lbl_systemMessages;
    private javax.swing.JLabel lbl_transferPort;
    private javax.swing.JLabel lbl_userMessages;
    private javax.swing.JLabel lbl_whisperMessages;
    private javax.swing.JLabel lbl_yourUsername;
    private javax.swing.JPanel pnl_body;
    private javax.swing.JPanel pnl_bodyColors;
    private javax.swing.JPanel pnl_general;
    private javax.swing.JPanel pnl_lookAndFeel;
    private javax.swing.JPanel pnl_network;
    private javax.swing.JPanel pnl_text;
    private javax.swing.JPanel pnl_textColors;
    private javax.swing.JPanel pnl_textStyle;
    private javax.swing.JTabbedPane tabpn_settings;
    private javax.swing.JTextField tf_dplayEnv;
    private javax.swing.JTextField tf_playerMessagesSize;
    private javax.swing.JTextField tf_playerNamesSize;
    private javax.swing.JTextField tf_receiveDir;
    private javax.swing.JTextField tf_serverAddress;
    private javax.swing.JTextField tf_serverPort;
    private javax.swing.JTextField tf_transferPort;
    // End of variables declaration//GEN-END:variables

}

