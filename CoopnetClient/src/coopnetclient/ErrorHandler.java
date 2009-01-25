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
import java.net.SocketTimeoutException;

public class ErrorHandler {

    public static void handleException(Throwable exc) {
        if (exc == null) {
            return;
        }
        
        //From now on we want to log any exception!
        Logger.log(exc);
        if(exc instanceof java.nio.channels.AsynchronousCloseException){
            return;
        }
        
        if ( exc instanceof java.nio.channels.ClosedChannelException ){
            TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_RESET, exc);
        }else
        if (exc instanceof IOException) {
            if (exc.getMessage() != null && exc.getMessage().equals("socket closed")) {
                return; // do nothing, happens when disconnecting
            }

            if (       exc.getMessage() != null &&  exc.getMessage().contains("Connection refused")
                    || exc.getMessage() != null &&  exc.getMessage().contains("timed out")
                    || exc instanceof SocketTimeoutException) {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_REFUSED, exc);
            } else
            if (       exc.getMessage() != null && exc.getMessage().contains("Connection reset")
                    || exc.getMessage() != null && exc.getMessage().contains("Connection lost")
                    || exc.getMessage() != null && exc.getMessage().contains("forcibly closed by the remote host") ) {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.CONNECTION_RESET, exc);
            } else {
                TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN_IO, exc);
            }
        } else { // regular errors
            TabOrganizer.openErrorPanel(ErrorPanelStyle.UNKNOWN, exc);
        }
    }
}
