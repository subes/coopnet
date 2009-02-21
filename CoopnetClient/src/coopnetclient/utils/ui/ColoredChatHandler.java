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
package coopnetclient.utils.ui;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.utils.Logger;
import coopnetclient.utils.settings.Settings;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class ColoredChatHandler {

    private static String myNameStyleName = "myname";
    private static String highlightedmyNameStyleName = "hl_myname";
    private static String otherNameStyleName = "othername";
    private static String highlightedOtherNameStyleName = "hl_othername";
    private static String messageStyleName = "message";
    private static String highlightedMessageStyleName = "hl_message";
    private static String hlinkStyleName = "hlink";
    private static String highlightedhlinkStyleName = "hl_hlink";
    private static String friendNameStyleName = "friend_name";
    private static String highlightedFriendNameStyleName = "hl_friend_name";
    private static String friendMessageStyleName = "friend_message";
    private static String highlightedFriendMessageStyleName = "hl_friend_message";
    private static String systemNameStyleName = "system";
    private static String systemMessageStyleName = "systemmessage";
    private static String whisperNameStyleName = "whispername";
    private static String highlightedWhisperNameStyleName = "hl_whispername";
    private static String whisperMessageStyleName = "whispermessage";
    private static String highlightedWhisperMessageStyleName = "hl_whispermessage";
    private static String defaultNameStyleName = "defaultname";
    private static String defaultMessageStyleName = "defaultmessage";

    //Styles have to be temporary, so they follow color changes!
    public static void addColoredText(String name, String message, ChatStyles chatStyle, StyledDocument doc, javax.swing.JScrollPane scrl_ChatOutput, javax.swing.JTextPane tp_ChatOutput, ArrayList<StyledChatMessage> messages) {

        synchronized (doc) {

            //cutting off trailing newline
            if (message.endsWith("\n")) {
                message = message.substring(0, message.length() - 1);
            }

            //storing selection position
            int start, end;
            start = tp_ChatOutput.getSelectionStart();
            end = tp_ChatOutput.getSelectionEnd();

            boolean doHighlight = Globals.isHighlighted(name);
            Boolean isFriend = Globals.getContactList().contains(name);
            String nameStyle = "";
            String messageStyle = "";
            String tempname = new String(name);
            String timeStamp = "";

            switch (chatStyle) {
                default:
                    nameStyle = defaultNameStyleName;
                    messageStyle = defaultMessageStyleName;
                    break;
                case SYSTEM:
                    nameStyle = systemNameStyleName;
                    messageStyle = systemMessageStyleName;
                    break;
                case WHISPER:
                case USER:
                    if (Globals.getThisPlayerLoginName().equals(name)) {
                        if (doHighlight) {
                            nameStyle = highlightedmyNameStyleName;
                            messageStyle = highlightedMessageStyleName;
                        }else{
                            nameStyle = myNameStyleName;
                            messageStyle = messageStyleName;
                        }                        
                    } else {
                        if (doHighlight) {
                            if (isFriend) {
                                nameStyle = highlightedFriendNameStyleName;
                                messageStyle = highlightedFriendMessageStyleName;
                            } else {
                                nameStyle = highlightedOtherNameStyleName;
                                messageStyle = highlightedMessageStyleName;
                            }
                        } else { //not highlighted
                            if (isFriend) {
                                nameStyle = friendNameStyleName;
                                messageStyle = friendMessageStyleName;
                            } else {
                                nameStyle = otherNameStyleName;
                                messageStyle = messageStyleName;
                            }
                        }
                    }
                    break;
                case WHISPER_NOTIFICATION:
                    if (doHighlight) {
                        nameStyle = highlightedWhisperNameStyleName;
                        messageStyle = highlightedWhisperMessageStyleName;
                    } else {
                        nameStyle = whisperNameStyleName;
                        messageStyle = whisperMessageStyleName;
                    }
                    break;
            }

            //printing

            if (name.length() > 0 && coopnetclient.utils.settings.Settings.getTimeStampEnabled() && chatStyle != ChatStyles.SYSTEM) {
                Date date = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
                timeStamp = "(" + dateformat.format(date) + ") ";
            }

            if (chatStyle == ChatStyles.WHISPER_NOTIFICATION) {
                tempname = timeStamp + tempname + " whispers";
            }

            if (message.startsWith("/me")) {
                tempname = "  **" + name;
                message = message.substring(3);
            } else if (name.length() > 0) {
                tempname = timeStamp + name + ": ";
            } else {
                tempname = "    ";
            }

            try {
                int namestart = doc.getLength();
                doc.insertString(doc.getLength(), "\n" + tempname, doc.getStyle(nameStyle));
                messages.add(new StyledChatMessage(name, nameStyle, namestart, doc.getLength()));

                //setup attributes
                SimpleAttributeSet nameAttributes = new SimpleAttributeSet();
                nameAttributes.addAttribute(HTML.Attribute.TYPE, "userName");
                nameAttributes.addAttribute(HTML.Attribute.DATA, name);
                nameAttributes.addAttribute(HTML.Attribute.CLASS, chatStyle);
                nameAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                doc.setCharacterAttributes(doc.getLength() - tempname.length(), tempname.length(), nameAttributes, false);

                //print each word
                String[] messageWords = message.split(" ");
                StringBuilder messageBuffer = new StringBuilder();
                int lastMessageStart = doc.getLength();
                for (String word : messageWords) {
                    //links
                    if (word.startsWith("http://") || word.startsWith("room://") || word.startsWith("www.")) {
                        //make message object from previous chunk
                        String chunk = messageBuffer.toString();
                        if (chunk.length() > 0) {
                            messages.add(new StyledChatMessage(name, messageStyle, lastMessageStart, doc.getLength()));
                            lastMessageStart = doc.getLength();
                            messageBuffer.delete(0, chunk.length());
                        }
                        //print link
                        String href = word;
                        SimpleAttributeSet hrefAttributes = new SimpleAttributeSet();
                        hrefAttributes.addAttribute(HTML.Attribute.HREF, href);
                        int hrefstart = doc.getLength();
                        if (doHighlight) {
                            doc.insertString(doc.getLength(), href, doc.getStyle(highlightedhlinkStyleName));
                            messages.add(new StyledChatMessage(name, highlightedhlinkStyleName, hrefstart, doc.getLength()));
                        } else {
                            doc.insertString(doc.getLength(), href, doc.getStyle(hlinkStyleName));
                            messages.add(new StyledChatMessage(name, hlinkStyleName, hrefstart, doc.getLength()));
                        }
                        doc.setCharacterAttributes(doc.getLength() - href.length(), href.length(), hrefAttributes, false);
                        //add a message whitespace after link
                        messageBuffer.append(" ");
                        doc.insertString(doc.getLength(), " ", doc.getStyle(messageStyle));
                        lastMessageStart = doc.getLength();
                    } else { //print normal text
                        word += " ";
                        messageBuffer.append(word);
                        doc.insertString(doc.getLength(), word, doc.getStyle(messageStyle));
                    }
                }
                //pring last part if any
                String chunk = messageBuffer.toString();
                if (chunk.length() > 0) {
                    messages.add(new StyledChatMessage(name, messageStyle, lastMessageStart, doc.getLength()));
                    lastMessageStart = doc.getLength();
                    messageBuffer.delete(0, chunk.length());
                }
            } catch (BadLocationException ex) {//won't happen
                ex.printStackTrace();
            }
            //scroll
            tp_ChatOutput.setCaretPosition( doc.getLength() );
            //restore selection
            if(start != end){
                tp_ChatOutput.setSelectionStart(start);
                tp_ChatOutput.setSelectionEnd(end);
            }
        }
    }

    public static void setupStyles(StyledDocument doc) {

        //removing the old styles
        doc.removeStyle(myNameStyleName);
        doc.removeStyle(highlightedmyNameStyleName);
        doc.removeStyle(messageStyleName);
        doc.removeStyle(hlinkStyleName);
        doc.removeStyle(highlightedMessageStyleName);
        doc.removeStyle(friendNameStyleName);
        doc.removeStyle(highlightedFriendNameStyleName);
        doc.removeStyle(friendMessageStyleName);
        doc.removeStyle(highlightedFriendMessageStyleName);
        doc.removeStyle(highlightedhlinkStyleName);
        doc.removeStyle(systemNameStyleName);
        doc.removeStyle(systemMessageStyleName);
        doc.removeStyle(whisperNameStyleName);
        doc.removeStyle(highlightedWhisperMessageStyleName);
        doc.removeStyle(defaultNameStyleName);
        doc.removeStyle(defaultMessageStyleName);

        //Setting style and colors
        Style myNameStyle = doc.addStyle(myNameStyleName, null);
        Style hl_myNameStyle = doc.addStyle(highlightedmyNameStyleName, myNameStyle);
        Style otherNameStyle = doc.addStyle(otherNameStyleName, null);
        Style hl_otherNameStyle = doc.addStyle(highlightedOtherNameStyleName, otherNameStyle);
        Style friendNameStyle = doc.addStyle(friendNameStyleName, myNameStyle);
        Style hl_friendNameStyle = doc.addStyle(highlightedFriendNameStyleName, friendNameStyle);
        Style messageStyle = doc.addStyle(messageStyleName, null);
        Style hl_messageStyle = doc.addStyle(highlightedMessageStyleName, messageStyle);
        Style friendMessageStyle = doc.addStyle(friendMessageStyleName, messageStyle);
        Style hl_friendMessageStyle = doc.addStyle(highlightedFriendMessageStyleName, friendMessageStyle);
        Style hlinkStyle = doc.addStyle(hlinkStyleName, null);
        Style hl_hlinkStyle = doc.addStyle(highlightedhlinkStyleName, hlinkStyle);
        Style systemNameStyle = doc.addStyle(systemNameStyleName, null);
        Style systemMessageStyle = doc.addStyle(systemMessageStyleName, null);
        Style whisperNameStyle = doc.addStyle(whisperNameStyleName, myNameStyle);
        Style hl_whisperNameStyle = doc.addStyle(highlightedWhisperNameStyleName, whisperNameStyle);
        Style whisperMessageStyle = doc.addStyle(whisperMessageStyleName, messageStyle);
        Style hl_whisperMessageStyle = doc.addStyle(highlightedWhisperMessageStyleName, hl_messageStyle);
        Style defaultNameStyle = doc.addStyle(defaultNameStyleName, null);
        Style defaultMessageStyle = doc.addStyle(defaultMessageStyleName, null);

        //highlight bg colors
        Color highlightingColor = getHighlightingColor();

        StyleConstants.setBackground(hl_otherNameStyle, highlightingColor);
        StyleConstants.setBackground(hl_friendNameStyle, highlightingColor);
        StyleConstants.setBackground(hl_friendMessageStyle, highlightingColor);
        StyleConstants.setBackground(hl_messageStyle, highlightingColor);
        StyleConstants.setBackground(hl_hlinkStyle, highlightingColor);
        StyleConstants.setBackground(hl_whisperNameStyle, highlightingColor);
        StyleConstants.setBackground(hl_whisperMessageStyle, highlightingColor);
        StyleConstants.setBackground(hl_myNameStyle, highlightingColor);

        //init link style
        StyleConstants.setForeground(hlinkStyle, Color.BLUE);
        StyleConstants.setUnderline(hlinkStyle, true);
        //other styles
        //note: styles have a hierarchic structure, only top levels and odd styles need to be set
        if (coopnetclient.utils.settings.Settings.getColorizeText()) {
            StyleConstants.setForeground(systemNameStyle, coopnetclient.utils.settings.Settings.getSystemMessageColor());
            StyleConstants.setForeground(systemMessageStyle, coopnetclient.utils.settings.Settings.getSystemMessageColor());
            StyleConstants.setForeground(myNameStyle, coopnetclient.utils.settings.Settings.getYourUsernameColor());
            StyleConstants.setForeground(otherNameStyle, coopnetclient.utils.settings.Settings.getOtherUsernamesColor());
            StyleConstants.setForeground(messageStyle, coopnetclient.utils.settings.Settings.getUserMessageColor());
            StyleConstants.setForeground(whisperNameStyle, coopnetclient.utils.settings.Settings.getWhisperMessageColor());
            StyleConstants.setForeground(whisperMessageStyle, coopnetclient.utils.settings.Settings.getWhisperMessageColor());
            StyleConstants.setForeground(friendNameStyle, coopnetclient.utils.settings.Settings.getFriendUsernameColor());
            StyleConstants.setForeground(friendMessageStyle, coopnetclient.utils.settings.Settings.getFriendMessageColor());
        }
        StyleConstants.setFontFamily(systemMessageStyle, "monospaced");
        StyleConstants.setFontFamily(systemNameStyle, "monospaced");
        StyleConstants.setFontFamily(myNameStyle, coopnetclient.utils.settings.Settings.getNameStyle());
        StyleConstants.setFontFamily(otherNameStyle, coopnetclient.utils.settings.Settings.getNameStyle());
        StyleConstants.setFontFamily(messageStyle, coopnetclient.utils.settings.Settings.getMessageStyle());
        StyleConstants.setFontFamily(hlinkStyle, coopnetclient.utils.settings.Settings.getMessageStyle());
        StyleConstants.setFontFamily(defaultNameStyle, coopnetclient.utils.settings.Settings.getNameStyle());
        StyleConstants.setFontFamily(defaultMessageStyle, coopnetclient.utils.settings.Settings.getMessageStyle());

        StyleConstants.setFontSize(systemNameStyle, coopnetclient.utils.settings.Settings.getNameSize());
        StyleConstants.setFontSize(systemMessageStyle, coopnetclient.utils.settings.Settings.getMessageSize());
        StyleConstants.setFontSize(myNameStyle, coopnetclient.utils.settings.Settings.getNameSize());
        StyleConstants.setFontSize(otherNameStyle, coopnetclient.utils.settings.Settings.getNameSize());
        StyleConstants.setFontSize(messageStyle, coopnetclient.utils.settings.Settings.getMessageSize());
        StyleConstants.setFontSize(hlinkStyle, coopnetclient.utils.settings.Settings.getMessageSize());
        StyleConstants.setFontSize(defaultNameStyle, coopnetclient.utils.settings.Settings.getNameSize());
        StyleConstants.setFontSize(defaultMessageStyle, coopnetclient.utils.settings.Settings.getMessageSize());

    }

    public static void updateHighLight(StyledDocument doc, ArrayList<StyledChatMessage> messages) {
        synchronized (doc) {

            for (StyledChatMessage message : messages) {
                boolean doHighlight = Globals.isHighlighted(message.getSenderName());
                if (doHighlight) {
                    if (!message.getStyle().startsWith("hl_")) {
                        message.setStyle("hl_" + message.getStyle());
                    }
                } else {//unhighlight
                    if (message.getStyle().startsWith("hl_")) {
                        message.setStyle(message.getStyle().substring(3));
                    }
                }
                doc.setCharacterAttributes(message.getContentStartIndex(), message.getContentEndIndex() - message.getContentStartIndex(), doc.getStyle(message.getStyle()), true);
                if (message.getStyle().contains("hlink")) {//fix link
                    try {
                        SimpleAttributeSet hrefAttributes = new SimpleAttributeSet();
                        hrefAttributes.addAttribute(HTML.Attribute.HREF, doc.getText(message.getContentStartIndex(), message.getContentEndIndex() - message.getContentStartIndex()));
                        doc.setCharacterAttributes(message.getContentStartIndex(), message.getContentEndIndex() - message.getContentStartIndex(), hrefAttributes, false);
                    } catch (Exception e) {
                        Logger.log(e);
                    }
                }
            }
        }
    }

    private static Color getHighlightingColor(){
        Color highlightingColor;

        if (Settings.getColorizeBody()) {
            highlightingColor = Settings.getSelectionColor();
        } else {
            highlightingColor = (Color) UIManager.get("List.selectionBackground");
            if (highlightingColor == null) {
                highlightingColor = (Color) UIManager.get("List[Selected].textBackground");
            }
        }

        final int alpha = 100;

        return new Color(
                highlightingColor.getRed(),
                highlightingColor.getGreen(),
                highlightingColor.getBlue(),
                alpha);
    }
}
