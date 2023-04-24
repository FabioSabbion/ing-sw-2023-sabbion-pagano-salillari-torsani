package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.models.*;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class CLIRenderer {
    private static final Map<Category, Color> categoryColorMap = new HashMap<>();
    static {
        categoryColorMap.put(Category.CATS, Color.GREEN);
        categoryColorMap.put(Category.BOOKS, Color.WHITE);
        categoryColorMap.put(Category.GAMES, Color.YELLOW);
        categoryColorMap.put(Category.FRAMES, Color.BLUE);
        categoryColorMap.put(Category.TROPHIES, Color.CYAN);
        categoryColorMap.put(Category.PLANTS, Color.PURPLE);
    }

    private static final String square = "■";

    /**
     * Returns the color associated to the respective Category
     * @param category
     * @return Color
     */
    public static Color CategoryToColor(Category category){
        return categoryColorMap.get(category);
    }

    /**
     * Returns the render of the personalGoalCard in ASCII
     * @param personalGoalCard
     * @return String
     */
    public static String renderPersonalGoalCard(PersonalGoalCard personalGoalCard){
        Formatter formatter = new Formatter();
        List<String> render = new ArrayList<>();

        List<Pair<Category, Coordinates>> positions = personalGoalCard.getPositions();
        String matrix[][] = new String[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = "  ";
            }
        }

        for (var position : positions) {
            Coordinates coordinate = position.getRight();
            Color color = CategoryToColor(position.getLeft());

            matrix[coordinate.x][coordinate.y] = color.escape() + square + " " + Color.RESET;
        }

        Arrays.stream(matrix).forEach(row -> Arrays.stream(row).forEach(render::add));

        return String.valueOf(formatter.format(ASCIIArt.bookshelf, render.toArray()));
    }

    /**
     * Returns the render of the Bookshelf in ASCII
     * @param bookshelf
     * @return String
     */
    public static String renderBookshelf(Bookshelf bookshelf){
        Formatter formatter = new Formatter();
        List<String> render = new ArrayList<>();

        Tile[][] tiles = bookshelf.getBookshelf();
        String matrix[][] = new String[6][5];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if (tiles[i][j] == null) {
                    matrix[i][j] = "  ";
                }
                else{
                    Color color = CategoryToColor(tiles[i][j].getCategory());
                    matrix[i][j] = color.escape() + square + " " + Color.RESET;
                }
            }
        }

        Arrays.stream(matrix).forEach(row -> Arrays.stream(row).forEach(render::add));

        return String.valueOf(formatter.format(ASCIIArt.bookshelf, render.toArray()));
    }


    public static String renderLivingRoom(LivingRoom livingRoom) {
        Formatter formatter = new Formatter();
        List<String> render = new ArrayList<>();

        Tile[][] tiles = livingRoom.getBoard();
        String matrix[][] = new String[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (tiles[i][j] == null) {
                    matrix[i][j] = "  ";
                }
                else{
                    Color color = CategoryToColor(tiles[i][j].getCategory());
                    matrix[i][j] = color.escape() + square + " " + Color.RESET;
                }
            }
        }
        Arrays.stream(matrix).forEach(row -> Arrays.stream(row).forEach(render::add));

        return String.valueOf(formatter.format(ASCIIArt.livingRoom, render.toArray()));

    }
}

