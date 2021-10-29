package it.amadeus.client.clickgui.util.values.valuetypes;

import it.amadeus.client.clickgui.util.values.Value;
import it.amadeus.client.module.Module;

public class NumberValue<T> extends Value<T> {

    private T minimumValue, maximumValue;

    public NumberValue(String valueName, T defaultValueObject, T minimumValue, T maximumValue, Module parent) {
        super(valueName, defaultValueObject, parent, null, "");
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    public T getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(T minimumValue) {
        this.minimumValue = minimumValue;
    }

    public T getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(T maximumValue) {
        this.maximumValue = maximumValue;
    }

}
