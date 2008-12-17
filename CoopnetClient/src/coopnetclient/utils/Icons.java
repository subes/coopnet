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
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class Icons {

    public static ImageIcon CoopnetnormalIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(Globals.getResourceAsString("data/icons/coopnet.png")));
    public static ImageIcon dirIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static ImageIcon dirIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static ImageIcon fileIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static ImageIcon fileIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static ImageIcon chatIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/inchat.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon lobbyIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/inlobby.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon gameIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/ingame.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon normalOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon normalPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon normalOpenRoomLaunchedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon normalPasswordedRoomLaucnhedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon instantOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon instantPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static ImageIcon ContactListIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/contacts.png")));
    public static ImageIcon FavouritesIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/favourites.png")));
    public static ImageIcon ContactListIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/contacts_small.png")));
    public static ImageIcon FavouritesIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quicktab/favourites_small.png")));
    public static ImageIcon pendingRequestIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/pending_request.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon pendingContactIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/pending_contact.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon offlineIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/offline.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon downloadIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/download.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon uploadIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/upload.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static ImageIcon transferIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/transfers2.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
}
