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
package coopnetclient.frames.listeners;

import coopnetclient.frames.clientframetabs.PrivateChatPanel;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextPane;

public class ChatInputKeyListener implements KeyListener {

    public static final int CHANNEL_CHAT_MODE = 0;
    public static final int ROOM_CHAT_MODE = 1;
    public static final int PRIVATE_CHAT_MODE = 2;
    public static final int MIN_MESSAGE_INTERVAL = 800; //ms
    public static final int MAX_ALLOWED_RAPID_MESSAGES = 3;
    public static final int[] MAXIMUM_MESSAGE_LENGTHS = {1000, 2500, 2500};
    public static final String MSG_TOO_LONG_CHANNEL = "Long messages not allowed in channels!";
    public static final String MSG_TOO_LONG_OTHER = "Could not send message, because it is too big!";
    public static final String WHISPER_SELF = "Private chat with yourself is not allowed!";
    public static final String SPAM_WARNING = "SPAM WARNING! Send messages slower!";
    private boolean ctrlIsPressed;
    private int mode;
    private String prefix;
    private long lastMessageDate;
    private int spamCount;

    public ChatInputKeyListener(int mode, String prefix) {
        this.prefix = prefix; // players name in private chat or channel ID
        this.mode = mode;
        lastMessageDate = System.currentTimeMillis();
    }

    public void resetCTRLStatus(){
        ctrlIsPressed = false;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JTextPane source = (JTextPane) e.getSource();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_CONTROL:
                ctrlIsPressed = true;
                break;
            case KeyEvent.VK_ENTER:
                if (ctrlIsPressed) { //CTRL+ENTER prints new line
                    if (source.getText().length() > 0 
                            && !source.getText().startsWith("\n")
                            && !source.getText().endsWith("\n\n")) {
                        source.setText(source.getText() + "\n");
                    }
                } else {
                    String command = source.getText();

                    //dont send anything if empty
                    if ((command.trim()).equals("")) {
                        return;
                    }
                    //ANTI_SPAM:
                    //A - prevent spamming by disallowing rapid messages
                    long date = System.currentTimeMillis();
                    if ((date - lastMessageDate) < MIN_MESSAGE_INTERVAL) {
                        lastMessageDate = date;
                        spamCount++;
                        if (spamCount > MAX_ALLOWED_RAPID_MESSAGES) {
                            FrameOrganizer.getClientFrame().printSystemMessage(SPAM_WARNING, false);
                            source.setText("");
                            return;
                        }
                    } else {
                        spamCount = 0;
                        lastMessageDate = date;
                    }

                    //B - TODO if message is "long" check agains a pattern to see
                    //if its the same text repeated many times -> spam

                    //C - disallow long messages
                    //check length
                    if (command.length() > MAXIMUM_MESSAGE_LENGTHS[mode]) {
                        if (mode == CHANNEL_CHAT_MODE) {
                            FrameOrganizer.getClientFrame().printSystemMessage(MSG_TOO_LONG_CHANNEL, false);
                        } else {
                            FrameOrganizer.getClientFrame().printSystemMessage(MSG_TOO_LONG_OTHER, false);
                        }
                        source.setText("");
                        return;
                    }

                    //whisper command
                    if (command.startsWith("/w ")) {
                        String tail = command.substring(3);
                        int nameEnd = tail.indexOf(" ");
                        if (nameEnd == -1) {
                            FrameOrganizer.getClientFrame().printSystemMessage("Usage: /w username message", false);
                            source.setText("");
                            return;
                        }
                        String name = tail.substring(0, nameEnd);
                        String msg = tail.substring(nameEnd + 1, tail.length());
                        if (name.equals(Globals.getThisPlayerLoginName())) {
                            FrameOrganizer.getClientFrame().printSystemMessage(WHISPER_SELF, false);
                            source.setText("");
                            return;
                        }
                        PrivateChatPanel privatechat = TabOrganizer.getPrivateChatPanel(name);

                        if (privatechat != null) {
                            privatechat.append(Globals.getThisPlayerLoginName(), msg, ChatStyles.WHISPER);
                        }
                        
                        Protocol.privateChat(name, msg);
                        source.setText("");
                        return;
                    }
                    //invite command
                    if (command.startsWith("/invite ")) {
                        String name = command.substring(8);
                        Protocol.sendRoomInvite(name);
                        source.setText("");
                        return;
                    }

                    //chat command
                    if (this.mode == CHANNEL_CHAT_MODE) { //main chat
                        Protocol.mainChat(prefix, command);
                    } else if (this.mode == ROOM_CHAT_MODE) { //room chat
                        Protocol.roomChat(command);
                    } else if (this.mode == PRIVATE_CHAT_MODE) { // private chat

                        PrivateChatPanel privatechat = TabOrganizer.getPrivateChatPanel(prefix);

                        if (privatechat != null) {
                            privatechat.append(Globals.getThisPlayerLoginName(), command, ChatStyles.WHISPER);
                        }
                        Protocol.privateChat(prefix, command);
                    }
                    source.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        JTextPane source = (JTextPane) e.getSource();
        //reset the type field when command was sent
        if (e.getKeyCode() == KeyEvent.VK_CONTROL && ctrlIsPressed) {
            ctrlIsPressed = false;
        } else if (e.getKeyCode() == (KeyEvent.VK_ENTER) && !ctrlIsPressed) {
            source.setText("");
        }
    }
}
