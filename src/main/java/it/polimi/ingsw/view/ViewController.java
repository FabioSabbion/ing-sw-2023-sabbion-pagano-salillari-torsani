package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.GameUpdate;

import java.util.List;

public interface ViewController {
    void updatedPlayerList(List<String> players);
    void updateGame(GameUpdate update);

    void serverError(String message);
    void askNumPlayers(); //It calls setNumPlayers;

    void setNickname(String nickname);

    void getPlayerChoice(boolean yourTurn, String menuChoice);
}