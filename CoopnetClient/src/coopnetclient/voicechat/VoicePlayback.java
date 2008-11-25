package coopnetclient.voicechat;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.models.VoiceChatChannelListModel;
import coopnetclient.utils.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.sound.sampled.*;
import javax.sound.sampled.Port.Info;

public class VoicePlayback {

    public static final int MAXEXPIRE = 3;
    public static final int LOW_QUALITY = 1;
    public static final int MEDIUM_QUALITY = 2;
    public static final int HIGH_QUALITY = 3;
    public static int QUALITY_LEVEL = 1;
    private static AudioFormat voiceformat = null;
    private static HashMap<String, SourceDataLine> channels;
    private static DataLine.Info playbackInfo;
    private static Thread recorderThread;
    private static TargetDataLine micInputLine;
    private static DataLine.Info captureInfo;
    private static boolean captureAndSend = false;
    private static boolean recordLoop = false;
    private static boolean isTalking = false;
    private static Mixer playbackMixer = null;
    public static int expire = 0;
    private static final Object Lock = new Object();


    static {
        channels = new HashMap<String, SourceDataLine>();
    }
    private static String RECORD_PORT_SELECT;

    public static int indexOfAudioDevice(Mixer.Info info) {
        return Arrays.asList(AudioSystem.getMixerInfo()).indexOf(info);
    }

    public static Mixer.Info[] getUsablePlayBackDevices() {
        ArrayList<Mixer.Info> usabledevices = new ArrayList<Mixer.Info>();
        playbackInfo = new DataLine.Info(SourceDataLine.class, voiceformat);
        Mixer.Info minfo[] = AudioSystem.getMixerInfo();
        for (int i = 0; i < minfo.length; ++i) {
            Mixer mixer = AudioSystem.getMixer(minfo[i]);
            if (mixer.isLineSupported(playbackInfo)) {
                usabledevices.add(minfo[i]);
            }
        }
        return usabledevices.toArray(new Mixer.Info[usabledevices.size()]);
    }

    public static Mixer.Info[] getUsableCaptureDevices() {
        ArrayList<Mixer.Info> usabledevices = new ArrayList<Mixer.Info>();
        Mixer.Info minfo[] = AudioSystem.getMixerInfo();
        for (int i = 0; i < minfo.length; ++i) {
            Port.Info pinfo[] = getAudioPorts(minfo[i]);
            if (pinfo.length > 0) {
                usabledevices.add(minfo[i]);
            }
        }
        return usabledevices.toArray(new Mixer.Info[usabledevices.size()]);
    }

    public static void autoDetect() {
        playbackInfo = new DataLine.Info(SourceDataLine.class, voiceformat);
        if (Settings.getPlaybackDeviceIndex() < 0) {
            Mixer.Info minfo[] = AudioSystem.getMixerInfo();
            for (int i = 0; i < minfo.length; ++i) {
                Mixer mixer = AudioSystem.getMixer(minfo[i]);
                if (mixer.isLineSupported(playbackInfo)) {
                    Settings.setPlaybackDeviceIndex(i);
                    break;
                }
            }
        }

        if (Settings.getCaptureDeviceIndex() < 0 || Settings.getCapturePortIndex() < 0) {
            Mixer.Info minfo[] = AudioSystem.getMixerInfo();
            for (int i = 0; i < minfo.length; ++i) {
                Port.Info pinfo[] = getAudioPorts(minfo[i]);
                if (pinfo.length > 0) {
                    Settings.setCaptureDeviceIndex(i);
                    for (int j = 0; j < pinfo.length; ++j) {
                        if (pinfo[j].getName().contains("MICROPHONE") || pinfo[j].getName().contains("microphone")) {
                            Settings.setCapturePortIndex(j);
                            break;
                        }
                    }
                    if (Settings.getCapturePortIndex() < 0) {//no mic found by name
                        Settings.setCapturePortIndex(0);
                    }
                    break;
                }
            }
        }
    }

    public static void init() {
        autoDetect();
        playbackMixer = null;

        recordLoop = false;
        captureAndSend = false;

        QUALITY_LEVEL = LOW_QUALITY;

        voiceformat = getAudioFormat();
        channels = new HashMap<String, SourceDataLine>();
        playbackInfo = new DataLine.Info(SourceDataLine.class, voiceformat);
        captureInfo = new DataLine.Info(TargetDataLine.class, voiceformat);

        if (Settings.getCaptureDeviceIndex() == -1) {
            RECORD_PORT_SELECT = "MICROPHONE";
        } else {
            try {
                RECORD_PORT_SELECT = getAudioPorts(AudioSystem.getMixerInfo()[Settings.getCaptureDeviceIndex()])[Settings.getCapturePortIndex()].getName();
            } catch (Exception e) {
                System.out.println("can't select port");
            }
        }

        try {
            playbackMixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[Settings.getPlaybackDeviceIndex()]);
            if (playbackMixer == null || !playbackMixer.isLineSupported(playbackInfo)) {
                Globals.getClientFrame().printToVisibleChatbox("System", "Error initialising voice playback! Please edit your settings!", ChatStyles.SYSTEM, false);
            }
        } catch (Exception e) {
            Globals.getClientFrame().printToVisibleChatbox("System", "Error initialising voice playback! Please edit your settings!", ChatStyles.SYSTEM, false);
        }

        try {
            micInputLine = (TargetDataLine) AudioSystem.getLine(captureInfo);
            micInputLine.open();
            adjustRecordingVolume();
        } catch (Exception e) {
            e.printStackTrace();
            Globals.getClientFrame().printToVisibleChatbox("System", "Error initialising voice capture! Please edit your settings!", ChatStyles.SYSTEM, false);
        }
        recordLoop = true;
        recorderThread = new Thread() {

            @Override
            public void run() {
                if (micInputLine == null) {
                    return;
                }
                byte[] buffer = new byte[micInputLine.getBufferSize() / 3];
                int read = 0;
                micInputLine.start();
                while (recordLoop) {
                    read = micInputLine.read(buffer, 0, buffer.length);
                    //System.out.println(read);
                    if (Settings.isVoiceActivated()) {
                        processVoiceActivated(buffer, read);
                    } else {
                        processPushToTalk(buffer, read);
                    }
                }
                micInputLine.stop();
            }
        };
        if (recorderThread != null) {
            recorderThread.start();
        } else {
            recordLoop = false;
        }
        System.out.println("Audio System initialised");
    }

    private static void processVoiceActivated(byte[] buffer, int read) {
        if (read > 0) {
            int max = 0;
            if (Settings.isVoiceActivated()) {
                for (int i = 0; i < read; ++i) {
                    int current = Math.abs(buffer[i]);
                    max = (current > max) ? current : max;
                }
            }
            if (max >= Settings.getVoiceSensitivity()) {//send data
                if (isTalking) {
                    byte[] output = new byte[read];
                    System.arraycopy(buffer, 0, output, 0, read);
                    VoiceClient.sendVoiceData(output);
                } else {//wasnt talking yet
                    isTalking = true;
                    VoiceClient.send("STV");
                    byte[] output = new byte[read];
                    System.arraycopy(buffer, 0, output, 0, read);
                    VoiceClient.sendVoiceData(output);
                }
            } else { //nothing sendable read, stop sending
                if (isTalking) {
                    if (expire >= MAXEXPIRE) {
                        VoiceClient.send("SPV");
                        isTalking = false;
                        expire = 0;
                    } else {
                        expire++;
                        byte[] output = new byte[read];
                        System.arraycopy(buffer, 0, output, 0, read);
                        VoiceClient.sendVoiceData(output);
                    }
                }
            }
        } else {
            if (isTalking) {
                VoiceClient.send("SPV");
                isTalking = false;
            } else {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                }
            }
        }
    }

    private static void processPushToTalk(byte[] buffer, int read) {
        if (captureAndSend) {//send data
            if (isTalking) {
                byte[] output = new byte[read];
                System.arraycopy(buffer, 0, output, 0, read);
                VoiceClient.sendVoiceData(output);
            } else {//wasnt talking yet
                isTalking = true;
                VoiceClient.send("STV");
                byte[] output = new byte[read];
                System.arraycopy(buffer, 0, output, 0, read);
                VoiceClient.sendVoiceData(output);
            }
            synchronized(Lock){
                expire++;
            }
            if (expire >= MAXEXPIRE +2) {
                    isTalking = false;
                    captureAndSend = false;
                    expire = 0;
                    VoiceClient.send("SPV");
                }
        }
    }

    public static void pushToTalk() {
        synchronized(Lock){
            if (!captureAndSend) {
                captureAndSend = true;
            }
            expire = 0;
        }
    }

    public static void shutDown() {
        recordLoop = false;
        captureAndSend = false;
        if (micInputLine != null) {
            micInputLine.close();
        }
        recorderThread = null;
        for (SourceDataLine c : channels.values()) {
            c.close();
        }
        System.out.println("Audio System was shut down");
    }

    private static AudioFormat getAudioFormat() {
        AudioFormat format = null;
        switch (QUALITY_LEVEL) {
            case LOW_QUALITY:
                format = new AudioFormat(
                        8000.0F,
                        8,
                        1, true, false);
                break;

            case MEDIUM_QUALITY:
                format = new AudioFormat(
                        11025.0F,
                        16,
                        1, true, false);
                break;

            case HIGH_QUALITY:
                format = new AudioFormat(
                        22050.0F,
                        16,
                        1, true, false);
                break;
        }
        return format;
    }//end getAudioFormat

    public static void openChannel(String playerName) {
        try {
            SourceDataLine channel = null;
            if (playbackMixer == null) {
                channel = (SourceDataLine) AudioSystem.getLine(playbackInfo);
            } else {
                channel = (SourceDataLine) playbackMixer.getLine(playbackInfo);
            }
            if (channel == null) {
                return;
            }
            channel.open(voiceformat);
            channel.flush();
            channels.put(playerName, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeChannel(String playerName) {
        try {
            SourceDataLine channel = channels.get(playerName);
            if (channel != null) {
                channels.remove(playerName);
                channel.drain();
                channel.stop();
                channel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanUp() {
        for (String name : channels.keySet()) {
            SourceDataLine channel = channels.get(name);
            channel.stop();
            channel.close();
        }
        channels.clear();
    }

    public static void putDataInChannel(byte[] data, String playerName) {
        SourceDataLine channel = channels.get(playerName);
        VoiceChatChannelListModel model = Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel();
        if (channel != null) {
            if (!model.isMuted(playerName)) {
                model.setTalking(playerName);
                channel.write(data, 0, data.length);
                channel.start();
            }
        } else {
            openChannel(playerName);
            channel = channels.get(playerName);
            if (!model.isMuted(playerName)) {
                model.setTalking(playerName);
                channel.write(data, 0, data.length);
                channel.start();
            }
        }
    }

    public static void startRecording() {
        VoiceClient.send("STV");
        if (micInputLine != null) {
            micInputLine.flush();
            micInputLine.start();
        }
        captureAndSend = true;
    }

    public static void stopRecording() {
        captureAndSend = false;
        if (micInputLine != null) {
            micInputLine.stop();
            micInputLine.drain();
        }
    }

    public static Port.Info[] getAudioPorts(Mixer.Info deviceInfo) {
        ArrayList<Port.Info> ports = new ArrayList<Port.Info>();
        try {
            {
                Mixer mixer = AudioSystem.getMixer(deviceInfo);
                {
                    ArrayList<Line.Info> srcInfos = new ArrayList<Line.Info>(
                            Arrays.asList(
                            mixer.getSourceLineInfo()));
                    for (Line.Info srcInfo : srcInfos) {
                        //Port.Info pi = (Port.Info) srcInfo;
                        //ports.add(pi);
                        if (srcInfo instanceof Port.Info) {
                            ports.add((Info) srcInfo);
                        }
                    } // of for Line.Info                
                } // of if
            // (mixer.isLineSupported)
            } // of for (Mixer.Info)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ports.toArray(new Port.Info[]{});
    }

    public static void adjustRecordingVolume()
            throws Exception {
        Port.Info recPortInfo =
                new Port.Info(Port.class,
                RECORD_PORT_SELECT, true);
        if (recPortInfo == null) {
            return;
        }
        Port recPort = (Port) AudioSystem.getLine(
                recPortInfo);
        if (recPort == null) {
            return;
        }
        setRecControlValue(recPort);
    }

    private static void setRecControlValue(Port inPort)
            throws Exception {
        inPort.open();
        Control[] controls =
                inPort.getControls();
        for (int i = 0; i < controls.length; i++) {
            if (controls[i] instanceof CompoundControl) {
                Control[] members =
                        ((CompoundControl) controls[i]).getMemberControls();
                for (int j = 0; j < members.length; j++) {
                    setCtrl(members[j]);
                } // for int j
            } // if
            else {
                setCtrl(controls[i]);
            }
        } // for i
    //inPort.close();
    }

    private static void setCtrl(
            Control ctl) {
        if (ctl.getType().toString().equals("Select")) {
            ((BooleanControl) ctl).setValue(true);
        }
    /*if (ctl.getType().toString().equals("Volume")) {
    FloatControl vol =
    (FloatControl) ctl;
    float setVal = vol.getMinimum() + (vol.getMaximum() - vol.getMinimum()) * RECORD_VOLUME_LEVEL;
    vol.setValue(setVal);
    }*/
    }
}
