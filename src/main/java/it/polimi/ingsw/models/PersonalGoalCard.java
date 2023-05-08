package it.polimi.ingsw.models;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents the personal goal card of a Player
 */
public class PersonalGoalCard implements GoalCard, Serializable {
    private final int[] points = {0, 1, 2, 4, 6, 9, 12};
    private final List<Pair<Category, Coordinates>> positions;
    private static JSONObject jsonFile;

    public PersonalGoalCard(List<Pair<Category, Coordinates>> positions) {
        this.positions = positions;
    }

    public int checkGoal(Player player) {
        int counterCorrect = 0;

        for (Pair<Category, Coordinates> position :
                this.positions) {
            if (player.getBookshelf().getBookshelf()[position.getRight().y][position.getRight().x].category() == position.getLeft()) {
                counterCorrect++;
            }
        }

        return points[counterCorrect];
    }

    /**
     * Builds a list of PersonalGoalCard from the respective JSON file
     * @return List<PersonalGoalCard>
     */
    public static List<PersonalGoalCard> buildFromJson(){
        if(jsonFile == null) reloadJson();

        List<PersonalGoalCard> personalGoalCards = new ArrayList<>();
        JSONArray personalGoalCardsJson = (JSONArray) jsonFile.get("PersonalGoalCards");

        for (Object pg : personalGoalCardsJson) {
            JSONObject pgJson = (JSONObject) pg;
            String pgId = pgJson.keySet().iterator().next().toString();
            JSONArray tilesJson = (JSONArray) pgJson.get(pgId);
            List<Pair<Category, Coordinates>> positions = new ArrayList<>();

            for (Object tile : tilesJson) {
                JSONObject tileJson = (JSONObject) tile;
                int x = Integer.parseInt(tileJson.get("x").toString());
                int y = Integer.parseInt(tileJson.get("y").toString());

                Category category = Category.valueOf(tileJson.get("tile").toString());
                Coordinates coordinates = new Coordinates(x, y);
                positions.add(new ImmutablePair<>(category, coordinates));
            }

            personalGoalCards.add(new PersonalGoalCard(positions));
        }
        return personalGoalCards;
    }

    /**
     * Reloads fileJson which contains all the PersonalGoalCards
     */
    public static void reloadJson(){
        JSONParser parser = new JSONParser();
        try {
            jsonFile = (JSONObject) parser.parse(new FileReader("src/main/java/it/polimi/ingsw/models/settings/PersonalGoalCards.json"));
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pair<Category, Coordinates>> getPositions() {
        return positions;
    }
}

