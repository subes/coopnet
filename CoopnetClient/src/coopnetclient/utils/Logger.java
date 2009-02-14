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
package coopnetclient.utils;

import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.enums.ServerProtocolCommands;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class Logger {

    private static final ArrayList<String> LOG = new ArrayList<String>();
    private static final int TAIL_LENGTH = 300;

    private Logger() {
    }

    public static String getEndOfLog() {
        String ret = new String();
        for (int i = 0; i < LOG.size(); i++) {
            ret += LOG.get(i) + "\n";
        }
        return ret;
    }

    public static void logInTraffic(final ServerProtocolCommands command, final String[] information) {
        String message = command.toString() + " ";
        if (information.length != 0) {
            message += "[";
            for (int i = 0; i < information.length; i++) {
                if (i != 0) {
                    message += "|";
                }
                message += information[i];
            }
            message += "]";
        }
        log(LogTypes.IN, message);
    }

    public static void logInTraffic(final String[] data) {
        String message = data[0] + " ";
        if (data.length != 1) {
            message += "[";
            for (int i = 1; i < data.length; i++) {
                if (i != 1) {
                    message += "|";
                }
                message += data[i];
            }
            message += "]";
        }
        log(LogTypes.IN, message);
    }

    public static void logOutTraffic(final String logString) {
        log(LogTypes.OUT, logString);
    }

    public static void log(final String message) {
        Logger.log(LogTypes.LOG, message);
    }

    public static void logErr(final String errorMessage) {
        Logger.log(LogTypes.ERROR, errorMessage);
    }

    public static void log(final LogTypes type, final String message) {
        String newMessage = message.trim();
        while (newMessage.endsWith("\n\n")) {
            newMessage = newMessage.substring(0, newMessage.length() - 1);
        }

        String entry = getHeader(type) + newMessage;

        if (Globals.getDebug()) {
            if (type == LogTypes.ERROR) {
                System.err.println(entry);
            } else {
                System.out.println(entry);
            }
        }
        append(entry);
    }

    public static void log(final Throwable exception) {
        String entry = "\n" + exception.getClass().toString() + ": " + exception.getMessage();

        StackTraceElement[] trace = exception.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            entry += "\n\tat " + trace[i].toString();
        }

        Throwable cause = exception.getCause();
        while (cause != null) {
            entry += "\nCaused by - " + cause.getClass().toString() + ": " + cause.getMessage();
            trace = cause.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                entry += "\n\tat " + trace[i].toString();
            }

            cause = cause.getCause();
        }

        log(LogTypes.ERROR, entry);
    }

    private static void append(final String entry) {
        if (LOG.size() == TAIL_LENGTH) {
            LOG.remove(0);
        }

        LOG.add(entry);
    }

    private static String getHeader(final LogTypes type) {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss.SSS");
        String entry = date.format(new Date()) + "\t" + type + ":\t";

        return entry;
    }
}

