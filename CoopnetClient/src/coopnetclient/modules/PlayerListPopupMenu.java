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

package coopnetclient.modules;

import coopnetclient.Client;
import coopnetclient.Protocol;
import filechooser.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PlayerListPopupMenu extends JPopupMenu implements ActionListener {

    public static final String HOST_MODE = "host";
    public static final String JOIN_MODE = "";
    String mode;
    JList source;
    JMenuItem playername;
    JMenuItem ban;
    JMenuItem unban;
    JMenuItem mute;
    JMenuItem unmute;
    JMenuItem whisper;
    JMenuItem kick;
    JMenuItem showprofile;
    JMenuItem nudge;
    JMenuItem sendfile;

    /**
    if mode is "host" u get kick
     * any other mode will result in general popupmenu
     */
    public PlayerListPopupMenu(String mode, JList source) {
        super();
        this.source = source;

        playername = new JMenuItem();
        playername.setEnabled(false);

        ban = makeMenuItem("Ban");
        unban = makeMenuItem("UnBan");
        mute = makeMenuItem("Mute");
        unmute = makeMenuItem("UnMute");
        whisper = makeMenuItem("Whisper");
        kick = makeMenuItem("Kick");
        showprofile = makeMenuItem("Show players profile...");
        nudge = makeMenuItem("Send nudge");
        sendfile = makeMenuItem("Send File...");

        this.mode = mode;
        this.add(playername);
        this.add(new JSeparator());
        this.add(nudge);
        this.add(sendfile);
        this.add(new JSeparator());
        if (mode.equals("host")) {
            this.add(kick);
        }
        this.add(ban);
        this.add(unban);
        this.add(mute);
        this.add(unmute);
        this.add(whisper);
        this.add(new JSeparator());
        this.add(showprofile);
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
            Client.mainFrame.newPrivateChat(subject);
            Client.mainFrame.showPMTab(subject);
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
                    int returnVal = mfc.choose(Client.lastOpenedDir);

                    if (returnVal == FileChooser.SELECT_ACTION) {
                        inputfile = mfc.getSelectedFile();
                        if (inputfile != null) {
                            Client.send(Protocol.Sendfile(subject, inputfile.getName(), inputfile.length() + ""), null);
                            Client.mainFrame.addTransferTab_Send(subject, inputfile);
                            Client.lastOpenedDir = new File(inputfile.getParent());
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
