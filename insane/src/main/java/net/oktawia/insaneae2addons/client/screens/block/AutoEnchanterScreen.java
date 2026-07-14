package net.oktawia.insaneae2addons.client.screens.block;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToggleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.block.AutoEnchanterMenu;

import java.util.List;

public class AutoEnchanterScreen extends AEBaseScreen<AutoEnchanterMenu> {

    private final ToggleButton autoSupplyLapis;
    private final ToggleButton autoSupplyBooks;

    public AutoEnchanterScreen(AutoEnchanterMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        IconButton opt1 = new IconButton(Icon.ENTER, b -> getMenu().syncOption(1));
        IconButton opt2 = new IconButton(Icon.ENTER, b -> getMenu().syncOption(2));
        IconButton opt3 = new IconButton(Icon.ENTER, b -> getMenu().syncOption(3));
        opt1.setTooltip(Tooltip.create(Component.translatable(LangDefs.ENCHANTER_CHEAP.getTranslationKey())));
        opt2.setTooltip(Tooltip.create(Component.translatable(LangDefs.ENCHANTER_MEDIUM.getTranslationKey())));
        opt3.setTooltip(Tooltip.create(Component.translatable(LangDefs.ENCHANTER_EXP.getTranslationKey())));

        this.autoSupplyLapis = new ToggleButton(Icon.VALID, Icon.INVALID, this::toggleLapis);
        this.autoSupplyLapis.setTooltipOn(List.of(Component.translatable(LangDefs.ENCHANTER_LAPIS_ON.getTranslationKey())));
        this.autoSupplyLapis.setTooltipOff(List.of(Component.translatable(LangDefs.ENCHANTER_LAPIS_OFF.getTranslationKey())));

        this.autoSupplyBooks = new ToggleButton(Icon.VALID, Icon.INVALID, this::toggleBooks);
        this.autoSupplyBooks.setTooltipOn(List.of(Component.translatable(LangDefs.ENCHANTER_BOOK_ON.getTranslationKey())));
        this.autoSupplyBooks.setTooltipOff(List.of(Component.translatable(LangDefs.ENCHANTER_BOOK_OFF.getTranslationKey())));

        this.widgets.add("opt1", opt1);
        this.widgets.add("opt2", opt2);
        this.widgets.add("opt3", opt3);
        this.widgets.add("aslapis", this.autoSupplyLapis);
        this.widgets.add("asbooks", this.autoSupplyBooks);
    }

    private void toggleLapis(boolean value) {
        getMenu().toggleLapis(value);
    }

    private void toggleBooks(boolean value) {
        getMenu().toggleBooks(value);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        var host = getMenu().getHost();
        String label = switch (host.getOption()) {
            case 1 -> "Cheap";
            case 2 -> "Medium";
            case 3 -> "Exp";
            default -> "None";
        };
        setTextContent("option", Component.literal("> " + label + " <"));
        setTextContent("xpval", Component.literal(Utils.shortenNumber(host.getXp())));
        setTextContent("estval", Component.translatable(LangDefs.ENCHANTER_REQUIRED.getTranslationKey(), host.getLevelCost()));
        this.autoSupplyLapis.setState(host.isAutoSupplyLapis());
        this.autoSupplyBooks.setState(host.isAutoSupplyBooks());
    }
}
