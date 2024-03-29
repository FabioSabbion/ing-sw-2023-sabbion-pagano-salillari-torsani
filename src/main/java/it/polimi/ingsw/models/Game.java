package it.polimi.ingsw.models;

import it.polimi.ingsw.GameUpdateToFile;
import it.polimi.ingsw.events.ViewEvent;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.utils.Observable;
import it.polimi.ingsw.utils.Observer;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Contains all game information dasds
 */
public class Game extends Observable<GameUpdateToFile, ViewEvent> {
    private @Nullable Player gameEnder;
    private Player currentPlayer;
    private final Player[] players;
    private final CommonGoalCard[] commonGoalCards;
    private final List<Tile> remainingTiles;
    private final LivingRoom livingRoom;

    public Game(Player[] players, CommonGoalCard[] commonGoalCards, List<Tile> remainingTiles, LivingRoom livingRoom, Player currentPlayer) {
        this.players = players;
        this.commonGoalCards = commonGoalCards;
        this.remainingTiles = remainingTiles;
        this.livingRoom = livingRoom;
        this.currentPlayer = currentPlayer;

        for (var player: this.players) {
            player.addObserver(new Observer<PlayerUpdate, ViewEvent>() {
                @Override
                public void update(PlayerUpdate value, ViewEvent eventType) {
                    notifyObservers(new GameUpdateToFile(
                            new GameUpdate(null, Arrays.asList(value), null, null, null), null),
                        eventType);
                }
            });
        }

        this.livingRoom.addObserver(new Observer<LivingRoom, ViewEvent>() {
            @Override
            public void update(LivingRoom value, ViewEvent eventType) {
                notifyObservers(new GameUpdateToFile(new GameUpdate(value, null, null, null, null), remainingTiles), eventType);
            }
        });

        Arrays.stream(this.commonGoalCards).forEach(commonGoalCard -> {
            commonGoalCard.addObserver((value, eventType) -> {
                notifyObservers(new GameUpdateToFile(new GameUpdate(null, null, List.of(value), null, null), null), eventType);
            });
        });
    }

    /**
     * creates a new empty game with the players specified
     * @param nicknames the list of nicknames of the players in the game
     * @return the {@link Game} created
     */
    static public Game createEmptyGame(List<String> nicknames) {
        var personalGoalCards = PersonalGoalCard.buildFromJson();

        Collections.shuffle(personalGoalCards);

        ArrayList<Player> players = new ArrayList<>(nicknames.size());

        for(int i = 0; i < nicknames.size(); i++) {
            players.add(new Player(nicknames.get(i), personalGoalCards.get(i)));
        }

        System.out.println("Players: " + players);

        // Create tiles
        Random rand = new Random();
        List<Tile> tiles = new ArrayList<>();
        for (Category c: Category.values()) {
            for (int i = 0; i < 22; i++) {
                tiles.add(new Tile(c, Icon.values()[Math.abs(rand.nextInt()) % Icon.values().length],
                        Orientation.values()[Math.abs(rand.nextInt()) % Icon.values().length]
                ));
            }
        }
        Collections.shuffle(tiles);

        var livingRoom = new LivingRoom();
        livingRoom.fillBoard(players.size(), tiles);
        return new Game(players.toArray(new Player[0]), CommonGoalCardFactory.getCommonGoalCard(players.size()), tiles, livingRoom, players.get(0));
    }

    public @Nullable Player getGameEnder() {
        return gameEnder;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public CommonGoalCard[] getCommonGoalCards() {
        return commonGoalCards;
    }

    public LivingRoom getLivingRoom() {
        return livingRoom;
    }

    public List<Tile> getRemainingTiles() {
        return remainingTiles;
    }

    /**
     * Change <b>currentPlayer</b> to the next player. Order is defined by <b>players</b>
     */
    public void nextPlayer(List<String> offlineNicknames) {
        int index = Arrays.asList(this.players).indexOf(this.currentPlayer);
        this.currentPlayer = this.players[(index + 1) % this.players.length];

        if (offlineNicknames.contains(this.currentPlayer.getNickname())) {
            this.nextPlayer(offlineNicknames);
        } else {
            //        if the game is ended we want to send the personalGoalCard so that the client can show you the final scoreboard
            notifyObservers(
                    new GameUpdateToFile(new GameUpdate(
                            null,
                            this.isEnded() ?
                                    Arrays.stream(this.players).map(player -> PlayerUpdate.from(player, true)).toList() : null,
                            null,
                            null,
                            PlayerUpdate.from(this.currentPlayer, this.isEnded())),
                            null),
                    this.isEnded() ? ViewEvent.GAME_END : ViewEvent.ACTION_UPDATE
            );
        }
    }

    /**
     * set the emitGameState of this object to false
     */
    public void emitGameState() {
        this.emitGameState(false);
    }

    /**
     * emits the GameState of this object
     * @param ending a boolean indicating whether the game is ending
     */
    public void emitGameState(boolean ending) {
        GameUpdate gameUpdate = new GameUpdate(
                this.livingRoom,
                Arrays.stream(this.players).map((p) -> PlayerUpdate.from(p, true)).toList(),
                Arrays.stream(this.commonGoalCards).map(CommonGoalCardUpdate::from).toList(),
                this.gameEnder == null ? null : PlayerUpdate.from(this.gameEnder, true),
                PlayerUpdate.from(this.currentPlayer, true)
        );

        notifyObservers(new GameUpdateToFile(gameUpdate, remainingTiles), ending ? ViewEvent.GAME_END : ViewEvent.GAME_STATE);
    }

    /**
     * @return Whether the game is ended
     */
    public boolean isEnded() {
        return (this.gameEnder != null && this.currentPlayer.equals(this.players[0]));
    }

    /**
     * sets the gameEnder
     * @param gameEnder the player that ends the game
     */
    public void setGameEnder(Player gameEnder) {
        this.gameEnder = gameEnder;

        notifyObservers(new GameUpdateToFile(new GameUpdate(null, null, null, PlayerUpdate.from(this.gameEnder, false), null), null), ViewEvent.ACTION_UPDATE);
    }
}
