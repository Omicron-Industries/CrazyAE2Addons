package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.GradientProgressBar;
import net.oktawia.crazyae2addons.client.screens.AbstractMultiblockControllerScreen;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.client.screens.ResearchStatusText;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.ResearchUnitBE;
import net.oktawia.insaneae2addons.menus.block.ResearchUnitMenu;

public class ResearchUnitScreen<C extends ResearchUnitMenu> extends AbstractMultiblockControllerScreen<C> {

    public ResearchUnitScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("fluidBar", new GradientProgressBar(menu.fluidBar,
                0xFF1E6FB0, 0xFF8FE0FF,
                Component.translatable(LangDefs.RESEARCH_STORED_FLUID.getTranslationKey())));

        this.widgets.add("powerBar", new GradientProgressBar(menu.powerBar,
                0xFF1E7A2E, 0xFF7CE88A,
                Component.translatable(LangDefs.RESEARCH_STORED_POWER.getTranslationKey())));
    }

    @Override
    protected Component previewTooltip(boolean previewEnabled) {
        return Component.translatable((previewEnabled
                ? LangDefs.HIDE_PREVIEW
                : LangDefs.SHOW_PREVIEW).getTranslationKey());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        C menu = getMenu();
        int computation = menu.formed ? menu.computation : 0;

        setTextContent("computation", label(LangDefs.RESEARCH_UNIT_COMPUTATION)
                .append(String.valueOf(computation)).append("/t"));
        setTextContent("coolant", label(LangDefs.RESEARCH_UNIT_COOLANT)
                .append(String.valueOf(computation / 4)).append(" mB/t"));
        setTextContent("power", label(LangDefs.RESEARCH_UNIT_POWER)
                .append(Utils.shortenNumber(computation * 64)).append(" AE/t"));
        setTextContent("buffer", label(LangDefs.RESEARCH_UNIT_BUFFER)
                .append(Utils.shortenNumber(menu.storedPower)).append("/")
                .append(Utils.shortenNumber((int) ResearchUnitBE.POWER_BUFFER_CAPACITY)));
        setTextContent("status", ResearchStatusText.of(menu.status()));
    }

    private static net.minecraft.network.chat.MutableComponent label(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }
}
