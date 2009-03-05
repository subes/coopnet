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

package coopnetclient.threads;

import coopnetclient.Err;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

public abstract class EdtRunner extends ErrThread {

    @Override
    public final synchronized void start() {
        if(SwingUtilities.isEventDispatchThread()){
            super.start();
        }else{
            //CHECKSTYLE:OFF
            SwingUtilities.invokeLater(this);
            //CHECKSTYLE:ON
        }
    }

    public final void invokeLater(){
        this.start();
    }

    public final void invokeAndWait() throws InterruptedException {
        if(SwingUtilities.isEventDispatchThread()){
            super.start();
            this.join();
        }else{
            try{
                //CHECKSTYLE:OFF
                SwingUtilities.invokeAndWait(this);
                //CHECKSTYLE:ON
            }catch(InvocationTargetException e){
                Err.handle(e);
            }
        }
    }

}
