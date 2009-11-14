package coopnetserver;

public final class Globals {

    public static final String DEVELOPMENT_VERSION = "DEVELOPMENT";

    private static String serverVersion;
    private static String compatibilityVersion;
    private static String ip;
    private static int port;
    private static boolean debug;

    private Globals(){}

    public static void preInit(){
        UncaughtExceptionHandler.init();

        //Detect Clientversion
        Package thisPackage = Globals.class.getPackage();
        String implementationVersion = thisPackage.getImplementationVersion();
        if(implementationVersion != null){
            serverVersion = implementationVersion;
        }else{
            serverVersion = DEVELOPMENT_VERSION;
        }
        //Detect Compatibilityversion
        String specificationVersion = thisPackage.getSpecificationVersion();
        if(specificationVersion != null){
            compatibilityVersion = specificationVersion;
        }else{
            compatibilityVersion = DEVELOPMENT_VERSION;
        }
    }
    
    public static String getServerVersion(){
        return serverVersion;
    }

    public static String getCompatibilityVersion(){
        return compatibilityVersion;
    }
    
    public static void setIP(String ip){
        Globals.ip = ip;
    }
    public static String getIP(){
        return ip;
    }
    
    public static void setPort(int port){
        Globals.port = port;
    }
    public static int getPort(){
        return port;
    }
    
    public static void setDebug(boolean value){
        debug = value;
    }
    public static boolean getDebug(){
        return debug;
    }
    
}
