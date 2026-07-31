package net.oktawia.insaneae2addons.client.screens.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.style.ScreenStyle;

import net.oktawia.crazyae2addons.client.screens.AbstractMultiblockControllerScreen;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.EntropyCradleControllerMenu;

public class EntropyCradleControllerScreen<C extends EntropyCradleControllerMenu>
        extends AbstractMultiblockControllerScreen<C> {

    public EntropyCradleControllerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected Component previewTooltip(boolean previewEnabled) {
        return Component.translatable((previewEnabled
                ? LangDefs.HIDE_PREVIEW
                : LangDefs.SHOW_PREVIEW).getTranslationKey());
    }
}
