package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import it.amadeus.client.utilities.PacketUtil;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2APacketParticles;
import org.lwjgl.input.Keyboard;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class Disabler extends Module {


    private final TimerUtil timer = new TimerUtil();
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedDeque<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.COMBACT, this);
    private long nextTime;
    private boolean expectedTeleport;

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
        return Keyboard.KEY_G;
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
        this.packetQueue.clear();
        this.expectedTeleport = false;
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
                case POOP:
                    if (!this.shouldRun()) {
                        this.expectedTeleport = false;
                        this.timer.reset();
                        this.packetQueue.clear();
                        return;
                    }

                    if (this.timer.hasReached(260L)) {
                        this.timer.reset();

                        if (!this.packetQueue.isEmpty()) {
                            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(this.packetQueue.poll());
                        }
                    }
                    break;
                case COMBACT:
                    if (mc.thePlayer.ticksExisted % 180 == 0) {
                        while (this.packetQueue.size() > 22) {
                            mc.getNetHandler().getNetworkManager().sendPacket(this.packetQueue.poll());
                        }
                    }
                    break;
                case VERUSCOSTUM:
                    if (this.timer.hasReached(490L)) {
                        if (!this.packetQueue.isEmpty())
                            mc.getNetHandler().getNetworkManager().sendPacket(this.packetQueue.poll());
                        this.timer.reset();
                    }
                    break;
                case VERUSOLD:
                    double yPos = Math.round(mc.thePlayer.posY / 0.015625) * 0.015625;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, yPos, mc.thePlayer.posZ);
                    if (mc.thePlayer.ticksExisted % 45 == 0) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new
                                        C03PacketPlayer.C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY,
                                        mc.thePlayer.posZ,
                                        true
                                )
                        );
                        mc.getNetHandler().getNetworkManager().sendPacket(new
                                        C03PacketPlayer.C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY - 11.025,
                                        mc.thePlayer.posZ,
                                        false
                                )
                        );
                        mc.getNetHandler().getNetworkManager().sendPacket(new
                                        C03PacketPlayer.C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY,
                                        mc.thePlayer.posZ,
                                        true
                                )
                        );
                    }
                    break;
                case JANITOR:
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }
                    break;
                case VERUS:
                    if ((mc.thePlayer != null) && mc.thePlayer.ticksExisted < 3) {//5
                        this.packetQueue.clear();
                        this.timer.reset();
                    }
                    if (timer.sleep(nextTime) && mc.thePlayer.isMovingOnGround() && !mc.thePlayer.isCollidedVertically) {
                        this.nextTime = (long) (310L + Math.random());
                        if (!packetQueue.isEmpty() && packetQueue.size() > 42) {
                            MotionUtil.sendDirect(packetQueue.poll());
                            //packetQueue.clear();
                        }
                        timer.reset();
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
                case VERUSCOSTUM:
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        C0FPacketConfirmTransaction c0fPacketConfirmTransaction = (C0FPacketConfirmTransaction) packet;
                        for (int i = 0; i < 3; i++) {
                            this.packetQueue.add(c0fPacketConfirmTransaction);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer c03 = (C03PacketPlayer) packet;
                        if (mc.thePlayer.ticksExisted % 40 == 0) {
                            c03.y = -0.015625f;
                            c03.onGround = false;
                            c03.moving = false;
                        }
                    }
                    if (mc.thePlayer != null && mc.thePlayer.ticksExisted <= 7) {
                        this.timer.reset();
                        this.packetQueue.clear();
                    }
                    break;
                case VERUSOLD:
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        for (int i = 0; i < 3; i++) {
                            packetQueue.add(packet);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    break;
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
                case OLDCOMBACT:
                    if (packet instanceof C00PacketKeepAlive) {
                        C00PacketKeepAlive packetKeepAlive = (C00PacketKeepAlive) packet;
                        int data = packetKeepAlive.getKey() * 4;
                        ((PacketSend) event).setCancelled(true);
                        MotionUtil.sendDirect(new C00PacketKeepAlive(data));
                    }
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        C0FPacketConfirmTransaction confirmTransaction = (C0FPacketConfirmTransaction) packet;
                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                            confirmTransaction.uid = -1;
                        } else {
                            ((PacketSend) event).setCancelled(true);
                        }
                    }
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C0CPacketInput());
                    }
                    if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0BPacketEntityAction) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    break;
                case VAC:
                    if (packet instanceof C13PacketPlayerAbilities) {
                        C13PacketPlayerAbilities c13 = (C13PacketPlayerAbilities) packet;
                        c13.setAllowFlying(true);
                        c13.setCreativeMode(true);
                        c13.setFlying(true);
                        c13.setFlySpeed(Float.NaN);
                        c13.setInvulnerable(true);
                        c13.setWalkSpeed(Float.NaN);
                    }
                    if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 150 == 0 && packet instanceof C03PacketPlayer) {
                        C03PacketPlayer c03 = (C03PacketPlayer) packet;
                        c03.y = -5038.18D;
                        c03.x = -5038.18D;
                    }
                    break;
                case COMBACT:
                    if (packet instanceof C00PacketKeepAlive) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0FPacketConfirmTransaction && Killaura.getCurrentTarget() != null) {
                        for (int i = 0; i < 4; i++) {
                            this.packetQueue.add(packet);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }


                    if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                        for (int i = 0; i < 4; i++) {
                            this.packetQueue.add(packet);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0BPacketEntityAction) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    break;
                case POOP:
                    if (!this.shouldRun()) return;
                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                        short action = -1;
                        if (packet instanceof C0FPacketConfirmTransaction) {
                            action = ((C0FPacketConfirmTransaction)packet).getUid();
                        }
                        if (action != -1 && this.isInventory(action)) return;
                        ((PacketSend) event).setCancelled(true);
                        this.packetQueue.add(packet);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                        if (mc.thePlayer.ticksExisted % 25 == 0) {
                            this.expectedTeleport = true;
                            c03PacketPlayer.setMoving(false);
                            c03PacketPlayer.setY(-0.015625);
                            c03PacketPlayer.setOnGround(false);
                        }
                    }
                    break;
                case VERUS:
                    if (packet instanceof C00PacketKeepAlive) {
                        for (int i = 0; i < 2; i++) {
                            this.packetQueue.add(packet);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        final C0FPacketConfirmTransaction CONFIRM = (C0FPacketConfirmTransaction) packet;
                        boolean block = mc.currentScreen instanceof GuiInventory;
                        if (block && CONFIRM.getUid() > 0 && CONFIRM.getUid() < 100) return;
                        for (int i = 0; i < 4; i++) {
                            this.packetQueue.add(CONFIRM);
                        }
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        MotionUtil.sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        MotionUtil.sendDirect(new C0CPacketInput());
                        final double offset = -0.015625f;
                        if (mc.currentScreen instanceof GuiContainer) return;
                        boolean canTicked = mc.thePlayer.ticksExisted % 64 == 0;
                        boolean canSendPacket = canTicked && intentionalMove() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isJumping && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.hurtTime <= 0 && !doHittingProcess();
                        if (canSendPacket) {
                            MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, offset, mc.thePlayer.posZ, mc.thePlayer.onGround));
                        }
                        setPremissionFly();
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
                case VERUSCOSTUM:
                case VERUSOLD:
                    if (packet instanceof S08PacketPlayerPosLook) {
                        S08PacketPlayerPosLook sex = (S08PacketPlayerPosLook) packet;
                        double x = sex.x - mc.thePlayer.posX;
                        double y = sex.y - mc.thePlayer.posY;
                        double z = sex.z - mc.thePlayer.posZ;
                        double diff = Math.sqrt(x * x + y * y + z * z);
                        if (diff <= 8) {
                            ((PacketReceive) event).setCancelled(true);
                            mc.getNetHandler().getNetworkManager().sendPacket(new
                                            C03PacketPlayer.C06PacketPlayerPosLook(
                                            sex.x,
                                            sex.y,
                                            sex.z,
                                            sex.getYaw(),
                                            sex.getPitch(),
                                            true
                                    )
                            );
                        }
                    }
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    break;
                case POOP:
                    if (packet instanceof S08PacketPlayerPosLook && this.expectedTeleport) {
                        S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;
                        this.expectedTeleport = false;

                        ((PacketReceive) event).setCancelled(true);

                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer
                                .C06PacketPlayerPosLook(s08PacketPlayerPosLook.getX(),
                                s08PacketPlayerPosLook.getY(),
                                s08PacketPlayerPosLook.getZ(),
                                s08PacketPlayerPosLook.getYaw(),
                                s08PacketPlayerPosLook.getPitch(), true));
                    }
                    break;
                case VERUS:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    if (packet instanceof S08PacketPlayerPosLook) {
                        S08PacketPlayerPosLook packet2 = (S08PacketPlayerPosLook) packet;
                        double x = packet2.getX() - mc.thePlayer.posX;
                        double y = packet2.getY() - mc.thePlayer.posY;
                        double z = packet2.getZ() - mc.thePlayer.posZ;
                        double diff = Math.sqrt(x * x + y * y + z * z);
                        if (diff <= 10.0D && mc.thePlayer.ticksExisted > 25 && !MotionUtil.isOverVoid()) {
                            ((PacketReceive) event).setCancelled(true);
                            PacketUtil.sendPacketSilent(new C03PacketPlayer.C06PacketPlayerPosLook(packet2.getX(), packet2.getY(), packet2.getZ(), packet2.getYaw(), packet2.getPitch(), true));
                        }
                        packet2.y += 1.0E-4D;
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
                case COMBACT:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    break;
            }
        }
    }

     private boolean isInGuiInventory(){
        return mc.currentScreen instanceof GuiContainer;
    }

    private boolean shouldRun() {
        return mc.thePlayer != null && mc.thePlayer.ticksExisted > 5;
    }

    private boolean isInventory(short action) {
        return action > 0 && action < 100;
    }

    private boolean intentionalMove() {
        return !(!mc.gameSettings.keyBindForward && !mc.gameSettings.keyBindBack.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed);
    }

    private void setPremissionFly() {
        PlayerCapabilities pc = new PlayerCapabilities();
        pc.disableDamage = true;
        pc.isFlying = true;
        pc.allowFlying = true;
        pc.isCreativeMode = true;
        mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(pc));
    }

    public boolean doHittingProcess() {
        return (mc.thePlayer.isBlocking() || mc.thePlayer.isSwingInProgress || mc.thePlayer.isUsingItem() || mc.thePlayer.isOnLadder() || mc.thePlayer.isEating() || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiChest);
    }

    public enum Mode {VERUS, RIDING, SPECTATE, ALICE, NEGATVITY, DUPLICATE, JANITOR, VERUSOLD, VERUSCOSTUM, COMBACT, OLDCOMBACT, VAC, POOP}
}