package it.polimi.ingsw.distributed;

import it.polimi.ingsw.models.Tile;

import java.io.Serializable;

public record LivingRoomUpdate(Tile[][] board) implements Serializable {
}
