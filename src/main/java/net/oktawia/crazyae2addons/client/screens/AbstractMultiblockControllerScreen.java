package net.oktawia.crazyae2addons.client.screens;

import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;

public abstract class AbstractMultiblockControllerScreen<C extends AbstractMultiblockControllerMenu>
        extends CrazyBaseScreen<C> {

    private final IconButton previewButton;

    protected AbstractMultiblockControllerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.previewButton = new IconButton(Icon.ENTER, btn -> getMenu().togglePreview());
        this.widgets.add("preview", this.previewButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.previewButton.setTooltip(Tooltip.create(previewTooltip(getMenu().isPreviewEnabled())));
    }

    protected abstract Component previewTooltip(boolean previewEnabled);
}
