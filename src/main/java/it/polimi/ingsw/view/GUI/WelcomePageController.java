package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class WelcomePageController {
    @FXML private TextField nicknameTextField;

    @FXML
    protected void playButtonPressed(ActionEvent event) {
        String input = nicknameTextField.getCharacters().toString();
        System.out.println(input); // DEBUG
        GUI.setNickname(input);
    }

}
