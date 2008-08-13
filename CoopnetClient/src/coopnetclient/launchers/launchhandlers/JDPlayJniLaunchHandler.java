/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coopnetclient.launchers.launchhandlers;

import coopnetclient.Globals;
import coopnetclient.enums.LaunchMethods;
import coopnetclient.launchers.launchinfos.DirectPlayLaunchInfo;
import coopnetclient.launchers.launchinfos.LaunchInfo;
import jdplay.JDPlay;

/**
 *
 * @author subes
 */
public class JDPlayJniLaunchHandler extends LaunchHandler {

    private static JDPlay jdplay;
    
    private DirectPlayLaunchInfo launchInfo;
    
    @Override
    public boolean doInitialize(LaunchInfo launchInfo) {
        if(jdplay == null){
            jdplay = new JDPlay(Globals.getThisPlayer_inGameName(), Globals.JDPLAY_MAXSEARCHRETRIES, Globals.getDebug());
        }
        
        if(!(launchInfo instanceof DirectPlayLaunchInfo)){
            throw new IllegalArgumentException("expected launchInfo to be "+DirectPlayLaunchInfo.class.toString()+", but got "+launchInfo.getClass().toString());
        }
        
        this.launchInfo = (DirectPlayLaunchInfo) launchInfo;
        
        return jdplay.initialize(this.launchInfo.getGameGUID(), this.launchInfo.getHostIP(), this.launchInfo.getIsHost());
    }

    @Override
    public boolean doLaunch() {
        boolean compatibility = launchInfo.getCompatibility();
        if(launchInfo.getLaunchMethod() == LaunchMethods.DIRECTPLAY_FORCED_COMPATIBILITY){
            compatibility = true;
        }
        
        return jdplay.launch(compatibility);
    }

    @Override
    public void updatePlayerName(String playerName) {
        jdplay.updatePlayerName(playerName);
    }

}
