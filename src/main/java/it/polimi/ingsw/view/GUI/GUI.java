package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.AppClientRMI;
import it.polimi.ingsw.AppClientSocket;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Message;
import it.polimi.ingsw.models.Tile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * main class that dinamically builds and orchestrates the GUI using javafx
 */
public class GUI extends Application {
    static private GUIController guiController;
    static private Stage primaryStage;
    static private boolean checkFirstTime = false;
    static private Stage chatStage;
    static private int screenWidth;
    static private int screenHeight;
    static final double ASPECT_RATIO = 1400.0 / 800.0;

    /**
     * sets a player's nickname by calling the guiController
     * @param nickname the nickname of the user
     */
    static public void setNickname(String nickname) {
        guiController.setNickname(nickname);
    }

    /**
     * shows the choose_numplayers_view.fxml window that allows to choose the number of players
     */
    static public void showNumPlayersView(){
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/choose_numplayers_view.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * shows the lobby window, lobby_page.fxml, with a list of the names of the connected players
     * @param players the list of the players in the game
     */
    static public void showLobbyView(List<String> players){
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/lobby_page.fxml")));
            updateLobby(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * updates the list of players in the lobby
     * @param players the list of the players in the lobby
     */
    static public void updateLobby(List<String> players) {
        Text text = (Text) primaryStage.getScene().lookup("#playersText");
        String s = "";
        for (String p: players) {
            s = s + p + "\n";
        }
        text.setText(s);
    }

    /**
     * shows the game_view.fxml window to play the game, and determines its width and height
     */
    static public void showGameView() {

        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/game_view.fxml")));

            int width = (int) (screenHeight * ASPECT_RATIO * 0.95);
            int height = (int) (screenHeight * 0.95);

            double factor = height / 800.0;

            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
            StackPane stackPane = (StackPane) primaryStage.getScene().lookup("#stackPane");
            stackPane.setScaleX(factor);
            stackPane.setScaleY(factor);

            primaryStage.setX(0);
            primaryStage.setY(0);

            // Platform.runLater(() -> {
            //     primaryStage.setResizable(true);
            // });
            // primaryStage.widthProperty().addListener(new ResizeListener(primaryStage));
            // primaryStage.heightProperty().addListener(new ResizeListener(primaryStage));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * shows the end_view.fxml window, that shows the points of all the players and the winner
     * @param playerPoints a map associating a player's nickname to its points
     * @param winner a string containing the winner's nickname
     */
    static public void showScoreboardView(Map<String, Integer> playerPoints, String winner) {
        Platform.runLater(() -> {
            try {
                Stage scoreboardStage = new Stage();

                Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/end_view.fxml"));
                Scene scene = new Scene(root);
                scoreboardStage.setScene(scene);
                scoreboardStage.setResizable(false);
                scoreboardStage.setWidth(900.0);
                scoreboardStage.setHeight(800.0);
                scoreboardStage.setTitle("Scoreboard");
                scoreboardStage.getIcons().add(new Image("/images/publisher/Icon 50x50px.png"));
                scoreboardStage.setOnCloseRequest((value) -> {
                    primaryStage.close();
                    System.exit(0);
                });

                scoreboardStage.show();

                Text winnerText = (Text) scoreboardStage.getScene().lookup("#winnerText");
                winnerText.setText(winner.equals(guiController.getMyNickname()) ? "You won the game!" : winner + " has won the game");
                GridPane scoreTable = (GridPane) scoreboardStage.getScene().lookup("#scoreTable");
                int row = 0;
                for (var entry : playerPoints.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).toList()) {
                    Text text1 = new Text();
                    Text text2 = new Text();

                    text1.setText(entry.getKey());
                    text2.setText(entry.getValue().toString());

                    text1.setFont(Font.font(null, FontWeight.BOLD, 36.0));
                    text1.setFill(Color.BLACK);
                    // text1.setStroke(Color.BLACK);
                    // text1.setStrokeWidth(1.5);
                    text2.setFont(Font.font(null, FontWeight.BOLD, 36.0));
                    text2.setFill(Color.BLACK);
                    // text2.setStroke(Color.BLACK);
                    // text2.setStrokeWidth(1.5);

                    scoreTable.add(text1, 0, row);
                    scoreTable.add(text2, 1, row);

                    row++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * updates the game_view.fxml window, following an action from any of the players
     * @param gameUpdate a GameUpdate object containing the updated game params
     * @param toRefresh a list of GuiParts to refresh
     */
    static public void updateGameView(GameUpdate gameUpdate, List<GuiParts> toRefresh) {
        if (!checkFirstTime) {
            int cgc1 = gameUpdate.commonGoalCards().get(0).commonGoalCardID();
            int cgc2 = gameUpdate.commonGoalCards().get(1).commonGoalCardID();
            String pgc = gameUpdate.players().stream().filter(p -> p.nickname().equals(guiController.getMyNickname())).findFirst().orElseThrow().personalGoalCard().ID();

            ImageView commonGoalCardImage1 = (ImageView) primaryStage.getScene().lookup("#commonGoalCardImage1");
            ImageView commonGoalCardImage2 = (ImageView) primaryStage.getScene().lookup("#commonGoalCardImage2");
            ImageView personalGoalCardImage = (ImageView) primaryStage.getScene().lookup("#personalGoalCardImage");

            commonGoalCardImage1.setImage(new Image("/images/common_goal_cards/"+ (cgc1+1) +".jpg"));
            commonGoalCardImage2.setImage(new Image("/images/common_goal_cards/"+ (cgc2+1) +".jpg"));
            personalGoalCardImage.setImage(new Image("/images/personal_goal_cards/Personal_Goals"+pgc.charAt(3)+".png"));

            // Update players buttons
            List<Button> playerButtons = new ArrayList<>();
            playerButtons.add((Button) primaryStage.getScene().lookup("#player1Button"));
            playerButtons.add((Button) primaryStage.getScene().lookup("#player2Button"));
            playerButtons.add((Button) primaryStage.getScene().lookup("#player3Button"));
            List<PlayerUpdate> playerUpdates = new ArrayList<>(gameUpdate.players());
            playerUpdates.removeIf(p -> p.nickname().equals(guiController.getMyNickname()));
            int c;
            for (c = 0; c < playerUpdates.size(); c++) {
                int finalC = c;
                Platform.runLater(() -> {
                    playerButtons.get(finalC).setText(playerUpdates.get(finalC).nickname());
                });
            }
            for (int j = c; j < 3; j++) {
                int finalJ = j;
                Platform.runLater(() -> {
                    playerButtons.get(finalJ).setVisible(false);
                });
            }

            checkFirstTime = true;
        }

        // Update Living Room
        if (toRefresh == null || toRefresh.contains(GuiParts.LIVING_ROOM)) {
            GridPane livingRoomGrid = (GridPane) primaryStage.getScene().lookup("#livingRoomGrid");

            Platform.runLater(() -> {
                livingRoomGrid.getChildren().clear();
            });


            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int finalI = i;
                    int finalJ = j;
                    Tile t = gameUpdate.livingRoom().getBoard()[j][i];

                    ImageView imageView = new ImageView();
                    if (t != null) {
                        imageView.setImage(new Image("/images/item_tiles/" + t.category().toString() + "_" + t.icon().toString() + ".png"));
                    } else {
                        imageView.setImage(new Image("/images/item_tiles/empty_tile.png"));
                    }

                    imageView.setFitWidth(55);
                    imageView.setFitHeight(55);

                    Rectangle border = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
                    border.setFill(Color.TRANSPARENT);
                    border.setStrokeWidth(2.0);
                    // Wrap the ImageView and Rectangle inside a StackPane
                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().addAll(imageView, border);

                    if (t != null) {
                        stackPane.setOnMouseClicked(value -> {
                            if (guiController.isMyTurn()) {
                                if (border.getStroke() == null && guiController.pickTile(finalJ, finalI)) {
                                    border.setStroke(Color.YELLOW);
                                } else {
                                    guiController.depositTile(finalJ, finalI);
                                    border.setStroke(null);
                                }
                            }
                        });
                    }

                    Platform.runLater(() -> {
                        livingRoomGrid.add(stackPane, finalI, finalJ);
                    });
                }
            }
        }

        if (toRefresh == null || toRefresh.contains(GuiParts.BOOKSHELF)) {
            // Update Bookshelf
            GridPane bookshelfGrid = (GridPane) primaryStage.getScene().lookup("#bookshelfGrid");
            Bookshelf myBookshelf = gameUpdate.players().stream()
                    .filter(player -> player.nickname().equals(guiController.getMyNickname()))
                    .findFirst().orElseThrow().bookshelf();
            updateBookshelf(myBookshelf, bookshelfGrid, 65);
        }

        // Update tokens
        HBox tokenRow = (HBox) primaryStage.getScene().lookup("#tokenRow");
        updateTokens(tokenRow, guiController.getMyNickname());

        // Update Info Box
        Text infoText = (Text) primaryStage.getScene().lookup("#infoText");
        if (gameUpdate.currentPlayer().nickname().equals(guiController.getMyNickname())) {
            infoText.setText("Now is your turn");
        } else {
            infoText.setText(gameUpdate.currentPlayer().nickname() + " is now playing his turn");
        }

        // Update Personal Goal Card points
        Text personalPointsText = (Text) primaryStage.getScene().lookup("#personalPointsText");
        int points = guiController.getGameUpdate().players().stream().filter(p -> p.nickname().equals(guiController.getMyNickname())).findFirst().orElseThrow().personalGoalCard().point();
        personalPointsText.setText(String.valueOf(points));

    }

    /**
     * updates the tokens images in the game_view.fxml window, following various actions throughout the game
     * @param tokenRow an HBox object containing the token images
     * @param nickname the nickname of the current player
     */
    static private void updateTokens(HBox tokenRow, String nickname) {
        Platform.runLater(() -> tokenRow.getChildren().clear());

        // End game token
        if (guiController.getGameUpdate().gameEnder() != null && guiController.getGameUpdate().gameEnder().nickname().equals(nickname)) {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image("/images/tokens/end game.jpg"));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            Platform.runLater(() -> {
                tokenRow.getChildren().add(imageView);
            });
        }
        // Scoring tokens
        for (CommonGoalCardUpdate cgcu : guiController.getGameUpdate().commonGoalCards()) {
            int index = cgcu.playerUpdateList().indexOf(nickname);
            if (index != -1) {
                int p = CommonGoalCard.points[guiController.getGameUpdate().players().size()][index];
                ImageView imageView = new ImageView();
                imageView.setImage(new Image("/images/tokens/scoring_" + p + ".jpg"));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                Platform.runLater(() -> {
                    tokenRow.getChildren().add(imageView);
                });
            }
        }
        // First player chair
        if (guiController.getGameUpdate().players().get(0).nickname().equals(nickname)) {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image("/images/misc/firstplayertoken.png"));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            Platform.runLater(() -> {
                tokenRow.getChildren().add(imageView);
            });
        }
    }

    /**
     * updates the player's bookshelf after an action
     * @param bookshelf the current player's Bookshelf
     * @param gridPane a GridPane object
     * @param size the size of the imageView
     */
    static private void updateBookshelf(Bookshelf bookshelf, GridPane gridPane, int size) {
        for (int i = 0; i < Bookshelf.ROWS; i++) {
            for (int j = 0; j < Bookshelf.COLUMNS; j++) {
                Tile t = bookshelf.getBookshelf()[i][j];
                if (t == null) continue;
                ImageView imageView = new ImageView();
                imageView.setImage(new Image("/images/item_tiles/" + t.category().toString() + "_" + t.icon().toString() + ".png"));
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                int finalI = i;
                int finalJ = j;
                Platform.runLater(() -> {
                    gridPane.add(imageView, finalJ, Bookshelf.ROWS - finalI - 1);
                });
            }
        }
    }

    /**
     * sends the selected column to the guiController
     * @param c the column index
     */
    static public void selectColumn(int c) {
        guiController.chooseColumn(c);
    }

    /**
     * sends the number of players to the guiController
     * @param numPlayers the number of players
     */
    static public void setNumPlayers(int numPlayers) {
        guiController.setNumPlayers(numPlayers);
        showLobbyView(List.of(guiController.getMyNickname()));
    }

    /**
     * shows the warning messages as Toast
     * @param message a string containing the message to show
     */
    static public void showToast(String message) {
        Platform.runLater(() -> {
            Toast.makeText(primaryStage, message, 3000, 200, 200);
        });
    }

    /**
     * opens the player_view.fxml windows, allowing to see the other player's bookshelves
     * @param nickname the nickname of the player
     */
    static public void openPlayerWindow(String nickname) {
        PlayerUpdate playerUpdate = guiController.getGameUpdate().players().stream().filter(p -> nickname.equals(p.nickname())).findFirst().orElseThrow();

        Stage newStage = new Stage();
        newStage.setTitle(nickname + "'s bookshelf");
        try {
            Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/player_view.fxml"));
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.getIcons().add(new Image("/images/publisher/Icon 50x50px.png"));
            newStage.show();
            GridPane bookshelfGrid = (GridPane) newStage.getScene().lookup("#bookshelfGrid");
            updateBookshelf(playerUpdate.bookshelf(), bookshelfGrid, 50);

            HBox tokenRow = (HBox) newStage.getScene().lookup("#tokenRow");
            updateTokens(tokenRow, nickname);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * sends a chat message to the guiController
     * @param to the nickname of the recipient
     * @param text the text of the message to send
     */
    static public void sendMessage(String to, String text) {
        guiController.sendMessage(to, text);
    }

    /**
     * adds the message history to the MessageHistory TextArea inside the chat_viwe.fxml
     * @param messages a list of messages to show
     */
    static public void addMessages(List<Message> messages) {
        if (chatStage == null) return;

        TextArea messagesTextArea = (TextArea) chatStage.getScene().lookup("#MessageHistory");
        StringBuilder stringBuilder = new StringBuilder();
        for (Message m : messages) {
            stringBuilder.append("[").append(m.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("]").append(m.to() != null ? (m.to().equals(guiController.getMyNickname()) ? " " : " (" + m.to() + ") ") : " (Everyone) ").append(m.from().equals(guiController.getMyNickname()) ? "You" : m.from()).append(": ").append(m.message()).append("\n");
        }

        messagesTextArea.appendText(stringBuilder.toString());
    }

    /**
     * opens the chat_view.fxml, allowing a player to see the previous messages and to send a private message or a
     * broadcast message to everyone
     */
    static public void openChatWindow() {
        if (chatStage != null) return;

        Stage newStage = new Stage();
        chatStage = newStage;
        newStage.setTitle("Chat");
        newStage.setResizable(false);
        newStage.getIcons().add(new Image("/images/publisher/Icon 50x50px.png"));
        try {
            Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/chat_view.fxml"));
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.setOnCloseRequest(value -> {
                chatStage = null;
            });
            newStage.show();
            TextArea messagesTextArea = (TextArea) chatStage.getScene().lookup("#MessageHistory");
            StringBuilder stringBuilder = new StringBuilder();
            for (Message m : guiController.getMessages()) {
                stringBuilder.append("[").append(m.timestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("]").append(m.to() != null ? (m.to().equals(guiController.getMyNickname()) ? " " : " (" + m.to() + ") ") : " (Everyone) ").append(m.from().equals(guiController.getMyNickname()) ? "You" : m.from()).append(": ").append(m.message()).append("\n");
            }

            messagesTextArea.setText(stringBuilder.toString());
            ChoiceBox<String> choiceBox = (ChoiceBox<String>) chatStage.getScene().lookup("#targetMenu");
            List<String> playerList = new ArrayList<>(guiController.getGameUpdate().players().stream().map(PlayerUpdate::nickname).filter(nickname -> !nickname.equals(guiController.getMyNickname())).toList());
            playerList.add(0, "Everyone");
            ObservableList<String> options = FXCollections.observableArrayList(playerList);
            choiceBox.setItems(options);
            choiceBox.setValue("Everyone");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * opens the welcome_page.fxml window, initializing the primaryStage and effectively starting the game
     * @param primaryStage the stage on which the game is being played on
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/welcome_page.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("MyShelfie");
        primaryStage.getIcons().add(new Image("/images/publisher/Icon 50x50px.png"));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        screenHeight = (int) Screen.getPrimary().getBounds().getHeight();
    }

    /**
     * initializes the connection and creates a new guiController
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        super.init();
        String connectionType = getParameters().getRaw().get(0);
        String IP = getParameters().getRaw().get(1);
        guiController = new GUIController();

        if (connectionType.equals("socket")) {
            AppClientSocket.start(guiController, IP);
        } else {
            AppClientRMI.start(guiController, IP);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * sets the scaling factor in order to adapt the game_view.fxml window to the user's screen
     * @param stage
     */
    static private void setScalingFactor(Stage stage) {
        double widthFactor = stage.getWidth() / 1400.0;
        double heightFactor = stage.getHeight() / 800.0;

        double factor = Math.min(widthFactor, heightFactor);


        StackPane stackPane = (StackPane) stage.getScene().lookup("#stackPane");
        stackPane.setScaleX(factor);
        stackPane.setScaleY(factor);

        //stage.setRenderScaleX(factor);
        //stage.setRenderScaleY(factor);

    }

    static private class ResizeListener implements ChangeListener<Number> {

        private final Stage stage;

        public ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            setScalingFactor(stage);
        }
    }
}

