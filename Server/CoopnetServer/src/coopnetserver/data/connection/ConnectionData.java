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
package coopnetserver.data.connection;

import coopnetserver.NioServer;
import coopnetserver.enums.LogTypes;
import coopnetserver.utils.Logger;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionData {

    private static int currentIndex = 0;
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private static HashMap<SelectionKey, Connection> keys_connections = new HashMap<SelectionKey, Connection>();

    public static synchronized Connection getNext() {
        synchronized (connections) {
            if (currentIndex >= connections.size()) {
                //Last index reached, start over again
                currentIndex = 0;
                return null;
            } else {
                Connection con = connections.get(currentIndex);
                currentIndex++;
                return con;
            }
        }
    }

    public static Connection createNewConnection(SelectionKey key) {
        Connection con = new Connection(key);
        synchronized (connections) {            
            connections.add(con);
            keys_connections.put(key, con);
        }
        Logger.log(LogTypes.CONNECTION, "New connection detected!", con);
        return con;
    }

    public static synchronized Connection getConnection(SelectionKey key) {
        synchronized (connections) {
            Connection con = keys_connections.get(key);

            return con;
        }
    }

    public static synchronized void removeConnection(Connection con) {
        synchronized (connections) {
            if (connections.remove(con)) {
                keys_connections.remove(con.getSelectionKey());
                Logger.log(LogTypes.CONNECTION, "Closed connection detected!", con);
            } else {
                Logger.log(LogTypes.ERROR, "A non registered connection was asked to be removed!", con);
            }
        }

        if(con.getSelectionKey() != null){
            removeConnection(con.getSelectionKey());
            con.setSelectionKey(null);
        }
    }

    public static synchronized void removeConnection(SelectionKey key) {
        Connection con = getConnection(key);

        if(con != null){
            removeConnection(con);
            return;
        }else{
            key.cancel(); //unregister from server
            try {
                synchronized(key){
                    key.channel().close();
                }
            } catch (Exception e) {}
        }
    }
}
