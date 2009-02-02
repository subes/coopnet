package coopnetclient.utils;

import coopnetclient.test.AbstractCoopnetClientTest;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class RegistryReaderTest extends AbstractCoopnetClientTest {

    @Test
    public void testRead() {
        String fullpath = "asdf";
        Assert.assertNull(RegistryReader.read(fullpath));

        fullpath = "HKEY_LOCAL_MACHINE\\SOFTWARE";
        Assert.assertNull(RegistryReader.read(fullpath));

        fullpath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\";
        Assert.assertNull(RegistryReader.read(fullpath));

        fullpath = "SOFTWARE";
        Assert.assertNull(RegistryReader.read(fullpath));

        try{
            fullpath = null;
            RegistryReader.read(fullpath);
            Assert.fail();
        }catch(NullPointerException e){}

        fullpath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
        Assert.assertNull(RegistryReader.read(fullpath));

        fullpath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\CurrentVersion";
        Assert.assertNotNull(RegistryReader.read(fullpath));
    }

    @Test
    public void testReadAny() {
        ArrayList<String> regkeys = new ArrayList<String>();
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\CurrentVersion");
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion");
        Assert.assertNotNull(RegistryReader.readAny(regkeys));

        regkeys = new ArrayList<String>();
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion");
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\CurrentVersion");
        Assert.assertNotNull(RegistryReader.readAny(regkeys));

        regkeys = new ArrayList<String>();
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\sjklhfds");
        regkeys.add("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\CuasdsaldrrentVersion");
        Assert.assertNull(RegistryReader.readAny(regkeys));

        regkeys = new ArrayList<String>();
        Assert.assertNull(RegistryReader.readAny(regkeys));

        try{
            regkeys = null;
            RegistryReader.readAny(regkeys);
            Assert.fail();
        }catch(NullPointerException e){}
    }

}