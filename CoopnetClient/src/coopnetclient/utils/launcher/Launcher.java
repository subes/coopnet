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

package coopnetclient.utils.launcher;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import coopnetclient.utils.SoundPlayer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.launcher.launchhandlers.JDPlayLaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.LaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.ParameterLaunchHandler;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;

public class Launcher {
    
    private static boolean isInitialized;
    private static String launchedGame;
    private static LaunchHandler launchHandler;
    
    public static boolean isInitialized(){
        return isInitialized;
    }
    
    public static boolean isPlaying(){
        return launchedGame != null;
    }

    public static String getLaunchedGame(){
        return launchedGame;
    }
    
    public static void initialize(LaunchInfo launchInfo){

        TempGameSettings.initalizeGameSettings(launchInfo.getGameName(), launchInfo.getModName());

        if(launchInfo instanceof DirectPlayLaunchInfo){
            launchHandler = new JDPlayLaunchHandler();
        }else
        if(launchInfo instanceof ParameterLaunchInfo){
            launchHandler = new ParameterLaunchHandler();
        }

        if(!isPlaying()){
            synchronized(launchHandler){
                isInitialized = launchHandler.initialize(launchInfo);

                if(TabOrganizer.getRoomPanel() != null){
                    int numSettings = GameDatabase.getGameSettings(launchInfo.getGameName(), launchInfo.getModName()).size();
                    if(isInitialized && numSettings == 0){
                        TabOrganizer.getRoomPanel().initDone();
                    }else{
                        TabOrganizer.getRoomPanel().initDoneReadyDisabled();
                    }
                }
            }

            if(launchInfo instanceof ParameterLaunchInfo){
                if(!launchInfo.getIsInstantLaunch()
                    && TabOrganizer.getRoomPanel()!= null
                    && TabOrganizer.getRoomPanel().isHost()
                    && GameDatabase.getGameSettings(launchInfo.getGameName(), launchInfo.getModName()).size() > 0){
                    Globals.openGameSettingsFrame(launchInfo.getGameName(), launchInfo.getModName(),launchInfo.getIsHost());
                }
            }
        }else{
            if(TabOrganizer.getRoomPanel() != null){
                TabOrganizer.getRoomPanel().initDone();
            }
        }
    }
    
    public static boolean predictSuccessfulLaunch(){
        if(isPlaying()){
            return true;
        }else
        if(!isInitialized ){
            Logger.log(LogTypes.LAUNCHER, "predictSuccessfulLaunch() called while Launcher was not initialized!");
            return false;
        }else{
            synchronized(launchHandler){
                return launchHandler.predictSuccessfulLaunch();
            }
        }
    }
    
    public static void launch(){        
        if(isInitialized()){
            synchronized(launchHandler){
                launchedGame = launchHandler.getGameName();

                if (Settings.getSleepEnabled()) {
                    Globals.setSleepModeStatus(true);
                }

                if(!launchHandler.launch()){
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Launch failed, maybe the game is not setup properly or a process closed unexpectedly!", ChatStyles.SYSTEM,false);
                }

                Globals.setSleepModeStatus(false);

                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", 
                                "Game closed.", 
                                ChatStyles.SYSTEM,false);

                launchedGame = null;
                for (int i = 0; TabOrganizer.getChannelPanel(i) != null; i++) {
                    TabOrganizer.getChannelPanel(i).enablebuttons();
                }                
            }
        }else{
            throw new IllegalStateException("The game has to be initialized before launching it!");
        }
    }
    
    public static void deInitialize(){
        isInitialized = false;
        launchHandler = null;
    }
    
    public static void updatePlayerName(){
        if(launchHandler != null){
            synchronized(launchHandler){
                launchHandler.updatePlayerName();
            }
        }
    }
    
}
