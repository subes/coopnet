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
import java.util.List;
import javax.swing.SwingWorker;

//CHECKSTYLE:OFF
public abstract class ErrSwingWorker extends SwingWorker {
//CHECKSTYLE:ON

    @Override
    protected final Object doInBackground() throws Exception {
        try{
            return handledDoInBackground();
        }catch(Throwable e){
            ErrorHandler.handle(e);
            throw new Exception(e);
        }
    }

    protected abstract Object handledDoInBackground() throws Exception;

    @Override
    protected final void done() {
        try{
            super.done();
            handledDone();
        }catch(Throwable e){
            ErrorHandler.handle(e);
        }
    }

    protected void handledDone() throws Throwable {
        
    }

    @Override
    protected final void finalize() throws Throwable {
        try{
            super.finalize();
            handledFinalize();
        }catch(Throwable e){
            ErrorHandler.handle(e);
        }
    }

    protected void handledFinalize() throws Throwable {
        
    }

    @Override
    protected final void process(List chunks) {
        try{
            super.process(chunks);
            handledProcess(chunks);
        }catch(Throwable e){
            ErrorHandler.handle(e);
        }
    }

    protected void handledProcess(List chunks) {

    }
}
