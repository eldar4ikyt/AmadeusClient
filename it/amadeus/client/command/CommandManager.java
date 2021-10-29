package it.amadeus.client.command;

import it.amadeus.client.command.list.bind;
import it.amadeus.client.command.list.help;
import it.amadeus.client.command.list.vclip;
import it.amadeus.client.utilities.ChatUtil;

import java.util.ArrayList;

public class CommandManager {
    private static final CommandManager theCommandManager = new CommandManager();

    private final ArrayList<Command> commands;

    public CommandManager() {
        this.commands = new ArrayList<>();
        Command[] arrayOfCommand;
        for (int j = (arrayOfCommand = commands()).length, i = 0; i < j; i++) {
            Command c = arrayOfCommand[i];
            if (c != null)
                this.commands.add(c);
        }
    }

    public static CommandManager getInstance() {
        return theCommandManager;
    }

    private Command[] commands() {
        return new Command[]{
                new bind(),
                new vclip(),
                new help(),
        };
    }

    public void callCommand(String input) {
        String[] split = input.split(" ");
        String command = split[0];
        String args = input.substring(command.length()).trim();
        for (Command c : getCommands()) {
            if (c.getAlias().equalsIgnoreCase(command)) {
                try {
                    c.onCommand(args, args.split(" "));
                } catch (Exception ignored) {
                }
                return;
            }
        }
        ChatUtil.print("Invalid Command.");
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }
}
