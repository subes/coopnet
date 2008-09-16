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

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.components.mutablelist.EditableJlist;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class ContactListPopupMenu extends JPopupMenu implements ActionListener {

    private EditableJlist source;
    private JMenuItem playername;
    private JMenuItem accept;
    private JMenuItem refuse;
    private JMenuItem deleteContact;
    private JMenuItem nudge;
    private JMenuItem showProfile;
    private JMenuItem whisper;
    private JMenuItem sendfile;
    private JMenuItem invite;
    private JMenuItem refresh;
    private JMenu moveto;
    private JCheckBoxMenuItem hideOffline;
    private JMenuItem create;
    private JMenuItem deleteGroup;
    private JMenuItem rename;
    private JMenuItem toggle;
    private JMenuItem mute_unmute;
    private JMenuItem ban_unban;

    public ContactListPopupMenu(EditableJlist source) {
        super();
        this.source = source;

        playername = new JMenuItem();
        playername.setEnabled(false);
        accept = makeMenuItem("Accept");
        refuse = makeMenuItem("Refuse");
        nudge = makeMenuItem("Nudge");
        showProfile = makeMenuItem("Show profile...");
        whisper = makeMenuItem("Whisper...");
        sendfile = makeMenuItem("Send file...");
        refresh = makeMenuItem("Refresh list");
        moveto = new JMenu("Move to Group:");
        create = makeMenuItem("Create new Group...");
        deleteGroup = makeMenuItem("Delete Group");
        deleteContact = makeMenuItem("Remove Contact");
        rename = makeMenuItem("Rename Group");
        hideOffline = new JCheckBoxMenuItem("Hide offline contacts", true);
        hideOffline.addActionListener(this);
        toggle = makeMenuItem("Open/Collapse");
        invite = makeMenuItem("Invite to room");
        mute_unmute = makeMenuItem("Mute");
        ban_unban = makeMenuItem("Ban");

        this.add(playername);
        this.add(new JSeparator());
        this.add(accept);
        this.add(refuse);
        this.add(deleteContact);
        this.add(moveto);
        this.add(new JSeparator());
        this.add(nudge);
        this.add(showProfile);
        this.add(whisper);
        this.add(sendfile);
        this.add(invite);
        this.add(mute_unmute);
        this.add(ban_unban);
        this.add(new JSeparator());
        this.add(hideOffline);
        this.add(create);
        this.add(deleteGroup);
        this.add(rename);
        this.add(toggle);
        this.add(refresh);
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    public void refreshMoveToMenu() {
        moveto.removeAll();
        for (Object group : ((ContactListModel) source.getModel()).getGroupNames()) {
            moveto.add(makeMenuItem(group.toString()));
        }
    }

    public void setContactActionVisibility(boolean isVisible) {
        moveto.setVisible(isVisible);
        deleteContact.setVisible(isVisible);
        deleteContact.setVisible(isVisible);
        nudge.setVisible(isVisible);
        showProfile.setVisible(isVisible);
        whisper.setVisible(isVisible);
        sendfile.setVisible(isVisible);
        if (TabOrganizer.getRoomPanel() == null) {
            invite.setVisible(false);
        } else {
            invite.setVisible(isVisible);
        }
    }

    public void setPendingActionVisibility(boolean isVisible) {
        accept.setVisible(isVisible);
        refuse.setVisible(isVisible);
    }

    public void setGroupActionVisibility(boolean isVisible) {
        if (playername.getText().equals(ContactListModel.DEFAULT_GROUP)) {
            deleteGroup.setVisible(false);
            rename.setVisible(false);
        } else {
            deleteGroup.setVisible(isVisible);
            rename.setVisible(isVisible);
        }
        toggle.setVisible(isVisible);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        Globals.setContactListPopupIsUp(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        ContactListModel model = ((ContactListModel) source.getModel());

        if (command.equals("Hide offline contacts")) {
            if (!model.isOfflineShown()) {
                model.toggleShowOfflineStatus();
                Protocol.refreshContacts(model.isOfflineShown());
            } else {
                model.toggleShowOfflineStatus();
            }
        }

        final String subject = playername.getText();//(String) source.getSelectedValue();
//Remove Contact
        if (command.equals("Accept")) {
            Protocol.acceptRequest(subject);
            model.removePending(subject);
        } else if (command.equals("Refuse")) {
            model.removePending(subject);
            Protocol.refuseRequest(subject);
        } else if (command.equals("Remove Contact")) {
            model.removecontact(subject);
            Protocol.removeContact(subject);
        } else if (command.equals("Create new Group...")) {
            Collection groups = model.getGroupNames();
            if (groups.contains("New Group")) {
                int i = 1;
                while (groups.contains("New Group" + i)) {
                    i++;
                }
                //create newgroup i
                model.createNewGroup("New Group" + i);
            } else {
                model.createNewGroup("New Group");
            }
            source.editCellAt(model.getSize() - 1, e);
            Component editorComp = source.getEditorComponent();
            if (editorComp != null) {
                editorComp.requestFocus();
            }
        } else if (command.equals("Delete Group")) {
            model.removeGroup(subject);
            Protocol.deleteGroup(subject);
        } else if (command.equals("Refresh list")) {
            Protocol.refreshContacts(model.isOfflineShown());
        } else if (command.equals("Rename Group")) {
            System.out.println("rename:" + source.getSelectedIndex());
            source.editCellAt(source.getSelectedIndex(), e);
            Component editorComp = source.getEditorComponent();
            if (editorComp != null) {
                editorComp.requestFocus();
            }
        } else if (command.equals("Open/Collapse")) {
            model.toggleGroupClosedStatus(subject);
        } else if (command.equals("Whisper...")) {
            if (model.getStatus(subject) != ContactListElementTypes.OFFLINE) {
                TabOrganizer.openPrivateChatPanel(subject, true);
            }
        } else if (command.equals("Nudge")) {
            Protocol.nudge(subject);
        } else if (command.equals("Invite to room")) {
            Protocol.sendInvite(subject);
        } else if (command.equals("Show profile...")) {
            Protocol.requestProfile(subject);
        } else if (command.equals("Ban")) {
            MuteBanList.ban(subject);
            Protocol.kick(subject);
            Protocol.ban(subject);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("UnBan")) {
            MuteBanList.unBan(subject);
            Protocol.unBan(subject);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("Mute")) {
            MuteBanList.mute(subject);
            Protocol.mute(subject);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("UnMute")) {
            MuteBanList.unMute(subject);
            Protocol.unMute(subject);
            Globals.updateMuteBanTableFrame();
        } else if (command.equals("Send file...")) {
            if (model.getStatus(subject) != ContactListElementTypes.OFFLINE) {

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
                                    Protocol.sendfile(subject, inputfile.getName(), inputfile.length() + "", coopnetclient.utils.Settings.getFiletTansferPort() + "");
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
        } else if (model.getGroupNames().contains(command)) {
            model.moveContact(subject, command);
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        if (source == null) {
            return;
        } else {
            if (source.getSelectedValue() == null) {
                setPendingActionVisibility(false);
                setGroupActionVisibility(false);
                setContactActionVisibility(false);
                refreshMoveToMenu();
                playername.setText("No selection");
                super.show(invoker, x, y);
            } else {
                refreshMoveToMenu();
                playername.setText((String) source.getSelectedValue());
                //HIDE UNNECESSARY ITEMS
                ContactListModel model = (ContactListModel) source.getModel();
                switch (model.getStatus(source.getSelectedValue().toString())) {
                    case GROUPNAME_CLOSED:
                        setPendingActionVisibility(false);
                        setGroupActionVisibility(true);
                        setContactActionVisibility(false);
                        break;
                    case GROUPNAME_OPEN:
                        setPendingActionVisibility(false);
                        setGroupActionVisibility(true);
                        setContactActionVisibility(false);
                        break;
                    case PENDING_CONTACT:
                        setPendingActionVisibility(false);
                        setGroupActionVisibility(false);
                        setContactActionVisibility(false);
                        break;
                    case PENDING_REQUEST:
                        setPendingActionVisibility(true);
                        setGroupActionVisibility(false);
                        setContactActionVisibility(false);
                        break;
                    default:
                        setPendingActionVisibility(false);
                        setGroupActionVisibility(false);
                        setContactActionVisibility(true);
                        break;
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
    }
}