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

import coopnetclient.enums.SettingTypes;
import coopnetclient.utils.gamedatabase.Game;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class TestGameDataEditor extends javax.swing.JPanel {

    private Game testdata = GameDatabase.getGameData(GameDatabase.getGameName("TST"));
    private ArrayList<GameSetting> settings;
    private SettingsListModel settingsmodel;
    private ChoiceListModel choicemodel = new ChoiceListModel();

    /** Creates new form TestGameDataEditor */
    public TestGameDataEditor() {
        initComponents();
        setChoiceFieldsEnabled(false);
        setNumberFieldsEnabled(false);
        LoadGameData();
        //add actionlisteners
        tf_HostPattern.addActionListener(saveaction);
        tf_JoinPattern.addActionListener(saveaction);
        tf_MapExtension.addActionListener(saveaction);
        tf_MapPath.addActionListener(saveaction);
        tf_RegKey.addActionListener(saveaction);
        tf_RelativeExePath.addActionListener(saveaction);
        settings = testdata.getGameSettings(null);
        settingsmodel = new SettingsListModel(settings);
        jl_Settings.setModel(settingsmodel);
        jl_MultiChoises.setModel(choicemodel);
    }

    private void setNumberFieldsEnabled(boolean enabled) {
        tf_MaxValue.setEnabled(enabled);
        tf_MinValue.setEnabled(enabled);
        if (enabled) {
            loadNumberProperties();
        }
    }

    private void loadNumberProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null) {
            if (gs.getMaxValue() < Integer.MAX_VALUE) {
                tf_MaxValue.setText(gs.getMaxValue() + "");
            } else {
                tf_MaxValue.setText("");
            }
            if (gs.getMinValue() > Integer.MIN_VALUE) {
                tf_MinValue.setText(gs.getMinValue() + "");
            } else {
                tf_MinValue.setText("");
            }
        }
    }

    private void saveNumberProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null) {
            if (tf_MaxValue.getText().length() > 0) {
                gs.setMaxValue(Integer.valueOf(tf_MaxValue.getText()));
            }
            if (tf_MinValue.getText().length() > 0) {
                gs.setMinValue(Integer.valueOf(tf_MinValue.getText()));
            }
        }
    }

    private void updateSettingFields() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null) {
            tf_DefaultValue.setText(gs.getDefaultValue());
            tf_KeyWord.setText(gs.getKeyWord());
            tf_SettingName.setText(gs.getName());
            cmb_SettingType.setSelectedIndex(gs.getType().ordinal());
            cmb_SettingTypeActionPerformed(null);
            if (gs.getType() == SettingTypes.CHOICE) {
                choicemodel = new ChoiceListModel(gs);
                jl_MultiChoises.setModel(choicemodel);
                choicemodel.refresh();
                jl_MultiChoises.revalidate();
                jl_MultiChoises.repaint();
                loadChoiceProperties();
            } else {
                choicemodel = new ChoiceListModel();
                jl_MultiChoises.setModel(choicemodel);
                choicemodel.refresh();
                jl_MultiChoises.revalidate();
                jl_MultiChoises.repaint();
            }
            if (gs.getType() == SettingTypes.CHOICE) {
                loadNumberProperties();
            }
        }
    }

    private void setChoiceFieldsEnabled(boolean enabled) {
        jl_MultiChoises.setEnabled(enabled);
        btn_AddChoice.setEnabled(enabled);
        btn_RemoveChoice.setEnabled(enabled);
        btn_SaveChoice.setEnabled(enabled);
        tf_ChoiceName.setEnabled(enabled);
        tf_ChoiceValue.setEnabled(enabled);
        if (enabled) {
            loadChoiceProperties();
        }
    }

    private void loadChoiceProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null) {
            if (jl_MultiChoises.getSelectedIndex() > -1) {
                tf_ChoiceName.setText(gs.getComboboxSelectNames().get(jl_MultiChoises.getSelectedIndex()));
                tf_ChoiceValue.setText(gs.getComboboxValues().get(jl_MultiChoises.getSelectedIndex()));
            }
        }
    }

    private void saveChoiceProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null) {
            gs.setComboboxSelectNames(choicemodel.names);
            gs.setComboboxValues(choicemodel.values);
        }
    }

    private void updateChoiceProperties() {
        GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
        if (gs != null && jl_MultiChoises.getSelectedIndex() > -1) {
            tf_ChoiceName.setText(gs.getComboboxSelectNames().get(jl_MultiChoises.getSelectedIndex()));
            tf_ChoiceValue.setText(gs.getComboboxValues().get(jl_MultiChoises.getSelectedIndex()));
        }
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

        @Override
        public void actionPerformed(
                ActionEvent e) {
            saveGameData();
        }
    };

    private void saveGameData() {
        //testdata = GameDatabase.getGameData(GameDatabase.getGameName("TST"));
        testdata.setHostPattern(tf_HostPattern.getText());
        testdata.setJoinPattern(tf_JoinPattern.getText());
        testdata.setMapExtension(tf_MapExtension.getText());
        testdata.setMapPath(tf_MapPath.getText());
        testdata.setRegEntry(tf_RegKey.getText());
        testdata.setRelativeExePath(tf_RelativeExePath.getText());
        testdata.setGameSettings(null, settings);
        try {
            GameDatabase.saveTestData();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class SettingsListModel extends AbstractListModel {

        ArrayList<GameSetting> data;

        public SettingsListModel(ArrayList<GameSetting> data) {
            this.data = data;
        }

        @Override
        public int getSize() {
            return data.size();
        }

        @Override
        public Object getElementAt(int i) {
            return data.get(i);
        }

        public void refresh() {
            fireContentsChanged(this, 0, getSize());
        }

        public void addSetting(boolean shared, String name, SettingTypes type, String keyword, String defaultValue) {
            data.add(new GameSetting(shared, name, type, keyword, defaultValue));
            fireContentsChanged(this, 0, getSize());
        }

        public void removeSetting(int index) {
            data.remove(index);
            fireContentsChanged(this, 0, getSize());
        }

        public void overrideSetting(int index, boolean shared, String name, SettingTypes type, String keyword, String defaultValue) {
            GameSetting gs = data.get(index);
            gs.setShared(shared);
            gs.setName(name);
            gs.setType(type);
            gs.setKeyWord(keyword);
            gs.setDefaultValue(defaultValue);
            fireContentsChanged(this, 0, getSize());
        }
    }

    private static class ChoiceListModel extends AbstractListModel {

        ArrayList<String> names;
        ArrayList<String> values;

        public ChoiceListModel() {
            this.names = new ArrayList<String>();
            this.values = new ArrayList<String>();
        }

        public ChoiceListModel(GameSetting data) {
            this.names = data.getComboboxSelectNames();
            this.values = data.getComboboxValues();
        }

        @Override
        public int getSize() {
            return names.size();
        }

        @Override
        public Object getElementAt(int i) {
            return names.get(i);
        }

        public void addChoise(String name, String value) {
            names.add(name);
            values.add(value);
            fireContentsChanged(this, 0, getSize());
        }

        public void removeChoise(int index) {
            names.remove(index);
            values.remove(index);
            fireContentsChanged(this, 0, getSize());
        }

        public void overrideChoise(String name, String value, int index) {
            names.set(index, name);
            values.set(index, value);
            fireContentsChanged(this, 0, getSize());
        }

        public void refresh() {
            fireContentsChanged(this, 0, getSize());
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
        java.awt.GridBagConstraints gridBagConstraints;

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
        lbl_ChoiceProperties = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jl_MultiChoises = new javax.swing.JList();
        btn_AddChoice = new javax.swing.JButton();
        btn_RemoveChoice = new javax.swing.JButton();
        lbl_ChoiceName = new javax.swing.JLabel();
        tf_ChoiceName = new javax.swing.JTextField();
        lbl_ChoiceValue = new javax.swing.JLabel();
        tf_ChoiceValue = new javax.swing.JTextField();
        lbl_wiki = new javax.swing.JLabel();
        lbl_Number = new javax.swing.JLabel();
        lbl_min = new javax.swing.JLabel();
        tf_MinValue = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tf_MaxValue = new javax.swing.JTextField();
        btn_SaveSetting = new javax.swing.JButton();
        btn_SaveChoice = new javax.swing.JButton();
        cb_isShared = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        lbl_host.setText("Launch parameters for hosting:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_host, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_HostPattern, gridBagConstraints);

        lbl_join.setText("Launch parameters for joining:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_join, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_JoinPattern, gridBagConstraints);

        lbl_RegKey.setText("Registry key of Install Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_RegKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_RegKey, gridBagConstraints);

        lbl_RelativeExePath.setText("Relative path of the runnable(exe/binary) from install path(e.g. /Bin/Game.exe):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_RelativeExePath, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_RelativeExePath, gridBagConstraints);

        lbl_mappath.setText("Relative path of Maps from Install path(e.g. Maps/):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_mappath, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_MapPath, gridBagConstraints);

        lbl_mapextension.setText("Map-file extension(e.g. map):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_mapextension, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_MapExtension, gridBagConstraints);

        lbl_settings.setText("Game Settings:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_settings, gridBagConstraints);

        jl_Settings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_Settings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_SettingsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jl_Settings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        btn_AddSetting.setText("Add");
        btn_AddSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_AddSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_AddSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_AddSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_AddSetting, gridBagConstraints);

        btn_RemoveSetting.setText("Remove");
        btn_RemoveSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_RemoveSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_RemoveSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_RemoveSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_RemoveSetting, gridBagConstraints);

        lbl_SettingName.setText("SettingName");
        lbl_SettingName.setMaximumSize(null);
        lbl_SettingName.setMinimumSize(null);
        lbl_SettingName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_SettingName, gridBagConstraints);

        tf_SettingName.setMaximumSize(null);
        tf_SettingName.setMinimumSize(null);
        tf_SettingName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_SettingName, gridBagConstraints);

        lbl_SettingType.setText("Setting type");
        lbl_SettingType.setMaximumSize(null);
        lbl_SettingType.setMinimumSize(null);
        lbl_SettingType.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_SettingType, gridBagConstraints);

        cmb_SettingType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Text", "Multiple choise", "Number" }));
        cmb_SettingType.setMaximumSize(null);
        cmb_SettingType.setMinimumSize(null);
        cmb_SettingType.setPreferredSize(null);
        cmb_SettingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_SettingTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmb_SettingType, gridBagConstraints);

        lbl_KeyWord.setText("Keyword");
        lbl_KeyWord.setMaximumSize(null);
        lbl_KeyWord.setMinimumSize(null);
        lbl_KeyWord.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_KeyWord, gridBagConstraints);

        tf_KeyWord.setMaximumSize(null);
        tf_KeyWord.setMinimumSize(null);
        tf_KeyWord.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_KeyWord, gridBagConstraints);

        lbl_DefValue.setText("Default Value");
        lbl_DefValue.setMaximumSize(null);
        lbl_DefValue.setMinimumSize(null);
        lbl_DefValue.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_DefValue, gridBagConstraints);

        tf_DefaultValue.setMaximumSize(null);
        tf_DefaultValue.setMinimumSize(null);
        tf_DefaultValue.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_DefaultValue, gridBagConstraints);

        lbl_ChoiceProperties.setText("Multiple choice properties:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_ChoiceProperties, gridBagConstraints);

        jl_MultiChoises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_MultiChoises.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_MultiChoisesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jl_MultiChoises);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        btn_AddChoice.setText("Add");
        btn_AddChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_AddChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_AddChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_AddChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_AddChoice, gridBagConstraints);

        btn_RemoveChoice.setText("Remove");
        btn_RemoveChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_RemoveChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_RemoveChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_RemoveChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_RemoveChoice, gridBagConstraints);

        lbl_ChoiceName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_ChoiceName.setText("Choice name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_ChoiceName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_ChoiceName, gridBagConstraints);

        lbl_ChoiceValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_ChoiceValue.setText("Choice value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_ChoiceValue, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_ChoiceValue, gridBagConstraints);

        lbl_wiki.setText("For detailed information and examples please visit our wiki on sourceforge.org!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(lbl_wiki, gridBagConstraints);

        lbl_Number.setText("Number properties:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_Number, gridBagConstraints);

        lbl_min.setText("Minimum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_min, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_MinValue, gridBagConstraints);

        jLabel1.setText("Maximum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_MaxValue, gridBagConstraints);

        btn_SaveSetting.setText("Update");
        btn_SaveSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_SaveSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_SaveSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_SaveSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_SaveSetting, gridBagConstraints);

        btn_SaveChoice.setText("Update");
        btn_SaveChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_SaveChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_SaveChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_SaveChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_SaveChoice, gridBagConstraints);

        cb_isShared.setText("Shared setting");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(cb_isShared, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void cmb_SettingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_SettingTypeActionPerformed
    setChoiceFieldsEnabled(cmb_SettingType.getSelectedIndex() == SettingTypes.CHOICE.ordinal());
    setNumberFieldsEnabled(cmb_SettingType.getSelectedIndex() == SettingTypes.NUMBER.ordinal());
}//GEN-LAST:event_cmb_SettingTypeActionPerformed

private void jl_SettingsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_SettingsValueChanged
    updateSettingFields();
}//GEN-LAST:event_jl_SettingsValueChanged

private void jl_MultiChoisesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_MultiChoisesValueChanged
    updateChoiceProperties();
}//GEN-LAST:event_jl_MultiChoisesValueChanged

private void btn_AddSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddSettingActionPerformed
    settingsmodel.addSetting(cb_isShared.isSelected(), tf_SettingName.getText(), SettingTypes.values()[cmb_SettingType.getSelectedIndex()], tf_KeyWord.getText(), tf_DefaultValue.getText());
    settingsmodel.refresh();
    jl_Settings.repaint();
    saveGameData();
}//GEN-LAST:event_btn_AddSettingActionPerformed

private void btn_RemoveSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveSettingActionPerformed
    if (jl_Settings.getSelectedIndex() > -1) {
        settingsmodel.removeSetting(jl_Settings.getSelectedIndex());
        settingsmodel.refresh();
        jl_Settings.repaint();
        saveGameData();
    }
}//GEN-LAST:event_btn_RemoveSettingActionPerformed

private void btn_AddChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
    if (gs != null) {
        choicemodel.addChoise(tf_ChoiceName.getText(), tf_ChoiceValue.getText());
        choicemodel.refresh();
        saveGameData();
        jl_Settings.setSelectedIndex(choicemodel.getSize());
        jl_Settings.repaint();
    }
}//GEN-LAST:event_btn_AddChoiceActionPerformed

private void btn_RemoveChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
    if (gs != null && jl_MultiChoises.getSelectedIndex() > -1) {
        choicemodel.removeChoise(jl_MultiChoises.getSelectedIndex());
        saveGameData();
    }
}//GEN-LAST:event_btn_RemoveChoiceActionPerformed

private void btn_SaveSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveSettingActionPerformed
    GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
    if (gs != null) {
        gs.setShared(cb_isShared.isSelected());
        gs.setName(tf_SettingName.getText());
        gs.setDefaultValue(tf_DefaultValue.getText());
        gs.setKeyWord(tf_KeyWord.getText());
        gs.setType(SettingTypes.values()[cmb_SettingType.getSelectedIndex()]);
        jl_MultiChoises.repaint();
        if (cmb_SettingType.getSelectedIndex() == SettingTypes.CHOICE.ordinal()) {
            saveChoiceProperties();
        }
        if (cmb_SettingType.getSelectedIndex() == SettingTypes.NUMBER.ordinal()) {
            saveNumberProperties();
        }
        settingsmodel.refresh();
        saveGameData();
    }
}//GEN-LAST:event_btn_SaveSettingActionPerformed

private void btn_SaveChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_Settings.getSelectedValue();
    if (gs != null && jl_MultiChoises.getSelectedIndex() > -1) {
        choicemodel.overrideChoise(tf_ChoiceName.getText(), tf_ChoiceValue.getText(), jl_MultiChoises.getSelectedIndex());
        choicemodel.refresh();
        saveChoiceProperties();
        saveGameData();
    }
}//GEN-LAST:event_btn_SaveChoiceActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddChoice;
    private javax.swing.JButton btn_AddSetting;
    private javax.swing.JButton btn_RemoveChoice;
    private javax.swing.JButton btn_RemoveSetting;
    private javax.swing.JButton btn_SaveChoice;
    private javax.swing.JButton btn_SaveSetting;
    private javax.swing.JCheckBox cb_isShared;
    private javax.swing.JComboBox cmb_SettingType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jl_MultiChoises;
    private javax.swing.JList jl_Settings;
    private javax.swing.JLabel lbl_ChoiceName;
    private javax.swing.JLabel lbl_ChoiceProperties;
    private javax.swing.JLabel lbl_ChoiceValue;
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
    private javax.swing.JTextField tf_ChoiceName;
    private javax.swing.JTextField tf_ChoiceValue;
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
