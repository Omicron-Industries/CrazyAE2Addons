package net.oktawia.crazyae2addons.client.misc;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ProgressBar;
import appeng.menu.interfaces.IProgressProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GradientProgressBar extends ProgressBar {

    private static final int FRAME_COLOR = 0xFF13161A;
    private static final int BG_COLOR = 0xFF2B2F36;

    private final IProgressProvider provider;
    private final int colorFrom;
    private final int colorTo;
    private final Direction direction;

    public GradientProgressBar(IProgressProvider source, int colorFrom, int colorTo, Component title) {
        this(source, colorFrom, colorTo, Direction.HORIZONTAL, title);
    }

    public GradientProgressBar(IProgressProvider source, int colorFrom, int colorTo,
                               Direction direction, Component title) {
        super(source, Blitter.texture("guis/states.png").src(0, 0, 1, 1), direction, title);
        this.provider = source;
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        this.direction = direction;
    }

    @Override
    public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }

        int x = getX();
        int y = getY();
        int w = this.width;
        int h = this.height;

        gg.fill(x - 1, y - 1, x + w + 1, y + h + 1, FRAME_COLOR);
        gg.fill(x, y, x + w, y + h, BG_COLOR);

        int max = provider.getMaxProgress();
        int current = Math.min(provider.getCurrentProgress(), max);

        if (this.direction == Direction.VERTICAL) {
            int filled = max > 0 ? Math.round(h * (current / (float) max)) : 0;
            for (int i = 0; i < filled; i++) {
                float t = h > 1 ? (float) i / (h - 1) : 0f;
                int row = y + h - 1 - i;
                gg.fill(x, row, x + w, row + 1, lerpColor(colorFrom, colorTo, t));
            }
        } else {
            int filled = max > 0 ? Math.round(w * (current / (float) max)) : 0;
            for (int i = 0; i < filled; i++) {
                float t = w > 1 ? (float) i / (w - 1) : 0f;
                gg.fill(x + i, y, x + i + 1, y + h, lerpColor(colorFrom, colorTo, t));
            }
        }
    }

    private static int lerpColor(int from, int to, float t) {
        int fa = (from >>> 24) & 0xFF, fr = (from >> 16) & 0xFF, fg = (from >> 8) & 0xFF, fb = from & 0xFF;
        int ta = (to >>> 24) & 0xFF, tr = (to >> 16) & 0xFF, tg = (to >> 8) & 0xFF, tb = to & 0xFF;
        int a = Math.round(fa + (ta - fa) * t);
        int r = Math.round(fr + (tr - fr) * t);
        int g = Math.round(fg + (tg - fg) * t);
        int b = Math.round(fb + (tb - fb) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
