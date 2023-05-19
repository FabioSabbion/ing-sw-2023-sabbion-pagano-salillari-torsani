package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.utils.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GamePersistence {
    static public final String path = "oldGames";
    static public final String startingString = "game";
    static public final int savingNumber = 3;

    private final Map<Integer, GameUpdateToFile> updateMap = new TreeMap<>();

    public GamePersistence() {

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

            List<CommonGoalCardUpdate> commonGoalCardUpdates = gameOldUpdate.commonGoalCards();

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

        int nextIndex = Arrays.stream(Objects.requireNonNull(gamesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                var splitName = name.split("-");

                if (splitName[0].equals(startingString)) {
                    try {
                        Integer.parseInt(splitName[2]);

                        if (Integer.parseInt(splitName[1]) == ID) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }


                return false;
            }
        }))).mapToInt(a -> Integer.parseInt(a.getName().split("-")[2])).max().orElse(-1) + 1;

        String fileName = startingString + "-" + ID + "-" + nextIndex % savingNumber;

        File toFile = Paths.get(path.toString(), fileName).toFile();


        new Thread(() -> {
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(new FileOutputStream(toFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                oos.writeObject(updateMap.get(ID));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
                                Integer.parseInt(tempSplit[2]);

                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                    }

                    return false;
                }
            }))).sorted((fileFirst, fileSecond) -> {
                int firstNum = Integer.parseInt(fileFirst.getName().split("-")[2]);
                int secondNum = Integer.parseInt(fileSecond.getName().split("-")[2]);

                return secondNum - firstNum;
            }).forEach(file -> {
                int gameID = Integer.parseInt(file.getName().split("-")[1]);

                if(!updateMap.containsKey(gameID)) {
                    try {
                        var ois = new ObjectInputStream(new FileInputStream(file));

                        GameUpdateToFile update = (GameUpdateToFile) ois.readObject();

                        updateMap.put(gameID, update);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException | IOException e) {
                        System.err.println("File " + file.getName() + " is corrupted, trying with previous version");
                    }
                }
            });
        }

        GameController.ID = this.updateMap.keySet().stream().mapToInt(a -> a).max().orElseGet(() -> 0);

        Lobby.getInstance().loadLobbyFromUpdates(updateMap);
    }
}
