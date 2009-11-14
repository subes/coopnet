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

import coopnetserver.Task;
import coopnetserver.Task;
import coopnetserver.TaskProcesser;
import coopnetserver.TaskProcesser;
import coopnetserver.data.player.Player;
import coopnetserver.enums.TaskTypes;
import coopnetserver.protocol.out.Protocol;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Connection {

    private boolean nullKeyDetected = false;
    private long lastMessageTimeStamp;
    private SelectionKey key;
    private Player player;
    private String clientVersion = "?";

    protected Connection(SelectionKey key) {
        this.key = key;
        updateLastMessageTimeStamp();
    }

    public long getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void updateLastMessageTimeStamp() {
        lastMessageTimeStamp = System.currentTimeMillis();
    }

    public SelectionKey getSelectionKey() {
        if (key == null && !nullKeyDetected) {
            nullKeyDetected = true;
            TaskProcesser.addTask(new Task(TaskTypes.LOGOFF, this, new String[]{}));
        }
        return key;
    }

    public void setSelectionKey(SelectionKey key){
        this.key = key;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setClientVersion(String version){
        this.clientVersion = version;
        Protocol.sendProtocolVersion(this);
    }

    public String getClientVersion(){
        return clientVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Connection)) {
            return false;
        }
        Connection c = (Connection) o;
        return c.getSelectionKey().equals(key);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }

    public String getIpAddress() {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            return client.socket().getInetAddress().getHostAddress();
        } catch (Exception e) {
            return "?";
        }
    }
}
