package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Coordinates;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ViewController {
    void getPlayerChoice(boolean yourTurn);

    void updatedPlayerList(List<String> players);
    void updateGame(GameUpdate update);
    void serverError(String message);
    int setNumPlayers(int numPlayers); //It calls setNumPlayers;
    void setNickname(String nickname);
    Pair<List<Coordinates>, Integer> getPlayerChoice();
}
