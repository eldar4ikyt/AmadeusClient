package it.amadeus.client.mods;

import it.amadeus.client.clickgui.ClickGui;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Overlay;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class Hud extends Module {

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
            mc.getTextureManager().bindTexture(new ResourceLocation("amadeus/Hud.png"));
            Gui.drawModalRectWithCustomSizedTexture(1, 1, 0.0F, 0.0F, 126, 66, 126.0F, 66.0F);
            newArrayList();
        }
    }

    private void newArrayList() {
        ScaledResolution sr = new ScaledResolution(mc);
        int y = 0;
        for (Module m : mc.getAmadeus().getModManager().getMods()) {
            if (!m.isToggled() || m.getName().equals("Hud") || m.getName().equals("ClickGui"))
                continue;
            if (m.isToggled()) {
                //do animation module
                final float xPos = sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName());
                mc.fontRendererObj.drawString(m.getName(), (int)xPos, (int)(y + 1.7F), ClickGui.getPrimaryColor().getRGB());
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
