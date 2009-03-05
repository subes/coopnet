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
import coopnetclient.frames.components.CustomScrollBarUI;
import coopnetclient.frames.components.TextComponentPopupMenu;
import coopnetclient.frames.listeners.TabbedPaneColorChangeListener;
import coopnetclient.utils.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
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
 */
public final class Colorizer {

   
    private static Vector<Component> toExecuteCustomCodeIn;

    private Colorizer() {
    }

    public static boolean isCurrentLafSupportedForColoring() {
        return isLafSupportedForColoring(UIManager.getLookAndFeel().getName());
    }

    public static boolean isLafSupportedForColoring(String lafName) {
        return lafName.equals("Metal");
    }

    public static void init() {
        try {
            if (Settings.getUseNativeLookAndFeel()) {
                throw new Exception("Invoking catch stuff!");
            } else {
                String selectedLAF = Settings.getSelectedLookAndFeel();
                LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();

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

    public static void colorize(Container root) {

        if (root == null) {
            return;
        }
        if (root instanceof Window) {
            Window frame = (Window) root;
            frame.setIconImage(Icons.coopnetNormalIcon.getImage());
        }

        toExecuteCustomCodeIn = new Vector<Component>();

        if (root instanceof JFrame) {
            JFrame frame = (JFrame) root;
            toExecuteCustomCodeIn.add(frame);
            enableEffectsRecursively(frame.getContentPane());
            addPopupMenusRecursively(frame.getContentPane());
            if (frame.getJMenuBar() != null) {
                enableEffectsRecursively(frame.getJMenuBar());
            }
        } else {
            //Treat it normally
            enableEffectsRecursively(root);
            addPopupMenusRecursively(root);
        }

        //First collect, then run (no problems then)
        executeCustomCode();
    }

    private static void addPopupMenusRecursively(Container root) {
        Component[] components = root.getComponents();
        toExecuteCustomCodeIn.add(root);

        for (Component c : components) {
            if (c instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) c;
                tc.setComponentPopupMenu(new TextComponentPopupMenu(tc));
            }

            toExecuteCustomCodeIn.add(c);

            if (c instanceof Container) {
                addPopupMenusRecursively((Container) c);
            }
        }

    }

    //Only local usage, recursive core function
    private static void enableEffectsRecursively(Container root) {

        //Dont colorize if following is the case
        if (isCurrentLafSupportedForColoring() == false || root == null || (!Settings.getColorizeBody() && (root instanceof JFileChooser || root instanceof JDialog))) {
            return;
        }

        //colorize root, if not already done in different color
        if (!(root instanceof JButton) && !(root instanceof JTextComponent)) {
            root.setForeground(Colors.getForegroundColor());
            root.setBackground(Colors.getBackgroundColor());
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
                if (Colors.getBackgroundColor() != null) {
                    lst.setSelectionBackground(Colors.getSelectionColor());
                    lst.setSelectionForeground(Colors.getForegroundColor());
                } else {
                    lst.setSelectionBackground((Color) UIManager.get("List.selectionBackground"));
                    lst.setSelectionForeground((Color) UIManager.get("List.selectionForeground"));
                }
            } else if (c instanceof JTable) {
                JTable tbl = (JTable) c;
                tbl.setUI(new BasicTableUI());
                if (Colors.getBackgroundColor() != null) {
                    tbl.setSelectionBackground(Colors.getSelectionColor());
                    tbl.setSelectionForeground(Colors.getForegroundColor());
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
                    if (cl instanceof TabbedPaneColorChangeListener) {
                        tp.removeChangeListener(cl);
                        if (tp.getTabCount() > 0) {
                            for (int i = 0; i < tp.getTabCount(); i++) {
                                tp.setBackgroundAt(i, null);
                            }
                        }
                    }
                }
                if (Colors.getBackgroundColor() != null) {
                    tp.addChangeListener(new TabbedPaneColorChangeListener(tp));
                }

            } else if (c instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) c;

                if (c instanceof JTextField) {
                    if (!(c.getParent().getClass().getName().contains("JSpinner"))) {
                        //Dont do this, if we have a JSpinner
                        tc.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
                    }
                }
                if (Colors.getBackgroundColor() != null) {
                    tc.setForeground(Colors.getForegroundColor());
                    tc.setBackground(Colors.getTextfieldBackgroundColor());
                    tc.setCaretColor(Colors.getForegroundColor());
                    tc.setSelectedTextColor(Colors.getForegroundColor());
                    tc.setSelectionColor(Colors.getSelectionColor());

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
                    tb.setTitleColor(Colors.getForegroundColor());
                    tb.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
                }
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
            } else if (c instanceof JScrollBar) {
                JScrollBar sb = (JScrollBar) c;

                if (Colors.getBackgroundColor() != null) {
                    sb.setUI(new CustomScrollBarUI());
                } else {
                    sb.setUI(new MetalScrollBarUI());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setForeground(Colors.getForegroundColor());
                btn.setBackground(Colors.getButtonBackgroundColor());

            } else if (c instanceof JComboBox) {
                JComboBox cb = (JComboBox) c;
                cb.setBorder(javax.swing.BorderFactory.createLineBorder(Colors.getBorderColor()));
                cb.updateUI();
            } else if (c instanceof JSplitPane) {
                JSplitPane sp = (JSplitPane) c;
                setSplitPaneDividerColor(sp, Colors.getBackgroundColor());
            } else if (c instanceof JSpinner) {
                JSpinner spnr = (JSpinner) c;
                spnr.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor()));
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
                if (m.getName().equals("customCodeForColoring") || m.getName().equals("customCodeForPopupMenu")) {
                    try {
                        m.invoke(obj);
                    } catch (Exception e) {
                        Logger.log(e);
                    }
                }
            }
        }
    }

    

    private static void setSplitPaneDividerColor(JSplitPane splitPane, Color newDividerColor) {
        SplitPaneUI splitUI = splitPane.getUI();
        if (splitUI instanceof BasicSplitPaneUI) { // obviously this will not work if the ui doen't extend Basic...
            BasicSplitPaneDivider div = ((BasicSplitPaneUI) splitUI).getDivider();
            assert div != null;
            Border divBorder = div.getBorder();
            Border newBorder = null;
            Border colorBorder = null;

            final class BGBorder implements Border {

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
}
