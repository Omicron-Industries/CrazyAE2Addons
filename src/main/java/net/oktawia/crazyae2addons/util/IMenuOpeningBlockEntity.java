package net.oktawia.crazyae2addons.util;

import net.minecraft.world.entity.player.Player;

import appeng.menu.locator.MenuLocator;

public interface IMenuOpeningBlockEntity {
    void openMenu(Player player, MenuLocator locator);

    default boolean canOpenMenu() {
        return true;
    }
}
