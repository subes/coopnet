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

package coopnetclient.utils.ui;

import coopnetclient.utils.settings.Settings;
import java.awt.Color;
import javax.swing.UIManager;

public final class Colors {


    private static Color bgColor;
    private static Color fgColor;
    private static Color btnbgColor;
    private static Color tfbgColor;
    private static Color disabledColor;
    private static Color selectionColor;
    private static Color borderColor;

    //difference to bg color (calculated later)
    private static final int BTN_DIFF = 20;
    private static final int TF_DIFF = 10;
    private static final int DISABLED_DIFF = 75;

    private Colors(){}

    //public getters for colors
    public static Color getBackgroundColor() {
        return bgColor;
    }

    public static Color getForegroundColor() {
        return fgColor;
    }

    public static Color getButtonBackgroundColor() {
        return btnbgColor;
    }

    public static Color getTextfieldBackgroundColor() {
        return tfbgColor;
    }

    public static Color getDisabledColor() {
        return disabledColor;
    }

    public static Color getSelectionColor() {
        return selectionColor;
    }

    public static Color getBorderColor() {
        return borderColor;
    }

    public static void init() {
        if (Settings.getColorizeBody()) {
            bgColor = coopnetclient.utils.settings.Settings.getBackgroundColor();
            fgColor = coopnetclient.utils.settings.Settings.getForegroundColor();
            selectionColor = coopnetclient.utils.settings.Settings.getSelectionColor();
            borderColor = coopnetclient.utils.settings.Settings.getForegroundColor();
        } else {
            bgColor = null;
            fgColor = null;
            selectionColor = null;
            borderColor = null;
        }

        if (bgColor != null) {
            //Calculating other component colors
            int[] rgb = new int[3];

            //Button color
            rgb[0] = bgColor.getRed();
            rgb[1] = bgColor.getGreen();
            rgb[2] = bgColor.getBlue();
            for (int i = 0; i < rgb.length; i++) {
                if (rgb[i] >= 128) {
                    rgb[i] -= BTN_DIFF;
                } else {
                    rgb[i] += BTN_DIFF;
                }
            }
            btnbgColor = new Color(rgb[0], rgb[1], rgb[2]);

            //Textfield color
            rgb[0] = bgColor.getRed();
            rgb[1] = bgColor.getGreen();
            rgb[2] = bgColor.getBlue();
            for (int i = 0; i < rgb.length; i++) {
                if (rgb[i] >= 128) {
                    rgb[i] -= TF_DIFF;
                } else {
                    rgb[i] += TF_DIFF;
                }
            }
            tfbgColor = new Color(rgb[0], rgb[1], rgb[2]);

            //disabled item color
            rgb[0] = bgColor.getRed();
            rgb[1] = bgColor.getGreen();
            rgb[2] = bgColor.getBlue();
            for (int i = 0; i < rgb.length; i++) {
                if (rgb[i] >= 128) {
                    rgb[i] -= DISABLED_DIFF;
                } else {
                    rgb[i] += DISABLED_DIFF;
                }
            }
            disabledColor = new Color(rgb[0], rgb[1], rgb[2]);
        } else {
            btnbgColor = null;
            tfbgColor = null;
            disabledColor = null;
        }

        modifyUI();
    }

    private static void modifyUI() {
        UIManager.put("TabbedPane.background", bgColor);
        UIManager.put("TabbedPane.borderHightlightColor", null);
        UIManager.put("TabbedPane.contentAreaColor", bgColor);
        UIManager.put("TabbedPane.darkShadow", null);
        UIManager.put("TabbedPane.focus", bgColor);
        UIManager.put("TabbedPane.foreground", fgColor);
        UIManager.put("TabbedPane.highlight", null);
        UIManager.put("TabbedPane.light", null);
        UIManager.put("TabbedPane.selectHighlight", null);
        UIManager.put("TabbedPane.selected", bgColor);
        UIManager.put("TabbedPane.shadow", null);
        UIManager.put("TabbedPane.tabAreaBackground", null);
        UIManager.put("TabbedPane.unselectedBackground", selectionColor);

        UIManager.put("Menu.acceleratorForeground", bgColor);
        UIManager.put("Menu.acceleratorSelectionForeground", fgColor);
        UIManager.put("Menu.background", bgColor);
        UIManager.put("Menu.disabledForeground", disabledColor);
        UIManager.put("Menu.foreground", fgColor);
        UIManager.put("Menu.selectionBackground", selectionColor);
        UIManager.put("Menu.selectionForeground", fgColor);

        UIManager.put("MenuItem.acceleratorForeground", null);
        UIManager.put("MenuItem.acceleratorSelectionForeground", null);
        UIManager.put("MenuItem.background", bgColor);
        UIManager.put("MenuItem.disabledForeground", disabledColor);
        UIManager.put("MenuItem.foreground", fgColor);
        UIManager.put("MenuItem.selectionBackground", selectionColor);
        UIManager.put("MenuItem.selectionForeground", fgColor);

        UIManager.put("TableHeader.background", bgColor);
        UIManager.put("TableHeader.focusCellBackground", null);
        UIManager.put("TableHeader.foreground", fgColor);

        UIManager.put("Table.background", bgColor);
        UIManager.put("Table.dropCellBackground", null);
        UIManager.put("Table.dropLineColor", null);
        UIManager.put("Table.dropLineShortColor", null);
        UIManager.put("Table.focusCellBackground", null);
        UIManager.put("Table.focusCellForeground", null);
        UIManager.put("Table.foreground", fgColor);
        UIManager.put("Table.gridColor", null);
        UIManager.put("Table.selectionBackground", btnbgColor);
        UIManager.put("Table.selectionForeground", fgColor);
        UIManager.put("Table.sortIconColor", fgColor);

        UIManager.put("TitledBorder.titleColor", fgColor);

        UIManager.put("PopupMenu.background", bgColor);
        UIManager.put("PopupMenu.foreground", fgColor);

        //No need to recursively color these yet
        UIManager.put("ComboBox.background", btnbgColor);
        UIManager.put("ComboBox.buttonBackground", btnbgColor);
        UIManager.put("ComboBox.buttonDarkShadow", null);
        UIManager.put("ComboBox.buttonHighlight", null);
        UIManager.put("ComboBox.buttonShadow", null);
        UIManager.put("ComboBox.disabledBackground", btnbgColor);
        UIManager.put("ComboBox.disabledForeground", disabledColor);
        UIManager.put("ComboBox.foreground", fgColor);
        UIManager.put("ComboBox.selectionBackground", selectionColor);
        UIManager.put("ComboBox.selectionForeground", fgColor);

        UIManager.put("Spinner.background", bgColor);
        UIManager.put("Spinner.foreground", fgColor);

        UIManager.put("ToolTip.background", tfbgColor);
        UIManager.put("ToolTip.backgroundInactive", tfbgColor);
        UIManager.put("ToolTip.foreground", fgColor);
        UIManager.put("ToolTip.foregroundInactive", disabledColor);

        //BEGIN***************JOPTIONPANE COLORING
        UIManager.put("Button.background", btnbgColor);
        UIManager.put("Button.darkShadow", null);
        UIManager.put("Button.disabledText", disabledColor);
        UIManager.put("Button.disabledToolBarBorderBackground", disabledColor);
        UIManager.put("Button.focus", null);
        UIManager.put("Button.foreground", fgColor);
        UIManager.put("Button.highlight", null);
        UIManager.put("Button.light", null);
        UIManager.put("Button.select", selectionColor);
        UIManager.put("Button.shadow", null);
        UIManager.put("Button.toolBarBorderBackground", borderColor);

        UIManager.put("Panel.background", bgColor);
        UIManager.put("Panel.foreground", fgColor);

        UIManager.put("OptionPane.background", bgColor);
        UIManager.put("OptionPane.errorDialog.border.background", borderColor);
        UIManager.put("OptionPane.errorDialog.titlePane.background", bgColor);
        UIManager.put("OptionPane.errorDialog.titlePane.foreground", fgColor);
        UIManager.put("OptionPane.errorDialog.titlePane.shadow", null);
        UIManager.put("OptionPane.foreground", fgColor);
        UIManager.put("OptionPane.messageForeground", fgColor);
        UIManager.put("OptionPane.questionDialog.border.background", borderColor);
        UIManager.put("OptionPane.questionDialog.titlePane.background", bgColor);
        UIManager.put("OptionPane.questionDialog.titlePane.foreground", fgColor);
        UIManager.put("OptionPane.questionDialog.titlePane.shadow", null);
        UIManager.put("OptionPane.warningDialog.border.background", borderColor);
        UIManager.put("OptionPane.warningDialog.titlePane.background", bgColor);
        UIManager.put("OptionPane.warningDialog.titlePane.foreground", fgColor);
        UIManager.put("OptionPane.warningDialog.titlePane.shadow", null);
    //END***************JOPTIONPANE COLORING

    /* UNUSED COMPONENT TYPES, DO NOT REMOVE
    UIManager.put("control", btnbgColor);
    UIManager.put("controlDkShadow", fgColor);
    UIManager.put("controlHighlight", null);
    UIManager.put("controlLtHighlight", null);
    UIManager.put("controlShadow", null);
    UIManager.put("controlText", fgColor);
    UIManager.put("MenuBar.background", bgColor);
    UIManager.put("MenuBar.borderColor", null);
    UIManager.put("MenuBar.foreground", fgColor);
    UIManager.put("MenuBar.highlight", null);
    UIManager.put("MenuBar.shadow", null);
    UIManager.put("CheckBoxMenuItem.acceleratorForeground", null);
    UIManager.put("CheckBoxMenuItem.acceleratorSelectionForeground", null);
    UIManager.put("CheckBoxMenuItem.background", bgColor);
    UIManager.put("CheckBoxMenuItem.disabledForeground", disabledColor);
    UIManager.put("CheckBoxMenuItem.foreground", fgColor);
    UIManager.put("CheckBoxMenuItem.selectionBackground", fgColor);
    UIManager.put("CheckBoxMenuItem.selectionForeground", bgColor);
    UIManager.put("RadioButtonMenuItem.acceleratorForeground", null);
    UIManager.put("RadioButtonMenuItem.acceleratorSelectionForeground", null);
    UIManager.put("RadioButtonMenuItem.background", bgColor);
    UIManager.put("RadioButtonMenuItem.disabledForeground", disabledColor);
    UIManager.put("RadioButtonMenuItem.foreground", fgColor);
    UIManager.put("RadioButtonMenuItem.selectionBackground", fgColor);
    UIManager.put("RadioButtonMenuItem.selectionForeground", bgColor);
    UIManager.put("Label.background", bgColor);
    UIManager.put("Label.disabledForeground", disabledColor);
    UIManager.put("Label.disabledShadow", null);
    UIManager.put("Label.foreground", fgColor);
    UIManager.put("ToggleButton.background", btnbgColor);
    UIManager.put("ToggleButton.darkShadow", null);
    UIManager.put("ToggleButton.disabledText", disabledColor);
    UIManager.put("ToggleButton.focus", null);
    UIManager.put("ToggleButton.foreground", fgColor);
    UIManager.put("ToggleButton.highlight", null);
    UIManager.put("ToggleButton.light", null);
    UIManager.put("ToggleButton.select", null);
    UIManager.put("ToggleButton.shadow", null);
    UIManager.put("CheckBox.background", bgColor);
    UIManager.put("CheckBox.disabledText", disabledColor);
    UIManager.put("CheckBox.focus", null);
    UIManager.put("CheckBox.foreground", fgColor);
    UIManager.put("Checkbox.select", null);
    UIManager.put("RadioButton.background", bgColor);
    UIManager.put("RadioButton.darkShadow", null);
    UIManager.put("RadioButton.disabledText", disabledColor);
    UIManager.put("RadioButton.focus", null);
    UIManager.put("RadioButton.foreground", fgColor);
    UIManager.put("RadioButton.highlight", null);
    UIManager.put("RadioButton.light", null);
    UIManager.put("RadioButton.select", null);
    UIManager.put("RadioButton.shadow", null);
    UIManager.put("List.background", bgColor);
    UIManager.put("List.dropCellBackground", null);
    UIManager.put("List.dropLineColor", null);
    UIManager.put("List.foreground", fgColor);
    UIManager.put("List.selectionBackground", fgColor);
    UIManager.put("List.selectionForeground", bgColor);
    UIManager.put("PasswordField.background", tfbgColor);
    UIManager.put("PasswordField.caretForeground", fgColor);
    UIManager.put("PasswordField.foreground", fgColor);
    UIManager.put("PasswordField.inactiveBackground", tfbgColor);
    UIManager.put("PasswordField.inactiveForeground", disabledColor);
    UIManager.put("PasswordField.selectionBackground", fgColor);
    UIManager.put("PasswordField.selectionForeground", tfbgColor);
    UIManager.put("TextField.background", tfbgColor);
    UIManager.put("TextField.caretForeground", fgColor);
    UIManager.put("TextField.darkShadow", null);
    UIManager.put("TextField.foreground", fgColor);
    UIManager.put("TextField.highlight", null);
    UIManager.put("TextField.inactiveBackground", tfbgColor);
    UIManager.put("TextField.inactiveForeground", disabledColor);
    UIManager.put("TextField.light", null);
    UIManager.put("TextField.selectionBackground", fgColor);
    UIManager.put("TextField.selectionForeground", tfbgColor);
    UIManager.put("TextField.shadow", null);
    UIManager.put("TextArea.background", tfbgColor);
    UIManager.put("TextArea.caretForeground", fgColor);
    UIManager.put("TextArea.foreground", fgColor);
    UIManager.put("TextArea.inactiveForeground", disabledColor);
    UIManager.put("TextArea.selectionBackground", fgColor);
    UIManager.put("TextArea.selectionForeground", tfbgColor);
    UIManager.put("TextPane.background", tfbgColor);
    UIManager.put("TextPane.caretForeground", fgColor);
    UIManager.put("TextPane.foreground", fgColor);
    UIManager.put("TextPane.inactiveForeground", disabledColor);
    UIManager.put("TextPane.selectionBackground", fgColor);
    UIManager.put("TextPane.selectionForeground", tfbgColor);
    UIManager.put("FormattedTextField.background", tfbgColor);
    UIManager.put("FormattedTextField.caretForeground", fgColor);
    UIManager.put("FormattedTextField.foreground", fgColor);
    UIManager.put("FormattedTextField.inactiveBackground", tfbgColor);
    UIManager.put("FormattedTextField.inactiveForeground", disabledColor);
    UIManager.put("FormattedTextField.selectionBackground", fgColor);
    UIManager.put("FormattedTextField.selectionForeground", tfbgColor);
    UIManager.put("EditorPane.background", tfbgColor);
    UIManager.put("EditorPane.caretForeground", fgColor);
    UIManager.put("EditorPane.foreground", fgColor);
    UIManager.put("EditorPane.inactiveForeground", disabledColor);
    UIManager.put("EditorPane.selectionBackground", fgColor);
    UIManager.put("EditorPane.selectionForeground", tfbgColor);
    UIManager.put("ProgressBar.background", bgColor);
    UIManager.put("ProgressBar.foreground", fgColor);
    UIManager.put("ProgressBar.selectionBackground",fgColor);
    UIManager.put("ProgressBar.selectionForeground", bgColor);
    UIManager.put("ScrollBar.background", null);
    UIManager.put("ScrollBar.darkShadow", null);
    UIManager.put("ScrollBar.foreground", null);
    UIManager.put("ScrollBar.highlight", null);
    UIManager.put("ScrollBar.shadow", null);
    UIManager.put("ScrollBar.thumb", null);
    UIManager.put("ScrollBar.thumbDarkShadow", null);
    UIManager.put("ScrollBar.thumbHighlight", null);
    UIManager.put("ScrollBar.thumbShadow", null);
    UIManager.put("ScrollBar.track", null);
    UIManager.put("ScrollBar.trackHighlight", null);
    UIManager.put("ScrollPane.background", null);
    UIManager.put("ScrollPane.foreground", null);
    UIManager.put("Slider.altTrackColor", null);
    UIManager.put("Slider.background", bgColor);
    UIManager.put("Slider.focus", null);
    UIManager.put("Slider.foreground", fgColor);
    UIManager.put("Slider.highlight", null);
    UIManager.put("Slider.shadow", null);
    UIManager.put("Slider.tickColor", null);

    UIManager.put("ColorChooser.background", bgColor);
    UIManager.put("ColorChooser.foreground", fgColor);
    UIManager.put("ColorChooser.swatchesDefaultRecentColor", null);
    UIManager.put("PropSheet.disabledForeground", disabledColor);
    UIManager.put("PropSheet.selectedSetBackground", fgColor);
    UIManager.put("PropSheet.selectedSetForeground", bgColor);
    UIManager.put("PropSheet.selectionBackground", fgColor);
    UIManager.put("PropSheet.selectionForeground", bgColor);
    UIManager.put("PropSheet.setBackground", bgColor);
    UIManager.put("PropSheet.setForeground", fgColor);
    UIManager.put("Viewport.background", bgColor);
    UIManager.put("Viewport.foreground", fgColor);

    UIManager.put("SplitPane.background", bgColor);
    UIManager.put("SplitPane.darkShadow", null);
    UIManager.put("SplitPane.dividerFocusColor", null);
    UIManager.put("SplitPane.highlight", null);
    UIManager.put("SplitPane.shadow", null);
    UIManager.put("SplitPaneDivider.draggingColor", null);
    UIManager.put("Separator.background", bgColor);
    UIManager.put("Separator.foreground", fgColor);
    UIManager.put("Separator.highlight", null);
    UIManager.put("Separator.shadow", null);
    //Not Changed
    UIManager.put("TabRenderer.selectedActivatedBackground", null);
    UIManager.put("TabRenderer.selectedActivatedForeground", null);
    UIManager.put("TabRenderer.selectedForeground", null);
    UIManager.put("Desktop.background", null);
    UIManager.put("DesktopIcon.background", null);
    UIManager.put("DesktopIcon.foreground", null);
    UIManager.put("InternalFrame.activeTitleBackground", null);
    UIManager.put("InternalFrame.activeTitleForeground", null);
    UIManager.put("InternalFrame.borderColor", null);
    UIManager.put("InternalFrame.borderDarkShadow", null);
    UIManager.put("InternalFrame.borderHighlight", null);
    UIManager.put("InternalFrame.borderLight", null);
    UIManager.put("InternalFrame.borderShadow", null);
    UIManager.put("InternalFrame.inactiveTitleBackground", null);
    UIManager.put("InternalFrame.inactiveTitleForeground", null);
    UIManager.put("Nb.ScrollPane.Border.color", null);
    UIManager.put("ToolBar.background", null);
    UIManager.put("ToolBar.borderColor", null);
    UIManager.put("ToolBar.darkShadow", null);
    UIManager.put("ToolBar.dockingBackground", null);
    UIManager.put("ToolBar.dockingForeground", null);
    UIManager.put("ToolBar.floatingBackground", null);
    UIManager.put("ToolBar.floatingForeground", null);
    UIManager.put("ToolBar.foreground", null);
    UIManager.put("ToolBar.highlight", null);
    UIManager.put("ToolBar.light", null);
    UIManager.put("ToolBar.shadow", null);
    UIManager.put("Tree.background", null);
    UIManager.put("Tree.dropCellBackground", null);
    UIManager.put("Tree.dropLineColor", null);
    UIManager.put("Tree.foreground", null);
    UIManager.put("Tree.hash", null);
    UIManager.put("Tree.line", null);
    UIManager.put("Tree.selectionBackground", null);
    UIManager.put("Tree.selectionBorderColor", null);
    UIManager.put("Tree.selectionForeground", null);
    UIManager.put("Tree.textBackground", null);
    UIManager.put("Tree.textForeground", null);
    UIManager.put("activeCaption", null);
    UIManager.put("activeCaptionBorder", null);
    UIManager.put("activeCaptionText", null);
    UIManager.put("desktop", null);
    UIManager.put("inactiveCaption", null);
    UIManager.put("inactiveCaptionBorder", null);
    UIManager.put("inactiveCaptionText", null);
    UIManager.put("info", null);
    UIManager.put("infoText", null);
    UIManager.put("menu", null);
    UIManager.put("menuText", null);
    UIManager.put("nb.errorForeground", null);
    UIManager.put("nb.explorer.unfocusedSelBg", null);
    UIManager.put("nb.warningForeground", null);
    UIManager.put("nbProgressBar.popupDynaText.foreground", null);
    UIManager.put("nbProgressBar.popupText.foreground", null);
    UIManager.put("nbProgressBar.popupText.selectBackground", null);
    UIManager.put("nbProgressBar.popupText.selectForeground", null);
    UIManager.put("scrollbar", btnbgColor);
    UIManager.put("text", fgColor);
    UIManager.put("textHighlight", null);
    UIManager.put("textHighlightText", null);
    UIManager.put("textInactiveText", disabledColor);
    UIManager.put("textText", null);
    UIManager.put("window", bgColor);
    UIManager.put("windowBorder", null);
    UIManager.put("windowText", fgColor);
     */
    }
}
