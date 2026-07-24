package net.oktawia.insaneae2addons.menus.block;

import appeng.menu.AEBaseMenu;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHeatVentBE;

public class PenroseHeatVentMenu extends AEBaseMenu {

    private static final String SET_COOLING = "set_cooling";

    @Getter
    private final PenroseHeatVentBE host;

    public PenroseHeatVentMenu(int id, Inventory playerInventory, PenroseHeatVentBE host) {
        super(InsaneMenuRegistrar.PENROSE_HEAT_VENT_MENU.get(), id, playerInventory, host);
        this.host = host;

        registerClientAction(SET_COOLING, Double.class, this::setCooling);
        createPlayerInventorySlots(playerInventory);
    }

    public void setCooling(double cooling) {
        this.host.setDesiredCooling(cooling);
        if (isClientSide()) {
            sendClientAction(SET_COOLING, cooling);
        }
    }
}
