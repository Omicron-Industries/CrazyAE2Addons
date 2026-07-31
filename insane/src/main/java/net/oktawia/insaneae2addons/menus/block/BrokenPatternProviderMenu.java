package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.PatternProviderMenu;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;

public class BrokenPatternProviderMenu extends PatternProviderMenu {
    public BrokenPatternProviderMenu(int id, Inventory ip, PatternProviderLogicHost host) {
        super(InsaneMenuRegistrar.BROKEN_PATTERN_PROVIDER_MENU.get(), id, ip, host);
    }
}
