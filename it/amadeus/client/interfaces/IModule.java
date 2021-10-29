package it.amadeus.client.interfaces;

import it.amadeus.client.module.Module;

public interface IModule {

    String getName(); //return name of module

    String getDescription(); //return description of module

    int getKey(); //return keybind

    Module.Category getCategory(); //return category
}
