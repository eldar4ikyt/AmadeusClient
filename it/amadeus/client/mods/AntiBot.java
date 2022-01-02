package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public final class AntiBot extends Module {

    private final List<EntityPlayer> watchdogBots = new ArrayList<>();

    @Override
    public String getName() {
        return "AntiBot";
    }

    @Override
    public String getDescription() {
        return "Rimuove i bot di hypixel";
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
        if (event instanceof Update) {
            if (mc.thePlayer.ticksExisted <= 500) {
                for (final EntityPlayer entity : mc.theWorld.playerEntities) {
                    if (entity.getDistanceToEntity(mc.thePlayer) <= 17.0f && Math.abs(mc.thePlayer.posY - entity.posY) > 2.0 && !isOnSameTeam(entity) && entity != mc.thePlayer && !watchdogBots.contains(entity) && entity.ticksExisted != 0 && entity.ticksExisted <= 10) {
                        watchdogBots.add(entity);
                        ChatUtil.print("Added bot: " + entity.getGameProfile().getName() + ", Distance: " + entity.getDistanceToEntity(mc.thePlayer) + ", Ticks Existed: " + entity.ticksExisted);
                    }
                }
            }
            if (watchdogBots.isEmpty()) {return;}
            watchdogBots.forEach(wdBots -> mc.theWorld.removeEntity(wdBots));
        }
    }


    private boolean isOnSameTeam(final EntityLivingBase entity) {
        if (entity.getTeam() != null && AntiBot.mc.thePlayer.getTeam() != null) {
            final char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            final char c2 = AntiBot.mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }
}
