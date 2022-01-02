package it.amadeus.client;

import it.amadeus.client.clickgui.util.font.FontManager;
import it.amadeus.client.command.CommandManager;
import it.amadeus.client.event.EventManager;
import it.amadeus.client.module.ModuleManager;
import it.amadeus.client.utilities.Blurrer;
import it.amadeus.client.utilities.FileUtil;
import it.amadeus.client.utilities.Revenge;
import it.amadeus.client.viamcp.ViaMCP;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.Display;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public final class Amadeus {

    private final String VERSION = "0.1", NAME = "Amadeus", DEVELOPER = "AdrianCode";
    private FontManager fontManager;
    private EventManager eventManager;
    private ModuleManager modManager;
    private CommandManager commandManager;
    private Blurrer blurrer;
    @Setter private boolean last;

    public strictfp void loadClient() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(System::gc, 0L, 5L, TimeUnit.MINUTES);
        System.out.println("[Amadeus] vivi nel labirinto di tutta la tristezza e sofferenza");
        System.out.println("[Amadeus] un esperimento si rivelò un fallimento");
        System.out.println("[Amadeus] condividiamo lo stesso destino...");
        Display.setTitle("(instable) " + NAME + " | " + VERSION);
        try {
            ViaMCP.getInstance().start();
            FileUtil.LoadSpotify();
        } catch (Exception e) {
            e.printStackTrace();
        }
        blurrer = new Blurrer(false);
        fontManager = new FontManager();
        eventManager = new EventManager();
        eventManager.setupListeners();
        modManager = new ModuleManager();
        commandManager = new CommandManager();
        last = true;
        System.runFinalization();
        System.gc();
    }
}