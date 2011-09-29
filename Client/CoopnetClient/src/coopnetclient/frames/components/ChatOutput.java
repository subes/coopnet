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
import coopnetclient.frames.popupmenus.ChatOutputPopupMenu;
import coopnetclient.utils.ui.ColoredChatHandler;
import coopnetclient.utils.ui.StyledChatMessage;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.ViewFactory;
import java.awt.Dimension;
import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;

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

        textPane.setEditorKit(new HTMLEditorKit() {

            @Override
            public ViewFactory getViewFactory() {

                return new HTMLFactory() {

                    @Override
                    public View create(Element e) {
                        View v = super.create(e);
                        if (v instanceof InlineView) {
                            return new InlineView(e) {

                                @Override
                                public int getBreakWeight(int axis, float pos, float len) {
                                    return GoodBreakWeight;
                                }

                                @Override
                                public View breakView(int axis, int p0, float pos, float len) {
                                    if (axis == View.X_AXIS) {
                                        checkPainter();
                                        int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                                        if (p0 == getStartOffset() && p1 == getEndOffset()) {
                                            return this;
                                        }
                                        return createFragment(p0, p1);
                                    }
                                    return this;
                                }
                            };
                        } else if (v instanceof ParagraphView) {
                            return new ParagraphView(e) {

                                @Override
                                protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
                                    if (r == null) {
                                        r = new SizeRequirements();
                                    }
                                    float pref = layoutPool.getPreferredSpan(axis);
                                    float min = layoutPool.getMinimumSpan(axis);
                                    // Don't include insets, Box.getXXXSpan will include them.
                                    r.minimum = (int) min;
                                    r.preferred = Math.max(r.minimum, (int) pref);
                                    r.maximum = Integer.MAX_VALUE;
                                    r.alignment = 0.5f;
                                    return r;
                                }
                            };
                        }
                        return v;
                    }
                };
            }
        });

        textPane.setContentType("text/html");
        this.setViewportView(textPane);
        this.setAutoscrolls(true);
        textPane.setEditable(false);
        textPane.setAutoscrolls(true);
        HyperlinkMouseListener hyperlinkMouseListener = new HyperlinkMouseListener();
        textPane.addMouseListener(hyperlinkMouseListener);
        textPane.addMouseMotionListener(hyperlinkMouseListener);

        messages = new ArrayList<StyledChatMessage>();
        //StyledDocument doc = textPane.getStyledDocument();
        //the next line prevents opening the forms that use this component
        //ColoredChatHandler.setupStyles(doc);
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void customCodeForPopupMenu() {
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
