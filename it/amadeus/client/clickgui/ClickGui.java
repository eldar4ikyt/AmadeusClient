package it.amadeus.client.clickgui;

import it.amadeus.client.clickgui.pannels.components.Mode;
import it.amadeus.client.clickgui.util.font.UnicodeFontRenderer;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import it.amadeus.client.clickgui.pannels.Panel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {

    public static ArrayList<Panel> panels = new ArrayList<>();
    public static boolean dragging = false;
    private static UnicodeFontRenderer fontRender;
    private final Translate translate = new Translate(0.0F, 0.0F);
    public boolean createdPanels;
    public Mode mode;

    public ClickGui() {
        int x = 3;
        int y = 5;
        int count = 0;
        if (panels.size() != Module.Category.values().length) {
            for (Module.Category c : Module.Category.values()) {
                Panel p = new Panel(x, y, c);
                panels.add(p);
                x += p.getWidth() + 5;
                count++;
                if (count % 4 == 0) {
                    y += 50;
                    x = 3;
                }
                createdPanels = (panels.size() == Module.Category.values().length);
            }
        }
    }

    public static Color getSecondaryColor(boolean setting) {
        return setting ? new Color(0, 0, 0, 200) : new Color(25, 25, 25, 200);
    }

    public static UnicodeFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Minecraft.getMinecraft().getAmadeus().getFontManager().comfortaa18;
        }
        return fontRender;
    }

    public static Color getPrimaryColor() {
        return new Color(213, 2, 45);
    }

    public void reload(boolean reloadUserInterface) {
        for (Panel p : ClickGui.panels) {
            p.reload();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.translate.interpolate(sr.getScaledWidth(), sr.getScaledHeight(), 0.45D);
        GL11.glPushMatrix();
        GL11.glTranslatef((sr.getScaledWidth() >> 1), (sr.getScaledHeight() >> 1), 0.0F);
        GL11.glScaled((this.translate.getX() / sr.getScaledWidth()), (this.translate.getY() / sr.getScaledHeight()), 0.0D);
        GL11.glTranslatef((-sr.getScaledWidth() >> 1), (-sr.getScaledHeight() >> 1), 0.0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        panels.sort((a, b) -> Double.compare(a.lastClickedMs, b.lastClickedMs));
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).onTop = i == 0;
            panels.get(i).drawScreen(mouseX, mouseY);
        }
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel p : ClickGui.panels) {
            p.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }
}