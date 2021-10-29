package it.amadeus.client.command.list;

import it.amadeus.client.command.Command;
import it.amadeus.client.utilities.ChatUtil;
import net.minecraft.util.EnumChatFormatting;

public class vclip extends Command {

    @Override
    public String getAlias() {
        return "vclip";
    }

    @Override
    public String getDescription() {
        return "this is a simple tp";
    }

    @Override
    public String getSyntax() {
        return ".vclip <Block>";
    }

    @Override
    public void onCommand(String comando, String[] args) throws Exception {
        if (comando.isEmpty()) {
            ChatUtil.print(EnumChatFormatting.RED + "Wrong Format, try: " + getSyntax());
        }
        int block = Integer.parseInt(args[0]);
        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + block, mc.thePlayer.posZ);
        ChatUtil.print("Teleported to " + block);
    }
}