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

package coopnetclient.utils.launcher.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.OperatingSystems;
import coopnetclient.utils.Logger;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.launcher.launchinfos.ParameterLaunchInfo;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class ParameterLaunchHandler extends LaunchHandler {

    private ParameterLaunchInfo launchInfo;

    private String binary;
    
    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if(!(launchInfo instanceof ParameterLaunchInfo)){
            throw new IllegalArgumentException("expected launchInfo to be "+ParameterLaunchInfo.class.toString()+", but got "+launchInfo.getClass().toString());
        }
        
        this.launchInfo = (ParameterLaunchInfo) launchInfo;

        String binaryPath = new File(this.launchInfo.getBinaryPath()).getAbsolutePath();
        this.binary = binaryPath.substring(binaryPath.lastIndexOf(File.separatorChar)+1);
        
        return true;
    }

    @Override
    public boolean launch() {

        boolean doNormalLaunch = true;

        //Detect if executable is already running
        if(processExists()){
            JOptionPane.showMessageDialog(null,
                    "<html>Coopnet has detected that the game \"<b>"+binary+"</b>\" is already running.<br>" +
                    "Please make sure the other players can <b>connect to a running server</b> there<br>" +
                    "or <b>close the game</b> before confirming this message.<br>" +
                    "<br>" +
                    "<br>If the game is still running after you have confirmed this message," +
                    "<br>Coopnet will launch everyone in your room except you.",
                    "WARNING: Game is already running",
                    JOptionPane.WARNING_MESSAGE);
            
            doNormalLaunch = !processExists();
        }

        if(doNormalLaunch){
            Process p = null;
            try {
                Runtime rt = Runtime.getRuntime();

                Logger.log(LogTypes.LAUNCHER, launchInfo.getBinaryPath() + launchInfo.getParameters());

                File installdir = new File(launchInfo.getInstallPath());
                p = rt.exec(launchInfo.getBinaryPath() + launchInfo.getParameters(), null, installdir);

                try {
                    p.waitFor();
                } catch (InterruptedException ex) {
                }
            } catch (IOException e) {
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                        "Error while launching: " + e.getMessage(),
                        ChatStyles.SYSTEM, false);
                Logger.log(e);
            }
            return (p.exitValue() == 0 ? true : false);
        }else{
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

    @Override
    public void updatePlayerName() {
        //do nothing
    }
    
    @Override
    public boolean predictSuccessfulLaunch() {
        File exec = new File(launchInfo.getBinaryPath());
        
        boolean ret = exec.exists() && exec.canExecute();
        
        if(ret == false){
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                "Launcher failed. Please make sure that the game is properly setup for launch.",
                ChatStyles.SYSTEM,false);
        }
        
        return ret;
    }

    public boolean processExists(){

        String pgrepCommand;
        if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
            pgrepCommand = "pgrep";
        }else{
            pgrepCommand = "lib\\winpgrep.exe";
        }

        try {
            String command = pgrepCommand + " " + binary;

            Logger.log(LogTypes.LAUNCHER, command);

            Process p = Runtime.getRuntime().exec(command);
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                Logger.log(ex);
            }

            return p.exitValue() == 0;

        } catch (IOException ex) {
            Logger.log(ex);
        }

        return false;
    }
}
