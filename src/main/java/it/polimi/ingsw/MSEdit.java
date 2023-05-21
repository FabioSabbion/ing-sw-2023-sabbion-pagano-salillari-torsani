package it.polimi.ingsw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.models.*;

/**
 * This class is meant to create fake JSON games for testing purposes
 */
public class MSEdit {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Bookshelf bookshelf = new Bookshelf();


        for (int i = 0; i < Bookshelf.COLUMNS; i++) {
            bookshelf.getBookshelf()[0][i] = new Tile(Category.values()[i], Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < Bookshelf.COLUMNS; i++) {
            bookshelf.getBookshelf()[1][i] = new Tile(Category.GAMES, Icon.VARIATION1, Orientation.UP);
        }

        for (int i = 0; i < Bookshelf.COLUMNS; i++) {
            bookshelf.getBookshelf()[2][i] = new Tile(Category.values()[i], Icon.VARIATION1, Orientation.UP);
        }

        bookshelf.getBookshelf()[3][0] = new Tile(Category.GAMES, Icon.VARIATION2, Orientation.UP);
        bookshelf.getBookshelf()[4][0] = new Tile(Category.CATS, Icon.VARIATION2, Orientation.UP);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookshelf));
    }

}
