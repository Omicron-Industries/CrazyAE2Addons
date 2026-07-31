package net.oktawia.insaneae2addons.client.screens.block;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.style.ScreenStyle;

import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.client.misc.ValueField;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHeatVentBE;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;
import net.oktawia.insaneae2addons.menus.block.PenroseHeatVentMenu;

public class PenroseHeatVentScreen<C extends PenroseHeatVentMenu> extends PenrosePeripheralScreen<C> {

    private static final long COOLANT_WINDOW_MS = 1000L;

    private final Deque<long[]> coolantSamples = new ArrayDeque<>();

    public PenroseHeatVentScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        ValueField cooling = addField("cooling", 64, menu.getHost()::getDesiredCooling, menu::setCooling);
        cooling.setTooltipMessage(fieldTooltip(
                LangDefs.PENROSE_VENT_COOLING_TIP_TITLE, LangDefs.PENROSE_VENT_COOLING_TIP));
        addCurve(PenroseCurveModel.heat(), this::heatPosition, () -> new double[0], this::heatVentTooltip);
    }

    private double smoothedCoolant(int current) {
        long now = System.currentTimeMillis();
        this.coolantSamples.addLast(new long[] { now, current });
        while (!this.coolantSamples.isEmpty() && now - this.coolantSamples.peekFirst()[0] > COOLANT_WINDOW_MS) {
            this.coolantSamples.removeFirst();
        }
        long min = current;
        for (long[] sample : this.coolantSamples) {
            min = Math.min(min, sample[1]);
        }
        return min;
    }

    private List<Component> heatVentTooltip() {
        double heat = getMenu().getHost().getDesiredCooling();
        double perSingu = InsaneConfig.COMMON.PENROSE_HEAT_PER_SINGU_FLOW.get();
        double mbPerMK = InsaneConfig.COMMON.PENROSE_COOLANT_MB_PER_MK.get();
        double massFactorMax = InsaneConfig.COMMON.PENROSE_MASS_FACTOR_MAX.get();

        Component throughput = perSingu > 0.0
                ? Component.translatable(LangDefs.PENROSE_CURVE_VENT_THROUGHPUT_HEAT.getTranslationKey(),
                        Utils.shortenNumber(heat / (perSingu * massFactorMax)),
                        Utils.shortenNumber(heat * mbPerMK / 1000.0))
                : null;

        return ventCurveTooltip(throughput);
    }

    private double heatPosition() {
        PortablePenroseSphereControllerBE controller = getMenu().getHost().getActiveController();
        if (controller == null) {
            return 0.0;
        }

        double max = controller.getMaxHeat();
        return max <= 0.0 ? 0.0 : controller.getHeat() / max;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PenroseHeatVentBE host = getMenu().getHost();
        double coolantBuckets = host.getDesiredCooling() * InsaneConfig.COMMON.PENROSE_COOLANT_MB_PER_MK.get() / 1000.0;

        setTextContent("cooling", Component.translatable(LangDefs.PENROSE_VENT_COOLING.getTranslationKey(),
                Utils.shortenNumber(host.getDesiredCooling())));
        setTextContent("usage", Component.translatable(LangDefs.PENROSE_VENT_COOLANT_USE.getTranslationKey(),
                Utils.shortenNumber(coolantBuckets)));
        setTextContent("tank", Component.translatable(LangDefs.PENROSE_VENT_TANK.getTranslationKey(),
                Utils.shortenNumber(smoothedCoolant(host.getCoolantAmount()) / 1000.0),
                Utils.shortenNumber(host.getCoolantCapacity() / 1000.0)));
        setTextContent("status", Component.translatable((host.isArmed()
                ? LangDefs.PENROSE_PERIPHERAL_ARMED
                : LangDefs.PENROSE_PERIPHERAL_IDLE).getTranslationKey()));
    }
}
