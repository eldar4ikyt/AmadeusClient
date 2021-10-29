package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;

public final class MoveJump extends Event implements Cancellable {
    private float yaw;

    public MoveJump(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {

    }
}
