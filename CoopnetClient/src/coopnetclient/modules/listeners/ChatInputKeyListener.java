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

package coopnetclient.modules.listeners;

import coopnetclient.frames.clientframepanels.PrivateChatPanel;
import coopnetclient.Client;
import coopnetclient.Protocol;
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
        this.prefix = prefix; // players name in private chat or channel name
        this.mode = mode;
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
                source.setText(source.getText() + "\n");
            } else {

                String command = source.getText();

                //dont send anything if empty
                if (command.equals("")) {
                    return;
                }
                if ((command.trim()).equals("")) {
                    return;
                }

                if (this.mode == CHANNEL_CHAT_MODE) { //main chat
                    Client.send(Protocol.mainChat(command), prefix);
                } else if (this.mode == ROOM_CHAT_MODE) { //room chat
                    Client.send(Protocol.roomChat(command), prefix);
                } else if (this.mode == PRIVATE_CHAT_MODE) { // private chat

                    int index = Client.clientFrame.indexOfTab(prefix);

                    PrivateChatPanel privatechat = null;
                    if (index != -1) {
                        privatechat = (PrivateChatPanel) Client.clientFrame.getTabComponentAt(index);
                    }

                    if (privatechat != null) {
                        privatechat.append(Client.thisPlayer_loginName, command);
                    }
                    Client.send(Protocol.privatechat(command, prefix), null);
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
