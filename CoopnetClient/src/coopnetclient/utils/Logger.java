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

package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.ClientProtocolCommands;
import coopnetclient.enums.ServerProtocolCommands;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void logInTraffic(ClientProtocolCommands command, String[] information){
        if(Globals.getDebug()){
            logTraffic(LogTypes.IN, command.toString(), information);
        }
    }
    
    public static void logOutTraffic(ServerProtocolCommands command, String[] information){
        if(Globals.getDebug()){
            logTraffic(LogTypes.OUT, command.toString(), information);
        }
    }
    
    private static void logTraffic(LogTypes type, String command, String[] information){
        String message = command + " ";
        for( int i = 0; i < information.length; i++){
            if(i != 0){
                message += "|";
            }
            message += information[i];
        }
        log(type, message);
    }
    
    public static void log(LogTypes type, String message){
        if(Globals.getDebug()){
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss.SSS");
            System.out.println(date.format(new Date())+"\t"+type.toString()+":\t"+message);
        }
    }
}

