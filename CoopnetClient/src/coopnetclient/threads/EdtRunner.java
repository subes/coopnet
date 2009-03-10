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

import coopnetclient.ErrorHandler;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

public abstract class EdtRunner extends ErrThread {

    /**
     * Calls invokeLater() internally.
     */
    @Override
    public final synchronized void start() {
        this.invokeLater();
    }

    /**
     * Runs this Thread in EDT via invokeLater().
     *
     */
    public final void invokeLater(){
        //CHECKSTYLE:OFF
        SwingUtilities.invokeLater(this);
        //CHECKSTYLE:ON
    }

    /**
     * Runs this Thread in EDT via invokeAndWait().
     *
     * @throws java.lang.InterruptedException
     */
    public final void invokeAndWait() throws InterruptedException {
        try{
            //CHECKSTYLE:OFF
            SwingUtilities.invokeAndWait(this);
            //CHECKSTYLE:ON
        }catch(InvocationTargetException e){
            ErrorHandler.handle(e);
        }
    }

}
