package it.polimi.ingsw.distributed;

import javax.annotation.Nullable;
import java.io.Serializable;
/**
 * The MessageUpdate class represents an update of a message that can be transmitted over the network.
 * It contains the recipient (optional if the recipient is the whole chat) and the message content.
 */
public record MessageUpdate(@Nullable String to, String message) implements Serializable {
}
