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

import coopnetclient.Client;
import coopnetclient.ErrorHandler;
import coopnetclient.Globals;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.threads.ErrThread;
import javax.swing.JOptionPane;

public final class Updater {

    private Updater() {
    }

    public static void checkAndUpdate() {
        new ErrThread() {

            @Override
            public void handledRun() throws Throwable {
                setNonCritical();
                String version = OnlineClientData.getClientVersion();
                if (!Verification.verifyClientVersion(version)) {
                    FrameOrganizer.getClientFrame().enableUpdate();
                    update("Client update available",
                            "<html>There is a new version of CoopnetClient available.<br>" +
                            "Would you like to update now?<br>The client will close and update itself.");
                }
            }
        }.start();
    }

    public static void invokeUpdate() {
        new ErrThread() {

            @Override
            public void handledRun() throws Throwable {
                setNonCritical();
                update("Update Client", "<html>Would you like to update your CoopnetClient now?<br>" +
                        "The client will close and update itself.");
            }
        }.start();
    }

    private static void update(final String confirmTitle,
            final String confirmText) throws Throwable {
        int n = JOptionPane.showConfirmDialog(null,
                confirmText,
                confirmTitle, JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            OnlineClientData.downloadLatestUpdater("CoopnetUpdater.jar");
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("java -jar CoopnetUpdater.jar", null, Globals.
                    getCurrentDirectory());
            ProcessHelper.closeStreams(p);
            Client.quit(true);
        }

    }
}
