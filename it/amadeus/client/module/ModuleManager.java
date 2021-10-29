package it.amadeus.client.module;

import it.amadeus.client.mods.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {

    @Getter
    private final List<Module> mods = new ArrayList<>(); //get all Mods

    public ModuleManager() {
        mods.add(new Killaura());//patch only this shit
        mods.add(new Speed());
        mods.add(new Sprint());
        mods.add(new Flight());
        mods.add(new Disabler());
        mods.add(new Velocity());
        mods.add(new Fucker());
        mods.add(new Spotify());
        mods.add(new PlayerESP());
        mods.add(new PingSpoof());
        mods.add(new Insulter());
        mods.add(new Hud());
        mods.add(new Click());
    }

    public Module getModuleByClass(final Class<?> clazz) {
        return mods.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null);
    }

    public Module getModuleByName(final String name) {
        return mods.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Prende I Moduli Per Categoria
     **/
    public ArrayList<Module> getModsByCat(Module.Category cat) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : getMods()) {
            if (m.getCategory() == cat) {
                mods.add(m);
            }
        }
        return mods;
    }

}