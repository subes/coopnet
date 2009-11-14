package coopnetserver.data.channel;

import coopnetserver.data.player.Player;
import coopnetserver.enums.LogTypes;
import coopnetserver.protocol.out.Protocol;
import coopnetserver.utils.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
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
        for (Channel ch : channels.values()) {
            ch.removePlayer(player);
        }
    }

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

    public static void removeChannel(String channel) {
        Channel ch = channels.get(channel);
        channels.remove(ch);
    }
}
