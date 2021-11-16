package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.*;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

public final class Flight extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VANILLA, this);
    private final NumberValue<Double> damage_ticks = new NumberValue<>("Damage Tick", 20.0D, 5.0D, 25.0D, this);
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 2.72D, 1.0D, 10.0D, this);
    private int boostTicks = 0;
    private float motion;
    private double launchY;

    @Override
    public String getName() {
        return "Flight";
    }

    @Override
    public String getDescription() {
        return "Vola Come un Uccellino";
    }

    @Override
    public int getKey() {
        return Keyboard.KEY_F;
    }

    @Override
    public Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue().equals(Mode.FASTCOLLIDE)) {
            mc.thePlayer.motionY = 0.0D;
        }
        if (this.mode.getValue().equals(Mode.DAMAGE)) {
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 3.0001, 0).expand(0, 0, 0)).isEmpty()) {
                if (mc.thePlayer.onGround) {
                    for (int i = 0; i < 9; i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (float) 0.42D, mc.thePlayer.posZ, false));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.000063, mc.thePlayer.posZ, false));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
            }
        }
        if (this.mode.getValue().equals(Mode.FEAR)) {
            if (mc.thePlayer.onGround) {
                for (int i = 0; i < 9; i++) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (float) 0.42D, mc.thePlayer.posZ, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.000063, mc.thePlayer.posZ, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            }
            mc.timer.timerSpeed = 0.15F;
        }
        this.launchY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {
            if (this.mode.getValue().equals(Mode.VANILLA)) {
                double offset = -.015625f;
                MovementInput movementInput = mc.thePlayer.movementInput;
                mc.thePlayer.motionY = movementInput.jump ? 0.87 : movementInput.sneak ? -0.87 : 0.0F;
                MotionUtil.setMotion(speed.getValue().floatValue());
            }
        }
        if (event instanceof MoveFlying) {
            if (this.mode.getValue().equals(Mode.DAMAGE)) {
                MotionUtil.legitStrafeMovement((MoveFlying) event, mc.thePlayer.rotationYaw);
            }
        }
        if (event instanceof PreMotion) {
            if (this.mode.getValue().equals(Mode.SKYWALKER)) {
                if (mc.thePlayer.motionY < 0) {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.onGround = true;
                }
            }
            if (this.mode.getValue().equals(Mode.FASTCOLLIDE)) {
                if (mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.thePlayer.setSpeed(1.8F);
                        return;
                    }
                    mc.thePlayer.setSpeed(0.35F);
                }
            }
            if (this.mode.getValue().equals(Mode.BLOCKSMC)) {
                if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                    mc.thePlayer.jump();
                    MotionUtil.setSpeed1(0.47999998927116394D);
                }
                MotionUtil.setSpeed1(MotionUtil.getSpeed());
            }
            if (this.mode.getValue().equals(Mode.FEAR)) {
                mc.thePlayer.motionY = 0;
                if (mc.thePlayer.isJumping) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
                }
                float fearGamesSpeed;
                if (mc.thePlayer.isMoving()) fearGamesSpeed = 5F;
                else fearGamesSpeed = 0;
                MotionUtil.setMotion(fearGamesSpeed);
                mc.thePlayer.cameraYaw = fearGamesSpeed * 0.02F;
            }
            if (this.mode.getValue().equals(Mode.DAMAGE)) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;
                if (mc.thePlayer.hurtTime > 0) {
                    boostTicks = damage_ticks.getValue().intValue();
                }
                if (boostTicks > 0) {
                    motion = (boostTicks / damage_ticks.getValue().intValue()) * speed.getValue().floatValue();
                    boostTicks--;
                } else {
                    motion = 0.15f;
                }
                MotionUtil.strafe(motion, mc.thePlayer.rotationYaw, false);
            }
        }
        if (event instanceof EventCollide) {
            if (this.mode.getValue().equals(Mode.FASTCOLLIDE)) {
                if (mc.thePlayer.isSneaking())
                    return;
                if (((EventCollide) event).getBlock() instanceof net.minecraft.block.BlockAir && ((EventCollide) event).getY() < mc.thePlayer.posY)
                    ((EventCollide) event).setAxisalignedbb(AxisAlignedBB.fromBounds(((EventCollide) event).getX(), ((EventCollide) event).getY(), ((EventCollide) event).getZ(), ((EventCollide) event).getX() + 1.0D, mc.thePlayer.posY, ((EventCollide) event).getZ() + 1.0D));
            }
            if (this.mode.getValue().equals(Mode.VERUS) && mc.theWorld != null && !mc.thePlayer.isSneaking()) {
                ((EventCollide) event).setAxisalignedbb((new AxisAlignedBB(-2.0D, -1.0D, -2.0D, 2.0D, 1.0D, 2.0D)).offset(((EventCollide) event).getX(), ((EventCollide) event).getY(), ((EventCollide) event).getZ()));
            }
            if (this.mode.getValue().equals(Mode.BLOCKSMC)) {
                if (((EventCollide) event).getBlock() instanceof BlockAir && ((EventCollide) event).getY() <= this.launchY)
                    ((EventCollide) event).setAxisalignedbb(AxisAlignedBB.fromBounds(((EventCollide) event).getX(), ((EventCollide) event).getY(), ((EventCollide) event).getZ(), ((EventCollide) event).getX() + 1.0D, this.launchY, ((EventCollide) event).getZ() + 1.0D));
            }
        }
        if(event instanceof PacketSend){
            Packet<?> packet = ((PacketSend) event).getPacket();
            if (this.mode.getValue().equals(Mode.SKYWALKER)) {
                if (packet instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) packet;
                    if (mc.thePlayer.motionY < 0) {
                        player.setOnGround(true);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (boostTicks > 0) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
        motion = 0;
        boostTicks = 0;
        mc.timer.timerSpeed = 1.0F;
        MotionUtil.sendDirect(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, -.015625f, mc.thePlayer.posZ, mc.thePlayer.onGround));
        super.onDisable();
    }


    public enum Mode {VANILLA, DAMAGE, VERUS, FEAR, BLOCKSMC, FASTCOLLIDE, SKYWALKER}
}