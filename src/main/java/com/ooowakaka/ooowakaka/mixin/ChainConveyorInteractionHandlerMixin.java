package com.ooowakaka.ooowakaka.mixin;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(targets = "com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler", remap = false)
public abstract class ChainConveyorInteractionHandlerMixin {

    private static boolean isEmptyHandEnabled() {
        Feature f = FeatureManager.INSTANCE.getByName("空手上链");
        return f != null && f.isEnabled();
    }

    @Redirect(method = "isActive", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHolding(Ljava/util/function/Predicate;)Z"))
    private static boolean redirectIsActive(LocalPlayer p, Predicate<ItemStack> pred) {
        return isEmptyHandEnabled() || p.isHolding(pred);
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHolding(Ljava/util/function/Predicate;)Z"))
    private static boolean redirectOnUse(LocalPlayer p, Predicate<ItemStack> pred) {
        return isEmptyHandEnabled() || p.isHolding(pred);
    }
}
