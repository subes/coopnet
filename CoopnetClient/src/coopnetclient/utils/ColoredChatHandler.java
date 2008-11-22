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
package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
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

    //Styles have to be temporary, so they follow color changes!
    public static void addColoredText(String name, String message, ChatStyles chatStyle, StyledDocument doc, javax.swing.JScrollPane scrl_ChatOutput, javax.swing.JTextPane tp_ChatOutput) {

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

        switch (chatStyle) {
            case SYSTEM:
                if (coopnetclient.utils.Settings.getColorizeText()) {
                    StyleConstants.setForeground(nameStyle, coopnetclient.utils.Settings.getSystemMessageColor());
                    StyleConstants.setForeground(messageStyle, coopnetclient.utils.Settings.getSystemMessageColor());
                }

                StyleConstants.setFontFamily(nameStyle, "monospaced");
                StyleConstants.setFontSize(nameStyle, coopnetclient.utils.Settings.getNameSize());

                StyleConstants.setFontFamily(messageStyle, "monospaced");
                StyleConstants.setFontSize(messageStyle, coopnetclient.utils.Settings.getMessageSize());

                break;
            case WHISPER:
            //Identical as USER atm, but we might want to change something sometime
            case USER:
                if (coopnetclient.utils.Settings.getColorizeText()) {
                    if (name.equals(Globals.getThisPlayer_loginName())) {
                        StyleConstants.setForeground(nameStyle, coopnetclient.utils.Settings.getYourUsernameColor());
                    } else {
                        StyleConstants.setForeground(nameStyle, coopnetclient.utils.Settings.getOtherUsernamesColor());
                    }
                    StyleConstants.setForeground(messageStyle, coopnetclient.utils.Settings.getUserMessageColor());
                }

                StyleConstants.setFontFamily(nameStyle, coopnetclient.utils.Settings.getNameStyle());
                StyleConstants.setFontSize(nameStyle, coopnetclient.utils.Settings.getNameSize());

                StyleConstants.setFontFamily(messageStyle, coopnetclient.utils.Settings.getMessageStyle());
                StyleConstants.setFontSize(messageStyle, coopnetclient.utils.Settings.getMessageSize());

                break;
            case WHISPER_NOTIFICATION:
                if (coopnetclient.utils.Settings.getColorizeText()) {
                    StyleConstants.setForeground(nameStyle, coopnetclient.utils.Settings.getWhisperMessageColor());
                    StyleConstants.setForeground(messageStyle, coopnetclient.utils.Settings.getWhisperMessageColor());
                }

                StyleConstants.setFontFamily(nameStyle, coopnetclient.utils.Settings.getNameStyle());
                StyleConstants.setFontSize(nameStyle, coopnetclient.utils.Settings.getNameSize());

                StyleConstants.setFontFamily(messageStyle, coopnetclient.utils.Settings.getMessageStyle());
                StyleConstants.setFontSize(messageStyle, coopnetclient.utils.Settings.getMessageSize());

                break;
            default:
                if (coopnetclient.utils.Settings.getColorizeBody()) {
                    StyleConstants.setForeground(nameStyle, coopnetclient.utils.Settings.getForegroundColor());
                    StyleConstants.setForeground(messageStyle, coopnetclient.utils.Settings.getForegroundColor());
                }

                StyleConstants.setFontFamily(nameStyle, coopnetclient.utils.Settings.getNameStyle());
                StyleConstants.setFontSize(nameStyle, coopnetclient.utils.Settings.getNameSize());

                StyleConstants.setFontFamily(messageStyle, coopnetclient.utils.Settings.getMessageStyle());
                StyleConstants.setFontSize(messageStyle, coopnetclient.utils.Settings.getMessageSize());
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
        if (chatStyle == ChatStyles.WHISPER_NOTIFICATION) {
            name += " whispers";
        }

        if (name.length() != 0 && coopnetclient.utils.Settings.getTimeStampEnabled() && chatStyle != ChatStyles.SYSTEM) {
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


        try {
            tp_ChatOutput.setCaretPosition(tp_ChatOutput.getDocument().getLength());
            doc.insertString(doc.getLength(), "\n" + name, doc.getStyle(nameStyleName));
            String[] messageWords = message.split(" ");
            for (String word : messageWords) {
                if (word.startsWith("http://") || word.startsWith("room://") || word.startsWith("voice://")) {
                    //print link
                    String href = word;
                    SimpleAttributeSet attr2 = new SimpleAttributeSet();
                    attr2.addAttribute(HTML.Attribute.HREF, href);
                    try {
                        doc.insertString(doc.getLength(), href, doc.getStyle(hlinkStyleName));
                        doc.setCharacterAttributes(doc.getLength() - href.length() - 1, doc.getLength(), attr2, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    attr2.removeAttributes(attr2.getAttributeNames());
                } else {//print normal text
                    doc.insertString(doc.getLength(), word, doc.getStyle(messageStyleName));
                }
                doc.insertString(doc.getLength(), " ", doc.getStyle(messageStyleName));
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
