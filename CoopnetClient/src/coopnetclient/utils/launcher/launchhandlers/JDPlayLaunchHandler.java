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
import coopnetclient.frames.clientframe.TabOrganizer;
import coopnetclient.frames.clientframe.tabs.RoomPanel;
import coopnetclient.utils.Logger;
import coopnetclient.utils.RegistryReader;
import coopnetclient.utils.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.utils.launcher.launchinfos.LaunchInfo;
import coopnetclient.utils.Settings;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class JDPlayLaunchHandler extends LaunchHandler {
    
    private static Process jdplay;
    private static OutputStream out;
    private static BufferedReader in;
    
    private DirectPlayLaunchInfo launchInfo;
    private String binary;
    
    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if(jdplay == null){
            //Workaround for wine, it puts "" to the playername if the playername doesn't contain a space
            String playerName;
            if(Globals.getThisPlayer_inGameName().contains(" ")){
                playerName = "\""+Globals.getThisPlayer_inGameName()+"\"";
            }else{
                playerName = Globals.getThisPlayer_inGameName();
            }
            
            String command = "";
            
            if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
                command += Globals.getWineCommand();
            }
            
            command += " lib/jdplay.exe" +
                 " --playerName " + playerName + 
                 " --maxSearchRetries " + Globals.JDPLAY_MAXSEARCHRETRIES;
            
            if(Globals.getDebug()){
                command += " --debug";
            }
            
            //print exec string
            Logger.log(LogTypes.LAUNCHER, command);
            
            //run
            try {
                jdplay = Runtime.getRuntime().exec(command);
                out = jdplay.getOutputStream();
                in = new BufferedReader(new InputStreamReader(jdplay.getInputStream()));
            } catch (IOException e) {
                Logger.log(e);
                
                Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "Error while initializing:" + e.getMessage(),
                    ChatStyles.SYSTEM,false);
                
                return false;
            }   
        }

        String regPath = GameDatabase.getRegEntry(launchInfo.getGameName(), launchInfo.getModName()).get(0);
        binary = RegistryReader.read(regPath.substring(0, regPath.lastIndexOf("\\")+1)+"File");
        
        if(!(launchInfo instanceof DirectPlayLaunchInfo)){
            throw new IllegalArgumentException("expected launchInfo to be "+DirectPlayLaunchInfo.class.toString()+", but got "+launchInfo.getClass().toString());
        }
        
        this.launchInfo = (DirectPlayLaunchInfo) launchInfo;
        
        if(!write("INITIALIZE" +
                        " gameGUID:" + this.launchInfo.getGameGUID() +
                        " hostIP:" + this.launchInfo.getHostIP() +
                        " isHost:" + this.launchInfo.getIsHost())){
            return false;
        }

        boolean ret = waitForCommandResult();
        
        return ret;
    }

    @Override
    protected boolean doLaunch() {

        if (!write("LAUNCH doSearch:" + true)){
            return false;
        }
        
        boolean ret = waitForCommandResult();
        
        if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
            try{
                Process p = Runtime.getRuntime().exec("pkill -f dplaysvr.exe");
                p.waitFor();
            }catch(Exception e){
                printError(e);
            }
        }
        
        return ret;
    }

    @Override
    public void updatePlayerName() {
        write("UPDATE playerName:" + Globals.getThisPlayer_inGameName());
    }
    
    private boolean read(String toRead) {
        String[] asArray = {toRead};
        if(read(asArray) == 0){
            return true;
        }else{
            return false;
        }
        
    }
    //Reads one of the possibilities given in toRead and gives the found index
    private int read(String[] toRead) {
        try {
            do {
                String ret = in.readLine();
                Logger.log(LogTypes.LAUNCHER, "IN: "+ret);

                if (ret == null) {
                    Logger.log(LogTypes.LAUNCHER, "Read null, jdplay.exe closed");
                    reinitJDPlay();
                    return -1;
                }

                for (int i = 0; i < toRead.length; i++) {
                    if (toRead[i].equals(ret)) {
                        return i;
                    }
                }
            } while (true);
        } catch (Exception e) {
            reinitJDPlay();
            printError(e);
            return -1;
        }
    }
    
    private boolean waitForCommandResult(){
        String[] toRead = {"FIN", "ERR"};
        int ret = read(toRead);
        
        if(ret == 0){
            return true;
        }else{
            return false;
        }
    }
    
    private synchronized boolean write(String toWrite) {
        toWrite = toWrite.trim();

        //wait until jdplay is ready
        read("RDY");
        
        try {
            if(!toWrite.endsWith("\n")){
                toWrite += "\n";
            }
            
            //write
            Logger.log(LogTypes.LAUNCHER, "OUT: " + toWrite);
            out.write(toWrite.getBytes());
            out.flush();

            if(!toWrite.equals("DONE\n")){
                toWrite = "DONE\n";
                Logger.log(LogTypes.LAUNCHER, "OUT: " + toWrite);
                out.write(toWrite.getBytes());
                out.flush();

                //verify if jdplay understood
                String[] toRead = {"ACK", "NAK"};
                int ret = read(toRead);
                if(ret == 0){
                    return true;
                }else{
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            reinitJDPlay();
            printError(e);
            return false;
        }
    }

    private void printError(Exception e) {        
        if (e == null) {
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "Undetermined DirectPlay error.\nRecovering ...",
                    ChatStyles.SYSTEM,false);
        } else {
            Logger.log(e);
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "DirectPlay error: " + e.getMessage() + "\nRecovering ...",
                    ChatStyles.SYSTEM,false);
        }
    }
    
    private void reinitJDPlay(){        
        
        RoomPanel room = TabOrganizer.getRoomPanel();
        if(room != null){
            room.displayReInit();
        }
        
        if(jdplay != null){
            jdplay.destroy();
            jdplay = null;
        }

        if(out != null){
            try {
                out.close();
            } catch (IOException ex) {}
            out = null;
        }

        if(in != null){
            try {
                in.close();
            } catch (IOException ex) {}
            in = null;
        }
        
        if(room != null){
            room.initLauncher();
        }
    }

    @Override
    public boolean predictSuccessfulLaunch() {
        boolean ret = jdplay != null && out != null && in != null;
        
        if(ret == true){
            try{
                jdplay.exitValue();
            }catch(NullPointerException e){
                ret = false;
            }catch(IllegalThreadStateException e){}
        }
        
        if(ret == true){
            write("DONE");
        }
        
        if(ret == false){
            reinitJDPlay();
        }
                
        return ret;
    }

    @Override
    public String getBinaryName() {
        return binary;
    }

}
