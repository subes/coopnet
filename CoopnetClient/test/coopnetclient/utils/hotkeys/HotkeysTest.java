package coopnetclient.utils.hotkeys;

import coopnetclient.test.AbstractCoopnetClientTest;
import org.junit.Test;

public class HotkeysTest extends AbstractCoopnetClientTest {
    @Test
    public void testBindHotKey() {
        Hotkeys.bindHotKey(Hotkeys.ACTION_LAUNCH);
    }

    @Test
    public void testReBindHotKey() {
        Hotkeys.reBindHotKey(Hotkeys.ACTION_LAUNCH);
    }

    @Test
    public void testUnbindHotKey() {
        Hotkeys.unbindHotKey(Hotkeys.ACTION_LAUNCH);
    }

    @Test
    public void testCleanUp() {
        Hotkeys.cleanUp();
    }

}