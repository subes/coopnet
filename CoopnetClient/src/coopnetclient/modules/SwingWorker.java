/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zolt (kovacs.zsolt.85@gmail.com)

    This file is part of CoopNet.

    CoopNet is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoopNet is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoopNet.  If not, see <http://www.gnu.org/licenses/>.
*/

package coopnetclient.modules;

import coopnetclient.CommandHandler;
import coopnetclient.ErrorHandler;

public class SwingWorker implements Runnable {

    private String command;

    public SwingWorker(String command) {
        this.command = command;
    }

    public void run() {
        try {
            CommandHandler.execute(command);
        } catch (Exception e) {
            ErrorHandler.handleException(e);
        }
    }
}
