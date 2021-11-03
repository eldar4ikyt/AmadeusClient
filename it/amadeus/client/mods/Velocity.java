package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public final class Velocity extends Module {

    @Override
    public String getName() {
        return "Velocity";
    }

    @Override
    public String getDescription() {
        return "Non prenderai più il kb";
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
        if (event instanceof PacketReceive) {
            Packet<?> packet = ((PacketReceive) event).getPacket();
            if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity p = (S12PacketEntityVelocity) packet;
                if (mc.theWorld.getEntityByID(p.getEntityID()) == mc.thePlayer) {
                    ((PacketReceive) event).setCancelled(true);
                }
            }
            if (packet instanceof S27PacketExplosion) {
                ((PacketReceive) event).setCancelled(true);
            }
        }
    }
}