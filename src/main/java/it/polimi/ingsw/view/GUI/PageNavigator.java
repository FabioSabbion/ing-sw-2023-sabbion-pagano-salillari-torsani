package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageNavigator {
    private Stage primaryStage;

    public PageNavigator(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void navigateToHomePage() {
        // Load and display the home page
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/welcome_page.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public void navigateToNumPlayersPage() {
        try {
            primaryStage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/fxml/choose_numplayers_view.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
