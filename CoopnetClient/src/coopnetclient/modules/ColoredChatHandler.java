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

package coopnetclient.modules;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JScrollBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class ColoredChatHandler {

    public final static int DEFAULT_STYLE = 0;
    public final static int SYSTEM_STYLE = 1;
    public final static int USER_STYLE = 2;
    public final static int PRIVATE_NOTIFICATION_STYLE = 3;
    public final static int PRIVATE_STYLE = 4;

    //Styles have to be temporary, so they follow color changes!
    public static void addColoredText(String name, String message, int styleMode, StyledDocument doc, javax.swing.JScrollPane scrl_ChatOutput, javax.swing.JTextPane tp_ChatOutput) {

        String nameStyleName = "name";
        String messageStyleName = "message";
        String hlinkStyleName = "hlink";

        //Setting style and colors
        Style nameStyle = doc.addStyle(nameStyleName, null);
        Style messageStyle = doc.addStyle(messageStyleName, null);
        Style hlinkStyle = doc.addStyle(hlinkStyleName, null);

        //init link style
        StyleConstants.setForeground(hlinkStyle, Color.BLUE);
        StyleConstants.setUnderline(hlinkStyle, true);

        if (styleMode == SYSTEM_STYLE) {
            //systemmessage

            if (coopnetclient.Settings.getColorizeText()) {
                StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getSystemMessageColor());
                StyleConstants.setForeground(messageStyle, coopnetclient.Settings.getSystemMessageColor());
            }

            StyleConstants.setFontFamily(nameStyle, "monospaced");
            StyleConstants.setFontSize(nameStyle, coopnetclient.Settings.getNameSize());

            StyleConstants.setFontFamily(messageStyle, "monospaced");
            StyleConstants.setFontSize(messageStyle, coopnetclient.Settings.getMessageSize());

        } else if (styleMode == USER_STYLE) {
            //usermessage

            if (coopnetclient.Settings.getColorizeText()) {
                if (name.equals(coopnetclient.Settings.getLastLoginName())) {
                    StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getYourUsernameColor());
                } else {
                    StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getOtherUsernamesColor());
                }
                StyleConstants.setForeground(messageStyle, coopnetclient.Settings.getUserMessageColor());
            }

            StyleConstants.setFontFamily(nameStyle, coopnetclient.Settings.getNameStyle());
            StyleConstants.setFontSize(nameStyle, coopnetclient.Settings.getNameSize());

            StyleConstants.setFontFamily(messageStyle, coopnetclient.Settings.getMessageStyle());
            StyleConstants.setFontSize(messageStyle, coopnetclient.Settings.getMessageSize());

        } else if (styleMode == PRIVATE_NOTIFICATION_STYLE) {
            //private chat notification

            if (coopnetclient.Settings.getColorizeText()) {
                StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getWhisperMessageColor());
                StyleConstants.setForeground(messageStyle, coopnetclient.Settings.getWhisperMessageColor());
            }

            StyleConstants.setFontFamily(nameStyle, coopnetclient.Settings.getNameStyle());
            StyleConstants.setFontSize(nameStyle, coopnetclient.Settings.getNameSize());

            StyleConstants.setFontFamily(messageStyle, coopnetclient.Settings.getMessageStyle());
            StyleConstants.setFontSize(messageStyle, coopnetclient.Settings.getMessageSize());

        } else if (styleMode == PRIVATE_STYLE) {
            //usermessage

            if (coopnetclient.Settings.getColorizeText()) {
                if (name.equals(coopnetclient.Settings.getLastLoginName())) {
                    StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getYourUsernameColor());
                } else {
                    StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getOtherUsernamesColor());
                }
                StyleConstants.setForeground(messageStyle, coopnetclient.Settings.getUserMessageColor());
            }

            StyleConstants.setFontFamily(nameStyle, coopnetclient.Settings.getNameStyle());
            StyleConstants.setFontSize(nameStyle, coopnetclient.Settings.getNameSize());

            StyleConstants.setFontFamily(messageStyle, coopnetclient.Settings.getMessageStyle());
            StyleConstants.setFontSize(messageStyle, coopnetclient.Settings.getMessageSize());

        } else {
            //regular

            if (coopnetclient.Settings.getColorizeBody()) {
                StyleConstants.setForeground(nameStyle, coopnetclient.Settings.getForegroundColor());
                StyleConstants.setForeground(messageStyle, coopnetclient.Settings.getForegroundColor());
            }

            StyleConstants.setFontFamily(nameStyle, coopnetclient.Settings.getNameStyle());
            StyleConstants.setFontSize(nameStyle, coopnetclient.Settings.getNameSize());

            StyleConstants.setFontFamily(messageStyle, coopnetclient.Settings.getMessageStyle());
            StyleConstants.setFontSize(messageStyle, coopnetclient.Settings.getMessageSize());

        }

        //autoscrolling setup
        JScrollBar vbar = scrl_ChatOutput.getVerticalScrollBar();
        boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) > (vbar.getMaximum() - 5));

        //cutting off trailing newline
        if (message.endsWith("\n")) {
            message = message.substring(0, message.length() - 1);
        }

        //storing selection position
        int start, end;
        start = tp_ChatOutput.getSelectionStart();
        end = tp_ChatOutput.getSelectionEnd();

        //printing
        if (styleMode == PRIVATE_NOTIFICATION_STYLE) {
            name += " whispers";
        }

        if (name.length() != 0 && coopnetclient.Settings.getTimeStampEnabled() && styleMode != SYSTEM_STYLE) {
            Date date = new Date();
            SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

            name = "(" + dateformat.format(date) + ") " + name;

        }
        
        if (message.startsWith("/me")) {
            name = "  **" + name;
            message = message.substring(3);
        } else if (name.length() > 0) {
            name = name + ": ";
        } else {
            name = "    ";
        }

        int linkstart = message.indexOf("http://");
        int linkend = message.indexOf(' ', linkstart);
        String href = null;
        SimpleAttributeSet attr2 = new SimpleAttributeSet();
        String messageend = null;
        if (linkstart != -1) {
            if (linkend == -1) {
                linkend = message.length();
            }
            href = message.substring(linkstart, linkend);
            attr2.addAttribute(HTML.Attribute.HREF, href);
            messageend = message.substring(linkend);
            message = message.substring(0, linkstart);
        }
        try {
            tp_ChatOutput.setCaretPosition(tp_ChatOutput.getDocument().getLength());
            doc.insertString(doc.getLength(), "\n" + name, doc.getStyle(nameStyleName));
            doc.insertString(doc.getLength(), message, doc.getStyle(messageStyleName));

            if (linkstart != -1) {
                StyledDocument m_doc = (StyledDocument) tp_ChatOutput.getDocument();
                try {
                    doc.insertString(doc.getLength(), href, doc.getStyle(hlinkStyleName));
                    m_doc.setCharacterAttributes(m_doc.getLength() - href.length() - 1, m_doc.getLength(), attr2, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                attr2.removeAttributes(attr2.getAttributeNames());
                doc.insertString(doc.getLength(), messageend, doc.getStyle(messageStyleName));
            }

        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        //autoscrolling
        if (autoScroll) {
            tp_ChatOutput.setCaretPosition(doc.getLength());
            tp_ChatOutput.setSelectionStart(doc.getLength());
            tp_ChatOutput.setSelectionEnd(doc.getLength());
        } else {
            //restoring selection
            tp_ChatOutput.setSelectionStart(start);
            tp_ChatOutput.setSelectionEnd(end);
        }

        //removing the styles again
        doc.removeStyle(nameStyleName);
        doc.removeStyle(messageStyleName);
        doc.removeStyle(hlinkStyleName);
    }
}
