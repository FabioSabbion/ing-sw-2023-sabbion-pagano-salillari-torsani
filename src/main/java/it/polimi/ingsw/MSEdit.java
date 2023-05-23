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


        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                bookshelf.getBookshelf()[i][j] = new Tile(Category.values()[i % Category.values().length], Icon.VARIATION1, Orientation.UP);
            }
        }

        bookshelf.getBookshelf()[Bookshelf.ROWS-1][0] = null;

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookshelf));
    }

}
