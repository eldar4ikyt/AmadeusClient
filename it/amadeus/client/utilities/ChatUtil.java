package it.amadeus.client.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void print(String text) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.DARK_RED + mc.getAmadeus().getNAME() + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.RED + text));
    }
}