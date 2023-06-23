package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
/**
 * controller for the welcome_page.fxml that defines the responses to the playButtonPressed event
 */
public class WelcomeViewController {
    @FXML private TextField nicknameTextField;

    @FXML
    protected void playButtonPressed(ActionEvent event) {
        String input = nicknameTextField.getCharacters().toString();
        System.out.println(input); // DEBUG
        GUI.setNickname(input);
    }

}
