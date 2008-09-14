package coopnetclient.protocol;

public enum ClientProtocolCommands {
    SEND_REQUEST,
    ACCEPT_REQUEST,
    REFUSE_REQUEST,
    CREATE_GROUP,
    DELETE_CONTACT,
    RENAME_GROUP,
    DELETE_GROUP,
    MOVE_TO_GROUP,
    REFRESH_CONTACTS,
    ACCEPT_FILE,
    REFUSE_FILE,
    CANCEL_FILE,
    CREATE_ROOM,
    JOIN_ROOM,
    JOIN_ROOM_BY_ID,
    SET_GAMESETTING,
    SET_SLEEP,
    REFRESH_ROOMS_AND_PLAYERS,
    LIST_CHANNELS,
    JOIN_CHANNEL,
    LEAVE_CHANNEL,
    CHANGE_PASSWORD,
    EDIT_PROFILE,
    QUIT,
    LOGIN,
    NEW_PLAYER,
    CHAT_MAIN,
    CHAT_ROOM,
    WHISPER,
    KICK,
    BAN,
    UNBAN,
    MUTE,
    UNMUTE,
    INVITE_USER,
    TURN_AROUND_FILE,
    REQUEST_PROFILE,
    NUDGE,
    SET_EMAIL,
    SET_EMAIL_IS_PUBLIC,
    SET_COUNTRY,
    SET_WEBSITE,
    SET_INGAMENAME,
    SET_LOGINNAME,
    GAME_CLOSED,
    CLOSE_ROOM,
    LEAVE_ROOM,
    LAUNCH,
    FLIP_READY,
    SEND_FILE,
}
