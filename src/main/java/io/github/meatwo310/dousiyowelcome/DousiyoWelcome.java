package io.github.meatwo310.dousiyowelcome;

import io.github.meatwo310.dousiyowelcome.client.ClientConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DousiyoWelcome.MODID)
public class DousiyoWelcome {
    public static final String MODID = "dousiyowelcome";

    public DousiyoWelcome(FMLJavaModLoadingContext ctx) {
        ctx.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
