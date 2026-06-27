package com.ooowakaka.ooowakaka.feature.chain;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChainLockFeature extends Feature {
    public ChainLockFeature() {
        super("锁链锁定", "chainlock", "锁定在锁链上,按右shift紧急脱离", FeatureCategory.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null)
            mc.player.displayClientMessage(Component.literal("§7按 §b右Shift §7紧急脱离"), true);
    }
}
