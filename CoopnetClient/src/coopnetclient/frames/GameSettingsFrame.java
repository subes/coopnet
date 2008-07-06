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

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.Protocol;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

public class GameSettingsFrame extends javax.swing.JFrame {

    private int fieldcount = 0;
    private String gamename;
    private String modname;
    private HashMap<String, String> gamemodes = new HashMap<String, String>();
    
    /** Creates new form GameSettingsPanel */
    public GameSettingsFrame(String gamename,String modname) {
        initComponents();
        this.gamename = gamename;
        this.modname = modname;
        hideAll();
        customize();
    }

    private String KeyOfValue(String value) {
        for (String s : gamemodes.keySet()) {
            String val = gamemodes.get(s);
            if (val.equals(value)) {
                return s;
            }
        }
        return null;
    }

    private void hideAll() {
        tf_port.setVisible(false);
        spn_timeLimit.setVisible(false);
        lbl_timeLimit.setVisible(false);
        lbl_port.setVisible(false);
        lbl_map.setVisible(false);
        lbl_mode.setVisible(false);
        cb_mode.setVisible(false);
        cb_map.setVisible(false);        
        lbl_bots.setVisible(false);
        spn_bots.setVisible(false);
        lbl_scoreLimit.setVisible(false);
        spn_scoreLimit.setVisible(false);
    }

    private void customize() {
        String tmp = GameDatabase.getGameSettings(gamename,modname);
        if (tmp == null) {
            return;
        }
        String fields[] = tmp.split(";");
        for (String field : fields) {
            //enabling required fields:
            if (field.equals("bots")) {
                lbl_bots.setVisible(true);
                spn_bots.setVisible(true);
                spn_bots.setValue(Globals.getLauncher().getBots());
                fieldcount++;
            }
            if (field.equals("goalscore")) {
                lbl_scoreLimit.setVisible(true);
                spn_scoreLimit.setVisible(true);
                spn_scoreLimit.setValue(Globals.getLauncher().getGoalScore());
                fieldcount++;
            }
            if (field.equals("map")) {
                cb_map.setModel(new DefaultComboBoxModel(loadMaps()));
                lbl_map.setVisible(true);
                cb_map.setVisible(true);
                cb_map.setSelectedItem(Globals.getLauncher().getMap());
                fieldcount++;
            }
            if (field.equals("port")) {
                lbl_port.setVisible(true);
                tf_port.setVisible(true);
                tf_port.setText(Globals.getLauncher().getPort() + "");
                fieldcount++;
            }
            if (field.equals("timelimit")) {
                lbl_timeLimit.setVisible(true);
                spn_timeLimit.setVisible(true);
                spn_timeLimit.setValue(Globals.getLauncher().getTimelimit());
                fieldcount++;
            }
            if (field.equals("gamemode")) {
                cb_mode.setModel(new DefaultComboBoxModel(getGameModes()));
                lbl_mode.setVisible(true);
                cb_mode.setVisible(true);
                cb_mode.setSelectedItem(KeyOfValue(Globals.getLauncher().getGameMode()));
                fieldcount++;
            }
        //end of field enabling loop
        }       
    }

    private String[] getGameModes() {
        Vector<String> modenames = new Vector<String>();
        String tmp = GameDatabase.getGameModes(gamename,modname);
        if(tmp==null) return null;
        String tmp2[] = tmp.split(";");
        for (String s : tmp2) {
            String tmp3[] = s.split("#");
            String code = tmp3[0];
            String name = tmp3[1];
            modenames.add(name);
            gamemodes.put(name, code);
        }
        return modenames.toArray(new String[0]);
    }

    private String[] loadMaps() {
        String extension = GameDatabase.getMapExtension(gamename,modname);
        String path = Globals.getLauncher().getFullMapPath(gamename);
        System.out.println("loading maps from: "+path);
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

        btn_save = new javax.swing.JButton();
        pnl_settings = new javax.swing.JPanel();
        lbl_mode = new javax.swing.JLabel();
        cb_mode = new javax.swing.JComboBox();
        lbl_map = new javax.swing.JLabel();
        cb_map = new javax.swing.JComboBox();
        lbl_timeLimit = new javax.swing.JLabel();
        spn_timeLimit = new javax.swing.JSpinner();
        lbl_scoreLimit = new javax.swing.JLabel();
        spn_scoreLimit = new javax.swing.JSpinner();
        lbl_bots = new javax.swing.JLabel();
        spn_bots = new javax.swing.JSpinner();
        tf_port = new javax.swing.JTextField();
        lbl_port = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        lbl_mode.setText("Mode:");

        cb_mode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_map.setText("Map:");

        cb_map.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbl_timeLimit.setText("Time limit:");

        lbl_scoreLimit.setText("Score limit:");

        lbl_bots.setText("Bots:");

        tf_port.setMaximumSize(new java.awt.Dimension(30, 20));
        tf_port.setMinimumSize(new java.awt.Dimension(30, 20));
        tf_port.setPreferredSize(new java.awt.Dimension(30, 20));

        lbl_port.setText("Port:");

        javax.swing.GroupLayout pnl_settingsLayout = new javax.swing.GroupLayout(pnl_settings);
        pnl_settings.setLayout(pnl_settingsLayout);
        pnl_settingsLayout.setHorizontalGroup(
            pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_settingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_settingsLayout.createSequentialGroup()
                        .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_mode)
                            .addComponent(lbl_map))
                        .addGap(39, 39, 39)
                        .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cb_map, 0, 276, Short.MAX_VALUE)
                            .addComponent(cb_mode, 0, 276, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_settingsLayout.createSequentialGroup()
                            .addComponent(lbl_timeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spn_timeLimit))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnl_settingsLayout.createSequentialGroup()
                            .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbl_port, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbl_bots)
                                .addComponent(lbl_scoreLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(spn_scoreLimit)
                                .addComponent(spn_bots)
                                .addComponent(tf_port, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        pnl_settingsLayout.setVerticalGroup(
            pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_settingsLayout.createSequentialGroup()
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_mode)
                    .addComponent(cb_mode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_map)
                    .addComponent(cb_map, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_timeLimit)
                    .addComponent(spn_timeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_scoreLimit)
                    .addComponent(spn_scoreLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_bots)
                    .addComponent(spn_bots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_port)
                    .addComponent(tf_port, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_save)
                .addContainerGap(321, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_settings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_save)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
    //update the launcher
    try {
        //if somethings unselected an exception is thrown
        if (cb_mode.isVisible()) {
            Globals.getLauncher().setGameMode(gamemodes.get(cb_mode.getSelectedItem().toString()));
        }
        if (cb_map.isVisible()) {
            Globals.getLauncher().setMap(cb_map.getSelectedItem().toString());
        }
        if (tf_port.isVisible()) {
            Globals.getLauncher().setPort(new Integer(tf_port.getText()));
        }
        if (spn_timeLimit.isVisible()) {
            Globals.getLauncher().setTimelimit((Integer)spn_timeLimit.getValue());
        }        
        if (spn_bots.isVisible()) {
            Globals.getLauncher().setBots((Integer)spn_bots.getValue());
        }
        if (spn_scoreLimit.isVisible()) {
            Globals.getLauncher().setGoalScore((Integer)spn_scoreLimit.getValue());
        }
        Globals.getRoomPanel().enableButtons();
        Client.send(Protocol.SendPort(new Integer(tf_port.getText())), null);
        
        Globals.closeGameSettingsFrame();
    } catch (Exception e) {
    }

}//GEN-LAST:event_btn_saveActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Globals.closeGameSettingsFrame();
}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox cb_map;
    private javax.swing.JComboBox cb_mode;
    private javax.swing.JLabel lbl_bots;
    private javax.swing.JLabel lbl_map;
    private javax.swing.JLabel lbl_mode;
    private javax.swing.JLabel lbl_port;
    private javax.swing.JLabel lbl_scoreLimit;
    private javax.swing.JLabel lbl_timeLimit;
    private javax.swing.JPanel pnl_settings;
    private javax.swing.JSpinner spn_bots;
    private javax.swing.JSpinner spn_scoreLimit;
    private javax.swing.JSpinner spn_timeLimit;
    private javax.swing.JTextField tf_port;
    // End of variables declaration//GEN-END:variables

}
