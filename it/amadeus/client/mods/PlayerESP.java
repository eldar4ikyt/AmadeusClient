package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.ChamsEvent;
import it.amadeus.client.module.Module;
import it.amadeus.client.objloader.mc.TessellatorModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class PlayerESP extends Module {

    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.NAZI, this);
    private final TessellatorModel hitlerHead = new TessellatorModel("/assets/minecraft/amadeus/hitler/head.obj");
    private final TessellatorModel hitlerBody = new TessellatorModel("/assets/minecraft/amadeus/hitler/body.obj");
    private final TessellatorModel nagatoro = new TessellatorModel("/assets/minecraft/amadeus/nagatoro/nagatoro.obj");
    private final TessellatorModel sasuke = new TessellatorModel("/assets/minecraft/amadeus/Sasuke/sasuke.obj");
    private final TessellatorModel naruto = new TessellatorModel("/assets/minecraft/amadeus/Naruto/D0401253.obj");
    private final TessellatorModel sussy = new TessellatorModel("/assets/minecraft/amadeus/sussy/sussy.obj");
    private final TessellatorModel spongebob = new TessellatorModel("/assets/minecraft/amadeus/Spongebob/spongebob.obj");

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

    @Override
    public void onEvent(Event event) {
        if (event instanceof ChamsEvent) {
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            EntityPlayer entity = ((ChamsEvent) event).getAbstractClientPlayer();
            ((ChamsEvent) event).setCancelled(true);

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;

            float yaw = entity.rotationYaw;


            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-yaw, 0, entity.height, 0);
            if (this.mode.getValue().equals(Mode.NAZI)) {
                GlStateManager.scale(0.026, (entity.isSneaking() ? 0.020 : 0.026), 0.026);
            } else if (this.mode.getValue().equals(Mode.NARUTO) || this.mode.getValue().equals(Mode.SASUKE)) {
                GlStateManager.scale(0.076, (entity.isSneaking() ? 0.070 : 0.076), 0.076);
            } else if (this.mode.getValue().equals(Mode.SUSSY)) {
                GlStateManager.scale(0.496, (entity.isSneaking() ? 0.490 : 0.496), 0.496);
            }

            GlStateManager.disableLighting();

            if (entity.hurtTime > 0) {
                GlStateManager.color(1, 0.1f, 0.1f, 1);
            }

            if (this.mode.getValue().equals(Mode.NAZI)) {
                this.hitlerHead.render();
                this.hitlerBody.render();
            } else if (this.mode.getValue().equals(Mode.SEX)) {
                this.nagatoro.render();
            } else if (this.mode.getValue().equals(Mode.NARUTO)) {
                this.naruto.render();
            } else if (this.mode.getValue().equals(Mode.SASUKE)) {
                this.sasuke.render();
            } else if (this.mode.getValue().equals(Mode.SUSSY)) {
                GL11.glColor4f(217, 0, 0, 11);
                this.sussy.render();
            }
            else if (this.mode.getValue().equals(Mode.SPONGEBOB)) {
                this.spongebob.render();
            }

            GlStateManager.enableLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.resetColor();
        }
    }

    public enum Mode {NAZI, SEX, NARUTO, SASUKE, SPONGEBOB, SUSSY}
}
