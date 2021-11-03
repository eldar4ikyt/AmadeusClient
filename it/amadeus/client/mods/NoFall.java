package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFall extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VANILLA, this);

    @Override
    public String getName() {
        return "NoFall";
    }

    @Override
    public String getDescription() {
        return "Ti impedisce di prendere danno da caduta";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Module.Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PreMotion) {
            if (this.mode.getValue().equals(Mode.VANILLA)) {
                if (mc.thePlayer.fallDistance >= 3) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
            }
        }
        if (event instanceof PacketSend) {
            Packet<?> packet = ((PacketSend) event).getPacket();
            if (packet instanceof C03PacketPlayer && this.mode.getValue().equals(Mode.PACKETLESS)) {
                C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
                if (mc.thePlayer.fallDistance >= 3) {//maybe 42 vaue is a jumping (current 0.77)
                    packetPlayer.setOnGround(true);
                }
            }
        }
    }

    public enum Mode {VANILLA, PACKETLESS}
}