package net.oktawia.insaneae2addons.client.screens.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.style.ScreenStyle;

import net.oktawia.crazyae2addons.client.misc.GradientProgressBar;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PenroseLaserBE;
import net.oktawia.insaneae2addons.menus.block.PenroseLaserMenu;

public class PenroseLaserScreen<C extends PenroseLaserMenu> extends PenrosePeripheralScreen<C> {

    private static final int CHARGE_COLOR_FROM = 0xFF5A0A0A;
    private static final int CHARGE_COLOR_TO = 0xFFFF4040;

    public PenroseLaserScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        GradientProgressBar chargeBar = new GradientProgressBar(
                menu, CHARGE_COLOR_FROM, CHARGE_COLOR_TO, Component.empty());
        this.widgets.add("charge", chargeBar);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PenroseLaserBE host = getMenu().getHost();

        setTextContent("energy", Component.translatable(LangDefs.PENROSE_LASER_ENERGY.getTranslationKey(),
                Utils.shortenNumber(host.getEnergy()), Utils.shortenNumber(PenroseLaserBE.CAPACITY)));
        setTextContent("status", Component.translatable((host.isCharged()
                ? LangDefs.PENROSE_LASER_CHARGED
                : LangDefs.PENROSE_LASER_CHARGING).getTranslationKey()));
    }
}
