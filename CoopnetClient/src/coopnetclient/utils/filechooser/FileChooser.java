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

package coopnetclient.utils.filechooser;

import coopnetclient.Globals;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.utils.RegistryReader;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.TreeSet;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class FileChooser extends javax.swing.JFrame {

    public final static int SELECT_ACTION = 1;
    public final static int CANCEL_ACTION = 0;
    public final static int ANY_MODE = 2;
    public final static int FILES_ONLY_MODE = 3;
    public final static int DIRECTORIES_ONLY_MODE = 4;

    /** Creates new form MyFileChooser */
    public FileChooser(int mode) {
        initComponents();
        
        if(mode == DIRECTORIES_ONLY_MODE){
            setTitle("Choose directory");
        }
        
        choosemode = mode;
        displaymodel = new FileChooserTableModel(tbl_display);
        tbl_display.setModel(displaymodel);
        tbl_display.setDefaultRenderer(String.class, new FileChooserTableCellRenderer());
        tbl_display.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        setLocationRelativeTo(null);
    }

    /**
     * Shows a new chooser-window
     * and returns what action the user made:
     * select or cancel
     */
    public int choose(String startDir) {
        try {
            if (startDir != null) {
                currentdir = new File(startDir);
            } else {
                if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
                    currentdir = new File(System.getenv("HOME"));
                }else{
                    currentdir = new File(".").getCanonicalFile();
                }
            }
        } catch (Exception e) {
            return CANCEL_ACTION;
        }
        
        fillPlacesComboBox();
        openDirectory(currentdir);
        
        this.setVisible(true);
        tbl_display.requestFocusInWindow();
        while (ischoosing) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        dispose();
        return chooseresult;
    }

    public File getSelectedFile() {
        return selectedfile;
    }

    private void fillPlacesComboBox() {
        cmb_places.addItem("Jump to ...");
        cmb_places.addItem("<html><b>- Places -");
        
        if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
            if(currentdir != null && !currentdir.getAbsolutePath().equals(System.getenv("HOME"))){
                cmb_places.addItem(currentdir);
            }
            cmb_places.addItem(System.getenv("HOME"));
        }else{
            if(currentdir != null && !currentdir.getAbsolutePath().equals(System.getenv("USERPROFILE"))){
                cmb_places.addItem(currentdir);
            }
            cmb_places.addItem(System.getenv("USERPROFILE"));
            //add desktop and my documents
            cmb_places.addItem("Desktop");
            cmb_places.addItem("Documents");
        }
        
        cmb_places.addItem("<html><b>- Roots -");
        
        for (File root : File.listRoots()) {
                try {
                    String rootname = root.getCanonicalPath();
                    cmb_places.addItem(rootname);
                } catch (Exception e) {
                }
        }
        
        cmb_places.setSelectedIndex(0);
    }

    private String getFileType(File file) {
        String filename = file.getName();
        String[] tmp = filename.split("\\.");
        
        if (tmp.length < 2) {
            return "";
        }
        
        return tmp[tmp.length - 1].toLowerCase();
    }

    private void openDirectory(File directory) {
        String prevdir = "";
        TreeSet<File> directories = new TreeSet<File>();
        TreeSet<File> files = new TreeSet<File>();
        //stores where we were when going up
        if( directory.isFile()){
            directory = directory.getParentFile();
        }
        if (directory.getName().equals("..") ) {
            prevdir = directory.getParentFile().getName();
        }
        try {
            //this moves back a level
            directory = directory.getCanonicalFile();
            tf_currentDir.setText(directory.toString());
            tf_currentDir.setCaretPosition(tf_currentDir.getText().length()-1);
            
            //check all files in dir
            for (File file : directory.listFiles()) {
                if(cb_showHidden.isSelected() && file.isHidden() || !file.isHidden()){
                    if (file.isDirectory()) {
                        directories.add(file);
                    } else {
                        files.add(file);
                    }
                }
            }
            currentdir = directory;
            displaymodel.clear();
            
            //Add current directory
            displaymodel.addNewFile(".", "", 0, "", false);
            
            if (directory.getParent() != null) {
                btn_up.setEnabled(true);
                displaymodel.addNewFile("..", "", 0, "", false);
            } else {
                btn_up.setEnabled(false);
            }
            for (File file : directories) {
                displaymodel.addNewFile(file.getName(), "dir",
                        file.length(),
                        DateFormat.getDateInstance(DateFormat.LONG).format(new Date(file.lastModified())),
                        file.isHidden());
            }
            if (choosemode != DIRECTORIES_ONLY_MODE) {
                for (File file : files) {
                    displaymodel.addNewFile(file.getName(), getFileType(file),
                            file.length(),
                            DateFormat.getDateInstance(DateFormat.LONG).format(new Date(file.lastModified())),
                            file.isHidden());
                }
            }
            //set selection to the dir we left
            int i = displaymodel.indexOf(prevdir);
            if (i > -1) {
                tbl_display.getSelectionModel().setSelectionInterval(0,tbl_display.convertRowIndexToView(i));
                Rectangle rect = tbl_display.getCellRect(tbl_display.convertRowIndexToView(i), 0, true);
                tbl_display.scrollRectToVisible(rect);
            } else {//set slection to top
                tbl_display.getSelectionModel().setSelectionInterval(1, 1);
                Rectangle rect = tbl_display.getCellRect(1, 1, true);
                tbl_display.scrollRectToVisible(rect);
            }
        } catch(NullPointerException e){
            //Invalid dir
            displaymodel.clear();
        } catch (Exception e) {//ignore errors
            e.printStackTrace();
            return;
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

        btn_up = new javax.swing.JButton();
        scrl_display = new javax.swing.JScrollPane();
        tbl_display = new javax.swing.JTable();
        btn_select = new javax.swing.JButton();
        btn_cancel = new javax.swing.JButton();
        cmb_places = new javax.swing.JComboBox();
        tf_currentDir = new javax.swing.JTextField();
        cb_showHidden = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose file");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        btn_up.setText("up");
        btn_up.setFocusable(false);
        btn_up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_upActionPerformed(evt);
            }
        });

        scrl_display.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrl_display.setFocusable(false);

        tbl_display.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"[DIR1]", "Directory", "", "2008.01.01"},
                {"[DIR2]", "Directory", "", "2007.03.11"},
                {"wtf.exe", "application", "10K", "2001.00.00"},
                {null, null, null, null}
            },
            new String [] {
                "Filename", "Type", "Size", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_display.setFillsViewportHeight(true);
        tbl_display.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_display.setShowHorizontalLines(false);
        tbl_display.setShowVerticalLines(false);
        tbl_display.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_displayMouseClicked(evt);
            }
        });
        tbl_display.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_displayKeyPressed(evt);
            }
        });
        scrl_display.setViewportView(tbl_display);

        btn_select.setText("Select");
        btn_select.setFocusable(false);
        btn_select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_selectActionPerformed(evt);
            }
        });

        btn_cancel.setText("Cancel");
        btn_cancel.setFocusable(false);
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        cmb_places.setFocusable(false);
        cmb_places.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cmb_placesPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                cmb_placesPopupMenuWillBecomeVisible(evt);
            }
        });

        tf_currentDir.setText("C:\\someplace");
        tf_currentDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_currentDirActionPerformed(evt);
            }
        });

        cb_showHidden.setText("show hidden");
        cb_showHidden.setFocusable(false);
        cb_showHidden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_showHiddenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmb_places, 0, 339, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_up))
                    .addComponent(scrl_display, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btn_select)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
                        .addComponent(cb_showHidden))
                    .addComponent(tf_currentDir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_up)
                    .addComponent(cmb_places, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_currentDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrl_display, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_select)
                    .addComponent(btn_cancel)
                    .addComponent(cb_showHidden))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btn_upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_upActionPerformed
    openDirectory(new File(currentdir.getPath() + (File.separatorChar) + ".."));
}//GEN-LAST:event_btn_upActionPerformed

private void btn_selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_selectActionPerformed
    try {
        //remove [ ] around directory
        String filename = displaymodel.getSelectedFile();
        
        //dont select if its the . or .. that was selected
        if(filename.equals("..")){
            return;
        }
        
        //create selected file
        if(filename.equals(".")){
            selectedfile = currentdir;
        }else{
            selectedfile = new File(currentdir.getCanonicalPath() + File.separatorChar + filename);
        }
        
        //chose action
        switch (choosemode) {
            case DIRECTORIES_ONLY_MODE: {
                if (!selectedfile.isDirectory()) {
                    selectedfile = null;
                } else {
                    chooseresult = SELECT_ACTION;
                    ischoosing = false;
                    this.setVisible(false);
                }
                break;
            }
            case FILES_ONLY_MODE: {
                if (selectedfile.isDirectory()) {
                    selectedfile = null;
                } else {
                    chooseresult = SELECT_ACTION;
                    ischoosing = false;
                    this.setVisible(false);
                }
                break;
            }
            case ANY_MODE: {
                chooseresult = SELECT_ACTION;
                ischoosing = false;
                this.setVisible(false);
            }
        }
    } catch (Exception e) {//ignore errors
    }
}//GEN-LAST:event_btn_selectActionPerformed

private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
    chooseresult = CANCEL_ACTION;
    ischoosing = false;
    this.setVisible(false);
}//GEN-LAST:event_btn_cancelActionPerformed

private void tbl_displayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_displayKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        String filename = displaymodel.getSelectedFile();
        
        File file = new File(currentdir.getPath() + File.separatorChar + filename);
        if (file.isDirectory()) {//open directory
            openDirectory(file);
        } else {//file was selected            
            selectedfile = file;
            chooseresult = SELECT_ACTION;
            ischoosing = false;
            this.setVisible(false);
        }
    }else
    if (evt.getKeyCode() == KeyEvent.VK_HOME) {
        tbl_display.getSelectionModel().setSelectionInterval(1, 1);
        Rectangle rect = tbl_display.getCellRect(1, 1, true);
        tbl_display.scrollRectToVisible(rect);
    }else
    if (evt.getKeyCode() == KeyEvent.VK_END) {
        tbl_display.getSelectionModel().setSelectionInterval(displaymodel.getRowCount() - 2, displaymodel.getRowCount() - 1);
        Rectangle rect = tbl_display.getCellRect(displaymodel.getRowCount() - 1, 0, true);
        tbl_display.scrollRectToVisible(rect);
    }else
    if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){
        btn_up.doClick();
    } else {//jump to file with same startletter
        if (!(displaymodel.getValueAt(tbl_display.getSelectedRow(), 0).toString().charAt(0)+"").equalsIgnoreCase(evt.getKeyChar() + "")) {
            int idx = displaymodel.indexOfStartLetter(evt.getKeyChar(),0);
            if (idx > -1) {
                tbl_display.getSelectionModel().setSelectionInterval(0, idx);
                Rectangle rect = tbl_display.getCellRect(idx, 0, true);
                tbl_display.scrollRectToVisible(rect);
            }
        }else{
            int idx = displaymodel.indexOfStartLetter(evt.getKeyChar(),tbl_display.getSelectedRow()+1);
            if (idx > -1) {
                tbl_display.getSelectionModel().setSelectionInterval(0, idx);
                Rectangle rect = tbl_display.getCellRect(idx, 0, true);
                tbl_display.scrollRectToVisible(rect);
            }
        }
    }
}//GEN-LAST:event_tbl_displayKeyPressed

private void tbl_displayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_displayMouseClicked
    if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
        String filename = displaymodel.getSelectedFile();
        
        if(filename != null){
            File file = new File(currentdir.getPath() + File.separatorChar + filename);
            if (file.isDirectory()) {//open directory
                openDirectory(file);
            } else {//file was selected   
                if(!(filename.equals(".") || filename.equals(".."))){
                    selectedfile = file;
                    chooseresult = SELECT_ACTION;
                    ischoosing = false;
                    this.setVisible(false);
                }
            }
        }
    }
}//GEN-LAST:event_tbl_displayMouseClicked

private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    //set cancel when x was clicked
    if (ischoosing) {
        chooseresult = CANCEL_ACTION;
        ischoosing = false;
    }
}//GEN-LAST:event_formWindowClosed

private void cb_showHiddenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_showHiddenActionPerformed
    openDirectory(currentdir);
}//GEN-LAST:event_cb_showHiddenActionPerformed

private void cmb_placesPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cmb_placesPopupMenuWillBecomeInvisible
    if(cmb_places.getSelectedItem().toString().equals("Desktop")){
        openDirectory(new File(RegistryReader.read("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\\Desktop")));
    }else
        if(cmb_places.getSelectedItem().toString().equals("Documents")){
        openDirectory(new File(RegistryReader.read("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\\Personal")));
    }else
    if(cmb_places.getSelectedItem().toString().contains("/") || cmb_places.getSelectedItem().toString().contains("\\")){
        openDirectory(new File(cmb_places.getSelectedItem().toString()));
    }
    cmb_places.removeItemAt(0);
    cmb_places.insertItemAt("Jump to ...", 0);
    cmb_places.setSelectedIndex(0);
}//GEN-LAST:event_cmb_placesPopupMenuWillBecomeInvisible

private void cmb_placesPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cmb_placesPopupMenuWillBecomeVisible
    cmb_places.removeItemAt(0);
    cmb_places.insertItemAt(tf_currentDir.getText(), 0);
    cmb_places.setSelectedIndex(0);
}//GEN-LAST:event_cmb_placesPopupMenuWillBecomeVisible

private void tf_currentDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_currentDirActionPerformed
    openDirectory(new File(tf_currentDir.getText()));
}//GEN-LAST:event_tf_currentDirActionPerformed
    /*
    public static void main(String args[]) {
    MyFileChooser mfc =new MyFileChooser(MyFileChooser.ANY_MODE);
    int ret = mfc.choose(null);
    File selected = mfc.getSelectedFile();
    System.out.println("Selected: "+ selected);
    System.out.println("Action: "+ ret);
    }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_select;
    private javax.swing.JButton btn_up;
    private javax.swing.JCheckBox cb_showHidden;
    private javax.swing.JComboBox cmb_places;
    private javax.swing.JScrollPane scrl_display;
    private javax.swing.JTable tbl_display;
    private javax.swing.JTextField tf_currentDir;
    // End of variables declaration//GEN-END:variables
    private File currentdir = new File(".");
    private FileChooserTableModel displaymodel;
    private File selectedfile = null;
    private boolean ischoosing = true;
    private int choosemode = -1;
    private int chooseresult = -1;
}
