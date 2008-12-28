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
    CONTACT_REQUESTED,
    SET_CONTACTSTATUS,
    ACCEPTED_CONTACT_REQUEST,
    REFUSED_CONTACT_REQUEST,
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
    LEFT_CHANNEL,
    ADD_TO_PLAYERS,
    ON_CHANNEL,
    CHAT_PRIVATE,
    GAME_CLOSED,
    NUDGE,
    KICKED,
    LEFT_ROOM,
    SERVER_SHUTTING_DOWN,
    NOT_READY_STATUS,
    READY_STATUS,
    ROOM_PLAYING_STATUS,
    CHANNEL_PLAYING_STATUS,
    EDIT_PROFILE,
    SHOW_PROFILE,
    UPDATE_PLAYERNAME,
    ERROR,
    PROFILE_SAVED,
    INGAMENAME,
    PASSWORD_CHANGED,
    JOINED_ROOM,
    OK_LOGIN,
    OK_REGISTER,
    LOGINNAME_IN_USE,
    LOGIN_INCORRECT,
    JOIN_ROOM,
    ADD_MEMBER_TO_ROOM,
    CREATE_ROOM,
    ADD_ROOM,
    LAUNCH,
    LEAVE_ROOM,
    CLOSE_ROOM,
    REMOVE_ROOM,
    REMOVE_MEMBER_FROM_ROOM,
    ECHO_BANNED,
    ECHO_UNBANNED,
    ECHO_MUTED,
    ECHO_UNMUTED,
    ECHO_NO_SUCH_PLAYER,
    ERROR_YOU_ARE_BANNED,
    ERROR_ROOM_IS_FULL,
    ERROR_ROOM_DOES_NOT_EXIST,
    ERROR_LOGINNAME_IS_ALREADY_USED,
    ERROR_INCORRECT_PASSWORD,
    ERROR_WHISPER_TO_OFFLINE_USER,
    VERIFICATION_ERROR,
    CRIPPLED_SERVER_MODE,
    CONTACT_REQUEST_ACKNOWLEDGE,
    CONTACT_ACCEPT_ACKNOWLEDGE,
    CONTACT_REFUSE_ACKNOWLEDGE,
    CONTACT_REMOVE_ACKNOWLEDGE,
    CONTACT_MOVE_ACKNOWLEDGE,
    GROUP_CREATE_ACKNOWLEDGE,
    GROUP_DELETE_ACKNOWLEDGE,
    GROUP_RENAME_ACKNOWLEDGE,
    REMOVE_CONTACT_REQUEST,
    ROOM_INVITE,
    YOUR_IP_IS
}
