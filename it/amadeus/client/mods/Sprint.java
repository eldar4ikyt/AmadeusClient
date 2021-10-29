package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;

public final class Sprint extends Module {

    @Override
    public String getName() {
        return "Sprint";
    }

    @Override
    public String getDescription() {
        return "Setta il giocatore in sprint mode";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            if (mc.thePlayer.isMoving()) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}