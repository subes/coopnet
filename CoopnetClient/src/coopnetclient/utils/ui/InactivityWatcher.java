package coopnetclient.utils.ui;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import coopnetclient.utils.launcher.Launcher;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class InactivityWatcher implements AWTEventListener, ActionListener {

    private static final int TIMEOUT_MILLIS = 60*1000;

    private long lastEventTimeStamp = System.currentTimeMillis();
    private boolean isAway = false;

    private Timer afkToggler = new Timer(1000, this);

    private InactivityWatcher() {
        afkToggler.start();
    }

    public static void init(){
        Toolkit.getDefaultToolkit()
                .addAWTEventListener(new InactivityWatcher(),
                    AWTEvent.MOUSE_EVENT_MASK
                    | AWTEvent.MOUSE_MOTION_EVENT_MASK
                    | AWTEvent.MOUSE_WHEEL_EVENT_MASK
                    | AWTEvent.KEY_EVENT_MASK);
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
