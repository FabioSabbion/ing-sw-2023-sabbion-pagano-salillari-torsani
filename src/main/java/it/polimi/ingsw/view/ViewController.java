package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.GameUpdate;

import java.util.List;

public interface ViewController {
    void getPlayerChoice(boolean yourTurn);

    void updatedPlayerList(List<String> players);
    void updateGame(GameUpdate update);
    void serverError(String message);
    void setNumPlayers(int numPlayers); //It calls setNumPlayers;
    void setNickname(String nickname);
}
