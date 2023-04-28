package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.models.Coordinates;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.models.Player;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private CLIRenderer render;
    private Printer printer;
    private HashMap<Player, String> renderPersonalGoalCards;
    private HashMap<Player, String> renderBookshelves;
    private String renderLivingRoom;
    private Player viewingPlayer;
    private String renderCommonGoalCards;

    public CLI(List<Player> players, LivingRoom livingRoom, Player viewingPlayer, int[] commonGoalCardsValues) {
        this.render = new CLIRenderer();
        this.printer = new Printer();
        setRenderPersonalGoalCards(players);
        setRenderBookshelves(players);
        setLivingRoom(livingRoom);
        this.viewingPlayer = viewingPlayer;
        setRenderCommonGoalCards(commonGoalCardsValues);
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

    public int showMenu(){
        String choices = """
                Menu:
                1. 'help'/'menu -> show all commands
                2. 'main' -> show the main view (LivingRoom, PersonalGoalCard, Bookshelf)
                3. 'showPlayers' -> shows players' bookshelf and respective points
                4. 'CommonGoalCards' -> outputs the game commonGoalCards
                5. 'scoreboard' -> view scoreboard""";
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
                    default -> System.out.println("That is not a valid option! Enter a valid one");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setRenderPersonalGoalCards(List<Player> players){

        this.renderPersonalGoalCards = new HashMap<>();

        for (Player player : players) {
            this.renderPersonalGoalCards.put(player, render.renderPersonalGoalCard(player.getPersonalGoalCard()));
        }
    }


    private void setRenderBookshelves(List<Player> players){

        this.renderBookshelves = new HashMap<>();

        for (Player player : players) {
            this.renderBookshelves.put(player, render.renderBookshelf(player.getBookshelf()));
        }
    }

    public void updateBookshelf(Player player){
        this.renderBookshelves.put(player, render.renderBookshelf(player.getBookshelf()));
    }

    public void setLivingRoom(LivingRoom livingRoom){
        this.renderLivingRoom = render.renderLivingRoom(livingRoom);
    }


    public void showPlayers(HashMap<Player, Integer> playerPoints){

        String concatBookshelves = "";

        List<Player> players = new ArrayList<>(playerPoints.keySet());

        for (Player player : players) {
            concatBookshelves = render.concatAsciiArt(concatBookshelves, this.renderBookshelves.get(player));
        }
        printer.print(concatBookshelves + "\n\n\n");

        for (Player player : players) {
            printer.print("- " + player.getNickname() + " points: " + playerPoints.get(player));
        }
    }


    public void setRenderCommonGoalCards(int[] CommonGoalCardsValues){
        this.renderCommonGoalCards = "CommonGoalCard1: \n\n" + render.renderCommonGoalCard(CommonGoalCardsValues[0]) +
                "\n\n\n CommonGoalCard2: \n\n" + render.renderCommonGoalCard(CommonGoalCardsValues[1]);
    }

    public void showMain(Player currentPlayer){
        String main = renderLivingRoom;
        main = render.concatAsciiArt(main, renderPersonalGoalCards.get(viewingPlayer));
        main = render.concatAsciiArt(main, renderBookshelves.get(viewingPlayer));
        printer.print(main);

        List<Player> players = new ArrayList<>(renderPersonalGoalCards.keySet());

        for (int i = 0; i < players.size(); i++) {
            String toPrint = i +". " + players.get(i).getNickname();

            if(players.get(i).equals(currentPlayer)){
                toPrint = Color.RED.escape() + toPrint + Color.RESET;
            }

            printer.print(toPrint);
        }
    }

    public void showCommonGoalCards(){
        printer.print(this.renderCommonGoalCards);
    }

    public void showScoreboard(HashMap<Player, Integer> playerPoints){
        List<Player> players = new ArrayList<>(playerPoints.keySet());

        printer.print(Color.BLUE.escape() + "The current players' points are: " + Color.RESET);

        for (int i = 0; i < players.size(); i++) {
            String toPrint = i +". " + players.get(i).getNickname() + " :" + playerPoints.get(players.get(i));

            printer.print(toPrint);
        }
    }

    public void menuChoice(HashMap<Player, Integer> playerPoints, Player currentPlayer){
        int menuChoice = showMenu();


        try {
                switch (menuChoice) {
                    case 1 -> this.menuChoice(playerPoints, currentPlayer);

                    case 2 -> this.showMain(currentPlayer);

                    case 3 -> this.showPlayers(playerPoints);

                    case 4 -> this.showCommonGoalCards();

                    case 5 -> this.showScoreboard(playerPoints);

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

        printer.print("\n\nWhere you want to place those tiles in your bookshelf? ");

        int col = -1;

        while (col < 0 || col > 4) {
            col = Integer.parseInt(scanner.nextLine());
            if(col < 0 || col > 4) printer.print("\nWrong Column. Try again!");
        }

        return col;
    }


    public void updateAll(LivingRoom livingRoom, Player currentPlayer){
        setLivingRoom(livingRoom);
        updateBookshelf(currentPlayer);
    }

}