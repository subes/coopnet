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
package coopnetserver;

public final class Globals {

    public static final String DEVELOPMENT_VERSION = "DEVELOPMENT";

    private static String serverVersion;
    private static String compatibilityVersion;
    private static String ip;
    private static int port;
    private static boolean debug;

    private Globals(){}

    public static void preInit(){
        UncaughtExceptionHandler.init();

        //Detect Clientversion
        Package thisPackage = Globals.class.getPackage();
        String implementationVersion = thisPackage.getImplementationVersion();
        if(implementationVersion != null){
            serverVersion = implementationVersion;
        }else{
            serverVersion = DEVELOPMENT_VERSION;
        }
        //Detect Compatibilityversion
        String specificationVersion = thisPackage.getSpecificationVersion();
        if(specificationVersion != null){
            compatibilityVersion = specificationVersion;
        }else{
            compatibilityVersion = DEVELOPMENT_VERSION;
        }
    }
    
    public static String getServerVersion(){
        return serverVersion;
    }

    public static String getCompatibilityVersion(){
        return compatibilityVersion;
    }
    
    public static void setIP(String ip){
        Globals.ip = ip;
    }
    public static String getIP(){
        return ip;
    }
    
    public static void setPort(int port){
        Globals.port = port;
    }
    public static int getPort(){
        return port;
    }
    
    public static void setDebug(boolean value){
        debug = value;
    }
    public static boolean getDebug(){
        return debug;
    }
    
}
