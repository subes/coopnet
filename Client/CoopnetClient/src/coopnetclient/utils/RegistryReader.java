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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

public class RegistryReader {

    private static final String REG_UTIL = "reg ";
    private static final String REG_QUERY_UTIL = "query \"";
    private static final String REG_WRITE_UTIL = "add \"";
    private static final String REG_VALUE_UTIL = "\" /v \"";
    private static final String REG_WRITE_DATA_UTIL = "\" /f /d \"";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String REGDWORD_TOKEN = "REG_DWORD";
    private static String command = "";

    private RegistryReader() {
    }

    private static void init() {
        if (Globals.getOperatingSystem() == OperatingSystems.LINUX) {
            command += Globals.getWineCommand() + " ";
        }

        command += REG_UTIL;

    }

    public static String read(String fullPath) {
        String ret = null;
        try {
            if (command.isEmpty()) {
                init();
            }
            int idx = fullPath.lastIndexOf("\\");
            if (idx == -1) {
                return null;
            }
            String nodePath = fullPath.substring(0, idx);
            String key = fullPath.substring(idx + 1);
            String cmd = command + REG_QUERY_UTIL + nodePath + REG_VALUE_UTIL + key + "\"";

            //remove Wow6432Node from registry path as it is still working well on XP without it
            if (Globals.getOperatingSystem() != OperatingSystems.WINDOWS_7) {
                cmd = cmd.replace("Wow6432Node\\", "");
            }

            Process process = Runtime.getRuntime().exec(cmd);

            Logger.log(LogTypes.REGISTRY, cmd);
            StreamReader reader = new StreamReader(process.getInputStream());

            process.waitFor();
            reader.start();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1) {
                p = result.indexOf(REGDWORD_TOKEN);
                if (p != -1) {
                    //read double
                    ret = result.substring(p + REGDWORD_TOKEN.length()).trim();
                }
            } else {
                //read string
                ret = result.substring(p + REGSTR_TOKEN.length()).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ret != null && ret.length() == 0) {
            ret = null;
        }
        Logger.log(LogTypes.REGISTRY, ret == null ? "null" : ret);
        return ret;
    }

    public static String readAny(ArrayList<String> regkeys) {
        if (regkeys == null) {
            return null;
        }
        for (String key : regkeys) {
            String path = read(key);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    public static void write(String nodePath, String keyName, String value) {
        try {
            if (command.isEmpty()) {
                init();
            }

            String cmd = command + REG_WRITE_UTIL + nodePath + REG_VALUE_UTIL + keyName + REG_WRITE_DATA_UTIL + value + "\"";

            //remove Wow6432Node from registry path as it is still working well on XP without it
            if (Globals.getOperatingSystem() == OperatingSystems.WINDOWS_XP) {
                cmd = cmd.replace("Wow6432Node\\", "");
            }

            Process process = Runtime.getRuntime().exec(cmd);
            Logger.log(LogTypes.REGISTRY, cmd);
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class StreamReader extends Thread {

        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) {
                    sw.write(c);
                }
            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        String getResult() {
            return sw.toString();
        }
    }
}
