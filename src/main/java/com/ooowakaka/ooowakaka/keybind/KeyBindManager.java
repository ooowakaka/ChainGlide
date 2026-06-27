package com.ooowakaka.ooowakaka.keybind;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import java.util.ArrayList;
import java.util.List;

public final class KeyBindManager {

    private static final String CATEGORY = "key.categories.chainglide";
    private static final List<KeyMapping> keyMappings = new ArrayList<>();

    private KeyBindManager() {}

    public static void createKeyBindings() {
        keyMappings.clear();
        for (Feature f : FeatureManager.INSTANCE.getFeatures())
            keyMappings.add(new KeyMapping(f.getName(), -1, CATEGORY));
    }

    public static void register(RegisterKeyMappingsEvent event) {
        for (KeyMapping km : keyMappings) event.register(km);
    }

    public static void onClientTick() {
        List<Feature> features = FeatureManager.INSTANCE.getFeatures();
        for (int i = 0; i < keyMappings.size() && i < features.size(); i++)
            if (keyMappings.get(i).consumeClick()) features.get(i).toggle();
    }
}
