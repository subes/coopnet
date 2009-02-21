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
package coopnetclient.utils.ipc;

import coopnetclient.utils.Logger;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class IPC {

    private static final String SERVER_NAME = "CoopnetClientRunningInstance";
    private static final String SERVER_URL = "//127.0.0.1:"+Registry.REGISTRY_PORT+"/" + SERVER_NAME;

    private IPC() {
    }

    public static boolean isAnotherInstanceAlreadyRunning() {
        try{
            String[] list = Naming.list(SERVER_URL);
            for(String l : list){
                if(l.contains(SERVER_NAME)){
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    public static void showClientFrameOfRunningInstance(){
        try{
            RmiServerI server = (RmiServerI) Naming.lookup(SERVER_URL);
            server.showClientFrame();
        }catch(Exception e){
            Logger.log(e);
        }
    }

    public static void registerAsRunningInstance(){
        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Naming.bind(SERVER_NAME, new RmiServerImpl());
        }catch(Exception e){
            Logger.log(e);
        }
    }
}
