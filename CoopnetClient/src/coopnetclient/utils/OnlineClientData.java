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
package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.listeners.HyperlinkMouseListener;
import coopnetclient.threads.ErrThread;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.settings.Settings;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.JOptionPane;

public final class OnlineClientData {

    public static final String IP_PORT_SEPARATOR = ":";
    private static final String ONLINE_CLIENT_DATA =
            "http://coopnet.svn.sourceforge.net/viewvc/coopnet/trunk/misc/OnlineClientData/";
    private static final String WIKI_URL =
            "http://sourceforge.net/apps/mediawiki/coopnet/index.php?title=";

    private OnlineClientData() {
    }

    private static String extractFirstLine(String urlToFile) {
        try {
            URL url = new URL(urlToFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.
                    openStream()));
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getLatestUpdaterUrl() {
        return extractFirstLine(ONLINE_CLIENT_DATA + "LatestUpdater.txt");
    }

    public static String getClientVersion() throws Exception {
        return extractFirstLine(ONLINE_CLIENT_DATA + "LatestClientVersion.txt");
    }

    public static void readServerAddress() {
        String server = null;
        //Determine server IP and Port
        if (Globals.getServerIP() == null) {
            server = OnlineClientData.getCoopnetServerIP();
            Logger.log("Server address read: " + server);

            if (server != null) {
                String ip =
                        server.substring(0, server.indexOf(IP_PORT_SEPARATOR));
                Globals.setServerIP(ip);
                int port =
                        Integer.parseInt(server.substring(server.indexOf(IP_PORT_SEPARATOR) +
                        1));
                Globals.setServerPort(port);
                Settings.setLastValidServerIP(ip);
                Settings.setLastValidServerPort(port);
            } else {
                Globals.setServerIP(Settings.getLastValidServerIP());
                Globals.setServerPort(Settings.getLastValidServerPort());
            }
        }
    }

    private static String getCoopnetServerIP() {
        try {
            URL sourceforge =
                    new URL(ONLINE_CLIENT_DATA + "CoopnetServerAddress.txt");
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(sourceforge.
                    openStream()));
            StringBuilder temp = new StringBuilder();
            int c;
            while ((c = br.read()) != -1) {
                temp.append((char) c);
            }
            br.close();
            br = null;
            return temp.toString().trim();
        } catch (Exception e) {
            Logger.log(e);
        }
        return null;
    }

    private static String getFaqUrl() {
        return WIKI_URL + "User_FAQ";
    }

    public static void openFaq() {
        HyperlinkMouseListener.openURL(getFaqUrl());
    }

    private static String getBeginnersGuideUrl() {
        return WIKI_URL + "Beginner%27s_Guide";
    }

    public static void openBeginnersGuide() {
        HyperlinkMouseListener.openURL(getBeginnersGuideUrl());
    }

    public static void downloadLatestUpdater(String toFile) {
        FileDownloader.downloadFile(
                getLatestUpdaterUrl(),
                Globals.getResourceAsString(toFile));
    }

    public static void checkAndUpdateGameData() {
        new ErrThread() {

            @Override
            public void handledRun() throws Throwable {
                try {
                    URL url = new URL(ONLINE_CLIENT_DATA + "gamedata.xml");
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(url.
                            openStream()));
                    int lastversion = 0;
                    String readHeader1 = br.readLine();
                    String readHeader2 = br.readLine();
                    String[] parts = readHeader2.split(" ");
                    lastversion = new Integer(parts[1]);
                    GameDatabase.loadVersion();
                    if (GameDatabase.version < lastversion) {
                        Logger.log(LogTypes.LOG, "Downloading new gamedata");
                        BufferedOutputStream bo = null;
                        File destfile = new File(GameDatabase.dataFilePath);
                        if (!destfile.createNewFile()) {
                            destfile.delete();
                            destfile.createNewFile();
                        }
                        bo =
                                new BufferedOutputStream(new FileOutputStream(destfile));
                        //save version
                        bo.write((readHeader1 + "\n").getBytes());
                        bo.write((readHeader2 + "\n").getBytes());
                        //save the rest
                        int readedbyte;
                        while ((readedbyte = br.read()) != -1) {
                            bo.write(readedbyte);
                        }
                        bo.flush();
                        try {
                            br.close();
                            bo.close();
                        } catch (Exception ex) {
                            Logger.log(ex);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FrameOrganizer.getClientFrame(),
                            "You have an outdated version of the gamedata, but couldn't update it!",
                            "Gamedata outdated", JOptionPane.INFORMATION_MESSAGE);
                    throw e;
                } finally {
                    GameDatabase.loadVersion();
                    GameDatabase.load("", GameDatabase.dataFilePath);
                    GameDatabase.detectGames();
                }

            }
        }.start();
    }
}
