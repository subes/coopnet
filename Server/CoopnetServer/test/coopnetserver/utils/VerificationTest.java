package coopnetserver.utils;

import coopnetserver.Globals;
import coopnetserver.test.AbstractCoopnetServerTest;
import org.junit.Assert;
import org.junit.Test;

public class VerificationTest extends AbstractCoopnetServerTest {

    @Test
    public void testVerifyPassword(){
        String password = "1234567890123456789012345678901234567890";
        Assert.assertTrue(Verification.verifyPassword(password));

        password = "123456789012345678901234567890123456789";
        Assert.assertFalse(Verification.verifyPassword(password));

        password = "12345678901234567890123456789012345678901";
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

        loginName = "€€?=?-A?4?<©L®"; //Special characters
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

        ingameName = "€€?=?-A?4?<©L®"; //Special characters
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

}