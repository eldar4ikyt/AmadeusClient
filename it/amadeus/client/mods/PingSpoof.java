package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.ArrayList;
import java.util.List;

public final class PingSpoof extends Module {

    private final NumberValue<Integer> delay = new NumberValue<Integer>("Delay", 320, 100, 600, this);
    private final List<Packet<?>> packets = new ArrayList<>();

    @Override
    public String getName() {
        return "PingSpoof";
    }

    @Override
    public String getDescription() {
        return "Manipola il tuo Ping";
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
        if (event instanceof PacketSend) {
            if(mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketSend) event).getPacket();
            if (packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction) {
                if (packets.contains(packet)) {
                    return;
                }
                ((PacketSend) event).setCancelled(true);
                packets.add(packet);
                new Thread(() -> {
                    try {
                        Thread.sleep(delay.getValue().longValue());
                        mc.thePlayer.sendQueue.addToSendQueue(packet);
                    } catch (Exception ignored) {
                    }
                    packets.remove(packet);
                }).start();
            }
        }
    }

    @Override
    public void onDisable() {
        packets.clear();
        super.onDisable();
    }
}
