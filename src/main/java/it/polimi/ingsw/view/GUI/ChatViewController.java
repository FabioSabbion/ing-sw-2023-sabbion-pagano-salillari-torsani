package it.polimi.ingsw.view.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

/**
 * Controller that sends chat messages to the GUI
 */

public class ChatViewController {
    @FXML
    TextField messageField;
    @FXML
    ChoiceBox<String> targetMenu;

    /**
     * sends a chat message to the GUI if not blank
     * @param event
     */
    @FXML
    protected void onSendButtonPressed(ActionEvent event) {
        String text = messageField.getText();
        String to = targetMenu.getValue();
        if (!text.isBlank()) {
            GUI.sendMessage(to, text.trim());
        }
        messageField.setText("");
    }
}
