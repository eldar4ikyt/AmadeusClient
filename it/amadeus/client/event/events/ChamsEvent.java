package it.amadeus.client.event.events;

import it.amadeus.client.event.Cancellable;
import it.amadeus.client.event.Event;
import lombok.Getter;
import net.minecraft.client.entity.AbstractClientPlayer;


public final class ChamsEvent extends Event implements Cancellable {

    @Getter AbstractClientPlayer abstractClientPlayer;
    @Getter double x, y, z;
    @Getter float yaw;
    @Getter float ticks;
    private boolean cancel;

    public ChamsEvent(AbstractClientPlayer abstractClientPlayer, double x, double y, double z, float yaw, float ticks) {
        this.abstractClientPlayer = abstractClientPlayer;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.ticks = ticks;
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
