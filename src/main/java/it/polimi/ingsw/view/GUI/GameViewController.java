package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameViewController {
    @FXML
    GridPane livingRoomGrid;
    @FXML
    GridPane bookshelfGrid;
    @FXML
    Button player1Button;
    @FXML
    Button player2Button;
    @FXML
    Button player3Button;

    @FXML
    protected void onPlayer1BtnPressed(ActionEvent event) {
        GUI.openPlayerWindow(player1Button.getText());
    }
    @FXML
    protected void onPlayer2BtnPressed(ActionEvent event) {
        GUI.openPlayerWindow(player2Button.getText());
    }
    @FXML
    protected void onPlayer3BtnPressed(ActionEvent event) {
        GUI.openPlayerWindow(player3Button.getText());
    }
    @FXML
    protected void onC1ButtonPressed(ActionEvent event) {
        GUI.selectColumn(0);
    }
    @FXML
    protected void onC2ButtonPressed(ActionEvent event) {
        GUI.selectColumn(1);
    }
    @FXML
    protected void onC3ButtonPressed(ActionEvent event) {
        GUI.selectColumn(2);
    }
    @FXML
    protected void onC4ButtonPressed(ActionEvent event) {
        GUI.selectColumn(3);
    }
    @FXML
    protected void onC5ButtonPressed(ActionEvent event) {
        GUI.selectColumn(4);
    }

}
