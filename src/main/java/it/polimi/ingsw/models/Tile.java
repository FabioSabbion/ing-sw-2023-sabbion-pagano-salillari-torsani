package it.polimi.ingsw.models;

import java.io.Serializable;

/**
 * Represents a <b>Tile</b> inside the game, that can be placed either on the board, in a <b>Player</b>'s <b>Bookshelf</b>, or can be out of the game
 */
public record Tile(Category category, Icon icon, Orientation orientation) implements Serializable {
}
