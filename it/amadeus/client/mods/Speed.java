package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.MoveFlying;
import it.amadeus.client.event.events.Moving;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import it.amadeus.client.utilities.MotionUtil;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

public final class Speed extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.BLATANT, this);
    private int stage;

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
        if (event instanceof MoveFlying) {
            if (this.mode.getValue().equals(Mode.YPORT)) {
                double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.1) : 1);
                double baseSpeed = 0.2873D;
                    if (mc.thePlayer.isCollidedHorizontally) {
                        if (mc.thePlayer.onGround) {
                            stage = 0;
                            mc.thePlayer.posY += 0.42F;
                        }
                    } else {
                        mc.gameSettings.keyBindJump.pressed = false;

                        if (mc.thePlayer.onGround) {
                            stage++;
                            double sped = 2.13 + speedAmplifier;
                            if (this.stage < 2) {
                                sped -= 0.4;
                            }
                            float sex  = mc.thePlayer.moveStrafing = (float) (baseSpeed * sped);
                            ((MoveFlying) event).setStrafe(sex);
                            mc.thePlayer.posY = 0.42F;
                            mc.thePlayer.motionY = 0;
                        }
                    }
                    ((MoveFlying) event).setStrafe(0);
                }
        }
        if (event instanceof Moving) {
            if (this.mode.getValue().equals(Mode.BLATANT)) {
                if (mc.thePlayer.isMovingOnGround()) {
                    ((Moving) event).setY(mc.thePlayer.motionY = 0.41999998688697815D);
                    MotionUtil.setMotion(2f);
                } else {
                    MotionUtil.setMotion(0.772F);
                }
            }
        }
        if (event instanceof PreMotion) {
            if (this.mode.getValue().equals(Mode.VERUS)) {
                if (mc.thePlayer.isMoving()) {
                    if (mc.thePlayer.hurtTime != 0)
                        mc.thePlayer.motionY = -2.0D;
                    if (mc.thePlayer.onGround && mc.thePlayer.hurtTime == 0) {
                        mc.thePlayer.jump();
                    } else {
                        MotionUtil.setMotion(0.33F);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    public enum Mode {VERUS, YPORT, BLATANT}
}
