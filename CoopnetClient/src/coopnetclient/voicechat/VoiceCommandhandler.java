package coopnetclient.voicechat;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.models.VoiceChatChannelListModel;
import coopnetclient.protocol.out.Protocol;
import java.nio.channels.SelectionKey;
import java.util.Vector;

public class VoiceCommandhandler {
    //executed by clients

    public static void execute2(final String command) {
        //System.out.println("VOICECLIENT:" + command);
        if (command.startsWith("locked")) {
            Globals.getClientFrame().printToVisibleChatbox("System", "The VoiceChat server is locked! Noone is permitted to connect or change channel!", ChatStyles.SYSTEM , false);
            return;
        }
        if (command.startsWith("SPV")) { //stop voice
            String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
            Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel().setNotTalking(tmp[1]);
            VoicePlayback.closeChannel(tmp[1]);
        } else if (command.startsWith("cc")) { //change channel
            VoiceChatChannelListModel model = Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel();
            String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
            model.moveUserToChannel(tmp[1], Integer.valueOf(tmp[2]));
        } else if (command.startsWith("STV")) { //start voice
            String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
            Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel().setTalking(tmp[1]);
            VoicePlayback.openChannel(tmp[1]);
        } else if (command.startsWith("addplayer")) {// addpalyer | name | channelidx
            VoiceChatChannelListModel model = Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel();
            String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
            model.addUserToChannel(tmp[1], Integer.valueOf(tmp[2]));
        } else if (command.startsWith("removeplayer")) { //remove player
            VoiceChatChannelListModel model = Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel();
            String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
            model.removeUser(tmp[1]);
        }
    }

    public VoiceCommandhandler() {
    }

    //used by server
    static void execute(SelectionKey key, String command) {
        //System.out.println("VOICESERVER:" + command);
        String thisplayer = VoiceServer.playerByKey(key);
        if (thisplayer == null) {
            if (command.startsWith("login")) {
                if (VoiceServer.isLocked) {
                    VoiceServer.sendToKey(key, new MixedMessage("locked"));
                    VoiceServer.logOff(key);
                    return;
                }
                String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
                VoiceServer.logIn(tmp[1], key);
            }
        } else {//logged in
            if (command.startsWith("cc")) { //change channel: cc | channelidx
                if (VoiceServer.isLocked) {
                    VoiceServer.sendToKey(key, new MixedMessage("locked"));
                    return;
                }
                String tmp[] = command.split(Protocol.MESSAGE_DELIMITER);
                for (Vector<SelectionKey> v : VoiceServer.playersInChannels) {
                    v.remove(key);
                }
                VoiceServer.playersInChannels.get(Integer.valueOf(tmp[1])).add(key);
                VoiceServer.sendtoall(new MixedMessage("cc" + Protocol.MESSAGE_DELIMITER + thisplayer + Protocol.MESSAGE_DELIMITER + tmp[1]));
            } else if (command.startsWith("STV")) { //start voice
                VoiceServer.sendtoall(new MixedMessage("STV" + Protocol.MESSAGE_DELIMITER + thisplayer));
            } else if (command.startsWith("SPV")) { //stop voice
                VoiceServer.sendtoall(new MixedMessage("SPV" + Protocol.MESSAGE_DELIMITER + thisplayer));
            }
        }
    }
}
