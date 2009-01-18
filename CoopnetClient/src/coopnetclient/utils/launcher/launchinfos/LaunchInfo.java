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

package coopnetclient.utils.launcher.launchinfos;

public abstract class LaunchInfo {
    
    protected String gameName;
    protected String childName; //null, if original game
    protected String hostIP;
    protected boolean isHost;
    protected boolean isInstantLaunch;
    protected String password;
    protected String roomID;
    
    public LaunchInfo(String gameName, String childName, String hostIP, boolean isHost, boolean isInstantLaunch, String password, String roomID){
        this.gameName = gameName;
        this.childName = childName;
        this.hostIP = hostIP;
        this.isHost = isHost;
        this.isInstantLaunch = isInstantLaunch;
        this.password = password;
        this.roomID = roomID;
    }
    
    public String getGameName(){
        return gameName;
    }
    
    public String getModName(){
        return childName;
    }
    
    public String getHostIP(){
        return hostIP;
    }
    
    public boolean getIsHost(){
        return isHost;
    }
    
    public boolean getIsInstantLaunch(){
        return isInstantLaunch;
    }
    
    public String getRoomID(){
        return roomID;
    }
}
