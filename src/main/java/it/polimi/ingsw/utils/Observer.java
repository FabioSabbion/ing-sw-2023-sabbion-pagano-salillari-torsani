package it.polimi.ingsw.utils;

public interface Observer<SubjectType extends Observable<Event>, Event extends Enum<Event>> {
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@code Observable} object's
     * {@code notifyObservers} method to have all the object's
     * observers notified of the change.
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */

    void update(SubjectType o, Event arg);
}
