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

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.Logger;
import coopnetclient.utils.ui.SoundPlayer;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import java.io.IOException;
import javax.swing.JOptionPane;

public abstract class LaunchHandler {
    
    private boolean firstInitDone = false;
    private LaunchInfo launchInfo;

    public LaunchInfo getLaunchInfo(){
        return launchInfo;
    }

    public boolean initialize(LaunchInfo launchInfo){
        if(firstInitDone){
            updatePlayerName();
        }
        firstInitDone = true;

        this.launchInfo = launchInfo;

        return doInitialize(launchInfo);
    }
    protected abstract boolean doInitialize(LaunchInfo launchInfo);
    
    public abstract boolean predictSuccessfulLaunch();

    public boolean launch(){
        boolean doNormalLaunch = true;

        //Detect if executable is already running
        if(launchInfo.getRoomData().isHost()){
            if(processExists()){
                JOptionPane.showMessageDialog(null,
                        "<html>Coopnet has detected that the game \"<b>"+launchInfo.getBinaryName()+"</b>\" is already running.<br>" +
                        "Please make sure the other players can <b>connect to a running server</b> there<br>" +
                        "or <b>close the game</b> before confirming this message.<br>" +
                        "<br>" +
                        "<br>If the game is still running after you have confirmed this message," +
                        "<br>Coopnet will launch everyone in your room except you.",
                        "WARNING: Game is already running",
                        JOptionPane.WARNING_MESSAGE);

                doNormalLaunch = !processExists();
            }
        }else{
            while(processExists()){
                int option = JOptionPane.showConfirmDialog(null,
                        "<html>Coopnet has detected that the game \"<b>"+launchInfo.getBinaryName()+"</b>\" is already running.<br>" +
                        "Please <b>close the game</b>, so you can actually join the hosts game.<br>" +
                        "<br>" +
                        "Press ok to retry or press cancel to abort the launch.",
                        "WARNING: Game is already running",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if(option == JOptionPane.CANCEL_OPTION){
                    return true;
                }
            }
            doNormalLaunch = true;
        }

        SoundPlayer.playLaunchSound();

        if(doNormalLaunch){
            return doLaunch();
        }else{
            if(launchInfo.getRoomData().isHost() && !launchInfo.getRoomData().isInstant()){
                Protocol.launch();
            }
            while(processExists()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.log(ex);
                }
            }

            return true;
        }
    }

    protected abstract boolean doLaunch();

    public boolean processExists(){
        String pgrepCommand;
        if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
            if(launchInfo instanceof DirectPlayLaunchInfo){
                pgrepCommand = Globals.getWineCommand() + " lib\\winpgrep.exe";
            }else{
                pgrepCommand = "pgrep";
            }
        }else{
            pgrepCommand = "lib\\winpgrep.exe";
        }

        try {
            String command = pgrepCommand + " " + launchInfo.getBinaryName();

            Process p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.log(ex);
            }

            Logger.log(LogTypes.LAUNCHER, command + "; ReturnCode = " + p.exitValue());

            return p.exitValue() == 0;

        } catch (IOException ex) {
            Logger.log(ex);
        }

        return false;
    }

    public abstract void updatePlayerName();
}
