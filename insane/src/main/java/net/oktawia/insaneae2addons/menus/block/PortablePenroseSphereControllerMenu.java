package net.oktawia.insaneae2addons.menus.block;

import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;

public class PortablePenroseSphereControllerMenu extends AbstractMultiblockControllerMenu {

    @Getter
    private final PortablePenroseSphereControllerBE host;

    public PortablePenroseSphereControllerMenu(int id, Inventory playerInventory, PortablePenroseSphereControllerBE host) {
        super(InsaneMenuRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_MENU.get(), id, playerInventory, host);
        this.host = host;

        this.createPlayerInventorySlots(playerInventory);
    }
}
