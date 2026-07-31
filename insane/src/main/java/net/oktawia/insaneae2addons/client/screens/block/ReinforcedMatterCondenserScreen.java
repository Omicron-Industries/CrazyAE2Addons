package net.oktawia.insaneae2addons.client.screens.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;

import net.oktawia.crazyae2addons.client.misc.GradientProgressBar;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.ReinforcedMatterCondenserMenu;

public class ReinforcedMatterCondenserScreen<C extends ReinforcedMatterCondenserMenu> extends AEBaseScreen<C> {

    private static final int SINGU_COLOR_FROM = 0xFF6E1E5A;
    private static final int SINGU_COLOR_TO = 0xFFFF8CE0;
    private static final int CELL_COLOR_FROM = 0xFF1E6FB0;
    private static final int CELL_COLOR_TO = 0xFF8FE0FF;

    public ReinforcedMatterCondenserScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("progressBar", new GradientProgressBar(menu,
                SINGU_COLOR_FROM, SINGU_COLOR_TO, ProgressBar.Direction.VERTICAL,
                LangDefs.CONDENSER_SINGULARITIES.text()));
        this.widgets.add("cellBar", new GradientProgressBar(menu.getCellProgress(),
                CELL_COLOR_FROM, CELL_COLOR_TO, ProgressBar.Direction.VERTICAL,
                LangDefs.CONDENSER_CELLS.text()));
    }
}
