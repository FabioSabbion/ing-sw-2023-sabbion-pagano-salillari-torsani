package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.LivingRoomUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.view.CLI.utils.Color;
import it.polimi.ingsw.view.ViewController;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class CLIController implements ViewController {
    List<PlayerUpdate> players;
    LivingRoomUpdate livingRoom;
    List<CommonGoalCard> commonGoalCards;
    PlayerUpdate currentPlayer;
    PlayerUpdate gameEnder;
    Server server;
    Client client;
    private CLI cli;

    String viewingPlayerNickname;

    @Override
    public void setNickname(String nickname) {
        try {
            Scanner scanner = new Scanner(System.in);
            server.setNickname(nickname, client);
            this.viewingPlayerNickname = nickname;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void getPlayerChoice(boolean yourTurn) {
        cli.menuChoice(calculatePoints(), currentPlayer, yourTurn);
    }

    public Pair<List<Coordinates>, Integer> getTiles(){
        List<Coordinates> chosenTiles = new ArrayList<>();
        int numTiles = cli.getNumberTiles();

        for (int i = 0; i < numTiles; i++) {
            chosenTiles.add(cli.getPlayerTileCoordinate());
        }
        int column = cli.getPlayerColumn();
        return new ImmutablePair<>(chosenTiles, column);
    }


    @Override
    public void updatedPlayerList(List<String> players) {
        players.stream().forEach(System.out::println);
    }

    @Override
    public void updateGame(GameUpdate update){

        this.livingRoom = update.livingRoomUpdate(); //Tile[][]
        this.players = update.players();
        this.commonGoalCards = update.commonGoalCards();
        this.currentPlayer = update.currentPlayer();
        this.gameEnder = update.gameEnder();



        if (cli == null) {
            PlayerUpdate viewingPlayer = players.stream().filter(p -> p.nickname().equals(viewingPlayerNickname))
                    .findFirst().get();
            cli = new CLI(this.livingRoom, this.players, this.commonGoalCards,
                    this.currentPlayer, this.gameEnder, viewingPlayer);
        }
        cli.updateAll(this.livingRoom, this.players, this.currentPlayer, this.gameEnder);
    }

    @Override
    public void serverError(String message){
        System.out.println(Color.RED.escape() + message + Color.RESET);
    }

    @Override
    public void setNumPlayers(int numPlayers){
        try {
            Scanner scanner = new Scanner(System.in);
            server.setNumPlayers(parseInt(scanner.nextLine()), client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<PlayerUpdate, Integer> calculatePoints(){
        HashMap<PlayerUpdate, Integer> playerPoints = new HashMap<>();
        for (PlayerUpdate player : this.players) {
            // TODO: IT'S ALL FAKE
            // CommmonGoal
            playerPoints.put(player, 10);
            // PersonalGoalCard
            playerPoints.put(player, 5);
        }

        return playerPoints;
    }


}

