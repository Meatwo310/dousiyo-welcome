package io.github.meatwo310.dousiyowelcome.client.screen;

import io.github.meatwo310.dousiyowelcome.client.ClientConfig;
import net.minecraft.ChatFormatting;
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

// Code yoinked from Twilight Forest, via Supplementaries.
public class WelcomeMessageScreen extends Screen {
    private final Screen lastScreen;
    private final Component text;
    @Nullable
    private final Component url;
    private final Runnable onTurnOff;
    private int ticksUntilEnable;
    private MultiLineLabel message;
    private MultiLineLabel suggestions;

    private Button disaleButton;

    public WelcomeMessageScreen(Screen screen, int ticksUntilEnable,
                                Component title, Component text, @Nullable Component url,
                                Runnable onTurnOff) {
        super(title);
        this.message = MultiLineLabel.EMPTY;
        this.suggestions = MultiLineLabel.EMPTY;
        this.lastScreen = screen;
        this.ticksUntilEnable = ticksUntilEnable;
        this.text = text;
        this.url = url;
        this.onTurnOff = onTurnOff;
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), text);
    }

    @Override
    protected void init() {
        super.init();

        this.disaleButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.dousiyowelcome.law.turn_off"), (pressed) -> {
                    Minecraft.getInstance().setScreen(this.lastScreen);
                    onTurnOff.run();
                }).bounds(this.width / 2 - 155, this.height * 5 / 6, 300, 20).build());
        this.disaleButton.active = false;

        this.message = MultiLineLabel.create(this.font, text, this.width - 50);
        this.suggestions = url == null
                ? MultiLineLabel.EMPTY
                : MultiLineLabel.create(this.font, url, this.width - 50);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 30, 16777215);
        this.message.renderCentered(graphics, this.width / 2, 55);
        this.suggestions.renderCentered(graphics, this.width / 2, 180);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (--this.ticksUntilEnable <= 0) {
            this.disaleButton.active = true;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.ticksUntilEnable <= 0;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.lastScreen);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pMouseY > 180.0 && pMouseY < 190.0 && this.url != null) {
            Style style = this.getClickedComponentStyleAt((int) pMouseX);
            if (url != null && style != null && style.getClickEvent() != null
                    && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL
            ) {
                this.handleComponentClicked(style);
                return false;
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

    private static final Component TEXT = Component
            .translatable("gui.dousiyowelcome.law.message");

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
                600,
                TITLE,
                TEXT,
                URL,
                ClientConfig::agreeToLaw
        );
    }
}
