package it.amadeus.client.command.list;


import it.amadeus.client.command.Command;
import it.amadeus.client.utilities.ChatUtil;

public class help extends Command {

    public String getAlias() {
        return "help";
    }

    public String getDescription() {
        return "show all commands";
    }

    public String getSyntax() {
        return ".help";
    }

    public void onCommand(String command, String[] args) throws Exception {
        for (Command com : mc.getAmadeus().getCommandManager().getCommands()) {
            ChatUtil.print("[C] " + com.getAlias() + " - " + com.getDescription());
        }
    }
}
