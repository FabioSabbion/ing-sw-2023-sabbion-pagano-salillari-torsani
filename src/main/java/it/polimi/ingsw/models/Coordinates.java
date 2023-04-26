package it.polimi.ingsw.models;

import java.io.Serializable;

/**
 * Simple auxiliary class that just contains two values, X and Y coordinates
 */
public class Coordinates implements Serializable {
    public final int x;
    public final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
