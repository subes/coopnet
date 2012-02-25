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
package coopnetclient.frames.popupmenus;

import coopnetclient.Globals;
import coopnetclient.enums.TransferStatuses;
import coopnetclient.frames.models.TransferTableModel;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;

public class TransferPopupMenu extends JPopupMenu implements ActionListener {

    private JMenuItem openFile;
    private JMenuItem openDir;
    private JMenuItem remove;
    private JMenuItem clear;
    private JTable source;
    private int index = -1;

    public TransferPopupMenu(JTable source) {
        super();
        this.source = source;

        openFile = makeMenuItem("Open File");
        this.add(openFile);
        openDir = makeMenuItem("Open Directory");
        this.add(openDir);
        this.add(new JSeparator());
        remove = makeMenuItem("Remove transfer");
        this.add(remove);
        clear = makeMenuItem("Remove all inactive transfers");
        this.add(clear);
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if (command.equals("Open File")) {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                try {
                    File destfile = null;
                    destfile = Globals.getTransferModel().getDestFile(index);
                    desktop.open(destfile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (command.equals("Open Directory")) {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                try {
                    File destfile = null;
                    destfile = Globals.getTransferModel().getDestFile(index).getParentFile();
                    desktop.open(destfile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (command.equals("Remove transfer")) {
            Globals.getTransferModel().removeTransfer(index);
        } else if (command.equals("Remove all inactive transfers")) {
            Globals.getTransferModel().removeAllEndedTransfers();
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        index = source.getSelectedRow();
        if (index >= 0) {
            TransferStatuses status = Globals.getTransferModel().getTransferStatus(index);
            int type = Globals.getTransferModel().getTransferType(index);
            if (type == TransferTableModel.RECIEVE_TYPE) {
                switch (status) {
                    case Waiting:
                    case Transferring:
                    case Retrying:
                    case Starting:
                        remove.setEnabled(false);
                        clear.setEnabled(true);
                        openDir.setEnabled(false);
                        openFile.setEnabled(false);
                        break;
                    case Finished:
                        remove.setEnabled(true);
                        clear.setEnabled(true);
                        openDir.setEnabled(true);
                        openFile.setEnabled(true);
                        break;
                    default:
                        remove.setEnabled(true);
                        clear.setEnabled(true);
                        openDir.setEnabled(false);
                        openFile.setEnabled(false);
                        break;
                }
            } else {
                openDir.setEnabled(true);
                openFile.setEnabled(true);
                clear.setEnabled(true);
                switch (status) {
                    case Waiting:
                    case Transferring:
                    case Retrying:
                    case Starting:
                        remove.setEnabled(false);
                        break;
                    case Finished:
                        remove.setEnabled(true);
                        break;
                    default:
                        remove.setEnabled(true);
                        break;
                }
            }
            super.show(invoker, x, y);
        } else {
            remove.setEnabled(false);
            clear.setEnabled(true);
            openDir.setEnabled(false);
            openFile.setEnabled(false);
            super.show(invoker, x, y);
        }
    }
}
