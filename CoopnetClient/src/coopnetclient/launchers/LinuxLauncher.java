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

import coopnetclient.launchers.handlers.DPlayExeHandler;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.*;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.io.IOException;
import java.util.ArrayList;

public class LinuxLauncher implements Launcher {

    private boolean dPlayIsInitialized = false;
    private boolean launcherIsInitialised = false ;
    private boolean isHost;
    private int launchMethod;
    private String ip;
    private String gameIdentifier;
    private String modName;
    private boolean compatible;
    private String map;
    private DPlayExeHandler dplay;
    private ArrayList<GameSetting> settings;
    
    @Override
    public void initialize(String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        launcherIsInitialised = false;
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.modName = modname;
        settings = GameDatabase.getGameSettings(gameIdentifier, modname);
        this.ip = ip;
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
                dPlayIsInitialized = true;
                if (!isHost) {
                    if(TabOrganizer.getRoomPanel() != null){
                        TabOrganizer.getRoomPanel().enableButtons();
                    }
                }
        }
        launcherIsInitialised = true;
        //call it here to NOT remember settings
        if(TabOrganizer.getRoomPanel() != null){
            TabOrganizer.getRoomPanel().showSettings();
        }
    }

    private void initDPlay() {
        if (dPlayIsInitialized) {
            stopDPlay();
        }

        dplay = new DPlayExeHandler(gameIdentifier, modName, isHost, ip, compatible);

        if (dplay.isInitialized) {
            dPlayIsInitialized = true;
            if (TabOrganizer.getRoomPanel() != null) { //May be null if user closes room too fast
                TabOrganizer.getRoomPanel().enableButtons();
            }
        } else {
            stopDPlay();
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "DirectPlay error!", 
                    coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
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
        callerstring = callerstring.replace("{NAME}", Globals.getThisPlayer_inGameName());
        if (map != null) {
            callerstring = callerstring.replace("{MAP}", map);
        }
        //replace settings text with actual values
        for(GameSetting gs:settings){
            callerstring = callerstring.replace(gs.getKeyWord(),gs.getValue());
        }
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
            dPlayIsInitialized = false;
        }
    }

    @Override
    public void setIngameName(String name) {
        dplay.setPlayerName(name);
    }

    @Override
    public boolean launch() {
        switch (launchMethod) {
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY:
            case GameDatabase.LAUNCHMETHOD_DIRECTPLAY_FORCED_COMPATIBILITY:
                if(dPlayIsInitialized){
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
        if(dPlayIsInitialized){
            stopDPlay();
        }
    }

    @Override
    public void setMap(String map) {
        this.map = map;
    }

    @Override
    public String getMap() {
        return this.map;
    }

    @Override
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

    @Override
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

    @Override
    public String getExecutablePath(String gamename) {
        return GameDatabase.getLocalExecutablePath(gamename);
    }

    @Override
    public String getInstallPath(String gamename) {
        return GameDatabase.getLocalInstallPath(gamename);
    }

    @Override
    public String getMod() {
        return modName;
    }

    @Override
      public void setMod(String newmod) {
        modName = newmod;
    }
    
    @Override
    public boolean isInitialised() {
        return  launcherIsInitialised;
    }
    
    @Override
    public void setSetting(String settingname, String value){
        for(GameSetting setting :settings){
            if(setting.getName().equals(settingname)){
                setting.setValue(value);
                return;
            }
        }
    }
}
