package net.oktawia.insaneae2addons.client.screens.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.style.ScreenStyle;

import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PenroseEmitterBE;
import net.oktawia.insaneae2addons.menus.block.PenroseEmitterMenu;

public class PenroseEmitterScreen<C extends PenroseEmitterMenu> extends PenrosePeripheralScreen<C> {

    public PenroseEmitterScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        PenroseEmitterBE host = menu.getHost();

        addField("on", 40, () -> host.getOnPercent() * 100.0, value -> menu.setOnPercent(value / 100.0));
        addField("off", 40, () -> host.getOffPercent() * 100.0, value -> menu.setOffPercent(value / 100.0));
        addCurve(host.curveModel(), host::getCurvePosition,
                () -> new double[] { host.getOnPercent(), host.getOffPercent() },
                this::emitterCurveTooltip);
    }

    private List<Component> emitterCurveTooltip() {
        List<Component> lines = new ArrayList<>();
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_EMIT_TITLE.getTranslationKey()),
                ChatFormatting.WHITE);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_EMIT_DESC.getTranslationKey()),
                ChatFormatting.GRAY);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_EMIT_DESC2.getTranslationKey()),
                ChatFormatting.GRAY);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_EMIT_NOW.getTranslationKey(),
                percent(curveMarker())), null);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_MARKER.getTranslationKey()),
                ChatFormatting.WHITE);
        addTooltipLine(lines, Component.translatable(LangDefs.PENROSE_CURVE_EMIT_TARGET.getTranslationKey()),
                ChatFormatting.GOLD);
        return lines;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PenroseEmitterBE host = getMenu().getHost();

        setTextContent("thresholds", Component.translatable(LangDefs.PENROSE_EMITTER_THRESHOLDS.getTranslationKey(),
                percent(host.getOnPercent()), percent(host.getOffPercent())));
        setTextContent("reading", Component.translatable(LangDefs.PENROSE_EMITTER_READING.getTranslationKey(),
                percent(host.getCurvePosition())));
        setTextContent("state", Component.translatable((host.isEmitting()
                ? LangDefs.PENROSE_EMITTER_EMITTING
                : LangDefs.PENROSE_EMITTER_SILENT).getTranslationKey()));
    }
}
