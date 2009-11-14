package coopnetserver.test;

import coopnetserver.Globals;
import org.junit.BeforeClass;
import org.junit.Ignore;

//CHECKSTYLE:OFF
@Ignore
public abstract class AbstractCoopnetServerTest {
//CHECKSTYLE:ON

    @BeforeClass
    public static void enableDebug(){
        Globals.setDebug(true);
    }
}
