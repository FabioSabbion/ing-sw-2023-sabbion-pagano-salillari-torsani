package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.Message;

import java.util.List;

/**
 * The ViewController interface defines the methods for updating and interacting with the game's view.
 * Implementing classes should provide implementations for these methods to handle various view-related tasks.
 */
public interface ViewController {
    /**
     * Updates the player list displayed in the view.
     *
     * @param players A list of player names.
     */
    void updatedPlayerList(List<String> players);

    /**
     * Updates the game state with the provided game update.
     *
     * @param update The game update containing the new state of the game.
     */
    void updateGame(GameUpdate update);

    /**
     * Notifies the view about a server error with the given error message.
     *
     * @param message The error message indicating the server error.
     */
    void serverError(String message);

    /**
     * Asks the view to prompt the user for the number of players.
     */
    void askNumPlayers();

    /**
     * Sets the nickname for the current player.
     *
     * @param nickname The nickname to set for the current player.
     */
    void setNickname(String nickname);

    /**
     * Asks the view to get the player's choice from a menu of options.
     *
     * @param yourTurn    A flag indicating whether it is the player's turn.
     * @param menuChoice  The available options for the player to choose from.
     */
    void getPlayerChoice(boolean yourTurn, String menuChoice);

    /**
     * Starts the game view with the provided client and server instances.
     *
     * @param client The client implementation used for communication with the server.
     * @param server The server instance responsible for managing the game logic.
     */
    void start(ClientImpl client, Server server);

    /**
     * Shows the ending screen of the game.
     */
    void showEndingScreen();

    /**
     * Receives a list of messages and updates the view accordingly.
     *
     * @param messages The list of messages to be displayed in the view.
     */
    void receiveMessages(List<Message> messages);
}
