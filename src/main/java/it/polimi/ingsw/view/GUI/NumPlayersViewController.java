package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * controller for the choose_numplayers_view.fxml that defines the responses to the various events
 */
public class NumPlayersViewController {
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
