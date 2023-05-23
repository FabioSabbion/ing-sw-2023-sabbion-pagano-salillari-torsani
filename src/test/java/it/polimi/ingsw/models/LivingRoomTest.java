package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NumPlayersException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.view.CLI.CLIRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LivingRoomTest {
    private LivingRoom livingRoom;
    private List<Tile> remainingTiles;

    @Test
    @DisplayName("Checking if the function rightfully throws in some certain basic cases")
    public void chooseTilesImpossibleStates() {
        livingRoom.fillBoard(3, remainingTiles);

        assertThrows(PickTilesException.class,
                () -> livingRoom.chooseTiles(List.of(new Coordinates(0, 0))),
                "Basic 0,0");
        assertThrows(PickTilesException.class,
                () -> livingRoom.chooseTiles(List.of(new Coordinates(4, 4))),
                "You cant pick a Tile with not an open side");

        System.out.println(CLIRenderer.renderLivingRoom(livingRoom));

        assertDoesNotThrow(() -> livingRoom.removeTiles(List.of(new Coordinates(7, 4))));
        assertThrows(PickTilesException.class, () -> livingRoom.chooseTiles(List.of(new Coordinates(7, 4))));


        assertThrows(PickTilesException.class, () -> livingRoom.chooseTiles(List.of(
                new Coordinates(6, 2),
                new Coordinates(6, 3),
                new Coordinates(8, 5)
        )));

        assertDoesNotThrow(() -> livingRoom.removeTiles(List.of(
                new Coordinates(7, 5),
                new Coordinates(8, 5)
        )));

        assertThrows(PickTilesException.class, () -> livingRoom.chooseTiles(List.of(
                new Coordinates(6, 2),
                new Coordinates(6, 5)
        )));

        assertThrows(PickTilesException.class, () -> livingRoom.removeTiles(List.of(new Coordinates(7, 5))));

        assertDoesNotThrow(() -> livingRoom.removeTiles(List.of(
                new Coordinates(5,0),
                new Coordinates(5, 1),
                new Coordinates(4, 1)
        )));

        System.out.println(CLIRenderer.renderLivingRoom(livingRoom));

        assertThrows(PickTilesException.class, () -> livingRoom.chooseTiles(List.of(
                new Coordinates(2, 2),
                new Coordinates(3, 2),
                new Coordinates(5, 2)
        )));

        assertThrows(PickTilesException.class, () -> livingRoom.chooseTiles(List.of(
                new Coordinates(0,0 ),
                new Coordinates(0, 0),
                new Coordinates(0,0 ),
                new Coordinates(0, 0)
        )));

        assertThrows(NumPlayersException.class, () -> livingRoom.fillBoard(5, remainingTiles));
    }

    @Test
    @DisplayName("Looking for correct cases for chooseTiles method")
    public void chooseTilesPossibleStates() {

        // Filling a board with all Tiles
        assertEquals(true, livingRoom.fillBoardIfNeeded(3, remainingTiles));
        // Seeing if fillBoard has placed in a 4-player position even though numPlayers == 4
        assertEquals(null, livingRoom.getBoard()[0][4]);
        // Checking if board has been correctly refilled
        assertEquals(false, livingRoom.fillBoardIfNeeded(3, remainingTiles));
        //Checking now if players selection do work
        List<Coordinates> playerCoords = new ArrayList<>();
        playerCoords.add(new Coordinates(0, 3));
        playerCoords.add(new Coordinates(1, 3));
        try {
            List<Tile> chosenTiles = livingRoom.chooseTiles(playerCoords);
            assertEquals(livingRoom.getBoard()[0][3], chosenTiles.get(0));
        } catch (PickTilesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Checking if needRefill works correctly")
    public void needRefillPossibleStates() {
        // Checking if an empty board needs to be refilled
        assertTrue(livingRoom.needRefill());
        List<Tile> singleTile = new ArrayList<>();
        singleTile.add(new Tile(Category.BOOKS, Icon.VARIATION1, Orientation.RIGHT));
        assertEquals(true, livingRoom.fillBoardIfNeeded(3, singleTile));
        // Since it is an isolated Tile it must still be true
        assertEquals(true, livingRoom.needRefill());
    }




    @BeforeEach
    public void setup() {
        livingRoom = new LivingRoom();
        remainingTiles = new ArrayList<>();
        for (Category c : Category.values()) {
            for (int i = 0; i < 22; i++) {
                remainingTiles.add(new Tile(c, Icon.VARIATION1, Orientation.UP));
            }
        }
        Collections.shuffle(remainingTiles);
    }
}