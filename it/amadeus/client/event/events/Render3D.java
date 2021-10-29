package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;

public final class Render3D extends Event {

    private final float partialTicks;

    /**
     * Introducing the render3d event
     *
     * @param partialTicks - partialTicks
     */

    public Render3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
