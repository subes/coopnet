/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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

package coopnetclient.launchers.handlers;

import coopnetclient.launchers.*;
import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.Settings;
import coopnetclient.modules.ColoredChatHandler;
import coopnetclient.utils.gamedatabase.GameDatabase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DPlayExeHandler {

    private Process process;
    private OutputStream out;
    private BufferedReader in;
    public boolean isInitialized = false;
    
    //Synchronization variables
    private boolean playing = false;
    private boolean isReady = false;
    private boolean waiting_stopDPlay = false;
    private boolean waiting_launch = false;
    private String waiting_setPlayerName = null;

    public DPlayExeHandler(final String gameIdentifier, String modname, boolean isHost, String ip, boolean compatible) {
        //Compile command string
        String execCommand = Settings.getWineCommand() + " lib/JDPlay_rmt.exe" +
                " --player " + Globals.thisPlayer_inGameName + 
                " --game " + GameDatabase.getGuid(gameIdentifier,modname);

        if (!isHost) {
            execCommand += " --host " + ip;
            if (compatible) {
                execCommand += " --search " + Launcher.MAX_RETRIES;
            }
        }

        if (Globals.debug) {
            execCommand += " --debug ";
        }
		
        //Print exec string
        if(Globals.debug){
            System.out.println("[RMT]\t"+execCommand);
        }
        
        if(!isInitialized){
            //Run
            try {
                process = Runtime.getRuntime().exec(execCommand);
                out = process.getOutputStream();
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                            //Read answer and determine if dplay was initialized properly
                String[] toRead = {"RDY", "ERR init"};
                int ret = read(toRead);

                if(ret == 0){ //RDY
                    isInitialized = true;
                    isReady = true;
                }else if(ret == 1){ //ERR init
                    Globals.clientFrame.printToVisibleChatbox("SYSTEM",
                            "DirectPlay failed to initialize properly, maybe you miss some dlls?",
                            coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
                }else{ 
                    Globals.clientFrame.printToVisibleChatbox("SYSTEM", 
                            "DirectPlay failed to initialize properly, maybe JDPlay_rmt.exe is missing?",
                            coopnetclient.modules.ColoredChatHandler.SYSTEM_STYLE);
                }
            } catch (IOException e) {
                printCommunicationError(e);
            }
        }
    }
    
    private void read(String toRead){
        String[] asArray = {toRead};
        read(asArray);
    }
    
    //Reads one of the possibilities given in toRead and gives the found index
    private int read(String[] toRead){
        try {
            do{
                String ret = in.readLine();
                if (Globals.debug) {
                    System.out.println("[RMT]\tIN: " + ret);
                }
                
                if(ret == null){
                    if(Globals.debug){
                        System.out.println("[RMT]\tRead null, JDPlay_rmt.exe closed");
                    }
                    return -1;
                }
                
                for(int i = 0; i < toRead.length; i++){
                    if(toRead[i].equals(ret)){
                        return i;
                    }
                }  
            }while(true);
        }catch(IOException e) {
            printCommunicationError(e);
            return -1;
        }
    }
    
    private boolean write(String toWrite){
        try{
            if (Globals.debug) {
                System.out.println("[RMT]\tOUT: "+toWrite);
            }
            out.write(toWrite.getBytes());
            out.flush();
            return true;
        }catch(IOException e){
            printCommunicationError(e);
            return false;
        }
    }
    
    private void printCommunicationError(Exception e){
        if(e == null){
            Globals.clientFrame.printToVisibleChatbox("SYSTEM", 
                    "DirectPlay communication error.", 
                    ColoredChatHandler.SYSTEM_STYLE);
        }else{
            Globals.clientFrame.printToVisibleChatbox("SYSTEM", 
                    "DirectPlay communication error; "+e.getMessage(),
                    ColoredChatHandler.SYSTEM_STYLE);
        }
    }

    public void setPlayerName(String name) {
        if(!isReady){
            waiting_setPlayerName = name;
        }else{
            isReady = false;
            waiting_setPlayerName = null;

            //Write playername
            if(!write("PLAYERNAME " + name + "\n")){return;}
            
            //Read ready
            read("RDY");
            isReady = true;
            
            //Call any waiting methods
            if (waiting_stopDPlay) {
                stopDPlay();
            }else
            if(waiting_launch){
                launch();  
            }
        }
    }
    
    public void launch() {
        if (!playing) {
            if(!isReady){
                waiting_launch = true;
            }else{
                waiting_launch = false;
                isReady = false;

                //Write LAUNCH
                if(!write("LAUNCH\n")){return;}
                playing = true;
                
                //Read launch status
                String[] toRead = {"FIN", "ERR launch"};
                int ret = read(toRead);
                
                if(ret == 0){ //FIN
                    playing = false;
                }else if(ret == 1){ //ERR launch
                    playing = false;
                    Globals.clientFrame.printToVisibleChatbox("SYSTEM", 
                            "Unable to start the game, maybe you miss some dlls?",
                            ColoredChatHandler.SYSTEM_STYLE);
                    return;
                }
                
                read("RDY");
                isReady = true;

                //Call any anywaiting methods
                if (waiting_stopDPlay) {
                    stopDPlay();
                }else
                if(waiting_setPlayerName != null){
                    setPlayerName(waiting_setPlayerName);  
                    waiting_setPlayerName = null;
                }
            }
        }
    }
	
    public void stopDPlay() {
        if (playing){
            if(Globals.debug){
                System.out.println("[RMT]\twaiting for close");
            }
            waiting_stopDPlay = true;
            waiting_setPlayerName = null;
            waiting_launch = false;
        } else {
            if(Globals.debug){
                System.out.println("[RMT]\tclosing");
            }
            isReady = false;
            waiting_stopDPlay = false;
            waiting_setPlayerName = null;
            waiting_launch = false;
            isInitialized = false;
            
            try {
                out.close();
                in.close();
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException ex) {
                //Nothing here
            }
        }
    }
}
