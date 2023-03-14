package it.polimi.ingsw.models;

public class Player {
    private String nickname;
    private PersonalGoalCard personalGoalCard;
    private Bookshelf bookshelf;

    public String getNickname() {
        return nickname;
    }

    public PersonalGoalCard getPersonalGoalCard() {
        return personalGoalCard;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }
}
