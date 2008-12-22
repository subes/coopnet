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
import coopnetclient.enums.MapLoaderTypes;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.TempGameSettings;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class GameSettingsFrame extends javax.swing.JFrame {

    private ArrayList<JLabel> labels = new ArrayList<JLabel>();
    private ArrayList<Component> inputfields = new ArrayList<Component>();
    private String gamename;
    private String modname;
    private String roomname,  password;
    private int modindex,  maxPlayers;
    private boolean compatible,  isInstant,  isHost;

    /** Creates new form GameSettingsPanel */
    public GameSettingsFrame(String gamename, String modname, boolean isHost) {
        initComponents();
        this.gamename = gamename;
        this.modname = modname;
        this.isHost = isHost;
        isInstant = false;
        lbl_map.setVisible(false);
        cb_map.setVisible(false);
        customize();
        this.getRootPane().setDefaultButton(btn_save);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_close.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    /** Creates new form GameSettingsPanel */
    public GameSettingsFrame(String gamename, String modname, String roomname, String password, int modindex, int maxPlayers, boolean compatible) {
        initComponents();
        isInstant = true;
        isHost = true;
        this.gamename = gamename;
        this.modname = modname;
        this.roomname = roomname;
        this.password = password;
        this.modindex = modindex;
        this.maxPlayers = maxPlayers;
        this.compatible = compatible;
        btn_save.setText("Launch");
        customize();
        this.getRootPane().setDefaultButton(btn_save);
        AbstractAction act = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btn_close.doClick();
            }
        };
        getRootPane().getActionMap().put("close", act);
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        pack();
    }

    private void customize() {
        //setup map if needed
        if (GameDatabase.getMapExtension(gamename, modname) != null) {
            lbl_map.setVisible(true);
            cb_map.setVisible(true);
            if (GameDatabase.getMapLoaderType(gamename, modname) == MapLoaderTypes.FILE) {
                cb_map.setModel(new DefaultComboBoxModel(loadFileMaps()));
            } else if (GameDatabase.getMapLoaderType(gamename, modname) == MapLoaderTypes.PK3) {
                cb_map.setModel(new DefaultComboBoxModel(loadPK3Maps()));
            }

            cb_map.setSelectedItem(TempGameSettings.getMap());

            if (cb_map.getSelectedItem() == null && cb_map.getItemCount() > 0) {
                cb_map.setSelectedIndex(0);
            }
            cb_map.setEnabled(isHost);
        }
        //add setting components to frame and internal lists
        GridBagConstraints firstcolumn = new GridBagConstraints();
        GridBagConstraints secondcolumn = new GridBagConstraints();
        int serverrowindex = 1;
        int localrowindex = 0;
        int localcount = 0;
        ArrayList<GameSetting> settings = GameDatabase.getGameSettings(gamename, modname);
        //setup constraints
        firstcolumn.gridwidth = 1;
        firstcolumn.gridheight = 1;
        firstcolumn.fill = GridBagConstraints.NONE;
        firstcolumn.ipadx = 40;
        firstcolumn.anchor = GridBagConstraints.EAST;
        firstcolumn.weightx = 0;
        firstcolumn.weighty = 0;
        firstcolumn.insets = new Insets(5, 5, 5, 5);
        firstcolumn.gridx = 0;
        secondcolumn.gridwidth = 1;
        secondcolumn.gridheight = 1;
        secondcolumn.fill = GridBagConstraints.HORIZONTAL;
        secondcolumn.ipadx = 0;
        secondcolumn.anchor = GridBagConstraints.CENTER;
        secondcolumn.weightx = 1.0;
        secondcolumn.weighty = 0;
        secondcolumn.insets = new Insets(5, 5, 5, 5);
        secondcolumn.gridx = 1;

        //add each setting
        for (GameSetting gs : settings) {
            if (gs.isLocal()) {
                localcount++;
                firstcolumn.gridy = localrowindex;
                secondcolumn.gridy = localrowindex;
            } else {
                firstcolumn.gridy = serverrowindex;
                secondcolumn.gridy = serverrowindex;
            }

            JLabel label = new JLabel(gs.getName());
            label.setHorizontalAlignment(JLabel.RIGHT);
            Component input = null;
            switch (gs.getType()) {
                case TEXT: {
                    input = new JTextField(gs.getDefaultValue());
                    String currentValue = TempGameSettings.getGameSettingValue(gs.getName());
                    if (currentValue != null && currentValue.length() > 0) {
                        ((JTextField) input).setText(currentValue);
                    }
                    if (!isHost && !gs.isLocal()) {
                        input.setEnabled(false);
                    }
                    break;
                }
                case NUMBER: {
                    int def = 0;
                    def = Integer.valueOf((gs.getDefaultValue() == null || gs.getDefaultValue().length() == 0) ? "0" : gs.getDefaultValue());
                    int min = Integer.valueOf(gs.getMinValue());
                    int max = Integer.valueOf(gs.getMaxValue());
                    if (min <= max && min <= def && def <= max) {
                        input = new JSpinner(new SpinnerNumberModel(def, min, max, 1));
                    } else {
                        input = new JSpinner();
                    }
                    String currentValue = TempGameSettings.getGameSettingValue(gs.getName());
                    if (currentValue != null && currentValue.length() > 0) {
                        ((JSpinner) input).setValue(Integer.valueOf(currentValue));
                    }
                    if (!isHost && !gs.isLocal()) {
                        input.setEnabled(false);
                    }
                    break;
                }
                case CHOICE: {
                    if (!isHost && !gs.isLocal()) {
                        input = new JTextField(gs.getDefaultValue());
                        String currentValue = TempGameSettings.getGameSettingValue(gs.getName());
                        if (gs.getDefaultValue() != null && gs.getDefaultValue().length() > 0) {
                            ((JTextField) input).setText(gs.getDefaultValue());
                        }
                        if (currentValue != null && currentValue.length() > 0) {
                            ((JTextField) input).setText(currentValue);
                        }
                        input.setEnabled(false);
                    } else {
                        input = new JComboBox(gs.getComboboxSelectNames().toArray());
                        if (gs.getDefaultValue() != null && gs.getDefaultValue().length() > 0) {
                            int idx = -1;
                            idx = gs.getComboboxSelectNames().indexOf(gs.getDefaultValue());
                            if (idx > -1) {
                                ((JComboBox) input).setSelectedIndex(idx);
                            }
                        }
                        String currentValue = TempGameSettings.getGameSettingValue(gs.getName());
                        if (currentValue != null && currentValue.length() > 0) {
                            ((JComboBox) input).setSelectedItem(currentValue);
                        }
                    }
                    break;
                }

            }
            //add items to internal array
            labels.add(label);
            inputfields.add(input);
            //set focust traverse path
            if(inputfields.size() == 1){ //first component
                //cb_map.setn
            }else{

            }

            //add to panel
            if (gs.isLocal()) {
                pnl_localSettings.add(label, firstcolumn);
                pnl_localSettings.add(input, secondcolumn);
                localrowindex++;
            } else {
                pnl_serverSettings.add(label, firstcolumn);
                pnl_serverSettings.add(input, secondcolumn);
                serverrowindex++;
            }
        }
        if (localcount == 0) {
            pnl_localSettings.setVisible(false);
        }
    }

    private String[] loadFileMaps() {
        String extension = GameDatabase.getMapExtension(gamename, modname);
        String path = GameDatabase.getFullMapPath(gamename, modname);
        System.out.println("loading maps from: " + path);
        if (path.endsWith("\\") || path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        File mapdir = new File(path);
        if (!mapdir.isDirectory()) {
            return new String[0];
        }
        File files[] = mapdir.listFiles();
        Vector<String> names = new Vector<String>();
        for (File f : files) {
            if (f.isFile()) {
                String tmp = f.getName();
                if (tmp.endsWith(extension)) {
                    names.add(tmp.substring(0, tmp.length() - (extension.length() + 1)));
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

    private ArrayList<File> getPK3Files(ArrayList<File> list, File baseDir) {
        for (File f : baseDir.listFiles()) {
            if (f.isDirectory()) {
                getPK3Files(list, f);
            } else {
                if (f.getName().endsWith("pk3") || f.getName().endsWith("pk4")) {
                    list.add(f);
                }
            }
        }
        return list;
    }

    private String getMapNameFromEntry(String entry) {
        Pattern pattern = Pattern.compile(GameDatabase.getMapPath(gamename, modname).replace('\\', '/') + "([\\p{Alnum}\\p{Punct}&&[^/\\\\]]+)\\." + GameDatabase.getMapExtension(gamename, modname));
        Matcher matcher = pattern.matcher(entry);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    private String[] loadPK3Maps() {
        String pk3FindPath = GameDatabase.getInstallPath(gamename) + GameDatabase.getPK3FindPath(gamename, modname);
        Vector<String> names = new Vector<String>();
        ArrayList<File> pk3Files = new ArrayList<File>();
        getPK3Files(pk3Files, new File(pk3FindPath));
        for (File pk3File : pk3Files) {
            try {
                // Open the ZIP file
                ZipFile zf = new ZipFile(pk3File);
                // Enumerate each entry
                for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                    // Get the entry name
                    String zipEntryName = ((ZipEntry) entries.nextElement()).getName();
                    //if is a map, add to mapnames
                    String mapFileName = getMapNameFromEntry(zipEntryName);
                    if (mapFileName != null) {
                        names.add(mapFileName);
                    }
                }
            } catch (IOException e) {
            }
        }

        return names.toArray(new String[names.size()]);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btn_save = new javax.swing.JButton();
        pnl_serverSettings = new javax.swing.JPanel();
        lbl_map = new javax.swing.JLabel();
        cb_map = new javax.swing.JComboBox();
        pnl_localSettings = new javax.swing.JPanel();
        btn_close = new javax.swing.JButton();

        setTitle("Game settings");
        setFocusTraversalPolicyProvider(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_save.setText("Save");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        pnl_serverSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Server settings"));
        pnl_serverSettings.setLayout(new java.awt.GridBagLayout());

        lbl_map.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_map.setText("Map:");
        lbl_map.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_serverSettings.add(lbl_map, gridBagConstraints);

        cb_map.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_serverSettings.add(cb_map, gridBagConstraints);

        pnl_localSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Local settings"));
        pnl_localSettings.setLayout(new java.awt.GridBagLayout());

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
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
                .addComponent(btn_close)
                .addGap(318, 318, 318))
            .addComponent(pnl_serverSettings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
            .addComponent(pnl_localSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_close, btn_save});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_serverSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_localSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_save)
                    .addComponent(btn_close))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_close, btn_save});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
    //update the launcher
    try {
        //if somethings unselected an exception is thrown        
        if (cb_map.isVisible()) {
            if (cb_map.getSelectedItem() != null && cb_map.isEnabled()) {
                TempGameSettings.setMap(cb_map.getSelectedItem().toString());
                Protocol.sendSetting("map", cb_map.getSelectedItem().toString());
            }
        }
        //save settings
        String name, value = "save-error";
        for (int i = 0; i < labels.size(); i++) {
            name = labels.get(i).getText();
            Component input = inputfields.get(i);
            if (input instanceof JTextField) {
                value = ((JTextField) input).getText();
            } else if (input instanceof JSpinner) {
                value = ((JSpinner) input).getValue() + "";
            } else if (input instanceof JComboBox) {
                value = ((JComboBox) input).getSelectedItem().toString();
            }
            if (input.isEnabled()) {
                TempGameSettings.setGameSetting(name, value, isHost);
            }
        }

        if (!isInstant) {
            TabOrganizer.getRoomPanel().initDone();
        }
        Globals.closeGameSettingsFrame();
    } catch (Exception e) {
        e.printStackTrace();
    }
    if (btn_save.getText().equals("Launch")) {
        Protocol.createRoom(gamename, roomname, modindex, password, maxPlayers, compatible, true);
        Globals.closeRoomCreationFrame();
        new Thread() {

            @Override
            public void run() {
                try {
                    Client.instantLaunch(gamename);
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }

}//GEN-LAST:event_btn_saveActionPerformed

    public void updateValues() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                cb_map.setSelectedItem(TempGameSettings.getMap());
                for (int i = 0; i < labels.size(); i++) {
                    String name = labels.get(i).getText();
                    Component input = inputfields.get(i);
                    String value = TempGameSettings.getGameSettingValue(name);
                    if (value != null && value.length() > 0) {
                        if (input instanceof JTextField) {
                            ((JTextField) input).setText(value);
                        } else if (input instanceof JSpinner) {
                            ((JSpinner) input).setValue(value);
                        } else if (input instanceof JComboBox) {
                            ((JComboBox) input).setSelectedItem(value);
                        }
                    }
                }
            }
        });
    }

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeGameSettingsFrame();
}//GEN-LAST:event_formWindowClosing

private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
    Globals.closeGameSettingsFrame();
}//GEN-LAST:event_btn_closeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox cb_map;
    private javax.swing.JLabel lbl_map;
    private javax.swing.JPanel pnl_localSettings;
    private javax.swing.JPanel pnl_serverSettings;
    // End of variables declaration//GEN-END:variables
}
