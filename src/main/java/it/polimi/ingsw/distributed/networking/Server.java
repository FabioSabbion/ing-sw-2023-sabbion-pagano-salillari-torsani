package it.polimi.ingsw.distributed.networking;

import it.polimi.ingsw.models.Coordinates;

import javax.annotation.Nullable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The Server interface defines the methods that can be invoked remotely by the clients.
 */
public interface Server extends Remote {
    /**
     * Sets the nickname for a client.
     * @param nickname the nickname to set.
     * @param client The client requesting the nickname.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void setNickname(String nickname, Client client) throws RemoteException;

    /**
     * Sets the number of players for the game.
     * @param num The number of players to set.
     * @param client The client requesting the number of players change.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void setNumPlayers(int num, Client client) throws RemoteException;

    /**
     * Lets the client perform a move in the game.
     * @param coordinates The list of coordinates representing the player move.
     * @param column The column in which to place the tiles.
     * @param client The client performing the move.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void playerMove(List<Coordinates> coordinates, int column, Client client) throws RemoteException;

    /**
     * Sends a message to a specific recipient.
     * @param to the nickname of the recipient (null for broadcasting to all players).
     * @param message The message to send.
     * @param client The client sending the message.
     * @throws RemoteException if a communication error occurs during the remote invocation.
     */
    void sendMessageTo(@Nullable String to, String message, Client client) throws RemoteException;

    void checkConnection() throws RemoteException;
}
