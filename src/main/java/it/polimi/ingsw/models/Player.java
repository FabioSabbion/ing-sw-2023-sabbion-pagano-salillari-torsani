package it.polimi.ingsw.models;


/**
 * Represents one player in the game
 */
public class Player {
    private final String nickname;
    private final PersonalGoalCard personalGoalCard;
    private final Bookshelf bookshelf;

    public Player(String nickname, PersonalGoalCard personalGoalCard) {
        this.nickname = nickname;
        this.personalGoalCard = personalGoalCard;
        this.bookshelf = new Bookshelf();
    }

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
