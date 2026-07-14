package net.oktawia.crazyae2addons.client.misc;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Tooltip;
import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class ResearchDiskPanel implements ICompositeWidget {

    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 7;

    private static final Blitter BACKGROUND = Blitter.texture("guis/extra_panels.png", 128, 128);

    @Nullable
    private final Slot slot;
    private final List<Component> tooltip;

    private Point screenOrigin = Point.ZERO;
    private int x;
    private int y;

    public ResearchDiskPanel(@Nullable Slot slot, List<Component> tooltip) {
        this.slot = slot;
        this.tooltip = tooltip;
    }

    @Override
    public boolean isVisible() {
        return slot != null;
    }

    @Override
    public void setPosition(Point position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {
    }

    @Override
    public Rect2i getBounds() {
        if (slot == null) {
            return new Rect2i(x, y, 0, 0);
        }
        int side = 2 * PADDING + SLOT_SIZE;
        return new Rect2i(x, y, side, side);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        this.screenOrigin = Point.fromTopLeft(bounds);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (slot == null) {
            return;
        }

        int destX = screenOrigin.getX() + this.x;
        int destY = screenOrigin.getY() + this.y;

        BACKGROUND.src(0, 0, SLOT_SIZE + 2 * PADDING, SLOT_SIZE + 2 * PADDING)
                .dest(destX, destY)
                .blit(guiGraphics);
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        if (slot == null || tooltip.isEmpty()) {
            return null;
        }

        int side = 2 * PADDING + SLOT_SIZE;
        if (mouseX < x || mouseX >= x + side || mouseY < y || mouseY >= y + side) {
            return null;
        }

        return new Tooltip(tooltip);
    }
}
