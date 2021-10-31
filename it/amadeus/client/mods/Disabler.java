package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import it.amadeus.client.utilities.MotionUtil;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2APacketParticles;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Disabler extends Module {

    /***
     * Il Disabler Di Verus Disabilità solo I Movimenti e non i check del combact (per quelli non esiste)
     *
     * By AdrianCode
     */

    private final TimerUtil timer = new TimerUtil();
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VERUS, this);
    private final BooleanValue<Boolean> ground_check = new BooleanValue<>("Ground Check", true, this);

    @Override
    public String getName() {
        return "Disabler";
    }

    @Override
    public String getDescription() {
        return "Disabilita Alcuni AntiCheat";
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
        if (this.mode.getValue().equals(Mode.VERUS)) {
            ChatUtil.print("Verus Only Movements Disabler");
        } else if (this.mode.getValue().equals(Mode.ALICE)) {
            MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ, false));
            mc.thePlayer.setPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ);
            mc.renderGlobal.loadRenderers();
        }
        this.timer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.packetQueue.clear();
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            switch (this.mode.getValue()) {
                case VERUS:
                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                            if (!packetQueue.isEmpty()) {
                                if (packetQueue.size() >= 245) {
                                    MotionUtil.sendDirect(packetQueue.poll());
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }).start();
                    break;
                case DUPLICATE:
                    if (mc.thePlayer.ticksExisted % 3 == 1){
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }
                    break;
            }
        }
        if (event instanceof PacketSend) {
            if (mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketSend) event).getPacket();
            switch (this.mode.getValue()) {
                case RIDING:
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        MotionUtil.sendDirect(new C0CPacketInput());
                        final PlayerCapabilities capabilities = new PlayerCapabilities();
                        capabilities.allowFlying = true;
                        capabilities.disableDamage = true;
                        capabilities.isFlying = true;
                        capabilities.isCreativeMode = true;
                        capabilities.allowEdit = true;
                        capabilities.setFlySpeed(Float.POSITIVE_INFINITY);
                        capabilities.setPlayerWalkSpeed(Float.POSITIVE_INFINITY);
                        MotionUtil.sendDirect(new C13PacketPlayerAbilities(capabilities));
                    }
                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    break;
                case ALICE:
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                    }
                    break;
                case NEGATVITY:
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer position = (C03PacketPlayer) packet;
                        if (mc.thePlayer.ticksExisted % 12 == 0) {
                            position.setY(mc.thePlayer.posY - 11.0D);
                            position.setOnGround(true);
                        }
                    }
                    break;
                case VERUS:
                    if (packet instanceof C00PacketKeepAlive) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        C0FPacketConfirmTransaction CONFIRM = (C0FPacketConfirmTransaction) packet;
                        boolean block = mc.currentScreen instanceof GuiInventory;
                        if (block && CONFIRM.getUid() > 0 && CONFIRM.getUid() < 100) {
                            return;
                        }
                        for (int i = 0; i < 4; i++) {
                            this.packetQueue.add(CONFIRM);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        MotionUtil.sendDirect(new C0CPacketInput());
                        if (mc.currentScreen instanceof GuiContainer) return;
                        double offset = -.015625f;
                        ((C03PacketPlayer) packet).y += 0.002D;
                        if (ground_check.getValue() && mc.thePlayer.onGround) return;
                        boolean canTicked = mc.thePlayer.ticksExisted % Math.round(68.62D) == 0;//integer
                        boolean canSendPacket = canTicked && intentionalMove();
                        if (canSendPacket) {
                            MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, offset, mc.thePlayer.posZ, mc.thePlayer.onGround));
                        }
                        if (mc.thePlayer.ticksExisted % 3 == 1)
                            ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0EPacketClickWindow) {
                        C0EPacketClickWindow pc2 = (C0EPacketClickWindow) packet;
                        if (mc.thePlayer.ticksExisted % 136 == 0) {
                            pc2.windowId = 0;
                            pc2.slotId = -999;
                            pc2.usedButton = 0;
                            pc2.actionNumber = 1;
                            pc2.clickedItem = null;
                            pc2.mode = 4;
                        }
                    }
                    if ((packet instanceof C0BPacketEntityAction)) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 8) {
                        this.packetQueue.clear();
                        this.timer.reset();
                    }
                    break;
                case SPECTATE:
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        MotionUtil.sendDirect(new C0CPacketInput());
                    }
                    if (packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction)
                        ((PacketSend) event).setCancelled(true);
                    break;
            }
        }
        if (event instanceof PacketReceive) {
            if (mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketReceive) event).getPacket();
            switch (this.mode.getValue()) {
                case VERUS:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    if (packet instanceof S08PacketPlayerPosLook) {
                        S08PacketPlayerPosLook packet8 = (S08PacketPlayerPosLook) packet;
                        packet8.yaw += 1.0E-4D;
                    }
                    break;
                case ALICE:
                    if (packet instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted % 33 == 1) {
                        ((PacketReceive) event).setCancelled(true);
                        mc.timer.elapsedPartialTicks = 0.65F;
                        mc.thePlayer.posX = 0.12D;
                        mc.thePlayer.posY = Math.toRadians(mc.thePlayer.rotationYaw);
                    }
                    break;
            }
        }
    }

    private boolean intentionalMove() {
        return !(!mc.gameSettings.keyBindForward && !mc.gameSettings.keyBindBack.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed);
    }

    public enum Mode {VERUS, RIDING, SPECTATE, ALICE, NEGATVITY, DUPLICATE}

    public enum PACKET_TYPE {EXTRA, CURRENT}
}