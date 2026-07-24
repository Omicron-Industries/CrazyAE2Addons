package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.screens.AbstractMultiblockControllerScreen;
import net.oktawia.insaneae2addons.client.misc.BlackHoleWidget;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;
import net.oktawia.insaneae2addons.menus.block.PortablePenroseSphereControllerMenu;

public class PortablePenroseSphereControllerScreen<C extends PortablePenroseSphereControllerMenu>
        extends AbstractMultiblockControllerScreen<C> {

    private final BlackHoleWidget blackHole;

    public PortablePenroseSphereControllerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.blackHole = new BlackHoleWidget();
        this.widgets.add("bh", this.blackHole);
    }

    @Override
    protected Component previewTooltip(boolean previewEnabled) {
        return Component.translatable((previewEnabled
                ? LangDefs.HIDE_PREVIEW
                : LangDefs.SHOW_PREVIEW).getTranslationKey());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PortablePenroseSphereControllerBE host = getMenu().getHost();
        boolean active = host.isBlackHoleActive();

        this.blackHole.setView(buildView(host, active));
    }

    private static BlackHoleWidget.View buildView(PortablePenroseSphereControllerBE host, boolean active) {
        long initial = host.getInitialMass();
        long max = host.getMaxMass();
        double maxHeat = host.getMaxHeat();

        double massPosition = max > initial ? (double) (host.getBlackHoleMass() - initial) / (max - initial) : 0.0;
        double heatPosition = maxHeat > 0.0 ? host.getHeat() / maxHeat : 0.0;

        return new BlackHoleWidget.View(
                active,
                host.getBlackHoleMass(),
                initial,
                max,
                host.getHeat(),
                maxHeat,
                host.getDiskMassSingu(),
                host.getLastGeneratedFePerTick(),
                host.getLastConsumedFePerTick(),
                host.getStoredEnergy(),
                host.getStoredEnergyInDisk(),
                host.getLastSecondMassDelta(),
                active ? PenroseCurveModel.mass().valueAt(massPosition) : 0.0,
                active ? PenroseCurveModel.heat().valueAt(heatPosition) : 0.0);
    }
}
