/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.launcher;

import coopnetclient.gamedatabase.GameDatabase;
import coopnetclient.*;
import java.io.IOException;

public class LinuxLauncher implements Launcher {

    private boolean isInitialized = false;
    private boolean isHost;
    private int launchMethod;
    private String ip;
    private String gameIdentifier;
    private String modName;
    private boolean compatible;
    private int maxPlayers;
    private String gameMode;
    private String map;
    private int timeLimit = 0;
    private int port;
    private int bots = 0;
    private int goalScore = 0;
    private DPlayExeHandler dplay;

    public void initialize(String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.modName = modname;

        this.ip = ip;
        this.port = GameDatabase.getDefPort(gameIdentifier, modname);
        this.maxPlayers = maxPlayers == 0 ? 99 : maxPlayers;
        this.compatible = compatible;
        this.launchMethod = GameDatabase.getLaunchMethod(gameIdentifier, modname);
        this.isHost = isHost;
        this.gameIdentifier = gameIdentifier;

        switch (launchMethod) {
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY:
                this.compatible = true;
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY:
                initDPlay();
                break;
            case GameDatabase.LAUNCHMETHOD_PARAMETERPASSING:
                isInitialized = true;
                if (!isHost) {
                    Client.currentRoom.enableButtons();
                }
        }
        //call it here to NOT remember settings
        Client.currentRoom.showSettings();
    }

    private void initDPlay() {
        if (isInitialized) {
            stopDPlay();
        }

        dplay = new DPlayExeHandler(gameIdentifier, modName, isHost, ip, compatible);

        if (dplay.isInitialized) {
            isInitialized = true;
            if (Client.currentRoom != null) { //May be null if user closes room too fast
                Client.currentRoom.enableButtons();
            }
        } else {
            stopDPlay();
            Client.mainFrame.printToVisibleChatbox("SYSTEM",
                    "DirectPlay error!", 
                    coopnetclient.coloring.ColoredChatHandler.SYSTEM_STYLE);
        }
    }

    private boolean launchDPlay() {
        dplay.launch();
        return true;
    }

    private boolean launchParam(String gamename) {
        String callerstring = null;
        callerstring = getLaunchPathWithExe(gamename);
        if (callerstring == null) {
            return false;
        }
        callerstring += " ";
        if (this.isHost) {
            callerstring += GameDatabase.getHostPattern(gamename, modName);
        } else {
            callerstring += GameDatabase.getJoinPattern(gamename, modName);
        }
        //insert data into pattern
        callerstring = callerstring.replace("{HOSTIP}", ip);
        callerstring = callerstring.replace("{NAME}", Client.inGameName);
        callerstring = callerstring.replace("{MAXPLAYERS}", maxPlayers + "");
        if (map != null) {
            callerstring = callerstring.replace("{MAP}", map);
        }
        if (gameMode != null) {
            callerstring = callerstring.replace("{GAMEMODE}", gameMode);
        }
        callerstring = callerstring.replace("{PORT}", port + "");
        callerstring = callerstring.replace("{BOTS}", bots + "");
        callerstring = callerstring.replace("{GOALSCORE}", goalScore + "");
        callerstring = callerstring.replace("{TIMELIMIT}", timeLimit + "");
        //other fields to parse?        

        System.out.println(callerstring);

        //run
        Process p = null;
        try {
            Runtime rt = Runtime.getRuntime();
            p = rt.exec(callerstring);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return (p.exitValue() == 0 ? true : false);
    }

    public void stopDPlay() {
        if (dplay != null) {
            dplay.stopDPlay();
        }
    }

    public void setIngameName(String name) {
        dplay.setPlayerName(name);
    }

    public boolean launch() {
        boolean launched = false;
        switch (launchMethod) {
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY:
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY:
                launched = launchDPlay();
                break;
            case GameDatabase.LAUNCHMETHOD_PARAMETERPASSING:
                launched = launchParam(gameIdentifier);
        }
        return launched;
    }

    public void stop() {
        stopDPlay();
    }

    public void setGameMode(String mode) {
        gameMode = mode;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setTimelimit(int limit) {
        timeLimit = limit;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getMap() {
        return this.map;
    }

    public int getTimelimit() {
        return timeLimit;
    }

    public int getPort() {
        return this.port;
    }

    public String getFullMapPath(String gamename) {
        String tmp = GameDatabase.getLocalInstallPath(gamename);
        if (tmp == null || tmp.length() == 0) {
            return null;
        }
        String relativemappath = GameDatabase.getMapPath(gamename, modName);
        relativemappath = relativemappath.replace('\\', '/');
        if (!tmp.endsWith("/")) {
            tmp += "/";
        }
        tmp = tmp + relativemappath;
        return tmp;
    }

    private String getLaunchPathWithExe(String gamename) {
        //user should be able to give the correct executable , no checking
        String path = GameDatabase.getLocalExecutablePath(gamename);
        return path;
    }

    public boolean isLaunchable(String gamename) {
        String test = null;
        if (GameDatabase.getLaunchMethod(gamename, modName) == GameDatabase.LAUNCHMETHOD_DIRECTPLAY 
                || GameDatabase.getLaunchMethod(gamename, modName) == GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY) {
            return true;
        }
        test = getLaunchPathWithExe(gamename);
        if (test != null && test.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getExecutablePath(String gamename) {
        return GameDatabase.getLocalExecutablePath(gamename);
    }

    public String getInstallPath(String gamename) {
        return GameDatabase.getLocalInstallPath(gamename);
    }

    public String getMod() {
        return modName;
    }

    public void setMod(String newmod) {
        modName = newmod;
    }

    public int getBots() {
        return bots;
    }

    public void setBots(int bots) {
        this.bots = bots;
    }

    public int getGoalScore() {
        return goalScore;
    }

    public void setGoalScore(int score) {
        goalScore = score;
    }
}
