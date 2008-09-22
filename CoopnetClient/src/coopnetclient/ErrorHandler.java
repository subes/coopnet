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

package coopnetclient;

import coopnetclient.enums.ErrorPanelStyle;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Logger;
import java.io.IOException;

public class ErrorHandler {

    public static void handleException(Exception exc) {
        if (exc == null) {
            return;
        }
        
        //From now on we want to log any exception!
        Logger.log(exc);
        
        if(exc instanceof java.nio.channels.AsynchronousCloseException){
            return;
        }
        
        if (exc.getMessage() == null) {
            TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN, exc);
            return;
        }
        
        if (exc instanceof IOException) {
            if (exc.getMessage().equals("socket closed")) {
                return; // do nothing, happens when disconnecting
            }

            if (exc.getMessage().contains("Connection refused") || exc.getMessage().contains("timed out")) {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_REFUSED, exc);
            } else if ( exc instanceof java.nio.channels.ClosedChannelException || exc.getMessage().equals("Connection reset")) {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_RESET, exc);
            } else {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN_IO, exc);
            }
        } else { // regular errors
            TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN, exc);
        }

    }
}
