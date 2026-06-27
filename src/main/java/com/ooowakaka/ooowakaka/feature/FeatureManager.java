package com.ooowakaka.ooowakaka.feature;

import com.ooowakaka.ooowakaka.config.ConfigStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FeatureManager {

    public static final FeatureManager INSTANCE = new FeatureManager();

    private final List<Feature> features = new ArrayList<>();
    private boolean initialized;

    private FeatureManager() {}

    public void register(Feature feature) {
        if (features.contains(feature)) return;
        features.add(feature);
    }

    public void init() {
        if (initialized) return;
        initialized = true;
        ConfigStore.load();
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    public Feature getByName(String name) {
        for (Feature f : features) if (f.getName().equals(name)) return f;
        return null;
    }

    public void clientTick() {
        for (Feature f : features) if (f.isEnabled()) f.onTick();
    }

    public void saveAll() {
        ConfigStore.save();
    }
}
