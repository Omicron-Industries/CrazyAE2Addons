package net.oktawia.crazyae2addons.multiblock;

import appeng.menu.AEBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMultiblockControllerMenu extends AEBaseMenu {

    private final @Nullable AbstractMultiblockControllerBE controller;

    protected AbstractMultiblockControllerMenu(MenuType<?> menuType, int id, Inventory playerInventory,
                                               AbstractMultiblockControllerBE host) {
        super(menuType, id, playerInventory, host);
        this.controller = host;
    }

    public void togglePreview() {
        if (this.controller != null) {
            this.controller.setPreviewEnabled(!this.controller.isPreviewEnabled());
        }
    }

    public boolean isPreviewEnabled() {
        return this.controller != null && this.controller.isPreviewEnabled();
    }
}
