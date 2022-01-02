package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Overlay;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class OnlyFans extends Module {

    private final String HANNA_Path_1 = "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/HannahOWO/1/";
    private final String HANNA_Path_2 = "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/HannahOWO/2/";
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.HannaOWO, this);
    private final String[] Viviolix = {
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Viviolix/photo_2021-09-15_12-06-35.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Viviolix/photo_2021-09-15_12-05-56.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Viviolix/photo_2021-09-15_12-06-36.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Viviolix/photo_2021-12-04_13-36-55.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Viviolix/puttana.jpg"};
    private final String[] Crisafulli = {
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-10-11_12-44-15.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-10-14_23-28-06.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-11-05_18-36-02.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-11-13_14-39-59.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-11-13_14-40-42.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-11-13_14-40-48.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-25.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-36.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-39.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-44.jpg ",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-48.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-50.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-53.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-56.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-35-59.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-02.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-10.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-13.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-16.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-19.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-22.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-28.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-30.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-36.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-39.jpg",
            "https://raw.githubusercontent.com/ChristopherProject/HostFile/main/Crisafulli/photo_2021-12-07_00-36-52.jpg"};
    private final String[] LucyLein = {"https://i.imgur.com/6pKFoSK.jpeg"};
    private final String[] HannaOWO = {
            HANNA_Path_1 + "photo_2021-10-05_14-34-13.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-15.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-16.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-19.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-25.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-26.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-28.jpg",
            HANNA_Path_1 + "photo_2021-10-05_14-34-29.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-54-42.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-54-50.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-54-52.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-54-54.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-55-00.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-55-04.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-55-06.jpg",
            HANNA_Path_1 + "photo_2021-12-04_16-55-09.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-31.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-34.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-37.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-39.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-42.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-45.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-47.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-49.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-52.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-55.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-57.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-05-59.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-06-06.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-06-09.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-06-12.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-06-14.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-19.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-21.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-24.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-27.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-29.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-32.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-34.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-36.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-39.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-43.jpg",
            HANNA_Path_1 + "photo_2021-12-04_19-07-45.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-12.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-20.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-38.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-42.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-46.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-50.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-53.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-55-56.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-56-01.jpg",
            HANNA_Path_2 + "photo_2021-12-04_16-56-07.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-03-13.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-04-57.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-01.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-03.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-07.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-10.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-19.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-22.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-24.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-27.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-05-29.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-17.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-19.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-22.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-24.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-28.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-31.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-33.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-36.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-41.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-44.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-47.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-49.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-54.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-56.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-06-58.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-03.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-05.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-08.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-11.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-14.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-07-17.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-02.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-05.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-07.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-11.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-14.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-16.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-19.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-21.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-25.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-25.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-30.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-32.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-34.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-37.jpg",
            HANNA_Path_2 + "photo_2021-12-04_19-08-40.jpg"
    };
    private BufferedImage coverImageBuffer;
    private ResourceLocation coverImage;

    @Override
    public String getName() {
        return "OnlyFans";
    }

    @Override
    public String getDescription() {
        return "Only Fans Shower";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.RENDER;
    }

    private void update() {
        mc.addScheduledTask(() -> {
            DynamicTexture dynamicTexture = new DynamicTexture(coverImageBuffer);
            coverImage = mc.getTextureManager().getDynamicTextureLocation("onlyfans.jpg", dynamicTexture);
        });
    }

    @Override
    public void onEnable() {
        try {
            switch (mode.getValue()) {
                case Viviolix:
                    coverImageBuffer = ImageIO.read(new URL(Viviolix[0]));
                    break;
                case Crisafulli:
                    coverImageBuffer = ImageIO.read(new URL(Crisafulli[0]));
                    break;
                case LucyLein:
                    coverImageBuffer = ImageIO.read(new URL(LucyLein[0]));
                    break;
                case HannaOWO:
                    coverImageBuffer = ImageIO.read(new URL(HannaOWO[0]));
                    break;
            }
            update();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        coverImageBuffer = null;
        coverImage = null;
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof Update) {//145
            if (mc.thePlayer.ticksExisted % 45 == 0) {
                mc.addScheduledTask(() -> {
                    coverImageBuffer = null;
                    coverImage = null;
                    try {
                        switch (mode.getValue()) {
                            case Viviolix:
                                coverImageBuffer = ImageIO.read(new URL(Viviolix[RandomUtils.nextInt(0, Viviolix.length)]));
                                break;
                            case Crisafulli:
                                coverImageBuffer = ImageIO.read(new URL(Crisafulli[RandomUtils.nextInt(0, Crisafulli.length)]));
                                break;
                            case LucyLein:
                                coverImageBuffer = ImageIO.read(new URL(LucyLein[RandomUtils.nextInt(0, LucyLein.length)]));
                                break;
                            case HannaOWO:
                                coverImageBuffer = ImageIO.read(new URL(HannaOWO[RandomUtils.nextInt(0, HannaOWO.length)]));
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    update();
                });
            }
        }
        if (event instanceof Overlay) {
            final int width = 115;
            final int height = 158;
            if (coverImage != null && coverImageBuffer != null) {
                GlStateManager.color(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(coverImage);
                Gui.drawScaledCustomSizeModalRect(7, 135, 0.0F, 0.0F, width, height, width, height, (float) width, (float) height);
                GlStateManager.color(1, 1, 1, 1);
            }
        }
    }

    public enum Mode {HannaOWO, Viviolix, Crisafulli, LucyLein}
}