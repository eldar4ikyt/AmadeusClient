package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.module.Module;

public final class NoSlow extends Module {

    @Override
    public String getName() {
        return "NoSlow";
    }

    @Override
    public String getDescription() {
        return "Non Ti Rallenta Quando Dovrebbe haha";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FIGHT;
    }

    @Override
    public void onEvent(Event event) {
    }
}
