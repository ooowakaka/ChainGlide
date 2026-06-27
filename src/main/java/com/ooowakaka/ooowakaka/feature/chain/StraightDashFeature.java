package com.ooowakaka.ooowakaka.feature.chain;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureCategory;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.feature.setting.FloatSetting;
import com.ooowakaka.ooowakaka.util.CreateReflect;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public class StraightDashFeature extends Feature {

    public final FloatSetting speed;
    private BlockPos conveyorPos;
    private int tickCounter;

    public StraightDashFeature() {
        super("虚空锁链", "straightdash", "虚空悬挂并向视角方向冲刺", FeatureCategory.MOVEMENT);
        speed = new FloatSetting("速度", "冲刺速度", 1.0f, 0.1f, 50.0f, 0.1f);
        addSetting(speed);
    }

    @Override
    protected void onEnable() {
        tickCounter = 0;
        conveyorPos = scanConveyor();
    }

    @Override
    protected void onDisable() {
        if (conveyorPos != null) {
            CreateReflect.sendStopPacket(conveyorPos);
            conveyorPos = null;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.fallDistance = 0f;

        if (conveyorPos != null && tickCounter % 100 == 0) {
            BlockPos fresh = scanConveyor();
            if (fresh != null && !fresh.equals(conveyorPos)) {
                CreateReflect.sendStopPacket(conveyorPos);
                conveyorPos = fresh;
            }
        }

        if (conveyorPos != null && tickCounter % 10 == 0)
            CreateReflect.sendRidingPacket(conveyorPos);
        tickCounter++;

        Vec3 look = mc.player.getLookAngle();
        mc.player.setDeltaMovement(look.scale(speed.getValue()));
    }

    @Override
    public void addExtraCommands(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("speed")
            .then(Commands.argument("value", FloatArgumentType.floatArg(0.1f, 50.0f))
                .executes(ctx -> {
                    float val = FloatArgumentType.getFloat(ctx, "value");
                    speed.setValue(val);
                    FeatureManager.INSTANCE.saveAll();
                    ctx.getSource().sendSystemMessage(
                        Component.literal("§7直线加速速度设置为 §b" + String.format(Locale.ROOT, "%.1f", val) + "x"));
                    return 1;
                })));
        root.then(Commands.literal("reset").executes(ctx -> {
            speed.reset();
            FeatureManager.INSTANCE.saveAll();
            ctx.getSource().sendSystemMessage(
                Component.literal("§7直线加速已恢复默认速度 §b" + speed.getDisplayValue() + "x"));
            return 1;
        }));
    }

    private static BlockPos scanConveyor() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return null;
        BlockPos center = mc.player.blockPosition();
        for (int r = 0; r <= 32; r++)
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;
                    for (int dy = -8; dy <= 8; dy++) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        BlockState state = mc.level.getBlockState(pos);
                        if (state.isAir()) continue;
                        if (state.getBlock().getClass().getSimpleName().equals("ChainConveyorBlock"))
                            return pos.immutable();
                    }
                }
        return null;
    }
}
