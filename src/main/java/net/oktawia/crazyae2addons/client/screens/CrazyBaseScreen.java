package net.oktawia.crazyae2addons.client.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class CrazyBaseScreen<C extends AEBaseMenu> extends AEBaseScreen<C> {

    private static final int MIN_TOOLTIP_WIDTH = 120;

    protected CrazyBaseScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void drawTooltip(GuiGraphics guiGraphics, int x, int y, List<Component> lines) {
        if (lines.isEmpty()) {
            return;
        }

        int maxWidth = Math.max(MIN_TOOLTIP_WIDTH, this.width / 2 - 40);

        List<FormattedCharSequence> wrapped = new ArrayList<>(lines.size());
        for (Component line : lines) {
            List<FormattedCharSequence> split = this.font.split(line, maxWidth);
            if (split.isEmpty()) {
                wrapped.add(FormattedCharSequence.EMPTY);
            } else {
                wrapped.addAll(split);
            }
        }

        guiGraphics.renderTooltip(this.font, wrapped, x, y);
    }
}
