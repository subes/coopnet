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

import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;

public class ParameterLaunchHandler extends LaunchHandler {

    private ParameterLaunchInfo launchInfo;
    
    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if(!(launchInfo instanceof ParameterLaunchInfo)){
            throw new IllegalArgumentException("expected launchInfo to be "+ParameterLaunchInfo.class.toString()+", but got "+launchInfo.getClass().toString());
        }
        
        this.launchInfo =  (ParameterLaunchInfo) launchInfo;
        
        return true;
    }

    @Override
    public boolean launch() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updatePlayerName() {
        //do nothing, because parameter based games don't support this
    }

}