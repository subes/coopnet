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

package coopnetclient.enums;

public enum ServerProtocolCommands { 
    CONTACT_REQUEST,
    SET_CONTACTSTATUS,
    ACCEPTED_REQUEST,
    REFUSED_REQUEST,
    CONTACT_LIST,
    MUTE_BAN_LIST,
    SENDING_FILE,
    ACCEPTED_FILE,
    REFUSED_FILE,
    CANCELED_FILE,
    REQUEST_PASSWORD,
    WRONG_ROOM_PASSWORD,
    CHAT_MAIN,
    CHAT_ROOM,
    INSTANT_LAUNCH,
    SET_GAMESETTING,
    TURN_AROUND_FILE,
    JOIN_CHANNEL,
    LOG_OFF,
    ADD_TO_PLAYERS,
    ON_CHANNEL,
    CHAT_PRIVATE,
    GAME_CLOSED,
    NUDGE,
    KICKED,
    LEFT_ROOM,
    ECHO,
}
