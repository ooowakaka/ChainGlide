package com.ooowakaka.ooowakaka.feature.setting;

import java.util.Arrays;
import java.util.List;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] values;
    private int index;

    @SafeVarargs
    public EnumSetting(String name, String description, E defaultValue, E... values) {
        super(name, description, defaultValue);
        this.values = values;
        this.index = indexOf(defaultValue);
    }

    public List<E> getValues() { return Arrays.asList(values); }
    public int getIndex() { return index; }
    public void setIndex(int idx) {
        int newIdx = Math.max(0, Math.min(idx, values.length - 1));
        setValue(values[newIdx]);
    }
    @Override public void setValue(E value) {
        int idx = indexOf(value);
        E old = this.value;
        this.index = idx;
        this.value = values[idx];
        if (!values[idx].equals(old)) notifyListeners();
    }
    public void next() { index = (index + 1) % values.length; setValue(values[index]); }
    public void prev() { index = (index - 1 + values.length) % values.length; setValue(values[index]); }
    @Override public String getDisplayValue() { return value.name(); }
    @Override public Object serialize() { return value.name(); }
    @Override public void deserialize(Object data) {
        if (data instanceof String s) for (int i = 0; i < values.length; i++) if (values[i].name().equals(s)) { setIndex(i); return; }
    }
    private int indexOf(E val) { for (int i = 0; i < values.length; i++) if (values[i] == val) return i; return 0; }
}
