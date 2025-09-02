package io.github.meatwo310.dousiyowelcome.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue AGREED_LAW_VERSION = BUILDER
            .comment("The version of the law that the user has agreed to.")
            .defineInRange("agreedLawVersion", 0, 0, 1);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
