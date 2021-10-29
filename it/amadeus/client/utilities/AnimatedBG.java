package it.amadeus.client.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class AnimatedBG extends GuiScreen {
    public static int delay = 0;
    public static int Frames = 2;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation[] backgroundResources = new ResourceLocation[Frames];
    private final TimerUtil timer = new TimerUtil();

    public AnimatedBG() {
        for (int i = 0; i < this.backgroundResources.length; i++) {
            this.backgroundResources[i] = new ResourceLocation("amadeus/stains/sex " + (i + 1) + ".jpg");
        }
    }

    public void BackgroundAnimated() {
        if (this.backgroundResources.length <= delay) {
            delay = 0;
        }
        delay++;
        this.mc.getTextureManager().bindTexture(this.backgroundResources[delay - 1]);
    }
}
