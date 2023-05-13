package it.polimi.ingsw.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Random;

public class CommonGoalCardFactory {
    private static final List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
    private List<Coordinates> schema = new ArrayList<>();
    private int maxDistinctCategoryNumber;
    private int exactCategoryNumber;
    private int repetitionNumber;
    private int numPlayers;
    private boolean othersEmpty;

    private static final JSONParser parser = new JSONParser();

    public static String getASCIIForCard(int cardID) {
        if(jsonObjects.size() == 0) {
            reloadJson();
        }
        JSONObject graphic = (JSONObject) jsonObjects.get(cardID).get("graphics");
        return graphic.get("ascii").toString() + "\n" + graphic.get("description").toString();
    }

    public static CommonGoalCard buildFromJson(int numPlayers, int cgcNum) {
        if(jsonObjects.size() == 0) {
            reloadJson();
        }
        Random rand = new Random();

        CommonGoalCardFactory factory = new CommonGoalCardFactory();

        factory.setNumPlayers(numPlayers);
        factory.setMaxDistinctCategoryNumber(((Long)jsonObjects.get(cgcNum).get("maxDistinctCategoryNumber")).intValue());
        factory.setExactCategoryNumber(((Long) jsonObjects.get(cgcNum).get("exactCategoryNumber")).intValue());
        factory.setRepetitionNumber(((Long) jsonObjects.get(cgcNum).get("repetitionNumber")).intValue());
        factory.setOthersEmpty((boolean) jsonObjects.get(cgcNum).get("othersEmpty"));
        JSONArray tempArray = (JSONArray) jsonObjects.get(cgcNum).get("schema");
        for(Object item : tempArray){
            JSONObject obj = (JSONObject) item;
            factory.schema.add(new Coordinates(((Long)obj.get("x")).intValue(), ((Long)obj.get("y")).intValue()));
        }
        try{
            return factory.buildCommonGoalCard(cgcNum);
        } catch(IndexOutOfBoundsException e){
            // TODO to fix outofboundsexception
            e.printStackTrace();
        }
        return CommonGoalCard.createCommonGoalCards(4)[0];
    }

    public CommonGoalCard[] getCommonGoalCard(int numPlayers){
        Random rand = new Random();

        int upperbound = jsonObjects.size();

        int first = rand.nextInt(upperbound);
        int second = rand.nextInt(2) == 0 ? rand.nextInt(first) : first + 1 + rand.nextInt(upperbound - first - 1);

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
    public CommonGoalCard buildCommonGoalCard(int cgcNum) {
        final Predicate<Bookshelf> controlFunction;
        Predicate<Bookshelf> controlFunction1 = new Predicate<Bookshelf>() {
            @Override
            public boolean test(Bookshelf bookshelf) {
                return false;
            }
        };
        if (othersEmpty) {
            controlFunction1 = new Predicate<Bookshelf>() {
                @Override
                public boolean test(Bookshelf bookshelf) {
                    boolean flag = false;
                    for(int i = 0; i < Bookshelf.COLUMNS; i++){
                        for(int j = Bookshelf.ROWS - 1; j >= Bookshelf.ROWS - i - 1; j--){
                            if(bookshelf.getBookshelf()[i][j] != null){
                                flag = true;
                            }
                        }
                        if(!flag){
                            for(int j = Bookshelf.ROWS - i - 2; j >= 0; j--){
                                if(bookshelf.getBookshelf()[i][j] == null){
                                    flag = true;
                                }
                            }
                        }
                    }
                    if(!flag){
                        return true;
                    }
                    else {
                        flag = false;
                        for(int i = Bookshelf.COLUMNS - 1; i >= 0; i--){
                            for(int j = Bookshelf.ROWS - 1; j >= i + 1; j--){
                                if(bookshelf.getBookshelf()[i][j] != null){
                                    flag = true;
                                }
                            }
                            if(!flag){
                                for(int j = i; j >= 0; j--){
                                    if(bookshelf.getBookshelf()[i][j] == null){
                                        flag = true;
                                    }
                                }
                            }
                        }
                        return !flag;
                    }
                }
            };
        } else {
            switch (maxDistinctCategoryNumber) {
                case (5): {
                    switch (exactCategoryNumber) {
                        case (0): {
                            switch (schema.size()) {
                                case (2): {
                                    controlFunction1 = new Predicate<Bookshelf>() {
                                        @Override
                                        public boolean test(Bookshelf bookshelf) {
                                            int[][] flags = new int[Bookshelf.ROWS][Bookshelf.COLUMNS];
                                            int repetition = 0;
                                            for (int i = 0; i < Bookshelf.ROWS - 1; i++) {
                                                for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                                                    if ((bookshelf.getBookshelf()[i][j] != null
                                                    && bookshelf.getBookshelf()[i + 1][j] != null
                                                    && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i + 1][j].category()))
                                                            && (flags[i][j] == 0 && flags[i + 1][j] == 0)) {
                                                        repetition++;
                                                        flags[i][j] = 1;
                                                        flags[i + 1][j] = 1;
                                                        if (repetition == repetitionNumber) {
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                            for (int i = 0; i < Bookshelf.ROWS; i++) {
                                                for (int j = 0; j < Bookshelf.COLUMNS - 1; j++) {
                                                    if ((bookshelf.getBookshelf()[i][j] != null
                                                            && bookshelf.getBookshelf()[i + 1][j] != null
                                                            && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i][j + 1].category()))
                                                            && (flags[i][j] == 0 && flags[i][j + 1] == 0)) {
                                                        repetition++;
                                                        flags[i][j] = 1;
                                                        flags[i + 1][j] = 1;
                                                        if (repetition == repetitionNumber) {
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                            return false;
                                        }
                                    };
                                }
                                case (5): {
                                    controlFunction1 = new Predicate<Bookshelf>() {
                                        @Override
                                        public boolean test(Bookshelf bookshelf) {
                                            for (int i = 0; i < Bookshelf.ROWS - 2; i++) {
                                                for (int j = 0; j < Bookshelf.COLUMNS - 2; j++) {
                                                    if (bookshelf.getBookshelf()[i][j] != null
                                                        && bookshelf.getBookshelf()[i+2][j] != null
                                                        && bookshelf.getBookshelf()[i][j+2] != null
                                                        && bookshelf.getBookshelf()[i+1][j+1] != null
                                                        &&bookshelf.getBookshelf()[i+2][j+2] != null
                                                    && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i + 2][j].category())
                                                            && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i][j + 2].category())
                                                            && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i + 1][j + 1].category())
                                                            && bookshelf.getBookshelf()[i][j].category().
                                                            equals(bookshelf.getBookshelf()[i + 2][j + 2].category())) {
                                                        return true;
                                                    }
                                                }
                                            }
                                            return false;
                                        }
                                    };
                                }
                                case (4): {
                                    if (schema.get(2).y == 4) {
                                        controlFunction1 = new Predicate<Bookshelf>() {
                                            @Override
                                            public boolean test(Bookshelf bookshelf) {
                                                return bookshelf.getBookshelf()[0][0] != null
                                                        && bookshelf.getBookshelf()[0][Bookshelf.COLUMNS - 1] != null
                                                        && bookshelf.getBookshelf()[Bookshelf.ROWS - 1][0] != null
                                                        && bookshelf.getBookshelf()[Bookshelf.ROWS - 1][Bookshelf.COLUMNS - 1] != null &&
                                                        bookshelf.getBookshelf()[0][0].category().
                                                        equals(bookshelf.getBookshelf()[0][Bookshelf.COLUMNS - 1].
                                                                category())
                                                        && bookshelf.getBookshelf()[0][0].category().
                                                        equals(bookshelf.getBookshelf()[Bookshelf.ROWS - 1][0].
                                                                category())
                                                        && bookshelf.getBookshelf()[0][0].category().
                                                        equals(bookshelf.
                                                                getBookshelf()[Bookshelf.ROWS - 1][Bookshelf.COLUMNS - 1].
                                                                category());
                                            }
                                        };
                                    } else {
                                        controlFunction1 = new Predicate<Bookshelf>() {
                                            @Override
                                            public boolean test(Bookshelf bookshelf) {
                                                int[][] flags = new int[Bookshelf.ROWS][Bookshelf.COLUMNS];
                                                int repetition = 0;
                                                for (int i = 0; i < Bookshelf.ROWS - 1; i++) {
                                                    for (int j = 0; j < Bookshelf.COLUMNS - 1; j++) {
                                                        if ((bookshelf.getBookshelf()[i][j] != null
                                                                && bookshelf.getBookshelf()[i+1][j] != null
                                                                && bookshelf.getBookshelf()[i][j+1] != null
                                                                && bookshelf.getBookshelf()[i+1][j+1] != null
                                                                && bookshelf.getBookshelf()[i][j].category().
                                                                equals(bookshelf.getBookshelf()[i + 1][j].category()))
                                                                && (bookshelf.getBookshelf()[i][j].category().
                                                                equals(bookshelf.getBookshelf()[i + 1][j + 1].category()))
                                                                && (bookshelf.getBookshelf()[i][j].category().
                                                                equals(bookshelf.getBookshelf()[i][j + 1].category()))
                                                                && (flags[i][j] == 0 && flags[i + 1][j] == 0 &&
                                                                flags[i + 1][j + 1] == 0 && flags[i][j + 1] == 0)) {
                                                            repetition++;
                                                            flags[i][j] = 1;
                                                            flags[i + 1][j] = 1;
                                                            flags[i + 1][j + 1] = 1;
                                                            flags[i][j + 1] = 1;
                                                            if (repetition == repetitionNumber) {
                                                                return true;
                                                            }
                                                        }
                                                    }
                                                }
                                                return false;
                                            }
                                        };
                                    }
                                }
                            }
                        }
                        case (5): {
                            controlFunction1 = new Predicate<Bookshelf>() {
                                @Override
                                public boolean test(Bookshelf bookshelf) {
                                    int flag = 0;
                                    int repetition = 0;
                                    for (int i = 0; i < Bookshelf.ROWS; i++) {
                                        flag = 0;
                                        for (int j = 0; j < Bookshelf.COLUMNS - 1; j++) {
                                            for (int k = j; k < Bookshelf.COLUMNS; k++) {
                                                if (bookshelf.getBookshelf()[i][j] != null
                                                    && bookshelf.getBookshelf()[i][k] != null
                                                    && bookshelf.getBookshelf()[i][j].
                                                        equals(bookshelf.getBookshelf()[i][k])) {
                                                    flag = 1;
                                                    k = Bookshelf.COLUMNS;
                                                    j = Bookshelf.COLUMNS - 1;
                                                }
                                            }
                                        }
                                        if (flag == 0) {
                                            repetition++;
                                            if (repetition == repetitionNumber) {
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                }
                            };
                        }
                    }
                }
                case (3): {
                    switch (schema.size()) {
                        case (4): {
                            controlFunction1 = new Predicate<Bookshelf>() {
                                @Override
                                public boolean test(Bookshelf bookshelf) {
                                    int[][] flags = new int[Bookshelf.ROWS][Bookshelf.COLUMNS];
                                    int repetition = 0;
                                    for (int i = 0; i < Bookshelf.ROWS - 3; i++) {
                                        for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                                            if ((bookshelf.getBookshelf()[i][j] != null
                                                    && bookshelf.getBookshelf()[i+1][j] != null
                                                    && bookshelf.getBookshelf()[i+2][j] != null
                                                    && bookshelf.getBookshelf()[i+3][j] != null
                                                    && bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i + 1][j].category()))
                                                    && (bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i + 2][j].category()))
                                                    && (bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i + 3][j].category()))
                                                    && (flags[i][j] == 0 && flags[i + 1][j] == 0
                                                    && flags[i + 2][j] == 0 && flags[i + 3][j] == 0)) {
                                                repetition++;
                                                flags[i][j] = 1;
                                                flags[i + 1][j] = 1;
                                                flags[i + 2][j] = 1;
                                                flags[1 + 3][j] = 1;
                                                if (repetition == repetitionNumber) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                    for (int i = 0; i < Bookshelf.ROWS; i++) {
                                        for (int j = 0; j < Bookshelf.COLUMNS - 3; j++) {
                                            if ((bookshelf.getBookshelf()[i][j] != null
                                                    && bookshelf.getBookshelf()[i][j+1] != null
                                                    && bookshelf.getBookshelf()[i][j+2] != null
                                                    && bookshelf.getBookshelf()[i][j+3] != null
                                                    && bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i][j + 1].category()))
                                                    && (bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i][j + 2].category()))
                                                    && (bookshelf.getBookshelf()[i][j].category().
                                                    equals(bookshelf.getBookshelf()[i][j + 3].category()))
                                                    && (flags[i][j] == 0 && flags[i][j + 1] == 0
                                                    && flags[i][j + 2] == 0 && flags[i][j + 3] == 0)) {
                                                repetition++;
                                                flags[i][j] = 1;
                                                flags[i][j + 1] = 1;
                                                flags[i][j + 2] = 1;
                                                flags[i][j + 3] = 1;
                                                if (repetition == repetitionNumber) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                    return false;
                                }
                            };
                        }
                        case (6): {
                            controlFunction1 = new Predicate<Bookshelf>() {
                                @Override
                                public boolean test(Bookshelf bookshelf) {
                                    int[] countCategories = new int[6];
                                    int numCategories = 0;
                                    int repetition = 0;
                                    for (int i = 0; i < Bookshelf.COLUMNS; i++) {
                                        numCategories = 0;
                                        for (int j = 0; j < Bookshelf.ROWS; j++) {
                                            if(bookshelf.getBookshelf()[j][i] != null){
                                                switch (bookshelf.getBookshelf()[j][i].category()) {
                                                    case CATS -> countCategories[0]++;
                                                    case BOOKS -> countCategories[1]++;
                                                    case FRAMES -> countCategories[2]++;
                                                    case GAMES -> countCategories[3]++;
                                                    case PLANTS -> countCategories[4]++;
                                                    case TROPHIES -> countCategories[5]++;
                                                }
                                            }
                                        }
                                        for (int k = 0; k < 6; k++) {
                                            if (countCategories[k] != 0) {
                                                numCategories++;
                                            }
                                        }
                                        if (numCategories < maxDistinctCategoryNumber) {
                                            repetition++;
                                        }
                                    }
                                    return repetition >= repetitionNumber;
                                }
                            };
                        }
                        case (5): {
                            controlFunction1 = new Predicate<Bookshelf>() {
                                @Override
                                public boolean test(Bookshelf bookshelf) {
                                    int[] countCategories = new int[6];
                                    int numCategories = 0;
                                    int repetition = 0;
                                    for (int j = 0; j < Bookshelf.ROWS; j++) {
                                        numCategories = 0;
                                        for (int i = 0; i < Bookshelf.COLUMNS; i++) {
                                            if(bookshelf.getBookshelf()[j][i] != null){
                                                switch (bookshelf.getBookshelf()[j][i].category()) {
                                                    case CATS -> countCategories[0]++;
                                                    case BOOKS -> countCategories[1]++;
                                                    case FRAMES -> countCategories[2]++;
                                                    case GAMES -> countCategories[3]++;
                                                    case PLANTS -> countCategories[4]++;
                                                    case TROPHIES -> countCategories[5]++;
                                                }
                                            }
                                        }
                                        for (int k = 0; k < 6; k++) {
                                            if (countCategories[k] != 0) {
                                                numCategories++;
                                            }
                                        }
                                        if (numCategories < maxDistinctCategoryNumber) {
                                            repetition++;
                                        }
                                    }
                                    return repetition >= repetitionNumber;
                                }
                            };
                        }
                    }
                }
                case (6): {
                    switch (exactCategoryNumber) {
                        case (0): {
                            switch (schema.size()) {
                                case (1): {
                                    controlFunction1 = new Predicate<Bookshelf>() {
                                        @Override
                                        public boolean test(Bookshelf bookshelf) {
                                            int[] countCategories = new int[6];
                                            for (int i = 0; i < Bookshelf.ROWS; i++) {
                                                for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                                                    if(bookshelf.getBookshelf()[j][i] != null){
                                                        switch (bookshelf.getBookshelf()[j][i].category()) {
                                                            case CATS -> countCategories[0]++;
                                                            case BOOKS -> countCategories[1]++;
                                                            case FRAMES -> countCategories[2]++;
                                                            case GAMES -> countCategories[3]++;
                                                            case PLANTS -> countCategories[4]++;
                                                            case TROPHIES -> countCategories[5]++;
                                                        }
                                                    }
                                                }
                                            }
                                            for (int i = 0; i < 6; i++) {
                                                if (countCategories[i] >= repetitionNumber) {
                                                    return true;
                                                }
                                            }
                                            return false;
                                        }
                                    };
                                }
                                case (5): {
                                    controlFunction1 = new Predicate<Bookshelf>() {
                                        @Override
                                        public boolean test(Bookshelf bookshelf) {
                                            return bookshelf.getBookshelf()[0][0] != null
                                                    && bookshelf.getBookshelf()[1][1] != null
                                                    && bookshelf.getBookshelf()[2][2] != null
                                                    && bookshelf.getBookshelf()[3][3] != null
                                                    && bookshelf.getBookshelf()[4][4] != null
                                                    && bookshelf.getBookshelf()[0][0].category().
                                                    equals(bookshelf.getBookshelf()[1][1].category())
                                                    && bookshelf.getBookshelf()[0][0].category().
                                                    equals(bookshelf.getBookshelf()[2][2].category())
                                                    && bookshelf.getBookshelf()[0][0].category().
                                                    equals(bookshelf.getBookshelf()[3][3].category())
                                                    && bookshelf.getBookshelf()[0][0].category().
                                                    equals(bookshelf.getBookshelf()[4][4].category());
                                        }
                                    };
                                }
                            }
                        }
                        case (6): {
                            controlFunction1 = new Predicate<Bookshelf>() {
                                @Override
                                public boolean test(Bookshelf bookshelf) {
                                    int flag = 0;
                                    int repetition = 0;
                                    for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                                        flag = 0;
                                        for (int i = 0; i < Bookshelf.ROWS - 1; i++) {
                                            for (int k = i; k < Bookshelf.ROWS; k++) {
                                                if (bookshelf.getBookshelf()[i][j] != null
                                                    && bookshelf.getBookshelf()[k][j] != null
                                                        && bookshelf.getBookshelf()[i][j].
                                                        equals(bookshelf.getBookshelf()[k][j])) {
                                                    flag = 1;
                                                    k = Bookshelf.COLUMNS;
                                                    j = Bookshelf.COLUMNS - 1;
                                                }
                                            }
                                        }
                                        if (flag == 0) {
                                            repetition++;
                                            if (repetition == repetitionNumber) {
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                }
                            };
                        }
                    }
                }
            }
        }
        controlFunction = controlFunction1;
        return new CommonGoalCard(controlFunction, numPlayers, cgcNum);
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

    public void setOthersEmpty(boolean othersEmpty){
        this.othersEmpty = othersEmpty;
    }
}
