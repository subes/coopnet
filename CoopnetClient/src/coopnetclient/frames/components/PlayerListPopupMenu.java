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
package coopnetclient.frames.components;

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.MuteBanList;
import coopnetclient.frames.models.ContactListModel;
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
    private JMenuItem playerName;
    private JMenuItem invite;
    private JMenuItem mute_UnMute;
    private JMenuItem ban_UnBan;
    private JMenuItem addContact;

    /**
    if mode is "host" u get kick
     * any other mode will result in general popupmenu
     */
    public PlayerListPopupMenu(int mode, JList source) {
        super();
        this.source = source;

        playerName = new JMenuItem();
        playerName.setEnabled(false);
        this.add(playerName);

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
        mute_UnMute = makeMenuItem("Mute");
        ban_UnBan = makeMenuItem("Ban");
        this.add(mute_UnMute);
        this.add(ban_UnBan);
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
        final String subject = playerName.getText();
        if (subject == null) {
            return;
        }
        if (command.equals("Kick")) {
            Protocol.kick(subject);
        } else if (command.equals("Ban")) {
            Protocol.kick(subject);
            Protocol.ban(subject);
        } else if (command.equals("UnBan")) {
            Protocol.unBan(subject);
        } else if (command.equals("Mute")) {
            Protocol.mute(subject);
        } else if (command.equals("Add to Contacts")) {
            Globals.getContactList().addContact(subject, ContactListModel.DEFAULT_GROUP, ContactListElementTypes.PENDING_CONTACT);
            Protocol.RequestContact(subject);
        } else if (command.equals("UnMute")) {
            Protocol.unMute(subject);
        } else if (command.equals("Whisper...")) {
            TabOrganizer.openPrivateChatPanel(subject, true);
        } else if (command.equals("Show profile...")) {
            Protocol.requestProfile(subject);
        } else if (command.equals("Invite to room")) {
            Protocol.sendInvite(subject);
        } else if (command.equals("Nudge")) {
            Protocol.nudge(subject);
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
                                Protocol.sendFile(subject, inputfile.getName(), inputfile.length() + "", coopnetclient.utils.Settings.getFiletTansferPort() + "");
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
            playerName.setText((String) source.getSelectedValue());
        }
        
        if(Globals.getContactList().contains( playerName.getText() )){
            addContact.setVisible(false);
        }else{
            addContact.setVisible(true);
        }
        if (TabOrganizer.getRoomPanel() == null) {
            invite.setVisible(false);
        } else {
            invite.setVisible(true);
        }        
        
        if (MuteBanList.getMuteBanStatus(playerName.getText()) == null) {
            mute_UnMute.setText("Mute");
            ban_UnBan.setText("Ban");
        } else {
            switch (MuteBanList.getMuteBanStatus(playerName.getText())) {
                case BANNED:
                    mute_UnMute.setText("Mute");
                    ban_UnBan.setText("UnBan");
                    break;
                case MUTED:
                    mute_UnMute.setText("UnMute");
                    ban_UnBan.setText("Ban");
                    break;
                case BOTH:
                    mute_UnMute.setText("UnMute");
                    ban_UnBan.setText("UnBan");
                    break;
            }
        }
        super.show(invoker, x, y);
    }
}
