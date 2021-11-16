package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Attack;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.MotionUtil;
import it.amadeus.client.utilities.PacketUtil;
import net.minecraft.entity.EntityLivingBase;

import java.util.concurrent.ThreadLocalRandom;

public final class Criticals extends Module {

    @Override
    public String getName() {
        return "Criticals";
    }

    @Override
    public String getDescription() {
        return "Fai solo danni critici";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FIGHT;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Attack) {
            EntityLivingBase ent = (EntityLivingBase) ((Attack) event).getEntity();
            double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
            if ((ent.hurtTime < 5.0D || ent.hurtTime == 0) && shouldCrit()) {
                PacketUtil.sendC04(x, y + 0.00124D + ThreadLocalRandom.current().nextDouble(1.0E-4D, 9.0E-4D), z, false, false);
                PacketUtil.sendC04(x, y + 8.5E-4D, z, false, false);
            }
        }
    }

    private boolean shouldCrit() {
        boolean isRealGround = (mc.thePlayer.onGround && MotionUtil.getOnRealGround(mc.thePlayer, 1.0E-4D) && mc.thePlayer.isCollidedVertically);
        return (isRealGround && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder());
    }
}
