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

package coopnetclient.coloring;

import coopnetclient.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.text.JTextComponent;

/* TODO:
 *    - colorize Controlbuttons in the same manner as the scrollbar buttons
 *    - find out how to change the scrollbar of jcombobox
 *    - change implementation of joptionpanes, so they become colorizable JDialog
 */
public class Colorizer {

    private static Color bgColor,  fgColor,  btnbgColor,  tfbgColor,  disabledColor,  selectionColor,  borderColor;
    private static Vector<Component> toExecuteCustomCodeIn;
    
    //difference to bg color (calculated later)
    private final static int BTN_DIFF = 20;
    private final static int TF_DIFF = 10;
    private final static int DISABLED_DIFF = 75;
    private static Image baseiconimage = Toolkit.getDefaultToolkit().getImage("data/icons/icon.gif");
    

    static {
        //init colors on first usage
        initColors();
    }

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
    
    /**************************************************************/

    //Use this for Frames
    public static void colorize(Container root) {

        if (root instanceof JFrame) {
            JFrame frame = (JFrame) root;
            frame.setIconImage(baseiconimage);
        }

        //Dont colorize if following is the case
        if (        getCurrentLAFisSupportedForColoring() == false 
                ||  root == null
                || (!Settings.getColorizeBody() && (root instanceof JFileChooser || root instanceof JDialog))
        ){
            return;
        }

        toExecuteCustomCodeIn = new Vector<Component>();

        if (root instanceof JFrame) {
            JFrame frame = (JFrame) root;
            toExecuteCustomCodeIn.add(frame);
            enableEffectsRecursively(frame.getContentPane());
            if (frame.getJMenuBar() != null) {
                enableEffectsRecursively(frame.getJMenuBar());
            }
        } else {
            //Treat it normally
            enableEffectsRecursively(root);
        }

        //First collect, then run (no problems then)
        executeCustomCode();
    }
    //Calculating colors (only use from settings frame)
    public static void initColors() {

        if (coopnetclient.Settings.getColorizeBody()) {
            bgColor = coopnetclient.Settings.getBackgroundColor();
            fgColor = coopnetclient.Settings.getForegroundColor();
            selectionColor = coopnetclient.Settings.getSelectionColor();
            borderColor = coopnetclient.Settings.getTitledBorderColor();
        //System.out.println("coloring");
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

        //If here, we call it only once
        modifyUI();
    }
    //Sets the LAF (only used by Client @ Startup)
    public static void initLAF() {
        try {
            if (Settings.getUseNativeLookAndFeel()) {
                throw new Exception("Invoking catch stuff!");
            } else {
                String selectedLAF = Settings.getSelectedLookAndFeel();
                UIManager.LookAndFeelInfo infos[] = UIManager.getInstalledLookAndFeels();

                String selectedLAFClass = null;
                for (int i = 0; i < infos.length; i++) {
                    if (selectedLAF.equals(infos[i].getName())) {
                        selectedLAFClass = infos[i].getClassName();
                        break;
                    }
                }

                if (selectedLAFClass != null) {
                    UIManager.setLookAndFeel(selectedLAFClass);
                } else {
                    //Invoke the catch stuff
                    throw new Exception("LAF not found!");
                }
            }
        } catch (Exception e) {
            //Set native LAF, if there was an exception
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("SystemLAF not found! Will revert to CrossPlatformLAF.");
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception exc) {
                    System.err.println("CrossPlatformLAF not found!?");
                    ex.printStackTrace();
                }
            }
        }
    }

    public static boolean getCurrentLAFisSupportedForColoring() {
        if (UIManager.getLookAndFeel().getName().equals("Metal")) {
            return true;
        }

        return false;
    }

    /*****************************************************************/    
    
    //Only local usage, recursive core function
    private static void enableEffectsRecursively(Container root) {

        //colorize root, if not already done in different color
        if (!(root instanceof JButton) && !(root instanceof JTextComponent)) {
            root.setForeground(fgColor);
            root.setBackground(bgColor);
        }
        toExecuteCustomCodeIn.add(root);

        //Read components of root
        Component[] components;

        if (root instanceof JMenuBar) {
            JMenuBar mbar = (JMenuBar) root;
            components = new Component[mbar.getMenuCount()];
            for (int i = 0; i < components.length; i++) {
                components[i] = mbar.getMenu(i);
            }
        } else if (root instanceof JMenu) {
            JMenu m = (JMenu) root;
            components = m.getMenuComponents();
        } else {
            //Treat it normally
            components = root.getComponents();
        }

        //Colorize components
        for (Component c : components) {

            if (c instanceof JList) {
                JList lst = (JList) c;
                if (bgColor != null) {
                    lst.setSelectionBackground(selectionColor);
                    lst.setSelectionForeground(fgColor);
                } else {
                    lst.setSelectionBackground((Color) UIManager.get("List.selectionBackground"));
                    lst.setSelectionForeground((Color) UIManager.get("List.selectionForeground"));
                }
            } else if (c instanceof JTable) {
                JTable tbl = (JTable) c;
                tbl.setUI(new BasicTableUI());
                if (bgColor != null) {
                    tbl.setSelectionBackground(selectionColor);
                    tbl.setSelectionForeground(fgColor);
                } else {
                    tbl.setSelectionBackground((Color) UIManager.get("Table.selectionBackground"));
                    tbl.setSelectionForeground((Color) UIManager.get("Table.selectionForeground"));
                }
            } else if (c instanceof JMenu) {
                JMenu m = (JMenu) c;
                m.setUI(new BasicMenuUI());
            } else if (c instanceof JMenuItem) {
                if (c instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
                    cbmi.setUI(new BasicCheckBoxMenuItemUI());
                } else {
                    JMenuItem mi = (JMenuItem) c;
                    mi.setUI(new BasicMenuItemUI());
                }
            } else if (c instanceof JTabbedPane) {
                JTabbedPane tp = (JTabbedPane) c;
                tp.setUI(new MetalTabbedPaneUI());

                ChangeListener[] listeners = tp.getChangeListeners();
                for (ChangeListener cl : listeners) {
                    if (cl instanceof TabbedPaneColorCL) {
                        tp.removeChangeListener(cl);
                        if (tp.getTabCount() > 0) {
                            for (int i = 0; i < tp.getTabCount(); i++) {
                                tp.setBackgroundAt(i, null);
                            }
                        }
                    }
                }
                if (bgColor != null) {
                    tp.addChangeListener(new TabbedPaneColorCL(tp));
                }

            } else if (c instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) c;
                if (c instanceof JTextField) {
                    if (!(c.getParent().getClass().getName().contains("JSpinner"))) { 
                        //Dont do this, if we have a JSpinner
                        tc.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
                    }
                }
                if (bgColor != null) {
                    tc.setForeground(fgColor);
                    tc.setBackground(tfbgColor);
                    tc.setCaretColor(fgColor);
                    tc.setSelectedTextColor(fgColor);
                    tc.setSelectionColor(selectionColor);

                } else {
                    tc.setForeground((Color) UIManager.get("TextArea.foreground"));
                    tc.setBackground((Color) UIManager.get("TextArea.background"));
                    tc.setCaretColor((Color) UIManager.get("TextArea.caretForeground"));
                    tc.setSelectedTextColor((Color) UIManager.get("TextArea.selectionForeground"));
                    tc.setSelectionColor((Color) UIManager.get("TextArea.selectionBackground"));

                }
            } else if (c instanceof JPanel) {
                JPanel pnl = (JPanel) c;
                Border b = pnl.getBorder();

                if (b != null && b instanceof TitledBorder) {
                    TitledBorder tb = (TitledBorder) b;
                    tb.setTitleColor(fgColor);
                    tb.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
                }
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
            } else if (c instanceof JScrollBar) {
                JScrollBar sb = (JScrollBar) c;

                if (bgColor != null) {
                    sb.setUI(new CustomScrollBarUI());
                } else {
                    sb.setUI(new MetalScrollBarUI());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setForeground(fgColor);
                btn.setBackground(btnbgColor);

            } else if (c instanceof JComboBox) {
                JComboBox cb = (JComboBox) c;
                cb.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
                cb.updateUI();
            } else if (c instanceof JSplitPane) {
                JSplitPane sp = (JSplitPane) c;
                setSplitPaneDividerColor(sp, bgColor);
            } else if (c instanceof JSpinner) {
                JSpinner spnr = (JSpinner) c;
                spnr.setBorder(BorderFactory.createLineBorder(borderColor));
            }

            //Component may have his own code, too
            toExecuteCustomCodeIn.add(c);

            //Recursive call if its another Container
            if (c instanceof JList) {
                JList lst = (JList) c;
                if (lst.getComponentPopupMenu() != null) {
                    enableEffectsRecursively(lst.getComponentPopupMenu());
                }
            }
            if (c instanceof Container) {
                enableEffectsRecursively((Container) c);
            }
        }
    }

    private static void executeCustomCode() {
        for (int i = 0; i < toExecuteCustomCodeIn.size(); i++) {

            Component obj = toExecuteCustomCodeIn.get(i);

            Method[] methods = obj.getClass().getDeclaredMethods();

            for (Method m : methods) {
                if (m.getName().equals("customCodeForColorizer")) {
                    try {
                        m.invoke(obj);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
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

    private static void setSplitPaneDividerColor(JSplitPane splitPane, Color newDividerColor) {
        SplitPaneUI splitUI = splitPane.getUI();
        if (splitUI instanceof BasicSplitPaneUI) { // obviously this will not work if the ui doen't extend Basic...
            BasicSplitPaneDivider div = ((BasicSplitPaneUI) splitUI).getDivider();
            assert div != null;
            Border divBorder = div.getBorder();
            Border newBorder = null;
            Border colorBorder = null;

            class BGBorder implements Border {

                private Color color;
                private final Insets NO_INSETS = new Insets(0, 0, 0, 0);

                private BGBorder(Color color) {
                    this.color = color;
                }
                Rectangle r = new Rectangle();

                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    g.setColor(color);
                    g.fillRect(x, y, width, height);
                    if (c instanceof Container) {
                        Container cont = (Container) c;
                        for (int i = 0, n = cont.getComponentCount(); i < n; i++) {
                            Component comp = cont.getComponent(i);
                            comp.getBounds(r);
                            Graphics tmpg = g.create(r.x, r.y, r.width, r.height);
                            comp.paint(tmpg);
                            tmpg.dispose();
                        }
                    }
                }

                @Override
                public Insets getBorderInsets(Component c) {
                    return NO_INSETS;
                }

                @Override
                public boolean isBorderOpaque() {
                    return true;
                }
            }

            colorBorder = new BGBorder(newDividerColor);

            if (divBorder == null) {
                newBorder = colorBorder;
            } else {
                newBorder = BorderFactory.createCompoundBorder(null, colorBorder);
            }
            div.setBorder(newBorder);
        }
    }
    /*END******************PRIVATE METHODS***********************************/
}
