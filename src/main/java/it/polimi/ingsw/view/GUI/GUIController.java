package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.view.ViewController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class GUIController extends Application implements ViewController {
    @FXML
    private TextField nicknameTextField;
    Server server;
    ClientImpl client;


    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/welcome_page.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("MyShelfie");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void updatedPlayerList(List<String> players) {

    }

    @Override
    public void updateGame(GameUpdate update) {

    }

    @Override
    public void serverError(String message) {
        System.out.println(message);
    }

    @Override
    public void askNumPlayers() {

    }

    @Override
    public void setNickname(String nickname) {
        try {

            if (!nickname.matches("[A-Za-z0-9]+")) {
                System.out.println("Inadmissible choice of characters! Try Again...");
                return;
            }

            server.setNickname(nickname, client);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getPlayerChoice(boolean yourTurn, String menuChoice) {

    }

    @Override
    public void start(ClientImpl client, Server server) {
        this.client = client;
        this.server = server;

        client.run(this);

        launch();
    }

    @Override
    public void showEndingScreen() {

    }

    @FXML protected void playButtonPressed(ActionEvent event) {
        String input = nicknameTextField.getCharacters().toString();
        System.out.println(input);
    }

}