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

import coopnetserver.protocol.out.Protocol;
import coopnetserver.data.player.Player;
import coopnetserver.data.player.PlayerData;
import coopnetserver.data.channel.ChannelData;
import coopnetserver.data.channel.Channel;
import coopnetserver.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public final class RuntimeConsoleCommands {

    private RuntimeConsoleCommands(){}

    public static void bindAndListen() {
        try {
            //TODO: make the port configurable via cli
            ServerSocket socket = new ServerSocket(6668, 5, InetAddress.getByName("localhost"));
            while (true) {
                try {
                    final Socket s = socket.accept();
                    new ErrThread() {
                        @Override
                        public void handledRun() throws Throwable {
                            startProcessing(s);
                        }
                    }.start();
                } catch (Exception e) {
                    Logger.log(e);
                }
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public static void startProcessing(Socket socket) throws IOException {

        //READING COMMANDS
        Scanner sc = new Scanner(socket.getInputStream());
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        while (sc.hasNext()) {
            String lastcommand = sc.nextLine();
            synchronized (RuntimeConsoleCommands.class) {
                try {
                    if (lastcommand.startsWith("startdropping")) {
                        if (!ConnectionDropper.isRunning()) {
                            new ConnectionDropper().start();
                        }
                        continue;
                    }

                    if (lastcommand.startsWith("stopdropping")) {
                        ConnectionDropper.setRunning(false);
                        continue;
                    }

                    if (lastcommand.startsWith("shutdown")) {
                        Protocol.sendServerShuttingDown();
                        System.exit(0);
                    }

                    if (lastcommand.startsWith("list online players")) {
                        pw.println("ONLINE PLAYERS:");
                        for (Player p : PlayerData.getOnlinePlayers()) {
                            pw.println(p.getLoginName());
                        }
                        pw.println("--");
                        pw.flush();
                        continue;
                    }

                    if (lastcommand.startsWith("reload channels")) {
                        ChannelData.load();
                        continue;
                    }

                    if (lastcommand.startsWith("start debug")) {
                        Globals.setDebug(true);
                        continue;
                    }

                    if (lastcommand.startsWith("stop debug")) {
                        Globals.setDebug(false);
                        continue;
                    }

                    if (lastcommand.startsWith("onlinecount")) {
                        pw.println("ONLINE PLAYERS:" + PlayerData.getOnlinePlayers().size());
                        pw.flush();
                        continue;
                    }

                    if (lastcommand.startsWith("list channels")) {
                        pw.println("OPEN CHANNELS:");
                        for (Channel ch : ChannelData.getChannels().values()) {
                            pw.println(ch.name);
                        }
                        pw.println("--");
                        pw.flush();
                        continue;
                    }

                    if (lastcommand.startsWith("help")) {
                        pw.println("\rServer version: " + Globals.getServerVersion());
                        pw.println("\rAvaible commands:");
                        pw.println("\rshutdown - stops the server\n" +
                                "\rlist online players - prints the online players\n" +
                                "\ronlinecount - the number of conencted cliens\n" +
                                "\rlist channels - lists the open channels\n" +
                                "\rstartdropping - start the connectiondropper thread\n" +
                                "\rstopdropping - stops the connectiondropper thread\n" +
                                "\rreload channels - scan the channellist file for new channels and add them\n");
                        pw.flush();
                        continue;
                    }
                    pw.println("unknown command (type help for usage):" + lastcommand);
                    pw.flush();
                } catch (Exception e) {
                    pw.println("error while executing console command:");
                    pw.flush();
                    e.printStackTrace();
                }
            }
        }
    }
}
