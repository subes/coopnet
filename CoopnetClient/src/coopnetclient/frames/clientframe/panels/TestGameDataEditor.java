package coopnetclient.frames.clientframe.panels;

import coopnetclient.utils.gamedatabase.Game;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class TestGameDataEditor extends javax.swing.JPanel {

    Game testdata = GameDatabase.getGameData(GameDatabase.getGameName("TST"));
    ArrayList<GameSetting> settings = testdata.getGameSettings(null);

    /** Creates new form TestGameDataEditor */
    public TestGameDataEditor() {
        initComponents();
        setChoiseFieldsEnabled(false);
        setNumberFieldsEnabled(false);
        LoadGameData();
        jl_Settings.setModel(new DinamicListModel(settings));
    //add actionlisteners
        /*
    tf_HostPattern.setText(testdata.getHostPattern(null));
    tf_JoinPattern.setText(testdata.getJoinPattern(null));
    tf_MapExtension.setText(testdata.getMapExtension(null));
    tf_MapPath.setText(testdata.getMapPath(null));
    tf_RegKey.setText(testdata.getRegEntry(null));
    tf_RelativeExePath.setText(testdata.getRelativeExePath(null));
     */

    }
    
    private void setNumberFieldsEnabled(boolean enabled) {
        tf_MaxValue.setEnabled(enabled);
        tf_MinValue.setEnabled(enabled);
        if(enabled){
            loadNumberProperties();
        }
    }

    private void loadNumberProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        tf_MaxValue.setText(gs.getMaxValue() + "");
        tf_MinValue.setText(gs.getMinValue() + "");
    }
    
    private void saveNumberProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        gs.setMaxValue(Integer.valueOf(tf_MaxValue.getText()));
        gs.setMinValue(Integer.valueOf(tf_MinValue.getText()));
        saveGameData();
    }

    private void updateSettingFields() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        tf_DefaultValue.setText(gs.getDefaultValue());
        tf_KeyWord.setText(gs.getKeyWord());
        tf_SettingName.setText(gs.getName());
        cmb_SettingType.setSelectedIndex(gs.getType());
    }

    private void setChoiseFieldsEnabled(boolean enabled) {
        jl_MultiChoises.setEnabled(enabled);
        btn_AddChoise.setEnabled(enabled);
        btn_RemoveChoise.setEnabled(enabled);
        tf_ChoiseName.setEnabled(enabled);
        tf_ChoiseValue.setEnabled(enabled);
        if (enabled) {
            loadChoiseProperties();
        }
    }
    
    private void loadChoiseProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        jl_MultiChoises.setModel(new DinamicListModel(gs.getComboboxSelectNames()));
        jl_MultiChoises.repaint();
        tf_ChoiseName.setText(gs.getComboboxSelectNames().get(jl_MultiChoises.getSelectedIndex()));
        tf_ChoiseValue.setText(gs.getComboboxValues().get(jl_MultiChoises.getSelectedIndex()));
    }

    private void saveChoiseProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        gs.getComboboxSelectNames().set(jl_MultiChoises.getSelectedIndex(), tf_ChoiseName.getText());
        gs.getComboboxValues().set(jl_MultiChoises.getSelectedIndex(), tf_ChoiseValue.getText());
        saveGameData();
    }
    
    private void updateChoiseProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        tf_ChoiseName.setText(gs.getComboboxSelectNames().get(jl_MultiChoises.getSelectedIndex()));
        tf_ChoiseValue.setText(gs.getComboboxValues().get(jl_MultiChoises.getSelectedIndex()));
    }

    private void LoadGameData() {
        tf_HostPattern.setText(testdata.getHostPattern(null));
        tf_JoinPattern.setText(testdata.getJoinPattern(null));
        tf_MapExtension.setText(testdata.getMapExtension(null));
        tf_MapPath.setText(testdata.getMapPath(null));
        tf_RegKey.setText(testdata.getRegEntry(null));
        tf_RelativeExePath.setText(testdata.getRelativeExePath(null));
    }
    private ActionListener saveaction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            saveGameData();
        }
    };

    private void saveGameData() {
        testdata.setHostPattern(tf_HostPattern.getText());
        testdata.setJoinPattern(tf_JoinPattern.getText());
        testdata.setMapExtension(tf_MapExtension.getText());
        testdata.setMapPath(tf_MapPath.getText());
        testdata.setRegEntry(tf_RegKey.getText());
        testdata.setRelativeExePath(tf_RelativeExePath.getText());
        testdata.setGameSettings(null, settings);
    }

    private static class DinamicListModel extends AbstractListModel {

        ArrayList data;

        public DinamicListModel(ArrayList data) {
            this.data = data;
        }

        public int getSize() {
            return data.size();
        }

        public Object getElementAt(int i) {
            return data.get(i);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_host = new javax.swing.JLabel();
        tf_HostPattern = new javax.swing.JTextField();
        lbl_join = new javax.swing.JLabel();
        tf_JoinPattern = new javax.swing.JTextField();
        lbl_RegKey = new javax.swing.JLabel();
        tf_RegKey = new javax.swing.JTextField();
        lbl_RelativeExePath = new javax.swing.JLabel();
        tf_RelativeExePath = new javax.swing.JTextField();
        lbl_mappath = new javax.swing.JLabel();
        tf_MapPath = new javax.swing.JTextField();
        lbl_mapextension = new javax.swing.JLabel();
        tf_MapExtension = new javax.swing.JTextField();
        lbl_settings = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jl_Settings = new javax.swing.JList();
        btn_AddSetting = new javax.swing.JButton();
        btn_RemoveSetting = new javax.swing.JButton();
        lbl_SettingName = new javax.swing.JLabel();
        tf_SettingName = new javax.swing.JTextField();
        lbl_SettingType = new javax.swing.JLabel();
        cmb_SettingType = new javax.swing.JComboBox();
        lbl_KeyWord = new javax.swing.JLabel();
        tf_KeyWord = new javax.swing.JTextField();
        lbl_DefValue = new javax.swing.JLabel();
        tf_DefaultValue = new javax.swing.JTextField();
        lbl_ChoiseProperties = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jl_MultiChoises = new javax.swing.JList();
        btn_AddChoise = new javax.swing.JButton();
        btn_RemoveChoise = new javax.swing.JButton();
        lbl_ChoiseName = new javax.swing.JLabel();
        tf_ChoiseName = new javax.swing.JTextField();
        lbl_ChoiseValue = new javax.swing.JLabel();
        tf_ChoiseValue = new javax.swing.JTextField();
        lbl_wiki = new javax.swing.JLabel();
        lbl_Number = new javax.swing.JLabel();
        lbl_min = new javax.swing.JLabel();
        tf_MinValue = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tf_MaxValue = new javax.swing.JTextField();
        btn_Close = new javax.swing.JButton();

        lbl_host.setText("Launch parameters for hosting:");

        lbl_join.setText("Launch parameters for joining:");

        lbl_RegKey.setText("Registry key of Install Path:");

        lbl_RelativeExePath.setText("Relative path of the runnable(exe/binary) from install path(e.g. /Bin/Game.exe):");

        lbl_mappath.setText("Relative path of Maps from Install path(e.g. /Maps/):");

        lbl_mapextension.setText("Map-file extension(e.g. .map):");

        lbl_settings.setText("Game Settings:");

        jl_Settings.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "", "dfsf", "fsdfsd", "fsdfsd", "dfgretr" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jl_Settings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_Settings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_SettingsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jl_Settings);

        btn_AddSetting.setText("Add/Update");
        btn_AddSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddSettingActionPerformed(evt);
            }
        });

        btn_RemoveSetting.setText("Remove");
        btn_RemoveSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveSettingActionPerformed(evt);
            }
        });

        lbl_SettingName.setText("SettingName");
        lbl_SettingName.setPreferredSize(new java.awt.Dimension(6, 20));

        lbl_SettingType.setText("Setting type");
        lbl_SettingType.setPreferredSize(new java.awt.Dimension(6, 20));

        cmb_SettingType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Text", "Multiple choise", "Number" }));
        cmb_SettingType.setPreferredSize(new java.awt.Dimension(6, 20));
        cmb_SettingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_SettingTypeActionPerformed(evt);
            }
        });

        lbl_KeyWord.setText("Keyword");
        lbl_KeyWord.setPreferredSize(new java.awt.Dimension(6, 20));

        lbl_DefValue.setText("Default Value");
        lbl_DefValue.setPreferredSize(new java.awt.Dimension(6, 20));

        lbl_ChoiseProperties.setText("Multiple choise properties:");

        jl_MultiChoises.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jl_MultiChoises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_MultiChoises.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_MultiChoisesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jl_MultiChoises);

        btn_AddChoise.setText("Add/Update");
        btn_AddChoise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddChoiseActionPerformed(evt);
            }
        });

        btn_RemoveChoise.setText("Remove");
        btn_RemoveChoise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveChoiseActionPerformed(evt);
            }
        });

        lbl_ChoiseName.setText("Choise name:");

        lbl_ChoiseValue.setText("Choise value");

        lbl_wiki.setText("For detailed information and examples please visit our wiki on sourceforge.org!");

        lbl_Number.setText("Number properties:");

        lbl_min.setText("Minimum:");

        jLabel1.setText("Maximum:");

        btn_Close.setText("Close");
        btn_Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tf_HostPattern, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_host)
                                .addComponent(lbl_join)
                                .addComponent(tf_JoinPattern, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_RegKey)
                                .addComponent(tf_RegKey, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_RelativeExePath, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(tf_RelativeExePath, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_mappath)
                                .addComponent(tf_MapPath, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_mapextension)
                                .addComponent(tf_MapExtension, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                                .addComponent(lbl_settings)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btn_RemoveSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_AddSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(tf_SettingName, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lbl_SettingName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cmb_SettingType, 0, 122, Short.MAX_VALUE)
                                        .addComponent(lbl_SettingType, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tf_KeyWord)
                                        .addComponent(lbl_KeyWord, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tf_DefaultValue)
                                        .addComponent(lbl_DefValue, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))))
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lbl_min)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tf_MinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tf_MaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lbl_Number)
                            .addContainerGap(364, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbl_ChoiseProperties)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tf_ChoiseName)
                                        .addComponent(lbl_ChoiseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_RemoveChoise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_AddChoise))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tf_ChoiseValue, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addComponent(lbl_ChoiseValue, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                    .addGap(25, 25, 25))
                                .addComponent(lbl_wiki))
                            .addGap(79, 79, 79)))
                    .addComponent(btn_Close, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_ChoiseProperties, lbl_SettingName, tf_SettingName});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbl_host))
                    .addComponent(btn_Close))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_HostPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_join)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_JoinPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_RegKey)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_RegKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_RelativeExePath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_RelativeExePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_mappath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_MapPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_mapextension)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_MapExtension, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_settings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_AddSetting)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_RemoveSetting)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_SettingName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_SettingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_KeyWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_DefValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_SettingName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_SettingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_KeyWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_DefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Number)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_min)
                    .addComponent(tf_MinValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tf_MaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_ChoiseProperties)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_AddChoise)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_RemoveChoise)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_ChoiseName)
                            .addComponent(lbl_ChoiseValue))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tf_ChoiseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tf_ChoiseValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_wiki)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void cmb_SettingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_SettingTypeActionPerformed
    setChoiseFieldsEnabled(cmb_SettingType.getSelectedIndex() == GameSetting.COMBOBOX_TYPE);
    setNumberFieldsEnabled(cmb_SettingType.getSelectedIndex() == GameSetting.SPINNER_TYPE);
}//GEN-LAST:event_cmb_SettingTypeActionPerformed

private void jl_SettingsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_SettingsValueChanged
    updateSettingFields();
}//GEN-LAST:event_jl_SettingsValueChanged

private void jl_MultiChoisesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_MultiChoisesValueChanged
        updateChoiseProperties();
}//GEN-LAST:event_jl_MultiChoisesValueChanged

private void btn_AddSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddSettingActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btn_AddSettingActionPerformed

private void btn_RemoveSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveSettingActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btn_RemoveSettingActionPerformed

private void btn_AddChoiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddChoiseActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btn_AddChoiseActionPerformed

private void btn_RemoveChoiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveChoiseActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btn_RemoveChoiseActionPerformed

private void btn_CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CloseActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btn_CloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddChoise;
    private javax.swing.JButton btn_AddSetting;
    private javax.swing.JButton btn_Close;
    private javax.swing.JButton btn_RemoveChoise;
    private javax.swing.JButton btn_RemoveSetting;
    private javax.swing.JComboBox cmb_SettingType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jl_MultiChoises;
    private javax.swing.JList jl_Settings;
    private javax.swing.JLabel lbl_ChoiseName;
    private javax.swing.JLabel lbl_ChoiseProperties;
    private javax.swing.JLabel lbl_ChoiseValue;
    private javax.swing.JLabel lbl_DefValue;
    private javax.swing.JLabel lbl_KeyWord;
    private javax.swing.JLabel lbl_Number;
    private javax.swing.JLabel lbl_RegKey;
    private javax.swing.JLabel lbl_RelativeExePath;
    private javax.swing.JLabel lbl_SettingName;
    private javax.swing.JLabel lbl_SettingType;
    private javax.swing.JLabel lbl_host;
    private javax.swing.JLabel lbl_join;
    private javax.swing.JLabel lbl_mapextension;
    private javax.swing.JLabel lbl_mappath;
    private javax.swing.JLabel lbl_min;
    private javax.swing.JLabel lbl_settings;
    private javax.swing.JLabel lbl_wiki;
    private javax.swing.JTextField tf_ChoiseName;
    private javax.swing.JTextField tf_ChoiseValue;
    private javax.swing.JTextField tf_DefaultValue;
    private javax.swing.JTextField tf_HostPattern;
    private javax.swing.JTextField tf_JoinPattern;
    private javax.swing.JTextField tf_KeyWord;
    private javax.swing.JTextField tf_MapExtension;
    private javax.swing.JTextField tf_MapPath;
    private javax.swing.JTextField tf_MaxValue;
    private javax.swing.JTextField tf_MinValue;
    private javax.swing.JTextField tf_RegKey;
    private javax.swing.JTextField tf_RelativeExePath;
    private javax.swing.JTextField tf_SettingName;
    // End of variables declaration//GEN-END:variables
}
