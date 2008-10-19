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
import java.io.File;

public class Verification {

    public static boolean verifyClientVersion(String checkAgainst) {
        //Here we have a number scheme x.x.x
        String[] checkAgainstSplit = checkAgainst.split("\\.");
        String[] clientVersionSplit = Globals.getClientVersion().split("\\.");

        for (int i = 0; i < clientVersionSplit.length; i++) {
            if (Integer.parseInt(clientVersionSplit[i]) < Integer.parseInt(checkAgainstSplit[i])) {
                return false;
            }
        }

        return true;
    }

    public static boolean verifyPassword(String password) {
        if (password.length() < 5) {
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
        if(email.length() > 320 || email.length()<5){
            return false;
        }
        
        return true;
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
    
    public static boolean verifyDirectory(String DIRName){
        File dir = new File(DIRName);
        if(dir.isDirectory() && dir.exists()){
            return true;
        }else{
            return false;
        }
    }
    
    public static boolean verifyFile(String FileName){
        File file = new File(FileName);
        if(file.isFile() && file.exists()){
            return true;
        }else{
            return false;
        }
    }
}
