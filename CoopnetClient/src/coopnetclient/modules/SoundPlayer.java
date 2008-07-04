/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

    This file is part of Coopnet.

    Coopnet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Coopnet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Coopnet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.modules;

import coopnetclient.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class SoundPlayer {

    private static String soundFile;

    public static void playNudgeSound() {
        playSoundFile("data/sounds/nudge.wav");
    }

    public static void playLaunchSound() {
        playSoundFile("data/sounds/launch.wav");
    }
    
    public static void playReadySound(){
        playSoundFile("data/sounds/ready.wav");
    }
    
    public static void playUnreadySound(){
        playSoundFile("data/sounds/unready.wav");
    }

    private static void playSoundFile(String file) {
        if(!Settings.getSoundEnabled()){
           return; 
        }
        
        soundFile = file;
        if (Globals.getOperatingSystem() == Globals.OS_WINDOWS || !Globals.getIsPlayingStatus()) {
            //On windows this works parallel, even when playing
            new Thread() {
                @Override
                public void run() {
                    try{
                        playSound();
                    }catch(Exception e){
                        ErrorHandler.handleException(e);
                    }
                }
            }.start();
        } else {
            //On linux, we have to wait until the sound has stopped before launching game
            //or else the game may crash
            playSound();
        }
    }

    private static void playSound() {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(soundFile));
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
            do {
                Thread.sleep(100);
            } while (clip.isRunning());
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
