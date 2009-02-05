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
    private boolean disableAutoScroll = false;
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
