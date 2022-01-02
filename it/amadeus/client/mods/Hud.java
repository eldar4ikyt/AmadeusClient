package it.amadeus.client.mods;

import it.amadeus.client.clickgui.ClickGui;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.KeyPress;
import it.amadeus.client.event.events.Overlay;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public final class Hud extends Module {

    private int selected;
    private int ToggleSelected;
    private boolean isOpen;

    @Override
    public String getName() {
        return "Hud";
    }

    @Override
    public String getDescription() {
        return "Renderizza Le Info Del Client";
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
        if (event instanceof Overlay) {
           // FXGL.entityBuilder().view(new Rectangle(21,21, javafx.scene.paint.Color.RED)).buildAndAttach();
            mc.getTextureManager().bindTexture(new ResourceLocation("amadeus/Hud.png"));
            Gui.drawModalRectWithCustomSizedTexture(1, 1, 0.0F, 0.0F, 126, 66, 126.0F, 66.0F);
            GlStateManager.color(1, 1, 1, 1);
            newArrayList();
            GL11.glPushMatrix();
            GlStateManager.translate(5, 27, 0);
            tabGui4Vinny();
            GL11.glPopMatrix();
        } else if (event instanceof KeyPress) {
            onKey(((KeyPress) event).getKey());
        }
    }

    private void tabGui4Vinny() {
        final ScaledResolution sr = new ScaledResolution(mc);
        int y = 45;
        for (int i = 0; i < Category.values().length; i++) {
            Category cat = Category.values()[i];
            String name = cat.name().charAt(0) + cat.name().substring(1).toLowerCase();
            Gui.drawRect((int) 2.0D, (y + i * 15), (int) 117.0D, (y + 15 + i * 15), (selected == i) ? Color.RED.getRGB() : Integer.MIN_VALUE);
            switch (name) {
                case "Fight":
                    mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont("COMBACT", 30.0F, (y + i * 15 + 2), Color.BLACK.getRGB());
                    break;
                case "Movements":
                    mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont("MOVE", 40.0F, (y + i * 15 + 2), Color.BLACK.getRGB());
                    break;
                case "Render":
                    mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont("RENDER", 36.0F, (y + i * 15 + 2), Color.BLACK.getRGB());
                    break;
                case "Fun":
                    mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont("FUN", 46.0F, (y + i * 15 + 2), Color.BLACK.getRGB());
                    break;
            }
            if (isOpen && i == selected)
                for (int j = 0; j < mc.getAmadeus().getModManager().getModsByCat(cat).size(); j++) {
                    Module m = mc.getAmadeus().getModManager().getModsByCat(cat).get(j);
                    Gui.drawRect((int) 120.0D, (y + i * 15 + j * 15), (int) 226.0D, (y + i * 15 + j * 15 + 15), (ToggleSelected == j) ? Color.RED.getRGB() : Integer.MIN_VALUE);
                    mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont(m.getName(), 124.0F, (y + i * 15 + j * 15 + 2), m.isToggled() ? Color.ORANGE.getRGB() : Color.BLACK.getRGB());
                }
        }
    }

    private void onKey(int k) {
        if (k == 200) {
            if (!isOpen) {
                selected--;
                if (selected <= -1)
                    selected = 3;
            } else {
                ToggleSelected--;
                if (ToggleSelected <= 0)
                    ToggleSelected = 0;
            }
        } else if (k == 208) {
            if (!isOpen) {
                selected++;
                if (selected >= 4)
                    selected = 0;
            } else {
                ToggleSelected++;
                if (ToggleSelected >= mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).size())
                    ToggleSelected = 0;
            }
        } else if (k == 205) {
            isOpen = ToggleSelected < mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).size();
        } else if (k == 203) {
            isOpen = false;
            ToggleSelected = 0;
        } else if (k == 28 &&
                isOpen) {
            boolean toggled = mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).get(ToggleSelected).isToggled();
            if (toggled) {
                mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).get(ToggleSelected).onDisable();
                mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).get(ToggleSelected).setToggled(false);
            } else {
                mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).get(ToggleSelected).onEnable();
                mc.getAmadeus().getModManager().getModsByCat(Category.values()[selected]).get(ToggleSelected).setToggled(true);
            }
        }
    }


    private void newArrayList() {
        final ScaledResolution sr = new ScaledResolution(mc);
        int y = 0;
        int count = 0;
        for (Module m : mc.getAmadeus().getModManager().getMods()) {
            if (!m.isToggled() || m.getName().equals("Hud") || m.getName().equals("ClickGui"))
                continue;
            if (m.isToggled()) {
                final float xPos = sr.getScaledWidth() - mc.getAmadeus().getFontManager().comfortaa20.getStringWidth(m.getName());
                mc.getAmadeus().getFontManager().comfortaa20.drawStringWithFont(m.getName(), (int) xPos, (int) (y + 1.7F), ClickGui.getPrimaryColor().getRGB());
                y += 12;
            }
        }
        mc.getAmadeus().getModManager().getMods().sort((mod, mod1) -> {
            final String name = mod.getName();
            final String name2 = mod1.getName();
            return Integer.compare(mc.getAmadeus().getFontManager().comfortaa20.getStringWidth(name2), mc.getAmadeus().getFontManager().comfortaa20.getStringWidth(name));
        });
    }


    private int rainbow(int count, float bright, float st) {
        double v1 = Math.ceil((System.currentTimeMillis() + (count * 109))) / 5.0D;
        return Color.getHSBColor(((float) ((v1 %= 360.0D) / 360.0D) < 0.5D) ? -((float) (v1 / 360.0D)) : (float) (v1 / 360.0D), st, bright).getRGB();
    }

    private void oldArrayList() {
        ScaledResolution sr = new ScaledResolution(mc);
        int y = 0;
        for (Module m : mc.getAmadeus().getModManager().getMods()) {
            if (!m.isToggled() || m.getName().equals("Hud") || m.getName().equals("ClickGui"))
                continue;
            if (m.isToggled()) {
                //do animation module
                final float xPos = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName());
                mc.fontRendererObj.drawString(m.getName(), (int) xPos, (int) (y + 1.7F), ClickGui.getPrimaryColor().getRGB());
                y += 12;
            }
        }
        mc.getAmadeus().getModManager().getMods().sort((mod, mod1) -> {
            final String name = mod.getName();
            final String name2 = mod1.getName();
            return Integer.compare(mc.fontRendererObj.getStringWidth(name2), mc.fontRendererObj.getStringWidth(name));
        });
    }
}
