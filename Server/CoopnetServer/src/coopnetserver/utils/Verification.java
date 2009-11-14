package coopnetserver.utils;


public class Verification {

    public static boolean verifyPassword(String password) {
        if (password.length() != 40) {
            return false;
        }

        return true;
    }

    public static boolean verifyLoginName(String loginName) {
        return loginName.matches("\\p{Graph}{3,30}");
    }
    
    public static boolean verifyIngameName(String ingameName){
        if(ingameName.length() < 1 || ingameName.length() > 30){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyEMail(String email){
        if(email.length() > 320){
            return false;
        }

        //See http://www.regular-expressions.info/email.html (more practical implementation of RFC 2822)
        return email.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    }
    
    public static boolean verifyWebsite(String website){
        if(website.length() > 320){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyCountry(String country){
        if(country.length() > 60){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyGroupName(String groupName){
        if(groupName.length() < 1 || groupName.length() > 30){
            return false;
        }
        
        return true;
    }
}
