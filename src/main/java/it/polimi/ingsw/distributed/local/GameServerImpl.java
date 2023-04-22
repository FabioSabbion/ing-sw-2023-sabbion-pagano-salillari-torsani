package it.polimi.ingsw.distributed.local;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.events.ControllerEvent;
import it.polimi.ingsw.distributed.Client;

import java.util.Map;

public class GameServerImpl {
    GameController gameController;
    Map<String, Client> nicknameClient;
    final int numPlayers;

    public GameServerImpl(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void register(String nickname, Client client) {
        nicknameClient.put(nickname, client);


    }

    public void update(Client client, ControllerEvent arg) {

    }
}
