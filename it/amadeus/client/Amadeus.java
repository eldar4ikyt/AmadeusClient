package it.amadeus.client;

import it.amadeus.client.clickgui.util.font.FontManager;
import it.amadeus.client.command.CommandManager;
import it.amadeus.client.event.EventManager;
import it.amadeus.client.module.ModuleManager;
import it.amadeus.client.utilities.FileUtil;
import it.amadeus.client.viamcp.ViaMCP;
import lombok.Getter;
import org.lwjgl.opengl.Display;

public final class Amadeus {

    @Getter
    private final String VERSION = "0.1", NAME = "Amadeus", DEVELOPER = "AdrianCode";

    @Getter
    private FontManager fontManager;
    @Getter
    private EventManager eventManager;
    @Getter
    private ModuleManager modManager;
    @Getter
    private CommandManager commandManager;

    public strictfp void loadClient() {
        Display.setTitle(NAME + " | " + VERSION);

        try{
            ViaMCP.getInstance().start();
            FileUtil.LoadSpotify();
        }catch (Exception e){
            e.printStackTrace();
        }

        fontManager = new FontManager();

        eventManager = new EventManager();
        eventManager.setupListeners();

        modManager = new ModuleManager();
        commandManager = new CommandManager();

        System.runFinalization();
        System.gc();
    }
}