package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class EventVelocity extends Event {

    private double x, y, z;

    public EventVelocity(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
