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

package coopnetclient.utils.ui;

import coopnetclient.ErrorHandler;
import coopnetclient.protocol.in.CommandHandler;
import coopnetclient.protocol.out.Protocol;
import java.util.ArrayList;

public class SwingTask implements Runnable {

    private String command;
      private static StringBuilder sb = new StringBuilder();
    private static ArrayList<String> array = new ArrayList<String>();

    public static String[] getParts(String input) {
        sb.delete(0, sb.length());
        array.clear();
        int index = 0;
        while (index < input.length()) {
            if (input.charAt(index) == Protocol.INFORMATION_DELIMITER.charAt(0)) {//start of new token, store old
                array.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                sb.append(input.charAt(index));
            }
            index++;
        }
        array.add(sb.toString());
        String[] info = new String[array.size()];
        return array.toArray(info);
    }

    public SwingTask(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        try {
            String[] com = getParts(command);            
            CommandHandler.execute(com);
        } catch (Exception e) {
            ErrorHandler.handle(e);
        }
    }
}
