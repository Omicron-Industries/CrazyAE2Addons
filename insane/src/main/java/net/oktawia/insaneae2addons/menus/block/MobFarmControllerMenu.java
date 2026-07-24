package net.oktawia.insaneae2addons.menus.block;

import appeng.api.inventories.InternalInventory;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.FakeSlot;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.mobstorage.MobFarmControllerBE;

public class MobFarmControllerMenu extends AbstractMultiblockControllerMenu {

    @Getter
    private final IUpgradeInventory upgrades;

    public MobFarmControllerMenu(int id, Inventory playerInventory, MobFarmControllerBE host) {
        super(InsaneMenuRegistrar.MOB_FARM_CONTROLLER_MENU.get(), id, playerInventory, host);

        this.upgrades = host.getUpgrades();
        this.setupUpgrades(this.upgrades);

        InternalInventory mobSlots = host.getMobConfig().createMenuWrapper();
        for (int slot = 0; slot < mobSlots.size(); slot++) {
            this.addSlot(new FakeSlot(mobSlots, slot), SlotSemantics.CONFIG);
        }
        this.addSlot(new AppEngSlot(host.getToolInventory(), 0), SlotSemantics.STORAGE);

        this.createPlayerInventorySlots(playerInventory);
    }
}
