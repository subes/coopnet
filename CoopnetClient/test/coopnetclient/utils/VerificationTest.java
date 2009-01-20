package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.protocol.out.Protocol;
import org.junit.Assert;
import org.junit.Test;

public class VerificationTest {

    @Test
    public void testVerifyProtocolVersion(){

        String protocolVersion = String.valueOf(Protocol.PROTOCOL_VERSION);
        Assert.assertTrue(Verification.verifyProtocolVersion(protocolVersion));

        protocolVersion = String.valueOf(Protocol.PROTOCOL_VERSION+1);
        Assert.assertFalse(Verification.verifyProtocolVersion(protocolVersion));

        protocolVersion = String.valueOf(Protocol.PROTOCOL_VERSION-1);
        Assert.assertFalse(Verification.verifyProtocolVersion(protocolVersion));

        protocolVersion = "";
        Assert.assertFalse(Verification.verifyProtocolVersion(protocolVersion));

        protocolVersion = null;
        Assert.assertFalse(Verification.verifyProtocolVersion(protocolVersion));

        protocolVersion = "notanumber";
        Assert.assertFalse(Verification.verifyProtocolVersion(protocolVersion));
    }

    @Test
    public void testVerifyClientVersion(){
        String clientVersion = Globals.CLIENT_VERSION;
        Assert.assertTrue(Verification.verifyClientVersion(clientVersion));

        clientVersion = "";
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        clientVersion = null;
        Assert.assertFalse(Verification.verifyClientVersion(clientVersion));

        String[] versionSplit = Globals.CLIENT_VERSION.split("\\.");
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

        password = null;
        Assert.assertFalse(Verification.verifyPassword(password));
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

        loginName = null;
        Assert.assertFalse(Verification.verifyLoginName(loginName));

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

        ingameName = null;
        Assert.assertFalse(Verification.verifyIngameName(ingameName));

        
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

        email = null;
        Assert.assertFalse(Verification.verifyEMail(email));

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

        website = null;
        Assert.assertFalse(Verification.verifyWebsite(website));

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

        country = null;
        Assert.assertFalse(Verification.verifyCountry(country));

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

        groupName = null;
        Assert.assertFalse(Verification.verifyGroupName(groupName));

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
    }

    @Test
    public void testVerifyFile(){
        String file = ".";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "manifest.mf";
        Assert.assertTrue(Verification.verifyFile(file));

        file = ".asdf";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./asdf";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./";
        Assert.assertFalse(Verification.verifyFile(file));

        file = ".\\";
        Assert.assertFalse(Verification.verifyFile(file));

        file = "./manifest.mf";
        Assert.assertTrue(Verification.verifyFile(file));

        file = ".\\manifest.mf";
        Assert.assertTrue(Verification.verifyFile(file));
    }

}