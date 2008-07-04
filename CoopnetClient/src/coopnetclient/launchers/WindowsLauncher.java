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

package coopnetclient.launchers;

import coopnetclient.*;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.io.IOException;
import jdplay.JDPlay;

public class WindowsLauncher implements Launcher {

    private JDPlay jDPlay = null;
    private boolean isInitialized = false;
    private boolean isHost;
    private int launchMethod;
    private String ip;
    private String gameIdentifier;
    private boolean compatible;
    private boolean compatibleForced;
    private int maxPlayers;
    private String gameMode;
    private String map;
    private String modName;
    private int timeLimit = 0;
    private int port;
    private int bots =0;
    private int goalScore = 0;

    public WindowsLauncher() {
    }

    @Override
    public void initialize(String gameIdentifier,String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.ip = ip;
               
        this.maxPlayers = maxPlayers==0?99:maxPlayers;
        this.compatible = compatible;
        this.launchMethod = GameDatabase.getLaunchMethod(gameIdentifier,modname);
        this.isHost = isHost;
        this.modName= modname;
		
        switch (launchMethod) {
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY: {
                compatibleForced = false;
                this.gameIdentifier = GameDatabase.getGuid(gameIdentifier,modname);
                initDPlay();
                break;
            }
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY: {
                compatibleForced = true;
                this.gameIdentifier = GameDatabase.getGuid(gameIdentifier,modname);
                initDPlay();
                break;
            }
            case GameDatabase.LAUNCHMETHOD_PARAMETERPASSING: {
                this.gameIdentifier = gameIdentifier;
                this.port = GameDatabase.getDefPort(gameIdentifier,modname); 
                isInitialized = true;
                if (!isHost) {
                    Globals.getRoomPanel().enableButtons();
                }
            }
        }
        //call it here to NOT remember settings
        Globals.getRoomPanel().showSettings();
    }

    private void initDPlay() {
        try {
            if (isInitialized) {
                stopDPlay();
            }

            jDPlay = new JDPlay(Globals.getThisPlayer_inGameName(), gameIdentifier, ip, isHost, coopnetclient.modules.Settings.getDebugMode());

            if (jDPlay.isInitializedProperly()) {
                    isInitialized = true;
                    jDPlay.setMaxSearchRetries(MAX_RETRIES);
                    Globals.getRoomPanel().enableButtons();
            } else {
                    jDPlay.delete();
                    Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "DirectPlay error!", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            }
        } catch (UnsatisfiedLinkError e) {
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "DirectPlay error, you miss the JDPlay dll.", coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
            e.printStackTrace();
        }
    }

    private boolean launchDPlay() {
        if(compatible || compatibleForced){
            return jDPlay.launch(true);
        }else{
            return jDPlay.launch(false);
        }
    }

    private boolean launchParam(String gamename) {
        String callerstring = null;
        callerstring = getLaunchPathWithExe(gamename);
        if (callerstring == null) {
            return false;
        }
        callerstring += " ";
        if (this.isHost) {
            callerstring += GameDatabase.getHostPattern(gamename,modName);
        } else {
            callerstring += GameDatabase.getJoinPattern(gamename,modName);
        }
        //insert data into pattern
        callerstring = callerstring.replace("{HOSTIP}", ip);
        callerstring = callerstring.replace("{NAME}", Globals.getThisPlayer_inGameName());

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
        if (jDPlay != null) {
            jDPlay.delete();
            isInitialized = false;
        }
    }

    @Override
    public void setIngameName(String name) {
        jDPlay.setPlayerName(name);
    }

    @Override
    public boolean launch() {

        switch (launchMethod) {
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY: 
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY: 
                if (isInitialized) {
                    return launchDPlay();
                }
                break;
            case GameDatabase.LAUNCHMETHOD_PARAMETERPASSING: 
                return launchParam(gameIdentifier);
        }
        return false;
    }

    @Override
    public void stop() {
        if (isInitialized) {
            stopDPlay();
        }
    }

    @Override
    public void setGameMode(String mode) {
        gameMode = mode;
    }

    @Override
    public void setMap(String map) {
        this.map = map;
    }

    @Override
    public void setTimelimit(int limit) {
        timeLimit = limit;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getGameMode() {
        return gameMode;
    }

    @Override
    public String getMap() {
        return this.map;
    }

    @Override
    public int getTimelimit() {
        return timeLimit;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getFullMapPath(String gamename) {
        String tmp = getLaunchPathWithExe(gamename);
        if (tmp == null || tmp.length() == 0) {
            return null;
        }
        String relativeexepath = GameDatabase.getRelativeExePath(gamename,modName);
        String mappath=  GameDatabase.getMapPath(gamename,modName);
        tmp = tmp.replace(relativeexepath,mappath);
        return tmp;
    }

    private String getLaunchPathWithExe(String gamename) {
        String path = "";

        path = GameDatabase.readRegistry(GameDatabase.getRegEntry(gamename,modName));

        //if its not detected try loading from local paths(given by user)
        if (path == null || (path != null && path.length() == 0)) {
            String tmp = GameDatabase.getLocalExecutablePath(gamename);
            if (tmp != null) {
                path = tmp;
            }
        }
        // make sure ret points to the exe
        if (path != null && path.length() > 0 && !path.endsWith(".exe")) {
            String tmp = GameDatabase.getRelativeExePath(gamename,modName);
            if (tmp != null) {
                path = path + tmp;
            }
        }
        return path;
    }

    @Override
    public boolean isLaunchable(String gamename) {
        String test = null;

        if (GameDatabase.getLaunchMethod(gamename,modName) == GameDatabase.LAUNCHMETHOD_DIRECTPLAY || GameDatabase.getLaunchMethod(gamename,modName) == GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY) {
            return true;
        }
        test = getLaunchPathWithExe(gamename);
        if (test != null && test.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getExecutablePath(String gamename) {
        return getLaunchPathWithExe(gamename);
    }

    @Override
    public String getInstallPath(String gamename) {
        String exepath = getLaunchPathWithExe(gamename);
        String relativexepath = GameDatabase.getRelativeExePath(gamename,modName);

        if (exepath != null && exepath.length() > 0 && relativexepath != null) {
            return exepath.substring(0, exepath.length() - relativexepath.length());
        } else {
            return "";
        }
    }

    @Override
    public String getMod() {
        return modName;
    }

    @Override
    public void setMod(String newmod) {
        modName= newmod;
    }

    @Override
    public int getBots() {
        return bots;
    }

    @Override
    public void setBots(int bots) {
        this.bots=bots;
    }

    @Override
    public int getGoalScore() {
        return goalScore;
    }

    @Override
    public void setGoalScore(int score) {
        goalScore= score;
    }
}
