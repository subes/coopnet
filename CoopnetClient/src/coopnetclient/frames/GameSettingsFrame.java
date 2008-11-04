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
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.TempGameSettings;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class GameSettingsFrame extends javax.swing.JFrame {

    private ArrayList<JLabel> labels = new ArrayList<JLabel>();
    private ArrayList<Component> inputfields = new ArrayList<Component>();
    private String gamename;
    private String modname;
    private String roomname,  password;
    private int modindex,  maxPlayers;
    private boolean compatible,  isInstant;

    /** Creates new form GameSettingsPanel */
    public GameSettingsFrame(String gamename, String modname) {
        initComponents();
        this.gamename = gamename;
        this.modname = modname;
        lbl_map.setVisible(false);
        cb_map.setVisible(false);
        customize();
    }

    /** Creates new form GameSettingsPanel */
    public GameSettingsFrame(String gamename, String modname, String roomname, String password, int modindex, int maxPlayers, boolean compatible) {
        initComponents();
        isInstant = true;
        this.gamename = gamename;
        this.modname = modname;
        this.roomname = roomname;
        this.password = password;
        this.modindex = modindex;
        this.maxPlayers = maxPlayers;
        this.compatible = compatible;
        btn_save.setText("Launch");
        customize();
        pack();
    }

    private void customize() {
        //setup map if needed
        if (GameDatabase.getMapExtension(gamename, modname) != null) {
            lbl_map.setVisible(true);
            cb_map.setVisible(true);
            cb_map.setModel(new DefaultComboBoxModel(loadMaps()));
            cb_map.setSelectedItem(TempGameSettings.getMap());            
            
            if(cb_map.getSelectedItem() == null && cb_map.getItemCount() > 0){
                cb_map.setSelectedIndex(0);
            }
        }
        //add setting components to frame and internal lists
        GridBagConstraints firstcolumn = new GridBagConstraints();
        GridBagConstraints secondcolumn = new GridBagConstraints();
        int rowindex = 1;
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
            firstcolumn.gridy = rowindex;
            secondcolumn.gridy = rowindex;

            JLabel label = new JLabel(gs.getName());
            label.setHorizontalAlignment(JLabel.RIGHT);
            Component input = null;
            switch (gs.getType()) {
                case TEXT: {
                    input = new JTextField(gs.getDefaultValue());
                    String currentValue = TempGameSettings.getGameSetting(gs.getName());
                    if( currentValue != null && currentValue.length()>0 ){
                        ((JTextField)input).setText(currentValue);
                    }
                    break;
                }
                case NUMBER: {
                    int def = 0;
                    def= Integer.valueOf((gs.getDefaultValue()==null||gs.getDefaultValue().length() == 0)?"0":gs.getDefaultValue());
                    int min = Integer.valueOf(gs.getMinValue());
                    int max = Integer.valueOf(gs.getMaxValue());
                    if(min <= max && min <= def && def <= max){
                        input = new JSpinner(new SpinnerNumberModel(def, min, max, 1));
                    }else{
                         input = new JSpinner();
                    }
                    String currentValue = TempGameSettings.getGameSetting(gs.getName());
                    if( currentValue != null && currentValue.length()>0 ){
                        ((JSpinner)input).setValue(Integer.valueOf(currentValue));
                    }
                    break;
                }
                case CHOICE: {
                    input = new JComboBox(gs.getComboboxSelectNames().toArray());
                    if (gs.getDefaultValue() != null && gs.getDefaultValue().length() > 0) {
                        int idx = -1;
                        idx = gs.getComboboxSelectNames().indexOf(gs.getDefaultValue());
                        if (idx > -1) {
                            ((JComboBox) input).setSelectedIndex(idx);
                        }
                    }
                    String currentValue = TempGameSettings.getGameSetting(gs.getName());
                    if( currentValue != null && currentValue.length()>0 ){
                        ((JComboBox)input).setSelectedItem(currentValue);
                    }
                    break;
                }

            }
            //add items to internal array
            labels.add(label);
            inputfields.add(input);
            //add to panel
            pnl_settings.add(label, firstcolumn);
            pnl_settings.add(input, secondcolumn);
            rowindex++;
        }
    }

    private String[] loadMaps() {
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
            String tmp = f.getName();
            if (tmp.endsWith(extension));
            names.add(tmp);
        }
        return names.toArray(new String[0]);
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
        pnl_settings = new javax.swing.JPanel();
        lbl_map = new javax.swing.JLabel();
        cb_map = new javax.swing.JComboBox();

        setTitle("Game settings");
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

        pnl_settings.setBorder(javax.swing.BorderFactory.createTitledBorder("Game settings"));
        pnl_settings.setLayout(new java.awt.GridBagLayout());

        lbl_map.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_map.setText("Map:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_settings.add(lbl_map, gridBagConstraints);

        cb_map.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnl_settings.add(cb_map, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_settings, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_save)
                .addContainerGap(395, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_settings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_save)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
    //update the launcher
    try {
        //if somethings unselected an exception is thrown        
        if (cb_map.isVisible()) {
            if(cb_map.getSelectedItem() != null){
                TempGameSettings.setMap(cb_map.getSelectedItem().toString());
            }
        }
        //save settings
        String name,value = "save-error";
        for(int i = 0;i < labels.size();i++){
            name = labels.get(i).getText();
            Component input = inputfields.get(i);
            if(input instanceof JTextField){
                value = ((JTextField)input).getText();
            }else
            if(input instanceof JSpinner){
                value = ((JSpinner)input).getValue()+"";
            }else
            if(input instanceof JComboBox){
                value =((JComboBox)input).getSelectedItem().toString();
            }
            TempGameSettings.setGameSetting(name, value,true);
        }

        if (!isInstant) {
            TabOrganizer.getRoomPanel().enableButtons();
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

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeGameSettingsFrame();
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox cb_map;
    private javax.swing.JLabel lbl_map;
    private javax.swing.JPanel pnl_settings;
    // End of variables declaration//GEN-END:variables
}
