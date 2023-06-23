package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {
    private final CLIController controller;
    private static final Printer printer = new Printer();
    private String renderPersonalGoalCard;
    private final HashMap<String, String> renderBookshelves;
    private final HashMap<CommonGoalCardUpdate, String> renderCommonGoalCards;
    private String renderLivingRoom;
    public PlayerUpdate viewingPlayer;

    public CLI (LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards,
                PlayerUpdate currentPlayer, PlayerUpdate viewingPlayer, CLIController controller){

        this.viewingPlayer = viewingPlayer;
        this.renderCommonGoalCards = new HashMap<>();
        this.renderBookshelves = new HashMap<>();
        this.controller = controller;

        setRenderPersonalGoalCard();
        setRenderBookshelves(players);
        setRenderCommonGoalCards(commonGoalCards);
        updateAll(livingRoom, players, currentPlayer);
    }

    /**
     * Shows the initial MyShelfie logo
     */
    public static void initialScreen() {
        printer.clearScreen();
        printer.print(Color.YELLOW.escape() + ASCIIArt.myShelfieLogo + Color.RESET);
        printer.print("\n\nWelcome to MyShelfie!");
    }

    /**
     * Shows the player all the possible choices in the menu. If it's the viewing player turn, it shows the 'play' option.
     * If the game is finished it lets you quit the game.
     * @param yourTurn: a boolean value indicating whether it is or not the viewing player's turn.
     * @param menuNotification: a list of <code>String</code> with all the notifications.
     * @param gameFinished: a boolean value indicating whether the game is finished.
     */
    public void showMenu(boolean yourTurn, List<String> menuNotification, boolean gameFinished){
        String choices = """
                Menu:
                1. 'help'/'menu' -> show all commands
                2. 'main' -> show the main view (LivingRoom, PersonalGoalCard, Bookshelf)
                3. 'showPlayers' -> shows players' bookshelf and respective points
                4. 'CommonGoalCards' -> outputs the game commonGoalCards
                5. 'scoreboard' -> view scoreboard
                6. 'chat -> open chat""";

        if (yourTurn && !gameFinished) {
            choices += "\n7. 'play' -> 'play your turn";
        } else if (gameFinished) {
            choices += "\n7. 'quit' -> 'end your game";
        }


        printer.print(Color.GREEN.escape() + choices + Color.RESET);

        if (menuNotification != null) {
            for (String notifications : menuNotification) {
                System.out.println(notifications);
            }
        }
    }

    /**
     * Sets the PersonalGoalCard render
     */
    private void setRenderPersonalGoalCard(){
        renderPersonalGoalCard = CLIRenderer.renderPersonalGoalCard(viewingPlayer.personalGoalCard());
    }

    /**
     * Sets the Bookshelves renders
     */
    private void setRenderBookshelves(List<PlayerUpdate> players){
        for (PlayerUpdate player : players) {
            this.renderBookshelves.put(player.nickname(), CLIRenderer.renderBookshelf(player.bookshelf()));
        }
    }

    /**
     * Sets the LivingRoom render
     */
    public void setLivingRoom(LivingRoom livingRoom){
        this.renderLivingRoom = CLIRenderer.renderLivingRoom(livingRoom);
    }

    /**
     * Shows all the players' bookshelves and the offline players.
     * @param playerMap
     * @param offlinePlayers
     */
    public void showPlayers(Map<String, PlayerUpdate> playerMap, List<String> offlinePlayers){

        String concatBookshelves = "";

        List<String> players = new ArrayList<>(playerMap.keySet());

        for (String player : players) {
            concatBookshelves = CLIRenderer.concatAsciiArt(concatBookshelves, this.renderBookshelves.get(player));
        }
        printer.print(concatBookshelves + "\n\n\n");

        for (String player : players) {
            if (player.equals(viewingPlayer.nickname())){
                printer.print("- You ( " + player + " )");
            }
            else {
                printer.print("- " + player);
            }
        }

        printer.print("\n Offline Players:");
        for (String offlinePlayer : offlinePlayers) {
            printer.print("- " + offlinePlayer);
        }
    }

    /**
     * Sets the CommonGoalCards' renders
     * @param commonGoalCards
     */
    public void setRenderCommonGoalCards(List<CommonGoalCardUpdate> commonGoalCards){
        for (var commonGoalCard : commonGoalCards) {
            this.renderCommonGoalCards.put(commonGoalCard, CLIRenderer.renderCommonGoalCard(commonGoalCard));
        }
    }

    /**
     * Shows the main view of the viewing player, consisting of the LivingRoom, the PersonalGoalCard, and the Bookshelf
     * @param currentPlayer: the current player in the game
     */
    public void showMain(PlayerUpdate currentPlayer){
        printer.clearScreen();
        String main = renderLivingRoom;
        main = CLIRenderer.concatAsciiArt(main, renderPersonalGoalCard);
        main = CLIRenderer.concatAsciiArt(main, renderBookshelves.get(viewingPlayer.nickname()));
        printer.print(main);

        var players = new ArrayList<>(renderBookshelves.keySet());

        for (int i = 0; i < players.size(); i++) {
            String toPrint = i +". " + players.get(i);

            if(players.get(i).equals(currentPlayer.nickname())){
                toPrint = Color.RED.escape() + toPrint + Color.RESET;
            }

            printer.print(toPrint);
        }
    }

    /**
     * Shows all the CommonGoalCards and their respective descriptions
     */
    public void showCommonGoalCards(){
        printer.clearScreen();

        List<String> renders = renderCommonGoalCards.values().stream().toList();

        for (int i = 0; i < renderCommonGoalCards.size(); i++) {
            printer.print("CommonGoalCard" + i);
            printer.print("\n\n");
            printer.print(renders.get(i));
        }
    }

    /**
     * Shows the partial scoreboard, consisting of your PersonalGoalCard point and all the players' CommonGoalCards points
     */
    public void showScoreboard(){
        List<String> players = new ArrayList<>(controller.players.keySet());
        printer.clearScreen();
        printer.print(Color.BLUE.escape() + ASCIIArt.scoreBoard + Color.RESET);
        var commonGoalCards = controller.commonGoalCards;

        for (int i = 0; i < commonGoalCards.size(); i++) {
            printer.print(Color.BLUE.escape() + "The current players' CommonGoalCard " + i + " points are: " + Color.RESET);
            var playerPoints = controller.calculateCommonGoalCardPoints(commonGoalCards.get(i).commonGoalCardID());

            for (int j = 0; j < players.size(); j++) {
                String toPrint = j +". " + players.get(j) + " : " + playerPoints.get(players.get(j));
                printer.print(toPrint);
            }
        }

        int yourPersonalGoalCardPoint = controller.calculatePersonalGoalCardPoints();

        printer.print(Color.BLUE.escape() + "Your PersonalGoalCard points:" + Color.RESET);
        printer.print(String.valueOf(yourPersonalGoalCardPoint));

        if (controller.gameEnder != null) {
            printer.print(Color.BLUE.escape() + "GameEnder point:" + Color.RESET);
            printer.print("1");
        }
    }

    /**
     * Updates the CLI
     * @param livingRoom
     * @param players
     * @param currentPlayer
     */
    public void updateAll(LivingRoom livingRoom, List<PlayerUpdate> players, PlayerUpdate currentPlayer){
        setLivingRoom(livingRoom);
        setRenderBookshelves(players);
    }

    /**
     * It shows the viewingPlayer who is currently playing
     * @param currentPlayer
     * @param menuNotification
     * @param gameFinished
     */
    public void showPlayerTurn(PlayerUpdate currentPlayer, List<String> menuNotification, boolean gameFinished){
        printer.clearScreen();
        boolean yourTurn = viewingPlayer.nickname().equals(currentPlayer.nickname());
        if(!yourTurn) {
            printer.print(Color.BLUE.escape() + currentPlayer.nickname() + " is now playing his turn" + Color.RESET);
        }
        else{
            printer.print(Color.BLUE.escape() + "Now is your turn" + Color.RESET);
        }
        this.showMenu(yourTurn, menuNotification, gameFinished);
    }

    /**
     * Shows whether you have won or lost
     * @param winningPlayer: the winner of the game
     */
    public void showEndScreen(String winningPlayer){

        if (winningPlayer.equals(viewingPlayer.nickname())){
            printer.print(Color.GREEN.escape() + ASCIIArt.youWon + Color.RESET);
        } else {
            printer.print(Color.RED.escape() + ASCIIArt.gameOver + Color.RESET);
        }

        printer.print(Color.YELLOW.escape() + "\n\n\n You can look at your menu by typing the same commands. To end the game enter 'quit'" + Color.RESET);
    }


}