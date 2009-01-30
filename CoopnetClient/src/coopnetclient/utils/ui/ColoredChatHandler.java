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
package coopnetclient.utils.ui;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.utils.Settings;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class ColoredChatHandler {

    private static String nameStyleName = "name";
    private static String messageStyleName = "message";
    private static String hlinkStyleName = "hlink";
    private static String highlightedNameStyleName = "hl_name";
    private static String highlightedMessageStyleName = "hl_message";
    private static String highlightedhlinkStyleName = "hl_hlink";
    private static String friendNameStyleName = "friend_name";
    private static String friendMessageStyleName = "friend_message";
    private static String highlightedFriendNameStyleName = "hl_friend_name";
    private static String highlightedFriendMessageStyleName = "hl_friend_message";

    //Styles have to be temporary, so they follow color changes!
    public static void addColoredText(String name, String message, ChatStyles chatStyle, StyledDocument doc, javax.swing.JScrollPane scrl_ChatOutput, javax.swing.JTextPane tp_ChatOutput) {

        synchronized (doc) {

            //setting up new styles
            setupStyles(name, chatStyle, doc);

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

            boolean doHighlight = Globals.isHighlighted(name);
            Boolean isFriend = Globals.getContactList().contains(name);
            String tempname = new String(name);

            //printing
            if (chatStyle == ChatStyles.WHISPER_NOTIFICATION) {
                tempname += " whispers";
            }

            if (message.startsWith("/me")) {
                tempname = "  **" + name;
                message = message.substring(3);
            } else if (name.length() > 0) {
                tempname = name + ": ";
            } else {
                tempname = "    ";
            }

            if (tempname.length() != 0 && coopnetclient.utils.Settings.getTimeStampEnabled() && chatStyle != ChatStyles.SYSTEM) {
                Date date = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
                tempname = "(" + dateformat.format(date) + ") " + tempname;
            }

            try {
                tp_ChatOutput.setCaretPosition(tp_ChatOutput.getDocument().getLength());
                if (doHighlight) {
                    doc.insertString(doc.getLength(), "\n" + tempname, doc.getStyle(isFriend ? highlightedFriendNameStyleName : highlightedNameStyleName));
                } else {
                    doc.insertString(doc.getLength(), "\n" + tempname, doc.getStyle(isFriend ? friendNameStyleName : nameStyleName));
                }
                //setup attributes
                SimpleAttributeSet nameAttributes = new SimpleAttributeSet();
                nameAttributes.addAttribute(HTML.Attribute.TYPE, "userName");
                nameAttributes.addAttribute(HTML.Attribute.DATA, name);
                nameAttributes.addAttribute(HTML.Attribute.CLASS, chatStyle);
                nameAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                doc.setCharacterAttributes(doc.getLength() - tempname.length(), tempname.length(), nameAttributes, false);

                //print each word
                String[] messageWords = message.split(" ");
                for (String word : messageWords) {
                    //links
                    if (word.startsWith("http://") || word.startsWith("room://") || word.startsWith("voice://")) {
                        //print link
                        String href = word;
                        SimpleAttributeSet hrefAttributes = new SimpleAttributeSet();
                        hrefAttributes.addAttribute(HTML.Attribute.HREF, href);
                        hrefAttributes.addAttribute(HTML.Attribute.TYPE, "href");
                        hrefAttributes.addAttribute(HTML.Attribute.CLASS, chatStyle);
                        try {
                            if (doHighlight) {
                                doc.insertString(doc.getLength(), href, doc.getStyle(highlightedhlinkStyleName));
                            } else {
                                doc.insertString(doc.getLength(), href, doc.getStyle(hlinkStyleName));
                            }
                            doc.setCharacterAttributes(doc.getLength() - href.length(), href.length(), hrefAttributes, false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    //hrefAttributes.removeAttributes(hrefAttributes.getAttributeNames());
                    } else { //print normal text
                        if (doHighlight) {
                            doc.insertString(doc.getLength(), word, doc.getStyle(isFriend ? highlightedFriendMessageStyleName : highlightedMessageStyleName));
                        } else {
                            doc.insertString(doc.getLength(), word, doc.getStyle(isFriend ? friendMessageStyleName : messageStyleName));
                        }
                        SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                        messageAttributes.addAttribute(HTML.Attribute.TYPE, "message");
                        messageAttributes.addAttribute(HTML.Attribute.CLASS, chatStyle);
                        messageAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                        doc.setCharacterAttributes(doc.getLength() - word.length(), word.length(), messageAttributes, false);
                    }
                    //add a whitespace after words (important after a link at the end of line)
                    if (doHighlight) {
                        doc.insertString(doc.getLength(), " ", doc.getStyle(isFriend ? highlightedFriendMessageStyleName : highlightedMessageStyleName));
                    } else {
                        doc.insertString(doc.getLength(), " ", doc.getStyle(isFriend ? friendMessageStyleName : messageStyleName));
                    }
                    SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                    messageAttributes.addAttribute(HTML.Attribute.TYPE, "message");
                    messageAttributes.addAttribute(HTML.Attribute.CLASS, chatStyle);
                    messageAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                    doc.setCharacterAttributes(doc.getLength() - 1, 1, messageAttributes, false);
                }

            } catch (BadLocationException ex) {//won't happen
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
        }
    }

    private static void setupStyles(String name, ChatStyles chatStyle, StyledDocument doc) {

        //removing the old styles
        doc.removeStyle(nameStyleName);
        doc.removeStyle(messageStyleName);
        doc.removeStyle(hlinkStyleName);
        doc.removeStyle(highlightedNameStyleName);
        doc.removeStyle(highlightedMessageStyleName);
        doc.removeStyle(friendNameStyleName);
        doc.removeStyle(highlightedFriendNameStyleName);
        doc.removeStyle(friendMessageStyleName);
        doc.removeStyle(highlightedFriendMessageStyleName);
        doc.removeStyle(highlightedhlinkStyleName);

        //Setting style and colors
        Style nameStyle = doc.addStyle(nameStyleName, null);
        Style messageStyle = doc.addStyle(messageStyleName, null);
        Style hlinkStyle = doc.addStyle(hlinkStyleName, null);
        Style hl_nameStyle = doc.addStyle(highlightedNameStyleName, nameStyle);
        Style hl_messageStyle = doc.addStyle(highlightedMessageStyleName, messageStyle);
        Style hl_hlinkStyle = doc.addStyle(highlightedhlinkStyleName, hlinkStyle);
        Style friendNameStyle = doc.addStyle(friendNameStyleName, nameStyle);
        Style friendMessageStyle = doc.addStyle(friendMessageStyleName, messageStyle);
        Style hl_friendNameStyle = doc.addStyle(highlightedFriendNameStyleName, friendNameStyle);
        Style hl_friendMessageStyle = doc.addStyle(highlightedFriendMessageStyleName, friendMessageStyle);

        //highlight bg colors
        if (Settings.getColorizeBody()) {
            StyleConstants.setBackground(hl_nameStyle, coopnetclient.utils.Settings.getSelectionColor().darker());
            StyleConstants.setBackground(hl_messageStyle, coopnetclient.utils.Settings.getSelectionColor().darker());
            StyleConstants.setBackground(hl_hlinkStyle, coopnetclient.utils.Settings.getSelectionColor().darker());
            StyleConstants.setBackground(hl_friendNameStyle, coopnetclient.utils.Settings.getSelectionColor().darker());
            StyleConstants.setBackground(hl_friendMessageStyle, coopnetclient.utils.Settings.getSelectionColor().darker());
        } else {
            Color clr = null;
            clr = (Color) UIManager.get("List.selectionBackground");
            if (clr == null) {
                clr = (Color) UIManager.get("List[Selected].textBackground");
            }
            StyleConstants.setBackground(hl_nameStyle, clr);
            StyleConstants.setBackground(hl_messageStyle, clr);
            StyleConstants.setBackground(hl_hlinkStyle, clr);
            StyleConstants.setBackground(hl_friendNameStyle, clr);
            StyleConstants.setBackground(hl_friendMessageStyle, clr);
        }
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
                    if (Globals.getThisPlayer_loginName().equals(name)) {
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

                StyleConstants.setForeground(friendNameStyle, coopnetclient.utils.Settings.getFriendUsernameColor());
                StyleConstants.setForeground(friendMessageStyle, coopnetclient.utils.Settings.getFriendMessageColor());

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
    }

    public static void updateHighLight(StyledDocument doc) {
        synchronized (doc) {
            int lastElementStart = 1;//inclusive start
            int lastElementEnd = 1;
            int currentPosition = 1;
            String lastTypeValue = "userName";
            String lastUserName = "";

            DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;

            for (; currentPosition < doc.getLength(); ++currentPosition) {

                Element el = hdoc.getCharacterElement(currentPosition);
                AttributeSet a = el.getAttributes();
                String elementType = (String) a.getAttribute(HTML.Attribute.TYPE);
                if (elementType != null && !lastTypeValue.equals(elementType)) {//type boundary found
                    lastElementEnd = currentPosition; //exclusive end
                    // apply style
                    String name = (String) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.DATA);
                    ChatStyles styleType = (ChatStyles) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.CLASS);
                    Boolean isFriend = (Boolean) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.COMMENT);
                    if (isFriend == null) {
                        isFriend = false;
                    }
                    setupStyles(name, styleType, doc);
                    if (lastTypeValue.equals("userName")) {//name
                        lastUserName = name;
                        if (Globals.isHighlighted(name)) {
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? highlightedFriendNameStyleName : highlightedNameStyleName), false);
                        } else {
                            //must override old attributes, readd additional data
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? friendNameStyleName : nameStyleName), true);
                            SimpleAttributeSet nameAttributes = new SimpleAttributeSet();
                            nameAttributes.addAttribute(HTML.Attribute.TYPE, "userName");
                            nameAttributes.addAttribute(HTML.Attribute.DATA, name);
                            nameAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                            nameAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, nameAttributes, false);
                        }
                    } else if (lastTypeValue.equals("href")) {
                        String href = (String) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.HREF);
                        if (Globals.isHighlighted(lastUserName)) {
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(highlightedhlinkStyleName), false);
                        } else {
                            //must override old attributes, readd additional data
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(hlinkStyleName), true);
                            SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                            messageAttributes.addAttribute(HTML.Attribute.TYPE, "href");
                            messageAttributes.addAttribute(HTML.Attribute.HREF, href);
                            messageAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, messageAttributes, false);
                        }
                    } else {//message
                        if (Globals.isHighlighted(lastUserName)) {
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? highlightedFriendMessageStyleName : highlightedMessageStyleName), false);
                        } else {
                            //must override old attributes, readd additional data
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? friendMessageStyleName : messageStyleName), true);
                            SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                            messageAttributes.addAttribute(HTML.Attribute.TYPE, "message");
                            messageAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                            messageAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                            doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, messageAttributes, false);
                        }
                    }
                    lastTypeValue = elementType;
                    lastElementStart = currentPosition;
                }
            }
            //apply style to last element
            lastElementEnd = doc.getLength();
            String name = (String) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.DATA);
            ChatStyles styleType = (ChatStyles) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.CLASS);
            Boolean isFriend = (Boolean) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.COMMENT);
            if (isFriend == null) {
                isFriend = false;
            }
            setupStyles(name, styleType, doc);
            if (lastTypeValue.equals("userName")) {//name
                lastUserName = name;
                if (Globals.isHighlighted(name)) {
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? highlightedFriendNameStyleName : highlightedNameStyleName), false);
                } else {
                    //must override old attributes, readd additional data
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? friendNameStyleName : nameStyleName), true);
                    SimpleAttributeSet nameAttributes = new SimpleAttributeSet();
                    nameAttributes.addAttribute(HTML.Attribute.TYPE, "userName");
                    nameAttributes.addAttribute(HTML.Attribute.DATA, name);
                    nameAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                    nameAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, nameAttributes, false);
                }
            } else if (lastTypeValue.equals("href")) {
                String href = (String) hdoc.getCharacterElement(lastElementStart).getAttributes().getAttribute(HTML.Attribute.HREF);
                if (Globals.isHighlighted(lastUserName)) {
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(highlightedhlinkStyleName), false);
                } else {
                    //must override old attributes, readd additional data
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(hlinkStyleName), true);
                    SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                    messageAttributes.addAttribute(HTML.Attribute.TYPE, "href");
                    messageAttributes.addAttribute(HTML.Attribute.HREF, href);
                    messageAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, messageAttributes, false);
                }
            } else {//message
                if (Globals.isHighlighted(lastUserName)) {
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? highlightedFriendMessageStyleName : highlightedMessageStyleName), false);
                } else {
                    //must override old attributes, readd additional data
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, doc.getStyle(isFriend ? friendMessageStyleName : messageStyleName), true);
                    SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
                    messageAttributes.addAttribute(HTML.Attribute.TYPE, "message");
                    messageAttributes.addAttribute(HTML.Attribute.CLASS, styleType);
                    messageAttributes.addAttribute(HTML.Attribute.COMMENT, isFriend);
                    doc.setCharacterAttributes(lastElementStart, lastElementEnd - lastElementStart, messageAttributes, false);
                }
            }
        }
    }
}
