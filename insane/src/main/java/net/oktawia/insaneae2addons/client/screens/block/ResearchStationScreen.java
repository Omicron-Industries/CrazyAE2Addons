package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.insaneae2addons.client.screens.ResearchStatusText;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;

public class ResearchStationScreen<C extends ResearchStationMenu> extends AEBaseScreen<C> {

    private IconButton devBtn;

    public ResearchStationScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("recipeBar", new ProgressBar(menu.recipeBar, style.getImage("progressRecipe"),
                ProgressBar.Direction.HORIZONTAL,
                Component.translatable(LangDefs.RESEARCH_RECIPE_PROGRESS.getTranslationKey())));

        if (playerInventory.player != null && playerInventory.player.isCreative()) {
            devBtn = new IconButton(Icon.ENTER, btn -> getMenu().unlockAllClick());
            devBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.RESEARCH_DEV_UNLOCK.getTranslationKey())));
            this.widgets.add("devbtn", devBtn);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        setTextContent("status", ResearchStatusText.of(getMenu().status()));

        boolean loading = getMenu().recipeBar.getCurrentProgress() > 0
                && (minecraft.level.getGameTime() / 20) % 2 == 0;
        setTextContent("working", loading
                ? Component.translatable(LangDefs.RESEARCH_LOADING.getTranslationKey())
                : Component.empty());
    }
}
