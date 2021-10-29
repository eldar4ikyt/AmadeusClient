package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;

public final class MouseClick extends Event {

    public int mouseKey;

    /**
     * Introducing the method for Mouse click
     */

    public MouseClick(int key) {
        this.mouseKey = key;
    }
}
