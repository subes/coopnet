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
package coopnetserver.utils;

import coopnetserver.Globals;
import coopnetserver.data.connection.Connection;
import coopnetserver.data.player.Player;
import coopnetserver.enums.LogTypes;
import coopnetserver.enums.ClientProtocolCommands;
import coopnetserver.enums.ServerProtocolCommands;
import coopnetserver.exceptions.VerificationException;
import coopnetserver.protocol.out.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public final class Logger {

    private static final String DATE_FORMAT = "dd.MM.yyyy - HH:mm:ss.SSS";
    private static Vector<String> log = new Vector<String>();
    private static final int TAIL_LENGTH = 50;
    private static Vector<String> knownErrors = new Vector<String>();

    private Logger() {
    }

    private static String getEndOfLog() {
        String ret = new String();
        for (int i = 0; i < log.size(); i++) {
            ret += log.get(i) + "\n";
        }
        return ret;
    }

    private static void append(String entry) {
        if (log.size() >= TAIL_LENGTH) {
            log.remove(0);
        }

        log.add(entry);
    }

    public static void logInTraffic(ClientProtocolCommands command, String[] information, Connection originator) {
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

        log(LogTypes.IN, message, originator);
    }

    public static void logInTraffic(String[] data, Connection originator) {
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
        log(LogTypes.IN, message, originator);
    }

    public static void logOutTraffic(String logString, Connection originator) {
        log(LogTypes.OUT, logString, originator);
    }

    public static void logVerificationError(VerificationException exc, Connection originator) {
        new Message(originator, ServerProtocolCommands.VERIFICATION_ERROR);
        log(exc, originator);
    }

    public static void log(Throwable exception) {
        log(exception, null);
    }

    public static void log(Throwable exception, Connection originator) {
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

        if (exception instanceof VerificationException) {
            log(LogTypes.VERIFICATION, entry, originator);
        } else {
            log(LogTypes.ERROR, entry, originator);
        }

        if (!Globals.getDebug()) {
            //in production, report occuring errors by mail
            if (!knownErrors.contains(getLocalExceptionSource(exception))) {
                knownErrors.add(getLocalExceptionSource(exception));
                MailSender.sendBugReportMail("SERVER EXCEPTION", compileReport(exception));
            }
        }
    }

    public static void log(LogTypes type, String message, Connection originator) {
        String entry = getHeader(type, originator) + message;
        output(type, entry);
    }

    public static void log(String message, Connection originator){
        log(LogTypes.LOG, message, originator);
    }

    public static void logErr(String message, Connection originator){
        log(LogTypes.ERROR, message, originator);
    }

    public static void log(LogTypes type, String message) {
        String entry = getHeader(type) + message;
        output(type, entry);
    }

    public static void log(String message){
        log(LogTypes.LOG, message);
    }

    public static void logErr(String message){
        log(LogTypes.ERROR, message);
    }

    private static void output(LogTypes type, String entry) {
        if (Globals.getDebug()) {
            if (type == LogTypes.VERIFICATION || type == LogTypes.ERROR) {
                //CHECKSTYLE:OFF
                System.err.println(entry);
            } else {
                System.out.println(entry);
                //CHECKSTYLE:ON
            }
        } else {
            append(entry);
        }
    }

    private static String getHeader(LogTypes type, Connection originator) {
        if (originator == null) {
            return getHeader(type);
        }

        SimpleDateFormat date = new SimpleDateFormat(DATE_FORMAT);
        Player p = originator.getPlayer();

        String entry = "";
        if (p != null) {
            entry += date.format(new Date()) + "\t(IP=" + originator.getIpAddress() + "|PID=" + p.getPid() + "|LN=" + p.getLoginName() + "|V=" + originator.getClientVersion() + ")\t" + type.toString() + ":\t";
        } else {
            entry += date.format(new Date()) + "\t(IP=" + originator.getIpAddress() + "|V=" + originator.getClientVersion() + ")\t" + type.toString() + ":\t";
        }

        return entry;
    }

    private static String getHeader(LogTypes type) {
        SimpleDateFormat date = new SimpleDateFormat(DATE_FORMAT);

        String entry = date.format(new Date()) + "\t" + type.toString() + ":\t";

        return entry;
    }

    private static String getLocalExceptionSource(Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement se : stack) {
            if (se.toString().startsWith("coopnetserver")) {
                return se.toString();
            }
        }
        return stack[0].toString();
    }

    //Returns the final report as a String
    private static String compileReport(Throwable exc) {
        //Date
        Date date = new Date();
        String report = "Date:" +
                "\n\t" + date.toLocaleString() +
                "\n\t" + date.toGMTString();

        report += "\n\n******************************************************************************************\n";

        if (exc != null) {
            //Stacktrace
            report += "\nException that caused this report: ";
            report += "\n\t" + exc.getClass().toString() + ": " + exc.getMessage();

            StackTraceElement[] trace = exc.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                report += "\n\t\tat " + trace[i].toString();
            }

            if (exc.getCause() != null) {
                report += "\nThis exception has a cause, look at the log snippet to see it.";
            }

            //Log
            report += "\n\nLog snippet:";

            for (String line : getEndOfLog().split("\n")) {
                report += "\n\t" + line;
            }
        } else {
            report += "\nThere is no Exception for this report.";
        }

        return report;
    }
}

