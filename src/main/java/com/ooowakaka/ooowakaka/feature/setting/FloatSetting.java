package com.ooowakaka.ooowakaka.feature.setting;

import java.util.Locale;

public class FloatSetting extends Setting<Float> {
    private final float min, max, step;

    public FloatSetting(String name, String description, float defaultValue, float min, float max, float step) {
        super(name, description, defaultValue);
        this.min = min; this.max = max; this.step = step;
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getStep() { return step; }

    @Override public void setValue(Float value) { super.setValue(clamp(value)); }
    public float getProgress() { return (value - min) / (max - min); }
    public void setFromProgress(float p) { setValue(min + p * (max - min)); }

    @Override public String getDisplayValue() {
        if (step >= 1.0f) return String.format(Locale.ROOT, "%.0f", value);
        if (step >= 0.1f) return String.format(Locale.ROOT, "%.1f", value);
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override public Object serialize() { return value; }
    @Override public void deserialize(Object data) { if (data instanceof Number n) this.value = clamp(n.floatValue()); }

    private float clamp(float v) { return Math.max(min, Math.min(max, v)); }
}
