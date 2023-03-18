package it.polimi.ingsw.models;

/**
 * Represents a <b>Tile</b> inside the game, that can be placed either on the board, in a <b>Player</b>'s <b>Bookshelf</b>, or can be out of the game
 */
public class Tile {
    private Category category;
    private Icon icon;
    private Orientation orientation;

    /**
     * @return the <b>Category</b> of a <b>Tile</b>
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return the <b>Icon</b> of a <b>Tile</b>
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * @return the <b>Orientation</b> of a <b>Tile</b>
     */
    public Orientation getOrientation() {
        return orientation;
    }
}
