package net.oktawia.insaneae2addons.client.misc;

import appeng.client.gui.widgets.ITooltip;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;

@Getter
@Accessors(chain = true)
public class CurveWidget extends AbstractWidget implements ITooltip {

    private static final int INNER_BG = 0xFF2B2B2B;
    private static final int BEVEL_DARK = 0xFF373737;
    private static final int BEVEL_LIGHT = 0xFFFFFFFF;
    private static final int BEVEL_CORNER = 0xFF8B8B8B;
    private static final int CURVE_COLOR = 0xFF56E2F5;
    private static final int MARKER_COLOR = 0xFFFFFFFF;
    private static final int TARGET_COLOR = 0xFFFFB300;

    private DoubleUnaryOperator curve = x -> x;

    private double marker = -1.0;
    private double optimum = -1.0;
    private double[] targets = new double[0];

    @Nullable
    private Supplier<List<Component>> tooltipSupplier;

    public CurveWidget(int width, int height) {
        super(0, 0, width, height, Component.empty());
        this.active = true;
    }

    public CurveWidget setCurve(DoubleUnaryOperator curve) {
        this.curve = curve;
        return this;
    }

    public CurveWidget setMarker(double marker) {
        this.marker = marker;
        return this;
    }

    public CurveWidget setOptimum(double optimum) {
        this.optimum = optimum;
        return this;
    }

    public CurveWidget setTargets(double... targets) {
        this.targets = targets;
        return this;
    }

    public CurveWidget setTooltipSupplier(@Nullable Supplier<List<Component>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return this.tooltipSupplier == null ? List.of() : this.tooltipSupplier.get();
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), this.width, this.height);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible || this.width <= 2 || this.height <= 2) {
            return;
        }

        int left = getX();
        int top = getY();
        int right = left + this.width;
        int bottom = top + this.height;

        graphics.fill(left, top, right, bottom, INNER_BG);
        graphics.fill(left, top, right, top + 1, BEVEL_DARK);
        graphics.fill(left, top, left + 1, bottom, BEVEL_DARK);
        graphics.fill(left, bottom - 1, right, bottom, BEVEL_LIGHT);
        graphics.fill(right - 1, top, right, bottom, BEVEL_LIGHT);
        graphics.fill(left, bottom - 1, left + 1, bottom, BEVEL_CORNER);
        graphics.fill(right - 1, top, right, top + 1, BEVEL_CORNER);

        int plotWidth = this.width - 2;
        int plotHeight = this.height - 2;

        for (int i = 0; i < plotWidth; i++) {
            double value = Mth.clamp(this.curve.applyAsDouble((double) i / (plotWidth - 1)), 0.0, 1.0);
            int columnHeight = (int) Math.round(value * plotHeight);
            if (columnHeight > 0) {
                graphics.fill(left + 1 + i, bottom - 1 - columnHeight, left + 2 + i, bottom - 1, CURVE_COLOR);
            }
        }

        for (double target : this.targets) {
            if (target >= 0.0) {
                int x = left + 1 + (int) Math.round(Mth.clamp(target, 0.0, 1.0) * (plotWidth - 1));
                graphics.fill(x, top + 1, x + 1, bottom - 1, TARGET_COLOR);
            }
        }

        if (this.marker >= 0.0) {
            int x = left + 1 + (int) Math.round(Mth.clamp(this.marker, 0.0, 1.0) * (plotWidth - 1));
            graphics.fill(x, top + 1, x + 1, bottom - 1, MARKER_COLOR);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }
}
