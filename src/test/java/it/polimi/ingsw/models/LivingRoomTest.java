package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LivingRoomTest {
    private LivingRoom livingRoom;
    private List<Tile> remainingTiles;

    @Test
    @DisplayName("")
    public void chooseTilesImpossibleStates() {

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