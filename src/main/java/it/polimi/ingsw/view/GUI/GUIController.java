package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GUIController implements ViewController {
    private Server server;
    private ClientImpl client;
    private String myNickname;
    private State currentState;
    private GameUpdate gameUpdate;
    private List<Coordinates> currentPickedTiles = new ArrayList<>();

    private int countUpdate = 0;

    @Override
    public void updatedPlayerList(List<String> players) {
        if (currentState != State.LOBBY) {
            GUI.showLobbyView(players);
            currentState = State.LOBBY;
        } else {
            GUI.updateLobby(players);
        }
    }

    @Override
    public void updateGame(GameUpdate update) {
        if (currentState != State.GAME) {
            gameUpdate = update;
            GUI.showGameView();
            currentState = State.GAME;
            GUI.updateGameView(gameUpdate, null);

        } else {
            // update [gameUpdate] with changes
            List<GuiParts> toRefresh = new ArrayList<>();
            this.gameUpdate = gameUpdate.copyWith(
                    myNickname,
                    toRefresh,
                    update.livingRoom(),
                    update.players(),
                    update.commonGoalCards(),
                    update.gameEnder(),
                    update.currentPlayer()
            );

            GUI.updateGameView(gameUpdate, toRefresh);

        }

        if (isMyTurn()) {
            currentPickedTiles = new ArrayList<>();
        }


    }

    @Override
    public void serverError(String message) {
        GUI.showToast(message);
    }

    @Override
    public void askNumPlayers() {
        GUI.showNumPlayersView();
        currentState = State.ASKNUMPLAYERS;
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

    public boolean pickTile(int x, int y) {
        if (currentPickedTiles.size() == 3) return false;

        currentPickedTiles.add(new Coordinates(x,y));
        return true;
    }

    public void depositTile(int x, int y) {
        currentPickedTiles.remove(new Coordinates(x,y));
    }

    public void chooseColumn(int c) {
        if (!gameUpdate.currentPlayer().nickname().equals(myNickname)) {
            GUI.showToast("It is not your turn");
            return;
        }
        if (currentPickedTiles.isEmpty()) {
            GUI.showToast("You must pick at least one tile");
            return;
        }

        try {
            server.playerMove(currentPickedTiles, c, client);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
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
        Map<String, Integer> playerPoints = new HashMap<>();

        for (String player: this.gameUpdate.players().stream().map(PlayerUpdate::nickname).toList()) {
            playerPoints.put(player, calculatePersonalGoalCardPoints(player));
        }

        for (CommonGoalCardUpdate card : this.gameUpdate.commonGoalCards()) {
            var cardPoints = calculateCommonGoalCardPoints(card.commonGoalCardID());

            for (String player : new ArrayList<>(playerPoints.keySet())) {
                playerPoints.put(player, playerPoints.get(player) + cardPoints.get(player));
            }
        }
        GUI.showScoreboardView(playerPoints);
    }

    public Map<String, Integer> calculateCommonGoalCardPoints(int cardID) {
        Map<String, Integer> playerPoints = new HashMap<>();

        CommonGoalCardUpdate commonGoalCard = this.gameUpdate.commonGoalCards().stream().filter(card -> card.commonGoalCardID() == cardID).findFirst().orElseThrow();

        for (PlayerUpdate player : this.gameUpdate.players()) {
            if (commonGoalCard.playerUpdateList().contains(player.nickname())){
                playerPoints.put(player.nickname(), CommonGoalCard.points[this.gameUpdate.players().size()][commonGoalCard.playerUpdateList().indexOf(player.nickname())]);
            }
            else {
                playerPoints.put(player.nickname(), 0);
            }
        }

        return playerPoints;
    }

    public int calculatePersonalGoalCardPoints(String playerNickname) {
        return this.gameUpdate.players().stream().filter(p -> p.nickname().equals(playerNickname)).findFirst().orElseThrow().personalGoalCard().point();
    }

    public boolean isMyTurn() {
        return gameUpdate.currentPlayer().nickname().equals(myNickname);
    }

    public String getMyNickname() {
        return myNickname;
    }

    public State getCurrentState() {
        return currentState;
    }

    public GameUpdate getGameUpdate() {
        return gameUpdate;
    }
}

