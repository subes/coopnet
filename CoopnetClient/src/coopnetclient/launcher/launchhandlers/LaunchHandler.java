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

package coopnetclient.launcher.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.launcher.launchinfos.LaunchInfo;

public abstract class LaunchHandler {
    
    private boolean isInitialized;
    private boolean isPlaying;
    
    public boolean isInitialized(){
        return isInitialized;
    }
    
    public void initalize(LaunchInfo launchInfo){
        isInitialized = doInitialize(launchInfo);
        if(isInitialized == false){
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed initializing the LaunchHandler , you won't be able to play the game!", ChatStyles.SYSTEM);
        }else{
            //TabOrganizer.getRoomPanel().enableButtons();
        }
    }
    protected abstract boolean doInitialize(LaunchInfo launchInfo);
    
    public void launch(){
        if(isInitialized){
            boolean success = doLaunch();
            if(!success){
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "Failed launching the game, there seems to be a problem with the setup of the game!", ChatStyles.SYSTEM);
            }
        }else{
            throw new IllegalStateException("The game has to be initialized before launching it!");
        }
    }
    protected abstract boolean doLaunch();
    
    public abstract void updatePlayerName();
    
    public void deInitialize(){
        //this just sets the variable for now, as we don't have any LaunchHandler that supports deinitialization
        isInitialized = false;
    }
}
