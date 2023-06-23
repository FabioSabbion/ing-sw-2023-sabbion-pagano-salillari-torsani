package it.polimi.ingsw.models;

import it.polimi.ingsw.events.MessageEvent;
import it.polimi.ingsw.utils.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatTest {
    private Chat chat;

    @Test
    void sendMessage() {
        chat.addObserver(new Observer<List<Message>, MessageEvent>() {
            @Override
            public void update(List<Message> value, MessageEvent eventType) {
                assertEquals(1, value.size());
                assertEquals("Bella", value.get(0).message());
                assertEquals("Marco", value.get(0).from());
                assertEquals("Giacomo", value.get(0).to());
            }
        });

        chat.sendMessage("Bella", "Marco", "Giacomo");
    }

    @Test
    void emitAllMessages() {
        chat.sendMessage("Bella", "Marco", "Giacomo");
        chat.sendMessage("Porcoddio", "Andri", "LP");

        chat.addObserver(new Observer<List<Message>, MessageEvent>() {
            @Override
            public void update(List<Message> value, MessageEvent eventType) {
                assertEquals(2, value.size());
            }
        });

        chat.emitAllMessages();
    }

    @BeforeEach
    void setup(){
        this.chat = new Chat();
    }
}