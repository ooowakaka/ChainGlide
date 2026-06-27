package com.ooowakaka.ooowakaka.mixin;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Mixin(targets = "com.simibubi.create.foundation.render.PlayerSkyhookRenderer", remap = false)
public abstract class PlayerSkyhookRendererMixin {

    @Shadow(remap = false)
    private static Set<UUID> hangingPlayers;

    @Inject(method = "updatePlayerList", at = @At("TAIL"), remap = false)
    private static void onUpdatePlayerList(Collection<UUID> uuids, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Feature f = FeatureManager.INSTANCE.getByName("直线加速");
        if (f != null && f.isEnabled()) {
            hangingPlayers.add(mc.player.getUUID());
        } else {
            hangingPlayers.remove(mc.player.getUUID());
        }
    }
}
