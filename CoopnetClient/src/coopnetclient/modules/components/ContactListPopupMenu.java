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
import coopnetclient.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.modules.components.mutablelist.EditableJlist;
import coopnetclient.modules.models.ContactListModel;
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

        this.add(playername);
        this.add(new JSeparator());
        this.add(accept);
        this.add(refuse);
        this.add(deleteContact);
        this.add(new JSeparator());
        this.add(nudge);
        this.add(showProfile);
        this.add(whisper);
        this.add(sendfile);
        this.add(invite);
        this.add(refresh);
        this.add(moveto);
        this.add(new JSeparator());
        this.add(hideOffline);
        this.add(create);
        this.add(deleteGroup);
        this.add(rename);
        this.add(toggle);
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
        if(TabOrganizer.getRoomPanel() == null){
            invite.setVisible(false);
        }else{
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
                Client.send(Protocol.refreshContacts(model.isOfflineShown()), null);
            } else {
                model.toggleShowOfflineStatus();
            }
        }

        final String subject = playername.getText();//(String) source.getSelectedValue();
//Remove Contact
        if (command.equals("Accept")) {
            Client.send(Protocol.acceptRequest(subject), null);
            model.removePending(subject);
        } else if (command.equals("Refuse")) {
            model.removePending(subject);
            Client.send(Protocol.refuseRequest(subject), null);
        } else if (command.equals("Remove Contact")) {
            model.removecontact(subject);
            Client.send(Protocol.removeContact(subject), null);
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
            Client.send(Protocol.deleteGroup(subject), null);
        } else if (command.equals("Refresh list")) {
            Client.send(Protocol.refreshContacts(model.isOfflineShown()), null);
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
            Client.send(Protocol.nudge(subject), null);
        } else if (command.equals("Invite to room")) {
            Client.send(Protocol.sendInvite(subject), null);
        } else if (command.equals("Show profile...")) {
            Client.send(Protocol.requestProfile(subject), null);
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
                //HIDE UNNECESSARY ITEMS
                ContactListModel model = (ContactListModel) source.getModel();
                switch (model.getStatus(source.getSelectedValue().toString())) {
                    case GROUPNAME_CLOSED:
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
                playername.setText((String) source.getSelectedValue());
                super.show(invoker, x, y);
            }
        }
    }
}
