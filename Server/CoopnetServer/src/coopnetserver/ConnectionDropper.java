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
import coopnetserver.data.connection.ConnectionData;
import coopnetserver.enums.LogTypes;
import coopnetserver.enums.TaskTypes;
import coopnetserver.utils.ErrThread;
import coopnetserver.utils.Logger;

/**
 * Goes trough the online players and logs off the players that arent connected
 */
public class ConnectionDropper extends ErrThread {

    private static final long TIMEOUT = 70000;
    private static boolean running = true;

    public static boolean isRunning(){
        return running;
    }

    public static void setRunning(boolean value){
        running = value;
    }

    @Override
    public void handledRun() throws Throwable {
        running = true;
        //main loop
        Connection c = null;
        while (running) {
            c = ConnectionData.getNext();
            if(c == null){
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
            }else{
                Long lastmsg = c.getLastMessageTimeStamp();
                if ((System.currentTimeMillis() - lastmsg) > TIMEOUT) {
                    Logger.log(LogTypes.CONNECTION, "Timeout detected, dropping!", c);
                    TaskProcesser.addTask(new Task(TaskTypes.LOGOFF,c,new String[] {} ));
                }
            }
            c = null;
        }        
    }
}

