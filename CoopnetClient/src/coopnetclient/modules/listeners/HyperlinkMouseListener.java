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

import coopnetclient.ErrorHandler;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.net.URI;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

public class HyperlinkMouseListener extends MouseAdapter {

    @Override
    public void mouseMoved(MouseEvent ev) {
    }

    /**
     * Called for a mouse click event.
     * If the component is read-only (ie a browser) then 
     * the clicked event is used to drive an attempt to
     * follow the reference specified by a link.
     *
     * @param e the mouse event
     * @see MouseListener#mouseClicked
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        JTextPane editor = (JTextPane) e.getSource();
        Point pt = new Point(e.getX(), e.getY());
        int pos = editor.viewToModel(pt);
        if (pos >= 0) {
            Document doc = editor.getDocument();
            if (pos == doc.getLength()) {
                return;
            }
            DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
            Element el = hdoc.getCharacterElement(pos);
            AttributeSet a = el.getAttributes();
            String href = (String) a.getAttribute(HTML.Attribute.HREF);
            if (href != null) {
                openURL(href);
            }
        }
    }

    public static void openURL(String address) {
        try {
        Desktop desktop= null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = null;
        
            uri = new URI(address);
            desktop.browse(uri);        
        }
        } catch(Exception e){
            ErrorHandler.handleException(e);
        }
    }
    
    //  Bare Bones Browser Launch 
    public static void openURL2(final String url) {
        new Thread() {

            @Override
            public void run() {
                String osName = System.getProperty("os.name");
                try {
                    if (osName.startsWith("Mac OS")) {
                        Class fileMgr = Class.forName("com.apple.eio.FileManager");
                        Method openURL = fileMgr.getDeclaredMethod("openURL",
                                new Class[]{String.class});
                        openURL.invoke(null, new Object[]{url});
                    } else if (osName.startsWith("Windows")) {
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                    } else { //assume Unix or Linux

                        String[] browsers = {
                            "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
                        };
                        String browser = null;
                        for (int count = 0; count < browsers.length && browser == null; count++) {
                            if (Runtime.getRuntime().exec(
                                    new String[]{"which", browsers[count]}).waitFor() == 0) {
                                browser = browsers[count];
                            }
                        }
                        if (browser == null) {
                            throw new Exception("Could not find web browser");
                        } else {
                            Runtime.getRuntime().exec(new String[]{browser, url});
                        }
                    }
                } catch (Exception e) {
                    ErrorHandler.handleException(e);
                }
            }
        }.start();
    }
}
