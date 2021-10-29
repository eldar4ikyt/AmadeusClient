package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;

public final class ShaderRender extends Event {

    private final float partialTicks;

    /**
     * Introducing the shader render event
     * @param partialTicks - partialTicks
     */

    public ShaderRender(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}