package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class MoveFlying extends Event implements Cancellable {

    private float yaw;
    private float strafe;
    private float forward;
    private float friction;
    private double x,y,z;

    public MoveFlying(float yaw, float strafe, float forward, float friction, double x, double y, double z) {
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {}
}
