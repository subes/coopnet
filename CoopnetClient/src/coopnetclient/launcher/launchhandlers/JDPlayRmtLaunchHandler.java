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

package coopnetclient.launcher.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.enums.ChatStyles;
import coopnetclient.launcher.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.launcher.launchinfos.LaunchInfo;
import coopnetclient.modules.Settings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class JDPlayRmtLaunchHandler extends LaunchHandler {

    private DirectPlayLaunchInfo launchInfo;
    
    private Process jdplay;
    private OutputStream out;
    private BufferedReader in;
    
    @Override
    protected boolean doInitialize(LaunchInfo launchInfo) {
        if(jdplay == null){
            String command = /*Settings.getWineCommand() +*/ " lib/JDPlay_rmt.exe" +
                 " --playerName " + Globals.getThisPlayer_inGameName() + 
                 " --maxSearchRetries " + Globals.JDPLAY_MAXSEARCHRETRIES;
            
            if(Globals.getDebug()){
                command += " --debug";
            }
            
            //print exec string
            if (Globals.getDebug()) {
                System.out.println("[RMT]\t" + command);
            }
            
            //run
            try {
                jdplay = Runtime.getRuntime().exec(command);
                out = jdplay.getOutputStream();
                in = new BufferedReader(new InputStreamReader(jdplay.getInputStream()));
            } catch (IOException e) {
                printCommunicationError(e);
            }   
        }
        
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
        
        String[] toRead = {"FIN", "ERR"};
        int ret = read(toRead);
        if(ret == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected boolean doLaunch() {
        if (!write("LAUNCH doSearch:" + launchInfo.getCompatibility())){
            return false;
        }
        
        String[] toRead = {"FIN", "ERR"};
        int ret = read(toRead);
        if(ret == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void updatePlayerName() {
        write("UPDATE playerName:"+Globals.getThisPlayer_inGameName());
    }
    
    private void read(String toRead) {
        String[] asArray = {toRead};
        read(asArray);
    }
    //Reads one of the possibilities given in toRead and gives the found index
    private int read(String[] toRead) {
        try {
            do {
                String ret = in.readLine();
                if (Globals.getDebug()) {
                    System.out.println("[RMT]\tIN: " + ret);
                }

                if (ret == null) {
                    if (Globals.getDebug()) {
                        System.out.println("[RMT]\tRead null, JDPlay_rmt.exe closed");
                        jdplay.destroy();
                        jdplay = null;
                        in.close();
                        in = null;
                        out.close();
                        out = null;
                    }
                    return -1;
                }

                for (int i = 0; i < toRead.length; i++) {
                    if (toRead[i].equals(ret)) {
                        return i;
                    }
                }
            } while (true);
        } catch (IOException e) {
            printCommunicationError(e);
            return -1;
        }
    }

    private synchronized boolean write(String toWrite) {
        //wait until jdplay is ready
        read("RDY");
        
        try {
            if(!toWrite.endsWith("\n")){
                toWrite += "\n";
            }
            
            //write
            if (Globals.getDebug()) {
                System.out.print("[RMT]\tOUT: " + toWrite);
            }
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
        } catch (IOException e) {
            printCommunicationError(e);
            return false;
        }
    }

    private void printCommunicationError(Exception e) {
        if (e == null) {
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "Undetermined DirectPlay communication error.",
                    ChatStyles.SYSTEM);
        } else {
            Globals.getClientFrame().printToVisibleChatbox("SYSTEM",
                    "DirectPlay communication error: " + e.getMessage(),
                    ChatStyles.SYSTEM);
        }
    }

}
