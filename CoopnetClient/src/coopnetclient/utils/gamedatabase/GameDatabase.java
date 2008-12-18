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

import coopnetclient.*;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.MapLoaderTypes;
import coopnetclient.enums.OperatingSystems;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class GameDatabase {

    public static final String dataFilePath = Globals.getResourceAsString("data/gamedata.xml");
    private static final String localPathsFilePath = Globals.getResourceAsString("data/localpaths");
    private static boolean registryOK = false;
    protected static HashMap<String, String> IDtoGameName;     // key is the ID    
    protected static HashMap<String, LaunchMethods> IDtoLaunchMethod;     // key is the ID    
    private static HashMap<String, String> localExecutablePath; //shud point to the exe/binary
    private static HashMap<String, String> localInstallPath; //shud point to the game basedir
    protected static ArrayList<String> isExperimental;
    protected static ArrayList<Game> gameData;
    public static int version = 0;
    private static XMLReader xmlReader = new XMLReader();
    private static Vector<String> installedGameIDs ;
    

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
        IDtoLaunchMethod = new HashMap<String, LaunchMethods>();
        localExecutablePath = new HashMap<String, String>();
        localInstallPath = new HashMap<String, String>();
        isExperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
        installedGameIDs = new  Vector<String>();
        load("", dataFilePath);
        loadLocalPaths();
    }

    public static void ClearInstalledGameList() {
        installedGameIDs.clear();
    }

    public static void addIDToInstalledList(String ID) {
        installedGameIDs.add(ID);
    }

    public static Vector<String> getInstalledGameNames() {
        Vector<String> data = new Vector<String>();
        for (String ID : installedGameIDs) {
            data.add(getGameName(ID));
        }
        for (String name : localExecutablePath.keySet()) {
            if(!data.contains(name)){
                data.add(name);
            }
        }

        return data;
    }

    public static boolean isBeta(String channelname) {
        return isExperimental.contains(channelname);
    }

    public static boolean isInstantLaunchable(String gamename) {
        return gameData.get(indexOfGame(gamename)).isInstantLaunchable(localPathsFilePath);
    }

    public static void reset() {
        version = 0;
        localExecutablePath = new HashMap<String, String>();
        localInstallPath = new HashMap<String, String>();
        IDtoGameName = new HashMap<String, String>();
        isExperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
    }

    protected static int indexOfGame(String gamename) {
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
        int idx = indexOfGame(gamename);
        if (idx > -1) {
            return gameData.get(idx);
        } else {
            return null;
        }
    }

    public static Object[] getGameModNames(String gamename) {
        return gameData.get(indexOfGame(gamename)).getAllModNames();
    }

    public static String getModByIndex(String gamename, int modindex) {
        String modname = null;
        if (modindex > 0) {
            modname = GameDatabase.getGameModNames(gamename)[Integer.valueOf(modindex)].toString();
        }
        return modname;
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
    
    public static String readRegistry(ArrayList<String> regkeys) {
        if(regkeys == null){
            return null;
        }
        for(String key :regkeys ){
            String path = readRegistry(key);
            if(path != null){
                return path;
            }
        }
        return null;
    }

    public static boolean getNoSpacesFlag(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getNoSpacesFlag(modname);
    }

    public static ArrayList<GameSetting> getGameSettings(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGameSettings(modname);
    }
    
    public static String getMapPath(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getMapPath(modname);
    }

    public static MapLoaderTypes getMapLoaderType(String gamename, String modname) {
        return MapLoaderTypes.valueOf(gameData.get(indexOfGame(gamename)).getMapLoaderType(modname));
    }

    public static String getMapExtension(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getMapExtension(modname);
    }

    public static String getRelativeExePath(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx == -1) {
            return null;
        }
        return gameData.get(idx).getRelativeExePath(modname);
    }

    public static String getLocalExecutablePath(String gamename) {
        return localExecutablePath.get(gamename);
    }

    public static String getLocalInstallPath(String gamename) {
        return localInstallPath.get(gamename);
    }

    public static void setLocalExecutablePath(String gamename, String path) {
        localExecutablePath.put(gamename, path);
    }

    public static void setLocalInstallPath(String gamename, String path) {
        localInstallPath.put(gamename, path);
    }

    public static Object[] getAllGameNames() {
        return IDtoGameName.values().toArray(new String[0]);
    }
    
    public static String[] getNonDPlayGameNames() {
        ArrayList<String> games = new ArrayList<String>();
        for(String ID:IDtoGameName.keySet()){
            LaunchMethods m = IDtoLaunchMethod.get(ID);
            if(  m!= null && m.equals(LaunchMethods.PARAMETER)){
                games.add(IDtoGameName.get(ID));
            }
        }
        return games.toArray(new String[games.size()]);
    }

    public static String[] getAllGameNamesAsStringArray() {
        return IDtoGameName.values().toArray(new String[0]);
    }

    public static String getHostPattern(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getHostPattern(modname);
    }

    public static String  getHostPasswordPattern(String gamename, String pattern) {
        return gameData.get(indexOfGame(gamename)).getHostPasswordPattern(pattern);
    }
    
    public static String  getWelcomeMessage(String gamename) {
        return gameData.get(indexOfGame(gamename)).getWelcomeMessage();
    }

    public static String  getJoinPasswordPattern(String gamename, String pattern) {
        return gameData.get(indexOfGame(gamename)).getJoinPasswordPattern(pattern);
    }

    public static String getJoinPattern(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getJoinPattern(modname);
    }

    public static String getPK3FindPath(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getPK3FindPath(modname);
    }

    public static ArrayList<String> getRegEntry(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx == -1) {
            return null;
        }
        return gameData.get(idx).getRegEntries(modname);
    }

    public static LaunchMethods getLaunchMethod(String gamename, String modname) {
        int idx = indexOfGame(gamename);
        if (idx > -1) {
            return gameData.get(idx).getLaunchMethod(modname);
        } else {
            return LaunchMethods.CHAT_ONLY;
        }
    }

    public static String getGuid(String gamename, String modname) {
        return gameData.get(indexOfGame(gamename)).getGuid(modname);
    }

    public static boolean isLaunchable(String gamename) {
        String test = null;

        if (getLaunchMethod(gamename, null) == LaunchMethods.DIRECTPLAY || getLaunchMethod(gamename, null) == LaunchMethods.DIRECTPLAY_FORCED_COMPATIBILITY) {
            return true;
        }
        test = getLaunchPathWithExe(gamename, null);
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
                        path = path + (( path.endsWith("\\") || path.endsWith("/") )?"":"/")+ tmp;
                    }
                }
                break;
        }
        return path;
    }

    public static void loadVersion() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(dataFilePath));
        } catch (FileNotFoundException ex) {
            //ex.printStackTrace();            
            System.out.println("Could not load gamedatabase");
            return;
        }
        //read version
        try {
            // looks like: <!--Version 10 -->
            br.readLine();
            String[] parts = br.readLine().split(" ");
            version = new Integer(parts[1]);
        } catch (IOException ex) {
            //ex.printStackTrace();
            version = 0;
            System.out.println("Could not load gamedatabase");
            return;
        }
    }

    public static synchronized void load(String gamename, String datafilepath) {
        try {
            System.out.println("loading from:"+datafilepath);
            xmlReader.parseGameData(gamename, datafilepath,XMLReader.LOAD_GAMEDATA);
            System.out.println("game database loaded");  
        }
        catch (Exception e) {
            System.out.println("game database loading failed!");
            e.printStackTrace();
        }
    }

    public static synchronized void detectGames() {
        ClearInstalledGameList();        
        try {
            xmlReader.parseGameData(null, dataFilePath,XMLReader.DETECT_GAMES);
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public static void loadLocalPaths() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(localPathsFilePath));

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
                localExecutablePath.put(tmp[0], tmp2[0]);
                if (tmp2.length > 1) {
                    localInstallPath.put(tmp[0], tmp2[1]);
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
            pw = new PrintWriter(new FileWriter(localPathsFilePath));
        } catch (Exception ex) {
            System.out.println("Could not save gamedatabase");
        }
        for (String gamename : localExecutablePath.keySet()) {
            String execpath = localExecutablePath.get(gamename);
            String installpath = localInstallPath.get(gamename);
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
