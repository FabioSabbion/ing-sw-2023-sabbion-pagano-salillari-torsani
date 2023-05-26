package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class CLIController implements ViewController {
    Map<String, PlayerUpdate> players;
    LivingRoom livingRoom;
    List<CommonGoalCardUpdate> commonGoalCards;
    PlayerUpdate currentPlayer;
    PlayerUpdate gameEnder;
    Server server;
    ClientImpl client;
    private CLI cli;
    private State state;
    private PlayerTurn playerTurn;
    private boolean gameFinished = false;

    String viewingPlayerNickname;
    private final List<String> menuNotifications = new ArrayList<>();

    public CLIController(ClientImpl client, Server server) {
        this.client = client;
        this.server = server;
        this.state = State.START;

        client.run(this);
    }

    enum State {
        START,
        WAITING,
        ASK_NUM_PLAYERS,
        GET_PLAYER_CHOICE,
        YOUR_TURN,
        END_GAME;

    }

    public void start() {
        this.changeState(State.START);
        CLI.initialScreen();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a nickname");

        while (!this.state.equals(State.END_GAME)) {
            this.inputHandler(scanner.nextLine());
        }
    }


    public void inputHandler(String input) {
        try {
            switch (this.state) {
                case START -> setNickname(input);
                case GET_PLAYER_CHOICE ->
                        getPlayerChoice(viewingPlayerNickname.equals(currentPlayer.nickname()), input);

                case ASK_NUM_PLAYERS -> this.setNumPlayers(input);

                case YOUR_TURN -> {
                    playerTurn.inputHandler(input);
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setNickname(String nickname) {
        try {

            if (!nickname.matches("[A-Za-z0-9]+")) {
                System.out.println("Inadmissible choice of characters! Try Again...");
                return;
            }

            this.viewingPlayerNickname = nickname;
            server.setNickname(nickname, client);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void getPlayerChoice(boolean yourTurn, String menuChoice) {

        switch (menuChoice.toLowerCase()) {
            case "help", "menu", "1" -> {
                cli.showMenu(yourTurn, menuNotifications, gameFinished);
            }
            case "main", "2" -> {
                cli.showMain(currentPlayer);

            }
            case "showPlayers", "3" -> {
                cli.showPlayers(this.players);
            }
            case "CommonGoalCards", "4" -> {
                cli.showCommonGoalCards();
            }
            case "scoreboard", "5" -> {
                if (!gameFinished)
                    cli.showScoreboard();
                else
                    this.showFinalScoreBoard();
            }
            case "play", "6", "quit" -> {
                if (yourTurn && !gameFinished && !menuChoice.equalsIgnoreCase("quit")) {
                    playerTurn = new PlayerTurn();
                    this.changeState(State.YOUR_TURN);
                } else if (gameFinished) {
                    System.out.println(Color.YELLOW.escape() + "Thanks for playing!" + Color.RESET);
                    this.changeState(State.END_GAME);
                } else System.out.println("It's not your turn! Please enter a valid option...");
            }
            default -> System.out.println("That is not a valid option! Enter a valid one");
        }
    }


    @Override
    public void updatedPlayerList(List<String> players) {
        players.stream().forEach(System.out::println);
    }


    @Override
    public void updateGame(GameUpdate update) {
        this.livingRoom = update.livingRoom() == null ? this.livingRoom : update.livingRoom();
        if (update.players() != null) {
            if (this.players == null) {
                this.players = new HashMap<>();
                for (PlayerUpdate playerUpdate : update.players()) {
                    this.players.put(playerUpdate.nickname(), playerUpdate);
                }
            } else {
                for (PlayerUpdate playerUpdate : update.players()) {
                    var prev = this.players.get(playerUpdate.nickname());

                    this.players.put(playerUpdate.nickname(),
                            new PlayerUpdate(prev.nickname(),
                                    Optional.ofNullable(playerUpdate.bookshelf()).orElse(prev.bookshelf()),
                                    Optional.ofNullable(playerUpdate.personalGoalCard()).orElse(prev.personalGoalCard())));
                }
            }
        }


        if (update.commonGoalCards() != null) {
            if (this.commonGoalCards != null) {
                for (CommonGoalCardUpdate card : update.commonGoalCards()) {
                    var matchingCard = this.commonGoalCards.stream().filter(c -> c.commonGoalCardID() == card.commonGoalCardID()).findFirst();
                    if (matchingCard.isPresent() && !matchingCard.get().playerUpdateList().equals(card.playerUpdateList())) {
                        card.playerUpdateList().stream().filter(player -> !matchingCard.get().playerUpdateList().contains(player)).forEach(player -> {
                            menuNotifications.add(Color.BLUE.escape() +
                                    ((player.equals((viewingPlayerNickname)) ? "You have" : (player + " has")) + " completed CommmonGoalCard n° "
                                            + this.commonGoalCards.indexOf(matchingCard.get())) + Color.RESET);
                        });

                        this.commonGoalCards.set(this.commonGoalCards.indexOf(matchingCard.get()), card);
                    }
                }

            } else {
                this.commonGoalCards = new ArrayList<>(update.commonGoalCards());
            }
        }

        boolean updatedCurrent = false;

        if (update.currentPlayer() != null) {
            this.currentPlayer = update.currentPlayer();
            updatedCurrent = true;
        }



        if (update.gameEnder() != null) {
            this.gameEnder = update.gameEnder();
        }

        if (cli == null) {
            PlayerUpdate viewingPlayer = players.values().stream().filter(p -> p.nickname().equals(viewingPlayerNickname))
                    .findFirst().get();
            cli = new CLI(this.livingRoom, this.players.values().stream().toList(), this.commonGoalCards,
                    this.currentPlayer, viewingPlayer, this);
        }


        cli.updateAll(this.livingRoom, this.players.values().stream().toList(), this.currentPlayer);


        if (update.gameEnder() != null) {
            menuNotifications.add(Color.RED.escape()
                    + (this.gameEnder.nickname().equals(viewingPlayerNickname) ? "You have filled your" : (this.gameEnder.nickname() + " has filled his"))
                    + " bookshelf. The game is ending" + Color.RESET);

            cli.showPlayerTurn(currentPlayer, menuNotifications, gameFinished);
        }

        if (updatedCurrent) {
            cli.showPlayerTurn(currentPlayer, menuNotifications, gameFinished);
            menuNotifications.clear();
            this.changeState(State.GET_PLAYER_CHOICE);
        }
    }

    @Override
    public void serverError(String message) {
        System.out.println(Color.RED.escape() + message + Color.RESET);
    }

    @Override
    public void askNumPlayers() {
        System.out.println("How many players will join your game? (2 - 4)");
        this.changeState(State.ASK_NUM_PLAYERS);
    }

    public void setNumPlayers(String input) throws RemoteException {
        try {
            if (Integer.parseInt(input) < 2 || Integer.parseInt(input) > 4)
                this.serverError("Wrong number of players, try again!");
            else {
                server.setNumPlayers(parseInt(input), client);
                System.out.println(Color.BLUE.escape() + "Waiting for number of players..." + Color.RESET);
                this.changeState(State.WAITING);
            }
        } catch (NumberFormatException e) {
            this.serverError("You must choose a number");
        }

    }

    public Map<String, Integer> calculateCommonGoalCardPoints(int cardID) {
        Map<String, Integer> playerPoints = new HashMap<>();

        CommonGoalCardUpdate commonGoalCard = this.commonGoalCards.stream().filter(card -> card.commonGoalCardID() == cardID).findFirst().get();

        for (PlayerUpdate player : this.players.values()) {
            if (commonGoalCard.playerUpdateList().contains(player.nickname())){
                playerPoints.put(player.nickname(), CommonGoalCard.points[this.players.size()][commonGoalCard.playerUpdateList().indexOf(player.nickname())]);
            }
            else {
                playerPoints.put(player.nickname(), 0);
            }
        }

        return playerPoints;
    }

    /**
     * Calculates viewingPlayer's PersonalGoalCard points
     *
     * @return
     */
    public int calculatePersonalGoalCardPoints() {
        return this.players.get(viewingPlayerNickname).personalGoalCard().point();
    }
    public int calculatePersonalGoalCardPoints(String playerNickname) {
        return this.players.get(playerNickname).personalGoalCard().point();
    }

    private synchronized void changeState(State state) {
        this.state = state;
    }

    public void returnTiles(List<Coordinates> coordinates, int column) {

        var yourself = this.players.values().stream().filter(player -> player.nickname().equals(this.viewingPlayerNickname)).findFirst().get();

        try {
            var tempTiles = this.livingRoom.chooseTiles(coordinates);

            yourself.bookshelf().pickFirstFreeIndex(column, tempTiles);

            this.changeState(State.GET_PLAYER_CHOICE);

            this.server.playerMove(coordinates, column, client);
        } catch (PickTilesException ignored) {
            this.serverError("You cannot choose this tiles");

            this.playerTurn = new PlayerTurn();
            this.changeState(State.YOUR_TURN);
        } catch (NotEnoughCellsException e) {
            this.serverError("This column is already full");

            this.playerTurn = new PlayerTurn();
            this.changeState(State.YOUR_TURN);
        } catch (RemoteException e) {
            this.changeState(State.GET_PLAYER_CHOICE);

            this.serverError("Connection problem");
        }
    }

    @Override
    public void showEndingScreen() {
        String winningPlayer = this.showFinalScoreBoard();

        this.gameFinished = true;
        cli.showEndScreen(winningPlayer);
    }

    private String showFinalScoreBoard() {
        Map<String, Integer> playerPoints = new HashMap<>();

        for (String player: new ArrayList<>(this.players.keySet())) {
            playerPoints.put(player, calculatePersonalGoalCardPoints(player));
        }

        for (CommonGoalCardUpdate card : this.commonGoalCards) {
            var cardPoints = calculateCommonGoalCardPoints(card.commonGoalCardID());

            for (String player : new ArrayList<>(playerPoints.keySet())) {
                playerPoints.put(player, playerPoints.get(player) + cardPoints.get(player));
            }
        }

        System.out.print("\n".repeat(60));
        System.out.println(Color.BLUE.escape() + ASCIIArt.scoreBoard + Color.RESET);

        System.out.println(Color.BLUE.escape() + "\n Final Results are:" + Color.RESET);

        for (String player: new ArrayList<>(this.players.keySet())) {
            System.out.println("- " + player + ": " + playerPoints.get(player));
        }

        int maxPoint = playerPoints.values().stream().mapToInt(x -> x).max().getAsInt();
        return playerPoints.entrySet().stream().filter(p -> p.getValue() == maxPoint).findFirst().get().getKey();
    }


    class PlayerTurn {
        int rowLivingRoom, colLivingRoom, columnBookShelf, numTiles;
        List<Coordinates> coordinates;
        TurnState state;

        enum TurnState {
            NUM_TILES,
            ROW_LIVING_ROOM,
            ADD_COORDINATE,
            COL_LIVING_ROOM,
            COL_BOOKSHELF;

        }

        public PlayerTurn() {
            System.out.println("How many tiles do you want? (1 - 3) (enter 'back' to go back to the menu)");
            this.state = TurnState.NUM_TILES;
            this.rowLivingRoom = -1;
            this.colLivingRoom = -1;
            this.columnBookShelf = -1;
            this.numTiles = -1;
            this.coordinates = new ArrayList<>();
        }

        private synchronized void changeState(TurnState state) {
            this.state = state;
        }

        public void inputHandler(String input) {

            try {
                if (input.equalsIgnoreCase("back")){
                    CLIController.this.changeState(State.GET_PLAYER_CHOICE);
                    return;
                }
                int inputInt = Integer.parseInt(input);

                switch (this.state) {
                    case NUM_TILES -> this.setNumTiles(inputInt);

                    case ROW_LIVING_ROOM -> this.setRowLivingRoom(inputInt);

                    case COL_LIVING_ROOM -> this.setColLivingRoom(inputInt);

                    case COL_BOOKSHELF -> this.setColBookShelf(inputInt);
                }
            } catch (NumberFormatException e) {
                serverError("You must enter a number!");
            }

        }

        private void setColBookShelf(int inputInt) {
            if (inputInt < 0 || inputInt > 4)
                serverError("You must pick a number between 0 and 4");
            else {
                this.columnBookShelf = inputInt;
                returnTiles(this.coordinates, this.columnBookShelf);
            }
        }

        private void setColLivingRoom(int inputInt) {
            if (inputInt < 0 || inputInt > 8)
                serverError("Column must range from 0 to 8!");
            else {
                this.colLivingRoom = inputInt;
                this.addCoordinate();
            }
        }

        private void setRowLivingRoom(int inputInt) {
            if (inputInt < 0 || inputInt > 8)
                serverError("Row must range from 0 to 8!");
            else {
                this.rowLivingRoom = inputInt;
                this.getColumnLivingRoom();
            }
        }

        void getRowLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Row (enter 'back' to go back to the menu)");
            this.changeState(TurnState.ROW_LIVING_ROOM);
        }

        void getColumnLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Column (enter 'back' to go back to the menu)");
            this.changeState(TurnState.COL_LIVING_ROOM);
        }

        void addCoordinate() {
            coordinates.add(new Coordinates(rowLivingRoom, colLivingRoom));
            numTiles--;

            if (numTiles > 0) {
                this.getRowLivingRoom();
                this.changeState(TurnState.ROW_LIVING_ROOM);
            } else {
                this.getColumnBookshelf();
            }
        }

        private void getColumnBookshelf() {
            cli.showMain(currentPlayer);
            System.out.printf("Choose your column (0-%d) (enter 'back' to go back to the menu)\n", Bookshelf.COLUMNS - 1);
            this.changeState(TurnState.COL_BOOKSHELF);
        }

        public void setNumTiles(int inputInt) {
            if (inputInt < 1 || inputInt > 3)
                serverError("Wrong number of tiles! Try Again...");
            else {
                this.numTiles = inputInt;
                this.getRowLivingRoom();
            }
        }

    }

}

