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

public interface OLDLauncher {

    public void initialize(String gameIdentifier, String modname, final boolean isHost, String ip, boolean compatible, int maxplayers);

    public boolean isInitialised();

    public boolean launch();

    public void stop();

    public void setIngameName(String name);

    public void setMap(String map);

    public String getGameName();

    public String getMap();

    public String getMod();

    public void setMod(String mod);

    public boolean isLaunchable(String gamename);

    public String getExecutablePath(String gamename);

    public String getInstallPath(String gamename);

    public String getFullMapPath(String gamename);

    public void setSetting(String settingname, String value, boolean broadcastit);
    public String getSetting(String settingName);
}
