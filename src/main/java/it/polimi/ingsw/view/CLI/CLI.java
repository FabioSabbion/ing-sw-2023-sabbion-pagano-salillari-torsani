package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.distributed.LivingRoomUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.view.CLI.utils.ASCIIArt;
import it.polimi.ingsw.view.CLI.utils.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CLI {

    private CLIRenderer render;
    private CLIController controller;
    private static Printer printer = new Printer();
    private HashMap<PlayerUpdate, String> renderPersonalGoalCards;
    private HashMap<PlayerUpdate, String> renderBookshelves;
    private HashMap<CommonGoalCard, String> renderCommonGoalCards;
    private String renderLivingRoom;
    public PlayerUpdate viewingPlayer;
    private PlayerUpdate currentPlayer;
    private PlayerUpdate gameEnder;

    public CLI (LivingRoomUpdate livingRoom, List<PlayerUpdate> players, List<CommonGoalCard> commonGoalCards,
                PlayerUpdate currentPlayer, PlayerUpdate gameEnder, PlayerUpdate viewingPlayer){

        this.render = new CLIRenderer();
        this.viewingPlayer = viewingPlayer;

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




}