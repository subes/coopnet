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
package coopnetclient.frames.components;

import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ContactListElementTypes;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.frames.components.mutablelist.EditableJlist;
import coopnetclient.frames.models.ContactListModel;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class ContactListPopupMenu extends JPopupMenu implements ActionListener {

    private boolean isClosing = false;
    
    private EditableJlist source;
    private JMenuItem playerName;
    private JMenuItem acceptAndAdd;
    private JMenuItem accept;
    private JMenuItem refuse;
    private JMenuItem removeContact;
    private JSeparator sep_contact;
    private JMenuItem nudge;
    private JMenuItem showProfile;
    private JMenuItem whisper;
    private JMenuItem sendFile;
    private JMenuItem roomInvite;
    private JMenuItem refreshList;
    private JMenu moveto;
    private JSeparator sep_group;
    private JCheckBoxMenuItem showOffline;
    private JMenuItem createGroup;
    private JMenuItem deleteGroup;
    private JMenuItem renameGroup;
    private JCheckBoxMenuItem mute_UnMute;
    private JCheckBoxMenuItem ban_UnBan;

    public ContactListPopupMenu(EditableJlist source) {
        super();
        this.source = source;

        playerName = new JMenuItem();
        playerName.setEnabled(false);
        playerName.putClientProperty("html.disable", Boolean.TRUE);
        accept = makeMenuItem("Accept");
        acceptAndAdd = makeMenuItem("Accept and add");
        refuse = makeMenuItem("Refuse");
        sep_contact = new JSeparator();
        nudge = makeMenuItem("Nudge");
        showProfile = makeMenuItem("Show profile");
        whisper = makeMenuItem("Whisper");
        sendFile = makeMenuItem("Send file");
        refreshList = makeMenuItem("Refresh list");
        moveto = new JMenu("Move to group");
        createGroup = makeMenuItem("Create new group");
        deleteGroup = makeMenuItem("Delete group");
        removeContact = makeMenuItem("Remove contact");
        renameGroup = makeMenuItem("Rename group");
        sep_group = new JSeparator();
        showOffline = new JCheckBoxMenuItem("Show offline contacts", Settings.getShowOfflineContacts());
        showOffline.addActionListener(this);
        roomInvite = makeMenuItem("Invite to room");
        mute_UnMute = new JCheckBoxMenuItem("Mute");
        mute_UnMute.addActionListener(this);
        ban_UnBan = new JCheckBoxMenuItem("Ban");
        ban_UnBan.addActionListener(this);

        this.add(playerName);
        this.add(new JSeparator());
        this.add(accept);
        this.add(acceptAndAdd);
        this.add(refuse);
        this.add(removeContact);
        this.add(moveto);
        this.add(sep_contact);
        this.add(nudge);
        this.add(whisper);
        this.add(sendFile);
        this.add(roomInvite);
        this.add(mute_UnMute);
        this.add(ban_UnBan);
        this.add(showProfile);
        this.add(deleteGroup);
        this.add(renameGroup);
        this.add(sep_group);
        this.add(showOffline);
        this.add(createGroup);
        this.add(refreshList);
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }

    public void refreshMoveToMenu() {
        ArrayList<String> moveToGroups = Globals.getContactList().getMoveToGroups(playerName.getText());
        
        if(moveToGroups.size() == 0){
            moveto.setVisible(false);
        }else{
            moveto.removeAll();
            for (String groupName : moveToGroups) {
                moveto.add(makeMenuItem(groupName));
            }
        }
    }
    
    public boolean isClosing(){
        return isClosing;
    }

    public void setContactActionVisibility(boolean isVisible) {
        sep_contact.setVisible(isVisible);
        moveto.setVisible(isVisible);
        removeContact.setVisible(isVisible);
        mute_UnMute.setVisible(isVisible);
        ban_UnBan.setVisible(isVisible);
        
        if(Globals.getContactList().getStatus(playerName.getText()) == ContactListElementTypes.OFFLINE){
            nudge.setVisible(false);
            sendFile.setVisible(false);
            whisper.setVisible(false);
        }else{
            nudge.setVisible(isVisible);
            whisper.setVisible(isVisible);
            sendFile.setVisible(isVisible);
        }
        
        showProfile.setVisible(isVisible);
        
        if (TabOrganizer.getRoomPanel() == null) {
            roomInvite.setVisible(false);
        } else {
            roomInvite.setVisible(isVisible);
        }        
        sep_group.setVisible(isVisible);
    }

    public void setPendingActionVisibility(boolean isVisible) {
        accept.setVisible(isVisible);
        acceptAndAdd.setVisible(isVisible);
        refuse.setVisible(isVisible);
    }

    public void setGroupActionVisibility(boolean isVisible) {
        if (playerName.getText().equals(ContactListModel.DEFAULT_GROUP)) {
            deleteGroup.setVisible(false);
            renameGroup.setVisible(false);
        } else {
            deleteGroup.setVisible(isVisible);
            renameGroup.setVisible(isVisible);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        if(visible == false && isClosing == false){
            new Thread(){
                @Override
                public void run() {
                    isClosing = true;
                    try {
                        sleep(100);
                    } catch (InterruptedException ex) {}
                    isClosing = false;
                }
            }.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ContactListModel model = ((ContactListModel) source.getModel());

        if (e.getSource() == showOffline) {
            Settings.setShowOfflineContacts(showOffline.isSelected());
        }

        final String player = playerName.getText();
        
        if (e.getSource() == accept) {
            Protocol.acceptRequest(player);
        } else if (e.getSource() == acceptAndAdd) {
            Protocol.acceptRequest(player);
            Protocol.addToContacts(player);
        } else if (e.getSource() == refuse) {
            Protocol.refuseRequest(player);
        } else if (e.getSource() == removeContact) {
            Protocol.removeContact(player);
        } else if (e.getSource() == createGroup) {
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
                editorComp.requestFocusInWindow();
            }
        } else if (e.getSource() == deleteGroup) {
            Protocol.deleteGroup(player);
        } else if (e.getSource() == refreshList) {
            Protocol.refreshContacts();
        } else if (e.getSource() == renameGroup) {
            source.editCellAt(source.getSelectedIndex(), e);
            Component editorComp = source.getEditorComponent();
            if (editorComp != null) {
                editorComp.requestFocusInWindow();
            }
        } else if (e.getSource() == whisper) {
            if (model.getStatus(player) != ContactListElementTypes.OFFLINE) {
                TabOrganizer.openPrivateChatPanel(player, true);
            }
        } else if (e.getSource() == nudge) {
            Protocol.nudge(player);
        } else if (e.getSource() == roomInvite) {
            Protocol.sendRoomInvite(player);
        } else if (e.getSource() == showProfile) {
            Protocol.requestProfile(player);
         } else if (e.getSource() == ban_UnBan) {
            if(!ban_UnBan.isSelected()){ //Somehow this is inverted
                Protocol.unBan(player);
            }else{
                Protocol.kick(player);
                Protocol.ban(player);
            }
        } else if (e.getSource() == mute_UnMute) {
            if(!mute_UnMute.isSelected()){ //Somehow this is inverted
                Protocol.unMute(player);
            }else{
                Protocol.mute(player);
            }
        } else if (e.getSource() == sendFile) {
            if (model.getStatus(player) != ContactListElementTypes.OFFLINE) {

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
                                    if(TabOrganizer.sendFile(player, inputfile)){
                                        Protocol.sendFile(player, inputfile.getName(), inputfile.length() + "", coopnetclient.utils.settings.Settings.getFiletTansferPort() + "");
                                        Globals.setLastOpenedDir(inputfile.getParent());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            ErrorHandler.handleException(e);
                        }
                    }
                }.start();
            }
        } else if (model.getGroupNames().contains(e.getActionCommand())) {//otherwise the action name is a groupname and the selected user must be moved there
            Protocol.moveToGroup(player, e.getActionCommand());
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
                playerName.setText("No selection");
                super.show(invoker, x, y);
            } else {
                playerName.setText((String) source.getSelectedValue());
                //HIDE UNNECESSARY ITEMS
                ContactListModel model = (ContactListModel) source.getModel();
                
                if(model.getStatus(source.getSelectedValue().toString()) != ContactListElementTypes.PENDING_CONTACT && model.isPending(source.getSelectedIndex())){
                    setPendingActionVisibility(true);
                    setGroupActionVisibility(false);
                    setContactActionVisibility(false);
                    sep_contact.setVisible(true);
                    
                    if(model.groupOfContact(playerName.getText()) == null){
                        acceptAndAdd.setVisible(true);
                    }else{
                        acceptAndAdd.setVisible(false);
                    }
                    
                    mute_UnMute.setVisible(true);
                    ban_UnBan.setVisible(true);
                    if(model.getStatus(playerName.getText()) != ContactListElementTypes.OFFLINE){
                        whisper.setVisible(true);
                    }
                    
                    if(model.groupOfContact(playerName.getText()) != null && model.getStatus(playerName.getText()) != ContactListElementTypes.OFFLINE){
                        //If hes online and on our list, we can also send files to this pendingcontact
                        sendFile.setVisible(true);
                    }
                    
                    showProfile.setVisible(true);
                    sep_group.setVisible(true);
                }else{
                    switch (model.getStatus(source.getSelectedValue().toString())) {
                        case GROUPNAME_CLOSED:
                        case GROUPNAME_OPEN:
                            setPendingActionVisibility(false);
                            setGroupActionVisibility(true);
                            setContactActionVisibility(false);
                            if(playerName.getText().equals(ContactListModel.DEFAULT_GROUP)){
                                sep_group.setVisible(false);
                            }else{
                                sep_group.setVisible(true);
                            }
                            break;
                        case PENDING_CONTACT:
                            setPendingActionVisibility(false);
                            setGroupActionVisibility(false);
                            setContactActionVisibility(false);
                            moveto.setVisible(true);
                            removeContact.setVisible(true);
                            sep_contact.setVisible(true);
                            mute_UnMute.setVisible(true);
                            ban_UnBan.setVisible(true);
                            whisper.setVisible(true);
                            showProfile.setVisible(true);
                            sep_group.setVisible(true);
                            break;
                        case PENDING_REQUEST:
                            setPendingActionVisibility(true);
                            setGroupActionVisibility(false);
                            setContactActionVisibility(false);
                            
                            sep_contact.setVisible(true);
                            
                            mute_UnMute.setVisible(true);
                            ban_UnBan.setVisible(true);
                            if(model.getStatus(playerName.getText()) != ContactListElementTypes.OFFLINE){
                                whisper.setVisible(true);
                            }
                            showProfile.setVisible(true);
                            
                            sep_group.setVisible(true);
                            break;
                        default:
                            setPendingActionVisibility(false);
                            setGroupActionVisibility(false);
                            setContactActionVisibility(true);
                            sep_group.setVisible(true);
                            break;
                    }
                }
                if (MuteBanList.getMuteBanStatus(playerName.getText()) == null) {
                    mute_UnMute.setSelected(false);
                    ban_UnBan.setSelected(false);
                } else {
                    switch (MuteBanList.getMuteBanStatus(playerName.getText())) {
                        case BANNED:
                            mute_UnMute.setSelected(false);
                            ban_UnBan.setSelected(true);
                            break;
                        case MUTED:
                            mute_UnMute.setSelected(true);
                            ban_UnBan.setSelected(false);
                            break;
                        case BOTH:
                            mute_UnMute.setSelected(true);
                            ban_UnBan.setSelected(true);
                            break;
                    }
                }
                
                refreshMoveToMenu();
                super.show(invoker, x, y);
            }
        }
    }
}
