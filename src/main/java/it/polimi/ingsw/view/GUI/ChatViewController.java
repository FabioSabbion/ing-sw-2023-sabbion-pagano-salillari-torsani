package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

public class ChatViewController {
    @FXML
    TextField messageField;
    @FXML
    ChoiceBox<String> targetMenu;

    @FXML
    protected void onSendButtonPressed(ActionEvent event) {
        String text = messageField.getText();
        String to = targetMenu.getValue();
        GUI.sendMessage(to, text);
        messageField.setText("");
    }
}
