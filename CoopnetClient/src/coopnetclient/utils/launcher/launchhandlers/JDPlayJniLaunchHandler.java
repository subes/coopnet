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

package coopnetclient.utils.launcher.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import jdplay.JDPlay;

public class JDPlayJniLaunchHandler extends LaunchHandler {

    private JDPlay jdplay;
    
    private DirectPlayLaunchInfo launchInfo;
    
    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if(jdplay == null){
            jdplay = new JDPlay(Globals.getThisPlayer_inGameName(), Globals.JDPLAY_MAXSEARCHRETRIES, Globals.getDebug());
        }
        
        if(!(launchInfo instanceof DirectPlayLaunchInfo)){
            throw new IllegalArgumentException("expected launchInfo to be "+DirectPlayLaunchInfo.class.toString()+", but got "+launchInfo.getClass().toString());
        }
        
        this.launchInfo = (DirectPlayLaunchInfo) launchInfo;
        
        return jdplay.initialize(this.launchInfo.getGameGUID(), this.launchInfo.getHostIP(), this.launchInfo.getIsHost());
    }

    @Override
    public boolean launch() {
        boolean compatibility = launchInfo.getCompatibility();
        
        return jdplay.launch(compatibility);
    }

    @Override
    public void updatePlayerName() {
        jdplay.updatePlayerName(Globals.getThisPlayer_inGameName());
    }

}
