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

import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.util.ArrayList;

public class TempGameSettings {
    
    private static boolean isHost;
    private static String map;
    private static ArrayList<GameSetting> gameSettings;
    
    public static String getMap(){
        return map;
    }
    
    public static void setMap(String map){
        TempGameSettings.map = map;
    }
    
    public static boolean getIsHost(){
        return isHost;
    }
    
    public static void setIsHost(boolean isHost){
        TempGameSettings.isHost = isHost;
    }
    
    public static void initalizeGameSettings(String gameName, String childName){
        gameSettings = GameDatabase.getGameSettings(gameName, childName);
        for(GameSetting setting : gameSettings){
            setting.reset();
        }
    }
    
    public static ArrayList<GameSetting> getGameSettings(){
        return gameSettings;
    }
    
    public static String getGameSetting(String settingName) {
        for(GameSetting gs : gameSettings){
            if(gs.getName().equals(settingName)){
                return gs.getValue();
            }
        }
        return null;
    }
    
    public static void setGameSetting(String settingname, String value, boolean broadcast){
        for(GameSetting setting : gameSettings){
            if(setting.getName().equals(settingname)){
                setting.setValue(value, isHost && broadcast);
                return;
            }
        }
    }


}
