package it.amadeus.client.command.list;

import it.amadeus.client.command.Command;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import org.lwjgl.input.Keyboard;

public class bind extends Command {

    public String getAlias() {
        return "bind";
    }

    public String getDescription() {
        return "set keybinds.";
    }

    public String getSyntax() {
        return ".bind <Module> <Key>";
    }

    public void onCommand(String command, String[] args) throws Exception {
        if (command.isEmpty()) {
            ChatUtil.print("§cWrong Format, try: " + getSyntax());
        }
        Module m = mc.getAmadeus().getModManager().getModuleByName(args[0]);
        m.setKey(Keyboard.getKeyIndex(args[1].toUpperCase()));
        ChatUtil.print(args[0] + " was bind to " +args[1]);
    }
}
