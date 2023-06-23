package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.Lobby;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.models.*;
import it.polimi.ingsw.models.exceptions.NotEnoughCellsException;
import it.polimi.ingsw.models.exceptions.PickTilesException;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
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
    private final SortedMap<Integer, Message> chatMessages = new TreeMap<>();

    String viewingPlayerNickname;
    private final List<String> menuNotifications = new ArrayList<>();
    private List<String> offlinePlayers = new ArrayList<>();


    enum State {
        START,
        WAITING,
        ASK_NUM_PLAYERS,
        GET_PLAYER_CHOICE,
        YOUR_TURN,
        END_GAME,
        CHAT;

    }

    /**
     * The start method initializes and starts the game by setting up the client and server, handling user input,
     * and controlling the game flow.
     * @param client: an instance of the ClientImpl class representing the client for the game.
     * @param server: an instance of the Server class representing the server for the game.
     */
    @Override
    public void start(ClientImpl client, Server server) {
        this.client = client;
        this.server = server;
        this.state = State.START;

        client.run(this);

        this.changeState(State.START);
        CLI.initialScreen();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a nickname");

        while (!this.state.equals(State.END_GAME)) {
            this.inputHandler(scanner.nextLine());
        }
    }

    /**
     * The inputHandler method is responsible for handling user input during different states of the game.
     * It processes the input and performs the corresponding actions based on the current state.
     * @param input: the user's input.
     */
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
                case CHAT -> {
                    this.sendMessage(input);
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is responsible for setting the nickname of the viewing player in the game.
     * It checks if 'nickname' is made of alphanumerical characters
     * and updates the viewingPlayerNickname field accordingly.
     * It also notifies the server of the nickname change.
     * @param nickname: a <code>String</code> representing the nickname to be set for the viewing player.
     */
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

    /**
     * This method handles the player's menu choice.
     * @param yourTurn: a boolean value indicating whether it is the player's turn.
     *               If true lets the player pick the tiles from the livingRoom
     * @param menuChoice: menu choice made by the player.
     */
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
                cli.showPlayers(this.players, this.offlinePlayers);
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
            case "chat", "6" -> {
                this.openChat();
            }
            case "play", "7", "quit" -> {
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

    /**
     * Opens the chat and let you send a message to everyone or to a specific player in the game
     */
    private void openChat() {

        System.out.println(Color.PURPLE.escape() + "Chat messages\n" + Color.RESET);

        for (Message message: this.chatMessages.values()) {
            System.out.println("- ["
                    + message.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + "] " + (message.to() == null ? "@All " : "")
                    + (message.from().equals(viewingPlayerNickname) ? "You" : message.from()) + ": " + message.message());
        }

        System.out.println(
                Color.PURPLE.escape() + "To send a message enter " +
                Color.RED.escape() + "PlayerName" +
                Color.PURPLE.escape() + "/" +
                Color.RED.escape() + "@All" +
                Color.PURPLE.escape() + "'" +
                Color.RED.escape() + " message" +
                Color.PURPLE.escape() + "'" +
                Color.RESET);
        System.out.println(
                Color.PURPLE.escape() + "To go back to the menu enter '" +
                Color.RED.escape() + "back" +
                Color.PURPLE.escape() + "'" +
                Color.RESET);

        this.changeState(State.CHAT);
    }

    /**
     * This method is responsible for sending a message to a specific recipient or to all players in the game.
     * It handles the formatting of the input message, validates the recipient,
     * and sends the message to the server for further processing.
     * @param input: a <code>String</code> representing the message to be sent
     */
    private void sendMessage(String input) {
        String[] splitInput = input.split(" ");

        if (splitInput.length == 1) {
            if (splitInput[0].equals("back")){
                this.changeState(State.GET_PLAYER_CHOICE);
                this.cli.showMenu(viewingPlayerNickname.equals(currentPlayer.nickname()), menuNotifications, gameFinished);
            } else
                this.serverError("Wrong formatting for the message!");

            return;
        }

        String recipient = splitInput[0];
        String message = input.substring(recipient.length() + 1);


        List<String> players = new ArrayList<>(this.players.keySet());

        if (recipient.equals(viewingPlayerNickname)){
            this.serverError("It is pointless to send a message to yourself! Try Again");
            return;
        }

        if (!players.contains(recipient) && !recipient.equals("@All")){
            this.serverError("There's no such message recipient! Try Again");
            return;
        }

        try {
            this.server.sendMessageTo(recipient.equals("@All") ? null : recipient, message, this.client);
            System.out.println(Color.BLUE.escape() + "Message was successfully sent!" + Color.RESET);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        this.cli.showMenu(viewingPlayerNickname.equals(currentPlayer.nickname()), menuNotifications, gameFinished);
        this.changeState(State.GET_PLAYER_CHOICE);
    }

    /**
     * Adds all the new chat messages in <code>this.chatMessages</code>.
     * @param messages: list of {@link Message}.
     */
    @Override
    public void receiveMessages(List<Message> messages){
        for (Message message: messages){
           chatMessages.put(message.id(), message);
        }
    }


    /**
     * The updatedPlayerList method is called when the player list is updated, either due to players joining or
     * leaving the game. It compares the updated player list with the previous player list to determine if any players
     * have disconnected or reconnected.
     * It also updates the offline player list and displays relevant notifications to the player.
     * @param players
     */
    @Override
    public void updatedPlayerList(List<String> players) {
        if (this.players != null){

            if ((this.players.size() - players.size()) < offlinePlayers.size() && offlinePlayers.size() != 0){
                //Means someone has reconnected

                for (String updatedPlayer: players) {
                    if (offlinePlayers.contains(updatedPlayer)){
                        menuNotifications.add(Color.RED.escape() + updatedPlayer + " has reconnected" + Color.RESET);

                        offlinePlayers.remove(updatedPlayer);
                    }
                }
            } else {
                this.offlinePlayers = new ArrayList<>(this.players.keySet());
                this.offlinePlayers.removeAll(players);

                for (String offlinePlayer: offlinePlayers) {
                    menuNotifications.add(Color.RED.escape() + offlinePlayer + " has disconnected" + Color.RESET);
                }

                if (this.players.keySet().size() - offlinePlayers.size() == 1){
                    menuNotifications.add(Color.RED.escape() + ("You are the only player left!" +
                            " After %d seconds you will win if no one reconnects").formatted(Lobby.seconds) + Color.RESET);
                }
            }

            cli.showPlayerTurn(currentPlayer, menuNotifications, gameFinished);
        }
    }


    /**
     * The <code>updateGame</code> method is called when a game update is received from the server. It updates the game state,
     * including the living room, player information, common goal cards, current player, and game ender. It also handles
     * notifications, updates the CLI interface, and triggers state changes if necessary.
     * @param update: a <code>GameUpdate</code> object containing the updated game information.
     */
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

    /**
     * The serverError method is called to display an error message received from the server. It prints the error message in red color to the console.
     * @param message: A <code>String</code> representing the error message received from the server.
     */
    @Override
    public void serverError(String message) {
        System.out.println(Color.RED.escape() + message + Color.RESET);
    }

    /**
     * It displays a message asking the user to enter the number of players within a specified range.
     */
    @Override
    public void askNumPlayers() {
        System.out.println("How many players will join your game? (2 - 4)");
        this.changeState(State.ASK_NUM_PLAYERS);
    }

    /**
     * This method is called to set the number of players in the game based on the user's input and updates the server.
     * @param input: a <code>String</code>> representing the user's input for the number of players.
     * @throws RemoteException
     */
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

    /**
     * It calculates the points earned by each player for a specific common goal card.
     * @param cardID: represents the ID of the CommonGoalCard for which the points are to be calculated.
     * @return a map where for each player's nickname there's the corresponding points for this specific CommonGoalCard.
     */
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
     * Calculates viewingPlayer's PersonalGoalCard points.
     *
     * @return total points of the viewingPlayer's PersonalGoalCard.
     */
    public int calculatePersonalGoalCardPoints() {
        return this.players.get(viewingPlayerNickname).personalGoalCard().point();
    }

    /**
     * Calculates <b>playerNickname</b>'s PersonalGoalCard points.
     * @param playerNickname: a <code>String</code> representing the player's nickname
     * @return total points of <b>playerNickname</b>'s PersonalGoalCard.
     */
    public int calculatePersonalGoalCardPoints(String playerNickname) {
        return this.players.get(playerNickname).personalGoalCard().point();
    }

    /**
     * Changes CLIController's current state
     * @param state
     */
    private synchronized void changeState(State state) {
        this.state = state;
    }

    /**
     * This method is responsible for handling the action of a player picking the tiles from LivingRoom in a game. It takes
     * a list of coordinates representing the tiles to be returned and an integer column indicating the column
     * where the tiles should be placed
     * @param coordinates: a list of coordinates indicating the tiles chosen in the board.
     * @param column: an integer indicating the column where the returned tiles should be placed in the bookshelf.
     */
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

    /**
     * This method show the final ending screen, with the scoreboard and the winner. If the viewingPlayer is the last
     * online player, he is automatically the winner.
     */
    @Override
    public void showEndingScreen() {
        String winningPlayer = this.showFinalScoreBoard();

        if (offlinePlayers.size() == players.size() - 1 && !offlinePlayers.contains(viewingPlayerNickname)){
            winningPlayer = this.viewingPlayerNickname;
        }

        this.gameFinished = true;
        cli.showEndScreen(winningPlayer);
    }


    /**
     *  This private method is responsible for displaying the <b>final scoreboard </b> of the game
     *  and determining the winner based on the calculated points. The points are the sum of the PersonalGoalCard,
     *  CommonGoalCard, and GameEnding points.
     * @return String: The nickname of the winning player based on maximum score.
     */
    private String showFinalScoreBoard() {
        Map<String, Integer> playerPoints = new HashMap<>();

        for (String player: new ArrayList<>(this.players.keySet())) {
            playerPoints.put(player, calculatePersonalGoalCardPoints(player) + this.players.get(player).bookshelf().getPoints());
        }

        for (CommonGoalCardUpdate card : this.commonGoalCards) {
            var cardPoints = calculateCommonGoalCardPoints(card.commonGoalCardID());

            for (String player : new ArrayList<>(playerPoints.keySet())) {
                playerPoints.put(player, playerPoints.get(player) + cardPoints.get(player));
            }
        }

        if (gameEnder != null)
            playerPoints.put(gameEnder.nickname(), playerPoints.get(gameEnder.nickname()) + 1 );

        System.out.print("\n".repeat(60));
        System.out.println(Color.BLUE.escape() + ASCIIArt.scoreBoard + Color.RESET);

        System.out.println(Color.BLUE.escape() + "\n Final Results are:" + Color.RESET);

        for (String player: new ArrayList<>(this.players.keySet())) {
            System.out.println("- " + player + ": " + playerPoints.get(player));
        }

        int maxPoint = playerPoints.values().stream().mapToInt(x -> x).max().getAsInt();
        return playerPoints.entrySet().stream().filter(p -> p.getValue() == maxPoint).findFirst().get().getKey();
    }

    /**
     * This class represents a player's turn in the game. It manages the state
     * and flow of the player's actions during their turn when choosing the tiles and the column from the livingRoom.
     */
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

        /**
         * The inputHandler method is responsible for handling the user's input during the player's turn
         * @param input: a String representing the user's input.
         */
        public void inputHandler(String input) {

            try {
                if (input.equalsIgnoreCase("back")){
                    CLIController.this.changeState(State.GET_PLAYER_CHOICE);
                    CLIController.this.cli.showMenu(viewingPlayerNickname.equals(currentPlayer.nickname()), CLIController.this.menuNotifications, CLIController.this.gameFinished);
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

        /**
         * Sets the column in the bookshelf where to place the choosen tiles.
         * It also calls the returnTiles method with the given coordinates and column number.
         * @param inputInt: the column number in the bookshelf chosen by the player.
         */
        private void setColBookShelf(int inputInt) {
            if (inputInt < 0 || inputInt > 4)
                serverError("You must pick a number between 0 and 4");
            else {
                this.columnBookShelf = inputInt;
                returnTiles(this.coordinates, this.columnBookShelf);
            }
        }

        /**
         * Lets the player choose the column in the livingRoom from which to pick the tile
         * @param inputInt: the column number in the livingRoom chosen by the player
         */
        private void setColLivingRoom(int inputInt) {
            if (inputInt < 0 || inputInt > 8)
                serverError("Column must range from 0 to 8!");
            else {
                this.colLivingRoom = inputInt;
                this.addCoordinate();
            }
        }

        /**
         * Lets the player choose the row in the livingRoom from which to pick the tile
         * @param inputInt: the row number in the livingRoom chosen by the player
         */
        private void setRowLivingRoom(int inputInt) {
            if (inputInt < 0 || inputInt > 8)
                serverError("Row must range from 0 to 8!");
            else {
                this.rowLivingRoom = inputInt;
                this.getColumnLivingRoom();
            }
        }

        /**
         * Tells the player to choose a row in the livingRoom
         */
        void getRowLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Row (enter 'back' to go back to the menu)");
            this.changeState(TurnState.ROW_LIVING_ROOM);
        }
        /**
         * Tells the player to choose a column in the livingRoom
         */
        void getColumnLivingRoom() {
            cli.showMain(currentPlayer);
            System.out.println("\nChoose Column (enter 'back' to go back to the menu)");
            this.changeState(TurnState.COL_LIVING_ROOM);
        }

        /**
         * Add a {@link Coordinates} made of the row and the column chosen by the player
         */
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

        /**
         * Tells the player to choose a column in the bookshelf
         */
        private void getColumnBookshelf() {
            cli.showMain(currentPlayer);
            System.out.printf("Choose your column (0-%d) (enter 'back' to go back to the menu)\n", Bookshelf.COLUMNS - 1);
            this.changeState(TurnState.COL_BOOKSHELF);
        }

        /**
         * Lets the player choose the number of {@link Tile} to pick, ranging from 1 to 3
         * @param inputInt: the number of tiles chosen by the player
         */
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

