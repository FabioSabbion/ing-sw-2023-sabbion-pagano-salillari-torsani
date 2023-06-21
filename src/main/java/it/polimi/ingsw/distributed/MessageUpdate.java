package it.polimi.ingsw.distributed;

import javax.annotation.Nullable;
import java.io.Serializable;

public record MessageUpdate(@Nullable String to, String message) implements Serializable {
}
