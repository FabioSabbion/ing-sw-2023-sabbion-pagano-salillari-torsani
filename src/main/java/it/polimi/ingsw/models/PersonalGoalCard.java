package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents the personal goal card of a Player
 */
public class PersonalGoalCard implements GoalCard, Serializable {
    private final int[] points = {0, 1, 2, 4, 6, 9, 12};
    private final List<MutablePair<Category, Coordinates>> positions;
    private static JSONObject jsonFile;
    public final String cardID;

    public PersonalGoalCard(List<MutablePair<Category, Coordinates>> positions, String cardID) {
        this.positions = positions;
        this.cardID = cardID;
    }

    /**
     * checks whether the player has obtained points in the {@link PersonalGoalCard}
     * @param player The player to check
     * @return the points obtained
     */
    public int checkGoal(Player player) {
        int counterCorrect = 0;

        for (Pair<Category, Coordinates> position :
                this.positions) {
            Tile checkTile = player.getBookshelf().getBookshelf()[position.getRight().x][position.getRight().y];
            if (checkTile != null && checkTile.category() == position.getLeft()) {
                counterCorrect++;
            }
        }

        return points[counterCorrect];
    }

    /**
     * Builds a list of PersonalGoalCard from the respective JSON file
     * @return A list of PersonalGoalCard
     */
    public static List<PersonalGoalCard> buildFromJson(){
        if(jsonFile == null) reloadJson();

        List<PersonalGoalCard> personalGoalCards = new ArrayList<>();
        JSONArray personalGoalCardsJson = (JSONArray) jsonFile.get("PersonalGoalCards");

        for (Object pg : personalGoalCardsJson) {
            JSONObject pgJson = (JSONObject) pg;
            String pgId = pgJson.keySet().iterator().next().toString();
            JSONArray tilesJson = (JSONArray) pgJson.get(pgId);
            List<MutablePair<Category, Coordinates>> positions = new ArrayList<>();

            for (Object tile : tilesJson) {
                JSONObject tileJson = (JSONObject) tile;
                int x = Integer.parseInt(tileJson.get("x").toString());
                int y = Integer.parseInt(tileJson.get("y").toString());

                Category category = Category.valueOf(tileJson.get("tile").toString());
                Coordinates coordinates = new Coordinates(x, y);
                positions.add(new MutablePair<>(category, coordinates));
            }

            personalGoalCards.add(new PersonalGoalCard(positions, pgId));
        }
        return personalGoalCards;
    }

    /**
     * Reloads fileJson which contains all the PersonalGoalCards
     */
    public static void reloadJson(){
        JSONParser parser = new JSONParser();
        try {
            InputStream path = PersonalGoalCard.class.getResourceAsStream("/settings/PersonalGoalCards.json");
            jsonFile = (JSONObject) parser.parse(new InputStreamReader(path));
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MutablePair<Category, Coordinates>> getPositions() {
        return positions;
    }
}

