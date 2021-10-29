package it.amadeus.client.mods;

import it.amadeus.client.clickgui.ClickGui;
import it.amadeus.client.event.Event;
import it.amadeus.client.module.Module;
import org.lwjgl.input.Keyboard;

public final class Click extends Module {

    @Override
    public String getName() {
        return "ClickGui";
    }

    @Override
    public String getDescription() {
        return "Non Disponibile";
    }

    @Override
    public int getKey() {
        return Keyboard.KEY_RSHIFT;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }

    @Override
    public void onEnable() {
         mc.displayGuiScreen(new ClickGui());
        setToggled(false);
        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
    }
}
