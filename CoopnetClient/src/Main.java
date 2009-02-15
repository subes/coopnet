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

import coopnetclient.Client;
import coopnetclient.Globals;
import coopnetclient.enums.LogTypes;
import coopnetclient.utils.Logger;
import coopnetclient.utils.Settings;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Main {
    private static final String DEBUG = "debug";
    private static final String HELP = "help";
    private static final String IP_PORT_SEPARATOR = ":";
    private static final String SAFEMODE = "safemode";
    private static final String SERVER = "server";

    private Main() {
    }

    public static void main(String[] args) {
        //See if we have security problems
        try {
            System.getProperty("os.name");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null,
                    "An error occured while trying to detect your operating system!" +
                    "\nPlease make sure that your security policy in java is not set too tight." +
                    "\nException message: " + e.getLocalizedMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        Globals.detectOperatingSystem();
        checkArgs(args);
        Globals.init();

        Logger.log(LogTypes.LOG, "Starting ...");

        cleanUpdater();

        Client.startup();
    }

    private static void checkArgs(String[] args) {

        Options options = createCommandlineOptions();

        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption(SAFEMODE)){ //Reset has to be first
                Settings.resetSettings();
            }
            if(cmd.hasOption(HELP)){
                printHelp(options);
            }
            if(cmd.hasOption(SERVER)){
                String value = cmd.getOptionValue(SERVER);
                String ip = value.substring(0, value.indexOf(IP_PORT_SEPARATOR));
                Globals.setServerIP(ip);
                int port = Integer.parseInt(value.substring(value.indexOf(IP_PORT_SEPARATOR) + 1));
                Globals.setServerPort(port);
            }
            if(cmd.hasOption(DEBUG)){
                Globals.enableDebug();
            }
        } catch (ParseException ex) {
            //CHECKSTYLE:OFF
            System.err.println( "Parsing failed. Reason: " + ex.getMessage() );
            //CHECKSTYLE:ON
            System.exit(1);
        } catch (Exception ex){
            printHelp(options);
        }

        printHelp(options);
    }

    @SuppressWarnings("static-access")
    private static Options createCommandlineOptions() {
        Options options = new Options();

        Option safemode = OptionBuilder.withDescription("resets all settings")
                .create(SAFEMODE);

        Option server = OptionBuilder.withDescription("ip and port of the server to connect to (e.g. 127.0.0.1:6667)")
                .hasArg()
                .withArgName("ip:port")
                .create(SERVER);

        Option debug = OptionBuilder.withDescription("print debug messages during operation")
                .create(DEBUG);

        Option help = OptionBuilder.withDescription("print this message")
                .create(HELP);

        options.addOption(safemode);
        options.addOption(server);
        options.addOption(debug);
        options.addOption(help);

        return options;
    }

    private static void printHelp(Options options) {

        //CHECKSTYLE:OFF
        System.out.println("CoopnetClient, version "+Globals.CLIENT_VERSION);
        //CHECKSTYLE:ON
        new HelpFormatter().printHelp("java -jar CoopnetClient.jar",
                        "options:",
                        options,
                        "Visit our project website at \"http://coopnet.sourceforge.net\".", true);
        System.exit(0);
    }

    private static void cleanUpdater() {
        final File tmpDir = new File("./UPDATER_TMP");
        final File updaterFile = new File("./CoopnetUpdater.jar");

        if (tmpDir.exists() || updaterFile.exists()) {
            Logger.log(LogTypes.LOG, "Updater files queued for deletion ...");
            new Thread() {
                private static final int UPDATER_CLOSING_SLEEP = 1000;

                @Override
                public void run() {
                    try {
                        sleep(UPDATER_CLOSING_SLEEP);
                    } catch (InterruptedException ex) {
                        Logger.log(ex);
                    }
                    try {
                        if (tmpDir.exists()) {
                            Logger.log(LogTypes.LOG, "Deleting ./UPDATER_TMP recursively");
                            deleteFile(tmpDir);
                        }
                    } catch (IOException e) {
                        Logger.log(e);
                    }
                    try {
                        if (updaterFile.exists()) {
                            Logger.log(LogTypes.LOG, "Deleting ./CoopnetUpdater.jar");
                            deleteFile(updaterFile);
                        }
                    } catch (IOException e) {
                        Logger.log(e);
                    }
                }
            }.start();
        }
    }

    private static boolean deleteFile(File resource) throws IOException {

        if (resource.isDirectory()) {
            File[] childFiles = resource.listFiles();
            for (File child : childFiles) {
                deleteFile(child);
            }
        }

        return resource.delete();

    }
}
