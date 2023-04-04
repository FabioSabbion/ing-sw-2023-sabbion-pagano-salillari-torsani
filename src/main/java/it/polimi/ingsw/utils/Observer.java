package it.polimi.ingsw.utils;

public interface Observer<U,T extends Enum<T>> {
     void update(U value, T eventType);
}
