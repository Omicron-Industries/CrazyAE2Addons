package net.oktawia.insaneae2addons.menus.part;

import net.minecraft.world.entity.player.Inventory;

import appeng.menu.implementations.UpgradeableMenu;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.parts.EntityTickerPart;

public class EntityTickerMenu extends UpgradeableMenu<EntityTickerPart> {

    public EntityTickerMenu(int id, Inventory playerInventory, EntityTickerPart host) {
        super(InsaneMenuRegistrar.ENTITY_TICKER_MENU.get(), id, playerInventory, host);
    }
}
