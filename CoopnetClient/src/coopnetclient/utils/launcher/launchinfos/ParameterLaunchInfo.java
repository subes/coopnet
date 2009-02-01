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
import coopnetclient.utils.RoomData;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.TempGameSettings;
import java.io.File;

public class ParameterLaunchInfo extends LaunchInfo {
    
    private String binaryPath;
    private String parameters;
    
    public ParameterLaunchInfo(RoomData roomData){
        super(roomData);
        
        binaryPath = GameDatabase.getLaunchPathWithExe(roomData.getChannel(), roomData.getModName());
        
        if(roomData.isHost()){
            parameters = " " + GameDatabase.getHostPattern(roomData.getChannel(), roomData.getModName()) ;
        }else{
            parameters = " " + GameDatabase.getJoinPattern(roomData.getChannel(), roomData.getModName()) ;
        }
    }
    
    public String getBinaryPath(){
        return binaryPath;
    }
    
    public String getInstallPath(){
        return GameDatabase.getInstallPath(roomData.getChannel());
    }
    
    public String getParameters(){
        String ret = parameters;
        
        ret = ret.replace("{HOSTIP}", roomData.getIP());
        if(GameDatabase.getNoSpacesFlag(roomData.getChannel(), roomData.getModName())){
            ret = ret.replace("{NAME}", Globals.getThisPlayer_inGameName().replace(" ", "_"));
        }else{
            ret = ret.replace("{NAME}", Globals.getThisPlayer_inGameName());
        }
        
        ret = ret.replace("{ROOMNAME}", roomData.getRoomName());
        
        if( roomData.getPassword()!= null && roomData.getPassword().length() > 0){
            String tmp;
            if(roomData.isHost()){
                tmp = GameDatabase.getHostPasswordPattern(roomData.getChannel(), roomData.getModName()) ;
            }else{
                tmp = GameDatabase.getJoinPasswordPattern(roomData.getChannel(), roomData.getModName()) ;
            }
            
            tmp = tmp.replace("{PASSWORD}", roomData.getPassword());
            ret = ret.replace("{PASSWORD}",tmp  );
        }else{
             ret = ret.replace("{PASSWORD}","" );
        }
        
        if(TempGameSettings.getMap() != null){
            ret = ret.replace("{MAP}", TempGameSettings.getMap());
        }
        
        for (GameSetting gs : TempGameSettings.getGameSettings()) {
            ret = ret.replace("{" + gs.getKeyWord() + "}", gs.getRealValue());
        }

        String params = GameDatabase.getAdditionalParameters(GameDatabase.getIDofGame(roomData.getChannel()));
        if( params != null && params.length() >0){
            ret += " " + params;
        }
        
        return ret;
    }

    @Override
    public String getBinaryName(){
        return binaryPath.substring(binaryPath.lastIndexOf(File.separatorChar)+1);
    }
}
