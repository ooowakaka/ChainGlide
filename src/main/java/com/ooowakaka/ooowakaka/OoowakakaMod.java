package com.ooowakaka.ooowakaka;

import com.mojang.logging.LogUtils;
import com.ooowakaka.ooowakaka.config.ConfigStore;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(OoowakakaMod.MODID)
public class OoowakakaMod {
    public static final String MODID = "chainglide";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OoowakakaMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.debug("{} loaded", MODID);
        ConfigStore.init(FMLPaths.GAMEDIR.get());
    }
}
