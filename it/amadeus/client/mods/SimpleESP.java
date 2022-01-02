package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Render3D;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public final class SimpleESP extends Module {

    @Override
    public String getName() {
        return "SimpleESP";
    }

    @Override
    public String getDescription() {
        return "New Simple ESP For Finding Player";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }


    @Override
    public void onEvent(Event event) {
        if (event instanceof Render3D) {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e instanceof EntityLivingBase) {
                    EntityLivingBase entity = (EntityLivingBase) e;
                    if (canRender(entity)) {
                        drawEntityBox(entity);
                    }
                }
            }
        }
    }

    public boolean canRender(EntityLivingBase player) {
        if (player == mc.thePlayer) return false;
        if (player instanceof EntityAnimal || player instanceof EntityMob) return false;
        return (player instanceof EntityPlayer);
    }

    public void drawEntityBox(final EntityLivingBase p) {
        double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        double y = p.lastTickPosY + (p.posY - p.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
        double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
        int color = Color.RED.getRGB();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glScalef(0.03f, 0.03f, 0.03f);
        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
        GlStateManager.disableDepth();
        Color c = Color.WHITE;
        if (p.hurtTime != 0)
            c = Color.RED;
        Gui.drawRect(5, 73, 16, 70, Color.BLACK.hashCode());
        Gui.drawRect(13, 72, 16, 61, Color.BLACK.hashCode());
        Gui.drawRect(6, 71, 15, 72, c.hashCode());
        Gui.drawRect(14, 71, 15, 62, c.hashCode());
        Gui.drawRect(-4, 70, -15, 73, Color.BLACK.hashCode());
        Gui.drawRect(-12, 70, -15, 61, Color.BLACK.hashCode());
        Gui.drawRect(-5, 72, -14, 71, c.hashCode());
        Gui.drawRect(-13, 71, -14, 62, c.hashCode());
        Gui.drawRect(13, 14, 16, 2, Color.BLACK.hashCode());
        Gui.drawRect(5, 5, 16, 2, Color.BLACK.hashCode());
        Gui.drawRect(14, 13, 15, 3, c.hashCode());
        Gui.drawRect(6, 4, 15, 3, c.hashCode());
        Gui.drawRect(-12, 4, -15, 14, Color.BLACK.hashCode());
        Gui.drawRect(-15, 2, -4, 5, Color.BLACK.hashCode());
        Gui.drawRect(-13, 4, -14, 13, c.hashCode());
        Gui.drawRect(-14, 3, -5, 4, c.hashCode());
        double health = p.getHealth();
        Color healthColor = Color.lightGray;
        if (health >= 15.0) {
            healthColor = Color.green;
        } else if (health < 15 && health >= 6) {
            healthColor = Color.yellow;
        } else {
            healthColor = Color.red;
        }
        int healthX = -22;
        int s = -7;
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.rotate(180, 0.0f, 0.0f, 1.0f);
        net.minecraft.client.renderer.GlStateManager.enableDepth();
        GL11.glPopMatrix();

    }
}