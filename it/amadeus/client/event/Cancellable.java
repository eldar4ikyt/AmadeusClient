package it.amadeus.client.event;

public interface Cancellable {

    /**
     * Introducing the boolean for check if an event is cancelled
     */

    boolean isCancelled();

    /**
     * Introducing the method for set an event to cancelled
     */

    void setCancelled(final boolean cancelled);
}