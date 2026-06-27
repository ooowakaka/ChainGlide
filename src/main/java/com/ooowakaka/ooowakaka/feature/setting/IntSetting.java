package com.ooowakaka.ooowakaka.feature.setting;

public class IntSetting extends Setting<Integer> {
    private final int min, max;

    public IntSetting(String name, String description, int defaultValue, int min, int max) {
        super(name, description, defaultValue);
        this.min = min; this.max = max;
    }

    public int getMin() { return min; }
    public int getMax() { return max; }

    @Override public void setValue(Integer value) { super.setValue(Math.max(min, Math.min(max, value))); }
    public float getProgress() { return (float)(value - min) / (float)(max - min); }
    public void setFromProgress(float p) { setValue(Math.round(min + p * (max - min))); }
    @Override public String getDisplayValue() { return String.valueOf(value); }
    @Override public Object serialize() { return value; }
    @Override public void deserialize(Object data) { if (data instanceof Number n) this.value = Math.max(min, Math.min(max, n.intValue())); }
}
