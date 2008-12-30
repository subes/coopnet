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

package coopnetclient.utils;

import coopnetclient.*;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.utils.launcher.Launcher;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;

public class SoundPlayer {
    
    private static int soundsPlaying = 0;
    private static boolean delayOtherSounds = false;
    private static DataLine.Info info;

    static {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(Globals.getResource("data/sounds/launch.wav"));
            info = new DataLine.Info(Clip.class, stream.getFormat());
        } catch (Exception e) {
            //failed to initialise
        }
    }

    public static int indexOfAudioDevice(Mixer.Info info) {
        return Arrays.asList(AudioSystem.getMixerInfo()).indexOf(info);
    }

    public static Mixer.Info[] getUsablePlayBackDevices() {
        ArrayList<Mixer.Info> usabledevices = new ArrayList<Mixer.Info>();
        Mixer.Info minfo[] = AudioSystem.getMixerInfo();
        for (int i = 0; i < minfo.length; ++i) {
            Mixer mixer = AudioSystem.getMixer(minfo[i]);
            if (mixer.isLineSupported(info)) {
                usabledevices.add(minfo[i]);
            }
        }
        return usabledevices.toArray(new Mixer.Info[usabledevices.size()]);
    }

    public static void autoDetect() {
        //TODO OS dependant default device selection
        if (Settings.getaudioPlaybackDeviceIndex() < 0) {
            Mixer.Info minfo[] = AudioSystem.getMixerInfo();
            for (int i = 0; i < minfo.length; ++i) {
                Mixer mixer = AudioSystem.getMixer(minfo[i]);
                if (mixer.isLineSupported(info)) {
                    Settings.setaudioPlaybackDeviceIndex(i);
                    break;
                }
            }
        }}

    //Threadsafe use of variable
    private synchronized static void updateSoundsPlaying(int additor){
        soundsPlaying+=additor;
    }
    
    
    public static void playNudgeSound() {
        playSoundFile(Globals.getResourceAsString("data/sounds/nudge.wav"), true);
    }

    public static void playLaunchSound() {
        if(Settings.getSoundEnabled() && Globals.getOperatingSystem() != OperatingSystems.WINDOWS){
            while(soundsPlaying != 0){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }
        
        playSoundFile(Globals.getResourceAsString("data/sounds/launch.wav"), Globals.getOperatingSystem() == OperatingSystems.WINDOWS); //Don't fork a thread here
        
        if(Settings.getSoundEnabled() && Globals.getOperatingSystem() != OperatingSystems.WINDOWS)
        {        
            delayOtherSounds = true;
            
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {}
                    delayOtherSounds = false;
                }
            }.start();
        }
    }
    
    public static void playReadySound(){
        playSoundFile(Globals.getResourceAsString("data/sounds/ready.wav"), true);
    }
    
    public static void playUnreadySound(){
        playSoundFile(Globals.getResourceAsString("data/sounds/unready.wav"), true);
    }
    
    public static void playLoginSound(){
        playSoundFile(Globals.getResourceAsString("data/sounds/login.wav"), true);
    }
    
    public static void playLogoutSound(){
        playSoundFile(Globals.getResourceAsString("data/sounds/logout.wav"), true);
    }

    private static void playSoundFile(final String file, boolean forkThread) {
        if(!Settings.getSoundEnabled()){
           return; 
        }
        
        while(delayOtherSounds && Launcher.isPlaying()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {}
        }
        
        
        if (forkThread) {
            //On windows this works parallel, even when playing
            new Thread() {
                @Override
                public void run() {
                    try{
                        playSound(file);
                    }catch(Exception e){
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();
        } else {
            //On linux, we have to wait until the sound has stopped before launching game
            //or else the game may crash
            playSound(file);
        }
    }

    private static void playSound(String file) {
        updateSoundsPlaying(1);
        try {
            autoDetect();
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(file));
            DataLine.Info linfo = new DataLine.Info(Clip.class, stream.getFormat());
            Mixer.Info minfo[] = AudioSystem.getMixerInfo();
            Mixer mixer = AudioSystem.getMixer(minfo[Settings.getaudioPlaybackDeviceIndex()]);
            //Clip clip = (Clip) AudioSystem.getLine(info);
            Clip clip = (Clip) mixer.getLine(linfo);
            clip.open(stream);
            clip.start();
            do {
                Thread.sleep(100);
            } while (clip.isRunning());
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateSoundsPlaying(-1);
    }
}
