package it.polimi.ingsw;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.utils.Util;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GamePersistence {
    private static final class PairSerializer extends JsonSerializer<Pair> {

        @Override
        public void serialize(Pair value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("left", value.getLeft());
            gen.writeObjectField("right", value.getRight());
            gen.writeEndObject();
        }

    }
    static public final String path = "oldGames";
    static public final String startingString = "game";
    static public final int savingNumber = 3;
    final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
    final ObjectMapper mapper = new ObjectMapper();

    Executor executor = Executors.newSingleThreadExecutor();

    private final Map<Integer, GameUpdateToFile> updateMap = new TreeMap<>();

    public GamePersistence() {
        mapper.registerModule(new SimpleModule().addSerializer(Pair.class, new PairSerializer()));
    }

    public void removeGames(int id) {
        Path path = Paths.get(GamePersistence.path);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File gamesDir = path.toFile();

        Arrays.stream(Objects.requireNonNull(gamesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                var splitName = name.split("-");

                if (splitName[0].equals(startingString)) {
                    try {
                        var ignored = LocalDateTime.parse(splitName[2], timeFormat);

                        if (Integer.parseInt(splitName[1]) == id) {
                            return true;
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        return false;
                    }
                }


                return false;
            }
        }))).forEach(game -> {var ignored = game.delete();});
    }

    public void saveGames(GameUpdateToFile gameUpdateToFile, int ID) {
        if (updateMap.containsKey(ID)) {
            var oldUpdate = updateMap.get(ID);

            var gameOldUpdate = oldUpdate.update();
            var gameNewUpdate = gameUpdateToFile.update();

            Map<String, PlayerUpdate> players = null;

            if (gameNewUpdate.players() != null) {
                players = new HashMap<>();
                if (gameOldUpdate.players() == null) {
                    for (PlayerUpdate playerUpdate : gameNewUpdate.players()) {
                        players.put(playerUpdate.nickname(), playerUpdate);
                    }
                } else {

                    for (PlayerUpdate playerUpdate : gameOldUpdate.players()) {
                        players.put(playerUpdate.nickname(), playerUpdate);
                    }

                    for (PlayerUpdate playerUpdate : gameNewUpdate.players()) {
                        var prev = players.get(playerUpdate.nickname());

                        players.put(playerUpdate.nickname(),
                                new PlayerUpdate(prev.nickname(),
                                        Optional.ofNullable(playerUpdate.bookshelf()).orElse(prev.bookshelf()),
                                        Optional.ofNullable(playerUpdate.personalGoalCard()).orElse(prev.personalGoalCard())));
                    }
                }
            }

            List<PlayerUpdate> newPlayers = gameOldUpdate.players();

            if (players != null) {
                newPlayers = players.values().stream().toList();
            }

            List<CommonGoalCardUpdate> commonGoalCardUpdates = new ArrayList<>(gameOldUpdate.commonGoalCards());

            if (gameNewUpdate.commonGoalCards() != null) {
                for(var card: gameNewUpdate.commonGoalCards()) {
                    for (int i = 0; i < commonGoalCardUpdates.size(); i++) {
                        if(commonGoalCardUpdates.get(i).commonGoalCardID() == card.commonGoalCardID()) {
                            commonGoalCardUpdates.set(i, card);
                        }
                    }
                }
            }


            updateMap.put(ID, new GameUpdateToFile(
                    new GameUpdate(
                            Util.nullOrElse(gameNewUpdate.livingRoom(), gameOldUpdate.livingRoom()),
                            newPlayers,
                            commonGoalCardUpdates,
                            Util.nullOrElse(gameNewUpdate.gameEnder(), gameOldUpdate.gameEnder()),
                            Util.nullOrElse(gameNewUpdate.currentPlayer(), gameOldUpdate.currentPlayer())
                            ),
                    Util.nullOrElse(gameUpdateToFile.remainingTiles(), oldUpdate.remainingTiles())
            ));
        } else {
            updateMap.put(ID, gameUpdateToFile);
        }

        Path path = Paths.get(GamePersistence.path);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File gamesDir = path.toFile();

        var files = Arrays.stream(Objects.requireNonNull(gamesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                var splitName = name.split("-");

                if (splitName[0].equals(startingString)) {
                    try {
                        var ignored = LocalDateTime.parse(splitName[2], timeFormat);

                        if (Integer.parseInt(splitName[1]) == ID) {
                            return true;
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        return false;
                    }
                }


                return false;
            }
        }))).sorted((fileFirst, fileSecond) -> {
            var firstDate = LocalDateTime.parse(fileFirst.getName().split("-")[2],timeFormat);
            var secondDate = LocalDateTime.parse(fileSecond.getName().split("-")[2],timeFormat);

            return secondDate.compareTo(firstDate);
        }).toList();

        for (int i = savingNumber - 1; i < files.size(); i++) {
            var ignored = files.get(i).delete();
        }

        LocalDateTime myDateObj = LocalDateTime.now();
        String formattedDate = myDateObj.format(timeFormat);
        String fileName = startingString + "-" + ID + "-" + formattedDate;

        File toFile = Paths.get(path.toString(), fileName).toFile();

        executor.execute(() -> {
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(toFile, updateMap.get(ID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadOldGames() {
        Path path = Paths.get(GamePersistence.path);

        if (Files.exists(path) && Files.isDirectory(path)) {
            File oldGamesDir = path.toFile();

            Arrays.stream(Objects.requireNonNull(oldGamesDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.startsWith(startingString)) {
                        var tempSplit = name.split("-");

                        Arrays.stream(tempSplit).forEach(System.out::println);

                        if (tempSplit.length == 3 && tempSplit[0].equals(startingString)) {
                            try {
                                Integer.parseInt(tempSplit[1]);
                                var ignored = LocalDateTime.parse(tempSplit[2], timeFormat);

                                return true;
                            } catch (NumberFormatException | DateTimeParseException e) {
                                return false;
                            }
                        }
                    }

                    return false;
                }
            }))).sorted((fileFirst, fileSecond) -> {
                var firstDate = LocalDateTime.parse(fileFirst.getName().split("-")[2],timeFormat);
                var secondDate = LocalDateTime.parse(fileSecond.getName().split("-")[2],timeFormat);

                return secondDate.compareTo(firstDate);
            }). forEach(file -> {
                int gameID = Integer.parseInt(file.getName().split("-")[1]);

                if(!updateMap.containsKey(gameID)) {
                    try {
                        GameUpdateToFile update = mapper.reader().readValue(file, GameUpdateToFile.class);

                        updateMap.put(gameID, update);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        System.err.println("File " + file.getName() + " is corrupted, trying with previous version");
                    }
                }
            });
        }

        GameController.ID = this.updateMap.keySet().stream().mapToInt(a -> a).max().orElseGet(() -> 0);

        Lobby.getInstance().loadLobbyFromUpdates(updateMap);
    }
}
