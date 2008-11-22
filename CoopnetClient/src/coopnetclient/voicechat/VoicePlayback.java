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

    public static final int MAXEXPIRE = 2;
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


    static {
        channels = new HashMap<String, SourceDataLine>();
    }
    private static String RECORD_PORT_SELECT;

    public static void init() {
        playbackMixer = null;

        recordLoop = false;
        captureAndSend = false;

        QUALITY_LEVEL = LOW_QUALITY;//TODO do we want choseable quality or jsut stick with low as it requires the least traffic
        System.out.println("Sound Quality:" + QUALITY_LEVEL);

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
        /*if (!AudioSystem.isLineSupported(playbackInfo)) {//no playback, report to user
        JOptionPane.showMessageDialog(Client.mainwindow,
        "Cannot find suitable sound device! Sound playback is not possible!",
        "Cannot find audio device", JOptionPane.ERROR_MESSAGE);
        }*/

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
                int expire = 0;
                micInputLine.start();
                while (recordLoop) {
                    read = micInputLine.read(buffer, 0, buffer.length);
                    //System.out.println(read);
                    if (read > 0) {
                        if (Settings.isVoiceActivated()) {
                            int sum = 0;
                            for (int i = 0; i < read; ++i) {
                                sum += Math.abs(buffer[i]);
                            }
                            int average = sum / read;
                            if (average >= Settings.getVoiceSensitivity()) {//send data
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
                        } else if (captureAndSend) {//push to talk
                            byte[] output = new byte[read];
                            System.arraycopy(buffer, 0, output, 0, read);
                            VoiceClient.sendVoiceData(output);
                            //micInputLine.flush();
                            VoiceClient.send("SPV");
                        } else {
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        if (Settings.isVoiceActivated() && isTalking) {
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

    public static void putDataInChannel(byte[] data, String playerName) {
        SourceDataLine channel = channels.get(playerName);
        VoiceChatChannelListModel model = Globals.getClientFrame().getQuickPanel().getVoiceChatPanel().getModel();
        if (channel != null && !model.isMuted(playerName)) {
            channel.write(data, 0, data.length);
            channel.start();
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
