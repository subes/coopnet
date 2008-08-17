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

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JList;
import javax.swing.JTextField;

public class DefaultListCellEditor extends DefaultCellEditor implements ListCellEditor{ 
    
    public DefaultListCellEditor(final JTextField textField){ 
        super(textField); 
    } 
 
    @Override
    public Component getListCellEditorComponent(JList list, Object value, boolean isSelected, int index){ 
        delegate.setValue(value); 
        return editorComponent; 
    } 
}