package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class NumPlayersController {
    @FXML
    protected void numPlayer2(ActionEvent event) {
        GUI.setNumPlayers(2);
    }
    @FXML
    protected void numPlayer3(ActionEvent event) {
        GUI.setNumPlayers(3);
    }
    @FXML
    protected void numPlayer4(ActionEvent event) {
        GUI.setNumPlayers(4);
    }
}
