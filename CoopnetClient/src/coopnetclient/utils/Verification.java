/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
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
import coopnetclient.protocol.out.Protocol;
import java.io.File;

public class Verification {


    public static boolean verifyProtocolVersion(String version){
        if(version == null){
            return false;
        }

        try{
            int v = Integer.parseInt(version);
            if(v != Protocol.PROTOCOL_VERSION){
                return false;
            }
        }catch(NumberFormatException e){
            Logger.log(e);
            return false;
        }

        return true;
    }

    public static boolean verifyClientVersion(String checkAgainst) {
        if(checkAgainst == null){
            return false;
        }

        //Here we have a number scheme x.x.x
        String[] checkAgainstSplit = checkAgainst.split("\\.");
        String[] clientVersionSplit = Globals.CLIENT_VERSION.split("\\.");

        try{
            for (int i = 0; i < clientVersionSplit.length ; i++) {
                if (Integer.parseInt(clientVersionSplit[i]) < Integer.parseInt(checkAgainstSplit[i])) {
                    return Integer.parseInt(clientVersionSplit[i-1]) > Integer.parseInt(checkAgainstSplit[i-1]);
                }
            }
        }catch(NumberFormatException e){
            Logger.log(e);
            return false;
        }catch(ArrayIndexOutOfBoundsException e){
            Logger.log(e);
            return false;
        }

        return true;
    }

    public static boolean verifyPassword(String password) {
        if(password == null){
            return false;
        }

        if (password.length() < 5) {
            return false;
        }

        return true;
    }

    public static boolean verifyLoginName(String loginName) {
        if(loginName == null){
            return false;
        }

        return loginName.matches("\\p{Graph}{3,30}");
    }
    
    public static boolean verifyIngameName(String ingameName){
        if(ingameName == null){
            return false;
        }

        if(ingameName.length() < 1 || ingameName.length() > 30){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyEMail(String email){
        if(email == null){
            return false;
        }

        if(email.length() > 320 || email.length()<5){
            return false;
        }

        //See http://www.regular-expressions.info/email.html (more practical implementation of RFC 2822)
        return email.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    }
    
    public static boolean verifyWebsite(String website){
        if(website == null){
            return false;
        }

        if(website.length() > 320){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyCountry(String country){
        if(country == null){
            return false;
        }

        if(country.length() > 60){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyGroupName(String groupName){
        if(groupName == null){
            return false;
        }

        if(groupName.length() < 1 || groupName.length() > 30){
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyDirectory(String DirName){
        if(DirName == null){
            return false;
        }

        File dir = new File(DirName);
        if(dir.isDirectory() && dir.exists()){
            return true;
        }else{
            return false;
        }
    }
    
    public static boolean verifyFile(String fileName){
        if(fileName == null){
            return false;
        }

        File file = new File(fileName);
        if(file.isFile() && file.exists()){
            return true;
        }else{
            return false;
        }
    }
}
