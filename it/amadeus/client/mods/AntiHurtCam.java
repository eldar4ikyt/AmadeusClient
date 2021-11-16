package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.module.Module;

public final class AntiHurtCam extends Module {

    @Override
    public String getName() {
        return "AntiHurtCam";
    }

    @Override
    public String getDescription() {
        return "Toglie l'oscillamento della visuale quando vieni hittato";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }


    @Override
    public void onEvent(Event event) {}
}
