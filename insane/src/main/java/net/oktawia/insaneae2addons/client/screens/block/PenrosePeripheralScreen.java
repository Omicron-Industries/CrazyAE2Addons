package net.oktawia.insaneae2addons.client.screens.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.style.ScreenStyle;
import appeng.menu.AEBaseMenu;

import net.oktawia.crazyae2addons.client.screens.CrazyBaseScreen;
import net.oktawia.insaneae2addons.client.misc.CurveWidget;
import net.oktawia.insaneae2addons.client.misc.ValueField;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;

public abstract class PenrosePeripheralScreen<C extends AEBaseMenu> extends CrazyBaseScreen<C> {

    private final List<ValueField> fields = new ArrayList<>();

    private CurveWidget curve;
    private PenroseCurveModel curveModel;
    private DoubleSupplier curvePosition;
    private Supplier<double[]> curveTargets;

    protected PenrosePeripheralScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    protected ValueField addField(String id, int width, DoubleSupplier reader, DoubleConsumer writer) {
        ValueField field = new ValueField(this.style, this.font, width, reader, writer);
        this.widgets.add(id, field);
        this.fields.add(field);
        return field;
    }

    protected List<Component> fieldTooltip(LangDefs title, LangDefs description) {
        List<Component> lines = new ArrayList<>();
        addTooltipLine(lines, Component.translatable(title.getTranslationKey()), ChatFormatting.WHITE);
        addTooltipLine(lines, Component.translatable(description.getTranslationKey()), ChatFormatting.GRAY);
        return lines;
    }

    protected void addCurve(PenroseCurveModel model, DoubleSupplier position) {
        addCurve(model, position, () -> new double[0]);
    }

    protected void addCurve(PenroseCurveModel model, DoubleSupplier position, Supplier<double[]> targets) {
        addCurve(model, position, targets, () -> ventCurveTooltip(null));
    }

    protected void addCurve(PenroseCurveModel model, DoubleSupplier position, Supplier<double[]> targets,
            Supplier<List<Component>> tooltip) {
        this.curveModel = model;
        this.curvePosition = position;
        this.curveTargets = targets;

        this.curve = new CurveWidget(96, 28);
        this.curve.setCurve(model.curve());
        this.curve.setOptimum(model.optimum());
        this.curve.setTooltipSupplier(tooltip);
        this.widgets.add("curve", this.curve);
    }

    protected List<Component> ventCurveTooltip(@Nullable Component throughput) {
        List<Component> lines = new ArrayList<>();
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_VENT_TITLE.getTranslationKey()),
                ChatFormatting.WHITE);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_VENT_DESC.getTranslationKey()),
                ChatFormatting.GRAY);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_VENT_NOW.getTranslationKey(),
                percent(curveValueAtMarker())), null);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_VENT_PEAK.getTranslationKey(),
                percent(curveOptimum())), ChatFormatting.GRAY);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_MARKER.getTranslationKey()),
                ChatFormatting.WHITE);

        if (throughput != null) {
            addTooltipLine(lines, throughput, ChatFormatting.AQUA);
        }

        return lines;
    }

    protected static void addTooltipLine(List<Component> out, Component text, @Nullable ChatFormatting style) {
        out.add(style == null ? text : text.copy().withStyle(style));
    }

    protected final double curveMarker() {
        return this.curvePosition.getAsDouble();
    }

    protected final double curveValueAtMarker() {
        return this.curveModel.valueAt(this.curvePosition.getAsDouble());
    }

    protected final double curveOptimum() {
        return this.curveModel.optimum();
    }

    protected static String percent(double fraction) {
        return String.format("%.0f", Math.max(0.0, fraction) * 100.0);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        for (ValueField field : this.fields) {
            field.syncFromHost();
        }

        if (this.curve != null) {
            this.curve.setMarker(this.curvePosition.getAsDouble());
            this.curve.setTargets(this.curveTargets.get());
        }
    }
}
