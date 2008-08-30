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

package coopnetclient.utils.gamedatabase;

import coopnetclient.enums.LaunchMethods;
import java.util.HashMap;
import java.util.ArrayList;

public class Game {

    private String name;
    protected HashMap<String, String> fields;
    protected ArrayList<Game> mods;
    protected ArrayList<GameSetting> settings;

    public Game() {
        name = "";
        fields = new HashMap<String, String>();
        mods = new ArrayList<Game>();
        settings = new ArrayList<GameSetting>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return ((Game) o).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }

    private int indexOfMod(String modname) {
        for (int i = 0; i < mods.size(); i++) {
            if (mods.get(i).getGameName().equals(modname)) {
                return i;
            }
        }
        return -1;
    }

    public void addSetting(GameSetting setting){
        settings.add(setting);
    }
    
    public void addMod(Game mod) {
        mods.add(mod);
    }

    public String getModName(int index) {
        return mods.get(index).getGameName();
    }

    public Object[] getAllModNames() {
        ArrayList names = new ArrayList();
        for (Game game : mods) {
            names.add(game.getGameName());
        }
        if (names.size() != 0) {
            names.add(0, "None");
        }
        return names.toArray();
    }
    
    public Object[] getAllSettingNames(String modname) {
        ArrayList<String> names = new ArrayList<String>();
        for (GameSetting setting : getGameSettings(modname)) {
            names.add(setting.getName());
        }
        return names.toArray(new String[0]);
    }

    private Game getMod(String mod) {
        int index = indexOfMod(mod);
        return index == -1 ? null : mods.get(index);
    }

    private String getFieldValue(String field, String modname) {
        if (modname != null) {
            Game mod = getMod(modname);
            if (mod != null) {
                String value = mod.getFieldValue(field, null);
                return ((value == null) ? (fields.get(field)) : (value));
            } else {
                return fields.get(field);
            }
        } else {
            return fields.get(field);
        }
    }

    /**
     * used for gamename 
     * and modname for mods
     */
    public String getGameName() {
        return name;
    }

    public void setGameName(String name) {
        this.name = name;
    }

    public boolean isInstantLaunchable(String modname) {
        String val = getFieldValue("InstantLaunchable", modname);
        if(val != null){
            return true; 
        }
        return false;
    }
    
    public void setInstantLauncable(boolean value){
        if(value){
            fields.put("InstantLaunchable", "");
        }else{
            fields.remove("InstantLaunchable");
        }
    }

    public ArrayList<GameSetting> getGameSettings(String modname) {
        if(modname == null || modname.length()==0){
            return   settings;
        }
        else{
            return mods.get(indexOfMod(modname)).settings;
        }        
    }
    
    public void setGameSetting(String settingName,String settingValue,boolean broadcast){
        for(GameSetting gs :settings){
            if(gs.getName().equals(settingName)){
                gs.setValue(settingValue,broadcast);
                return;
            }
        }
    }
    
    public void setGameSettings(String modname ,ArrayList<GameSetting> settings) {
        if(modname == null || modname.length()==0){
            this.settings = settings;  
        }
        else{
            mods.get(indexOfMod(modname)).settings = settings;
        }        
    }
    
    public boolean getNoSpacesFlag(String modname) {
        String val = getFieldValue("NOSPACES", modname);
        if(val == null){
            return false;
        }
        return true;
    }

    public void setNoSpacesFlag(boolean value) {
        fields.put("NOSPACES", value?"true":"false");
    }

    public String getMapPath(String modname) {
        return getFieldValue("MAPPATH", modname);
    }

    public void setMapPath(String value) {
        fields.put("MAPPATH", value);
    }

    public String getMapExtension(String modname) {
        return getFieldValue("MAPEXT", modname);
    }

    public void setMapExtension(String value) {
        fields.put("MAPEXT", value);
    }

    public String getRelativeExePath(String modname) {
        return getFieldValue("EXE", modname);
    }

    public void setRelativeExePath(String value) {
        fields.put("EXE", value);
    }

    public String getHostPattern(String modname) {
        return getFieldValue("LAUNCHPATTERN", modname);
    }

    public void setHostPattern(String value) {
        fields.put("LAUNCHPATTERN", value);
    }

    public String getJoinPattern(String modname) {
        return getFieldValue("JOINPATTERN", modname);
    }

    public void setJoinPattern(String value) {
        fields.put("JOINPATTERN", value);
    }

    public String getRegEntry(String modname) {
        return getFieldValue("REGENTRY", modname);
    }

    public void setRegEntry(String value) {
        fields.put("REGENTRY", value);
    }

    public LaunchMethods getLaunchMethod(String modname) {
        return LaunchMethods.valueOf(getFieldValue("LAUNCHMETHOD", modname));
    }

    public void setLaunchMethod(String value) {
        fields.put("LAUNCHMETHOD", value);
    }

    public String getGuid(String modname) {
        return getFieldValue("GUID", modname);
    }

    public void setGuid(String value) {
        fields.put("GUID", value);
    }
}
