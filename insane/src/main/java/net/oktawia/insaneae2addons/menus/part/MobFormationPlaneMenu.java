package net.oktawia.insaneae2addons.menus.part;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.core.definitions.AEItems;
import appeng.menu.implementations.UpgradeableMenu;

import net.oktawia.insaneae2addons.parts.mobstorage.MobFormationPlanePart;

public class MobFormationPlaneMenu extends UpgradeableMenu<MobFormationPlanePart> {

    public MobFormationPlaneMenu(MenuType<?> menuType, int id, Inventory ip, MobFormationPlanePart host) {
        super(menuType, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD) > idx;
    }

}
