package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import org.lwjgl.input.Keyboard;

public final class TimerMC extends Module {

    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.0D, 0.2D, 5.0D, this);

    @Override
    public String getName() {
        return "Timer";
    }

    @Override
    public String getDescription() {
        return "Modifica La Velocità di gioco";
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
        if(event instanceof Update)
        mc.timer.timerSpeed = speed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
