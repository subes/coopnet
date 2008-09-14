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

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.enums.ClientProtocolCommands;

public class Main {
    
    public static void main(String[] args) {
        checkArgs(args);
        
        if(Globals.getDebug()){
            System.out.println("[L]\tStarting ...");
        }
        
        Client.startup();
    }
    
    private static void checkArgs(String[] args){
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("--server")){
                if(args.length < i+1 || args[i].indexOf(":") == -1){
                    try{
                        String ip = args[i+1].substring(0, args[i+1].indexOf(":"));
                        Globals.setServerIP(ip);
                        int port = Integer.parseInt(args[i+1].substring(args[i+1].indexOf(":")+1));
                        Globals.setServerPort(port);
                        i++;
                    }catch(NumberFormatException e){
                        System.out.println("ERROR: invalid value for <PORT>, number expected");
                    }
                    catch(java.lang.StringIndexOutOfBoundsException e){
                        System.out.println("ERROR: invalid value for <PORT>, number expected");
                    } 
                }else{
                    System.out.println("ERROR: --server expects data in the form of \"127.0.0.1:6667\"");
                    printHelp();
                }
            }else
            if(args[i].equals("--debug")){
                Globals.enableDebug();
            }else
            if(args[i].equals("--help")){
                printHelp();
            }else{
                printHelp();
            }
        }
    }
    
    private static void printHelp(){
        System.out.println( "\nCoopnetClient usage:\n" +
                            "    java -jar CoopnetClient.jar [--server <IP>:<PORT>] [--debug]\n" +
                            "\n" +
                            "    --server   ip and port of the server to connect to\n" +
                            "    --debug    print debug messages during operation\n" +
                            "    --help     print this help and exit\n");
        
        System.exit(1);
    }
}
