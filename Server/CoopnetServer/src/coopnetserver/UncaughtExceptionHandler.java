package coopnetserver;

import coopnetserver.utils.Logger;


public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void init(){
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.log(e);
    }

}
