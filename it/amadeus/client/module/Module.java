package it.amadeus.client.module;

import it.amadeus.client.clickgui.util.values.Value;
import it.amadeus.client.event.Handler;
import it.amadeus.client.interfaces.IModule;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Module implements Handler, IModule {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final ArrayList<Value> settings = new ArrayList<>();

    private String name, description;

    private int key;

    private Category category;

    private boolean toggled;

    public void onEnable() {
        mc.getAmadeus().getEventManager().addListener(this);
    }

    public void onDisable() {
        mc.getAmadeus().getEventManager().removeListener(this);
    }

    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void addSetting(Value<?> value) {
        this.settings.add(value);
    }

    public enum Category {FIGHT, MOVEMENTS, RENDER, FUN}
}