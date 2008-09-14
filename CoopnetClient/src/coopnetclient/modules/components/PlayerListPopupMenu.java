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
package coopnetclient.modules.components;

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.protocol.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.MuteBanList;
import coopnetclient.modules.models.ContactListModel;
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
    private JMenuItem invite;
    private JMenuItem mute_unmute;
    private JMenuItem ban_unban;
    private JMenuItem addContact;

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
        this.add(makeMenuItem("Nudge"));
        invite = makeMenuItem("Invite to room");
        this.add(invite);
        this.add(makeMenuItem("Whisper..."));
        this.add(makeMenuItem("Send file..."));
        addContact = makeMenuItem("Add to Contacts");
        this.add(addContact);

        this.add(new JSeparator());

        if (mode == HOST_MODE) {
            this.add(makeMenuItem("Kick"));
        }
        mute_unmute = makeMenuItem("Mute");
        ban_unban = makeMenuItem("Ban");
        this.add(mute_unmute);
        this.add(ban_unban);
        this.add(new JSeparator());

        this.add(makeMenuItem("Show profile..."));
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        Globals.setPlayerListPopupIsUp(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if (source == null) {
            return;
        }
        final String subject = playername.getText();
        if (subject == null) {
            return;
        }
        if (command.equals("Kick")) {
            Client.send(Protocol.kick(subject), null);
        } else if (command.equals("Ban")) {
            MuteBanList.ban(subject);
            Client.send(Protocol.kick(subject), null);
            Client.send(Protocol.ban(subject), null);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("UnBan")) {
            MuteBanList.unBan(subject);
            Client.send(Protocol.unban(subject), null);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("Mute")) {
            MuteBanList.mute(subject);
            Client.send(Protocol.mute(subject), null);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("Add to Contacts")) {
            Globals.getContactList().addContact(subject, ContactListModel.DEFAULT_GROUP, ContactListElementTypes.PENDING_CONTACT);
            Client.send(Protocol.RequestContact(subject), null);
        } else if (command.equals("UnMute")) {
            MuteBanList.unMute(subject);
            Client.send(Protocol.unmute(subject), null);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("Whisper...")) {
            TabOrganizer.openPrivateChatPanel(subject, true);
        } else if (command.equals("Show profile...")) {
            Client.send(Protocol.requestProfile(subject), null);
        } else if (command.equals("Invite to room")) {
            Client.send(Protocol.sendInvite(subject), null);
        } else if (command.equals("Nudge")) {
            Client.send(Protocol.nudge(subject), null);
        } else if (command.equals("Send file...")) {

            new Thread() {

                @Override
                public void run() {
                    try {
                        File inputfile = null;

                        FileChooser mfc = new FileChooser(FileChooser.FILES_ONLY_MODE);
                        int returnVal = mfc.choose(Globals.getLastOpenedDir());

                        if (returnVal == FileChooser.SELECT_ACTION) {
                            inputfile = mfc.getSelectedFile();
                            if (inputfile != null) {
                                Client.send(Protocol.Sendfile(subject, inputfile.getName(), inputfile.length() + "", coopnetclient.modules.Settings.getFiletTansferPort() + ""), null);
                                TabOrganizer.openFileTransferSendPanel(subject, inputfile);
                                Globals.setLastOpenedDir(inputfile.getParent());
                            }
                        }
                    } catch (Exception e) {
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        if (source == null || source.getSelectedValue() == null || source.getSelectedValue().equals(Globals.getThisPlayer_loginName())) {
            setVisible(false);
            return;
        } else {
            playername.setText((String) source.getSelectedValue());
        }
        
        if(Globals.getContactList().contains( playername.getText() )){
            addContact.setVisible(false);
        }else{
            addContact.setVisible(true);
        }
        if (TabOrganizer.getRoomPanel() == null) {
            invite.setVisible(false);
        } else {
            invite.setVisible(true);
        }        
        
        if (MuteBanList.getMuteBanStatus(playername.getText()) == null) {
            mute_unmute.setText("Mute");
            ban_unban.setText("Ban");
        } else {
            switch (MuteBanList.getMuteBanStatus(playername.getText())) {
                case BANNED:
                    mute_unmute.setText("Mute");
                    ban_unban.setText("UnBan");
                    break;
                case MUTED:
                    mute_unmute.setText("UnMute");
                    ban_unban.setText("Ban");
                    break;
                case BOTH:
                    mute_unmute.setText("UnMute");
                    ban_unban.setText("UnBan");
                    break;
            }
        }
        super.show(invoker, x, y);
    }
}
