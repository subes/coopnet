/*	Copyright 2007  Edwin Stang (edwinstang@gmail.com),
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
import coopnetclient.enums.OperatingSystems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class RegistryReader {

    private static Process handler;
    private static OutputStream out;
    private static BufferedReader in;

    private RegistryReader(){}

    private static void init(){
        String command = "";

        if(Globals.getOperatingSystem() == OperatingSystems.LINUX){
            command += Globals.getWineCommand();
        }

        command += " lib/registryreader.exe";

        Logger.log(LogTypes.REGISTRY, command);

        try{
            handler = Runtime.getRuntime().exec(command);
            out = handler.getOutputStream();
            in = new BufferedReader(new InputStreamReader(handler.getInputStream()));
        }catch(IOException ex){
            Logger.log(ex);
        }
    }

    public static String read(String fullpath) {
        if(handler == null){
            init();
        }

        try {
            return communicateRead(fullpath);
        } catch (IOException ex) {
            Logger.log(ex);
            try {
                return retryRead(fullpath);
            } catch (IOException ex1) {
                Logger.log(ex1);
                return null;
            }
        }
    }

    private static String retryRead(String fullpath) throws IOException{
        if(handler != null){
            ProcessHelper.destroy(handler);
            handler = null;
        }

        if(out != null){
            try {
                out.close();
            } catch (IOException ex) {}
            out = null;
        }

        if(in != null){
            try {
                in.close();
            } catch (IOException ex) {}
            in = null;
        }

        init();

        return communicateRead(fullpath);
    }

    private static String communicateRead(String fullpath) throws IOException{

        write(fullpath);

        String ret = read();

        if(ret.startsWith("ERR")){
            return null;
        }else{
            return ret;
        }
    }

    private static void write(String toWrite) throws IOException{
        if(!toWrite.endsWith("\n")){
            toWrite += "\n";
        }

        Logger.log(LogTypes.REGISTRY, "OUT: "+toWrite);
        out.write(toWrite.getBytes());
        out.flush();

        toWrite = "DONE\n";
        Logger.log(LogTypes.REGISTRY, "OUT: "+toWrite);
        out.write(toWrite.getBytes());
        out.flush();
    }
    
    private static String read() throws IOException{
        String ret = in.readLine();
        Logger.log(LogTypes.REGISTRY, "IN: "+ret);
        return ret;
    }

    public static String readAny(ArrayList<String> regkeys) {
        if(regkeys == null){
            return null;
        }
        for(String key : regkeys ){
            String path = read(key);
            if(path != null){
                return path;
            }
        }
        return null;
    }

}
