package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The Client interface defines the methods that can be invoked remotely by the server.
 */
public interface Client extends Remote {
    /**
     * Notifies the client with an updated list of players.
     * @param players The updated list of players.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void updatedPlayerList(List<String> players) throws RemoteException;

    /**
     * Notifies the client with an updated game state.
     * @param update The updated game state.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void updateGame(GameUpdate update) throws RemoteException;

    /**
     * Notifies the client about a server error message.
     * @param message The error message from the server.
     * @throws RemoteException  if a communication error occurs during the remote invocation.
     */
    void serverError(String message) throws RemoteException;

    /**
     * Requests the client to provide the number of players for the game.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void askNumPlayers() throws RemoteException;

    /**
     * Notifies the client to keep the connection alive.
     * @throws RemoteException throws RemoteException if a communication error occurs during the remote invocation.
     */
    void keepAlive() throws RemoteException;

    /**
     * Displays the ending scoreboard to the client.
     * @param gameUpdate The final game state for generating the scoreboard.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void showEndingScoreboard(GameUpdate gameUpdate) throws RemoteException;

    /**
     * Notifies the client with an updated list of messages in chat.
     * @param messageList The updated list of messages.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void sendMessagesUpdate(List<Message> messageList) throws RemoteException;
}
