package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.RenderUtil;
import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.Render3D;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Blink extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.TRANCIACION, this);
    private final BooleanValue<Boolean> lag = new BooleanValue<>("BlinkLag", true, this);
    private final NumberValue<Double> blink_lag_delay = new NumberValue<>("Blink Lag Delay", 5.0, 1.0, 30.0, this);
    private final ArrayList<Packet<?>> packetList = new ArrayList<>();
    private final List<Vec3> crumbs = new CopyOnWriteArrayList<>();
    private final TimerUtil timer = new TimerUtil();


    @Override
    public String getName() {
        return "Blink";
    }

    @Override
    public String getDescription() {
        return "Trattiene i packet di gioco";
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
            switch (this.mode.getValue()) {
                case MOVEMENT:
                    if ((((PacketSend) event).getPacket() instanceof C0APacketAnimation || ((PacketSend) event).getPacket() instanceof C03PacketPlayer || ((PacketSend) event).getPacket() instanceof C07PacketPlayerDigging || ((PacketSend) event).getPacket() instanceof C08PacketPlayerBlockPlacement)) {
                        if (this.lag.getValue()) {
                            if (mc.thePlayer.ticksExisted % this.blink_lag_delay.getValue() == 0.0) {
                                try {
                                    for (final Packet<?> packets : this.packetList) {
                                        mc.getNetHandler().addToSendQueueNoPacket(packets);
                                    }
                                    this.packetList.clear();
                                    this.crumbs.clear();
                                } catch (ConcurrentModificationException exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                ((PacketSend) event).setCancelled(true);
                                this.packetList.add(((PacketSend) event).getPacket());
                            }
                        } else {
                            ((PacketSend) event).setCancelled(true);
                            this.packetList.add(((PacketSend) event).getPacket());
                        }
                    }
                    break;
                case TRANCIACION:
                    if (((PacketSend) event).getPacket() instanceof C0FPacketConfirmTransaction) {
                        if (packetList.contains(((PacketSend) event).getPacket())) {
                            return;
                        }
                        ((PacketSend) event).setCancelled(true);
                        packetList.add(((PacketSend) event).getPacket());
                        if (mc.thePlayer.ticksExisted % this.blink_lag_delay.getValue() == 0.0) {
                            try {
                                for (final Packet<?> packets : this.packetList) {
                                    mc.getNetHandler().addToSendQueueNoPacket(packets);
                                }
                                this.packetList.clear();
                                this.crumbs.clear();
                            } catch (ConcurrentModificationException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                    break;
            }
        } else if (event instanceof Render3D) {
            if (this.timer.sleep(10L)) {
                this.crumbs.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                this.timer.reset();
            }
            if (!this.crumbs.isEmpty() && this.crumbs.size() > 2) {
                for (int i = 1; i < this.crumbs.size(); ++i) {
                    final Vec3 vecBegin = this.crumbs.get(i - 1);
                    final Vec3 vecEnd = this.crumbs.get(i);
                    final int color = getColor(255, 255, 255);
                    final float beginX = (float) ((float) vecBegin.xCoord - RenderManager.renderPosX);
                    final float beginY = (float) ((float) vecBegin.yCoord - RenderManager.renderPosY);
                    final float beginZ = (float) ((float) vecBegin.zCoord - RenderManager.renderPosZ);
                    final float endX = (float) ((float) vecEnd.xCoord - RenderManager.renderPosX);
                    final float endY = (float) ((float) vecEnd.yCoord - RenderManager.renderPosY);
                    final float endZ = (float) ((float) vecEnd.zCoord - RenderManager.renderPosZ);
                    final boolean bobbing = mc.gameSettings.viewBobbing;
                    mc.gameSettings.viewBobbing = false;
                    RenderUtil.drawLine3D(beginX, beginY, beginZ, endX, endY, endZ, color);
                    mc.gameSettings.viewBobbing = bobbing;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.crumbs.clear();
        try {
            for (final Packet<?> packets : this.packetList) {
                mc.getNetHandler().addToSendQueueNoPacket(packets);
            }
            this.packetList.clear();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        this.crumbs.clear();
        super.onEnable();
    }

    private int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    private int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    private enum Mode {MOVEMENT, TRANCIACION}
}
