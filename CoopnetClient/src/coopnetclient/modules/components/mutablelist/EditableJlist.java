/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

 /**
 * Based on http://www.jroller.com/santhosh/entry/making_jlist_editable_no_jtable
 */

package coopnetclient.modules.components.mutablelist;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.event.*;

public class EditableJlist extends JList implements CellEditorListener {

    protected Component editorComp = null;
    protected int editingIndex = -1;
    protected ListCellEditor editor = null;
    private PropertyChangeListener editorRemover = null;

    public EditableJlist() {
        super();
        init();
    }

    public EditableJlist(ListModel dataModel) {
        super(dataModel);
        init();
    }

    private void init() {
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
    }

    public void setListCellEditor(ListCellEditor editor) {
        this.editor = editor;
    }

    public ListCellEditor getListCellEditor() {
        return editor;
    }

    public boolean isEditing() {
        return (editorComp == null) ? false : true;
    }

    public Component getEditorComponent() {
        return editorComp;
    }

    public int getEditingIndex() {
        return editingIndex;
    }

    public Component prepareEditor(int index) {
        Object value = getModel().getElementAt(index);
        boolean isSelected = isSelectedIndex(index);
        Component comp = editor.getListCellEditorComponent(this, value, isSelected, index);
        if (comp instanceof JComponent) {
            JComponent jComp = (JComponent) comp;
        }
        return comp;
    }

    public void removeEditor() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                removePropertyChangeListener("permanentFocusOwner", editorRemover);
        editorRemover = null;

        if (editor != null) {
            editor.removeCellEditorListener(this);

            if (editorComp != null) {
                remove(editorComp);
            }

            Rectangle cellRect = getCellBounds(editingIndex, editingIndex);

            editingIndex = -1;
            editorComp = null;

            repaint(cellRect);
        }
    }

    public boolean editCellAt(int index, EventObject e) {
        if (editor != null && !editor.stopCellEditing()) {
            return false;
        }
        if (index < 0 || index >= getModel().getSize()) {
            return false;
        }
        if (!isCellEditable(index)) {
            return false;
        }
        if (editorRemover == null) {
            KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            editorRemover = new CellEditorRemover(fm);
            fm.addPropertyChangeListener("permanentFocusOwner", editorRemover);
        }

        if (editor != null && editor.isCellEditable(e)) {
            editorComp = prepareEditor(index);
            if (editorComp == null) {
                removeEditor();
                return false;
            }
            editorComp.setBounds(getCellBounds(index, index));
            add(editorComp);
            editorComp.validate();

            editingIndex = index;
            editor.addCellEditorListener(this);

            return true;
        }
        return false;
    }

    @Override
    public void removeNotify() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                removePropertyChangeListener("permanentFocusOwner", editorRemover);
        super.removeNotify();
    }

    class CellEditorRemover implements PropertyChangeListener {

        KeyboardFocusManager focusManager;

        public CellEditorRemover(KeyboardFocusManager fm) {
            this.focusManager = fm;
        }

        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            if (!isEditing() || getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE) {
                return;
            }

            Component c = focusManager.getPermanentFocusOwner();
            while (c != null) {
                if (c == EditableJlist.this) {
                    // focus remains inside the table 
                    return;
                } else if ((c instanceof Window) ||
                        (c instanceof Applet && c.getParent() == null)) {
                    if (c == SwingUtilities.getRoot(EditableJlist.this)) {
                        if (!getListCellEditor().stopCellEditing()) {
                            getListCellEditor().cancelCellEditing();
                        }
                    }
                    break;
                }
                c = c.getParent();
            }
        }
    }

    public boolean isCellEditable(int index) {
        if (getModel() instanceof EditableListModel) {
            return ((EditableListModel) getModel()).isCellEditable(index);
        }
        return false;
    }

    public void setValueAt(Object value, int index) {
        ((EditableListModel) getModel()).setValueAt(value, index);
    }

  
    @Override
    public void editingStopped(ChangeEvent e) {
        if (editor != null) {
            Object value = editor.getCellEditorValue();
            setValueAt(value, editingIndex);
            removeEditor();
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        removeEditor();
    }
}