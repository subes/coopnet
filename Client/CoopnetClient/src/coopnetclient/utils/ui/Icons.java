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
package coopnetclient.utils.ui;

import coopnetclient.Globals;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public final class Icons {

    public static final ImageIcon coopnetNormalIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(Globals.getResourceAsString("data/icons/coopnet.png")));
    public static final ImageIcon dirIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon dirIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/folder_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon fileIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon fileIconHidden = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filechooser/file_hidden.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon chatIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/inchat.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon awayIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/away.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon lobbyIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/inlobby.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon lobbyIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/inlobby.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon gameIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/ingame.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon normalOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon normalPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon normalOpenRoomLaunchedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon normalPasswordedRoomLaucnhedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/lobby_private_busy.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon instantOpenRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon instantPasswordedRoomIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/instantlaunch_private.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    public static final ImageIcon contactListIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quickpanel/contacts.png")));
    public static final ImageIcon favouritesIconBig = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quickpanel/favourites.png")));
    public static final ImageIcon contactListIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quickpanel/contacts_small.png")));
    public static final ImageIcon favouritesIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/quickpanel/favourites_small.png")));
    public static final ImageIcon pendingRequestIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/pending_request.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon pendingContactIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/pending_contact.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon offlineIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/playerstatus/offline.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon downloadIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/download.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon uploadIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/upload.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    public static final ImageIcon transferIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/filetransfer/transfers.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon errorIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/error.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    public static final ImageIcon acceptIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/actions/accept.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH));
    public static final ImageIcon refuseIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/actions/decline.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH));
    public static final ImageIcon cancelIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/actions/cancel.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH));
    public static final ImageIcon tabCloseIconNormal = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/actions/close.png")));
    public static final ImageIcon tabCloseIconMouseOver = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/actions/close_mouseover.png")));
    public static final ImageIcon starred = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/favourites/starred.png")));
    public static final ImageIcon unstarred = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/favourites/unstarred.png")));
    public static final ImageIcon modIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/roomtype/mod.png")));
    public static final ImageIcon privateChatIconSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Globals.getResourceAsString("data/icons/nudge.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));

    private Icons(){}
}
