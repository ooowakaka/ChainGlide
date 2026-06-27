package com.ooowakaka.ooowakaka.mixin;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.feature.setting.FloatSetting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(targets = "com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRidingHandler", remap = false)
public abstract class ChainConveyorRidingHandlerMixin {

    @ModifyConstant(method = "updateTargetPosition", constant = @Constant(floatValue = 360.0f, ordinal = 0))
    private static float modifySpeedDivisor(float original) {
        Feature f = FeatureManager.INSTANCE.getByName("锁链速度");
        if (f == null || !f.isEnabled()) return original;
        FloatSetting m = f.getSetting("速度倍率");
        if (m == null) return original;
        float val = m.getValue();
        return (val <= 0f || val == 1f) ? original : original / val;
    }

    private static boolean isChainLockEnabled() {
        Feature f = FeatureManager.INSTANCE.getByName("锁链锁定");
        return f != null && f.isEnabled();
    }

    @Redirect(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isShiftKeyDown()Z"))
    private static boolean redirectShiftCheck(LocalPlayer player) {
        if (isChainLockEnabled()) return isRightShiftDown();
        return player.isShiftKeyDown();
    }

    @Redirect(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHolding(Ljava/util/function/Predicate;)Z"))
    private static boolean redirectHoldingCheck(LocalPlayer player, Predicate<ItemStack> p) {
        if (isChainLockEnabled()) return true;
        Feature eh = FeatureManager.INSTANCE.getByName("空手上链");
        if (eh != null && eh.isEnabled()) return true;
        return player.isHolding(p);
    }

    @ModifyConstant(method = "clientTick", constant = @Constant(doubleValue = 3.0, ordinal = 0))
    private static double modifyDistanceTolerance(double original) {
        return isChainLockEnabled() ? 1000.0 : original;
    }

    @ModifyConstant(method = "clientTick", constant = @Constant(doubleValue = -1.0, ordinal = 0))
    private static double modifyYTolerance(double original) {
        return isChainLockEnabled() ? -1000.0 : original;
    }

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private static void onStopRiding(CallbackInfo ci) {
        if (isChainLockEnabled() && !isRightShiftDown()) ci.cancel();
    }

    private static boolean isRightShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_RSHIFT);
    }
}
