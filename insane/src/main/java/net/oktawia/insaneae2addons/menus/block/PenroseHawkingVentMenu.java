package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHawkingVentBE;

public class PenroseHawkingVentMenu extends AEBaseMenu {

    private static final String SET_EVAPORATION = "set_evaporation";

    @Getter
    private final PenroseHawkingVentBE host;

    public PenroseHawkingVentMenu(int id, Inventory playerInventory, PenroseHawkingVentBE host) {
        super(InsaneMenuRegistrar.PENROSE_HAWKING_VENT_MENU.get(), id, playerInventory, host);
        this.host = host;

        registerClientAction(SET_EVAPORATION, Double.class, this::setEvaporation);
        createPlayerInventorySlots(playerInventory);
    }

    public void setEvaporation(double evaporation) {
        this.host.setDesiredEvaporation(evaporation);
        if (isClientSide()) {
            sendClientAction(SET_EVAPORATION, evaporation);
        }
    }
}
