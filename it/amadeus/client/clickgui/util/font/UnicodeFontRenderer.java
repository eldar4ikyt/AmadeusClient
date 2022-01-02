package it.amadeus.client.clickgui.util.font;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;

public class UnicodeFontRenderer extends FontRenderer {

    private final UnicodeFont font;

    public UnicodeFontRenderer(Font awtFont) {
        super((Minecraft.getMinecraft()).gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
        this.font = new UnicodeFont(awtFont);
        this.font.addAsciiGlyphs();
        this.font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try {
            this.font.loadGlyphs();
        } catch (SlickException exception) {
            throw new RuntimeException(exception);
        }
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        this.FONT_HEIGHT = this.font.getHeight(alphabet) / 2;
    }


    public float drawStringWithFont(String string, float x, float y, int color) {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        boolean blend = GL11.glIsEnabled(3042);
        boolean lighting = GL11.glIsEnabled(2896);
        boolean texture = GL11.glIsEnabled(3553);
        if (!blend)
            GL11.glEnable(3042);
        if (lighting)
            GL11.glDisable(2896);
        if (texture)
            GL11.glDisable(3553);
        x *= 2.0F;
        y *= 2.0F;
        this.font.drawString(x, y, string, new Color(color));
        if (texture)
            GL11.glEnable(3553);
        if (lighting)
            GL11.glEnable(2896);
        if (!blend)
            GL11.glDisable(3042);
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        GL11.glPopMatrix();
        GlStateManager.bindTexture(0);
        return (int) x;
    }


    public void chromaString(String string, int x, int y) {
        int tmpX = x;
        int currentColor = -1;
        for (char currentChar : string.toCharArray()) {
            long l = (System.currentTimeMillis() - (tmpX * 10 - y * 10));
            currentColor = java.awt.Color.HSBtoRGB(l % (int) 2000.0F / 2000.0F, 0.8F, 0.8F);
            drawStringWithFont(string, tmpX, y, currentColor);
            tmpX += getCharWidth(currentChar);
        }
    }


    public int drawStringWithShadow(String text, float x, float y, int color) {
        drawStringWithFont(text, x + 1.0F, y + 1.0F, -16777216);
        return (int) drawStringWithFont(text, x, y, color);
    }

    public int getCharWidth(char c) {
        return getStringWidth(Character.toString(c));
    }

    public int getStringWidth(String string) {
        return this.font.getWidth(string) / 2;
    }

    public int getStringHeight(String string) {
        return this.font.getHeight(string) / 2;
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        drawStringWithFont(text, x - (getStringWidth(text) >> 1), y, color);
    }
}
