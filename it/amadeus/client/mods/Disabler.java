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


    private final TimerUtil timer = new TimerUtil();
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VERUS, this);
    private final ModeValue<SPOOF_TYPE> spoof = new ModeValue<>("Spoof", SPOOF_TYPE.NEW, this);
    private final ModeValue<PACKET_TYPE> packet = new ModeValue<>("Packet", PACKET_TYPE.EXTRA, this);
    private final BooleanValue<Boolean> ground_check = new BooleanValue<>("Ground Check", true, this);
    private long nextTime;

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
        if (this.mode.getValue().equals(Mode.ALICE)) {
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
                    switch (this.spoof.getValue()) {
                        case NEW:

                            if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 3) {
                                this.packetQueue.clear();
                                this.timer.reset();
                            }
                            if (timer.sleep(nextTime)) {

                                this.nextTime = (long) (310L + Math.random());

                                if (!packetQueue.isEmpty() && packetQueue.size() > 79) {
                                    MotionUtil.sendDirect(packetQueue.poll());
                                    this.packetQueue.clear();
                                }
                                timer.reset();
                            }
                            break;
                        case OLD:
                            if (mc.thePlayer.ticksExisted % 180 == 0) {
                                if (!packetQueue.isEmpty() && packetQueue.size() > 125) {
                                    MotionUtil.sendDirect(packetQueue.poll());
                                }
                            }
                            break;
                    }
                    break;
                case DUPLICATE:
                    if (mc.thePlayer.ticksExisted % 3 == 1) {
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
                        ((PacketSend) event).setCancelled(true);
                        for (int i = 0; i < 4; i++) {
                            this.packetQueue.add(CONFIRM);
                        }
                    }
                    if (packet instanceof C03PacketPlayer) {
                        final double offset = -0.015625f;
                        if (mc.currentScreen instanceof GuiContainer) return;
                        if (ground_check.getValue() && mc.thePlayer.onGround) return;
                        boolean canTicked = mc.thePlayer.ticksExisted % 64 == 0;
                        boolean canSendPacket = canTicked && intentionalMove() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isJumping && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.hurtTime <= 0 && !doHittingProcess();
                        if (canSendPacket) {
                            switch (this.packet.getValue()) {
                                case EXTRA:
                                    MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, offset, mc.thePlayer.posZ, mc.thePlayer.onGround));
                                    break;
                                case CURRENT:
                                    ((C03PacketPlayer) packet).setY(offset);
                                    ((C03PacketPlayer) packet).setOnGround(false);
                                    ((C03PacketPlayer) packet).setMoving(false);
                                    break;
                            }
                        }
                         if (mc.thePlayer.ticksExisted % 3 == 1){
                            ((PacketSend) event).setCancelled(true);
                        }
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

    private void setPremissionFly() {
        PlayerCapabilities pc = new PlayerCapabilities();
        pc.disableDamage = false;
        pc.isFlying = false;
        pc.allowFlying = false;
        pc.isCreativeMode = false;
        pc.setFlySpeed(0.0F);
        pc.setPlayerWalkSpeed(0.0F);
        mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(pc));
    }

    public boolean doHittingProcess() {
        return (mc.thePlayer.isBlocking() || mc.thePlayer.isSwingInProgress || mc.thePlayer.isUsingItem() || mc.thePlayer
                .isOnLadder() || mc.thePlayer.isEating() || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiChest);
    }

    public enum Mode {VERUS, RIDING, SPECTATE, ALICE, NEGATVITY, DUPLICATE}

    public enum SPOOF_TYPE {OLD, NEW}

    public enum PACKET_TYPE {EXTRA, CURRENT}
}