package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.protocol.ClientProtocolCommands;
import coopnetclient.protocol.ServerProtocolCommands;
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

