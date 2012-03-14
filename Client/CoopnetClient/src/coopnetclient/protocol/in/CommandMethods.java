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

package coopnetclient.protocol.in;

import coopnetclient.Client;
import coopnetclient.enums.ErrorPanelStyle;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframetabs.TabOrganizer;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Verification;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class CommandMethods {
    protected static void checkProtocolVersion(String version){
        if(!Verification.verifyCompatibilityVersion(version)){
            Logger.log(LogTypes.LOG, "Protocol version mismatch detected!");
            Client.disconnect();
            TabOrganizer.openErrorPanel(ErrorPanelStyle.PROTOCOL_VERSION_MISMATCH, null);
        }
    }
    
    public static void testConnection(final String[] info) {
        //info[0]: IP 
        //the rest: port numbers to connect to
        new Thread() {
            public void run() {
                String IP = info[0];
                for (int i = 1; i < info.length; i++) {
                    try {
                        SocketChannel socket = SocketChannel.open();
                        socket.configureBlocking(true);
                        Logger.log("Connectiontest connecting to: " + info[i]);
                        socket.connect(new InetSocketAddress(IP, new Integer(info[i])));
                        Logger.log("Connectiontest successull on: " + info[i]);
                        socket.close();
                    } catch (IOException exception) {
                        //ignore
                        exception.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }
}
