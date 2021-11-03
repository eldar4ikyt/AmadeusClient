package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.ChamsEvent;
import it.amadeus.client.module.Module;
import it.amadeus.client.objloader.mc.TessellatorModel;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class PlayerESP extends Module {

    @Override
    public String getName() {
        return "PlayerESP";
    }

    @Override
    public String getDescription() {
        return "Ti Mostra I giocatori vicini";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }

    private final TessellatorModel hitlerHead = new TessellatorModel("/assets/minecraft/amadeus/hitler/head.obj");
    private final TessellatorModel hitlerBody = new TessellatorModel("/assets/minecraft/amadeus/hitler/body.obj");

    @Override
    public void onEvent(Event event) {
        if(event instanceof ChamsEvent){
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            EntityPlayer entity = ((ChamsEvent) event).getAbstractClientPlayer();
            ((ChamsEvent) event).setCancelled(true);

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;

            float yaw =  entity.rotationYaw;


            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-yaw, 0, entity.height, 0);
            GlStateManager.scale(0.026, (entity.isSneaking() ? 0.020 : 0.026), 0.026);
            GlStateManager.disableLighting();

            if (entity.hurtTime > 0) {
                GlStateManager.color(1, 0.1f, 0.1f, 1);
            }

      //      this.dbz.render();

            this.hitlerHead.render();
            this.hitlerBody.render();

            GlStateManager.enableLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
        }
    }
}
