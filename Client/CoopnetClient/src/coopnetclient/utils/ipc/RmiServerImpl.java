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

import coopnetclient.frames.FrameOrganizer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServerImpl extends UnicastRemoteObject implements RmiServerI {

    public RmiServerImpl() throws RemoteException{
        super();
    }

    @Override
    public void showClientFrame() throws RemoteException {
        if(FrameOrganizer.getClientFrame() != null){
            FrameOrganizer.getClientFrame().setVisible(true);
            FrameOrganizer.getClientFrame().requestFocus();
        }
    }

}
