package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleControllerBE;

public class EntropyCradleControllerMenu extends AbstractMultiblockControllerMenu {

    public EntropyCradleControllerMenu(int id, Inventory playerInventory, EntropyCradleControllerBE host) {
        super(InsaneMenuRegistrar.ENTROPY_CRADLE_CONTROLLER_MENU.get(), id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
    }
}
