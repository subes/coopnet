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

import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import coopnetclient.utils.ui.ColoredChatHandler;
import coopnetclient.utils.ui.StyledChatMessage;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class ChatOutput extends JScrollPane {

    private JTextPane textPane;
    private boolean disableAutoScroll;
    private ArrayList<StyledChatMessage> messages;

    public ChatOutput() {
        super();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.setFocusable(false);
        textPane = new JTextPane() {

            @Override
            public void scrollRectToVisible(Rectangle aRect) {
                if (!disableAutoScroll) {
                    super.scrollRectToVisible(aRect);
                }
            }
        };
        this.setViewportView(textPane);
        this.setAutoscrolls(true);
        textPane.setEditable(false);
        textPane.setAutoscrolls(true);
        textPane.addMouseListener(new HyperlinkMouseListener());
       
        messages = new ArrayList<StyledChatMessage>();
        StyledDocument doc = textPane.getStyledDocument();
        ColoredChatHandler.setupStyles(doc);
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void customCodeForPopupMenu(){
        textPane.setComponentPopupMenu(new ChatOutputPopupMenu(textPane));
    }

    public void printChatMessage(String name, String message, ChatStyles modeStyle) {
        JScrollBar vbar = (JScrollBar) this.getVerticalScrollBar();
        if ((vbar.getValue() + vbar.getVisibleAmount()) > vbar.getMaximum() - 5) {
            disableAutoScroll = false;
        } else if (!disableAutoScroll) {
            disableAutoScroll = true;
        }
        StyledDocument doc = textPane.getStyledDocument();
        ColoredChatHandler.addColoredText(name, message, modeStyle, doc, this, textPane, messages);
    }

    public void updateStyle() {
        StyledDocument doc = textPane.getStyledDocument();
        ColoredChatHandler.setupStyles(doc);
    }

    public void updateHighlights() {
        StyledDocument doc = textPane.getStyledDocument();
        ColoredChatHandler.updateHighLight(doc, messages);
    }
}
