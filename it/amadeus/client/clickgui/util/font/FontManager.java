package it.amadeus.client.clickgui.util.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class FontManager {

    private final HashMap<String, HashMap<Float, UnicodeFontRenderer>> fonts = new HashMap<>();

    public UnicodeFontRenderer comfortaa10 = getFont("comfortaa", 10.0F);

    public UnicodeFontRenderer comfortaa15 = getFont("comfortaa", 15.0F);

    public UnicodeFontRenderer comfortaa16 = getFont("comfortaa", 16.0F);

    public UnicodeFontRenderer comfortaa17 = getFont("comfortaa", 17.0F);

    public UnicodeFontRenderer comfortaa18 = getFont("comfortaa", 18.0F);

    public UnicodeFontRenderer comfortaa20 = getFont("comfortaa", 20.0F);

    public UnicodeFontRenderer getFont(String name, float size) {
        UnicodeFontRenderer unicodeFont = null;
        try {
            if (this.fonts.containsKey(name) && this.fonts.get(name).containsKey(size))
                return this.fonts.get(name).get(size);
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/" + name + ".ttf")).getInputStream();

            Font font = null;
            font = Font.createFont(0, inputStream);
            unicodeFont = new UnicodeFontRenderer(font.deriveFont(size));
            unicodeFont.setUnicodeFlag(true);
            unicodeFont.setBidiFlag((Minecraft.getMinecraft()).getLanguageManager().isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> map = new HashMap<>();
            if (this.fonts.containsKey(name))
                map.putAll(this.fonts.get(name));
            map.put(size, unicodeFont);
            this.fonts.put(name, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unicodeFont;
    }

    public UnicodeFontRenderer getFont(String name, float size, boolean b) {
        UnicodeFontRenderer unicodeFont = null;
        try {
            if (this.fonts.containsKey(name) && this.fonts.get(name).containsKey(size))
                return (this.fonts.get(name)).get(size);
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("fonts/" + name + ".otf")).getInputStream();

            Font font = null;
            font = Font.createFont(0, inputStream);
            unicodeFont = new UnicodeFontRenderer(font.deriveFont(size));
            unicodeFont.setUnicodeFlag(true);
            unicodeFont.setBidiFlag((Minecraft.getMinecraft()).getLanguageManager().isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> map = new HashMap<>();
            if (this.fonts.containsKey(name))
                map.putAll(this.fonts.get(name));
            map.put(size, unicodeFont);
            this.fonts.put(name, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unicodeFont;
    }
}