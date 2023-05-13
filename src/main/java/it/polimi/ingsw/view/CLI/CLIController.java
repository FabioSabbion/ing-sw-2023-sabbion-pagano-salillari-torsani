package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.view.CLI.utils.Color;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class CLIController implements ViewController {
    List<PlayerUpdate> players;
    LivingRoom livingRoom;
    List<CommonGoalCardUpdate> commonGoalCards;
    PlayerUpdate currentPlayer;
    PlayerUpdate gameEnder;
    Server server;
    ClientImpl client;
    private CLI cli;
    private State state;
    private PlayerTurn playerTurn;

    String viewingPlayerNickname;

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
        YOUR_TURN;

    }

    public void start() {
        this.changeState(State.START);
        CLI.initialScreen();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a nickname");

        while (true) {
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

                case YOUR_TURN -> playerTurn.inputHandler(input);
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
                cli.showMenu(yourTurn);
            }
            case "main", "2" -> {
                cli.showMain(currentPlayer);
            }
            case "showPlayers", "3" -> {
                cli.showPlayers(this.calculatePoints());
            }
            case "CommonGoalCards", "4" -> {
                cli.showCommonGoalCards();
            }
            case "scoreboard", "5" -> {
                cli.showScoreboard(this.calculatePoints());
            }
            case "play", "6" -> {
                if (yourTurn) {
                    playerTurn = new PlayerTurn();
                    this.changeState(State.YOUR_TURN);
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
                this.players = new ArrayList<>(update.players());
            } else {
                for (int i = 0; i < update.players().size(); i++) {
                    for (int j = 0; j < this.players.size(); j++) {
                        if (this.players.get(j).nickname().equals(update.players().get(i).nickname())) {
                            this.players.set(j, update.players().get(i));
                        }
                    }
                }
            }
        }
        this.commonGoalCards = update.commonGoalCards() == null ? this.commonGoalCards : update.commonGoalCards();

        boolean updatedCurrent = false;

        if (update.currentPlayer() != null) {
            this.currentPlayer = update.currentPlayer();
            updatedCurrent = true;
        }

        this.gameEnder = update.gameEnder() == null ? this.gameEnder : update.gameEnder();

        if (cli == null) {
            PlayerUpdate viewingPlayer = players.stream().filter(p -> p.nickname().equals(viewingPlayerNickname))
                    .findFirst().get();
            cli = new CLI(this.livingRoom, this.players, this.commonGoalCards,
                    this.currentPlayer, this.gameEnder, viewingPlayer);
        }


        cli.updateAll(this.livingRoom, this.players, this.currentPlayer, this.gameEnder);



        if (updatedCurrent) {
            cli.showPlayerTurn(currentPlayer);
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

                this.changeState(State.WAITING);
            }
        } catch (NumberFormatException e) {
            this.serverError("You must choose a number");
        }

    }

    public HashMap<PlayerUpdate, Integer> calculatePoints() {
        HashMap<PlayerUpdate, Integer> playerPoints = new HashMap<>();
        for (PlayerUpdate player : this.players) {
            // TODO: IT'S ALL FAKE

            playerPoints.put(player, 10);
        }

        return playerPoints;
    }

    private synchronized void changeState(State state) {
        this.state = state;
    }

    public void returnTiles(List<Coordinates> coordinates, int column) {

        var yourself = this.players.stream().filter(player -> player.nickname().equals(this.viewingPlayerNickname)).findFirst().get();

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
            System.out.println("How many tiles do you want? (1 - 3)");
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


        void getNumTiles() {
            this.changeState(TurnState.NUM_TILES);
        }

        void getRowLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Row");
            this.changeState(TurnState.ROW_LIVING_ROOM);
        }

        void getColumnLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Column");
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
            System.out.printf("Choose your column (0-%d)\n", Bookshelf.COLUMNS - 1);
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

