package it.amadeus.client.command;

import it.amadeus.client.interfaces.ICommand;
import net.minecraft.client.Minecraft;

public abstract class Command implements ICommand {
    protected final Minecraft mc = Minecraft.getMinecraft();
}
