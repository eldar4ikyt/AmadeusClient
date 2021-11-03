package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.EventCollide;
import it.amadeus.client.event.events.MoveFlying;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

public final class Flight extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VANILLA, this);
    private final NumberValue<Double> damage_ticks = new NumberValue<>("Damage Tick", 20.0D, 5.0D, 25.0D, this);
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 2.72D, 1.0D, 10.0D, this);
    private int boostTicks = 0;
    private float motion;

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
        if (this.mode.getValue().equals(Mode.DAMAGE)) {
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 3.0001, 0).expand(0, 0, 0)).isEmpty()) {
                MotionUtil.damage();
            }
        }
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
        if(event instanceof MoveFlying){
            if (this.mode.getValue().equals(Mode.DAMAGE)) {
                MotionUtil.legitStrafeMovement((MoveFlying) event, mc.thePlayer.rotationYaw);
            }
        }
        if(event instanceof  PreMotion){
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
        }if(event instanceof EventCollide){
            if (this.mode.getValue().equals(Mode.VERUS) && mc.theWorld != null && !mc.thePlayer.isSneaking()) {
                ((EventCollide) event).setAxisalignedbb((new AxisAlignedBB(-2.0D, -1.0D, -2.0D, 2.0D, 1.0D, 2.0D)).offset(((EventCollide) event).getX(), ((EventCollide) event).getY(), ((EventCollide) event).getZ()));
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

    public enum Mode {VANILLA, DAMAGE, VERUS}
}