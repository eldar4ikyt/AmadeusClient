package it.amadeus.client.event.events;

import it.amadeus.client.event.Event;
import net.minecraft.entity.Entity;

public final class Attack extends Event {
    public Entity entity;

    public Attack(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
