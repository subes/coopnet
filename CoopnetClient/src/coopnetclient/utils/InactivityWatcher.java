package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.launcher.Launcher;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.Timer;

public class InactivityWatcher implements AWTEventListener, ActionListener {

    private static final int TIMEOUT_MILLIS = 60*1000;

    private long lastEventTimeStamp = System.currentTimeMillis();
    private boolean isAway = false;

    private Timer afkToggler = new Timer(1000, this);

    public InactivityWatcher() {
        afkToggler.start();
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        lastEventTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Globals.getLoggedInStatus()) {
            if (Launcher.isPlaying()) {
                return;
            }

            boolean isTimedOut = System.currentTimeMillis() - lastEventTimeStamp > TIMEOUT_MILLIS;

            if (!isTimedOut && isAway) {
                isAway = false;
                Protocol.unSetAwayStatus();
            }else if(isTimedOut && !isAway){
                isAway = true;
                    Protocol.setAwayStatus();
            }
        }
    }
}
