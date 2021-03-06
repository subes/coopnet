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

package coopnetclient.utils.ui;

import coopnetclient.utils.settings.Settings;
import coopnetclient.utils.*;
import coopnetclient.*;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.utils.launcher.Launcher;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundPlayer {

    private static int soundsPlaying = 0;
    private static boolean delayOtherSounds = false;

    //Threadsafe use of variable
    private synchronized static void updateSoundsPlaying(int additor){
        soundsPlaying+=additor;
    }


    public static void playNudgeSound() {
        playSoundFile(Globals.getResourceAsString("data/sounds/nudge.wav"), true);
    }

    public static void playLaunchSound() {
        if(Settings.getSoundEnabled() && Globals.getOperatingSystem() == OperatingSystems.LINUX){
            while(soundsPlaying != 0){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        }

        playSoundFile(Globals.getResourceAsString("data/sounds/launch.wav"), Globals.getOperatingSystem() != OperatingSystems.LINUX); //Don't fork a thread here

        if(Settings.getSoundEnabled() && Globals.getOperatingSystem() == OperatingSystems.LINUX)
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

    public static void playRoomCloseSound(){
        playSoundFile(Globals.getResourceAsString("data/sounds/room_close.wav"), true);
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
                        ErrorHandler.handle(e);
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
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(file));
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
            do {
                Thread.sleep(100);
            } while (clip.isRunning());
            clip.close();
        } catch (Exception e) {
            Logger.log(e);
        }
        updateSoundsPlaying(-1);
    }
}
