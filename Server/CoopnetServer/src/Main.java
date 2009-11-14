import coopnetserver.Globals;
import coopnetserver.NioServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Main {

    private static final String IP_PORT_SEPARATOR = ":";

    private static final String DEBUG = "debug";
    private static final String HELP = "help";
    private static final String BIND = "bind";

    private Main(){}

    public static void main(String[] args) {
        Globals.preInit();
        checkArgs(args);
        NioServer.startup();
    }
    
    private static void checkArgs(String[] args) {

        Options options = createCommandlineOptions();

        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption(HELP)){
                printHelp(options);
                System.exit(0);
            }
            if(cmd.hasOption(BIND)){
                String value = cmd.getOptionValue(BIND);
                String ip = value.substring(0, value.indexOf(IP_PORT_SEPARATOR));
                Globals.setIP(ip);
                int port = Integer.parseInt(value.substring(value.indexOf(IP_PORT_SEPARATOR) + 1));
                Globals.setPort(port);
            }
            if(cmd.hasOption(DEBUG)){
                Globals.setDebug(true);
            }
        } catch (ParseException ex) {
            //CHECKSTYLE:OFF
            System.out.println(ex.getMessage());
            System.out.println();
            //CHECKSTYLE:ON
            printHelp(options);
            System.exit(1);
        }
    }

    @SuppressWarnings("static-access")
    private static Options createCommandlineOptions() {
        Options options = new Options();

        Option bind = OptionBuilder.withDescription("ip and port to bind the server to (e.g. 127.0.0.1:6667)")
                .isRequired()
                .hasArg()
                .withArgName("ip:port")
                .withLongOpt(BIND)
                .create("b");

        Option debug = OptionBuilder.withDescription("print debug messages during operation")
                .withLongOpt(DEBUG)
                .create("d");

        Option help = OptionBuilder.withDescription("print this message")
                .withLongOpt(HELP)
                .create("h");

        options.addOption(bind);
        options.addOption(debug);
        options.addOption(help);

        return options;
    }

    private static void printHelp(Options options) {
        //CHECKSTYLE:OFF
        System.out.println("CoopnetServer, version "+Globals.getServerVersion());
        //CHECKSTYLE:ON
        new HelpFormatter().printHelp("java -jar CoopnetServer.jar",
                        "options:",
                        options,
                        "Visit our project website at \"http://coopnet.sourceforge.net\".", true);
    }
}
