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

public interface Launcher {
    
    public static final int maxRetries = 10;

    public void initialize(String gameIdentifier,String modname, final boolean isHost, String ip, boolean compatible, int maxplayers);
    
    public boolean launch();
    
    public void stop();
    
    public void setIngameName(String name);
    
    public void setGameMode(String mode);
    
    public void setMap(String map);
    
    public void setTimelimit(int limit);    
    
    public String getGameMode();
    
    public String getMap();
    
    public String getMod();
    
    public void setMod(String mod);
    
    public int getTimelimit();
    
    public int getPort();
    
    public void setPort(int port);
    
    public int getBots();
    
    public void setBots(int bots);
    
    public int getGoalScore();
    
    public void setGoalScore(int score);
    
    public boolean isLaunchable(String gamename);
    
    public String getExecutablePath(String gamename);
    
    public String getInstallPath(String gamename);
    
    public String getFullMapPath(String gamename);
}
