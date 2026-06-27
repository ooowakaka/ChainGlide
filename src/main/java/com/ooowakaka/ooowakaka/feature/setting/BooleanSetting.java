package com.ooowakaka.ooowakaka.feature.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }
    @Override public String getDisplayValue() { return value ? "§aON" : "§cOFF"; }
    public void toggle() { setValue(!value); }
    @Override public Object serialize() { return value; }
    @Override public void deserialize(Object data) { if (data instanceof Boolean b) this.value = b; }
}
