package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.AppClientRMI;
import it.polimi.ingsw.AppClientSocket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GUI extends Application {
    static private GUIController guiController;
    static private Stage primaryStage;
    static private Page currentPage;

    static public void setNickname(String nickname) {
        guiController.setNickname(nickname);
    }

    static public void showAskNumPlayersPage(){
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/choose_numplayers_view.fxml")));
            currentPage = Page.ASKNUMPLAYERS;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void showLobbyPage(List<String> players){
        try {
            if (currentPage != Page.LOBBY) {
                primaryStage.getScene().setRoot(FXMLLoader.load(GUI.class.getResource("/fxml/lobby_page.fxml")));
                currentPage = Page.LOBBY;
            }
            Text text = (Text) primaryStage.getScene().lookup("#playersText");
            String s = "";
            for (String p: players) {
                s = s + p + "\n";
            }
            text.setText(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void setNumPlayers(int numPlayers) {
        guiController.setNumPlayers(numPlayers);
        showLobbyPage(List.of(guiController.getMyNickname()));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI.primaryStage = primaryStage;
        currentPage = Page.WELCOME;
        Parent root = FXMLLoader.load(GUI.class.getResource("/fxml/welcome_page.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("MyShelfie");
        primaryStage.setScene(scene);
        primaryStage.show();
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

