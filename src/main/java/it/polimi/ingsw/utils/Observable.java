package it.polimi.ingsw.utils;

import java.util.Vector;

public class Observable<U, T extends Enum<T>> {
    private Vector<Observer<U, T>> obs;

    /** Construct an Observable with zero Observers. */

    public Observable() {
        obs = new Vector<>();
    }

    public synchronized Observer<U, T> addObserver(Observer<U, T> o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }

        return o;
    }

    public synchronized void deleteObserver(Observer<U, T> o) {
        obs.removeElement(o);
    }

    protected void notifyObservers(U value, T eventType) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

        synchronized (this) {
            arrLocal = obs.toArray();
        }

        for (int i = arrLocal.length-1; i>=0; i--)
            ((Observer<U, T>)arrLocal[i]).update(value, eventType);
    }

    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    public synchronized int countObservers() {
        return obs.size();
    }
}
