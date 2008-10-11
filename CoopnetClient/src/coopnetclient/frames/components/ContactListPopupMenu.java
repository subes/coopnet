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
import coopnetclient.frames.components.mutablelist.EditableJlist;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.Settings;
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
    private JMenuItem playerName;
    private JMenuItem accept;
    private JMenuItem refuse;
    private JMenuItem deleteContact;
    private JSeparator sep_contact;
    private JMenuItem nudge;
    private JMenuItem showProfile;
    private JMenuItem whisper;
    private JMenuItem sendFile;
    private JMenuItem invite;
    private JMenuItem refresh;
    private JMenu moveto;
    private JSeparator sep_group;
    private JCheckBoxMenuItem showOffline;
    private JMenuItem create;
    private JMenuItem deleteGroup;
    private JMenuItem rename;
    private JMenuItem toggle;
    private JMenuItem mute_UnMute;    
    private JMenuItem ban_UnBan;

    public ContactListPopupMenu(EditableJlist source) {
        super();
        this.source = source;

        playerName = new JMenuItem();
        playerName.setEnabled(false);
        accept = makeMenuItem("Accept");
        refuse = makeMenuItem("Refuse");
        sep_contact = new JSeparator();
        nudge = makeMenuItem("Nudge");
        showProfile = makeMenuItem("Show profile");
        whisper = makeMenuItem("Whisper");
        sendFile = makeMenuItem("Send file");
        refresh = makeMenuItem("Refresh list");
        moveto = new JMenu("Move to group");
        create = makeMenuItem("Create new group");
        deleteGroup = makeMenuItem("Delete group");
        deleteContact = makeMenuItem("Remove contact");
        rename = makeMenuItem("Rename group");
        sep_group = new JSeparator();
        showOffline = new JCheckBoxMenuItem("Show offline contacts", Settings.getShowOfflineContacts());
        showOffline.addActionListener(this);
        toggle = makeMenuItem("Open/Collapse");
        invite = makeMenuItem("Invite to room");
        mute_UnMute = makeMenuItem("Mute");
        ban_UnBan = makeMenuItem("Ban");

        this.add(playerName);
        this.add(new JSeparator());
        this.add(accept);
        this.add(refuse);
        this.add(deleteContact);
        this.add(moveto);
        this.add(sep_contact);
        this.add(nudge);
        this.add(showProfile);
        this.add(whisper);
        this.add(sendFile);
        this.add(invite);
        this.add(mute_UnMute);
        this.add(ban_UnBan);
        this.add(sep_group);
        this.add(showOffline);
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
        sep_contact.setVisible(isVisible);
        moveto.setVisible(isVisible);
        deleteContact.setVisible(isVisible);
        deleteContact.setVisible(isVisible);
        mute_UnMute.setVisible(isVisible);
        ban_UnBan.setVisible(isVisible);
        nudge.setVisible(isVisible);
        showProfile.setVisible(isVisible);
        whisper.setVisible(isVisible);
        sendFile.setVisible(isVisible);
        if (TabOrganizer.getRoomPanel() == null) {
            invite.setVisible(false);
        } else {
            invite.setVisible(isVisible);
        }
        sep_group.setVisible(isVisible);
    }

    public void setPendingActionVisibility(boolean isVisible) {
        accept.setVisible(isVisible);
        refuse.setVisible(isVisible);
    }

    public void setGroupActionVisibility(boolean isVisible) {
        if (playerName.getText().equals(ContactListModel.DEFAULT_GROUP)) {
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

        if (command.equals("Show offline contacts")) {
            Settings.setShowOfflineContacts(showOffline.isSelected());
        }

        final String subject = playerName.getText();
        
        if (command.equals("Accept")) {
            Protocol.acceptRequest(subject);            
        } else if (command.equals("Refuse")) {
            Protocol.refuseRequest(subject);
        } else if (command.equals("Remove contact")) {
            Protocol.removeContact(subject);
        } else if (command.equals("Create new group")) {
            Collection groups = model.getGroupNames();
            if (groups.contains("New group")) {
                int i = 1;
                while (groups.contains("New group" + i)) {
                    i++;
                }
                //create newgroup i
                ContactListModel.isNewGroup = true;
                model.createNewGroup("New group" + i);
            } else { 
                ContactListModel.isNewGroup = true;
                model.createNewGroup("New group");
            }
            source.editCellAt(model.getSize() - 1, e);
            Component editorComp = source.getEditorComponent();
            if (editorComp != null) {
                editorComp.requestFocus();
            }
        } else if (command.equals("Delete group")) {
            Protocol.deleteGroup(subject);
        } else if (command.equals("Refresh list")) {
            Protocol.refreshContacts();
        } else if (command.equals("Rename group")) {
            source.editCellAt(source.getSelectedIndex(), e);
            Component editorComp = source.getEditorComponent();
            if (editorComp != null) {
                editorComp.requestFocus();
            }
        } else if (command.equals("Open/Collapse")) {
            model.toggleGroupClosedStatus(subject);
        } else if (command.equals("Whisper")) {
            if (model.getStatus(subject) != ContactListElementTypes.OFFLINE) {
                TabOrganizer.openPrivateChatPanel(subject, true);
            }
        } else if (command.equals("Nudge")) {
            Protocol.nudge(subject);
        } else if (command.equals("Invite to room")) {
            Protocol.sendInvite(subject);
        } else if (command.equals("Show profile")) {
            Protocol.requestProfile(subject);
        } else if (command.equals("Ban")) {
            Protocol.kick(subject);
            Protocol.ban(subject);
        } else if (command.equals("UnBan")) {
            Protocol.unBan(subject);
        } else if (command.equals("Mute")) {
            Protocol.mute(subject);
        } else if (command.equals("UnMute")) {
            Protocol.unMute(subject);
        } else if (command.equals("Send file")) {
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
        } else if (model.getGroupNames().contains(command)) {//otherwise the action name is a groupname and the selected user must be moved there
            Protocol.moveToGroup(subject, command);
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        if (source == null) {
            return;
        } else {
            showOffline.setSelected(Settings.getShowOfflineContacts());
            
            if (source.getSelectedValue() == null) {
                setPendingActionVisibility(false);
                setGroupActionVisibility(false);
                setContactActionVisibility(false);
                refreshMoveToMenu();
                playerName.setText("No selection");
                super.show(invoker, x, y);
            } else {
                refreshMoveToMenu();
                playerName.setText((String) source.getSelectedValue());
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
    }
}
