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

import coopnetclient.Globals;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.components.KeyGrabberTextField;
import coopnetclient.frames.listeners.ColorChooserButtonActionListener;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.threads.EdtRunner;
import coopnetclient.threads.ErrThread;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Verification;
import coopnetclient.utils.filechooser.FileChooser;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.hotkeys.Hotkeys;
import coopnetclient.utils.launcher.Launcher;
import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.ui.Colorizer;
import coopnetclient.utils.ui.Colors;
import coopnetclient.utils.ui.Icons;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class SettingsFrame extends javax.swing.JFrame {

    private static final String CLOSE = "close";
    private static final String INVALID_DIRECTORY = "Invalid directory!";
    private static boolean testiInprogress = false;
    private static final Object LOCK = new Object();
    private static final long TIMEOUT = 10000;

    /**
     * Creates new form OptionsFrame
     */
    public SettingsFrame() {
        initComponents();
        this.getRootPane().setDefaultButton(btn_save);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_close.doClick();
            }
        };
        getRootPane().getActionMap().put(CLOSE, act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE);

        List gamenames = Arrays.asList(GameDatabase.getAllGameNames());
        Collections.sort(gamenames);
        cmb_homeChannel.setModel(new DefaultComboBoxModel(gamenames.toArray()));
        cmb_homeChannel.insertItemAt("", 0);

        addKeyGrabberUnfocusMouseListener();
        tf_launchKey.setKey(Settings.getLaunchHotKey(), Settings.getLaunchHotKeyMask());

        //FILL IN FIELDS
        cmb_homeChannel.setSelectedItem(Settings.getHomeChannel());

        cmb_QuickPanelPosition.setSelectedIndex(Settings.getQuickPanelPostionisLeft() ? 0 : 1);
        spn_DividerWidth.setValue(Settings.getQuickPanelDividerWidth());
        spn_ToggleButtonWidth.setValue(Settings.getQuickPanelToggleBarWidth());

        cb_TextNotification.setSelected(Settings.getContactStatusChangeTextNotification());
        cb_SoundNotification.setSelected(Settings.getContactStatusChangeSoundNotification());

        tf_transferPort.setText(Settings.getFiletTansferPort() + "");

        if (Settings.getAutoLogin()) {
            cb_autoLogin.setSelected(true);
        } else {
            cb_autoLogin.setSelected(false);
            cb_autoLogin.setToolTipText("Autologin can only be enabled on login!");
        }

        cb_timeStamps.setSelected(Settings.getTimeStampEnabled());

        tf_receiveDir.setText(Settings.getRecieveDestination());
        cb_colorizeBody.setSelected(Settings.getColorizeBody());
        cb_colorizeText.setSelected(Settings.getColorizeText());

        cb_sounds.setSelected(Settings.getSoundEnabled());
        cb_sleepMode.setSelected(Settings.getSleepEnabled());
        if (SystemTray.isSupported()) {
            cb_TrayIconEnabled.setSelected(Settings.getTrayIconEnabled());
        } else {
            cb_TrayIconEnabled.setEnabled(false);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        cmb_playerNamesType.setModel(new DefaultComboBoxModel(fontNames));
        cmb_playerMessagesType.setModel(new DefaultComboBoxModel(fontNames));

        tf_playerNamesSize.setText(String.valueOf(Settings.getNameSize()));
        cmb_playerNamesType.setSelectedItem(Settings.getNameStyle());
        tf_playerMessagesSize.setText(String.valueOf(Settings.getMessageSize()));
        cmb_playerMessagesType.setSelectedItem(Settings.getMessageStyle());

        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        String[] styles = new String[infos.length];
        for (int i = 0; i < infos.length; i++) {
            styles[i] = infos[i].getName();
        }
        cmb_style.setModel(new javax.swing.DefaultComboBoxModel(styles));
        cmb_style.setSelectedItem(Settings.getSelectedLookAndFeel());
        cb_nativeStyle.setSelected(Settings.getUseNativeLookAndFeel());

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
        btn_friendUsernames.addActionListener(new ColorChooserButtonActionListener(btn_friendUsernames));
        btn_contactMessages.addActionListener(new ColorChooserButtonActionListener(btn_contactMessages));

        cb_showOfflineContacts.setSelected(Settings.getShowOfflineContacts());

        cb_multiChannel.setSelected(Settings.getMultiChannel());

        cb_rememberMainFrameSize.setSelected(Settings.getRememberMainFrameSize());

        cb_logActivity.setSelected(Settings.getLogUserActivity());

        //Toggle buttons corresponding to checkboxes
        toggleItemsOf_cb_NativeStyle();
        toggleItemsOf_cb_ColorizeBody();
        toggleItemsOf_cb_ColorizeText();

        //connectivity test
        if (TabOrganizer.getRoomPanel() != null) {
            //if In room, add VPN options to test
            for (String interfaceName : Globals.getMatchingInterfaceIPMap(TabOrganizer.getRoomPanel().getRoomData().getInterfaceIPs()).keySet()) {
                cmb_connectionTestIPList.addItem(interfaceName);
            }
        } else {
            cmb_connectionTestIPList.addItem(Globals.INTERNET_INTERFACE_NAME);
        }
        cmb_connectionTestIPList.setSelectedItem(Globals.INTERNET_INTERFACE_NAME);
    }

    public void customCodeForColoring() {
        colorizeColorButtons();
    }

    private void colorizeColorButtons() {
        btn_background.setForeground(Settings.getBackgroundColor());
        btn_foreground.setForeground(Settings.getForegroundColor());
        btn_yourUsername.setForeground(Settings.getYourUsernameColor());
        btn_otherUsernames.setForeground(Settings.getOtherUsernamesColor());
        btn_systemMessages.setForeground(Settings.getSystemMessageColor());
        btn_whisperMessages.setForeground(Settings.getWhisperMessageColor());
        btn_userMessages.setForeground(Settings.getUserMessageColor());
        btn_selection.setForeground(Settings.getSelectionColor());
        btn_friendUsernames.setForeground(Settings.getFriendUsernameColor());
        btn_contactMessages.setForeground(Settings.getFriendMessageColor());
    }

    private List<Integer> parsePortList(String input) {
        List<Integer> result = new ArrayList<Integer>();
        List<String> wrongInputList = new ArrayList<String>();
        String[] bits = input.split(",");
        for (String bit : bits) {
            if (bit.contains("-")) {
                //range
                String[] pieces = bit.split("-");
                if (pieces.length == 1) {
                    try {
                        Integer port = Integer.valueOf(bit.trim());
                        result.add(port);
                    } catch (NumberFormatException ex) {
                        wrongInputList.add(bit);
                    }
                } else if (pieces.length == 2) {
                    Integer port1 = 0;
                    Integer port2 = 0;
                    try {
                        port1 = Integer.valueOf(pieces[0].trim());
                    } catch (NumberFormatException ex) {
                        wrongInputList.add(pieces[0]);
                    }
                    try {
                        port2 = Integer.valueOf(pieces[1].trim());
                    } catch (NumberFormatException ex) {
                        wrongInputList.add(pieces[1]);
                    }
                    for (int i = port1; i <= port2; i++) {
                        result.add(i);
                    }
                }
            } else {
                //single port
                try {
                    Integer port = Integer.valueOf(bit.trim());
                    result.add(port);
                } catch (NumberFormatException ex) {
                    wrongInputList.add(bit);
                }
            }
        }
        if (!wrongInputList.isEmpty()) {
            JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(),
                    "Cannot parse: " + wrongInputList,
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    public List<Integer> testConnection(List<Integer> ports) {
        Logger.log("Starting TCP connection test");
        List<Integer> portList = new ArrayList<Integer>();
        portList.addAll(ports);

        double totalcount = ports.size();
        double successCount = 0;

        for (Integer port : ports) {
            ServerSocketChannel server = null;
            try {
                Selector selector = Selector.open();
                // Create the server socket channel
                server = ServerSocketChannel.open();
                // nonblocking I/O
                server.configureBlocking(false);
                // bind all ports
                server.socket().bind(new java.net.InetSocketAddress(port));
                server.register(selector, SelectionKey.OP_ACCEPT);
                Logger.log("binded " + port);
                long timeoutTS = System.currentTimeMillis() + TIMEOUT;
                //wait while ports are left, or time runs out
                boolean accepted = false;
                while (System.currentTimeMillis() < timeoutTS && !accepted) {
                    accepted = false;
                    selector.selectNow();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    for (SelectionKey selectionKey : selectedKeys) {
                        if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                            ServerSocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel());
                            SocketChannel connection = socketChannel.accept();
                            if (connection == null) {
                                Logger.log("null connection accepted");
                            }
                            int workingPort = socketChannel.socket().getLocalPort();
                            Logger.log("ConnectionTest successful on port:" + workingPort);
                            portList.remove(Integer.valueOf(workingPort));

                            socketChannel.close();

                            //update progressbar, delay timeout
                            setConnTestProgress((int) (++successCount / totalcount * 100));
                            timeoutTS = System.currentTimeMillis() + TIMEOUT;
                            accepted = true;
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }//ignore
                }
                if (accepted) {
                    continue;
                } else {
                    //test failed
                    break;
                }
            } catch (IOException exception) {
                Logger.log(exception);
                //ignore, already bound, this port failed
                break;
            } finally {
                if (server != null) {
                    try {
                        server.close();
                    } catch (IOException e) {
                    }//ignore
                }
            }
        }
        return portList;
    }

    private void setConnTestProgress(final int value) {
        final Runnable task = new Runnable() {

            @Override
            public void run() {

                pb_ConnectionTestProgress.setValue(value);

            }
        };
        if (isVisible()) {
            SwingUtilities.invokeLater(task);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        bg_ConnectionTestButtonGroup = new javax.swing.ButtonGroup();
        tabpn_settings = new javax.swing.JTabbedPane();
        pnl_general = new javax.swing.JPanel();
        cb_sounds = new javax.swing.JCheckBox();
        lbl_homeChannel = new javax.swing.JLabel();
        cmb_homeChannel = new javax.swing.JComboBox();
        cb_TrayIconEnabled = new javax.swing.JCheckBox();
        pnl_hotkeys = new javax.swing.JPanel();
        lbl_launchKey = new javax.swing.JLabel();
        tf_launchKey = new KeyGrabberTextField(Hotkeys.ACTION_LAUNCH,false);
        lbl_hotkeyNoteText = new javax.swing.JLabel();
        cb_multiChannel = new javax.swing.JCheckBox();
        cb_rememberMainFrameSize = new javax.swing.JCheckBox();
        pnl_network = new javax.swing.JPanel();
        cb_autoLogin = new javax.swing.JCheckBox();
        lbl_transferPort = new javax.swing.JLabel();
        tf_transferPort = new javax.swing.JTextField();
        lbl_receiveDir = new javax.swing.JLabel();
        btn_browseReceiveDir = new javax.swing.JButton();
        cb_sleepMode = new javax.swing.JCheckBox();
        tf_receiveDir = new coopnetclient.frames.components.ValidatorJTextField();
        pnl_connection_test = new javax.swing.JPanel();
        btn_Dplay = new javax.swing.JButton();
        lbl_ConnectionTestIP = new javax.swing.JLabel();
        cmb_connectionTestIPList = new javax.swing.JComboBox();
        rb_dplay = new javax.swing.JRadioButton();
        rb_custome = new javax.swing.JRadioButton();
        tf_CustomePort = new javax.swing.JTextField();
        lbl_connectionTestResult = new javax.swing.JLabel();
        cb_connectionType = new javax.swing.JComboBox();
        pb_ConnectionTestProgress = new javax.swing.JProgressBar();
        lbl_progress = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        pnl_preview = new javax.swing.JPanel();
        lbl_preview_username = new javax.swing.JLabel();
        lbl_preview_messagetext = new javax.swing.JLabel();
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
        lbl_friendUsernames = new javax.swing.JLabel();
        btn_friendUsernames = new javax.swing.JButton();
        lbl_contactMessages = new javax.swing.JLabel();
        btn_contactMessages = new javax.swing.JButton();
        cb_logActivity = new javax.swing.JCheckBox();
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
        pnl_quickpanel = new javax.swing.JPanel();
        pnl_General = new javax.swing.JPanel();
        lbl_quickpanelposition = new javax.swing.JLabel();
        cmb_QuickPanelPosition = new javax.swing.JComboBox();
        lbl_DividerWidth = new javax.swing.JLabel();
        spn_DividerWidth = new javax.swing.JSpinner();
        lbl_ToggleButtonWidth = new javax.swing.JLabel();
        spn_ToggleButtonWidth = new javax.swing.JSpinner();
        lbl_noteText1 = new javax.swing.JLabel();
        pnl_ContactList = new javax.swing.JPanel();
        lbl_StatusChangeNotification = new javax.swing.JLabel();
        cb_TextNotification = new javax.swing.JCheckBox();
        cb_SoundNotification = new javax.swing.JCheckBox();
        cb_showOfflineContacts = new javax.swing.JCheckBox();
        btn_save = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();
        btn_apply = new javax.swing.JButton();

        setTitle("Client settings");
        setBounds(new java.awt.Rectangle(550, 400, 0, 0));
        setMinimumSize(new java.awt.Dimension(550, 300));
        setPreferredSize(new java.awt.Dimension(550, 400));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabpn_settings.setMaximumSize(getMaximumSize());
        tabpn_settings.setMinimumSize(getMinimumSize());
        tabpn_settings.setPreferredSize(new java.awt.Dimension(550, 300));

        pnl_general.setMaximumSize(getMaximumSize());
        pnl_general.setMinimumSize(getMinimumSize());

        cb_sounds.setText("Sounds");
        cb_sounds.setToolTipText("Enable sound effects");

        lbl_homeChannel.setText("Home channel:");

        cmb_homeChannel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_homeChannel.setMinimumSize(new java.awt.Dimension(5, 20));

        cb_TrayIconEnabled.setText("Tray Icon");
        cb_TrayIconEnabled.setToolTipText("<html>A tray Icon will be placed in the system tray, closing the main window will only hide it.<br>\nWhen enabled use the right click menu on the trayicon or Client/Quit menuitem in the main window to quit.");

        pnl_hotkeys.setBorder(javax.swing.BorderFactory.createTitledBorder("General HotKeys"));

        lbl_launchKey.setText("Launch room:");

        tf_launchKey.setNextFocusableComponent(btn_apply);

        lbl_hotkeyNoteText.setText("<html><table><tr><td><b>Note:</b></td><td>Click anywhere to cancel assignment,<br>press backspace to disable the hotkey.");

        javax.swing.GroupLayout pnl_hotkeysLayout = new javax.swing.GroupLayout(pnl_hotkeys);
        pnl_hotkeys.setLayout(pnl_hotkeysLayout);
        pnl_hotkeysLayout.setHorizontalGroup(
            pnl_hotkeysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_hotkeysLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_hotkeysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_hotkeysLayout.createSequentialGroup()
                        .addComponent(lbl_launchKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_launchKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_hotkeysLayout.createSequentialGroup()
                        .addComponent(lbl_hotkeyNoteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60))))
        );
        pnl_hotkeysLayout.setVerticalGroup(
            pnl_hotkeysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_hotkeysLayout.createSequentialGroup()
                .addGroup(pnl_hotkeysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_launchKey)
                    .addComponent(tf_launchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_hotkeyNoteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        cb_multiChannel.setText("Open channels as new tabs");

        cb_rememberMainFrameSize.setText("Remember size of main frame");

        javax.swing.GroupLayout pnl_generalLayout = new javax.swing.GroupLayout(pnl_general);
        pnl_general.setLayout(pnl_generalLayout);
        pnl_generalLayout.setHorizontalGroup(
            pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_generalLayout.createSequentialGroup()
                        .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cb_sounds)
                            .addComponent(cb_TrayIconEnabled)
                            .addComponent(cb_rememberMainFrameSize))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(pnl_hotkeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cb_multiChannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_generalLayout.createSequentialGroup()
                        .addComponent(lbl_homeChannel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_homeChannel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnl_generalLayout.setVerticalGroup(
            pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_homeChannel)
                    .addComponent(cmb_homeChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_multiChannel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                .addGroup(pnl_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_generalLayout.createSequentialGroup()
                        .addComponent(cb_rememberMainFrameSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cb_TrayIconEnabled)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cb_sounds))
                    .addComponent(pnl_hotkeys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabpn_settings.addTab("General", pnl_general);

        pnl_network.setMaximumSize(getMaximumSize());
        pnl_network.setMinimumSize(getMinimumSize());

        cb_autoLogin.setText("Automatically login");
        cb_autoLogin.setToolTipText("This feature securely saves your password and logs in automatically on startup, so you dont have to type your password every time.");

        lbl_transferPort.setText("Filetransfer port:");

        lbl_receiveDir.setText("Default file destination:");

        btn_browseReceiveDir.setText("Browse");
        btn_browseReceiveDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browseReceiveDirActionPerformed(evt);
            }
        });

        cb_sleepMode.setText("Sleep mode");
        cb_sleepMode.setToolTipText("<html>Sleep mode saves bandwith when playing games by disabling the channel functions.<br> Refreshing or sending a message will reenable channel functions.");

        tf_receiveDir.setToolTipText("Your recieved files will be saved to this directory by default, unless you select an other directory in the recieve panel.");
        tf_receiveDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_receiveDirActionPerformed(evt);
            }
        });
        tf_receiveDir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tf_receiveDirFocusLost(evt);
            }
        });

        pnl_connection_test.setBorder(javax.swing.BorderFactory.createTitledBorder("Connectivity test"));
        pnl_connection_test.setLayout(new java.awt.GridBagLayout());

        btn_Dplay.setText("Test now");
        btn_Dplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DplayActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnl_connection_test.add(btn_Dplay, gridBagConstraints);

        lbl_ConnectionTestIP.setText("Test connection via:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(19, 0, 0, 0);
        pnl_connection_test.add(lbl_ConnectionTestIP, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 10, 0, 0);
        pnl_connection_test.add(cmb_connectionTestIPList, gridBagConstraints);

        bg_ConnectionTestButtonGroup.add(rb_dplay);
        rb_dplay.setSelected(true);
        rb_dplay.setText("DirectPlay");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        pnl_connection_test.add(rb_dplay, gridBagConstraints);

        bg_ConnectionTestButtonGroup.add(rb_custome);
        rb_custome.setText("Custome Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnl_connection_test.add(rb_custome, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 83;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        pnl_connection_test.add(tf_CustomePort, gridBagConstraints);

        lbl_connectionTestResult.setMaximumSize(new java.awt.Dimension(20, 20));
        lbl_connectionTestResult.setMinimumSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnl_connection_test.add(lbl_connectionTestResult, gridBagConstraints);

        cb_connectionType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP", "UDP" }));
        cb_connectionType.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 0, 0);
        pnl_connection_test.add(cb_connectionType, gridBagConstraints);

        pb_ConnectionTestProgress.setMaximumSize(new java.awt.Dimension(150, 14));
        pb_ConnectionTestProgress.setMinimumSize(new java.awt.Dimension(150, 14));
        pb_ConnectionTestProgress.setPreferredSize(new java.awt.Dimension(150, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        pnl_connection_test.add(pb_ConnectionTestProgress, gridBagConstraints);

        lbl_progress.setText("Progress:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        pnl_connection_test.add(lbl_progress, gridBagConstraints);

        jLabel1.setText("<html>This utility will test your incoming connectivity.\n<br>This can help to verify port forwarding. \n<br>A Firewall may still block the game itself however.\n<br>If you are in a room, the host, otherwise the\n<br>central server will connect to you computer\n<br>to test connectivity.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnl_connection_test.add(jLabel1, gridBagConstraints);

        javax.swing.GroupLayout pnl_networkLayout = new javax.swing.GroupLayout(pnl_network);
        pnl_network.setLayout(pnl_networkLayout);
        pnl_networkLayout.setHorizontalGroup(
            pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_networkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_networkLayout.createSequentialGroup()
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbl_transferPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_receiveDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_networkLayout.createSequentialGroup()
                                .addComponent(tf_receiveDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_browseReceiveDir))
                            .addComponent(tf_transferPort, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(pnl_connection_test, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addGroup(pnl_networkLayout.createSequentialGroup()
                        .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cb_autoLogin)
                            .addComponent(cb_sleepMode))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnl_networkLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_receiveDir, lbl_transferPort});

        pnl_networkLayout.setVerticalGroup(
            pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_networkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_receiveDir)
                    .addComponent(tf_receiveDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_browseReceiveDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_networkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_transferPort)
                    .addComponent(tf_transferPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnl_connection_test, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(cb_sleepMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_autoLogin)
                .addContainerGap())
        );

        tabpn_settings.addTab("Network", pnl_network);

        pnl_textStyle.setBorder(javax.swing.BorderFactory.createTitledBorder("Text style"));

        lbl_playerNames.setText("<html><u>Names</u></html>");

        lbl_playerNamesType.setText("Type:");

        cmb_playerNamesType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_playerNamesType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_playerNamesTypeActionPerformed(evt);
            }
        });

        lbl_playerNamesSize.setText("Size:");

        tf_playerNamesSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_playerNamesSizeActionPerformed(evt);
            }
        });

        lbl_playerMessages.setText("<html><u>Messages</u></html>");

        lbl_playerMessagesType.setText("Type:");

        lbl_playerMessagesSize.setText("Size:");

        cmb_playerMessagesType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_playerMessagesType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_playerMessagesTypeActionPerformed(evt);
            }
        });

        tf_playerMessagesSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_playerMessagesSizeActionPerformed(evt);
            }
        });

        pnl_preview.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));

        lbl_preview_username.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_preview_username.setText("username:");
        lbl_preview_username.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbl_preview_messagetext.setText("message text");

        javax.swing.GroupLayout pnl_previewLayout = new javax.swing.GroupLayout(pnl_preview);
        pnl_preview.setLayout(pnl_previewLayout);
        pnl_previewLayout.setHorizontalGroup(
            pnl_previewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_previewLayout.createSequentialGroup()
                .addComponent(lbl_preview_username, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lbl_preview_messagetext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_previewLayout.setVerticalGroup(
            pnl_previewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_preview_messagetext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbl_preview_username, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnl_textStyleLayout = new javax.swing.GroupLayout(pnl_textStyle);
        pnl_textStyle.setLayout(pnl_textStyleLayout);
        pnl_textStyleLayout.setHorizontalGroup(
            pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_textStyleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnl_preview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_playerNames, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_playerMessages, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_textStyleLayout.createSequentialGroup()
                        .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_playerMessagesType)
                            .addComponent(lbl_playerMessagesSize))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_playerMessagesSize, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_playerMessagesType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_textStyleLayout.createSequentialGroup()
                        .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_playerNamesType)
                            .addComponent(lbl_playerNamesSize))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_playerNamesSize, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_playerNamesType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnl_textStyleLayout.setVerticalGroup(
            pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textStyleLayout.createSequentialGroup()
                .addComponent(lbl_playerNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerNamesType)
                    .addComponent(cmb_playerNamesType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerNamesSize)
                    .addComponent(tf_playerNamesSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_playerMessages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerMessagesType)
                    .addComponent(cmb_playerMessagesType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_textStyleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_playerMessagesSize)
                    .addComponent(tf_playerMessagesSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_preview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnl_textColors.setBorder(javax.swing.BorderFactory.createTitledBorder("Text colors"));
        pnl_textColors.setLayout(new java.awt.GridBagLayout());

        lbl_yourUsername.setText("Your username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_yourUsername, gridBagConstraints);

        btn_yourUsername.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_yourUsername, gridBagConstraints);

        lbl_otherUsernames.setText("Other usernames:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_otherUsernames, gridBagConstraints);

        btn_otherUsernames.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_otherUsernames, gridBagConstraints);

        lbl_systemMessages.setText("System messages:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_systemMessages, gridBagConstraints);

        btn_systemMessages.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_systemMessages, gridBagConstraints);

        lbl_whisperMessages.setText("Whisper messages:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_whisperMessages, gridBagConstraints);

        btn_whisperMessages.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_whisperMessages, gridBagConstraints);

        lbl_userMessages.setText("User messages:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_userMessages, gridBagConstraints);

        btn_userMessages.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_userMessages, gridBagConstraints);

        cb_colorizeText.setText("Enabled");
        cb_colorizeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_colorizeTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_textColors.add(cb_colorizeText, gridBagConstraints);

        lbl_friendUsernames.setText("Contact usernames:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_friendUsernames, gridBagConstraints);

        btn_friendUsernames.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_friendUsernames, gridBagConstraints);

        lbl_contactMessages.setText("Contact messages:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnl_textColors.add(lbl_contactMessages, gridBagConstraints);

        btn_contactMessages.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnl_textColors.add(btn_contactMessages, gridBagConstraints);

        cb_logActivity.setText("Log user activity");

        cb_timeStamps.setText("Timestamps");

        javax.swing.GroupLayout pnl_textLayout = new javax.swing.GroupLayout(pnl_text);
        pnl_text.setLayout(pnl_textLayout);
        pnl_textLayout.setHorizontalGroup(
            pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_textStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnl_textLayout.createSequentialGroup()
                        .addComponent(cb_timeStamps)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cb_logActivity)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_textColors, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_textLayout.setVerticalGroup(
            pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_textLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_textColors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnl_textLayout.createSequentialGroup()
                        .addComponent(pnl_textStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addGroup(pnl_textLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cb_timeStamps)
                            .addComponent(cb_logActivity))))
                .addContainerGap())
        );

        tabpn_settings.addTab("Text", pnl_text);

        pnl_body.setMaximumSize(getMaximumSize());
        pnl_body.setMinimumSize(getMinimumSize());

        pnl_bodyColors.setBorder(javax.swing.BorderFactory.createTitledBorder("Body colors"));

        lbl_background.setText("Background:");

        lbl_foreground.setText("Foreground:");

        btn_foreground.setText(" ");

        btn_background.setText(" ");

        cb_colorizeBody.setText("Enabled");
        cb_colorizeBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_colorizeBodyActionPerformed(evt);
            }
        });

        lbl_selection.setText("Selection:");

        btn_selection.setText(" ");

        javax.swing.GroupLayout pnl_bodyColorsLayout = new javax.swing.GroupLayout(pnl_bodyColors);
        pnl_bodyColors.setLayout(pnl_bodyColorsLayout);
        pnl_bodyColorsLayout.setHorizontalGroup(
            pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                        .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_foreground)
                            .addComponent(lbl_background)
                            .addComponent(lbl_selection))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_foreground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_selection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_background, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(cb_colorizeBody))
                .addContainerGap())
        );
        pnl_bodyColorsLayout.setVerticalGroup(
            pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyColorsLayout.createSequentialGroup()
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_background)
                    .addComponent(btn_background))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_foreground)
                    .addComponent(lbl_foreground))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_bodyColorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_selection)
                    .addComponent(lbl_selection))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cb_colorizeBody)
                .addContainerGap())
        );

        pnl_lookAndFeel.setBorder(javax.swing.BorderFactory.createTitledBorder("Body style"));

        cmb_style.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_style.setText("Style:");

        cb_nativeStyle.setText("Native style");
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
                        .addComponent(cmb_style, 0, 1, Short.MAX_VALUE))
                    .addComponent(cb_nativeStyle))
                .addContainerGap())
        );
        pnl_lookAndFeelLayout.setVerticalGroup(
            pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_lookAndFeelLayout.createSequentialGroup()
                .addGroup(pnl_lookAndFeelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_style)
                    .addComponent(cmb_style, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cb_nativeStyle)
                .addContainerGap())
        );

        lbl_noteText.setText("<html><table><tr><td><b>Note:</b></td><td>Custom body colors are only supported by the \"Metal\" style.<br>To enable a new style, Coopnet has to be restarted.</html>");

        javax.swing.GroupLayout pnl_bodyLayout = new javax.swing.GroupLayout(pnl_body);
        pnl_body.setLayout(pnl_bodyLayout);
        pnl_bodyLayout.setHorizontalGroup(
            pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyLayout.createSequentialGroup()
                .addGroup(pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_bodyLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnl_lookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnl_bodyColors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnl_bodyLayout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(lbl_noteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnl_bodyLayout.setVerticalGroup(
            pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_bodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_bodyColors, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnl_lookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addComponent(lbl_noteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99))
        );

        tabpn_settings.addTab("Body", pnl_body);

        pnl_quickpanel.setMaximumSize(getMaximumSize());
        pnl_quickpanel.setMinimumSize(getMinimumSize());

        pnl_General.setBorder(javax.swing.BorderFactory.createTitledBorder("General"));

        lbl_quickpanelposition.setText("Position:");

        cmb_QuickPanelPosition.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Left", "Right" }));

        lbl_DividerWidth.setText("Divider width:");

        spn_DividerWidth.setModel(new javax.swing.SpinnerNumberModel(5, 0, 100, 1));
        spn_DividerWidth.setEditor(new javax.swing.JSpinner.NumberEditor(spn_DividerWidth, ""));

        lbl_ToggleButtonWidth.setText("Togglebutton width:");

        spn_ToggleButtonWidth.setModel(new javax.swing.SpinnerNumberModel(5, 0, 100, 1));
        spn_ToggleButtonWidth.setEditor(new javax.swing.JSpinner.NumberEditor(spn_ToggleButtonWidth, ""));

        javax.swing.GroupLayout pnl_GeneralLayout = new javax.swing.GroupLayout(pnl_General);
        pnl_General.setLayout(pnl_GeneralLayout);
        pnl_GeneralLayout.setHorizontalGroup(
            pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_GeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_GeneralLayout.createSequentialGroup()
                        .addComponent(lbl_quickpanelposition)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_QuickPanelPosition, 0, 86, Short.MAX_VALUE))
                    .addGroup(pnl_GeneralLayout.createSequentialGroup()
                        .addComponent(lbl_DividerWidth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spn_DividerWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_GeneralLayout.createSequentialGroup()
                        .addComponent(lbl_ToggleButtonWidth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spn_ToggleButtonWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_GeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_DividerWidth, lbl_ToggleButtonWidth, lbl_quickpanelposition});

        pnl_GeneralLayout.setVerticalGroup(
            pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_GeneralLayout.createSequentialGroup()
                .addGroup(pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_quickpanelposition, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_QuickPanelPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_DividerWidth)
                    .addComponent(spn_DividerWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_GeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_ToggleButtonWidth)
                    .addComponent(spn_ToggleButtonWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        lbl_noteText1.setText("<html><table><tr><td><b>Note:</b></td><td>Changes to general settings take effect after restarting Coopnet.</html>");

        pnl_ContactList.setBorder(javax.swing.BorderFactory.createTitledBorder("Contact List"));

        lbl_StatusChangeNotification.setText("Contact status-change notifications:");

        cb_TextNotification.setText("Text");

        cb_SoundNotification.setText("Sound");

        cb_showOfflineContacts.setText("Show offline contacts");

        javax.swing.GroupLayout pnl_ContactListLayout = new javax.swing.GroupLayout(pnl_ContactList);
        pnl_ContactList.setLayout(pnl_ContactListLayout);
        pnl_ContactListLayout.setHorizontalGroup(
            pnl_ContactListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ContactListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_ContactListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cb_showOfflineContacts)
                    .addComponent(lbl_StatusChangeNotification)
                    .addGroup(pnl_ContactListLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(cb_TextNotification)
                        .addGap(18, 18, 18)
                        .addComponent(cb_SoundNotification)))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        pnl_ContactListLayout.setVerticalGroup(
            pnl_ContactListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ContactListLayout.createSequentialGroup()
                .addComponent(cb_showOfflineContacts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_StatusChangeNotification)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ContactListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cb_TextNotification)
                    .addComponent(cb_SoundNotification))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_quickpanelLayout = new javax.swing.GroupLayout(pnl_quickpanel);
        pnl_quickpanel.setLayout(pnl_quickpanelLayout);
        pnl_quickpanelLayout.setHorizontalGroup(
            pnl_quickpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_quickpanelLayout.createSequentialGroup()
                .addGroup(pnl_quickpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_quickpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnl_General, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnl_ContactList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_quickpanelLayout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(lbl_noteText1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnl_quickpanelLayout.setVerticalGroup(
            pnl_quickpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_quickpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_quickpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_General, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_ContactList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_noteText1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(143, 143, 143))
        );

        tabpn_settings.addTab("QuickPanel", pnl_quickpanel);

        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicked(evt);
            }
        });

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
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
                .addComponent(btn_close)
                .addContainerGap())
            .addComponent(tabpn_settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabpn_settings, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_close)
                    .addComponent(btn_apply))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_apply, btn_close, btn_save});

        tabpn_settings.getAccessibleContext().setAccessibleName("General");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicked
        //save the data
        saveSettings();
        FrameOrganizer.closeSettingsFrame();
    }//GEN-LAST:event_clicked

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        FrameOrganizer.closeSettingsFrame();
    }//GEN-LAST:event_cancel

    private void btn_apply_ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_apply_ActionPerformed
        saveSettings();
}//GEN-LAST:event_btn_apply_ActionPerformed

    private void btn_browseReceiveDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browseReceiveDirActionPerformed
        new ErrThread() {

            @Override
            public void handledRun() throws Throwable {
                File inputfile = null;
                FileChooser mfc = new FileChooser(FileChooser.DIRECTORIES_ONLY_MODE);
                int returnVal = mfc.choose(Globals.getLastOpenedDir());

                if (returnVal == FileChooser.SELECT_ACTION) {
                    inputfile = mfc.getSelectedFile();
                    if (inputfile != null) {
                        tf_receiveDir.setText(inputfile.getPath());
                    }
                }
            }
        }.start();
}//GEN-LAST:event_btn_browseReceiveDirActionPerformed

    //Enables or disables the corresponding buttons of Text Colors
    private void toggleItemsOf_cb_ColorizeText() {
        btn_yourUsername.setEnabled(cb_colorizeText.isSelected());
        btn_otherUsernames.setEnabled(cb_colorizeText.isSelected());
        btn_userMessages.setEnabled(cb_colorizeText.isSelected());
        btn_systemMessages.setEnabled(cb_colorizeText.isSelected());
        btn_whisperMessages.setEnabled(cb_colorizeText.isSelected());
        btn_friendUsernames.setEnabled(cb_colorizeText.isSelected());
        btn_contactMessages.setEnabled(cb_colorizeText.isSelected());
    }

private void cb_colorizeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_colorizeTextActionPerformed
    toggleItemsOf_cb_ColorizeText();
}//GEN-LAST:event_cb_colorizeTextActionPerformed

    private void toggleItemsOf_cb_ColorizeBody() {
        btn_foreground.setEnabled(cb_colorizeBody.isSelected());
        btn_background.setEnabled(cb_colorizeBody.isSelected());
        btn_selection.setEnabled(cb_colorizeBody.isSelected());
    }

private void cb_colorizeBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_colorizeBodyActionPerformed
    toggleItemsOf_cb_ColorizeBody();
}//GEN-LAST:event_cb_colorizeBodyActionPerformed

    private void toggleItemsOf_cb_NativeStyle() {
        if (cb_nativeStyle.isSelected()) {
            cb_colorizeBody.setSelected(false);
            toggleItemsOf_cb_ColorizeBody();

            //Set selection to current LAF
            UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < infos.length; i++) {
                if (UIManager.getSystemLookAndFeelClassName().equals(infos[i].getClassName())) {
                    cmb_style.setSelectedItem(infos[i].getName());
                }
            }
        } else {
            //Set selection to current LAF
            UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < infos.length; i++) {
                if (UIManager.getLookAndFeel().getClass().getName().equals(infos[i].getClassName())) {
                    cmb_style.setSelectedItem(infos[i].getName());
                }
            }
        }

        cmb_style.setEnabled(!cb_nativeStyle.isSelected());
        if (Colorizer.isCurrentLafSupportedForColoring()) {
            cb_colorizeBody.setEnabled(!cb_nativeStyle.isSelected());
        } else {
            cb_colorizeBody.setEnabled(false);
        }
    }

    private void addKeyGrabberUnfocusMouseListener() {
        MouseListener ml = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (getFocusOwner() instanceof KeyGrabberTextField) {
                    Component owner = getFocusOwner();
                    owner.setFocusable(false);
                    owner.setFocusable(true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };

        this.addMouseListener(ml);
        pnl_hotkeys.addMouseListener(ml);
        tabpn_settings.addMouseListener(ml);
        pnl_general.addMouseListener(ml); //current location of the component
    }

    private void saveSettings() {
        boolean error = false;

        try {
            Settings.setLaunchHotKey(tf_launchKey.getKey());
            Settings.setLaunchHotKeymask(tf_launchKey.getModifiers());

            Settings.setQuickPanelPostionisLeft(cmb_QuickPanelPosition.getSelectedIndex() == 0);
            Settings.setQuickPanelDividerWidth(Integer.valueOf(spn_DividerWidth.getValue().toString()));
            Settings.setQuickPanelToggleBarWidth(Integer.valueOf(spn_ToggleButtonWidth.getValue().toString()));

            Settings.setContactStatusChangeTextNotification(cb_TextNotification.isSelected());
            Settings.setContactStatusChangeSoundNotification(cb_SoundNotification.isSelected());

            if (cb_autoLogin.isSelected()) {
                Settings.setAutoLogin(true);
            } else {
                Settings.setAutoLogin(false);
                Settings.setLastLoginPassword("");
            }

            Settings.setSoundEnabled(cb_sounds.isSelected());
            Settings.setTrayIconEnabled(cb_TrayIconEnabled.isSelected());
            Settings.setTimeStampEnabled(cb_timeStamps.isSelected());

            if (cb_sleepMode.isSelected() != Settings.getSleepEnabled()) {
                Settings.setSleepenabled(cb_sleepMode.isSelected());
                Protocol.setSleep(cb_sleepMode.isSelected());
            }
            Settings.setRecieveDestination(tf_receiveDir.getText());
            Settings.setHomeChannel(cmb_homeChannel.getSelectedItem().toString());
            Settings.setFiletTansferPort(new Integer(tf_transferPort.getText()));

            //Colors
            Settings.setBackgroundColor(btn_background.getForeground());
            Settings.setForegroundColor(btn_foreground.getForeground());

            Settings.setYourUsernameColor(btn_yourUsername.getForeground());
            Settings.setOtherUsernamesColor(btn_otherUsernames.getForeground());
            Settings.setFriendUsernameColor(btn_friendUsernames.getForeground());
            Settings.setFriendMessageColor(btn_contactMessages.getForeground());
            Settings.setSystemMessageColor(btn_systemMessages.getForeground());
            Settings.setWhisperMessageColor(btn_whisperMessages.getForeground());
            Settings.setUserMessageColor(btn_userMessages.getForeground());
            Settings.setSelectionColor(btn_selection.getForeground());

            Settings.setSelectedLookAndFeel((String) cmb_style.getSelectedItem());
            Settings.setUseNativeLookAndFeel(cb_nativeStyle.isSelected());

            if (Colorizer.isLafSupportedForColoring(cmb_style.getSelectedItem().toString())) {
                Settings.setColorizeBody(cb_colorizeBody.isSelected());
            } else {
                Settings.setColorizeBody(false);
            }
            Settings.setColorizeText(cb_colorizeText.isSelected());

            Settings.setNameStyle(cmb_playerNamesType.getSelectedItem().toString());
            Settings.setNameSize(Integer.parseInt(tf_playerNamesSize.getText()));
            Settings.setMessageStyle(cmb_playerMessagesType.getSelectedItem().toString());
            Settings.setMessageSize(Integer.parseInt(tf_playerMessagesSize.getText()));

            Settings.setShowOfflineContacts(cb_showOfflineContacts.isSelected());

            Settings.setMultiChannel(cb_multiChannel.isSelected());

            Settings.setRememberMainFrameSize(cb_rememberMainFrameSize.isSelected());
            Settings.setLogUserActivity(cb_logActivity.isSelected());

            Globals.updateSettings();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(),
                    "Please verify that you have entered valid information!"
                    + "\nFor example:\n  Serverport and textsizes need to be non-decimal numbers.",
                    "Wrong input", JOptionPane.ERROR_MESSAGE);
            error = true;
        }

        if (!error) {
            new EdtRunner() {

                @Override
                public void handledRun() throws Throwable {
                    Colors.init();
                    FrameOrganizer.recolorFrames();
                    TabOrganizer.updateStyle();
                }
            }.invokeLater();
        }

        requestFocus(); //somehow clientframe steals focus
    }

private void cb_nativeStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_nativeStyleActionPerformed
    toggleItemsOf_cb_NativeStyle();
}//GEN-LAST:event_cb_nativeStyleActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    FrameOrganizer.closeSettingsFrame();
}//GEN-LAST:event_formWindowClosing

private void tf_receiveDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_receiveDirActionPerformed
    if (!Verification.verifyDirectory(tf_receiveDir.getText())) {
        tf_receiveDir.showErrorMessage(INVALID_DIRECTORY);
    }
}//GEN-LAST:event_tf_receiveDirActionPerformed

private void tf_receiveDirFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tf_receiveDirFocusLost
    if (!Verification.verifyDirectory(tf_receiveDir.getText())) {
        tf_receiveDir.showErrorMessage(INVALID_DIRECTORY);
    }
}//GEN-LAST:event_tf_receiveDirFocusLost

private void cmb_playerNamesTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_playerNamesTypeActionPerformed
    lbl_preview_username.setFont(new Font(
            cmb_playerNamesType.getSelectedItem().toString(),
            Font.PLAIN,
            Integer.valueOf(tf_playerNamesSize.getText())));
}//GEN-LAST:event_cmb_playerNamesTypeActionPerformed

private void tf_playerNamesSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_playerNamesSizeActionPerformed
    lbl_preview_username.setFont(new Font(
            cmb_playerNamesType.getSelectedItem().toString(),
            Font.PLAIN,
            Integer.valueOf(tf_playerNamesSize.getText())));
}//GEN-LAST:event_tf_playerNamesSizeActionPerformed

private void cmb_playerMessagesTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_playerMessagesTypeActionPerformed
    lbl_preview_messagetext.setFont(new Font(
            cmb_playerMessagesType.getSelectedItem().toString(),
            Font.PLAIN,
            Integer.valueOf(tf_playerMessagesSize.getText())));
}//GEN-LAST:event_cmb_playerMessagesTypeActionPerformed

private void tf_playerMessagesSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_playerMessagesSizeActionPerformed
    lbl_preview_messagetext.setFont(new Font(
            cmb_playerMessagesType.getSelectedItem().toString(),
            Font.PLAIN,
            Integer.valueOf(tf_playerMessagesSize.getText())));
}//GEN-LAST:event_tf_playerMessagesSizeActionPerformed

    private void btn_DplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DplayActionPerformed
        synchronized (LOCK) {
            if (testiInprogress) {
                Logger.log("Connection test already in progress");
                return;
            }
        }
        //warning if a game is running
        if (Launcher.isPlaying()) {
            int ret = JOptionPane.showConfirmDialog(FrameOrganizer.getClientFrame(),
                    "A game is currently running. The connection test might interfere with it.\n Do you want to proceed with the test?",
                    "Game running", JOptionPane.WARNING_MESSAGE);
            if (ret == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        testiInprogress = true;
        lbl_connectionTestResult.setIcon(Icons.pendingContactIcon);
        lbl_connectionTestResult.setToolTipText("Working...");
        setConnTestProgress(0);

        final List<Integer> result = new ArrayList<Integer>();
        final Runnable task = new Runnable() {

            @Override
            public void run() {
                if (result.isEmpty()) {
                    lbl_connectionTestResult.setIcon(Icons.acceptIcon);
                    lbl_connectionTestResult.setToolTipText("Test passed!.");
                    Logger.log("ConnectionTest passed");
                } else {
                    lbl_connectionTestResult.setIcon(Icons.refuseIcon);
                    lbl_connectionTestResult.setToolTipText("Test failed on port " + result.get(0));
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(),
                            "Connection test failed on the following port: " + result.get(0) + "\nPlease ensure that the port is forwarded in the router\nallowed in the firewall, and no other application is using it!",
                            "Connection test failed", JOptionPane.ERROR_MESSAGE);
                    Logger.log("ConnectionTest failed on ports:" + result.toString());
                }
                testiInprogress = false;
                setConnTestProgress(0);
            }
        };

        new Thread() {

            public void run() {
                List<Integer> ports = new ArrayList<Integer>();
                if (rb_dplay.isSelected()) {
                    //DPlay ports: 2300-2400, 47624,6073 TCP and UDP
                    ports.add(47624);
                    ports.add(6073);
                    for (int i = 2300; i <= 2400; i++) {
                        ports.add(i);
                    }
                } else {
                    String input = tf_CustomePort.getText();
                    //validate port numbers,add them to port list
                    ports.addAll(parsePortList(input));
                }

                String[] portStrings = new String[ports.size()];
                for (int i = 0; i < ports.size(); i++) {
                    portStrings[i] = String.valueOf(ports.get(i));
                }
                String key = cmb_connectionTestIPList.getSelectedItem().toString();
                String IP = Globals.getInterfaceIPMap().get(key);
                Protocol.sendConnectionTestRequest(IP, portStrings);
                result.addAll(testConnection(ports));
                SwingUtilities.invokeLater(task);
            }
        }.start();
    }//GEN-LAST:event_btn_DplayActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_ConnectionTestButtonGroup;
    private javax.swing.JButton btn_Dplay;
    private javax.swing.JButton btn_apply;
    private javax.swing.JButton btn_background;
    private javax.swing.JButton btn_browseReceiveDir;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_contactMessages;
    private javax.swing.JButton btn_foreground;
    private javax.swing.JButton btn_friendUsernames;
    private javax.swing.JButton btn_otherUsernames;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_selection;
    private javax.swing.JButton btn_systemMessages;
    private javax.swing.JButton btn_userMessages;
    private javax.swing.JButton btn_whisperMessages;
    private javax.swing.JButton btn_yourUsername;
    private javax.swing.JCheckBox cb_SoundNotification;
    private javax.swing.JCheckBox cb_TextNotification;
    private javax.swing.JCheckBox cb_TrayIconEnabled;
    private javax.swing.JCheckBox cb_autoLogin;
    private javax.swing.JCheckBox cb_colorizeBody;
    private javax.swing.JCheckBox cb_colorizeText;
    private javax.swing.JComboBox cb_connectionType;
    private javax.swing.JCheckBox cb_logActivity;
    private javax.swing.JCheckBox cb_multiChannel;
    private javax.swing.JCheckBox cb_nativeStyle;
    private javax.swing.JCheckBox cb_rememberMainFrameSize;
    private javax.swing.JCheckBox cb_showOfflineContacts;
    private javax.swing.JCheckBox cb_sleepMode;
    private javax.swing.JCheckBox cb_sounds;
    private javax.swing.JCheckBox cb_timeStamps;
    private javax.swing.JComboBox cmb_QuickPanelPosition;
    private javax.swing.JComboBox cmb_connectionTestIPList;
    private javax.swing.JComboBox cmb_homeChannel;
    private javax.swing.JComboBox cmb_playerMessagesType;
    private javax.swing.JComboBox cmb_playerNamesType;
    private javax.swing.JComboBox cmb_style;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbl_ConnectionTestIP;
    private javax.swing.JLabel lbl_DividerWidth;
    private javax.swing.JLabel lbl_StatusChangeNotification;
    private javax.swing.JLabel lbl_ToggleButtonWidth;
    private javax.swing.JLabel lbl_background;
    private javax.swing.JLabel lbl_connectionTestResult;
    private javax.swing.JLabel lbl_contactMessages;
    private javax.swing.JLabel lbl_foreground;
    private javax.swing.JLabel lbl_friendUsernames;
    private javax.swing.JLabel lbl_homeChannel;
    private javax.swing.JLabel lbl_hotkeyNoteText;
    private javax.swing.JLabel lbl_launchKey;
    private javax.swing.JLabel lbl_noteText;
    private javax.swing.JLabel lbl_noteText1;
    private javax.swing.JLabel lbl_otherUsernames;
    private javax.swing.JLabel lbl_playerMessages;
    private javax.swing.JLabel lbl_playerMessagesSize;
    private javax.swing.JLabel lbl_playerMessagesType;
    private javax.swing.JLabel lbl_playerNames;
    private javax.swing.JLabel lbl_playerNamesSize;
    private javax.swing.JLabel lbl_playerNamesType;
    private javax.swing.JLabel lbl_preview_messagetext;
    private javax.swing.JLabel lbl_preview_username;
    private javax.swing.JLabel lbl_progress;
    private javax.swing.JLabel lbl_quickpanelposition;
    private javax.swing.JLabel lbl_receiveDir;
    private javax.swing.JLabel lbl_selection;
    private javax.swing.JLabel lbl_style;
    private javax.swing.JLabel lbl_systemMessages;
    private javax.swing.JLabel lbl_transferPort;
    private javax.swing.JLabel lbl_userMessages;
    private javax.swing.JLabel lbl_whisperMessages;
    private javax.swing.JLabel lbl_yourUsername;
    private javax.swing.JProgressBar pb_ConnectionTestProgress;
    private javax.swing.JPanel pnl_ContactList;
    private javax.swing.JPanel pnl_General;
    private javax.swing.JPanel pnl_body;
    private javax.swing.JPanel pnl_bodyColors;
    private javax.swing.JPanel pnl_connection_test;
    private javax.swing.JPanel pnl_general;
    private javax.swing.JPanel pnl_hotkeys;
    private javax.swing.JPanel pnl_lookAndFeel;
    private javax.swing.JPanel pnl_network;
    private javax.swing.JPanel pnl_preview;
    private javax.swing.JPanel pnl_quickpanel;
    private javax.swing.JPanel pnl_text;
    private javax.swing.JPanel pnl_textColors;
    private javax.swing.JPanel pnl_textStyle;
    private javax.swing.JRadioButton rb_custome;
    private javax.swing.JRadioButton rb_dplay;
    private javax.swing.JSpinner spn_DividerWidth;
    private javax.swing.JSpinner spn_ToggleButtonWidth;
    private javax.swing.JTabbedPane tabpn_settings;
    private javax.swing.JTextField tf_CustomePort;
    private coopnetclient.frames.components.KeyGrabberTextField tf_launchKey;
    private javax.swing.JTextField tf_playerMessagesSize;
    private javax.swing.JTextField tf_playerNamesSize;
    private coopnetclient.frames.components.ValidatorJTextField tf_receiveDir;
    private javax.swing.JTextField tf_transferPort;
    // End of variables declaration//GEN-END:variables
}
