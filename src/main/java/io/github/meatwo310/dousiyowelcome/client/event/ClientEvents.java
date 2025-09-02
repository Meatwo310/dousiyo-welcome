package io.github.meatwo310.dousiyowelcome.client.event;

import io.github.meatwo310.dousiyowelcome.DousiyoWelcome;
import io.github.meatwo310.dousiyowelcome.client.ClientConfig;
import io.github.meatwo310.dousiyowelcome.client.screen.WelcomeMessageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DousiyoWelcome.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean firstScreenShown = false;
    @SubscribeEvent
    public static void onScreenDrawPost(ScreenEvent.Init.Post event) {
        if (firstScreenShown || ClientConfig.hasAgreedToLaw() || !(event.getScreen() instanceof TitleScreen)) {
            return;
        }

        Minecraft.getInstance().setScreen(WelcomeMessageScreen.create(event.getScreen()));
        firstScreenShown = true;
    }
}
