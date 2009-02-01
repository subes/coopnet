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
import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import coopnetclient.utils.launcher.TempGameSettings;
import java.io.File;

public class DOSLaunchInfo extends LaunchInfo {
    
    private String dosboxBinaryPath;
    private String dosboxParameters;
    private String gameBinaryPath;

    public DOSLaunchInfo(RoomData roomData){
        super(roomData);

        //TODO: move dosboxBinaryPath and dosboxPrameters outside of the launchinfo and handle this in DOSLaunchHandler
        //see jdplay implementation
        dosboxBinaryPath = Settings.getDOSBoxExecutable();
        gameBinaryPath = GameDatabase.getLocalExecutablePath(GameDatabase.getIDofGame(roomData.getChannel()));
        
        if(roomData.isHost()){
            dosboxParameters = " " + GameDatabase.getHostPattern(roomData.getChannel(), roomData.getModName()) ;
        }else{
            dosboxParameters = " " + GameDatabase.getJoinPattern(roomData.getChannel(), roomData.getModName()) ;
        }
        if(Settings.getDOSBoxFullscreen()){
            dosboxParameters += " -fullscreen";
        }
    }

    public String getGameBinaryPath(){
        return gameBinaryPath;
    }
    
    public String getDosboxBinaryPath(){
        return dosboxBinaryPath;
    }
    
    public String getInstallPath(){
        return GameDatabase.getInstallPath(roomData.getChannel());
    }
    
    public String getDosboxParameters(){
        String ret = dosboxParameters;

        ret = ret.replace("{GAMEEXE}", GameDatabase.getLocalExecutablePath(GameDatabase.getIDofGame(roomData.getChannel())));
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
    public String getBinaryName() {
        return gameBinaryPath.substring(gameBinaryPath.lastIndexOf(File.separatorChar)+1);
    }
}
