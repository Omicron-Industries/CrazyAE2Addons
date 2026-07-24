package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHawkingVentBE;
import net.oktawia.insaneae2addons.client.misc.ValueField;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurves;
import net.oktawia.insaneae2addons.menus.block.PenroseHawkingVentMenu;

import java.util.List;

public class PenroseHawkingVentScreen<C extends PenroseHawkingVentMenu> extends PenrosePeripheralScreen<C> {

    public PenroseHawkingVentScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        ValueField evaporation = addField("evaporation", 64,
                () -> PenroseCurves.hawkingBalancedInjection(menu.getHost().getDesiredEvaporation()),
                feed -> menu.setEvaporation(PenroseCurves.hawkingEvaporationForInjection(feed)));
        evaporation.setTooltipMessage(fieldTooltip(
                LangDefs.PENROSE_HAWKING_EVAP_TIP_TITLE, LangDefs.PENROSE_HAWKING_EVAP_TIP));
        addCurve(PenroseCurveModel.mass(), this::massPosition, () -> new double[0], this::hawkingVentTooltip);
    }

    private List<Component> hawkingVentTooltip() {
        double feed = PenroseCurves.hawkingBalancedInjection(getMenu().getHost().getDesiredEvaporation());
        Component throughput = Component.translatable(
                LangDefs.PENROSE_CURVE_VENT_THROUGHPUT_SINGU.getTranslationKey(), Utils.shortenNumber(feed));

        return ventCurveTooltip(throughput);
    }

    private double massPosition() {
        PortablePenroseSphereControllerBE controller = getMenu().getHost().getActiveController();
        if (controller == null) {
            return 0.0;
        }

        long initial = controller.getInitialMass();
        long span = controller.getMaxMass() - initial;
        return span <= 0L ? 0.0 : (double) (controller.getBlackHoleMass() - initial) / span;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PenroseHawkingVentBE host = getMenu().getHost();

        setTextContent("evaporation", Component.translatable(LangDefs.PENROSE_HAWKING_EVAPORATION.getTranslationKey(),
                Utils.shortenNumber(PenroseCurves.hawkingBalancedInjection(host.getDesiredEvaporation()))));
        setTextContent("cost", Component.translatable(LangDefs.PENROSE_HAWKING_COST.getTranslationKey(),
                Utils.shortenNumber(PenroseCurves.hawkingVentCost(host.getDesiredEvaporation()))));
        setTextContent("buffer", Component.translatable(LangDefs.PENROSE_BUFFER.getTranslationKey(),
                Utils.shortenNumber(host.getBufferedEnergy())));
        setTextContent("status", Component.translatable((host.isArmed()
                ? LangDefs.PENROSE_PERIPHERAL_ARMED
                : LangDefs.PENROSE_PERIPHERAL_IDLE).getTranslationKey()));
    }
}
