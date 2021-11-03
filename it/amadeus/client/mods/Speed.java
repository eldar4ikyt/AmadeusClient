package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Moving;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import org.lwjgl.input.Keyboard;

public final class Speed extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.BLATANT, this);

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
            if (this.mode.getValue().equals(Mode.BLATANT)) {
                if(mc.thePlayer.isMovingOnGround()){
                    ((Moving) event).setY(mc.thePlayer.motionY = 0.41999998688697815D);
                    MotionUtil.setMotion(2f);
                }else{
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

    public enum Mode {VERUS, BLATANT}
}
