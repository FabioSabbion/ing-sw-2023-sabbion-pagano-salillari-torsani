package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.AppClientRMI;
import it.polimi.ingsw.AppClientSocket;
import it.polimi.ingsw.distributed.CommonGoalCardUpdate;
import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.PersonalGoalCardUpdate;
import it.polimi.ingsw.distributed.PlayerUpdate;
import it.polimi.ingsw.models.Bookshelf;
import it.polimi.ingsw.models.CommonGoalCard;
import it.polimi.ingsw.models.Tile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GUI extends Application {
    static private GUIController guiController;
    static private Stage primaryStage;
    static private boolean checkFirstPlayer = false;


    static public void setNickname(String nickname) {
        guiController.setNickname(nickname);
    }

    static public void showNumPlayersView(){
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/choose_numplayers_view.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void showLobbyView(List<String> players){
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/lobby_page.fxml")));
            updateLobby(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void updateLobby(List<String> players) {
        Text text = (Text) primaryStage.getScene().lookup("#playersText");
        String s = "";
        for (String p: players) {
            s = s + p + "\n";
        }
        text.setText(s);
    }

    static public void showGameView() {
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/game_view.fxml")));
            primaryStage.setWidth(1400.0);
            primaryStage.setHeight(800.0);
            primaryStage.setX(0);
            primaryStage.setY(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void updateGameView(GameUpdate gameUpdate) {
        System.out.println("UPDATE GUI");
        int cgc1 = gameUpdate.commonGoalCards().get(0).commonGoalCardID();
        int cgc2 = gameUpdate.commonGoalCards().get(1).commonGoalCardID();
        PersonalGoalCardUpdate personalGoalCardUpdate = gameUpdate.players().stream().filter(playerUpdate -> playerUpdate.nickname().equals(guiController.getMyNickname())).toList().get(0).personalGoalCard();

        ImageView commonGoalCardImage1 = (ImageView) primaryStage.getScene().lookup("#commonGoalCardImage1");
        ImageView commonGoalCardImage2 = (ImageView) primaryStage.getScene().lookup("#commonGoalCardImage2");
        ImageView personalGoalCardImage = (ImageView) primaryStage.getScene().lookup("#personalGoalCardImage");

        commonGoalCardImage1.setImage(new Image("/images/common_goal_cards/"+ (cgc1+1) +".jpg"));
        commonGoalCardImage2.setImage(new Image("/images/common_goal_cards/"+ (cgc2+1) +".jpg"));
        //personalGoalCardImage.setImage(new Image("/images/personal_goal_cards/"++".jpg"))

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

        // Update Living Room
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

        // Update Bookshelf
        GridPane bookshelfGrid = (GridPane) primaryStage.getScene().lookup("#bookshelfGrid");
        Bookshelf myBookshelf = gameUpdate.players().stream()
                .filter(player -> player.nickname().equals(guiController.getMyNickname()))
                .findFirst().orElseThrow().bookshelf();
        updateBookshelf(myBookshelf, bookshelfGrid, 65);

        // Update tokens
        HBox tokenRow = (HBox) primaryStage.getScene().lookup("#tokenRow");
        updateTokens(tokenRow, guiController.getMyNickname());

    }

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
                    gridPane.add(imageView, finalJ, Bookshelf.ROWS - finalI);
                });
            }
        }
    }

    static public void selectColumn(int c) {
        guiController.chooseColumn(c);
    }

    static public void setNumPlayers(int numPlayers) {
        guiController.setNumPlayers(numPlayers);
        showLobbyView(List.of(guiController.getMyNickname()));
    }

    static public void showToast(String message) {
        Platform.runLater(() -> {
            Toast.makeText(primaryStage, message, 4000, 200, 200);
        });
    }

    static public void openPlayerWindow(String nickname) {
        PlayerUpdate playerUpdate = guiController.getGameUpdate().players().stream().filter(p -> nickname.equals(p.nickname())).findFirst().orElseThrow();

        Stage newStage = new Stage();
        newStage.setTitle(nickname + "'s bookshelf");
        try {
            Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/player_view.fxml"));
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.show();
            GridPane bookshelfGrid = (GridPane) newStage.getScene().lookup("#bookshelfGrid");
            updateBookshelf(playerUpdate.bookshelf(), bookshelfGrid, 50);

            HBox tokenRow = (HBox) newStage.getScene().lookup("#tokenRow");
            updateTokens(tokenRow, nickname);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/welcome_page.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("MyShelfie");
        primaryStage.getIcons().add(new Image("/images/publisher/Icon 50x50px.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    @Override
    public void init() throws Exception {
        super.init();
        String connectionType = getParameters().getRaw().get(0);
        guiController = new GUIController();

        if (connectionType.equals("socket")) {
            AppClientSocket.start(guiController);
        } else {
            AppClientRMI.start(guiController);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}

