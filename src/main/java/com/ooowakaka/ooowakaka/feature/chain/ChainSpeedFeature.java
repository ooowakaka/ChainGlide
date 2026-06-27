package com.ooowakaka.ooowakaka.feature.chain;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureCategory;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.feature.setting.FloatSetting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class ChainSpeedFeature extends Feature {

    public final FloatSetting speedMultiplier;

    public ChainSpeedFeature() {
        super("锁链速度", "chainspeed", "修改锁链移动速度倍率", FeatureCategory.MOVEMENT);
        speedMultiplier = new FloatSetting("速度倍率", "原版速度的倍率", 1.0f, 0.1f, 50.0f, 0.1f);
        addSetting(speedMultiplier);
    }

    @Override
    public void addExtraCommands(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("speed")
            .then(Commands.argument("multiplier", FloatArgumentType.floatArg(0.1f, 50.0f))
                .executes(ctx -> {
                    float val = FloatArgumentType.getFloat(ctx, "multiplier");
                    speedMultiplier.setValue(val);
                    FeatureManager.INSTANCE.saveAll();
                    ctx.getSource().sendSystemMessage(
                        Component.literal("§7锁链速度倍率设置为 §b" + String.format(Locale.ROOT, "%.1f", val) + "x"));
                    return 1;
                })));
        root.then(Commands.literal("reset").executes(ctx -> {
            speedMultiplier.reset();
            FeatureManager.INSTANCE.saveAll();
            ctx.getSource().sendSystemMessage(
                Component.literal("§7锁链速度已恢复默认值 §b" + speedMultiplier.getDisplayValue() + "x"));
            return 1;
        }));
    }
}
