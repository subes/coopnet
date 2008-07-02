/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.gamedatabase;

import java.util.HashMap;
import java.util.ArrayList;

public class Game {

    private String name;
    private HashMap<String, String> fields;
    private ArrayList<Game> mods;

    public Game() {
        name = "";
        fields = new HashMap<String, String>();
        mods = new ArrayList<Game>();
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

    private Game getMod(String mod) {
        int index = indexOfMod(mod);
        return index == -1 ? null : mods.get(index);
    }

    private String getValue(String field, String modname) {
        if (modname != null) {
            Game mod = getMod(modname);
            if (mod != null) {
                String value = mod.getValue(field, null);
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

    public int getDefPort(String modname) {
        try{
            return new Integer(getValue("DEFPORT", modname));
        }catch(NumberFormatException ex){
            return -1;
        }
    }

    public void setDefPort(String value) {
        fields.put("DEFPORT", value);
    }

    public String getGameSettings(String modname) {
        return getValue("SETTINGS", modname);
    }

    public void setGameSettings(String value) {
        fields.put("SETTINGS", value);
    }

    public String getMapPath(String modname) {
        return getValue("MAPPATH", modname);
    }

    public void setMapPath(String value) {
        fields.put("MAPPATH", value);
    }

    public String getMapExtension(String modname) {
        return getValue("MAPEXT", modname);
    }

    public void setMapExtension(String value) {
        fields.put("MAPEXT", value);
    }

    public String getRelativeExePath(String modname) {
        return getValue("EXE", modname);
    }

    public void setRelativeExePath(String value) {
        fields.put("EXE", value);
    }

    public String getGameModes(String modname) {
        return getValue("GAMEMODES", modname);
    }

    public void setGameModes(String value) {
        fields.put("GAMEMODES", value);
    }

    public String getHostPattern(String modname) {
        return getValue("LAUNCHPATTERN", modname);
    }

    public void setHostPattern(String value) {
        fields.put("LAUNCHPATTERN", value);
    }

    public String getJoinPattern(String modname) {
        return getValue("JOINPATTERN", modname);
    }

    public void setJoinPattern(String value) {
        fields.put("JOINPATTERN", value);
    }

    public String getRegEntry(String modname) {
        return getValue("REGENTRY", modname);
    }

    public void setRegEntry(String value) {
        fields.put("REGENTRY", value);
    }

    public String getLaunchMethod(String modname) {
        return getValue("LAUNCHMETHOD", modname);
    }

    public void setLaunchMethod(int value) {
        fields.put("LAUNCHMETHOD", value + "");
    }

    public String getGuid(String modname) {
        return getValue("GUID", modname);
    }

    public void setGuid(String value) {
        fields.put("GUID", value);
    }
}
