package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.PreMotion;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;

public final class LongJump extends Module {

    @Override
    public String getName() {
        return "LongJump";
    }

    @Override
    public String getDescription() {
        return "Zoom Zoom Jumping with you!";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.MOVEMENTS;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PreMotion) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                strafe();
                mc.thePlayer.motionX *= 2.139999988079071D;
                mc.thePlayer.motionY += 0.20999999977648254D;
                mc.thePlayer.motionZ *= 2.139999988079071D;
                mc.thePlayer.speedInAir = 0.1F;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    private void strafe(float speed) {
        if (!mc.thePlayer.isMoving()) return;
        double yaw = MotionUtil.getDirection(mc.thePlayer.rotationYaw);
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    private void strafe() {
        strafe((float) MotionUtil.getSpeed());
    }
}
