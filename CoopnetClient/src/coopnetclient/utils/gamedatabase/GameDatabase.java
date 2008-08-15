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
package coopnetclient.utils.gamedatabase;

import coopnetclient.*;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.enums.SettingTypes;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class GameDatabase {

    public static final String datafilepath = "data/gamedata";
    public static final String testdatafilepath = "data/testgamedata";
    private static final String lpfilepath = "data/localpaths";
    private static boolean registryOK = false;
    protected static final String SETTING_DELIMITER_PATTERN = "\\^";
    protected static final String SETTING_DELIMITER = "^";    //fields
    private static HashMap<String, String> IDtoGameName;     // key is the ID    
    private static HashMap<String, String> localexecutablepath; //shud point to the exe/binary
    private static HashMap<String, String> localinstallpath; //shud point to the game basedir
    private static ArrayList<String> isexperimental;
    private static ArrayList<Game> gameData;
    public static int version = 0;
    

    static {
        if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS) {
            try {
                System.loadLibrary("lib/ICE_JNIRegistry");
                Class.forName("com.ice.jni.registry.Registry");
                Class.forName("com.ice.jni.registry.RegistryKey");
                registryOK = true;
            } catch (UnsatisfiedLinkError er) {
                System.out.println("Error while loading external dlls");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        IDtoGameName = new HashMap<String, String>();
        localexecutablepath = new HashMap<String, String>();
        localinstallpath = new HashMap<String, String>();
        isexperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
        load("", datafilepath);
        load(null, testdatafilepath);
        loadLocalPaths();
        IDtoGameName.put("WLC", "Welcome");
        IDtoGameName.put("TST", "GameTest channel");
    }

    public static boolean isBeta(String channelname) {
        return isexperimental.contains(channelname);
    }

    public static boolean isInstantLaunchable(String gamename) {
        return gameData.get(indexOfGame(gamename)).isInstantLaunchable(lpfilepath);
    }

    public static void reset() {
        version = 0;
        localexecutablepath = new HashMap<String, String>();
        localinstallpath = new HashMap<String, String>();
        IDtoGameName = new HashMap<String, String>();
        isexperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
        IDtoGameName.put("WLC", "Welcome");
        IDtoGameName.put("TST", "GameTest channel");
    }

    private static int indexOfGame(String gamename) {
        if (gamename == null) {
            return -1;
        }
        for (int i = 0; i < gameData.size(); i++) {
            if (gameData.get(i).getGameName().equals(gamename)) {
                return i;
            }
        }
        return -1;
    }

    public static Game getGameData(String gamename) {
        return gameData.get(indexOfGame(gamename));
    }

    public static Object[] getGameModNames(String gamename) {
        return gameData.get(indexOfGame(gamename)).getAllModNames();
    }

    public static String getModByIndex(String gamename, String modindex) {
        return gameData.get(indexOfGame(gamename)).getGameName();
    }

    public static String getGameName(String ID) {
        return IDtoGameName.get(ID);
    }

    public static String IDofGame(String gamename) {
        String tmp = "";
        for (String s : IDtoGameName.keySet()) {
            tmp = IDtoGameName.get(s);
            if (tmp.equals(gamename)) {
                return s;
            }
        }
        return null;
    }

    public static String readRegistry(String fullpath) {
        if (registryOK) {
            try {
                String tmp[] = fullpath.split("\\\\");
                RegistryKey current = Registry.getTopLevelKey(tmp[0]);
                for (int i = 1; i < tmp.length - 1; i++) {
                    current = current.openSubKey(tmp[i]);
                }
                return current.getStringValue(tmp[tmp.length - 1]);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static void setGameSettings(String gamename, String modname, ArrayList<GameSetting> settings) {
        gameData.get(indexOfGame(gamename)).setGameSettings(modname, settings);
    }

    public static ArrayList<GameSetting> getGameSettings(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGameSettings(modname);
    }

    public static void setMapPath(String gamename, String path) {
        gameData.get(indexOfGame(gamename)).setMapPath(path);
    }

    public static String getMapPath(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getMapPath(modname);
    }

    public static void setMapExtension(String gamename, String ext) {
        gameData.get(indexOfGame(gamename)).setMapExtension(ext);
    }

    public static String getMapExtension(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getMapExtension(modname);
    }

    public static void setRelativeExePath(String gamename, String path) {
        gameData.get(indexOfGame(gamename)).setRelativeExePath(path);
    }

    public static String getRelativeExePath(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx == -1) {
            return null;
        }
        return gameData.get(idx).getRelativeExePath(modname);
    }

    public static String getLocalExecutablePath(String gamename) {
        return localexecutablepath.get(gamename);
    }

    public static String getLocalInstallPath(String gamename) {
        return localinstallpath.get(gamename);
    }

    public static void setLocalExecutablePath(String gamename, String path) {
        localexecutablepath.put(gamename, path);
    }

    public static void setLocalInstallPath(String gamename, String path) {
        localinstallpath.put(gamename, path);
    }

    public static Object[] gameNames() {
        return IDtoGameName.values().toArray(new String[0]);
    }

    public static String[] gameNamesAsStringArray() {
        return IDtoGameName.values().toArray(new String[0]);
    }

    public static void setHostPattern(String gamename, String pattern) {
        gameData.get(indexOfGame(gamename)).setHostPattern(pattern);
    }

    public static String getHostPattern(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getHostPattern(modname);
    }

    public static void setJoinPattern(String gamename, String pattern) {
        gameData.get(indexOfGame(gamename)).setJoinPattern(pattern);
    }

    public static String getJoinPattern(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getJoinPattern(modname);
    }

    public static void setRegEntry(String gamename, String regkey) {
        gameData.get(indexOfGame(gamename)).setRegEntry(regkey);
    }

    public static String getRegEntry(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx == -1) {
            return null;
        }
        return gameData.get(idx).getRegEntry(modname);
    }

    public static void setLaunchMethod(String gamename, LaunchMethods launchmethod) {
        gameData.get(indexOfGame(gamename)).setLaunchMethod(launchmethod.toString());
    }

    public static LaunchMethods getLaunchMethod(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx > 0) {
            return gameData.get(idx).getLaunchMethod(modname);
        } else {
            return LaunchMethods.CHAT_ONLY;
        }
    }

    public static void setGuid(String gamename, String guid) {
        gameData.get(indexOfGame(gamename)).setGuid(guid);
    }

    public static String getGuid(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGuid(modname);
    }

    public static boolean isLaunchable(String gamename) {
        String test = null;

        if (getLaunchMethod(gamename, null) == LaunchMethods.DIRECTPLAY || getLaunchMethod(gamename, null) == LaunchMethods.DIRECTPLAY_FORCED_COMPATIBILITY) {
            return true;
        }
        test = getLaunchPathWithExe(gamename,null);
        if (test != null && test.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getInstallPath(String gamename) {
        String exepath = getLaunchPathWithExe(gamename, null);
        String relativexepath = GameDatabase.getRelativeExePath(gamename, null);

        if (exepath != null && exepath.length() > 0 && relativexepath != null) {
            return exepath.substring(0, exepath.length() - relativexepath.length());
        } else {
            return "";
        }
    }

    public static String getFullMapPath(String gamename, String modName) {
        String tmp = getLaunchPathWithExe(gamename, modName);
        if (tmp == null || tmp.length() == 0) {
            return null;
        }
        String relativeexepath = GameDatabase.getRelativeExePath(gamename, modName);
        String mappath = GameDatabase.getMapPath(gamename, modName);
        tmp = tmp.replace(relativeexepath, mappath);
        return tmp;
    }

    public static String getLaunchPathWithExe(String gamename, String modName) {
        String path = "";
        switch (Globals.getOperatingSystem()) {
            case LINUX:
                path = GameDatabase.getLocalExecutablePath(gamename);
                break;
            case WINDOWS:
                path = GameDatabase.readRegistry(GameDatabase.getRegEntry(gamename, modName));

                //if its not detected try loading from local paths(given by user)
                if (path == null || (path != null && path.length() == 0)) {
                    String tmp = GameDatabase.getLocalExecutablePath(gamename);
                    if (tmp != null) {
                        path = tmp;
                    }
                }
                // make sure ret points to the exe
                if (path != null && path.length() > 0 && !path.endsWith(".exe")) {
                    String tmp = GameDatabase.getRelativeExePath(gamename, modName);
                    if (tmp != null) {
                        path = path + tmp;
                    }
                }
                break;
        }
        return path;
    }

    public static void loadVersion() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(datafilepath));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not load gamedatabase");
            return;
        }
        //read version
        try {
            version = new Integer(br.readLine().substring(8));
        } catch (IOException ex) {
            System.out.println("Could not load gamedatabase");
            return;
        }
    }

    public static void load(String gamename, String datafilepath) {
        Game currentgame = new Game();
        Game currentmod = new Game();
        boolean beta = false;
        String _ID = null;
        System.out.println("game database loading");
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(datafilepath));
        } catch (FileNotFoundException ex) {
            System.out.println("Could not load gamedatabase");
            return;
        }

        //reading data
        Boolean done = false;
        String input;
        int state = 0;
        while (!done) {
            try {
                input = br.readLine();
                if (input == null) {
                    done = true;
                    continue;
                }
            } catch (IOException ex) {
                System.out.println("Could not load gamedatabase");
                return;
            }
            switch (state) {

                case 0: {//start, waiting for '{'

                    if (input.startsWith("VERSION ")) {
                        version = Integer.valueOf(input.substring(8));
                    } else if (input.equals("{")) {
                        state = 1;
                        //reset values
                        currentgame = new Game();
                        beta = false;
                        _ID = null;
                        continue;
                    }
                    break;
                }

                case 1: {//reading data

                    if (input.equals("MODS:[")) {
                        state = 2;
                        currentmod = new Game();
                        continue;
                    } else if (input.equals("SETTINGS:(")) {
                        state = 3;
                        continue;
                    } else if (input.startsWith("beta")) {
                        beta = true;
                    } else if (input.startsWith("ID=")) {
                        _ID = input.substring(3);
                    } else if (input.startsWith("}")) {
                        //store data if needed
                        IDtoGameName.put(_ID, currentgame.getGameName());
                        if (gamename == null || currentgame.getGameName().equals(gamename)) {
                            if (indexOfGame(currentgame.getGameName()) > -1) { //remove old data
                                gameData.remove(indexOfGame(currentgame.getGameName()));
                            }
                            gameData.add(currentgame);
                            if (beta) {
                                isexperimental.add(currentgame.getGameName());
                            }
                        }
                        //game read, reset
                        state = 0;
                    } else {
                        BuildGameData(currentgame, input);
                    }
                    break;
                }
                case 2: {//read mods
                    if (input.equals("]")) {
                        currentgame.addMod(currentmod);
                        currentmod = null;
                        state = 1;
                        continue;
                    } else if (input.equals("SETTINGS:(")) {
                        state = 4;
                        continue;
                    } else {
                        BuildGameData(currentmod, input);
                    }
                    break;
                }
                case 3: {//read settings
                    if (input.equals(")")) {
                        state = 1;
                        continue;
                    } else {
                        buildSettingData(currentgame, input);
                    }
                    break;
                }
                case 4: {//read settings for mods
                    if (input.equals(")")) {
                        state = 2;
                        continue;
                    } else {
                        buildSettingData(currentmod, input);
                    }
                    break;
                }
            }
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        System.out.println("game database loaded");
    }

    public static void saveTestData() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(testdatafilepath));
        } catch (Exception ex) {
            System.out.println("Could not save testdata");
        }
        for (Game game : gameData) {
            if (game.getGameName().equals(GameDatabase.getGameName("TST"))) {
                //start
                pw.println("{");
                pw.println("NAME=" + game.getGameName());
                pw.println("ID=TST");
                //print fields:
                for (String key : game.fields.keySet()) {
                    if (game.fields.get(key).length() > 0) {
                        pw.println(key + "=" + game.fields.get(key));
                    }
                }
                //print settings
                pw.println("SETTINGS:(");
                for (GameSetting gs : game.settings) {
                    pw.println(gs.getStorageString());
                }
                //settings end
                pw.println(")");

                //end
                pw.println("}");
            //break;
            }
        }
        pw.flush();
        pw.close();
        System.out.println("tesdata saved");
    }

    private static void buildSettingData(Game currentdata, String input) {
        String[] parts = input.split(SETTING_DELIMITER_PATTERN);
        String name;
        boolean shared = false;
        if (parts[0].startsWith("shared:")) {
            name = parts[0].substring(7);
            shared = true;
        } else {
            name = parts[0];
        }

        GameSetting setting = new GameSetting(shared, name, SettingTypes.valueOf(parts[1]), parts[2], (parts.length > 3 ? parts[3] : ""));
        switch (SettingTypes.valueOf(parts[1])) {
            case CHOISE: {
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 4; i < parts.length; i++) {
                    String key = parts[i].substring(0, parts[i].indexOf("="));
                    String value = parts[i].substring(parts[i].indexOf("=") + 1);
                    names.add(key);
                    values.add(value);
                }
                setting.setComboboxSelectNames(names);
                setting.setComboboxValues(values);
                break;
            }
            case NUMBER: {
                if (parts.length > 5) {
                    setting.setMinValue(Integer.valueOf(parts[4]));
                    setting.setMaxValue(Integer.valueOf(parts[5]));
                }
                break;
            }
        }
        currentdata.addSetting(setting);
    }

    private static Game BuildGameData(Game currentdata, String input) {
        if (input.startsWith("NAME=")) {
            currentdata.setGameName(input.substring(5));
        } else if (input.startsWith("LAUNCHMETHOD=")) {
            currentdata.setLaunchMethod(input.substring(13));
        } else if (input.startsWith("GUID=")) {
            currentdata.setGuid(input.substring(5));
        } else if (input.startsWith("LAUNCHPATTERN=")) {
            currentdata.setHostPattern(input.substring(14));
        } else if (input.startsWith("JOINPATTERN=")) {
            currentdata.setJoinPattern(input.substring(12));
        } else if (input.startsWith("REGENTRY=")) {
            currentdata.setRegEntry(input.substring(9));
        } else if (input.startsWith("EXE=")) {
            currentdata.setRelativeExePath(input.substring(4));
        } else if (input.startsWith("MAPPATH=")) {
            currentdata.setMapPath(input.substring(8));
        } else if (input.startsWith("MAPEXT=")) {
            currentdata.setMapExtension(input.substring(7));
        } else if (input.startsWith("InstantLaunchable")) {
            currentdata.setInstantLauncable(true);
        }
        return currentdata;
    }

    public static void loadLocalPaths() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(lpfilepath));

        } catch (FileNotFoundException ex) {
            System.out.println("Could not load localpaths");
            return;
        }

        //reading data
        Boolean done = false;
        String input;
        while (!done) {
            try {
                input = br.readLine();
                if (input == null) {
                    done = true;
                    continue;
                }
            } catch (IOException ex) {
                System.out.println("Could not load localpaths");
                return;
            }
            String tmp[] = input.split("=");
            if (tmp.length > 1) {
                String tmp2[] = tmp[1].split(";");
                localexecutablepath.put(tmp[0], tmp2[0]);
                if (tmp2.length > 1) {
                    localinstallpath.put(tmp[0], tmp2[1]);
                }
            }
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        System.out.println("localpaths loaded");
    }

    public static void saveLocalPaths() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(lpfilepath));
        } catch (Exception ex) {
            System.out.println("Could not save gamedatabase");
        }
        for (String gamename : localexecutablepath.keySet()) {
            String execpath = localexecutablepath.get(gamename);
            String installpath = localinstallpath.get(gamename);
            if (execpath.length() > 0) {
                pw.println(gamename + "=" + execpath + ";" + installpath);
                pw.flush();
            }
        }
        pw.flush();
        pw.close();
        System.out.println("localpaths saved");
    }
}
