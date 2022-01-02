package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Moving;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

public final class Speed extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.VANILLA, this);
    private final NumberValue<Double> speed = new NumberValue<>("STREDIAN Speed", 0.33D, 0.10D, 10.0D, this);

    private int airMoves = 0;

    @Override
    public String getName() {
        return "Speed";
    }

    @Override
    public String getDescription() {
        return "Corri Veloce Come Flash";
    }

    @Override
    public int getKey() {
        return Keyboard.KEY_X;
    }

    @Override
    public Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Moving) {
            if (this.mode.getValue().equals(Mode.VANILLA)) {
                if (mc.thePlayer.isMovingOnGround()) {
                    ((Moving) event).setY(mc.thePlayer.motionY = 0.41999998688697815D);
                    MotionUtil.setMotion(2f);
                } else {
                    MotionUtil.setMotion(0.772F);
                }
            }
        }
        if (event instanceof PreMotion) {
            switch (this.mode.getValue()) {
                case VULCAN:
                    if (mc.thePlayer.onGround) {
                        MotionUtil.sendDirect(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posZ * 2, -1, mc.thePlayer.posZ * 2, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                    }
                 /*   if (!(mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown())) return;
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.setSprinting(true);
                        mc.thePlayer.jump();
                    } else {
                        mc.timer.timerSpeed = 1;
                        if (airMoves >= 2) {
                            mc.thePlayer.jumpMovementFactor = 0.0230f;
                            if (airMoves >= 13 && airMoves % 8 == 0.0) {
                                mc.thePlayer.motionY = -0.32 - 0.004 * Math.random();
                                mc.thePlayer.jumpMovementFactor = 0.0260f;
                            }
                        }
                        airMoves++;
                    }*/
                    break;
                case STREDIAN:
                    if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                        MotionUtil.setMotion(this.speed.getValue().floatValue());
                    }
                    break;
                case VERUS:
                    if (mc.thePlayer.isMoving()) {
                        if (mc.thePlayer.hurtTime != 0)
                            mc.thePlayer.motionY = -2.0D;
                        if (mc.thePlayer.onGround && mc.thePlayer.hurtTime == 0) {
                            mc.thePlayer.jump();
                        } else {
                            MotionUtil.setMotion(0.33F);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        this.airMoves = 0;
        super.onDisable();
    }

    public enum Mode {VERUS, VULCAN, VANILLA, STREDIAN}
}
