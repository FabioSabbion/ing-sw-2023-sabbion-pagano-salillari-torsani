package it.polimi.ingsw.models;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class CommonGoalCardFactory {
    private static final List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
    private List<Coordinates> schema = new ArrayList<>();
    private int maxDistinctCategoryNumber;
    private int exactCategoryNumber;
    private int repetitionNumber;
    private int numPlayers;
    private boolean othersEmpty;
    private boolean unitedCategories;
    private boolean rotatable;

    private static final JSONParser parser = new JSONParser();

    public static String getASCIIForCard(int cardID) {
        if(jsonObjects.size() == 0) {
            reloadJson();
        }
        JSONObject graphic = (JSONObject) jsonObjects.get(cardID).get("graphics");
        return graphic.get("ascii").toString() + "\n" + graphic.get("description").toString();
    }

    public static CommonGoalCard buildFromJson(int numPlayers, int cardID) {
        if(jsonObjects.size() == 0) {
            reloadJson();
        }
        CommonGoalCardFactory factory = new CommonGoalCardFactory();

        factory.setNumPlayers(numPlayers);
        factory.setMaxDistinctCategoryNumber(((Long)jsonObjects.get(cardID).get("maxDistinctCategoryNumber")).intValue());
        factory.setExactCategoryNumber(((Long) jsonObjects.get(cardID).get("exactCategoryNumber")).intValue());
        factory.setRepetitionNumber(((Long) jsonObjects.get(cardID).get("repetitionNumber")).intValue());
        factory.setOthersEmpty((boolean) jsonObjects.get(cardID).get("othersEmpty"));



        factory.setUnitedCategories(false);

        if (jsonObjects.get(cardID).containsKey("unitedCategories")) {
            factory.setUnitedCategories((boolean) jsonObjects.get(cardID).get("unitedCategories"));
        }

        factory.setRotatable(false);

        if (jsonObjects.get(cardID).containsKey("rotatable")) {
            factory.setRotatable((boolean) jsonObjects.get(cardID).get("rotatable"));
        }

        JSONArray tempArray = (JSONArray) jsonObjects.get(cardID).get("schema");
        for(Object item : tempArray){
            JSONObject obj = (JSONObject) item;
            factory.schema.add(new Coordinates(((Long)obj.get("x")).intValue(), ((Long)obj.get("y")).intValue()));
        }

        return factory.buildCommonGoalCard(cardID);
    }

    public static CommonGoalCard[] getCommonGoalCard(int numPlayers){
        Random rand = new Random();
        if(jsonObjects.size() == 0) {
            reloadJson();
        }

        int upperbound = jsonObjects.size();

        List<Integer> toUpperbound = new ArrayList<>();

        for (int i = 0; i < upperbound; i++) {
            toUpperbound.add(i);
        }

        Collections.shuffle(toUpperbound);

        int first = toUpperbound.get(0);
        int second = toUpperbound.get(1);

        return new CommonGoalCard[]{buildFromJson(numPlayers, first), buildFromJson(numPlayers, second)};
    }

    public static void reloadJson(){
        try{
            Object obj = parser.parse(new FileReader("src/main/java/it/polimi/ingsw/models/settings/cgc.json"));
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                jsonObjects.add((JSONObject) o);
            }
        }catch(IOException | ParseException e){
            e.printStackTrace();
        }
    }
    public CommonGoalCard buildCommonGoalCard(int cardID) {
        final Predicate<Bookshelf> controlFunction = new Predicate<Bookshelf>() {
            Set<Category> sameCategories = null;
            private record EfficiencyIndex(int i, int j, Set<Coordinates> blocked, boolean inverted) {
                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    EfficiencyIndex that = (EfficiencyIndex) o;
                    return i == that.i && j == that.j && inverted == that.inverted && Objects.equals(blocked, that.blocked);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(i, j, blocked, inverted);
                }
            };

            Map<EfficiencyIndex, Integer> previousResult;

            public int checkFrom(int i, int j, Set<Coordinates> blocked, Tile[][] bookshelfMat) {
                return checkFrom(i, j, blocked, bookshelfMat, false);
            }

            public int checkFrom(int i, int j, Set<Coordinates> blocked, Tile[][] bookshelfMat, boolean inverted) {
                Set<Category> categories = new HashSet<>();

                if(previousResult.containsKey(new EfficiencyIndex(i, j, blocked, inverted))) {
                    return previousResult.get(new EfficiencyIndex(i, j, blocked, inverted));
                }

                var localSchema = schema;
                int invertedResult = 0;

                if (rotatable && !inverted) {
                    invertedResult = checkFrom(i, j, blocked, bookshelfMat, true);

                    localSchema = localSchema.stream().map(coordinates -> new Coordinates(coordinates.y, coordinates.x)).toList();

                    if (invertedResult >= repetitionNumber) {
                        previousResult.put(new EfficiencyIndex(i, j, blocked, true), invertedResult);
                        return invertedResult;
                    }
                }

                for(Coordinates c: localSchema) {
                    if (i + c.x < bookshelfMat.length
                            && j + c.y < bookshelfMat[i + c.x].length
                            && bookshelfMat[i + c.x][j + c.y] != null
                            && !blocked.contains(new Coordinates(i + c.x, j + c.y))
                            && (sameCategories == null || sameCategories.contains(bookshelfMat[i + c.x][j + c.y].category()))) {
                        categories.add(bookshelfMat[i + c.x][j + c.y].category());
                    } else {
                        categories = null;

                        break;
                    }
                }

                if(othersEmpty && categories != null) {
                    boolean allMatch = true;

                    for (int i_else = 0; i_else < bookshelfMat.length && allMatch; i_else++) {
                        for (int j_else = 0; j_else < bookshelfMat[i_else].length && allMatch; j_else++) {
                            boolean containedSchema = false;

                            for (Coordinates c: localSchema) {
                                if ((i + c.x == i_else && j + c.y == j_else)) {
                                    containedSchema = true;
                                    break;
                                }
                            }

                            if (!containedSchema && bookshelfMat[i_else][j_else] != null) {
                                allMatch = false;
                                break;
                            }
                        }
                    }

                    if(!allMatch) {
                        categories = null;
                    }
                }

                int i_next = i;
                int j_next = 0;

                if (j + 1 < bookshelfMat[i].length) {
                    j_next = j + 1;
                } else {
                    if (i + 1 < bookshelfMat.length) {
                        i_next = i + 1;
                    } else {
                        previousResult.put(new EfficiencyIndex(i_next, j_next, blocked, inverted), 0);
                        return 0;
                    }
                }

                if (categories != null && categories.size() > maxDistinctCategoryNumber) {
                    previousResult.put(new EfficiencyIndex(i_next, j_next, blocked, inverted), 0);
                    return 0;
                }


                if (categories != null && (exactCategoryNumber == 0 || exactCategoryNumber == categories.size())) {
                    var newBlocked = new HashSet<>(blocked);

                    for (Coordinates c: localSchema) {
                        newBlocked.add(new Coordinates(i + c.x, j + c.y));
                    }

                    var tempResGood = checkFrom(i_next, j_next, newBlocked, bookshelfMat) + 1;

                    previousResult.put(new EfficiencyIndex(i_next, j_next, newBlocked, false), tempResGood);

                    if (tempResGood >= repetitionNumber) {
                        return tempResGood;
                    }

                    var tempResBad = checkFrom(i_next, j_next, blocked, bookshelfMat);

                    var tempResFinal = Math.max(Math.max(tempResGood, tempResBad), invertedResult);
                    previousResult.put(new EfficiencyIndex(i_next, j_next, blocked, false), tempResFinal);

                    return tempResFinal;
                }

                var resFinal = Math.max(checkFrom(i_next, j_next, blocked, bookshelfMat), invertedResult);

                previousResult.put(new EfficiencyIndex(i_next, j_next, blocked, invertedResult == resFinal), resFinal);
                return resFinal;
            }

            @Override
            public boolean test(Bookshelf bookshelf) {
                previousResult = new HashMap<>();

                var bookshelfMat = bookshelf.getBookshelf();

                int matching = 0;

                if (unitedCategories) {
                    Set<Set<Category>> combinations = Sets.combinations(ImmutableSet.copyOf(Category.values()), maxDistinctCategoryNumber);

                    for (var combination: combinations) {
                        sameCategories = combination;

                        for (int i = 0; i < bookshelfMat.length; i++) {
                            for (int j = 0; j < bookshelfMat[i].length; j++) {
                                matching = Math.max(matching, checkFrom(i, j, new HashSet<>(), bookshelfMat));

                                if (matching >= repetitionNumber) {
                                    System.err.println("MATCHING1: " + matching);
                                    return true;
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < bookshelfMat.length; i++) {
                    for (int j = 0; j < bookshelfMat[i].length; j++) {
                        matching = Math.max(matching, checkFrom(i, j, new HashSet<>(), bookshelfMat));

                        if (matching >= repetitionNumber) {
                            System.out.println("MATCHING2: " + matching);

                            System.out.println("MAPPA FINALE: " + previousResult);
                            return true;
                        }
                    }
                }

                System.err.println("MATCHING: " + matching);

                return matching >= repetitionNumber;
            }
        };

        return new CommonGoalCard(controlFunction, numPlayers, cardID);
    }

    public void setSchema(List<Coordinates> schema){
        this.schema = schema;
    }

    public void setMaxDistinctCategoryNumber(int maxDistinctCategoryNumber){
        this.maxDistinctCategoryNumber = maxDistinctCategoryNumber;
    }

    public void setExactCategoryNumber(int exactCategoryNumber){
        this.exactCategoryNumber = exactCategoryNumber;
    }

    public void setRepetitionNumber(int repetitionNumber){
        this.repetitionNumber = repetitionNumber;
    }

    public void setNumPlayers(int numPlayers){
        this.numPlayers = numPlayers;
    }

    public void setUnitedCategories(boolean unitedCategories) {
        this.unitedCategories = unitedCategories;
    }

    public void setOthersEmpty(boolean othersEmpty){
        this.othersEmpty = othersEmpty;
    }
    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }
}
