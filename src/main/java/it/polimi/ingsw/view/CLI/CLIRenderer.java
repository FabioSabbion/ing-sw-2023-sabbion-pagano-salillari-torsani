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
     *
     * @param category
     * @return Color
     */
    public static Color CategoryToColor(Category category) {
        return categoryColorMap.get(category);
    }

    /**
     * Returns the render of the personalGoalCard in ASCII
     *
     * @param personalGoalCard
     * @return String
     */
    public static String renderPersonalGoalCard(PersonalGoalCard personalGoalCard) {
        Formatter formatter = new Formatter();
        List<String> render = new ArrayList<>();

        List<Pair<Category, Coordinates>> positions = personalGoalCard.getPositions();
        String matrix[][] = new String[Bookshelf.ROWS][Bookshelf.COLUMNS];
        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                matrix[i][j] = "  ";
            }
        }

        for (var position : positions) {
            Coordinates coordinate = position.getRight();
            Color color = CategoryToColor(position.getLeft());

            matrix[coordinate.x][coordinate.y] = color.escape() + square + " " + Color.RESET;
        }

        for (int i = Bookshelf.ROWS-1; i >= 0; i--) {

            render.addAll(Arrays.asList(matrix[i]));
        }

        return String.valueOf(formatter.format(ASCIIArt.personalGoalCard, render.toArray()));
    }

    /**
     * Returns the render of the Bookshelf in ASCII
     *
     * @param bookshelf
     * @return String
     */
    public static String renderBookshelf(Bookshelf bookshelf) {
        Formatter formatter = new Formatter();
        List<String> render = new ArrayList<>();

        Tile[][] tiles = bookshelf.getBookshelf();
        String matrix[][] = new String[Bookshelf.ROWS][Bookshelf.COLUMNS];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if (tiles[i][j] == null) {
                    matrix[i][j] = "  ";
                } else {
                    Color color = CategoryToColor(tiles[i][j].getCategory());
                    matrix[i][j] = color.escape() + square + " " + Color.RESET;
                }
            }
        }

        for (int i = Bookshelf.ROWS-1; i >= 0; i--) {

            render.addAll(Arrays.asList(matrix[i]));
        }

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
                } else {
                    Color color = CategoryToColor(tiles[i][j].getCategory());
                    matrix[i][j] = color.escape() + square + " " + Color.RESET;
                }
            }
        }
        Arrays.stream(matrix).forEach(row -> Arrays.stream(row).forEach(render::add));

        return String.valueOf(formatter.format(ASCIIArt.livingRoom, render.toArray()));

    }

    public static String renderCommonGoalCard(int value){
        return ASCIIArt.CommonGoalCards[value];
    }


    public static String concatAsciiArt(String multilineString1, String multilineString2) {
        String[] lines1 = multilineString1.split("\\r?\\n"); // split by newline character
        String[] lines2 = multilineString2.split("\\r?\\n"); // split by newline character
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines1.length || i < lines2.length; i++) {
            String line1 = i < lines1.length ? lines1[i] : "";
            String line2 = i < lines2.length ? lines2[i] : "";
            sb.append(line1).append("\t\t").append(line2).append("\n"); // use tab character as separator and newline as line break
        }
        return sb.toString();
    }

}


