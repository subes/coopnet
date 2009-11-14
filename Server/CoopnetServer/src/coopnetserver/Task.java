package coopnetserver;

import coopnetserver.data.connection.Connection;
import coopnetserver.enums.ClientProtocolCommands;
import coopnetserver.enums.TaskTypes;
import coopnetserver.protocol.out.Protocol;
import java.util.ArrayList;

public class Task {
    public TaskTypes type;
    Connection con = null;
    String[] command = {};

    public Task(Object[] attachment) {
        //this.type = type;
        con = (Connection) attachment[0];
        command = getParts(attachment[1].toString());
        Integer commandidx = Integer.valueOf(command[0]);
        if( commandidx < ClientProtocolCommands.DIVIDER.ordinal() ){
            type = TaskTypes.QUICK;
        }else{
            type = TaskTypes.SLOW;
        }
    }    
    
    public Task(TaskTypes type, Connection con , String [] command) {
        this.type = type;
        this.con = con;
        this.command = command;
    }    
    
    private static StringBuilder sb = new StringBuilder();
    private static ArrayList<String> array = new ArrayList<String>();
    
    public static String[] getParts(String input) {
        sb.delete(0, sb.length());
        array.clear();
        int index = 0;
        while (index < input.length()) {
            if (input.charAt(index) == Protocol.INFORMATION_DELIMITER.charAt(0)) {//start of new token, store old
                array.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                sb.append(input.charAt(index));
            }
            index++;
        }
        array.add(sb.toString());
        String[] info = new String[array.size()];
        return array.toArray(info);
    }    
}
