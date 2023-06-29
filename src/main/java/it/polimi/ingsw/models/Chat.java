package it.polimi.ingsw.models;

import it.polimi.ingsw.events.MessageEvent;
import it.polimi.ingsw.utils.Observable;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * represents the Chat in the game
 */
public class Chat extends Observable<List<Message>, MessageEvent> {
    List<Message> messageList;

    public Chat() {
        messageList = new LinkedList<>();
    }

    /**
     * builds a {@link Message} object and notifies the observers
     * @param message the message
     * @param from the sender
     * @param to the recipient
     */
    public synchronized void sendMessage(String message, String from, @Nullable String to) {
        Message record = new Message(messageList.size(), from, to, message, LocalDateTime.now());

        messageList.add(record);

        this.notifyObservers(List.of(record), MessageEvent.SINGLE_MESSAGE);
    }

    /**
     * sends all messages to all the observers
     */
    public void emitAllMessages() {
        this.notifyObservers(messageList, MessageEvent.ALL_MESSAGE);
    }
}
