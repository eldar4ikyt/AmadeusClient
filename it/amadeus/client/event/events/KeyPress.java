package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;

public class KeyPress extends Event {

    private int key;

    public KeyPress(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
