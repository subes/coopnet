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

package coopnetclient;

import java.io.IOException;

public class ErrorHandler {

    public static void handleException(Exception exc) {
        if (exc == null) {
            return;
        }
        if (exc.getMessage() == null) {
            Client.clientFrame.addErrorTab(coopnetclient.frames.clientframe.ErrorPanel.UNKNOWN_MODE, exc);
            exc.printStackTrace();
            return;
        }
        //Print at least a CATCH notification
        System.err.println("CATCH: " + exc.getMessage());
        if (exc instanceof IOException) {
            if (exc.getMessage().equals("socket closed")) {
                return; // do nothing, happens when disconnecting
            }

            if (exc.getMessage().contains("Connection refused") || exc.getMessage().contains("timed out")) {
                Client.clientFrame.addErrorTab(coopnetclient.frames.clientframe.ErrorPanel.CONNECTION_REFUSED_MODE, exc);
            } else if (exc.getMessage().equals("Connection reset")) {
                Client.clientFrame.addErrorTab(coopnetclient.frames.clientframe.ErrorPanel.CONNECTION_RESET_MODE, exc);
            } else {
                Client.clientFrame.addErrorTab(coopnetclient.frames.clientframe.ErrorPanel.UNKNOWN_IO_MODE, exc);
                exc.printStackTrace();
            }
        } else { // regular errors
            Client.clientFrame.addErrorTab(coopnetclient.frames.clientframe.ErrorPanel.UNKNOWN_MODE, exc);
            //Here we really want a stacktrace
            exc.printStackTrace();
        }

    }
}
