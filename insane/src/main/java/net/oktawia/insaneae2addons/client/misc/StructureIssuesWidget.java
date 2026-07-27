package net.oktawia.insaneae2addons.client.misc;

import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StructureIssuesWidget extends AbstractWidget {

    private static final int PAD = 8;
    private static final int LINE_GAP = 2;

    private static final int PANEL_BACKGROUND = 0xFF111217;
    private static final int PANEL_LIGHT = 0xFF2A2D3A;
    private static final int PANEL_DARK = 0xFF07080C;

    private static final int TITLE_COLOR = 0xFFB4B4;
    private static final int TEXT_COLOR = 0xEDEDED;
    private static final int DETAIL_COLOR = 0x9A9AA6;

    @Setter
    private Component title = Component.empty();

    @Setter
    private Component hint = Component.empty();

    @Setter
    private List<Component> lines = List.of();

    public StructureIssuesWidget() {
        super(0, 0, 0, 0, Component.empty());
        this.active = true;
        this.visible = true;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible || this.width <= PAD * 2 || this.height <= PAD * 2) {
            return;
        }

        drawPanel(graphics);

        Font font = Minecraft.getInstance().font;
        int x = getX() + PAD;
        int y = getY() + PAD;
        int maxY = getY() + this.height - PAD;
        int step = font.lineHeight + LINE_GAP;

        graphics.drawString(font, this.title.copy().withStyle(ChatFormatting.BOLD), x, y, TITLE_COLOR, false);
        y += step;

        graphics.drawString(font, this.hint, x, y, DETAIL_COLOR, false);
        y += step + LINE_GAP * 2;

        for (Component line : this.lines) {
            if (y + step > maxY) {
                break;
            }

            graphics.drawString(font, line, x, y, TEXT_COLOR, false);
            y += step;
        }
    }

    private void drawPanel(GuiGraphics graphics) {
        int x = getX();
        int y = getY();

        graphics.fill(x + 2, y + 2, x + this.width + 2, y + this.height + 2, 0x55000000);
        graphics.fill(x, y, x + this.width, y + this.height, PANEL_BACKGROUND);
        graphics.fill(x, y, x + this.width, y + 1, PANEL_LIGHT);
        graphics.fill(x, y + this.height - 1, x + this.width, y + this.height, PANEL_DARK);
        graphics.fill(x, y, x + 1, y + this.height, PANEL_LIGHT);
        graphics.fill(x + this.width - 1, y, x + this.width, y + this.height, PANEL_DARK);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}
