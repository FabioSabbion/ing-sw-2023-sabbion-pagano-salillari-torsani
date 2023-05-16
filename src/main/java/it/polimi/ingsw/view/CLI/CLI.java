package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.LivingRoom;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;

import java.util.*;

public class CLI {

    private CLIRenderer render;
    private CLIController controller;
    private static Printer printer = new Printer();
    private String renderPersonalGoalCard;
    private HashMap<String, String> renderBookshelves;
    private HashMap<CommonGoalCardUpdate, String> renderCommonGoalCards;
    private String renderLivingRoom;
    public PlayerUpdate viewingPlayer;
    private PlayerUpdate currentPlayer;
    private PlayerUpdate gameEnder;

    public CLI (LivingRoom livingRoom, List<PlayerUpdate> players, List<CommonGoalCardUpdate> commonGoalCards,
                PlayerUpdate currentPlayer, PlayerUpdate gameEnder, PlayerUpdate viewingPlayer, CLIController controller){

        this.render = new CLIRenderer();
        this.viewingPlayer = viewingPlayer;
        this.renderCommonGoalCards = new HashMap<>();
        this.renderBookshelves = new HashMap<>();
        this.controller = controller;

        setRenderPersonalGoalCard();
        setRenderBookshelves(players);
        setRenderCommonGoalCards(commonGoalCards);
        updateAll(livingRoom, players, currentPlayer, gameEnder);
    }


    public static void initialScreen() {
        printer.clearScreen();
        printer.print(Color.YELLOW.escape() + ASCIIArt.myShelfieLogo + Color.RESET);
        printer.print("\n\nWelcome to Myshelfie!");
    }

    public void showMenu(boolean yourTurn){
        String choices = """
                Menu:
                1. 'help'/'menu -> show all commands
                2. 'main' -> show the main view (LivingRoom, PersonalGoalCard, Bookshelf)
                3. 'showPlayers' -> shows players' bookshelf and respective points
                4. 'CommonGoalCards' -> outputs the game commonGoalCards
                5. 'scoreboard' -> view scoreboard""";

        if (yourTurn)
            choices += "\n6. 'play' -> 'play your turn";

        printer.print(Color.GREEN.escape() + choices + Color.RESET);
    }

    private void setRenderPersonalGoalCard(){
        renderPersonalGoalCard = render.renderPersonalGoalCard(viewingPlayer.personalGoalCard());
    }


    private void setRenderBookshelves(List<PlayerUpdate> players){
        for (PlayerUpdate player : players) {
            this.renderBookshelves.put(player.nickname(), render.renderBookshelf(player.bookshelf()));
        }
    }

    public void updateBookshelf(PlayerUpdate player){
        this.renderBookshelves.put(player.nickname(), render.renderBookshelf(player.bookshelf()));
    }

    public void setLivingRoom(LivingRoom livingRoom){
        this.renderLivingRoom = render.renderLivingRoom(livingRoom);
    }


    public void showPlayers(Map<String, PlayerUpdate> playerMap){

        String concatBookshelves = "";

        List<String> players = new ArrayList<>(playerMap.keySet());

        for (String player : players) {
            concatBookshelves = render.concatAsciiArt(concatBookshelves, this.renderBookshelves.get(player));
        }
        printer.print(concatBookshelves + "\n\n\n");

        for (String player : players) {
            if (player.equals(viewingPlayer.nickname())){
                printer.print("- You ");
            }
            else {
                printer.print("- " + player);
            }
        }
    }


    public void setRenderCommonGoalCards(List<CommonGoalCardUpdate> commonGoalCards){
        for (var commonGoalCard : commonGoalCards) {
            this.renderCommonGoalCards.put(commonGoalCard, render.renderCommonGoalCard(commonGoalCard));
        }
    }

    public void showMain(PlayerUpdate currentPlayer){
        printer.clearScreen();
        String main = renderLivingRoom;
        main = render.concatAsciiArt(main, renderPersonalGoalCard);
        main = render.concatAsciiArt(main, renderBookshelves.get(viewingPlayer.nickname()));
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

    public void showCommonGoalCards(){
        printer.clearScreen();

        List<String> renders = renderCommonGoalCards.values().stream().toList();

        for (int i = 0; i < renderCommonGoalCards.size(); i++) {
            printer.print("CommonGoalCard" + i);
            printer.print("\n\n");
            printer.print(renders.get(i));
        }
    }

    public void showScoreboard(){
        List<String> players = new ArrayList<>(controller.players.keySet());
        printer.clearScreen();
        printer.print(Color.BLUE.escape() + ASCIIArt.scoreBoard + Color.RESET);
        var commonGoalCards = controller.commonGoalCards;

        for (int i = 0; i < commonGoalCards.size(); i++) {
            printer.print(Color.BLUE.escape() + "The current players' CommonGoalCard " + i + " points are: " + Color.RESET);
            var playerPoints = controller.calculateCommonGoalCardPoints(i);

            for (int j = 0; j < players.size(); j++) {
                String toPrint = j +". " + players.get(j) + " : " + playerPoints.get(players.get(j));
                printer.print(toPrint);
            }
        }

        int yourPersonalGoalCardPoint = controller.calculatePersonalGoalCardPoints();

        printer.print(Color.BLUE.escape() + "Your PersonalGoalCard points:" + Color.RESET);
        printer.print(String.valueOf(yourPersonalGoalCardPoint));
    }


    public void updateAll(LivingRoom livingRoom, List<PlayerUpdate> players, PlayerUpdate currentPlayer, PlayerUpdate gameEnder){

        setLivingRoom(livingRoom);
        setRenderBookshelves(players);
        this.currentPlayer = currentPlayer;
        this.gameEnder = gameEnder;
        showPlayerTurn(currentPlayer);
    }

    public void showPlayerTurn(PlayerUpdate currentPlayer){
        printer.clearScreen();
        boolean yourTurn = viewingPlayer.nickname().equals(currentPlayer.nickname());
        if(!yourTurn) {
            printer.print(Color.BLUE.escape() + currentPlayer.nickname() + " is now playing his turn" + Color.RESET);
        }
        else{
            printer.print(Color.BLUE.escape() + "Now is your turn" + Color.RESET);
        }
        this.showMenu(yourTurn);
    }




}