package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.ChamsEvent;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.ArrayList;
import java.util.List;

public final class LagLess extends Module {

    private final List<Packet<?>> packets = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    private final NumberValue<Integer> delay = new NumberValue<Integer>("Delay", 320, 100, 600, this);
    private EntityOtherPlayerMP oldPos;
    private boolean sexo = false;

    @Override
    public String getName() {
        return "Lagger";
    }

    @Override
    public String getDescription() {
        return "Simula Un Lag Funny";
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
    public void onEnable() {
        this.timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.theWorld.removeEntityFromWorld(-2);
        super.onDisable();
    }

    private boolean containsDuplicate(List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (i != j) {
                    if (list.get(i).equals(list.get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ChamsEvent) {
            if (((ChamsEvent) event).getAbstractClientPlayer() == mc.thePlayer && sexo) {
                if (!mc.thePlayer.isMoving()) {
                    mc.theWorld.removeEntityFromWorld(-2);
                    return;
                }
                oldPos = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
                oldPos.copyLocationAndAnglesFrom(mc.thePlayer);
                oldPos.setEntityId(-2);
                mc.theWorld.addEntityToWorld(-2, oldPos);
            }
        }
        if (event instanceof PacketSend) {
            if (mc.theWorld == null) return;
            Packet<?> packet = ((PacketSend) event).getPacket();//1000
            if (timer.hasReached(450) && (packet instanceof C03PacketPlayer || packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging)) {
                packets.add(packet);
                ((PacketSend) event).setCancelled(true);
                if (timer.hasReached(1500)) {
                    for (Packet<?> p : packets) {
                        mc.getNetHandler().addToSendQueueNoPacket(p);
                    }
                    sexo = true;
                    packets.clear();
                    timer.reset();
                } else {
                    sexo = false;
                }
            }
        }
    }
}