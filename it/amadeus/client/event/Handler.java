package it.amadeus.client.event;

public interface Handler {

    /**
     * Introducing the interface to draw on all the modules
     * @param event - Event
     */

    void onEvent(final Event event);
}