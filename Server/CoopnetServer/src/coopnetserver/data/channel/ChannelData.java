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
package coopnetserver.data.channel;

import coopnetserver.data.player.Player;
import java.util.HashMap;

public class ChannelData {

    private static HashMap<String, Channel> channels = new HashMap<String, Channel>();

    public static HashMap<String, Channel> getChannels(){
        return channels;
    }
    
    public static Channel getChannel(String ID) {
        return channels.get(ID);
    }

    public static void addChannel(Channel channel) {
        channels.put(channel.ID, channel);
    }

    public static void removePlayerFromAllChannels(Player player) {
        for (Channel ch : channels.values().toArray(new Channel[channels.size()])) {
            ch.removePlayer(player);
        }
    }
/*
    public static void load() {
        String input;
        boolean reading = true;
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/channellist"));
            //channels.clear();
            while (reading) {
                input = br.readLine();
                if (input != null) {
                    //load each line
                    String tmp[] = input.split(";");
                    if (getChannel(tmp[1]) == null) {
                        addChannel(new Channel(tmp[0], tmp[1]));
                    }
                } else {
                    reading = false;
                }
            }
            Logger.log(LogTypes.LOG, "Channel-list loaded");
        } catch (Exception e) {
            Logger.log(LogTypes.ERROR, "Channel-list not loaded");
            Logger.log(e);
        }
    }
*/
    public static void removeChannel(String channel) {
        Channel ch = channels.get(channel);
        channels.remove(ch.ID);
    }
}
