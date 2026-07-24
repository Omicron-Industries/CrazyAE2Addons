package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseInjectionPortBE;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;
import net.oktawia.insaneae2addons.menus.block.PenroseInjectionPortMenu;

public class PenroseInjectionPortScreen<C extends PenroseInjectionPortMenu> extends PenrosePeripheralScreen<C> {

    public PenroseInjectionPortScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        PenroseInjectionPortBE host = menu.getHost();
        addField("rate", 64, host::getDesiredRate, value -> menu.setRate(clampRate(value)));
        addCurve(PenroseCurveModel.mass(), this::massPosition);
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

    private static int clampRate(double value) {
        return (int) Math.max(0L, Math.min(PenroseInjectionPortBE.MAX_RATE, Math.round(value)));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PenroseInjectionPortBE host = getMenu().getHost();

        setTextContent("status", Component.translatable((host.isArmed()
                ? LangDefs.PENROSE_PERIPHERAL_ARMED
                : LangDefs.PENROSE_PERIPHERAL_IDLE).getTranslationKey()));
    }
}
