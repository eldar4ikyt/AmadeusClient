package it.amadeus.client.clickgui.util.values;

import it.amadeus.client.module.Module;

import java.awt.*;
import java.util.function.Supplier;


public class Value<T> {

    private final Supplier<?> supplier;
    public float HUE;
    public Color ColorPickerC;
    private String description = "";
    private String valueName;
    private T valueObject;
    private T defaultValueObject;
    private Module parent;
    private double id;
    private boolean ColorPicker;

    public Value(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier, String description) {
        this.supplier = supplier;
        this.valueName = valueName;
        this.description = description;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public Value(String valueName, T defaultValueObject, Module parent, Supplier<?> supplier) {
        this.supplier = supplier;
        this.valueName = valueName;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public Value(String valueName, T defaultValueObject, Module parent) {
        this.supplier = null;
        this.valueName = valueName;
        this.defaultValueObject = defaultValueObject;
        this.parent = parent;
        if (valueObject == null) {
            valueObject = defaultValueObject;
        }
        parent.addSetting(this);
    }

    public String getDescription() {
        return description;
    }

    public boolean checkDependants() {
        return supplier == null || (Boolean) supplier.get();
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public T getValue() {
        return valueObject;
    }

    public void setValueObject(T valueObject) {
        this.valueObject = valueObject;
    }

    public T getDefaultValueObject() {
        return defaultValueObject;
    }

    public void setDefaultValueObject(T defaultValueObject) {
        this.defaultValueObject = defaultValueObject;
    }

    public Module getParent() {
        return parent;
    }

    public void setParent(Module parent) {
        this.parent = parent;
    }
}
