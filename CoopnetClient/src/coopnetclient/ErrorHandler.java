/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient;

import java.io.IOException;

public class ErrorHandler {

    public static void handleException(Exception exc) {
        if (exc == null) {
            return;
        }
        if (exc.getMessage() == null) {
            return;
        }
        //Print at least a CATCH notification
        System.err.println("CATCH: " + exc.getMessage());
        if (exc instanceof IOException) {
            if (exc.getMessage().equals("socket closed")) {
                return; // do nothing, happens when disconnecting
            }

            if (exc.getMessage().contains("Connection refused") || exc.getMessage().contains("timed out")) {
                Client.mainFrame.addErrorTab(coopnetclient.panels.ErrorPanel.CONNECTION_REFUSED_MODE, exc);
            } else if (exc.getMessage().equals("Connection reset")) {
                Client.mainFrame.addErrorTab(coopnetclient.panels.ErrorPanel.CONNECTION_RESET_MODE, exc);
            } else {
                Client.mainFrame.addErrorTab(coopnetclient.panels.ErrorPanel.UNKNOWN_IO_MODE, exc);
                exc.printStackTrace();
            }
        } else { // regular errors
            Client.mainFrame.addErrorTab(coopnetclient.panels.ErrorPanel.UNKNOWN_MODE, exc);
            //Here we really want a stacktrace
            exc.printStackTrace();
        }

    }
}
