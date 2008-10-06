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

    private Game testData = GameDatabase.getGameData(GameDatabase.getGameName("TST"));
    private ArrayList<GameSetting> settings;
    private SettingsListModel settingsModel;
    private ChoiceListModel choiceModel = new ChoiceListModel();

    /** Creates new form TestGameDataEditor */
    public TestGameDataEditor() {
        initComponents();
        setChoiceFieldsEnabled(false);
        setNumberFieldsEnabled(false);
        loadGameData();
        //add actionlisteners
        tf_hostPattern.addActionListener(saveaction);
        tf_joinPattern.addActionListener(saveaction);
        tf_mapExtension.addActionListener(saveaction);
        tf_mapPath.addActionListener(saveaction);
        tf_regKey.addActionListener(saveaction);
        tf_relativeExePath.addActionListener(saveaction);
        settings = testData.getGameSettings(null);
        settingsModel = new SettingsListModel(settings);
        jl_settings.setModel(settingsModel);
        jl_multiChoises.setModel(choiceModel);
    }

    private void setNumberFieldsEnabled(boolean enabled) {
        tf_maxValue.setEnabled(enabled);
        tf_minValue.setEnabled(enabled);
        if (enabled) {
            loadNumberProperties();
        }
    }

    private void loadNumberProperties() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null) {
            if (gs.getMaxValue() < Integer.MAX_VALUE) {
                tf_maxValue.setText(gs.getMaxValue() + "");
            } else {
                tf_maxValue.setText("");
            }
            if (gs.getMinValue() > Integer.MIN_VALUE) {
                tf_minValue.setText(gs.getMinValue() + "");
            } else {
                tf_minValue.setText("");
            }
        }
    }

    private void saveNumberProperties() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null) {
            if (tf_maxValue.getText().length() > 0) {
                gs.setMaxValue(Integer.valueOf(tf_maxValue.getText()));
            }
            if (tf_minValue.getText().length() > 0) {
                gs.setMinValue(Integer.valueOf(tf_minValue.getText()));
            }
        }
    }

    private void updateSettingFields() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null) {
            tf_defaultValue.setText(gs.getDefaultValue());
            tf_keyWord.setText(gs.getKeyWord());
            tf_settingName.setText(gs.getName());
            cmb_settingType.setSelectedIndex(gs.getType().ordinal());
            cmb_settingTypeActionPerformed(null);
            if (gs.getType() == SettingTypes.CHOICE) {
                choiceModel = new ChoiceListModel(gs);
                jl_multiChoises.setModel(choiceModel);
                choiceModel.refresh();
                jl_multiChoises.revalidate();
                jl_multiChoises.repaint();
                loadChoiceProperties();
            } else {
                choiceModel = new ChoiceListModel();
                jl_multiChoises.setModel(choiceModel);
                choiceModel.refresh();
                jl_multiChoises.revalidate();
                jl_multiChoises.repaint();
            }
            if (gs.getType() == SettingTypes.CHOICE) {
                loadNumberProperties();
            }
        }
    }

    private void setChoiceFieldsEnabled(boolean enabled) {
        jl_multiChoises.setEnabled(enabled);
        btn_addChoice.setEnabled(enabled);
        btn_removeChoice.setEnabled(enabled);
        btn_saveChoice.setEnabled(enabled);
        tf_choiceName.setEnabled(enabled);
        tf_choiceValue.setEnabled(enabled);
        if (enabled) {
            loadChoiceProperties();
        }
    }

    private void loadChoiceProperties() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null) {
            if (jl_multiChoises.getSelectedIndex() > -1) {
                tf_choiceName.setText(gs.getComboboxSelectNames().get(jl_multiChoises.getSelectedIndex()));
                tf_choiceValue.setText(gs.getComboboxValues().get(jl_multiChoises.getSelectedIndex()));
            }
        }
    }

    private void saveChoiceProperties() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null) {
            gs.setComboboxSelectNames(choiceModel.names);
            gs.setComboboxValues(choiceModel.values);
        }
    }

    private void updateChoiceProperties() {
        GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
        if (gs != null && jl_multiChoises.getSelectedIndex() > -1) {
            tf_choiceName.setText(gs.getComboboxSelectNames().get(jl_multiChoises.getSelectedIndex()));
            tf_choiceValue.setText(gs.getComboboxValues().get(jl_multiChoises.getSelectedIndex()));
        }
    }

    private void loadGameData() {
        tf_hostPattern.setText(testData.getHostPattern(null));
        tf_joinPattern.setText(testData.getJoinPattern(null));
        tf_mapExtension.setText(testData.getMapExtension(null));
        tf_mapPath.setText(testData.getMapPath(null));
        tf_regKey.setText(testData.getRegEntry(null));
        tf_relativeExePath.setText(testData.getRelativeExePath(null));
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
        testData.setHostPattern(tf_hostPattern.getText());
        testData.setJoinPattern(tf_joinPattern.getText());
        testData.setMapExtension(tf_mapExtension.getText());
        testData.setMapPath(tf_mapPath.getText());
        testData.setRegEntry(tf_regKey.getText());
        testData.setRelativeExePath(tf_relativeExePath.getText());
        testData.setGameSettings(null, settings);
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
        tf_hostPattern = new javax.swing.JTextField();
        lbl_join = new javax.swing.JLabel();
        tf_joinPattern = new javax.swing.JTextField();
        lbl_regKey = new javax.swing.JLabel();
        tf_regKey = new javax.swing.JTextField();
        lbl_relativeExePath = new javax.swing.JLabel();
        tf_relativeExePath = new javax.swing.JTextField();
        lbl_mappath = new javax.swing.JLabel();
        tf_mapPath = new javax.swing.JTextField();
        lbl_mapextension = new javax.swing.JLabel();
        tf_mapExtension = new javax.swing.JTextField();
        lbl_settings = new javax.swing.JLabel();
        scrl_settings = new javax.swing.JScrollPane();
        jl_settings = new javax.swing.JList();
        btn_addSetting = new javax.swing.JButton();
        btn_removeSetting = new javax.swing.JButton();
        lbl_settingName = new javax.swing.JLabel();
        tf_settingName = new javax.swing.JTextField();
        lbl_settingType = new javax.swing.JLabel();
        cmb_settingType = new javax.swing.JComboBox();
        lbl_keyWord = new javax.swing.JLabel();
        tf_keyWord = new javax.swing.JTextField();
        lbl_defValue = new javax.swing.JLabel();
        tf_defaultValue = new javax.swing.JTextField();
        lbl_choiceProperties = new javax.swing.JLabel();
        scrl_choice = new javax.swing.JScrollPane();
        jl_multiChoises = new javax.swing.JList();
        btn_addChoice = new javax.swing.JButton();
        btn_removeChoice = new javax.swing.JButton();
        lbl_choiceName = new javax.swing.JLabel();
        tf_choiceName = new javax.swing.JTextField();
        lbl_choiceValue = new javax.swing.JLabel();
        tf_choiceValue = new javax.swing.JTextField();
        lbl_wiki = new javax.swing.JLabel();
        lbl_number = new javax.swing.JLabel();
        lbl_min = new javax.swing.JLabel();
        tf_minValue = new javax.swing.JTextField();
        lbl_maximum = new javax.swing.JLabel();
        tf_maxValue = new javax.swing.JTextField();
        btn_saveSetting = new javax.swing.JButton();
        btn_saveChoice = new javax.swing.JButton();
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
        add(tf_hostPattern, gridBagConstraints);

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
        add(tf_joinPattern, gridBagConstraints);

        lbl_regKey.setText("Registry key of Install Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_regKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_regKey, gridBagConstraints);

        lbl_relativeExePath.setText("Relative path of the runnable(exe/binary) from install path(e.g. /Bin/Game.exe):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_relativeExePath, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_relativeExePath, gridBagConstraints);

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
        add(tf_mapPath, gridBagConstraints);

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
        add(tf_mapExtension, gridBagConstraints);

        lbl_settings.setText("Game Settings:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_settings, gridBagConstraints);

        jl_settings.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_settings.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_settingsValueChanged(evt);
            }
        });
        scrl_settings.setViewportView(jl_settings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrl_settings, gridBagConstraints);

        btn_addSetting.setText("Add");
        btn_addSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_addSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_addSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_addSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_addSetting, gridBagConstraints);

        btn_removeSetting.setText("Remove");
        btn_removeSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_removeSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_removeSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_removeSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_removeSetting, gridBagConstraints);

        lbl_settingName.setText("SettingName");
        lbl_settingName.setMaximumSize(null);
        lbl_settingName.setMinimumSize(null);
        lbl_settingName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_settingName, gridBagConstraints);

        tf_settingName.setMaximumSize(null);
        tf_settingName.setMinimumSize(null);
        tf_settingName.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_settingName, gridBagConstraints);

        lbl_settingType.setText("Setting type");
        lbl_settingType.setMaximumSize(null);
        lbl_settingType.setMinimumSize(null);
        lbl_settingType.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_settingType, gridBagConstraints);

        cmb_settingType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Text", "Multiple choise", "Number" }));
        cmb_settingType.setMaximumSize(null);
        cmb_settingType.setMinimumSize(null);
        cmb_settingType.setPreferredSize(null);
        cmb_settingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_settingTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cmb_settingType, gridBagConstraints);

        lbl_keyWord.setText("Keyword");
        lbl_keyWord.setMaximumSize(null);
        lbl_keyWord.setMinimumSize(null);
        lbl_keyWord.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_keyWord, gridBagConstraints);

        tf_keyWord.setMaximumSize(null);
        tf_keyWord.setMinimumSize(null);
        tf_keyWord.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_keyWord, gridBagConstraints);

        lbl_defValue.setText("Default Value");
        lbl_defValue.setMaximumSize(null);
        lbl_defValue.setMinimumSize(null);
        lbl_defValue.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_defValue, gridBagConstraints);

        tf_defaultValue.setMaximumSize(null);
        tf_defaultValue.setMinimumSize(null);
        tf_defaultValue.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_defaultValue, gridBagConstraints);

        lbl_choiceProperties.setText("Multiple choice properties:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_choiceProperties, gridBagConstraints);

        jl_multiChoises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jl_multiChoises.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jl_multiChoisesValueChanged(evt);
            }
        });
        scrl_choice.setViewportView(jl_multiChoises);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrl_choice, gridBagConstraints);

        btn_addChoice.setText("Add");
        btn_addChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_addChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_addChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_addChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_addChoice, gridBagConstraints);

        btn_removeChoice.setText("Remove");
        btn_removeChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_removeChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_removeChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_removeChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_removeChoice, gridBagConstraints);

        lbl_choiceName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_choiceName.setText("Choice name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_choiceName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_choiceName, gridBagConstraints);

        lbl_choiceValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_choiceValue.setText("Choice value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_choiceValue, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_choiceValue, gridBagConstraints);

        lbl_wiki.setText("For detailed information and examples please visit our wiki on sourceforge.org!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(lbl_wiki, gridBagConstraints);

        lbl_number.setText("Number properties:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_number, gridBagConstraints);

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
        add(tf_minValue, gridBagConstraints);

        lbl_maximum.setText("Maximum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lbl_maximum, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tf_maxValue, gridBagConstraints);

        btn_saveSetting.setText("Update");
        btn_saveSetting.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_saveSetting.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_saveSetting.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_saveSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_saveSetting, gridBagConstraints);

        btn_saveChoice.setText("Update");
        btn_saveChoice.setMaximumSize(new java.awt.Dimension(80, 25));
        btn_saveChoice.setMinimumSize(new java.awt.Dimension(80, 25));
        btn_saveChoice.setPreferredSize(new java.awt.Dimension(80, 25));
        btn_saveChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveChoiceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btn_saveChoice, gridBagConstraints);

        cb_isShared.setText("Shared setting");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(cb_isShared, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void cmb_settingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_settingTypeActionPerformed
    setChoiceFieldsEnabled(cmb_settingType.getSelectedIndex() == SettingTypes.CHOICE.ordinal());
    setNumberFieldsEnabled(cmb_settingType.getSelectedIndex() == SettingTypes.NUMBER.ordinal());
}//GEN-LAST:event_cmb_settingTypeActionPerformed

private void jl_settingsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_settingsValueChanged
    updateSettingFields();
}//GEN-LAST:event_jl_settingsValueChanged

private void jl_multiChoisesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jl_multiChoisesValueChanged
    updateChoiceProperties();
}//GEN-LAST:event_jl_multiChoisesValueChanged

private void btn_addSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addSettingActionPerformed
    settingsModel.addSetting(cb_isShared.isSelected(), tf_settingName.getText(), SettingTypes.values()[cmb_settingType.getSelectedIndex()], tf_keyWord.getText(), tf_defaultValue.getText());
    settingsModel.refresh();
    jl_settings.repaint();
    saveGameData();
}//GEN-LAST:event_btn_addSettingActionPerformed

private void btn_removeSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeSettingActionPerformed
    if (jl_settings.getSelectedIndex() > -1) {
        settingsModel.removeSetting(jl_settings.getSelectedIndex());
        settingsModel.refresh();
        jl_settings.repaint();
        saveGameData();
    }
}//GEN-LAST:event_btn_removeSettingActionPerformed

private void btn_addChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
    if (gs != null) {
        choiceModel.addChoise(tf_choiceName.getText(), tf_choiceValue.getText());
        choiceModel.refresh();
        saveGameData();
        jl_settings.setSelectedIndex(choiceModel.getSize());
        jl_settings.repaint();
    }
}//GEN-LAST:event_btn_addChoiceActionPerformed

private void btn_removeChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
    if (gs != null && jl_multiChoises.getSelectedIndex() > -1) {
        choiceModel.removeChoise(jl_multiChoises.getSelectedIndex());
        saveGameData();
    }
}//GEN-LAST:event_btn_removeChoiceActionPerformed

private void btn_saveSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveSettingActionPerformed
    GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
    if (gs != null) {
        gs.setShared(cb_isShared.isSelected());
        gs.setName(tf_settingName.getText());
        gs.setDefaultValue(tf_defaultValue.getText());
        gs.setKeyWord(tf_keyWord.getText());
        gs.setType(SettingTypes.values()[cmb_settingType.getSelectedIndex()]);
        jl_multiChoises.repaint();
        if (cmb_settingType.getSelectedIndex() == SettingTypes.CHOICE.ordinal()) {
            saveChoiceProperties();
        }
        if (cmb_settingType.getSelectedIndex() == SettingTypes.NUMBER.ordinal()) {
            saveNumberProperties();
        }
        settingsModel.refresh();
        saveGameData();
    }
}//GEN-LAST:event_btn_saveSettingActionPerformed

private void btn_saveChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveChoiceActionPerformed
    GameSetting gs = (GameSetting) jl_settings.getSelectedValue();
    if (gs != null && jl_multiChoises.getSelectedIndex() > -1) {
        choiceModel.overrideChoise(tf_choiceName.getText(), tf_choiceValue.getText(), jl_multiChoises.getSelectedIndex());
        choiceModel.refresh();
        saveChoiceProperties();
        saveGameData();
    }
}//GEN-LAST:event_btn_saveChoiceActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addChoice;
    private javax.swing.JButton btn_addSetting;
    private javax.swing.JButton btn_removeChoice;
    private javax.swing.JButton btn_removeSetting;
    private javax.swing.JButton btn_saveChoice;
    private javax.swing.JButton btn_saveSetting;
    private javax.swing.JCheckBox cb_isShared;
    private javax.swing.JComboBox cmb_settingType;
    private javax.swing.JList jl_multiChoises;
    private javax.swing.JList jl_settings;
    private javax.swing.JLabel lbl_choiceName;
    private javax.swing.JLabel lbl_choiceProperties;
    private javax.swing.JLabel lbl_choiceValue;
    private javax.swing.JLabel lbl_defValue;
    private javax.swing.JLabel lbl_host;
    private javax.swing.JLabel lbl_join;
    private javax.swing.JLabel lbl_keyWord;
    private javax.swing.JLabel lbl_mapextension;
    private javax.swing.JLabel lbl_mappath;
    private javax.swing.JLabel lbl_maximum;
    private javax.swing.JLabel lbl_min;
    private javax.swing.JLabel lbl_number;
    private javax.swing.JLabel lbl_regKey;
    private javax.swing.JLabel lbl_relativeExePath;
    private javax.swing.JLabel lbl_settingName;
    private javax.swing.JLabel lbl_settingType;
    private javax.swing.JLabel lbl_settings;
    private javax.swing.JLabel lbl_wiki;
    private javax.swing.JScrollPane scrl_choice;
    private javax.swing.JScrollPane scrl_settings;
    private javax.swing.JTextField tf_choiceName;
    private javax.swing.JTextField tf_choiceValue;
    private javax.swing.JTextField tf_defaultValue;
    private javax.swing.JTextField tf_hostPattern;
    private javax.swing.JTextField tf_joinPattern;
    private javax.swing.JTextField tf_keyWord;
    private javax.swing.JTextField tf_mapExtension;
    private javax.swing.JTextField tf_mapPath;
    private javax.swing.JTextField tf_maxValue;
    private javax.swing.JTextField tf_minValue;
    private javax.swing.JTextField tf_regKey;
    private javax.swing.JTextField tf_relativeExePath;
    private javax.swing.JTextField tf_settingName;
    // End of variables declaration//GEN-END:variables
}
