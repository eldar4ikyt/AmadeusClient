package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.EventVelocity;
import it.amadeus.client.module.Module;

public final class SuperReverseKB extends Module {

    @Override
    public String getName() {
        return "SuperReverseKB";
    }

    @Override
    public String getDescription() {
        return "FOR FAT BOI NIGGA";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventVelocity){
            final double offset = (8000.0D / 4);
            if(mc.thePlayer.hurtTime > 0){
                ((EventVelocity) event).setX(offset * 2);
                ((EventVelocity) event).setY(offset);
                ((EventVelocity) event).setZ(offset * 2);
            }else {
                ((EventVelocity) event).setX(8000.0D);
                ((EventVelocity) event).setY(8000.0D);
                ((EventVelocity) event).setZ(8000.0D);
            }
        }
    }
}
