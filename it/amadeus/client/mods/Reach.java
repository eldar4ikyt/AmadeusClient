package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import it.amadeus.client.utilities.TimerUtil;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

public final class Reach extends Module {

    @Getter
    private static boolean isLegit;
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.Extreme, this);
    private final BooleanValue<Boolean> remove_bot = new BooleanValue<>("RemoveBots", false, this);
    private final BooleanValue<Boolean> assist = new BooleanValue<>("Assist", true, this);
    private final NumberValue<Double> reach = new NumberValue<>("Assist Reach", 4.02D, 1.0D, 6.7D, this);
    private final TimerUtil timer = new TimerUtil();

    @Override
    public String getName() {
        return "Reach";
    }

    @Override
    public String getDescription() {
        return "Ti Estende La Reach Di Gioco";
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
        if (event instanceof Update) {
            switch (mode.getValue()) {
                case Lite:
                    isLegit = true;
                    break;
                case Extreme:
                    isLegit = false;
                    break;
            }
        } else if (event instanceof PreMotion) {
            if (mc.gameSettings.keyBindPickBlock.pressed && assist.getValue() && !mc.getAmadeus().getModManager().getModuleByClass(Disabler.class).isToggled()) {
                if (mc.objectMouseOver.entityHit != null) {
                    if (mc.objectMouseOver.entityHit != mc.thePlayer) {
                        float distanceToEntity = mc.thePlayer.getDistanceToEntity(mc.objectMouseOver.entityHit);
                        if (distanceToEntity < reach.getValue().floatValue() && mc.thePlayer.canEntityBeSeen(mc.objectMouseOver.entityHit)) {
                            mc.thePlayer.swingItem();
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.ATTACK));
                            mc.thePlayer.setSprinting(false);
                        }
                    }
                }
            }
        } else if (event instanceof PacketReceive) {
            if (mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketReceive) event).getPacket();
            if (packet instanceof S0CPacketSpawnPlayer && mc.thePlayer.swingProgress > 0 && remove_bot.getValue()) {
                ((PacketReceive) event).setCancelled(true);
            }
        }
    }

    @Override
    public void onEnable() {
        ChatUtil.print("§bWork On Vulcan Try On §eFearGames/Coral");
        this.timer.reset();
        super.onEnable();
    }

    private enum Mode {Lite, Extreme}
}
