package net.oktawia.insaneae2addons.menus.block;

import appeng.api.upgrades.IUpgradeInventory;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.mobstorage.SpawnerExtractorControllerBE;

public class SpawnerExtractorControllerMenu extends AbstractMultiblockControllerMenu {

    @Getter
    private final IUpgradeInventory upgrades;

    public SpawnerExtractorControllerMenu(int id, Inventory playerInventory, SpawnerExtractorControllerBE host) {
        super(InsaneMenuRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_MENU.get(), id, playerInventory, host);
        this.upgrades = host.getUpgrades();
        this.setupUpgrades(this.upgrades);
        this.createPlayerInventorySlots(playerInventory);
    }
}
