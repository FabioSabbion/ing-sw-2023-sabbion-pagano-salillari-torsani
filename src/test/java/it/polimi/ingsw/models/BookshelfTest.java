package it.polimi.ingsw.models;

import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookshelfTest {

    private Bookshelf bookshelf;
    private List<Tile> tiles;
    private Tile tile1, tile2, tile3;
    @BeforeEach
    void setUp(){
        bookshelf = new Bookshelf();
        tile1 = new Tile(Category.CATS, Icon.VARIATION1, Orientation.UP);
        tile2 = new Tile(Category.FRAMES, Icon.VARIATION2, Orientation.LEFT);
        tile3 = new Tile(Category.BOOKS, Icon.VARIATION3, Orientation.DOWN);
    }

    @Test
    void insertTiles(){
        tiles.add(tile1);
        for(int i = 1; i <= 5; i++) {
            try {
                bookshelf.insertTiles(1, tiles);
            } catch (NotEnoughCellsException e) {
            }
            assertEquals(tile1, bookshelf.getBookshelf()[0][i]);
        }
        tiles.add(tile2);
        for(int i = 1; i <= 5; i++) {
            try {
                bookshelf.insertTiles(1, tiles);
            } catch (NotEnoughCellsException e) {
            }
            assertEquals(tile1, bookshelf.getBookshelf()[1][i]);
            assertEquals(tile2, bookshelf.getBookshelf()[2][i]);
        }
        tiles.add(tile3);
        for(int i = 1; i <= 5; i++) {
            try {
                bookshelf.insertTiles(1, tiles);
            } catch (NotEnoughCellsException e) {
            }
            assertEquals(tile1, bookshelf.getBookshelf()[3][i]);
            assertEquals(tile2, bookshelf.getBookshelf()[4][i]);
            assertEquals(tile3, bookshelf.getBookshelf()[5][i]);
        }
        tiles.clear();
        tiles.add(tile1);
        NotEnoughCellsException thrown = assertThrows(NotEnoughCellsException.class, () -> {bookshelf.insertTiles(1, tiles);});
        assertEquals("message", thrown.getMessage());
        bookshelf = new Bookshelf();
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        tiles.add(tile2);
        tiles.add(tile3);
        thrown = assertThrows(NotEnoughCellsException.class, () -> {bookshelf.insertTiles(1, tiles);});
        assertEquals("message", thrown.getMessage());
        tiles.clear();
        tiles.add(tile1);
        bookshelf = new Bookshelf();
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        try {
            bookshelf.insertTiles(1, tiles);
        } catch (NotEnoughCellsException e) {
        }
        tiles.add(tile2);
        thrown = assertThrows(NotEnoughCellsException.class, () -> {bookshelf.insertTiles(1, tiles);});
        assertEquals("message", thrown.getMessage());
    }

    @Test
    void isFull() {
        assertFalse(bookshelf.isFull());
        tiles.clear();
        tiles.add(tile1);
        tiles.add(tile2);
        tiles.add(tile3);
        for(int i = 1; i <= 5; i++) {
            for (int j = 0; j < 2; j++) {
                try {
                    bookshelf.insertTiles(i, tiles);
                } catch (NotEnoughCellsException e) {
                }
                if(i != 5 && j != 0){
                    assertFalse(bookshelf.isFull());
                }
            }
        }
        assertTrue(bookshelf.isFull());
    }

    @Test
    void getBookshelf() {
    }
}