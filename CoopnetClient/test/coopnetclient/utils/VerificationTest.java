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

import coopnetclient.Globals;
import coopnetclient.test.AbstractCoopnetClientTest;
import org.junit.Assert;
import org.junit.Test;

public class VerificationTest extends AbstractCoopnetClientTest {

    @Test
    public void testVerifyCompatibilityVersion(){

        //Do DEVELOPMENT tests
        Globals.overrideVersion(Globals.DEVELOPMENT_VERSION, Globals.DEVELOPMENT_VERSION);

        String compatibilityVersion = Globals.DEVELOPMENT_VERSION;
        Assert.assertTrue(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = "9999999999999";
        Assert.assertTrue(Verification.verifyCompatibilityVersion(compatibilityVersion));

        //Do release tests
        Globals.overrideVersion("6", "0.102.1");

        compatibilityVersion = Globals.DEVELOPMENT_VERSION;
        Assert.assertTrue(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = Globals.getCompatibilityVersion();
        Assert.assertTrue(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = String.valueOf(Integer.valueOf(Globals.getCompatibilityVersion())+1);
        Assert.assertFalse(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = String.valueOf(Integer.valueOf(Globals.getCompatibilityVersion())-1);
        Assert.assertFalse(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = "";
        Assert.assertFalse(Verification.verifyCompatibilityVersion(compatibilityVersion));

        //This tested method has to be nullsafe
        compatibilityVersion = null;
        Assert.assertFalse(Verification.verifyCompatibilityVersion(compatibilityVersion));

        compatibilityVersion = "notanumber";
        Assert.assertFalse(Verification.verifyCompatibilityVersion(compatibilityVersion));

        //Revert version numbers
        Globals.preInit();
    }

    @Test
    public void testVerifyClientVersion(){

        //Do DEVELOPMENT tests
        Globals.overrideVersion(Globals.DEVELOPMENT_VERSION, Globals.DEVELOPMENT_VERSION);

        String clientVersion = Globals.DEVELOPMENT_VERSION;
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        clientVersion = "9999.9999.9999";
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));
        
        //Do release tests
        Globals.overrideVersion("6", "0.102.1");

        clientVersion = Globals.getClientVersion();
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        clientVersion = "";
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        //This tested method has to be nullsafe
        clientVersion = null;
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        String[] versionSplit = Globals.getClientVersion().split("\\.");
        int[] i_versionSplit = new int[versionSplit.length];
        for(int i = 0; i < versionSplit.length; i++){
            i_versionSplit[i] = Integer.parseInt(versionSplit[i]);
        }

        clientVersion = i_versionSplit[0]+"."+i_versionSplit[1]+"."+i_versionSplit[2];
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        //Minor
        clientVersion = i_versionSplit[0]+"."+i_versionSplit[1]+"."+(i_versionSplit[2]+1);
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        clientVersion = i_versionSplit[0]+"."+i_versionSplit[1]+"."+(i_versionSplit[2]-1);
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));


        clientVersion = i_versionSplit[0]+"."+i_versionSplit[1]+".";
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        //Feature
        clientVersion = i_versionSplit[0]+"."+(i_versionSplit[1]+1)+"."+i_versionSplit[2];
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        clientVersion = i_versionSplit[0]+"."+(i_versionSplit[1]-1)+"."+i_versionSplit[2];
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        clientVersion = i_versionSplit[0]+"."+"."+i_versionSplit[2];
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        //Major
        clientVersion = (i_versionSplit[0]+1)+"."+i_versionSplit[1]+"."+i_versionSplit[2];
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        clientVersion = (i_versionSplit[0]-1)+"."+i_versionSplit[1]+"."+i_versionSplit[2];
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        clientVersion = "."+i_versionSplit[1]+"."+i_versionSplit[2];
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        //Revert version numbers
        Globals.preInit();
    }

    @Test
    public void testVerifyPassword(){
        String password = "1234567890123456789012345678901234567890";
        Assert.assertTrue(Verification.verifyPassword(password));

        password = "123456789012345678901234567890123456789";
        Assert.assertTrue(Verification.verifyPassword(password));

        password = "12345678901234567890123456789012345678901";
        Assert.assertTrue(Verification.verifyPassword(password));

        password = "12345";
        Assert.assertTrue(Verification.verifyPassword(password));

        password = "1234";
        Assert.assertFalse(Verification.verifyPassword(password));

        password = "";
        Assert.assertFalse(Verification.verifyPassword(password));

        try{
            password = null;
            Verification.verifyPassword(password);
            Assert.fail();
        }catch(NullPointerException e){}
    }

    @Test
    public void testVerifyLoginName(){
        String loginName = "\\[some]/-=_=-coopnetUser123";
        Assert.assertTrue(Verification.verifyLoginName(loginName));

        loginName = "��?=?-A?4?<�L�"; //Special characters
        Assert.assertFalse(Verification.verifyLoginName(loginName));

        loginName = "123";
        Assert.assertTrue(Verification.verifyLoginName(loginName));

        loginName = "123456789012345678901234567890";
        Assert.assertTrue(Verification.verifyLoginName(loginName));

        loginName = "12";
        Assert.assertFalse(Verification.verifyLoginName(loginName));

        loginName = "1234567890123456789012345678901";
        Assert.assertFalse(Verification.verifyLoginName(loginName));

        loginName = "";
        Assert.assertFalse(Verification.verifyLoginName(loginName));

        try{
            loginName = null;
            Verification.verifyLoginName(loginName);
            Assert.fail();
        }catch(NullPointerException e){}

    }

    @Test
    public void testVerifyIngameName(){

        String ingameName = "01234567890";
        Assert.assertTrue(Verification.verifyIngameName(ingameName));

        ingameName = "01234567890";
        Assert.assertTrue(Verification.verifyIngameName(ingameName));

        ingameName = "��?=?-A?4?<�L�"; //Special characters
        Assert.assertTrue(Verification.verifyIngameName(ingameName));

        ingameName = "1234567890123456789012345678901";
        Assert.assertFalse(Verification.verifyIngameName(ingameName));

        ingameName = "123456789012345678901234567890";
        Assert.assertTrue(Verification.verifyIngameName(ingameName));

        ingameName = "";
        Assert.assertFalse(Verification.verifyIngameName(ingameName));

        try{
            ingameName = null;
            Verification.verifyIngameName(ingameName);
            Assert.fail();
        }catch(NullPointerException e){}

        
    }

    @Test
    public void testVerifyEMail(){

        String email = "01234567890@1234.12";
        Assert.assertTrue(Verification.verifyEMail(email));

        email = "bla@fuk.u";
        Assert.assertTrue(Verification.verifyEMail(email));

        email = "some@email.de";
        Assert.assertTrue(Verification.verifyEMail(email));

        email = "some@emailde";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = "someatemail.de";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = String.valueOf(new char[320]);
        Assert.assertFalse(Verification.verifyEMail(email));
        
        //Check for max db length
        email = String.valueOf(new char[321]);
        Assert.assertFalse(Verification.verifyEMail(email));

        email = "";
        Assert.assertFalse(Verification.verifyEMail(email));

        try{
            email = null;
            Verification.verifyEMail(email);
            Assert.fail();
        }catch(NullPointerException e){}

        email = "some @email.de";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = "some@ email.de";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = "some@email .de";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = "some@email.de ";
        Assert.assertFalse(Verification.verifyEMail(email));

        email = " some@email .de";
        Assert.assertFalse(Verification.verifyEMail(email));
    }

    @Test
    public void testVerifyWebsite(){
        String website = "mehome";
        Assert.assertTrue(Verification.verifyWebsite(website));

        website = "";
        Assert.assertTrue(Verification.verifyWebsite(website));

        website = "hello this is some message i wanna share with everybody!";
        Assert.assertTrue(Verification.verifyWebsite(website));

        try{
            website = null;
            Verification.verifyWebsite(website);
            Assert.fail();
        }catch(NullPointerException e){}

        website = String.valueOf(new char[321]);
        Assert.assertFalse(Verification.verifyWebsite(website));

        website = String.valueOf(new char[320]);
        Assert.assertTrue(Verification.verifyWebsite(website));
    }

    @Test
    public void testVerifyCountry(){
        String country = "mehome";
        Assert.assertTrue(Verification.verifyCountry(country));

        country = "";
        Assert.assertTrue(Verification.verifyCountry(country));

        country = "hello this is some message i wanna share with everybody!";
        Assert.assertTrue(Verification.verifyCountry(country));

        try{
            country = null;
            Verification.verifyCountry(country);
            Assert.fail();
        }catch(NullPointerException e){}

        country = String.valueOf(new char[61]);
        Assert.assertFalse(Verification.verifyCountry(country));

        country = String.valueOf(new char[60]);
        Assert.assertTrue(Verification.verifyCountry(country));
    }

    @Test
    public void testVerifyGroupName(){
        String groupName = "mehome";
        Assert.assertTrue(Verification.verifyGroupName(groupName));

        groupName = "";
        Assert.assertFalse(Verification.verifyGroupName(groupName));

        groupName = "hello this is some message";
        Assert.assertTrue(Verification.verifyGroupName(groupName));

        try{
            groupName = null;
            Verification.verifyGroupName(groupName);
            Assert.fail();
        }catch(NullPointerException e){}

        groupName = String.valueOf(new char[31]);
        Assert.assertFalse(Verification.verifyGroupName(groupName));

        groupName = String.valueOf(new char[30]);
        Assert.assertTrue(Verification.verifyGroupName(groupName));
    }

    @Test
    public void testVerifyDirectory(){
        String dir = ".";
        Assert.assertTrue(Verification.verifyDirectory(dir));

        dir = ".asdf";
        Assert.assertFalse(Verification.verifyDirectory(dir));

        dir = "./asdf";
        Assert.assertFalse(Verification.verifyDirectory(dir));

        dir = "manifest.mf";
        Assert.assertFalse(Verification.verifyDirectory(dir));

        dir = "./";
        Assert.assertTrue(Verification.verifyDirectory(dir));

        dir = ".\\";
        Assert.assertTrue(Verification.verifyDirectory(dir));

        try{
            dir = null;
            Verification.verifyDirectory(dir);
            Assert.fail();
        }catch(NullPointerException e){}
    }

    @Test
    public void testVerifyFile(){
        String file = ".";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "src/Main.java";
        Assert.assertTrue(Verification.verifyFile(file));

        file = ".asdf";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./asdf";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./";
        Assert.assertFalse(Verification.verifyFile(file));

        file = ".\\";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./src/Main.java";
        Assert.assertTrue(Verification.verifyFile(file));

        file = ".\\src\\Main.java";
        Assert.assertTrue(Verification.verifyFile(file));

        try{
            file = null;
            Verification.verifyFile(file);
            Assert.fail();
        }catch(NullPointerException e){}
    }

}