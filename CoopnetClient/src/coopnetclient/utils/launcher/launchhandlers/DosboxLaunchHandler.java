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
import coopnetclient.utils.launcher.launchinfos.DosboxLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import java.io.File;
import java.io.IOException;

public class DosboxLaunchHandler extends LaunchHandler {

    private DosboxLaunchInfo launchInfo;

    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if (!(launchInfo instanceof DosboxLaunchInfo)) {
            throw new IllegalArgumentException("expected launchInfo to be " + DosboxLaunchInfo.class.toString() + ", but got " + launchInfo.getClass().toString());
        }

        this.launchInfo = (DosboxLaunchInfo) launchInfo;

        return true;
    }

    @Override
    protected boolean doLaunch() {

        FrameOrganizer.getClientFrame().printToVisibleChatbox("SYSTEM",
                "Launching game, please wait ...",
                ChatStyles.SYSTEM, false);

        if (FrameOrganizer.getGameSettingsFrame() != null && launchInfo.getRoomData().isHost()) {
            FrameOrganizer.getGameSettingsFrame().setEnabledOfGameSettingsFrameSettings(false);
        }

        Process p = null;
        try {
            Runtime rt = Runtime.getRuntime();

            Logger.log(LogTypes.LAUNCHER, launchInfo.getDosboxBinaryPath() + launchInfo.getDosboxParameters());

            File installdir = new File(launchInfo.getDosboxBinaryPath()).getParentFile();
            p = rt.exec("\"" + launchInfo.getDosboxBinaryPath() + "\"" + launchInfo.getDosboxParameters(), null, installdir);

            if (launchInfo.getRoomData().isHost() && !launchInfo.getRoomData().isInstant()) {
                Protocol.launch();
            }

            try {
                p.waitFor();
            } catch (InterruptedException ex) {
            }
        } catch (IOException e) {
            FrameOrganizer.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "Error while launching: " + e.getMessage() + "\nAborting launch!",
                    ChatStyles.SYSTEM, false);
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
        File exec = new File(launchInfo.getDosboxBinaryPath());

        boolean ret = exec.exists() && exec.canExecute();
        String path = launchInfo.getGameBinaryPath();
        if (path != null) {
            File exec2 = new File(path);
            ret = ret && exec2.exists();
        } else {
            ret = false;
        }
        if (ret == false) {
            FrameOrganizer.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "Launcher failed. Please make sure that the game is properly setup for launch.",
                    ChatStyles.SYSTEM, false);
        }

        return ret;
    }
}
