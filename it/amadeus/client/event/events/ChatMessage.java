package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;
import lombok.Getter;
import lombok.Setter;


public final class ChatMessage extends Event implements Cancellable {

    @Getter
    @Setter
    private String message;
    private boolean cancel;

    public ChatMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }
}
