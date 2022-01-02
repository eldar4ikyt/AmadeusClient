package it.amadeus.client.mods;

import io.netty.buffer.Unpooled;
import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.EventLoadWorld;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import it.amadeus.client.utilities.MotionUtil;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovementInput;
import org.apache.commons.lang3.RandomUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public final class Disabler extends Module {


    private final LinkedList<Packet<?>> packetList = new LinkedList<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VERUS, this);
    private final ModeValue<VerusMode> verus = new ModeValue<>("Verus", VerusMode.MOVEMENT, this);
    private final ModeValue<MatrixMode> matrix = new ModeValue<>("Matrix", MatrixMode.ENTERPRISE, this);
    private final ModeValue<MorganMode> morgan = new ModeValue<>("Morgan", MorganMode.FLYING, this);
    private final ModeValue<MineManMode> minemans = new ModeValue<>("MineMan", MineManMode.COMBACT, this);
    private final BooleanValue<Boolean> tranciacion = new BooleanValue<>("See Tranciacion", false, this);
    private final TimerUtil timer = new TimerUtil();
    private boolean expectedTeleport;
    private int memeTick;

    @Override
    public String getName() {
        return "Disabler";
    }

    @Override
    public String getDescription() {
        return "Rewrited Disabler";
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
        this.packetList.clear();
        this.timer.reset();
        this.expectedTeleport = false;
        if (this.mode.getValue().equals(Mode.ALICE)) {
            MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ, false));
            mc.thePlayer.setPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ);
            mc.renderGlobal.loadRenderers();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue().equals(Mode.MATRIX)) {
            if (this.matrix.getValue().equals(MatrixMode.PREMIUM)) {
                if (!packetList.isEmpty()) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(this.packetList.get(0));
                    this.packetList.clear();
                }
            }
        }
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            switch (this.mode.getValue()) {
                case NULLPLACE:
                    if (mc.thePlayer.isMoving()) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1, mc.thePlayer.posZ);
                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C08PacketPlayerBlockPlacement(pos, 1, null, 0, 0, 0));
                    }
                    break;
                case MATRIX:
                    switch (this.matrix.getValue()) {
                        case PREMIUM:
                            if (this.memeTick >= 5) {
                                this.memeTick = 0;
                                if (!packetList.isEmpty()) {
                                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(this.packetList.get(0));
                                    ChatUtil.print("RLB (Size=" + this.packetList.size() + "), (tick=" + RandomUtils.nextInt(0, 50) + ")");
                                    this.packetList.clear();
                                }
                            }
                            ++this.memeTick;
                            break;
                        case ENTERPRISE:
                            break;
                    }
                    break;
                case MORGAN:
                    switch (this.morgan.getValue()) {
                        case GROUND:
                            if (mc.thePlayer.ticksExisted % 3 == 1) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                            }
                            break;
                        case FLYING:
                            break;
                    }
                    break;
                case ALICE:
                    mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                    break;
                case MINEMAN:
                    switch (this.minemans.getValue()) {
                        case OLD:
                            mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                            break;
                        case COMBACT:
                            break;
                    }
                    break;
                case VERUS:
                    if (reset()) {
                        packetList.clear();
                        timer.reset();
                        return;
                    }
                    switch (this.verus.getValue()) {
                        case SPOOFER:
                            if ((mc.currentScreen instanceof GuiChest) || (mc.currentScreen instanceof GuiInventory)) {
                                mc.thePlayer.rotationYawHead = -180;
                            }
                            if ((timer.delay(900L))) {
                                if (packetList.size() > 143) {
                                    packetList.clear();
                                }
                                if (!doHittingProcess()) {
                                    if (!packetList.isEmpty() && !doHittingProcess() || (mc.thePlayer.isMoving() || this.lock())) {
                                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(packetList.poll());
                                    }
                                }
                                timer.reset();
                            }
                            if (timer.delay(1200L) && mc.getAmadeus().getModManager().getModuleByClass(Killaura.class).isToggled() || (mc.getAmadeus().getModManager().getModuleByClass(Speed.class).isToggled() || mc.getAmadeus().getModManager().getModuleByClass(Flight.class).isToggled() && mc.thePlayer.isMoving())) {
                                PlayerCapabilities pc = new PlayerCapabilities();
                                pc.disableDamage = false;
                                pc.isFlying = false;
                                pc.allowFlying = false;
                                pc.isCreativeMode = false;
                                pc.setFlySpeed(0.0F);
                                pc.setPlayerWalkSpeed(0.0F);
                                mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C13PacketPlayerAbilities(pc));
                                timer.reset();
                            }
                            break;
                        case REACH:
                            if (mc.thePlayer.ticksExisted % 180 == 0) {
                                while (this.packetList.size() > 25) {
                                    mc.thePlayer.sendQueue.addToSendQueueNoPacket(this.packetList.poll());
                                }
                            }
                            break;
                        case FULL:
                        case MOVEMENT:
                            if (!this.running()) {
                                this.expectedTeleport = false;
                                this.timer.reset();
                                this.packetList.clear();
                                return;
                            }
                            if (this.timer.sleep(260L)) {
                                this.timer.reset();
                                if (!this.packetList.isEmpty()) {
                                    mc.thePlayer.sendQueue.addToSendQueueNoPacket(this.packetList.poll());
                                }
                            }
                            break;
                    }
                    break;
            }
        } else if (event instanceof PacketSend) {
            if (mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketSend) event).getPacket();
            switch (this.mode.getValue()) {
                case MORGAN:
                    switch (this.morgan.getValue()) {
                        case GROUND:
                            break;
                        case FLYING:
                            if (packet instanceof C03PacketPlayer) {
                                C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
                                if (mc.thePlayer.ticksExisted % 4 == 2) {
                                    packetPlayer.setYaw(Float.MIN_VALUE);
                                    packetPlayer.y += 0.02D;
                                    packetPlayer.setOnGround(true);
                                }
                            }
                            break;
                    }
                    break;
                case MATRIX:
                    switch (this.matrix.getValue()) {
                        case PREMIUM:
                            if (packet instanceof C03PacketPlayer && !(packet instanceof C03PacketPlayer.C04PacketPlayerPosition) && !(packet instanceof C03PacketPlayer.C05PacketPlayerLook) && !(packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C02PacketUseEntity || packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C05PacketPlayerLook || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction) {
                                ((PacketSend) event).setCancelled(true);
                                this.packetList.add(packet);
                            }
                            break;
                        case ENTERPRISE:
                            if (packet instanceof C03PacketPlayer) {
                                if (mc.thePlayer.ticksExisted % 15 == 0) {
                                    try {
                                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                                        DataOutputStream out = new DataOutputStream(b);
                                        out.writeUTF(mc.thePlayer.getGameProfile().getName());
                                        out.writeBoolean(true);
                                        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                                        buf.writeBytes(b.toByteArray());
                                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C17PacketCustomPayload("matrix:geyser", buf));
                                    } catch (IOException ignored) {
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case MINEMAN:
                    switch (this.minemans.getValue()) {
                        case OLD:
                            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                                C03PacketPlayer.C04PacketPlayerPosition c04 = (C03PacketPlayer.C04PacketPlayerPosition) packet;
                                c04.setOnGround(false);
                                c04.setX(0);
                                c04.setY(0);
                                c04.setZ(0);
                            }
                            if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                                C03PacketPlayer.C06PacketPlayerPosLook c06 = (C03PacketPlayer.C06PacketPlayerPosLook) packet;
                                c06.setOnGround(false);
                            }
                            if (packet instanceof C03PacketPlayer) {
                                C03PacketPlayer c03 = (C03PacketPlayer) packet;
                                c03.setOnGround(false);
                            }
                            break;
                        case COMBACT:
                            if (Killaura.getCurrentTarget() != null && (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive)) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            break;
                    }
                    break;
                case NEGATIVITY:
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer position = (C03PacketPlayer) packet;
                        if (mc.thePlayer.ticksExisted % 12 == 0) {
                            position.setY(mc.thePlayer.posY - 11.0D);
                            position.setOnGround(true);
                        }
                    }
                    break;
                case VERUS:
                    switch (this.verus.getValue()) {
                        case TRANCIACION:
                            if (packet instanceof C03PacketPlayer) {
                                C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
                                packetPlayer.y += 0.02D;
                            }
                            break;
                        case PING:
                            if (packet instanceof C00PacketKeepAlive) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            break;
                        case RIDING:
                            if (packet instanceof C0FPacketConfirmTransaction) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (mc.thePlayer.ticksExisted % 3 == 0) {
                                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C18PacketSpectate(mc.thePlayer.getUniqueID()));
                                mc.getNetHandler().getNetworkManager().sendPacketNoEvent((new C0CPacketInput(MovementInput.moveStrafe, MovementInput.moveForward, mc.thePlayer.movementInput.jump, mc.thePlayer.movementInput.sneak)));
                            }
                            break;
                        case SPOOFER:
                            if (packet instanceof C00PacketKeepAlive) {
                                if ((mc.currentScreen instanceof GuiChest) || (mc.currentScreen instanceof GuiInventory)) {
                                    return;
                                }
                                if (timer.delay(1455L)) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings("en_US", 8, EntityPlayer.EnumChatVisibility.FULL, true, 127));
                                    mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C0DPacketCloseWindow(0));
                                } else if (timer.delay(1600L)) {
                                    timer.reset();
                                }
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C03PacketPlayer) {
                                C03PacketPlayer pos = (C03PacketPlayer) packet;
                                if (mc.thePlayer.ticksExisted % 3 != 0 && (mc.thePlayer.isMoving() || lock())) {
                                    if (!mc.thePlayer.isMovingOnGround() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isJumping && !mc.getAmadeus().getModManager().getModuleByClass(NoFall.class).isToggled() && timer.delay(1400L)) {
                                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                                        pos.y = +(RandomUtils.nextDouble(10.60508745964098D, 101.41138779393725D));//positive
                                        pos.x = RandomUtils.nextFloat(0.8412349224090576F, 0.9530588388442993F);
                                        pos.z = -0.43534232F;
                                        pos.moving = true;
                                    }
                                    if (doHittingProcess()) {
                                        mc.thePlayer.sendQueue.addToSendQueueNoPacket(new C0CPacketInput());
                                    }
                                    ((PacketSend) event).setCancelled(true);
                                }
                            }
                            if (packet instanceof C0FPacketConfirmTransaction) {
                                if ((mc.currentScreen instanceof GuiChest) || (mc.currentScreen instanceof GuiInventory)) {
                                    return;
                                }
                                if (!doHittingProcess() || !mc.thePlayer.isJumping) packetList.add(packet);
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (mc.thePlayer != null && mc.thePlayer.ticksExisted <= 7) {
                                timer.reset();
                                packetList.clear();
                            } else if (packet instanceof C03PacketPlayer.C05PacketPlayerLook || packet instanceof S08PacketPlayerPosLook) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            break;
                        case REACH:
                            if (reset()) {
                                packetList.clear();
                                return;
                            }
                            if (packet instanceof C0BPacketEntityAction) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction) {
                                packetList.add(packet);
                                ((PacketSend) event).setCancelled(true);
                            }
                            break;
                        case FULL:
                            if (!this.running()) return;
                            if (packet instanceof C0BPacketEntityAction) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                                ((PacketSend) event).setCancelled(true);
                            }
                            if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                                short action = -1;
                                if (packet instanceof C0FPacketConfirmTransaction)
                                    action = ((C0FPacketConfirmTransaction) packet).getUid();
                                if (action != -1 && this.isInventory(action)) return;
                                ((PacketSend) event).setCancelled(true);
                                this.packetList.add(packet);
                            }
                            if (packet instanceof C03PacketPlayer && mc.thePlayer.ticksExisted % 45 == 0) {
                                C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                                this.expectedTeleport = true;
                                c03PacketPlayer.setMoving(false);
                                c03PacketPlayer.setY(-0.015625);
                                c03PacketPlayer.setOnGround(false);
                            }
                            break;
                        case MOVEMENT:
                            if (!this.running()) return;
                            if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                                short action = -1;
                                if (packet instanceof C0FPacketConfirmTransaction)
                                    action = ((C0FPacketConfirmTransaction) packet).getUid();
                                if (action != -1 && this.isInventory(action)) return;
                                ((PacketSend) event).setCancelled(true);
                                this.packetList.add(packet);
                            }
                            if (packet instanceof C03PacketPlayer && mc.thePlayer.ticksExisted % 45 == 0) {
                                C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                                this.expectedTeleport = true;
                                c03PacketPlayer.setMoving(false);
                                c03PacketPlayer.setY(-0.015625);
                                c03PacketPlayer.setOnGround(false);
                            }
                            break;
                    }
                    break;
            }
            if(this.tranciacion.getValue()){
                if (packet instanceof C0FPacketConfirmTransaction) {
                    C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction) packet;
                    ChatUtil.print(EnumChatFormatting.GREEN + "You -> " + EnumChatFormatting.YELLOW + packetConfirmTransaction.getUid() +  " " + packetConfirmTransaction.accepted);
                }
            }
        } else if (event instanceof PacketReceive) {
            if (mc.isSingleplayer()) return;
            Packet<?> packet = ((PacketReceive) event).getPacket();
            switch (this.mode.getValue()) {
                case ALICE:
                    if (packet instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted % 33 == 1 && mc.theWorld != null) {
                        ((PacketReceive) event).setCancelled(true);
                        mc.timer.elapsedPartialTicks = 0.65F;
                        mc.thePlayer.posX = 0.12D;
                        mc.thePlayer.posY = Math.toRadians(mc.thePlayer.rotationYaw);
                    }
                    break;
                case MORGAN:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    break;
                case VERUS:
                    switch (this.verus.getValue()) {
                        case SPOOFER:
                            if (packet instanceof S08PacketPlayerPosLook) {
                                S08PacketPlayerPosLook playerPosLook = (S08PacketPlayerPosLook) packet;
                                playerPosLook.yaw += 1.0E-4;
                            }
                            if (packet instanceof S2APacketParticles) {
                                ((PacketReceive) event).setCancelled(true);
                            }
                            break;
                        case FULL:
                        case MOVEMENT:
                            if (packet instanceof S08PacketPlayerPosLook) {
                                S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) packet;
                                if (this.expectedTeleport && mc.theWorld != null) {
                                    this.expectedTeleport = false;
                                    ((PacketReceive) event).setCancelled(true);
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer
                                            .C06PacketPlayerPosLook(s08.getX(),
                                            s08.getY(),
                                            s08.getZ(),
                                            s08.getYaw(),
                                            s08.getPitch(), true));
                                }
                            }
                            if (packet instanceof S2APacketParticles) {
                                ((PacketReceive) event).setCancelled(true);
                            }
                            break;
                    }
                    break;
            }
            if(this.tranciacion.getValue()){
                if (packet instanceof S32PacketConfirmTransaction) {
                    S32PacketConfirmTransaction packetConfirmTransaction = (S32PacketConfirmTransaction) packet;
                    ChatUtil.print(EnumChatFormatting.RED + "Server -> " + EnumChatFormatting.GREEN + packetConfirmTransaction.getActionNumber() +  " " +  packetConfirmTransaction.func_148888_e());
                }
            }
        } else if (event instanceof EventLoadWorld) {
            packetList.clear();
            timer.reset();
            if (this.mode.getValue().equals(Mode.ALICE)) {
                MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ, false));
                mc.thePlayer.setPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ);
                mc.renderGlobal.loadRenderers();
            }
        }
    }

    private boolean reset() {
        return mc.thePlayer.ticksExisted <= 5;
    }

    private boolean running() {
        return mc.thePlayer != null && mc.thePlayer.ticksExisted > 5;
    }

    private boolean isInventory(short action) {
        return action > 0 && action < 100;
    }

    private boolean lock() {
        return !(!mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindBack.pressed
                && !mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindRight.pressed);
    }

    private boolean doHittingProcess() {
        return mc.thePlayer.isBlocking() || mc.thePlayer.isSwingInProgress || mc.thePlayer.isUsingItem() || mc.thePlayer.isOnLadder() || mc.thePlayer.isEating() || (mc.currentScreen instanceof GuiInventory) || (mc.currentScreen instanceof GuiChest);
    }

    private enum Mode {VERUS, MINEMAN, NEGATIVITY, ALICE, MORGAN, MATRIX, NULLPLACE}

    private enum VerusMode {FULL, MOVEMENT, REACH, SPOOFER, RIDING, PING, TRANCIACION}

    private enum MatrixMode {ENTERPRISE, PREMIUM}

    private enum MineManMode {OLD, COMBACT}

    private enum MorganMode {GROUND, FLYING}
}