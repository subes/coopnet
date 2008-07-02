/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com), 
                    Kovacs Zsolt (kovacs.zsolt.85@gmail.com)

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

import coopnetclient.Client;

public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    public static void main(String[] args) {
        checkArgs(args);
        Client.startup();
    }
    
    private static void checkArgs(String[] args){
        if(args.length > 0){
            if(args[0].equals("--debug")){
                Client.debug = true;
            }else
            if(args[0].equals("--help")){
                printHelp();
                System.exit(0);
            }
            else{
                printHelp();
                System.exit(0);
            }
        }
    }
    
    private static void printHelp(){
        System.out.println( "CoopnetClient usage:\n" +
                            "    java -jar CoopnetClient.jar [--debug]\n" +
                            "\n" +
                            "    --debug    print debug messages during operation\n" +
                            "    --help     print this help and exit\n");
    }
}
