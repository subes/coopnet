package coopnetclient.test;

import coopnetclient.Globals;
import org.junit.BeforeClass;

public abstract class AbstractCoopnetClientTest {

    @BeforeClass
    public static void enableDebug(){
        Globals.detectOperatingSystem();
        Globals.init();
        Globals.enableDebug();
    }

}
