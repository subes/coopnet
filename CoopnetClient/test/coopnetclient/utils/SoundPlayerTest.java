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

package coopnetclient.utils;

import coopnetclient.utils.ui.SoundPlayer;
import coopnetclient.test.AbstractCoopnetClientTest;
import coopnetclient.utils.settings.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author subes
 */
public class SoundPlayerTest extends AbstractCoopnetClientTest {
    private static final int SLEEP_ONE_SECOND = 1000;

    boolean soundEnabled;

    @Before
    public void setUp(){
        soundEnabled = Settings.getSoundEnabled();
        Settings.setSoundEnabled(true);
    }

    @After
    public void tearDown(){
        Settings.setSoundEnabled(soundEnabled);
    }

    @Test
    public void testPlayNudgeSound() throws InterruptedException {
        SoundPlayer.playNudgeSound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayLaunchSound() throws InterruptedException {
        SoundPlayer.playLaunchSound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayReadySound() throws InterruptedException {
        SoundPlayer.playReadySound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayUnreadySound() throws InterruptedException {
        SoundPlayer.playUnreadySound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayLoginSound() throws InterruptedException {
        SoundPlayer.playLoginSound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayLogoutSound() throws InterruptedException {
        SoundPlayer.playLogoutSound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

    @Test
    public void testPlayRoomCloseSound() throws InterruptedException {
        SoundPlayer.playRoomCloseSound();
        Thread.sleep(SLEEP_ONE_SECOND);
    }

}