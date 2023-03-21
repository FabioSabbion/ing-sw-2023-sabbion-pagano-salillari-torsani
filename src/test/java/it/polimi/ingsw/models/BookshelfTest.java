package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookshelfTest {

    private Bookshelf bookshelf;
    private List<Tile> tiles;
    private Tile tile1, tile2, tile3;
    @BeforeEach
    void setUp(){
        bookshelf = new Bookshelf();

    }

    @Test
    void insertTiles() throws PickTilesException, NotEnoughCellsException {
        tile1 = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        tile2 = new Tile(Category.FRAMES, Icon.VARIATION2, Orientation.LEFT);
        tile3 = new Tile(Category.BOOKS, Icon.VARIATION3, Orientation.DOWN);
        tiles = new ArrayList<>();
        tiles.add(tile1);
        bookshelf.insertTiles(0,tiles);
        assertEquals(tile1, bookshelf.getBookshelf()[0][0]);
        tiles.add(tile2);
        tiles.add(tile3);
        bookshelf.insertTiles(2, tiles);
        assertEquals(tile1, bookshelf.getBookshelf()[0][2]);
        assertEquals(tile2, bookshelf.getBookshelf()[1][2]);
        assertEquals(tile3, bookshelf.getBookshelf()[2][2]);
        bookshelf.insertTiles(0, tiles);
        assertEquals(tile1, bookshelf.getBookshelf()[1][0]);
        assertEquals(tile2, bookshelf.getBookshelf()[2][0]);
        assertEquals(tile3, bookshelf.getBookshelf()[3][0]);
        assertThrows(NotEnoughCellsException.class, () -> bookshelf.insertTiles(0, tiles));
        Throwable exception = assertThrows(PickTilesException.class, () -> bookshelf.insertTiles(5, tiles));
        assertEquals("Invalid column", exception.getMessage());
        tiles.add(tile1);
        exception = assertThrows(PickTilesException.class, () -> bookshelf.insertTiles(1, tiles));
        assertEquals("You can't insert 4 tiles", exception.getMessage());
        tiles.clear();
        exception = assertThrows(PickTilesException.class, () -> bookshelf.insertTiles(1, tiles));
        assertEquals("You can't insert 0 tiles", exception.getMessage());
    }

    @Test
    void isFull() throws PickTilesException, NotEnoughCellsException {
        tile1 = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        tile2 = new Tile(Category.FRAMES, Icon.VARIATION2, Orientation.LEFT);
        tile3 = new Tile(Category.BOOKS, Icon.VARIATION3, Orientation.DOWN);
        tiles = new ArrayList<>();
        assertFalse(bookshelf.isFull());
        tiles.clear();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        for(int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; j++) {
                bookshelf.insertTiles(i, tiles);
                if(i != 4 || j != 1){
                    assertFalse(bookshelf.isFull());
                }
            }
        }
        assertTrue(bookshelf.isFull());
    }

    @Test
    void getBookshelf() {
    }
    @Test
    void getCloseTilesTest () throws PickTilesException, NotEnoughCellsException{
        tiles = new ArrayList<>();
        tiles.add(new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP));
        tiles.add(new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP));
        tiles.add(new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP));
        bookshelf.insertTiles(0, tiles);
        tiles.clear();
        tiles.add(new Tile(Category.FRAMES, Icon.VARIATION1, Orientation.UP));
        tiles.add(new Tile(Category.FRAMES, Icon.VARIATION1, Orientation.UP));
        bookshelf.insertTiles(1, tiles);
        Map<Category, List<Integer>> expectedResult = new HashMap<>();
        expectedResult.put(Category.CATS, List.of(3));
        expectedResult.put(Category.FRAMES, List.of(2));
        assertEquals(bookshelf.getCloseTiles(), expectedResult);
    }
}