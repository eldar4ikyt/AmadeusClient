package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Strafing extends Event implements Cancellable {

    private float yaw;


    public Strafing(float yaw) {
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
