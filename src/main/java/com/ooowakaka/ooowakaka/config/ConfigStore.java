package com.ooowakaka.ooowakaka.config;

import com.google.gson.*;
import com.ooowakaka.ooowakaka.OoowakakaMod;
import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.feature.setting.Setting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class ConfigStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;

    private ConfigStore() {}

    public static void init(Path gameDir) {
        configPath = gameDir.resolve("config").resolve("chainglide.json");
    }

    public static void save() {
        if (configPath == null) return;
        JsonObject root = new JsonObject();
        for (Feature f : FeatureManager.INSTANCE.getFeatures()) {
            JsonObject fj = new JsonObject();
            fj.addProperty("enabled", f.isEnabled());
            JsonObject sj = new JsonObject();
            for (Setting<?> s : f.getSettings()) {
                Object v = s.serialize();
                if (v instanceof Number n) sj.addProperty(s.getName(), n);
                else if (v instanceof Boolean b) sj.addProperty(s.getName(), b);
                else if (v instanceof String str) sj.addProperty(s.getName(), str);
            }
            fj.add("settings", sj);
            root.add(f.getName(), fj);
        }
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(root), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) { OoowakakaMod.LOGGER.error("save config", e); }
    }

    public static void load() {
        if (configPath == null || !Files.exists(configPath)) return;
        try {
            JsonObject root = GSON.fromJson(Files.readString(configPath), JsonObject.class);
            if (root == null) return;
            for (Feature f : FeatureManager.INSTANCE.getFeatures()) {
                JsonElement fe = root.get(f.getName());
                if (fe == null || !fe.isJsonObject()) continue;
                JsonObject fj = fe.getAsJsonObject();
                JsonElement ee = fj.get("enabled");
                if (ee != null && ee.isJsonPrimitive()) f.setEnabled(ee.getAsBoolean());
                JsonElement se = fj.get("settings");
                if (se == null || !se.isJsonObject()) continue;
                JsonObject sj = se.getAsJsonObject();
                for (Setting<?> s : f.getSettings()) {
                    JsonElement ve = sj.get(s.getName());
                    if (ve == null || !ve.isJsonPrimitive()) continue;
                    JsonPrimitive p = ve.getAsJsonPrimitive();
                    if (p.isNumber()) s.deserialize(p.getAsNumber());
                    else if (p.isBoolean()) s.deserialize(p.getAsBoolean());
                    else if (p.isString()) s.deserialize(p.getAsString());
                }
            }
        } catch (IOException e) { OoowakakaMod.LOGGER.error("load config", e); }
    }
}
