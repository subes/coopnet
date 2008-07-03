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

package coopnetclient.modules.components;

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.Protocol;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PlayerListPopupMenu extends JPopupMenu implements ActionListener {

    public static final int HOST_MODE = 0;
    public static final int GENERAL_MODE = 1;
    
    private JList source;
    private JMenuItem playername;

    /**
    if mode is "host" u get kick
     * any other mode will result in general popupmenu
     */
    public PlayerListPopupMenu(int mode, JList source) {
        super();
        this.source = source;

        playername = new JMenuItem();
        playername.setEnabled(false);
        this.add(playername);
        
        this.add(new JSeparator());
        
        this.add(makeMenuItem("Send nudge"));
        this.add(makeMenuItem("Send file..."));
        this.add(new JSeparator());
        if (mode == HOST_MODE) {
            this.add(makeMenuItem("Kick"));
        }
        this.add(makeMenuItem("Ban"));
        this.add(makeMenuItem("UnBan"));
        this.add(makeMenuItem("Mute"));
        this.add(makeMenuItem("UnMute"));
        this.add(makeMenuItem("Whisper"));
        
        this.add(new JSeparator());
        
        this.add(makeMenuItem("Show players profile..."));
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        //JList source = null;
        //source =(JList) e.getSource();

        if (source == null) {
            return;
        }
        final String subject = (String) source.getSelectedValue();
        if (subject == null) {
            return;
        }
        if (command.equals("Kick")) {
            Client.send(Protocol.kick(subject), null);
        } else if (command.equals("Ban")) {
            Client.send(Protocol.kick(subject), null);
            Client.send(Protocol.ban(subject), null);
        } else if (command.equals("UnBan")) {
            Client.send(Protocol.unban(subject), null);
        } else if (command.equals("Mute")) {
            Client.send(Protocol.mute(subject), null);
        } else if (command.equals("UnMute")) {
            Client.send(Protocol.unmute(subject), null);
        } else if (command.equals("Whisper")) {
            Globals.clientFrame.newPrivateChat(subject);
            Globals.clientFrame.showPMTab(subject);
        } else if (command.equals("Show players profile...")) {
            Client.send(Protocol.requestProfile(subject), null);
        } else if (command.equals("Send nudge")) {
            Client.send(Protocol.nudge(subject), null);
        } else if (command.equals("Send File...")) {

            new Thread() {

                @Override
                public void run() {
                    File inputfile = null;

                    FileChooser mfc = new FileChooser(FileChooser.FILES_ONLY_MODE);
                    int returnVal = mfc.choose(Globals.lastOpenedDir);

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        inputfile = mfc.getSelectedFile();
                        if (inputfile != null) {
                            Client.send(Protocol.Sendfile(subject, inputfile.getName(), inputfile.length() + "" , coopnetclient.modules.Settings.getFiletTansferPort()+""), null);
                            Globals.clientFrame.addTransferTab_Send(subject, inputfile);
                            Globals.lastOpenedDir = inputfile.getParent();
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        if (source == null || source.getSelectedValue() == null) {
            setVisible(false);
        } else {
            playername.setText((String) source.getSelectedValue());
        }
    }
}