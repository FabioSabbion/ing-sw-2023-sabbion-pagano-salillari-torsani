package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.networking.Server;

import java.util.Arrays;
import java.util.Map;

/**
 * The GameServer class represents a game server that manages the connections between nicknames and server instances.
 *
 * It provides functionality to associate nicknames with server instances and create a game controller
 * for the connected players.
 */
public class GameServer {
    private Map<String, Server> nicknameConnection;
    private GameController gameController;

    GameServer(Map<String, Server> nicknameConnection) {
        this.nicknameConnection = nicknameConnection;

        this.gameController = new GameController(Arrays.asList(Arrays.toString(nicknameConnection.values().toArray())));
    }
}
