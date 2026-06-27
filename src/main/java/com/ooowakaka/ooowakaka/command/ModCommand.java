package com.ooowakaka.ooowakaka.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ooowakaka.ooowakaka.OoowakakaMod;
import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.hud.HudRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.util.Locale;

@EventBusSubscriber(modid = OoowakakaMod.MODID, value = Dist.CLIENT)
public class ModCommand {

    @SubscribeEvent
    static void registerClientCommands(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ooowakaka");

        for (Feature f : FeatureManager.INSTANCE.getFeatures()) {
            LiteralArgumentBuilder<CommandSourceStack> sub = Commands.literal(f.getCommandKey());
            sub.then(Commands.literal("on").executes(ctx -> {
                f.setEnabled(true);
                ctx.getSource().sendSystemMessage(Component.literal("§a✔ " + f.getName() + " 已启用"));
                return 1;
            }));
            sub.then(Commands.literal("off").executes(ctx -> {
                f.setEnabled(false);
                ctx.getSource().sendSystemMessage(Component.literal("§c✘ " + f.getName() + " 已禁用"));
                return 1;
            }));
            f.addExtraCommands(sub);
            f.addInfoCommand(sub);
            root.then(sub);
        }

        root.then(Commands.literal("hud")
            .then(Commands.literal("on").executes(ctx -> {
                HudRenderer.setVisible(true);
                ctx.getSource().sendSystemMessage(Component.literal("§7HUD 已§a显示"));
                return 1;
            }))
            .then(Commands.literal("off").executes(ctx -> {
                HudRenderer.setVisible(false);
                ctx.getSource().sendSystemMessage(Component.literal("§7HUD 已§c隐藏"));
                return 1;
            }))
            .then(Commands.literal("color")
                .then(Commands.literal("pink").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.PINK);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §d粉色"));
                    return 1;
                }))
                .then(Commands.literal("blue").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.BLUE);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §9蓝色"));
                    return 1;
                }))
                .then(Commands.literal("green").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.GREEN);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §a绿色"));
                    return 1;
                }))
                .then(Commands.literal("cyan").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.CYAN);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §b青色"));
                    return 1;
                }))
                .then(Commands.literal("red").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.RED);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §c红色"));
                    return 1;
                }))
                .then(Commands.literal("purple").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.PURPLE);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 §5紫色"));
                    return 1;
                }))
                .then(Commands.literal("rainbow").executes(ctx -> {
                    HudRenderer.setTheme(HudRenderer.ColorTheme.RAINBOW);
                    ctx.getSource().sendSystemMessage(Component.literal("§7HUD 颜色已设为 彩虹"));
                    return 1;
                })))
            .then(Commands.literal("speed").then(Commands.argument("value", FloatArgumentType.floatArg(0.1f, 50f)).executes(ctx -> {
                float val = FloatArgumentType.getFloat(ctx, "value");
                HudRenderer.setColorSpeed(val);
                ctx.getSource().sendSystemMessage(Component.literal("§7颜色速度设为 §b" + String.format(Locale.ROOT, "%.1f", val) + "x"));
                return 1;
            })))
        );

        root.then(Commands.literal("list").executes(ctx -> {
            CommandSourceStack s = ctx.getSource();
            s.sendSystemMessage(Component.literal("§7═══ 功能列表 §7═══"));
            for (Feature f : FeatureManager.INSTANCE.getFeatures()) {
                String st = f.isEnabled() ? "§a✔" : "§c✘";
                s.sendSystemMessage(Component.literal("  " + st + " §f" + f.getName() + " §7(§b/ooowakaka " + f.getCommandKey() + "§7)"));
            }
            return 1;
        }));

        root.executes(ctx -> {
            CommandSourceStack s = ctx.getSource();
            s.sendSystemMessage(Component.literal("§7═══ ooowakaka §7═══"));
            for (Feature f : FeatureManager.INSTANCE.getFeatures())
                s.sendSystemMessage(Component.literal("§b/ooowakaka " + f.getCommandKey() + " on§7/§coff §b| info"));
            s.sendSystemMessage(Component.literal("§b/ooowakaka hud on§7/§coff §b| color | speed"));
            s.sendSystemMessage(Component.literal("§b/ooowakaka list"));
            return 1;
        });

        event.getDispatcher().register(root);
    }
}
