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

import coopnetclient.frames.clientframe.tabs.PrivateChatPanel;
import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.TabOrganizer;
import java.awt.event.*;
import javax.swing.*;

public class ChatInputKeyListener implements KeyListener {

    public final static int CHANNEL_CHAT_MODE = 0;
    public final static int ROOM_CHAT_MODE = 1;
    public final static int PRIVATE_CHAT_MODE = 2;
    private boolean ctrlIsPressed;
    public int mode;
    private String prefix;

    /**
     * modes:   <br>
     * 0 - main     <br>
     * 1 - room<    br>
     * 2 - private  <br>
     */
    public ChatInputKeyListener(int mode, String prefix) {
        this.prefix = prefix; // players name in private chat or channel ID
        this.mode = mode;
    }

    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JTextPane source = (JTextPane) e.getSource();
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlIsPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            if (ctrlIsPressed) { //CTRL+ENTER prints new line
                if(source.getText().length() > 0 && !source.getText().startsWith("\n") && !source.getText().endsWith("\n\n")){
                    source.setText(source.getText() + "\n");
                }
            } else {

                String command = source.getText();

                //dont send anything if empty
                if (command.equals("")) {
                    return;
                }
                if ((command.trim()).equals("")) {
                    return;
                }
                if(command.length() > 2500){
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Could not send message, because it is too big!", ChatStyles.SYSTEM,false);
                    source.setText("");
                    return;
                }

                //whisper command
                if(command.startsWith("/w ")){
                    String tail = command.substring(3);
                    int nameEnd = tail.indexOf(" ");
                    if(nameEnd == -1){
                        Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Usage: /w username message", ChatStyles.SYSTEM,false);
                        source.setText("");
                        return;
                    }                    
                    String name = tail.substring(0, nameEnd);
                    String msg = tail.substring(nameEnd+1 , tail.length());
                    if(name.equals(Globals.getThisPlayer_loginName())){
                        Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Private chat with yourself is not allowed!", ChatStyles.SYSTEM,false);
                        source.setText("");
                        return;
                    }
                    Protocol.privateChat(name, msg);
                    source.setText("");
                    return;
                }
                
                if(command.startsWith("/invite ")){
                    String name = command.substring(8);
                    Protocol.sendRoomInvite(name);
                    source.setText("");
                    return;
                }
                
                if (this.mode == CHANNEL_CHAT_MODE) { //main chat
                    Protocol.mainChat(prefix, command);
                } else if (this.mode == ROOM_CHAT_MODE) { //room chat
                    Protocol.roomChat(command);
                } else if (this.mode == PRIVATE_CHAT_MODE) { // private chat

                    PrivateChatPanel privatechat = TabOrganizer.getPrivateChatPanel(prefix);

                    if (privatechat != null) {
                        privatechat.append(Globals.getThisPlayer_loginName(), command, ChatStyles.WHISPER);
                    }
                    Protocol.privateChat(prefix, command );
                }
                source.setText("");
            }
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
