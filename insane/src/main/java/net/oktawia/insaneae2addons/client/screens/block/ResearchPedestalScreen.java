package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.client.screens.ResearchStatusText;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.ResearchPedestalMenu;

public class ResearchPedestalScreen<C extends ResearchPedestalMenu> extends AEBaseScreen<C> {

    public ResearchPedestalScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        C menu = getMenu();
        switch (menu.connection()) {
            case SINGLE -> setTextContent("computation",
                    Component.translatable(LangDefs.RESEARCH_PEDESTAL_COMPUTATION.getTranslationKey())
                            .append(String.valueOf(menu.computation)).append("/t"));
            case MULTIPLE_UNITS -> setTextContent("computation",
                    Component.translatable(LangDefs.RESEARCH_PEDESTAL_MULTIPLE_UNITS.getTranslationKey())
                            .withStyle(ChatFormatting.RED));
            case UNIT_SHARED -> setTextContent("computation",
                    Component.translatable(LangDefs.RESEARCH_PEDESTAL_UNIT_SHARED.getTranslationKey())
                            .withStyle(ChatFormatting.RED));
            default -> setTextContent("computation",
                    Component.translatable(LangDefs.RESEARCH_PEDESTAL_INVALID.getTranslationKey())
                            .withStyle(ChatFormatting.RED));
        }
        setTextContent("status", ResearchStatusText.of(menu.status()));
    }
}
