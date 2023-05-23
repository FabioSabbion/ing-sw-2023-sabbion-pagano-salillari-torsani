package it.polimi.ingsw;

import it.polimi.ingsw.distributed.GameUpdate;
import it.polimi.ingsw.models.Tile;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

public record GameUpdateToFile(GameUpdate update, @Nullable List<Tile> remainingTiles) implements Serializable {
}
