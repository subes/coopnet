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
package coopnetclient.utils.launcher.launchhandlers;

import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LogTypes;
import coopnetclient.frames.FrameOrganizer;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.io.File;

public class ParameterLaunchHandler extends LaunchHandler {

    private ParameterLaunchInfo launchInfo;

    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if (!(launchInfo instanceof ParameterLaunchInfo)) {
            throw new IllegalArgumentException("expected launchInfo to be " + ParameterLaunchInfo.class.toString() + ", but got " + launchInfo.getClass().toString());
        }

        this.launchInfo = (ParameterLaunchInfo) launchInfo;

        return true;
    }

    @Override
    protected boolean doLaunch() {

        FrameOrganizer.getClientFrame().printSystemMessage(
                "Launching game, please wait ...", false);

        if (FrameOrganizer.getGameSettingsFrame() != null && launchInfo.getRoomData().isHost()) {
            FrameOrganizer.getGameSettingsFrame().setEnabledOfGameSettingsFrameSettings(false);
        }

        Process p = null;
        try {
            String command = "";

            //TODO: add a method to gamedata which checks if the games wine checkbox is checked
            /*if(Globals.getOperatingSystem() == OperatingSystems.LINUX && GameDataBase.useWine(someargs to identify game)){
            command += Globals.getWineCommand();
            }*/

            command += " " + launchInfo.getBinaryPath() +
                    " " + launchInfo.getParameters();

            Logger.log(LogTypes.LAUNCHER, command);

            Runtime rt = Runtime.getRuntime();
            p = rt.exec(command, null, new File(launchInfo.getInstallPath()));

            try {
                p.exitValue();
                throw new Exception("Game exited too fast!"); //caught by outer catch
            } catch (IllegalStateException e) {
            }

            if (launchInfo.getRoomData().isHost() && !launchInfo.getRoomData().isInstant()) {
                Protocol.launch();
            }

            try {
                p.waitFor();
            } catch (InterruptedException ex) {
            }
        } catch (Exception e) {
            FrameOrganizer.getClientFrame().printSystemMessage(
                    "Error while launching: " + e.getMessage() + "\nAborting launch!", false);
            Logger.log(e);
            return false;
        }

        if (FrameOrganizer.getGameSettingsFrame() != null && launchInfo.getRoomData().isHost()) {
            FrameOrganizer.getGameSettingsFrame().setEnabledOfGameSettingsFrameSettings(true);
        }

        return (p.exitValue() == 0 ? true : false);
    }

    @Override
    public void updatePlayerName() {
        //do nothing
    }

    @Override
    public boolean predictSuccessfulLaunch() {
        File exec = new File(launchInfo.getBinaryPath());

        boolean ret = exec.exists() && exec.canExecute();

        if (!ret) {
            FrameOrganizer.getClientFrame().printSystemMessage(
                    "Launcher failed. Please make sure that the game is properly setup for launch.", false);
        }

        return ret;
    }
}
