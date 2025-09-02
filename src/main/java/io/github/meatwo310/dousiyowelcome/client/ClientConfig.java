package io.github.meatwo310.dousiyowelcome.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final int CURRENT_LAW_VERSION = 0;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue AGREED_LAW_VERSION = BUILDER
            .comment("The version of the law that the user has agreed to.")
            .defineInRange("agreedLawVersion", -1, -1, 0);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean hasAgreedToLaw() {
        return AGREED_LAW_VERSION.get() >= CURRENT_LAW_VERSION;
    }

    public static void agreeToLaw() {
        AGREED_LAW_VERSION.set(CURRENT_LAW_VERSION);
        SPEC.save();
    }
}
