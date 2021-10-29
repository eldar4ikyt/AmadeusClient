package it.amadeus.client.mods;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.amadeus.client.clickgui.util.values.valuetypes.BooleanValue;
import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.event.events.PacketSend;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import it.amadeus.client.utilities.TimerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2APacketParticles;
import net.optifine.util.MathUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Disabler extends Module {

    private final TimerUtil timer = new TimerUtil();
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.BLOCKSMC, this);
    private final ModeValue<PACKET_TYPE> latestVerusType = new ModeValue<>("Packet Type", PACKET_TYPE.EXTRA, this);
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 68.62D, 1.0D, 145.0D, this);
    private final NumberValue<Float> pingSpoof = new NumberValue<>("PingSpoof", 307F, 1.0F, 750.0F, this);
    private final BooleanValue<Boolean> latestVerusInventoryFix = new BooleanValue<>("Fix Inventory", true, this);
    private final BooleanValue<Boolean> ground_check = new BooleanValue<>("Ground Check", true, this);
   // private final BooleanValue<Boolean> packetinputnuke = new BooleanValue<>("Dev Fix", false, this);
   // private final NumberValue<Double> packetinputdelay = new NumberValue<>("Dev Fix Delay", 23.52D, 1.0D, 75.0D, this);

    //Controlla Che Il Player Non Stia Attaccando o usando l'oggetto
   /* public static boolean doHittingProcess() {
        return mc.thePlayer.isBlocking() || mc.thePlayer.isSwingInProgress && mc.thePlayer.swingProgress != 0;
    }*/

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
        if (this.mode.getValue().equals(Mode.BLOCKSMC)) {
            ChatUtil.print("BlocksMC Disabler Is Experimental");
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
                case BLOCKSMC:
                    /*if(mc.thePlayer.isDead){
                        for (int i = 0; i < 20; i++) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
                        }
                        if (!packetQueue.isEmpty()){
                            sendDirect(this.packetQueue.poll());
                        }
                        timer.reset();
                    }*/

                    if(packetQueue.size() > 247){
                        sendDirect(packetQueue.poll());
                        packetQueue.clear();
                    }

                    new Thread(() -> {
                        try {
                            Thread.sleep(9000);
                            if (!packetQueue.isEmpty() && !(mc.thePlayer.movementInput.moveForward != 0)){
                                if(packetQueue.size() >= 245){//245, 125
                                    ChatUtil.print(""+packetQueue.size());
                                    sendDirect(packetQueue.poll());
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }).start();
                  /*  if (mc.getAmadeus().getModManager().getModuleByClass(Flight.class).isToggled()&& !doHittingProcess()&& packetinputnuke.getValue()) {
                        for (int i = 0; i < packetinputdelay.getValue().intValue(); i++) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
                        }
                    }*/
                    if(mc.getAmadeus().getModManager().getModuleByClass(Flight.class).isToggled() || mc.getAmadeus().getModManager().getModuleByClass(Speed.class).isToggled()){
                        setPremissionFly();
                    }
                    break;
                case VERUS:
                    if (this.timer.delay(pingSpoof.getValue())) {
                        if (!packetQueue.isEmpty()) {
                            sendDirect(this.packetQueue.poll());
                        }
                        timer.reset();
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
                        sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        sendDirect(new C0CPacketInput());
                        final PlayerCapabilities capabilities = new PlayerCapabilities();
                        capabilities.allowFlying = true;
                        capabilities.disableDamage = true;
                        capabilities.isFlying = true;
                        capabilities.isCreativeMode = true;
                        capabilities.allowEdit = true;
                        capabilities.setFlySpeed(Float.POSITIVE_INFINITY);
                        capabilities.setPlayerWalkSpeed(Float.POSITIVE_INFINITY);
                        sendDirect(new C13PacketPlayerAbilities(capabilities));
                    }
                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    break;
                case VERUS:
                    if (mc.theWorld == null||mc.thePlayer == null || mc.thePlayer.ticksExisted < 50) {
                        return;
                    }
                    if (packet instanceof C00PacketKeepAlive) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction) packet;
                        if (this.latestVerusInventoryFix.getValue()) {
                            boolean block = mc.currentScreen instanceof GuiInventory;
                            if (block && c0FPacketConfirmTransaction.getUid() > 0 && c0FPacketConfirmTransaction.getUid() < 100) {
                                return;
                            }
                        } else {
                            this.packetQueue.add(c0FPacketConfirmTransaction);
                            ((PacketSend) event).setCancelled(true);
                        }
                    }
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                        sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        double offset = -.015625f;
                        if (ground_check.getValue() && mc.thePlayer.onGround && !mc.getAmadeus().getModManager().getModuleByClass(Speed.class).isToggled())
                            return;
                        if (mc.thePlayer.ticksExisted % this.delay.getValue().intValue() == 0) {

                            switch (this.latestVerusType.getValue()) {
                                case EXTRA: {
                                    sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, offset, mc.thePlayer.posZ, mc.thePlayer.onGround));
                                    break;
                                }
                                case CURRENT: {
                                    c03PacketPlayer.setY(offset);
                                    c03PacketPlayer.setOnGround(false);
                                    c03PacketPlayer.setMoving(false);
                                    break;
                                }
                            }
                        }
                    }
                    if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 8) {
                        this.packetQueue.clear();
                        this.timer.reset();
                    }
                    break;
                case BLOCKSMC:
                    if (packet instanceof C00PacketKeepAlive) {//reach by ping manipulation?
                        C00PacketKeepAlive packetKeepAlive = (C00PacketKeepAlive) packet;
                       /* for (int i = 0; i < 4; i++) { //5
                                this.packetQueue.add(packetKeepAlive);
                        }*/
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
                    //TODO: DA RIVEDERE
                    if (packet instanceof C07PacketPlayerDigging && mc.thePlayer.isBlocking()) {
                        ((PacketSend) event).setCancelled(true);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        sendDirect(new C0CPacketInput());
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                        if (mc.currentScreen instanceof GuiContainer) return;
                        double offset = -.015625f;//test
                        ((C03PacketPlayer) packet).y += 0.002D;
                       // ((C03PacketPlayer) packet).setMoving(false);
                        if (ground_check.getValue() && mc.thePlayer.onGround) return;//fixed fly by intentional move
                        boolean canTicked = mc.thePlayer.ticksExisted % this.delay.getValue().intValue() == 0;
                        boolean canSendPacket = canTicked && intentionalMove();
                        if (canSendPacket) {
                            sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, offset, mc.thePlayer.posZ, mc.thePlayer.onGround));
                        }
                        if(mc.thePlayer.ticksExisted % 3 == 1)
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
                    //potevo anche solo cancellarlo lol
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
                        sendDirect(new C18PacketSpectate(mc.thePlayer.getGameProfile().getId()));
                        sendDirect(new C0CPacketInput());
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
                case BLOCKSMC:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    if (packet instanceof S08PacketPlayerPosLook) {
                        S08PacketPlayerPosLook packet8 = (S08PacketPlayerPosLook) packet;
                        packet8.yaw += 1.0E-4D;
                    }
                    break;
                case VERUS:
                    if (packet instanceof S2APacketParticles) {
                        ((PacketReceive) event).setCancelled(true);
                    }
                    break;
            }
        }
    }

    public boolean intentionalMove() {
        return !(!mc.gameSettings.keyBindForward && !mc.gameSettings.keyBindBack.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed);
    }

    private void setPremissionFly() {
        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
        PlayerCapabilities pc = new PlayerCapabilities();
        pc.disableDamage = false;
        pc.isFlying = false;
        pc.allowFlying = false;
        pc.isCreativeMode = false;
        pc.setFlySpeed(0.0F);
        pc.setPlayerWalkSpeed(0.0F);
        mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(pc));
    }

    private void sendDirect(Packet<?> p) {
        mc.getNetHandler().getNetworkManager().sendPacket(p, null, (GenericFutureListener<? extends Future<? super Void>>) null);
    }

    public enum Mode {BLOCKSMC, VERUS, RIDING, SPECTATE}

    public enum PACKET_TYPE {EXTRA, CURRENT}
}