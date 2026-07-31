package net.oktawia.insaneae2addons.client.screens.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;

import net.oktawia.insaneae2addons.menus.block.BrokenPatternProviderMenu;

public class BrokenPatternProviderScreen<C extends BrokenPatternProviderMenu> extends PatternProviderScreen<C> {
    public BrokenPatternProviderScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
