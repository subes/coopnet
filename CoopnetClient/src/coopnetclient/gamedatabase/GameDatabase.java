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

import coopnetclient.*;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
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
    static final String lpfilepath = "data/localpaths";    

    static {
        IDtoGameName = new HashMap<String, String>();
        localexecutablepath = new HashMap<String, String>();
        localinstallpath = new HashMap<String, String>();
        isexperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
        load(null);
        loadLocalPaths();
    }
    //constants
    public final static int LAUNCHMETHOD_DIRECTPLAY = 1;
    public final static int LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY = 2;
    public final static int LAUNCHMETHOD_PARAMETERPASSING = 3;
    //fields
    private static HashMap<String, String> IDtoGameName;     // key is the ID    
    private static HashMap<String, String> localexecutablepath; //shud point to the exe/binary
    private static HashMap<String, String> localinstallpath; //shud point to the game basedir
    private static ArrayList<String> isexperimental;
    private static ArrayList<Game> gameData;
    public static int version = 0;

    public static boolean isBeta(String channelname) {
        return isexperimental.contains(channelname);
    }

    public static void reset() {
        version = 0;
        localexecutablepath = new HashMap<String, String>();
        localinstallpath = new HashMap<String, String>();
        IDtoGameName = new HashMap<String, String>();
        isexperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
    }

    private static int indexOfGame(String gamename) {
        for (int i = 0; i < gameData.size(); i++) {
            if (gameData.get(i).getGameName().equals(gamename)) {
                return i;
            }
        }
        return -1;
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

    public static int getDefPort(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getDefPort(modname);
    }

    public static String readRegistry(String fullpath) {
        if (Client.registryOK) {
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

    public static void setGameSettings(String gamename, String settings) {
        gameData.get(indexOfGame(gamename)).setGameSettings(settings);
    }

    public static String getGameSettings(String gamename, String modname) {
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
        return gameData.get(indexOfGame(gamename)).getRelativeExePath(modname);
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

    public static void setGameModes(String gamename, String allgamemodes) {
        gameData.get(indexOfGame(gamename)).setGameModes(allgamemodes);
    }

    public static String getGameModes(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGameModes(modname);
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
        return gameData.get(indexOfGame(gamename)).getRegEntry(modname);
    }

    public static void setLaunchMethod(String gamename, int launchmethod) {
        gameData.get(indexOfGame(gamename)).setLaunchMethod(launchmethod);
    }

    public static int getLaunchMethod(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        return new Integer(gameData.get(idx).getLaunchMethod(modname));
    }

    public static void setGuid(String gamename, String guid) {
        gameData.get(indexOfGame(gamename)).setGuid(guid);
    }

    public static String getGuid(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGuid(modname);
    }

    public static void save() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(datafilepath));
        } catch (Exception ex) {
            System.out.println("Could not save gamedatabase");
        }
        //save version
        pw.println((version) + "");

        for (Game eachgame : gameData) {
            //start symbol
            pw.println("{");
            String tmp;
            //save data
            pw.println("NAME=" + eachgame.getGameName());
            pw.flush();
            if (isBeta(eachgame.getGameName())) {
                pw.println("beta");
                pw.flush();
            }

            pw.println("ID=" + IDofGame(eachgame.getGameName()));
            pw.flush();

            pw.println("LAUNCHMETHOD=" + eachgame.getLaunchMethod(null));
            pw.flush();
            tmp = eachgame.getGuid(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("GUID=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getHostPattern(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("LAUNCHPATTERN=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getJoinPattern(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("JOINPATTERN=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getRegEntry(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("REGENTRY=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getRelativeExePath(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("EXE=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getGameModes(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("GAMEMODES=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getMapPath(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("MAPPATH=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getMapExtension(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("MAPEXT=" + tmp);
                pw.flush();
            }
            tmp = eachgame.getGameSettings(null);
            if (tmp != null && tmp.length() > 0) {
                pw.println("SETTINGS=" + tmp);
                pw.flush();
            }
            Integer tmp1 = eachgame.getDefPort(null);
            if (tmp1 != null) {
                pw.println("DEFPORT=" + tmp1);
                pw.flush();
            }
            //end symbol
            pw.println("}");
            pw.flush();
        }
        pw.close();
        System.out.println("game database saved");
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
            version = new Integer(br.readLine());
        } catch (IOException ex) {
            System.out.println("Could not load gamedatabase");
            return;
        }
    }

    public static void load(String gamename) {
        if (indexOfGame(gamename) != -1) { //dont load if already loaded

            return;
        }
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

        //read version
        try {
            version = new Integer(br.readLine());
        } catch (IOException ex) {
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

                    if (input.equals("{")) {
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

                    if (input.equals("[")) {
                        state = 2;
                        currentmod = new Game();
                        continue;
                    } else if (input.startsWith("beta")) {
                        beta = true;
                    } else if (input.startsWith("ID=")) {
                        _ID = input.substring(3);
                    } else if (input.startsWith("}")) {
                        //store data if needed
                        IDtoGameName.put(_ID, currentgame.getGameName());
                        if (currentgame.getGameName().equals(gamename)) {
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
                case 2: {
                    if (input.equals("]")) {
                        currentgame.addMod(currentmod);
                        currentmod = null;
                        state = 1;
                        continue;
                    } else {
                        BuildGameData(currentmod, input);
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

    private static Game BuildGameData(Game currentdata, String input) {
        if (input.startsWith("NAME=")) {
            currentdata.setGameName(input.substring(5));
        } else if (input.startsWith("LAUNCHMETHOD=")) {
            currentdata.setLaunchMethod(new Integer(input.substring(13)));
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
        } else if (input.startsWith("GAMEMODES=")) {
            currentdata.setGameModes(input.substring(10));
        } else if (input.startsWith("MAPPATH=")) {
            currentdata.setMapPath(input.substring(8));
        } else if (input.startsWith("MAPEXT=")) {
            currentdata.setMapExtension(input.substring(7));
        } else if (input.startsWith("SETTINGS=")) {
            currentdata.setGameSettings(input.substring(9));
        } else if (input.startsWith("DEFPORT=")) {
            currentdata.setDefPort(input.substring(8));
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
