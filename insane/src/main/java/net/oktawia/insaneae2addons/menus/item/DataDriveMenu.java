package net.oktawia.insaneae2addons.menus.item;

import appeng.menu.AEBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.DataHost;

public class DataDriveMenu extends AEBaseMenu {

    public final DataHost host;

    public DataDriveMenu(int id, Inventory playerInventory, DataHost host) {
        super(InsaneMenuRegistrar.DATA_DRIVE_MENU.get(), id, playerInventory, host);
        this.host = host;
        this.createPlayerInventorySlots(playerInventory);
    }
}
