package it.polimi.ingsw.models;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + "}";
    }
}
