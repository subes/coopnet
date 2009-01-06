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
import coopnetclient.enums.ServerProtocolCommands;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {

    private static ArrayList<String> log = new ArrayList<String>();
    private static final int TAIL_LENGTH = 300;
    
    public static String getEndOfLog(){
        String ret = new String();
        for(int i = 0; i < log.size(); i++){
            ret += log.get(i) + "\n";
        }
        return ret;
    }
    
    public static void logInTraffic(ServerProtocolCommands command, String[] information){
        String message = command.toString() + " ";
        if(information.length != 0){
            message += "[";
            for( int i = 0; i < information.length; i++){
                if(i != 0){
                    message += "|";
                }
                message += information[i];
            }
            message += "]"; 
        }
        log(LogTypes.IN, message);
    }
    
    public static void logInTraffic(String[] data){
        String message = data[0] + " ";
        if(data.length != 1){
            message += "[";
            for( int i = 1; i < data.length; i++){
                if(i != 1){
                    message += "|";
                }
                message += data[i];
            }
            message += "]";
        }
        log(LogTypes.IN, message);
    }
    
    public static void logOutTraffic(String logString){
        log(LogTypes.OUT, logString);
    }
    
    public static void log(LogTypes type, String message){
        message = message.trim();
        while(message.endsWith("\n\n")){
            message = message.substring(0, message.length()-1);
        }

        String entry = getHeader(type) + message;
        
        if(Globals.getDebug()){
            if(type == LogTypes.ERROR){
                System.err.println(entry);
            }else{
                System.out.println(entry);
            }
        }
        append(entry);
    }
    
    public static void log(Exception exception){
        String entry = "\n" + exception.getClass().toString() + ": " + exception.getMessage();

        StackTraceElement[] trace = exception.getStackTrace();
        for(int i = 0; i < trace.length; i++){
            entry += "\n\tat "+trace[i].toString();
        }
        
        Throwable cause = exception.getCause();
        while(cause != null){
            entry += "\nCaused by - " + cause.getClass().toString() + ": " + cause.getMessage();
            trace = cause.getStackTrace();
            for(int i = 0; i < trace.length; i++){
                entry += "\n\tat "+trace[i].toString();
            }
            
            cause = cause.getCause();
        }
        
        log(LogTypes.ERROR, entry);
    }
    
    private static void append(String entry){
        if(log.size() == TAIL_LENGTH){
            log.remove(0);
        }
        
        log.add(entry);
    }
    
    private static String getHeader(LogTypes type){
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss.SSS");
        String entry = date.format(new Date()) + "\t" + type + ":\t";
        
        return entry;
    }
}

