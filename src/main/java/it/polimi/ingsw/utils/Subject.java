package it.polimi.ingsw.utils;

public class Subject<U, T extends Enum<T>> extends Observable<U, T>{
    public void notifyObservers(U value, T eventType) {
        super.notifyObservers(value, eventType);
    }
}
