package it.polimi.ingsw.models;

/**
 * Represents one player in the game
 */
public class Player {
    private String nickname;
    private PersonalGoalCard personalGoalCard;
    private Bookshelf bookshelf;

    /**
     * @return the <b>Nickname</b> of the player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the <b>Personal Goal Card</b> of the player
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return personalGoalCard;
    }

    /**
     * @return the <b>Bookshelf</b> of the player
     */
    public Bookshelf getBookshelf() {
        return bookshelf;
    }
}
