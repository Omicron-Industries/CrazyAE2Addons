package net.oktawia.insaneae2addons.client.screens.block;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.screens.AbstractMultiblockControllerScreen;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.MobFarmControllerMenu;

import java.util.ArrayList;
import java.util.List;

public class MobFarmControllerScreen<C extends MobFarmControllerMenu>
        extends AbstractMultiblockControllerScreen<C> {

    public MobFarmControllerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("upgrades", new UpgradesPanel(
                menu.getSlots(SlotSemantics.UPGRADE),
                this::compatibleUpgrades));
    }

    private List<Component> compatibleUpgrades() {
        List<Component> lines = new ArrayList<>();
        lines.add(GuiText.CompatibleUpgrades.text());
        lines.addAll(Upgrades.getTooltipLinesForMachine(getMenu().getUpgrades().getUpgradableItem()));
        return lines;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent("mobs", LangDefs.MOB_FARM_MOBS.text());
        setTextContent("weapon", LangDefs.MOB_FARM_WEAPON.text());
    }

    @Override
    protected Component previewTooltip(boolean previewEnabled) {
        return Component.translatable((previewEnabled
                ? LangDefs.HIDE_PREVIEW
                : LangDefs.SHOW_PREVIEW).getTranslationKey());
    }
}
