package it.polimi.ingsw.controller;

import it.polimi.ingsw.GameUpdateToFile;
import it.polimi.ingsw.events.ViewEvent;
import it.polimi.ingsw.models.*;
import it.polimi.ingsw.utils.Observer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    @Test
    @DisplayName("Checking if creating a new game puts the right players into the right place")
    void NewGameCreated() {
        GameController controller = new GameController(List.of("Andri", "LP"));

        assertEquals(controller.game.getPlayers()[0].getNickname(), "Andri");
        assertEquals(controller.game.getPlayers()[1].getNickname(), "LP");
    }

    @Test
    @DisplayName("Check what happens when the first player is the ender")
    void CheckingWinnerFirst() {
        Game oldGame = Game.createEmptyGame(List.of("Andri", "LP"));

        GameController controller = new GameController(oldGame, 37);

        var oldBookshelf = oldGame.getPlayers()[0].getBookshelf();

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                oldBookshelf.getBookshelf()[i][j] =
                        new Tile(Category.values()[i % Category.values().length], Icon.VARIATION1, Orientation.UP);
            }
        }

        oldBookshelf.getBookshelf()[Bookshelf.ROWS-1][0] = null;

        oldGame.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
                assertEquals(eventType, ViewEvent.ACTION_UPDATE);

                oldGame.deleteObserver(this);
            }
        });

        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,4)), 0, "Andri", List.of());
        });

        assertNotNull(oldBookshelf.getBookshelf()[Bookshelf.ROWS-1][0], "check if actually something was setted");
        assertNotNull(oldGame.getGameEnder(), "Check if the game ender was setted");

        assertEquals(oldGame.getCurrentPlayer().getNickname(), "LP");
        assertFalse(oldGame.isEnded());

        final boolean[] gameEnded = {false};

        oldGame.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
                gameEnded[0] |= eventType == ViewEvent.GAME_END;
            }
        });


        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,5)), 0, "LP", List.of());
        });

        assertTrue(gameEnded[0]);
    }

    @Test
    @DisplayName("Check what happens when the ender is the last player")
    void CheckingWinnerLast() {
        Game oldGame = Game.createEmptyGame(List.of("Andri", "LP"));

        GameController controller = new GameController(oldGame, 37);

        var oldBookshelf = oldGame.getPlayers()[1].getBookshelf();

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                oldBookshelf.getBookshelf()[i][j] =
                        new Tile(Category.values()[i % Category.values().length], Icon.VARIATION1, Orientation.UP);
            }
        }

        oldBookshelf.getBookshelf()[Bookshelf.ROWS-1][0] = null;

        oldGame.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
                assertEquals(eventType, ViewEvent.ACTION_UPDATE);

                oldGame.deleteObserver(this);
            }
        });

        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,4)), 0, "Andri", List.of());
        });


        assertEquals(oldGame.getCurrentPlayer().getNickname(), "LP");

        final boolean[] gameEnded = {false};

        oldGame.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
                gameEnded[0] |= eventType == ViewEvent.GAME_END;
            }
        });


        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,5)), 0, "LP", List.of());
        });

        assertNotNull(oldBookshelf.getBookshelf()[Bookshelf.ROWS-1][0], "check if actually something was setted");
        assertNotNull(oldGame.getGameEnder(), "Check if the game ender was setted");
        assertTrue(oldGame.isEnded());


        assertTrue(gameEnded[0]);
    }

    @Test
    @DisplayName("Check what happens when the ender is the last player remaining connected")
    void CheckingSingleWinner() {
        Game oldGame = Game.createEmptyGame(List.of("Andri", "LP"));

        GameController controller = new GameController(oldGame, 37);



        final boolean[] gameEnded = {false};

        oldGame.addObserver(new Observer<GameUpdateToFile, ViewEvent>() {
            @Override
            public void update(GameUpdateToFile value, ViewEvent eventType) {
                gameEnded[0] |= eventType == ViewEvent.GAME_END;
            }
        });

        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,4)), 0, "Andri", List.of("LP"));
        });

        assertFalse(gameEnded[0]);

        var oldBookshelf = oldGame.getPlayers()[0].getBookshelf();

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                oldBookshelf.getBookshelf()[i][j] =
                        new Tile(Category.values()[i % Category.values().length], Icon.VARIATION1, Orientation.UP);
            }
        }

        oldBookshelf.getBookshelf()[Bookshelf.ROWS-1][0] = null;

        assertDoesNotThrow(() -> {
            controller.update(List.of(new Coordinates(7,5)), 0, "Andri", List.of("LP"));
        });

        assertTrue(gameEnded[0]);
    }
}