package it.amadeus.client.event;

import net.minecraft.client.Minecraft;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager {

    private List<Handler> listeners;
    private boolean cancelled;

    /**
     * Introducing the method for hook an event
     * @param event - Event
     */

    public void hook(final Event event) {
        if (!this.cancelled) {
            if (this.listeners == null || this.listeners.isEmpty()) {
                return;
            }
            for (Handler listener : this.listeners) {
                if (Objects.nonNull(Minecraft.getMinecraft().theWorld)) {
                    listener.onEvent(event);
                }
            }
        }
    }

    public void addListener(final Handler listener) {
        if (!this.listeners.contains(listener) && !this.cancelled) this.listeners.add(listener);
    }

    public void removeListener(final Handler listener) {
        if (this.listeners.contains(listener) && !this.cancelled) this.listeners.remove(listener);
    }

    /**
     * Introducing a method for setup the listeners
     */

    public void setupListeners() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Introducing a method for sort the listeners
     */

    public void organizeListeners() {
        this.listeners.sort(Comparator.comparing(Object::toString));
    }

    /**
     * Introducing a method for get the listeners
     */

    public List<Handler> getListeners() {
        return this.listeners;
    }

    /**
     * Introducing a boolean for check if the event is cancelled
     */

    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Introducing a method for set the event to cancelled / not cancelled
     */

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
