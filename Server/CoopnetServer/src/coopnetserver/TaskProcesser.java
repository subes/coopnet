package coopnetserver;

import coopnetserver.data.player.PlayerData;
import coopnetserver.enums.LogTypes;
import coopnetserver.enums.TaskTypes;
import coopnetserver.protocol.in.CommandHandler;
import coopnetserver.exceptions.VerificationException;
import coopnetserver.utils.ErrThread;
import coopnetserver.utils.Logger;
import java.util.ArrayList;

public class TaskProcesser {

    private static final ArrayList<Task> pendingQuickTasks = new ArrayList<Task>();
    private static final ArrayList<Task> pendingSlowTasks = new ArrayList<Task>();

    public static void addTask(Task T) {
        switch (T.type) {
            case LOGOFF:
                synchronized(pendingQuickTasks){
                    pendingQuickTasks.add(0, T);
                }
                break;
            case QUICK:
                synchronized(pendingQuickTasks){
                    pendingQuickTasks.add(T);
                }
                break;
            case SLOW:
                synchronized(pendingSlowTasks){
                    pendingSlowTasks.add(T);
                }
                break;
        }
    }
    
    public void start(){
        quickTaskProcesser.start();
        slowTaskProcesser.start();
    }
    
    private ErrThread quickTaskProcesser = new ErrThread() {

        @Override
        public void handledRun() throws Throwable {
            Task current = null;
            while (true) {
                current = null;
                //get first task
                try {
                    synchronized(pendingQuickTasks){
                        current = pendingQuickTasks.remove(0);
                    }
                } catch (IndexOutOfBoundsException  none) {
                    try {
                        sleep(10);
                    } catch (Exception e) {
                    }
                    continue;
                }                
                //execute it
                try {
                    switch (current.type) {
                        case QUICK: {
                            CommandHandler.executeCommand(current.con, current.command);
                            continue;
                        }
                        case LOGOFF: {
                            if(current.con != null){//its null when multiple messages were being sent to the key but it was closed
                                PlayerData.logOff(current.con);
                            }
                            continue;
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof VerificationException) {
                        Logger.logVerificationError((VerificationException) e, current.con);
                    } else {
                        Logger.log(e, current.con);
                    }
                }
                current = null;
            }//end of infinite loop   
        }
    };
    
    private ErrThread slowTaskProcesser = new ErrThread() {

        @Override
        public void handledRun() throws Throwable {
            Task current = null;
            while (true) {
                current = null;
                //get first task
                try {
                    synchronized(pendingSlowTasks){
                        current = pendingSlowTasks.remove(0);
                    }
                } catch (IndexOutOfBoundsException  none) {
                    try {
                        sleep(10);
                    } catch (Exception e) {
                    }
                    continue;
                }
                //execute it
                try {
                    if( current.type == TaskTypes.SLOW){
                        CommandHandler.executeCommand(current.con, current.command);
                    }else{
                        Logger.log(LogTypes.ERROR, "Command on the wrong taskprocesser thread!" +current.command[0],current.con);
                    }                    
                } catch (Exception e) {
                    if (e instanceof VerificationException) {
                        Logger.logVerificationError((VerificationException) e, current.con);
                    } else {
                        Logger.log(e, current.con);
                    }
                }
                current = null;
            }//end of infinite loop   
        }
    };
}
