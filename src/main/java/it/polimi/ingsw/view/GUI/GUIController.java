package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.view.CLI.CLIController;
import it.polimi.ingsw.view.CLI.utils.Color;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.util.List;

import static java.lang.Integer.parseInt;

public class GUIController implements ViewController {
    private Server server;
    private ClientImpl client;
    private String myNickname;

    @Override
    public void updatedPlayerList(List<String> players) {
        GUI.showLobbyPage(players);
    }

    @Override
    public void updateGame(GameUpdate update) {

    }

    @Override
    public void serverError(String message) {
        System.out.println(message);
    }

    @Override
    public void askNumPlayers() {
        GUI.showAskNumPlayersPage();
    }

    public void setNumPlayers(int numPlayers) {
        try {
            server.setNumPlayers(numPlayers, client);
        } catch (NumberFormatException e) {
            this.serverError("You must choose a number");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNickname(String nickname) {
        try {

            if (!nickname.matches("[A-Za-z0-9]+")) {
                System.out.println("Inadmissible choice of characters! Try Again...");
                return;
            }

            server.setNickname(nickname, client);
            myNickname = nickname;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getPlayerChoice(boolean yourTurn, String menuChoice) {

    }

    @Override
    public void start(ClientImpl client, Server server) {
        this.client = client;
        this.server = server;

        client.run(this);
    }

    @Override
    public void showEndingScreen() {

    }

    public String getMyNickname() {
        return myNickname;
    }
}