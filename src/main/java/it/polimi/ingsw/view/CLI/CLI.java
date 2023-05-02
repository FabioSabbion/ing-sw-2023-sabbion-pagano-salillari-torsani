package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.LivingRoomUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private CLIRenderer render;
    private CLIController controller;
    private Printer printer;
    private HashMap<PlayerUpdate, String> renderPersonalGoalCards;
    private HashMap<PlayerUpdate, String> renderBookshelves;
    private HashMap<CommonGoalCard, String> renderCommonGoalCards;
    private String renderLivingRoom;
    private PlayerUpdate viewingPlayer;
    private PlayerUpdate currentPlayer;
    private PlayerUpdate gameEnder;

    public CLI (LivingRoomUpdate livingRoom, List<PlayerUpdate> players, List<CommonGoalCard> commonGoalCards,
                PlayerUpdate currentPlayer, PlayerUpdate gameEnder, PlayerUpdate viewingPlayer){

        this.printer = new Printer();
        this.render = new CLIRenderer();
        this.viewingPlayer = viewingPlayer;

        setRenderBookshelves(players);
        setRenderCommonGoalCards(commonGoalCards);
        updateAll(livingRoom, players, currentPlayer, gameEnder);
    }


    public boolean initialScreen() {
        try {
            printer.clearScreen();
            printer.print(Color.YELLOW.escape() + ASCIIArt.myShelfieLogo + Color.RESET);
            printer.print("1: Start Game");
            System.out.println("\n");
            printer.print("2: Exit Game");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> {
                        return true;
                    }
                    case "2" -> {
                        return false;
                    }
                    default -> System.out.println("That is not a valid option! Enter a valid one");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int showMenu(boolean yourTurn){
        String choices = """
                Menu:
                1. 'help'/'menu -> show all commands
                2. 'main' -> show the main view (LivingRoom, PersonalGoalCard, Bookshelf)
                3. 'showPlayers' -> shows players' bookshelf and respective points
                4. 'CommonGoalCards' -> outputs the game commonGoalCards
                5. 'scoreboard' -> view scoreboard""";

        if (yourTurn)
            choices += "\n6. 'play' -> 'play your turn";
        try {
            printer.print(Color.GREEN.escape() + choices + Color.RESET);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "help", "menu", "1" -> {
                        return 1;
                    }
                    case "main", "2" -> {
                        return 2;
                    }
                    case "showPlayers", "3" -> {
                        return 3;
                    }
                    case "CommonGoalCards", "4" -> {
                        return 4;
                    }
                    case "scoreboard", "5" -> {
                        return 5;
                    }
                    case "play", "6" ->{
                        if(yourTurn)
                            return 6;
                        else System.out.println("That is not a valid option! Enter a valid one");
                    }
                    default -> System.out.println("That is not a valid option! Enter a valid one");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setRenderPersonalGoalCards(List<PlayerUpdate> players){

        this.renderPersonalGoalCards = new HashMap<>();

        for (PlayerUpdate player : players) {
            this.renderPersonalGoalCards.put(player, render.renderPersonalGoalCard(player.personalGoalCard()));
        }
    }


    private void setRenderBookshelves(List<PlayerUpdate> players){

        this.renderBookshelves = new HashMap<>();

        for (PlayerUpdate player : players) {
            this.renderBookshelves.put(player, render.renderBookshelf(player.bookshelf()));
        }
    }

    public void updateBookshelf(PlayerUpdate player){
        this.renderBookshelves.put(player, render.renderBookshelf(player.bookshelf()));
    }

    public void setLivingRoom(LivingRoomUpdate livingRoom){
        this.renderLivingRoom = render.renderLivingRoom(livingRoom);
    }


    public void showPlayers(HashMap<PlayerUpdate, Integer> playerPoints){

        String concatBookshelves = "";

        List<PlayerUpdate> players = new ArrayList<>(playerPoints.keySet());

        for (PlayerUpdate player : players) {
            concatBookshelves = render.concatAsciiArt(concatBookshelves, this.renderBookshelves.get(player));
        }
        printer.print(concatBookshelves + "\n\n\n");

        for (PlayerUpdate player : players) {
            printer.print("- " + player.nickname() + " points: " + playerPoints.get(player));
        }
    }


    public void setRenderCommonGoalCards(List<CommonGoalCard> commonGoalCards){
        for (CommonGoalCard commonGoalCard : commonGoalCards) {
            this.renderCommonGoalCards.put(commonGoalCard, render.renderCommonGoalCard(commonGoalCard));
        }
    }

    public void showMain(PlayerUpdate currentPlayer){
        printer.clearScreen();
        String main = renderLivingRoom;
        main = render.concatAsciiArt(main, renderPersonalGoalCards.get(viewingPlayer));
        main = render.concatAsciiArt(main, renderBookshelves.get(viewingPlayer));
        printer.print(main);

        List<PlayerUpdate> players = new ArrayList<>(renderPersonalGoalCards.keySet());

        for (int i = 0; i < players.size(); i++) {
            String toPrint = i +". " + players.get(i).nickname();

            if(players.get(i).equals(currentPlayer)){
                toPrint = Color.RED.escape() + toPrint + Color.RESET;
            }

            printer.print(toPrint);
        }
    }

    public void showCommonGoalCards(){
        printer.clearScreen();

        List<String> renders = renderCommonGoalCards.values().stream().toList();

        for (int i = 0; i < renderCommonGoalCards.size(); i++) {
            printer.print("CommonGoalCard" + i);
            printer.print(renders.get(i));
        }
    }

    public void showScoreboard(HashMap<PlayerUpdate, Integer> playerPoints){
        List<PlayerUpdate> players = new ArrayList<>(playerPoints.keySet());
        printer.clearScreen();
        printer.print(Color.BLUE.escape() + "The current players' points are: " + Color.RESET);

        for (int i = 0; i < players.size(); i++) {
            String toPrint = i +". " + players.get(i).nickname() + " :" + playerPoints.get(players.get(i));

            printer.print(toPrint);
        }
    }

    public void menuChoice(HashMap<PlayerUpdate, Integer> playerPoints, PlayerUpdate currentPlayer, boolean yourTurn){
        printer.clearScreen();
        int menuChoice = showMenu(yourTurn);


        try {
                switch (menuChoice) {
                    case 1 -> this.menuChoice(playerPoints, currentPlayer, yourTurn);

                    case 2 -> this.showMain(currentPlayer);

                    case 3 -> this.showPlayers(playerPoints);

                    case 4 -> this.showCommonGoalCards();

                    case 5 -> this.showScoreboard(playerPoints);

                    case 6 -> controller.getTiles();

                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Coordinates getPlayerTileCoordinate(){

        Scanner scanner = new Scanner(System.in);
        this.showMain(this.viewingPlayer);

        printer.print("\n\nWhich tiles do you want to pick?");
        printer.print("Enter Rows:");

        int x = -1;
        int y = -1;

        while (x < 0 || x > 8) {
            x = Integer.parseInt(scanner.nextLine());
            if(x < 0 || x > 8) printer.print("Try again");
        }

        printer.print("Enter Columns:");

        while (y < 0 || y > 8) {
            y = Integer.parseInt(scanner.nextLine());
            if(y < 0 || y > 8) printer.print("Try again");
        }

        return new Coordinates(x, y);

    }


    public int getPlayerColumn(){

        Scanner scanner = new Scanner(System.in);

        this.showMain(this.viewingPlayer);

        printer.print("\n\nWhere you want to place those tiles in your bookshelf? ");

        int col = -1;

        while (col < 0 || col > 4) {
            col = Integer.parseInt(scanner.nextLine());
            if(col < 0 || col > 4) printer.print("\nWrong Column. Try again!");
        }

        return col;
    }


    public void updateAll(LivingRoomUpdate livingRoom, List<PlayerUpdate> players, PlayerUpdate currentPlayer, PlayerUpdate gameEnder){

        setLivingRoom(livingRoom);
        setRenderBookshelves(players);
        this.currentPlayer = currentPlayer;
        this.gameEnder = gameEnder;
        showPlayerTurn(currentPlayer);
    }

    public void showPlayerTurn(PlayerUpdate currentPlayer){
        printer.clearScreen();
        if(viewingPlayer != currentPlayer) {
            printer.print(currentPlayer + " is now playing his turn");
        }
        else{
            printer.print("Now is your turn");
        }
    }

    public int getNumberTiles(){
        printer.print("Choose your number of tile! (1-3)");
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine());
        while(num < 1 || num > 3){
            printer.print(Color.RED.escape() + "Wrong number of tiles! Try Again" + Color.RESET);
            num = Integer.parseInt(scanner.nextLine());
        }
        return num;
    }


}