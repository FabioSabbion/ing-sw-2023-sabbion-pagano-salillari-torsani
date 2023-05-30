package it.polimi.ingsw.models;

import it.polimi.ingsw.controller.events.MessageEvent;
import it.polimi.ingsw.utils.Observable;
import it.polimi.ingsw.utils.Observer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Chat extends Observable<List<Message>, MessageEvent> {
    List<Message> messageList;

    public Chat() {
        messageList = new LinkedList<>();
    }

    public void sendMessage(String message, String from, @Nullable String to) {
        Message record = new Message(messageList.size(), from, to, message);

        messageList.add(record);

        this.notifyObservers(List.of(record), MessageEvent.SINGLE_MESSAGE);
    }

    public void emitAllMessages() {
        this.notifyObservers(messageList, MessageEvent.ALL_MESSAGE);
    }
}
