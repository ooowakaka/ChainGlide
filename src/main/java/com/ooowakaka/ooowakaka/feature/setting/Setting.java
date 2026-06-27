package com.ooowakaka.ooowakaka.feature.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Setting<T> {

    protected final String name;
    protected final String description;
    protected T value;
    protected final T defaultValue;
    private final List<Consumer<T>> changeListeners = new ArrayList<>();

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public T getDefaultValue() { return defaultValue; }

    public void setValue(T value) {
        T old = this.value;
        this.value = value;
        if (!value.equals(old)) notifyListeners();
    }

    public void reset() { setValue(defaultValue); }
    public boolean isModified() { return !value.equals(defaultValue); }

    public void addChangeListener(Consumer<T> listener) { changeListeners.add(listener); }
    protected void notifyListeners() { for (Consumer<T> l : changeListeners) l.accept(value); }

    public abstract Object serialize();
    @SuppressWarnings("unchecked")
    public abstract void deserialize(Object data);
    public abstract String getDisplayValue();

    @Override
    public String toString() { return name + " = " + value; }
}
