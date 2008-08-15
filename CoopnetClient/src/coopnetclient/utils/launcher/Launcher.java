/*	
Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.utils.launcher;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.launcher.launchhandlers.JDPlayJniLaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.JDPlayRmtLaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.LaunchHandler;
import coopnetclient.utils.launcher.launchhandlers.ParameterLaunchHandler;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;

public class Launcher {
    
    private static boolean isInitialized;
    private static boolean isPlaying;
    private static LaunchHandler launchHandler;
    
    public static boolean isInitialized(){
        return isInitialized;
    }
    
    public static boolean isPlaying(){
        return isPlaying;
    }
    
    public static void initialize(LaunchInfo launchInfo){
        if(launchInfo instanceof DirectPlayLaunchInfo){
            if(Globals.getOperatingSystem() == OperatingSystems.WINDOWS){
                launchHandler = new JDPlayJniLaunchHandler();
            }else{
                launchHandler = new JDPlayRmtLaunchHandler();
            }
        }else
        if(launchInfo instanceof ParameterLaunchInfo){
            launchHandler = new ParameterLaunchHandler();
        }
        
        isInitialized = launchHandler.initialize(launchInfo);
        if(isInitialized == false){
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed initializing the "+launchHandler.getClass().toString()+", you won't be able to play the game!", ChatStyles.SYSTEM);
        }else{
            TabOrganizer.getRoomPanel().enableButtons();
        }
    }
    
    public static void launch(){
        if(isPlaying()){
            throw new IllegalStateException("Another game has been launched already!");
        }
        if(isInitialized()){
            isPlaying = true;
            boolean success = launchHandler.launch();
            if(!success){
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed launching the game, there seems to be a problem with the setup of the game!", ChatStyles.SYSTEM);
            }
            isPlaying = false;
        }else{
            throw new IllegalStateException("The game has to be initialized before launching it!");
        }
    }
    
    public static void deInitialize(){
        isInitialized = false;
        launchHandler = null;
    }
    
}
