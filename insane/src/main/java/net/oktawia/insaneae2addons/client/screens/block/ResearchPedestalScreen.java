package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
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
        if (menu.validConnection) {
            setTextContent("computation", Component.translatable(LangDefs.RESEARCH_PEDESTAL_COMPUTATION.getTranslationKey())
                    .append(String.valueOf(menu.computation)).append("/t"));
        } else {
            setTextContent("computation", Component.translatable(LangDefs.RESEARCH_PEDESTAL_INVALID.getTranslationKey()));
        }
        setTextContent("status", ResearchStatusText.of(menu.status()));
    }
}
