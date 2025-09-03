package io.github.meatwo310.dousiyowelcome.client.screen;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.dousiyowelcome.client.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

// Code yoinked from Twilight Forest, via Supplementaries.
public class WelcomeMessageScreen extends Screen {
    public static final Logger LOGGER = LogUtils.getLogger();

    private final Screen lastScreen;
    private final List<Component> texts;
    @Nullable
    private final Component url;
    private final Runnable onTurnOff;
    private int ticksUntilEnable;
    private boolean urlOpened;
    private MultiLineLabel suggestions;

    private Button disableButton;

    public WelcomeMessageScreen(Screen screen, int ticksUntilEnable,
                                Component title, List<Component> texts, @Nullable Component url,
                                Runnable onTurnOff) {
        super(title);
        this.suggestions = MultiLineLabel.EMPTY;
        this.lastScreen = screen;
        this.ticksUntilEnable = ticksUntilEnable;
        this.texts = texts;
        this.url = url;
        this.onTurnOff = onTurnOff;
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return CommonComponents.joinForNarration(Stream.concat(
                Stream.of(super.getNarrationMessage()),
                texts.stream()
        ).toArray(Component[]::new));
    }

    @Override
    protected void init() {
        super.init();

        this.disableButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.dousiyowelcome.law.turn_off"), (pressed) -> {
                    Minecraft.getInstance().setScreen(this.lastScreen);
                    onTurnOff.run();
                }).bounds(this.width / 2 - 155, this.height * 5 / 6, 300, 20).build());
        this.disableButton.active = false;

        this.suggestions = url == null
                ? MultiLineLabel.EMPTY
                : MultiLineLabel.create(this.font, url, this.width - 50);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 30, 0xffffff);
        for (int i = 0; i < texts.size(); i++) {
            graphics.drawCenteredString(this.font, texts.get(i), this.width / 2, 55 + i * 9, 0xffffff);
        }
        this.suggestions.renderCentered(graphics, this.width / 2, 180);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.urlOpened) {
            return;
        }

        if (this.ticksUntilEnable > 0) {
            this.ticksUntilEnable--;
        }
        if (this.ticksUntilEnable <= 0) {
            this.disableButton.active = true;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.lastScreen);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pMouseY > 180.0 && pMouseY < 190.0 && this.url != null) {
            Style style = this.getClickedComponentStyleAt((int) pMouseX);
            if (url != null && style != null) {
                ClickEvent clickEvent = style.getClickEvent();
                if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    String value = clickEvent.getValue();
                    LOGGER.info("Opening url: {}", value);
                    try {
                        var uri = new URI(value);
                        Util.getPlatform().openUri(uri);
                    } catch (URISyntaxException uriSyntaxException) {
                        LOGGER.error("Can't open url for {}", value, uriSyntaxException);
                    }
                    this.urlOpened = true;
                    return false;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private @Nullable Style getClickedComponentStyleAt(int xPos) {
        int wid = Minecraft.getInstance().font.width(url);
        int left = this.width / 2 - wid / 2;
        int right = this.width / 2 + wid / 2;
        return xPos >= left && xPos <= right
                ? Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(url, xPos - left)
                : null;
    }

    private static final Component TITLE = Component
            .translatable("gui.dousiyowelcome.law.title")
            .withStyle(ChatFormatting.GOLD)
            .withStyle(ChatFormatting.BOLD);

    private static final List<Component> TEXTS = List.of(
            Component.translatable("gui.dousiyowelcome.law.message1"),
            Component.translatable("gui.dousiyowelcome.law.message2"),
            Component.translatable("gui.dousiyowelcome.law.message3"),
            Component.translatable("gui.dousiyowelcome.law.message4"),
            Component.translatable("gui.dousiyowelcome.law.message5")
    );

    private static final Component URL = Component
            .translatable("gui.dousiyowelcome.law.suggestions")
            .withStyle(Style.EMPTY
                    .withColor(ChatFormatting.GREEN)
                    .applyFormat(ChatFormatting.UNDERLINE)
                    .withClickEvent(new ClickEvent(
                            ClickEvent.Action.OPEN_URL,
                            I18n.get("gui.dousiyowelcome.law.url")
                    ))
            );

    public static WelcomeMessageScreen create(Screen screen) {
        return new WelcomeMessageScreen(
                screen,
                200,
                TITLE,
                TEXTS,
                URL,
                ClientConfig::agreeToLaw
        );
    }
}
