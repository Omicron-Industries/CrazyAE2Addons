package net.oktawia.insaneae2addons.client.screens.part;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEItems;

import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.part.EntityTickerMenu;

public class EntityTickerScreen<C extends EntityTickerMenu> extends UpgradeableScreen<C> {

    public EntityTickerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        setTextContent("info1", Component.translatable(LangDefs.ENTITY_TICKER_INFO_1.getTranslationKey()));
        setTextContent("info2", Component.translatable(LangDefs.ENTITY_TICKER_INFO_2.getTranslationKey()));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        int speedCards = getMenu().getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD);
        double powerUsage = InsaneConfig.COMMON.ENTITY_TICKER_COST.get() * Math.pow(4, speedCards);
        int multiplier = (int) Math.pow(2, speedCards + 1);
        setTextContent("energy", Component.translatable(
                LangDefs.ENTITY_TICKER_ENERGY.getTranslationKey(), Utils.shortenNumber(powerUsage)));
        setTextContent("speed", Component.translatable(
                LangDefs.ENTITY_TICKER_SPEED.getTranslationKey(), multiplier));
    }
}
