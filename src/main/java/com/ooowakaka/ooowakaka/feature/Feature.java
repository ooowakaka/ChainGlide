package com.ooowakaka.ooowakaka.feature;

import com.ooowakaka.ooowakaka.feature.setting.Setting;
import com.ooowakaka.ooowakaka.hud.HudRenderer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Feature {

    protected final String name;
    protected final String commandKey;
    protected final String description;
    protected final FeatureCategory category;
    protected boolean enabled = false;
    protected final List<Setting<?>> settings = new ArrayList<>();

    protected Feature(String name, String commandKey, String description, FeatureCategory category) {
        this.name = name;
        this.commandKey = commandKey;
        this.description = description;
        this.category = category;
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
    protected void onSettingChanged(Setting<?> setting) {}

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
        FeatureManager.INSTANCE.saveAll();
        HudRenderer.updateState(this);
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected final void addSetting(Setting<?> setting) {
        setting.addChangeListener(val -> onSettingChanged(setting));
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() { return Collections.unmodifiableList(settings); }

    @SuppressWarnings("unchecked")
    public <T extends Setting<?>> T getSetting(String name) {
        for (Setting<?> s : settings) if (s.getName().equals(name)) return (T) s;
        return null;
    }

    public void resetAllSettings() {
        for (Setting<?> s : settings) s.reset();
    }

    public void addExtraCommands(LiteralArgumentBuilder<CommandSourceStack> root) {}

    public final void addInfoCommand(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("info").executes(ctx -> {
            String s = enabled ? "§a已启用" : "§c未启用";
            ctx.getSource().sendSystemMessage(Component.literal("§7═══ " + name + " §7═══"));
            ctx.getSource().sendSystemMessage(Component.literal("  状态: " + s));
            return 1;
        }));
    }

    public String getName() { return name; }
    public String getCommandKey() { return commandKey; }
    public String getDescription() { return description; }
    public FeatureCategory getCategory() { return category; }
}
