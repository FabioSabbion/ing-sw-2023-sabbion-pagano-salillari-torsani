package it.polimi.ingsw.models;

import it.polimi.ingsw.controller.events.ViewEvent;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.utils.Observable;
import it.polimi.ingsw.utils.Observer;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Contains all game information
 */
public class Game extends Observable<GameUpdate, ViewEvent> {
    private @Nullable Player gameEnder;
    private Player currentPlayer;
    private final Player[] players;
    private final CommonGoalCard[] commonGoalCards;
    private final List<Tile> remainingTiles;
    private final LivingRoom livingRoom;

    public Game(Player[] players, CommonGoalCard[] commonGoalCards, List<Tile> remainingTiles, LivingRoom livingRoom) {
        this.players = players;
        this.commonGoalCards = commonGoalCards;
        this.remainingTiles = remainingTiles;
        this.livingRoom = livingRoom;
        this.currentPlayer = players[0];

        for (var player: this.players) {
            player.addObserver(new Observer<PlayerUpdate, ViewEvent>() {
                @Override
                public void update(PlayerUpdate value, ViewEvent eventType) {
                    notifyObservers(new GameUpdate(null, Arrays.asList(value), null, null, null), eventType);
                }
            });
        }

        this.livingRoom.addObserver(new Observer<LivingRoom, ViewEvent>() {
            @Override
            public void update(LivingRoom value, ViewEvent eventType) {
                notifyObservers(new GameUpdate(value, null, null, null, null), eventType);
            }
        });
    }

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
        return new Game(players.toArray(new Player[0]), CommonGoalCard.createCommonGoalCards(players.size()), tiles, livingRoom);
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
    public void nextPlayer() {
        int index = Arrays.asList(this.players).indexOf(this.currentPlayer);
        this.currentPlayer = this.players[(index + 1) % this.players.length];

        notifyObservers(
                new GameUpdate(null, null, null, null, PlayerUpdate.from(this.currentPlayer, false)),
                this.isEnded() ? ViewEvent.GAME_END : ViewEvent.ACTION_UPDATE
        );
    }

    public void emitGameState(String nickname) {
        GameUpdate gameUpdate = new GameUpdate(
                this.livingRoom,
                Arrays.stream(this.players).map((p) -> PlayerUpdate.from(p, p.getNickname().equals(nickname))).toList(),
                Arrays.asList(this.commonGoalCards),
                PlayerUpdate.from(this.gameEnder, this.gameEnder.getNickname().equals(nickname)),
                PlayerUpdate.from(this.currentPlayer, this.currentPlayer.getNickname().equals(nickname))
        );

        notifyObservers(gameUpdate, ViewEvent.GAME_STATE);
    }

    /**
     * @return Whether the game is ended
     */
    public boolean isEnded() {
        return (this.gameEnder != null && this.currentPlayer.equals(this.players[0]));
    }

    public void setGameEnder(Player gameEnder) {
        this.gameEnder = gameEnder;

        notifyObservers(new GameUpdate(null, null, null, PlayerUpdate.from(this.gameEnder, false), null), ViewEvent.ACTION_UPDATE);
    }
}
