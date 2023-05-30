package it.polimi.ingsw.models;

import javax.annotation.Nullable;
import java.io.Serializable;

public record Message(int id,String from, @Nullable String to, String message) implements Serializable {
}
