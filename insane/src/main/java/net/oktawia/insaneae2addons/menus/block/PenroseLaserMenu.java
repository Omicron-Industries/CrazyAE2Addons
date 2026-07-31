package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;
import appeng.menu.interfaces.IProgressProvider;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.PenroseLaserBE;

public class PenroseLaserMenu extends AEBaseMenu implements IProgressProvider {

    @Getter
    private final PenroseLaserBE host;

    public PenroseLaserMenu(int id, Inventory playerInventory, PenroseLaserBE host) {
        super(InsaneMenuRegistrar.PENROSE_LASER_MENU.get(), id, playerInventory, host);
        this.host = host;

        createPlayerInventorySlots(playerInventory);
    }

    @Override
    public int getCurrentProgress() {
        return this.host.getEnergy();
    }

    @Override
    public int getMaxProgress() {
        return PenroseLaserBE.CAPACITY;
    }
}
