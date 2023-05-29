package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.distributed.networking.ServerStub;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
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
        ServerStub serverStub = new ServerStub("localhost", 4445);
        ClientImpl client = new ClientImpl();
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        serverStub.receive(client);
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from server. Stopping...");
                        try {
                            serverStub.close();
                        } catch (RemoteException ex) {
                            System.err.println("Cannot close connection with server. Halting...");
                        }
                        System.exit(1);
                    }
                }
            }
        }.start();

        guiController = new GUIController();
        guiController.start(client, serverStub);
    }

    public static void main(String[] args) {
        launch();
    }
}

