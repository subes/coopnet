/*  Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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

import coopnetclient.Globals;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.MapLoaderTypes;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RegistryReader;
import coopnetclient.utils.settings.SettingsHelper;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class GameDatabase {

    public static final String dataFilePath = Globals.getResourceAsString("data/gamedata.xml");
    private static String localPathsFilePath;
    protected static HashMap<String, String> IDtoGameName;     // key is the ID    
    protected static HashMap<String, LaunchMethods> IDtoLaunchMethod;     // key is the ID    
    private static HashMap<String, String> localExecutablePath; //should point to the exe/binary
    private static HashMap<String, String> localInstallPath; //should point to the game basedir
    private static HashMap<String, String> additionalParameters; //parameters to add on launch
    protected static ArrayList<String> isExperimental;
    protected static ArrayList<Game> gameData;
    public static int version = 0;
    private static XMLReader xmlReader = new XMLReader();
    private static Vector<String> installedGameIDs;

    public static void init() {
        localPathsFilePath = SettingsHelper.getSettingsDir() + "/localpaths";

        additionalParameters = new HashMap<String, String>();
        IDtoGameName = new HashMap<String, String>();
        IDtoLaunchMethod = new HashMap<String, LaunchMethods>();
        localExecutablePath = new HashMap<String, String>();
        localInstallPath = new HashMap<String, String>();
        isExperimental = new ArrayList<String>();
        gameData = new ArrayList<Game>();
        installedGameIDs = new Vector<String>();
        load("", dataFilePath);
        loadLocalPaths();
    }

    public static void setAdditionalParameters(String ID, String params) {
        if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
            String regKey = GameDatabase.getRegEntry(getGameName(ID), null).get(0);
            int idx = regKey.lastIndexOf("\\");
            String rootRegKey = regKey.substring(0, idx);
            RegistryReader.write(rootRegKey, "CommandLine", params);
        } else {
            additionalParameters.put(ID, params);
        }
    }

    public static String getAdditionalParameters(String ID) {
        if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
            for (String regKey : GameDatabase.getRegEntry(getGameName(ID), null)) {
                String installPath = RegistryReader.read(regKey);
                if (installPath != null) {
                    int idx = regKey.lastIndexOf("\\");
                    String commandLineRegKey = regKey.substring(0, idx) + "\\CommandLine";
                    String commandLine = RegistryReader.read(commandLineRegKey);
                    return commandLine;
                }
            }
            return "";
        }
        return additionalParameters.get(ID);
    }

    public static void clearInstalledGameList() {
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
        for (String ID : localExecutablePath.keySet()) {
            final String gameName = getGameName(ID);
            if (gameName != null && !data.contains(gameName)) {
                data.add(gameName);
            }
        }
        
        Collections.sort(data);
        return data;
    }

    public static boolean isBeta(String ID) {
        return isExperimental.contains(ID);
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
        additionalParameters = new HashMap<String, String>();
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

    public static String getShortName(String gamename) {
        return gameData.get(indexOfGame(gamename)).getShortName();
    }

    public static String getIDofGame(String gamename) {
        String tmp = "";
        for (String s : IDtoGameName.keySet()) {
            tmp = IDtoGameName.get(s);
            if (tmp.equals(gamename)) {
                return s;
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

    public static int getServerSettingCount(String gamename, String modname) {
        int i = 0;
        for (GameSetting gs : gameData.get(indexOfGame(gamename)).getGameSettings(modname)) {
            if (!gs.isLocal()) {
                ++i;
            }
        }
        return i;
    }

    public static int getLocalSettingCount(String gamename, String modname) {
        int i = 0;
        for (GameSetting gs : gameData.get(indexOfGame(gamename)).getGameSettings(modname)) {
            if (gs.isLocal()) {
                ++i;
            }
        }
        return i;
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

    public static String getLocalExecutablePath(String ID) {
        return localExecutablePath.get(ID);
    }

    public static String getLocalInstallPath(String ID) {
        return localInstallPath.get(ID);
    }

    public static void setLocalExecutablePath(String ID, String path) {
        if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
            String regKey = getRegEntry(getGameName(ID), null).get(0);
            int idx = regKey.lastIndexOf("\\");
            String rootRegKey = regKey.substring(0, idx);
            idx = path.lastIndexOf(File.separator);
            String installdir = path.substring(0, idx);
            String exeName = path.substring(idx + 1);
            RegistryReader.write(rootRegKey, "File", exeName);
            RegistryReader.write(rootRegKey, "Path", installdir);
            RegistryReader.write(rootRegKey, "Guid", getGuid(getGameName(ID), null));
        } else {
            localExecutablePath.put(ID, path);
        }
    }

    public static void setLocalInstallPath(String ID, String path) {
        if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
            String regKey = GameDatabase.getRegEntry(getGameName(ID), null).get(0);
            int idx = regKey.lastIndexOf("\\");
            String rootRegKey = regKey.substring(0, idx);
            RegistryReader.write(rootRegKey, "CurrentDirectory", path);
        } else {
            localInstallPath.put(ID, path);
        }
    }

    public static String[] getAllGameNames() {
        return IDtoGameName.values().toArray(new String[0]);
    }

    public static String[] getNonDPlayGameNames() {
        ArrayList<String> games = new ArrayList<String>();
        for (String ID : IDtoGameName.keySet()) {
            LaunchMethods m = IDtoLaunchMethod.get(ID);
            if (m != null && (m.equals(LaunchMethods.PARAMETER) || m.equals(LaunchMethods.DOS))) {
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

    public static String getHostPasswordPattern(String gamename, String pattern) {
        return gameData.get(indexOfGame(gamename)).getHostPasswordPattern(pattern);
    }

    public static String getWelcomeMessage(String gamename) {
        return gameData.get(indexOfGame(gamename)).getWelcomeMessage();
    }

    public static String getJoinPasswordPattern(String gamename, String pattern) {
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

    public static LaunchMethods getLaunchMethod(String ID) {
        return IDtoLaunchMethod.get(ID);
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
        String ID = getIDofGame(gamename);

        if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
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
        //path set by manage frame is pimary
        String path = getLocalInstallPath(getIDofGame(gamename));

        if (path != null && path.length() > 0) {
            return path;
        }

        if (getLaunchMethod(getIDofGame(gamename)) == LaunchMethods.DIRECTPLAY) {
            for (String regKey : GameDatabase.getRegEntry(gamename, null)) {
                String installPath = RegistryReader.read(regKey);
                if (installPath != null) {
                    //TODO remove trailing seperator
                    return installPath;
                }
            }
            return "";
        }

        //if not overridden try to detect it
        String exepath = getLaunchPathWithExe(gamename, null);
        //requires the gamedata to be loaded
        //TODO rethink this
        String relativexepath = GameDatabase.getRelativeExePath(gamename, null);
        if (exepath != null && relativexepath != null && exepath.endsWith(relativexepath)) {
            return exepath.substring(0, exepath.length() - relativexepath.length());
        } else {
            return exepath;
        }
    }

    public static String getFullMapPath(String gamename, String modName) {
        String tmp = getInstallPath(gamename);
        if (tmp == null || tmp.length() == 0) {
            return null;
        }
        String mappath = GameDatabase.getMapPath(gamename, modName);
        //add path seperator if needed
        if (!tmp.endsWith(File.separator) || !mappath.startsWith(File.separator)) {
            tmp += File.separator;
        }
        tmp += mappath;
        return tmp;
    }

    public static String getLaunchPathWithExe(String gamename, String modName) {
        String path = "";
        String ID = getIDofGame(gamename);
        //path set in manage frame is primary
        path = GameDatabase.getLocalExecutablePath(ID);

        //if its not overridden try detecting
        if (path == null || (path != null && path.length() == 0)) {

            if (getLaunchMethod(ID) == LaunchMethods.DIRECTPLAY) {
                for (String regKey : GameDatabase.getRegEntry(gamename, modName)) {
                    String installPath = RegistryReader.read(regKey);
                    //TODO remove trailing seperator
                    if (installPath != null) {
                        int idx = regKey.lastIndexOf("\\");
                        String fileNameRegKey = regKey.substring(0, idx) + "\\File";
                        String exeName = RegistryReader.read(fileNameRegKey);
                        return installPath + File.separator + exeName;
                    }
                }
                return "";
            }

            path = RegistryReader.readAny(GameDatabase.getRegEntry(gamename, modName));
            if (path == null) {
                return "";
            }
            String relativexepath = GameDatabase.getRelativeExePath(gamename, null);
            if (relativexepath == null || relativexepath.length() == 0) {
                return "";
            }
            if (path.endsWith(relativexepath)) {
                return path;
            } else {
                if (!path.endsWith(File.separator) || !relativexepath.startsWith(File.separator)) {
                    path += File.separator;
                }
                return path + relativexepath;
            }
        }
        return path;
    }

    public static void loadVersion() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(dataFilePath));
        } catch (FileNotFoundException ex) {
            Logger.log(LogTypes.ERROR, "Could not load gamedatabase");
            Logger.log(ex);
            return;
        }
        //read version
        try {
            // looks like: <!--Version 10 -->
            br.readLine();
            String[] parts = br.readLine().split(" ");
            version = new Integer(parts[1]);
        } catch (IOException ex) {
            version = 0;
            Logger.log(LogTypes.ERROR, "Could not load gamedatabase");
            Logger.log(ex);
            return;
        }
    }

    public static synchronized void load(String ID, String datafilepath) {
        try {
            Logger.log(LogTypes.LOG, "Loading gamedata from:" + datafilepath);
            xmlReader.parseGameData(getGameName(ID), datafilepath, XMLReader.LOAD_GAMEDATA);
            Logger.log(LogTypes.LOG, "Game database loaded");
        } catch (Exception e) {
            Logger.log(LogTypes.ERROR, "Game database loading failed!");
            Logger.log(e);
        }
    }

    public static synchronized void detectGames() {
        clearInstalledGameList();
        try {
            xmlReader.parseGameData(null, dataFilePath, XMLReader.DETECT_GAMES);
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public static void loadLocalPaths() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(localPathsFilePath));

        } catch (FileNotFoundException ex) {
            Logger.log(LogTypes.ERROR, "Could not load localpaths");
            return;
        }

        //reading data
        Boolean done = false;
        String input;
        String currentID = "";
        while (!done) {
            try {
                input = br.readLine();
                if (input == null) {
                    done = true;
                    continue;
                }
            } catch (IOException ex) {
                Logger.log(LogTypes.ERROR, "Could not load localpaths");
                Logger.log(ex);
                return;
            }

            if (input.startsWith("GAME=")) {
                currentID = input.substring(5);
            }
            if (input.startsWith("EXEPATH=")) {
                localExecutablePath.put(currentID, input.substring(8));
            }
            if (input.startsWith("INSTPATH=")) {
                localInstallPath.put(currentID, input.substring(9));
            }
            if (input.startsWith("PARAMS=")) {
                additionalParameters.put(currentID, input.substring(7));
            }
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        Logger.log(LogTypes.LOG, "localpaths loaded");
    }

    public static void saveLocalPaths() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(localPathsFilePath));
        } catch (Exception ex) {
            Logger.log(LogTypes.ERROR, "Could not save gamedatabase");
            Logger.log(ex);
        }
        for (String ID : IDtoGameName.keySet()) {
            String execpath = localExecutablePath.get(ID);
            String installpath = localInstallPath.get(ID);
            String additionalParam = additionalParameters.get(ID);
            if ((execpath != null && execpath.length() > 0)
                    || (installpath != null && installpath.length() > 0)
                    || (additionalParam != null && additionalParam.length() > 0)) {
                pw.println("GAME=" + ID);
                pw.println("EXEPATH=" + (execpath == null ? "" : execpath));
                pw.println("INSTPATH=" + (installpath == null ? "" : installpath));
                pw.println("PARAMS=" + (additionalParam == null ? "" : additionalParam));
                pw.flush();
            }
        }
        pw.flush();
        pw.close();
        Logger.log(LogTypes.LOG, "localpaths saved");
    }
}
