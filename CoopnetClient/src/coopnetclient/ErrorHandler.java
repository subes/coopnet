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
package coopnetclient;

import coopnetclient.enums.ErrorPanelStyle;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.utils.Logger;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;

public final class ErrorHandler {

    private ErrorHandler() {
    }

    public static void handle(Throwable e) {
        if (e == null || FrameOrganizer.getClientFrame() == null) {
            //Shutting down, don't handle anything!
            return;
        }

        Logger.log(e);

        if (e instanceof IOException) {
            handleIOException((IOException) e);
        } else {
            handleRegularException(e);
        }
    }

    private static void handleIOException(IOException e) {

        if (checkMessage(e, "socket closed")) {
            // do nothing, happens when disconnecting
            return;
        } else if (e instanceof ClosedChannelException) {
            TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_RESET, e);
        } else if (checkMessage(e, "Connection refused") ||
                checkMessage(e, "timed out") ||
                e instanceof SocketTimeoutException) {
            TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_REFUSED, e);
        } else if (checkMessage(e, "Connection reset") ||
                checkMessage(e, "Connection lost") ||
                checkMessage(e, "forcibly closed by the remote host")) {
            TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_RESET, e);
        } else {
            TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN_IO, e);
        }
    }

    private static void handleRegularException(Throwable e) {
        TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN, e);
    }

    private static boolean checkMessage(Throwable e, String contains) {
        return e.getMessage() != null && e.getMessage().contains(contains);
    }
}
