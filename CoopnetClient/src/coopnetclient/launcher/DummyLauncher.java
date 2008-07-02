/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

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

import coopnetclient.*;

public class DummyLauncher extends Thread implements Launcher {

    String gamename;

    public void initialize(String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible, int maxPlayers) {
        Client.currentRoom.enableButtons();
        this.gamename = gameIdentifier;
    }

    public boolean launch() {
        try {
            sleep(10000);
            return true;
        } catch (InterruptedException ex) {
        }
        return true;
    }
    /*    public void stop() {
    throw new UnsupportedOperationException("Not supported yet.");
    }
     */

    public void setIngameName(String name) {
    }

    public void setGameMode(String mode) {
    }

    public void setMap(String map) {
    }

    public void setTimelimit(int limit) {
    }

    public void setPort(int port) {
    }

    public String getGameMode() {
        return "";
    }

    public String getMap() {
        return "";
    }

    public int getTimelimit() {
        return 0;
    }

    public int getPort() {
        return 0;
    }

    public boolean isLaunchable(String gamename) {
        return true;
    }

    public String getExecutablePath(String gamename) {
        return "";
    }

    public String getInstallPath(String gamename) {
        return "";
    }

    public String getFullMapPath(String gamename) {
        return "";
    }

    public String getMod() {
        return "";
    }

    public void setMod(String mod) {
    }

    public int getBots() {
        return 0;
    }

    public void setBots(int bots) {
    }

    public int getGoalScore() {
        return 0;
    }

    public void setGoalScore(int score) {
    }
}
