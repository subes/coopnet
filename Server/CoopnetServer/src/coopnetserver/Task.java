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
