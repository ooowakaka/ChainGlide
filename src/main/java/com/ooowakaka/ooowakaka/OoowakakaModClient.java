package com.ooowakaka.ooowakaka;

import com.ooowakaka.ooowakaka.feature.FeatureManager;
import com.ooowakaka.ooowakaka.feature.chain.ChainEmptyHandFeature;
import com.ooowakaka.ooowakaka.feature.chain.ChainLockFeature;
import com.ooowakaka.ooowakaka.feature.chain.ChainSpeedFeature;
import com.ooowakaka.ooowakaka.feature.chain.NoFallFeature;
import com.ooowakaka.ooowakaka.feature.chain.StraightDashFeature;
import com.ooowakaka.ooowakaka.hud.HudRenderer;
import com.ooowakaka.ooowakaka.keybind.KeyBindManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OoowakakaMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OoowakakaMod.MODID, value = Dist.CLIENT)
public class OoowakakaModClient {

    public OoowakakaModClient(ModContainer container) {
        FeatureManager.INSTANCE.register(new ChainSpeedFeature());
        FeatureManager.INSTANCE.register(new ChainLockFeature());
        FeatureManager.INSTANCE.register(new ChainEmptyHandFeature());
        FeatureManager.INSTANCE.register(new StraightDashFeature());
        FeatureManager.INSTANCE.register(new NoFallFeature());
        KeyBindManager.createKeyBindings();
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        OoowakakaMod.LOGGER.debug("client setup complete");
        NeoForge.EVENT_BUS.addListener(OoowakakaModClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(HudRenderer::onRenderGui);
        FeatureManager.INSTANCE.init();
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindManager.register(event);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        FeatureManager.INSTANCE.clientTick();
        KeyBindManager.onClientTick();
        HudRenderer.onClientTick();
    }
}
