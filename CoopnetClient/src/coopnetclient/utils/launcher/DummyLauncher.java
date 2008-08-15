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

import coopnetclient.frames.clientframe.TabOrganizer;

public class DummyLauncher implements OLDLauncher {

    String gamename;

    @Override
    public void initialize(String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        if(TabOrganizer.getRoomPanel() != null){
            TabOrganizer.getRoomPanel().enableButtons();
        }
        this.gamename = gameIdentifier;
    }

    @Override
    public boolean launch() {
        try {
            Thread.sleep(10000);
            return true;
        } catch (InterruptedException ex) {
        }
        return true;
    }
    /*    public void stop() {
    throw new UnsupportedOperationException("Not supported yet.");
    }
     */

    @Override
    public void setIngameName(String name) {
    }

    @Override
    public void setMap(String map) {
    }
   
    @Override
    public String getMap() {
        return "";
    }

    @Override
    public String getMod() {
        return "";
    }

    @Override
    public void setMod(String mod) {
    }

   
    @Override
    public void stop() {
    }

    public boolean isInitialised() {
        return true;
    }
     @Override
    public void setSetting(String settingname, String value, boolean broadcast){
     
     }

    public String getGameName() {
      return gamename;
    }

    @Override
    public String getSetting(String settingName) {
        return null;
    }
}
