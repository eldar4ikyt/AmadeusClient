package it.amadeus.client.clickgui.util.values.valuetypes;

import it.amadeus.client.clickgui.util.values.Value;
import it.amadeus.client.module.Module;

import java.util.function.Supplier;


public class BooleanValue<T> extends Value<T> {
    public BooleanValue(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier) {
        super(valueName, defaultValueObject, parent, supplier);
    }

    public BooleanValue(String valueName, T defaultValueObject, Module parent) {
        super(valueName, defaultValueObject, parent, null);
    }
}
