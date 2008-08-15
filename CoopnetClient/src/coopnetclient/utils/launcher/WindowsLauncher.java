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

package coopnetclient.utils.launcher;

import coopnetclient.*;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.clientframe.panels.RoomPanel;
import coopnetclient.utils.gamedatabase.GameDatabase;
import coopnetclient.utils.gamedatabase.GameSetting;
import java.io.IOException;
import java.util.ArrayList;
import jdplay.JDPlay;

public class WindowsLauncher implements OLDLauncher {

    private JDPlay jDPlay = null;
    private boolean dplayIsInitialized = false;
    private boolean launcherInitialised = false ;
    private boolean isHost;
    private LaunchMethods launchMethod;
    private String ip;
    private String gameIdentifier;
    private boolean compatible;
    private boolean compatibleForced;
    private String map;
    private ArrayList<GameSetting> settings;
    private String modName;
    

    public WindowsLauncher() {
    }

    @Override
    public void initialize(String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        launcherInitialised= false;
        if (ip != null && ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        this.ip = ip;

        settings = GameDatabase.getGameSettings(gameIdentifier, modname);
        //reset settings
        for(GameSetting setting :settings){
            setting.reset();
        }
        this.modName = modname;
        this.compatible = compatible;
        this.launchMethod = GameDatabase.getLaunchMethod(gameIdentifier, modname);
        this.isHost = isHost;
        map=null;
        
        switch (launchMethod) {
            case DIRECTPLAY: {
                compatibleForced = false;
                this.gameIdentifier = GameDatabase.getGuid(gameIdentifier, modname);
                initDPlay();
                break;
            }
            case DIRECTPLAY_FORCED_COMPATIBILITY: {
                compatibleForced = true;
                this.gameIdentifier = GameDatabase.getGuid(gameIdentifier, modname);
                initDPlay();
                break;
            }
            case PARAMETER: {
                this.gameIdentifier = gameIdentifier;
                //isInitialized = true;
                if (!isHost) {
                    RoomPanel currentroom = TabOrganizer.getRoomPanel();
                    if (currentroom != null) {
                        currentroom.enableButtons();
                    }
                }
            }
        }
        launcherInitialised= true;
        //call it here to NOT remember settings
        RoomPanel currentroom = TabOrganizer.getRoomPanel();
        if (currentroom != null) {
            currentroom.showSettings();
        }
    }

    private void initDPlay() {
        try {
            if (dplayIsInitialized) {
                stopDPlay();
            }

            jDPlay = new JDPlay(Globals.getThisPlayer_inGameName(), Globals.JDPLAY_MAXSEARCHRETRIES, coopnetclient.modules.Settings.getDebugMode());

            boolean isInitialized = jDPlay.initialize(gameIdentifier, ip, isHost);
            
            if (isInitialized) {
                dplayIsInitialized = true;
                RoomPanel currentroom = TabOrganizer.getRoomPanel();
                if (currentroom != null) {
                    currentroom.enableButtons();
                }
            } else {
                jDPlay.delete();
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "DirectPlay error!", ChatStyles.SYSTEM);
            }
        } catch (UnsatisfiedLinkError e) {
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM", "DirectPlay error, you miss the JDPlay dll.", ChatStyles.SYSTEM);
            e.printStackTrace();
        }
    }

    private boolean launchDPlay() {
        if (compatible || compatibleForced) {
            return jDPlay.launch(true);
        } else {
            return jDPlay.launch(false);
        }
    }

    private boolean launchParam(String gamename) {
        String callerstring = null;
        callerstring = GameDatabase.getLaunchPathWithExe(gamename,modName);
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
            callerstring = callerstring.replace("{"+gs.getKeyWord()+"}",gs.getValue());
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
        if (jDPlay != null) {
            jDPlay.delete();
            dplayIsInitialized = false;
        }
    }

    @Override
    public void setIngameName(String name) {
        jDPlay.updatePlayerName(name);
    }

    @Override
    public boolean launch() {

        switch (launchMethod) {
            case DIRECTPLAY:
            case DIRECTPLAY_FORCED_COMPATIBILITY:
                if (dplayIsInitialized) {
                    return launchDPlay();
                }
                break;
            case PARAMETER:
                return launchParam(gameIdentifier);
        }
        return false;
    }

    @Override
    public void stop() {
        if (dplayIsInitialized) {
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
    public String getMod() {
        return modName;
    }

    @Override
    public void setMod(String newmod) {
        modName = newmod;
    }

    @Override
    public boolean isInitialised() {
        return launcherInitialised;
    }
    
    @Override
    public void setSetting(String settingname, String value, boolean broadcast){
        for(GameSetting setting :settings){
            if(setting.getName().equals(settingname)){
                setting.setValue(value,isHost && broadcast);
                return;
            }
        }
    }
    
    @Override
    public String getSetting(String settingName) {
        for(GameSetting gs : settings){
            if(gs.getName().equals(settingName)){
                return gs.getValue();
            }
        }        
        return null;
    }

    @Override
    public String getGameName() {
        return gameIdentifier;
    }
}
