package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.Server;
import it.polimi.ingsw.distributed.networking.ServerStub;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class GUI extends Application {
    static private GUIController guiController;
    static private PageNavigator navigator;

    static public void setNickname(String nickname) {
        guiController.setNickname(nickname);
    }

    static public void showAskNumPlayersPage(){
        navigator.navigateToNumPlayersPage();
    }

    static public void setNumPlayers(int numPlayers) {
        guiController.setNumPlayers(numPlayers);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI.navigator = new PageNavigator(primaryStage);
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

