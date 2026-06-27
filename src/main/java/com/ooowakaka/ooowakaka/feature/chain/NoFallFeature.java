package com.ooowakaka.ooowakaka.feature.chain;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFallFeature extends Feature {
    public NoFallFeature() {
        super("无摔落伤害", "nofall", "免疫摔落伤害", FeatureCategory.MOVEMENT);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.isCreative()) return;
        mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true));
    }
}
