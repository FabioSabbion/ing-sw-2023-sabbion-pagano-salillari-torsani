package it.polimi.ingsw.events;

/**
 * enumerates the types of the events observable throughout the event
 */
public enum EventType {
    CONNECT,
    DISCONNECT,
    PICK_TILES,
    CHOOSE_COLUMN,
    NUM_PLAYERS,
    LOBBY_UPDATE,
    GAME_END,
    ACTION_UPDATE,
    GAME_STATE,
    CONNECT_ERROR,
    LOBBY_ERROR,
    KEEP_ALIVE,
    MESSAGE_EVENT
}
