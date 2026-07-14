package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.client.misc.SimpleProgressBarWidget;
import net.oktawia.insaneae2addons.client.screens.ResearchStatusText;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;

import java.util.List;

public class ResearchStationScreen<C extends ResearchStationMenu> extends AEBaseScreen<C> {

    private IconButton devBtn;
    private final SimpleProgressBarWidget recipeBar;

    public ResearchStationScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.recipeBar = new SimpleProgressBarWidget(0, 0, 150, 8)
                .setTooltipSupplier(() -> {
                    int current = getMenu().recipeBar.getCurrentProgress();
                    int max = getMenu().recipeBar.getMaxProgress();
                    int pct = max > 0 ? (int) Math.round(100.0 * current / max) : 0;
                    return List.of(Component.translatable(LangDefs.RESEARCH_RECIPE_PROGRESS.getTranslationKey())
                            .append(": " + pct + "%"));
                });
        this.widgets.add("recipeBar", this.recipeBar);

        if (playerInventory.player != null && playerInventory.player.isCreative()) {
            devBtn = new IconButton(Icon.ENTER, btn -> getMenu().unlockAllClick());
            devBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.RESEARCH_DEV_UNLOCK.getTranslationKey())));
            this.widgets.add("devbtn", devBtn);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.recipeBar.setProgress(getMenu().recipeBar.getCurrentProgress(), getMenu().recipeBar.getMaxProgress());

        setTextContent("status", ResearchStatusText.of(getMenu().getHost().getStatus()));

        boolean loading = getMenu().recipeBar.getCurrentProgress() > 0
                && (minecraft.level.getGameTime() / 20) % 2 == 0;
        setTextContent("working", loading
                ? Component.translatable(LangDefs.RESEARCH_LOADING.getTranslationKey())
                : Component.empty());
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);

        if (this.recipeBar.isPointOver(mouseX, mouseY) && this.recipeBar.hasTooltip()) {
            gg.renderComponentTooltip(this.font, this.recipeBar.getTooltipToRender(), mouseX, mouseY);
        }
    }
}
