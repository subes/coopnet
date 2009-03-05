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

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.threads.ErrThread;
import coopnetclient.utils.MuteBanList;
import coopnetclient.utils.filechooser.FileChooser;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

public class PlayerListPopupMenu extends JPopupMenu implements ActionListener {

    private JList parent;
    private JMenuItem nudge;
    private JMenuItem playerName;
    private JMenuItem roomInvite;
    private JMenuItem whisper;
    private JMenuItem sendFile;
    private JCheckBoxMenuItem highlight;
    private JMenuItem addContact;
    private JMenuItem kick;
    private JCheckBoxMenuItem mute_UnMute;
    private JCheckBoxMenuItem ban_UnBan;
    private JMenuItem showProfile;

    public PlayerListPopupMenu(boolean playerIsHost, JList parent) {
        super();
        this.parent = parent;

        playerName = new JMenuItem();
        playerName.setEnabled(false);
        playerName.putClientProperty("html.disable", Boolean.TRUE);
        this.add(playerName);

        this.addSeparator();

        nudge = new JMenuItem("Nudge");
        nudge.addActionListener(this);
        this.add(nudge);

        roomInvite = new JMenuItem("Invite to room");
        roomInvite.addActionListener(this);
        this.add(roomInvite);

        whisper = new JMenuItem("Whisper");
        whisper.addActionListener(this);
        this.add(whisper);

        sendFile = new JMenuItem("Send file");
        sendFile.addActionListener(this);
        this.add(sendFile);

        highlight = new JCheckBoxMenuItem("Highlight messages");
        highlight.addActionListener(this);
        this.add(highlight);

        addContact = new JMenuItem("Add to contacts");
        addContact.addActionListener(this);
        this.add(addContact);

        this.add(new JSeparator());

        if (playerIsHost) {
            kick = new JMenuItem("Kick");
            kick.addActionListener(this);
            this.add(kick);
        }

        mute_UnMute = new JCheckBoxMenuItem("Mute");
        mute_UnMute.addActionListener(this);
        this.add(mute_UnMute);

        ban_UnBan = new JCheckBoxMenuItem("Ban");
        ban_UnBan.addActionListener(this);
        this.add(ban_UnBan);

        this.addSeparator();

        showProfile = new JMenuItem("Show profile");
        showProfile.addActionListener(this);
        this.add(showProfile);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (parent == null) {
            return;
        }

        final String player = playerName.getText();
        if (player == null) {
            return;
        }

        if (e.getSource() == highlight) {
            if(!highlight.isSelected()){ //Somehow this is inverted
                Globals.unSetHighlightOn(player);
            }else{
                Globals.setHighlightOn(player);
            }
        }else if (e.getSource() == kick) {
            Protocol.kick(player);
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
        } else if (e.getSource() == addContact){
            Protocol.addToContacts(player);
        } else if (e.getSource() == whisper) {
            TabOrganizer.openPrivateChatPanel(player, true);
        } else if (e.getSource() == showProfile) {
            Protocol.requestProfile(player);
        } else if (e.getSource() == roomInvite) {
            Protocol.sendRoomInvite(player);
        } else if (e.getSource() == nudge) {
            Protocol.nudge(player);
        } else if (e.getSource() == sendFile) {

            new ErrThread() {
                @Override
                public void handledRun() throws Throwable {
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
                }
            }.start();
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        if (parent == null || parent.getSelectedValue() == null || parent.getSelectedValue().equals(Globals.getThisPlayerLoginName())) {
            setVisible(false);
            return;
        } else {
            playerName.setText((String) parent.getSelectedValue());
        }
        
        if(Globals.getContactList().contains( playerName.getText() )){
            addContact.setVisible(false);
        }else{
            addContact.setVisible(true);
        }
        if (TabOrganizer.getRoomPanel() == null) {
            roomInvite.setVisible(false);
        } else {
            roomInvite.setVisible(true);
        }

        highlight.setSelected(Globals.isHighlighted(playerName.getText()));
        
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
        super.show(invoker, x, y);
    }
}
