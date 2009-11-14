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

        regkeys = null;
        Assert.assertNull(RegistryReader.readAny(regkeys));
    }

}