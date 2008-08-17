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

package coopnetclient.utils.launcher.launchinfos;

import coopnetclient.Globals;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.TempGameSettings;

public class ParameterLaunchInfo extends LaunchInfo {
    
    private String binaryPath;
    private String parameters;
    
    public ParameterLaunchInfo(String gameName, String selectedChildName, String hostIP, boolean isHost){
        super(gameName, selectedChildName, hostIP, isHost);
        
        binaryPath = GameDatabase.getLaunchPathWithExe(gameName, selectedChildName);
        
        if(isHost){
            parameters = " " + GameDatabase.getHostPattern(gameName, selectedChildName);
        }else{
            parameters = " " + GameDatabase.getJoinPattern(gameName, selectedChildName);
        }
    }
    
    public String getBinaryPath(){
        return binaryPath;
    }
    
    public String getParameters(){
        String ret = parameters;
        
        ret = ret.replace("{HOSTIP}", this.getHostIP());
        ret = ret.replace("{NAME}", Globals.getThisPlayer_inGameName());
        
        if(TempGameSettings.getMap() != null){
            ret = ret.replace("{MAP}", TempGameSettings.getMap());
        }
        
        for (GameSetting gs : TempGameSettings.getGameSettings()) {
            parameters = parameters.replace("{" + gs.getKeyWord() + "}", gs.getValue());
        }
        
        return parameters;
    }
}
