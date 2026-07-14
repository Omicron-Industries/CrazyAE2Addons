package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;
import net.oktawia.crazyae2addons.parts.RedstoneTerminal;


public class RedstoneTerminalPartItem extends PartItem<RedstoneTerminal> {

    public RedstoneTerminalPartItem(Properties props) {
        super(props, RedstoneTerminal.class, RedstoneTerminal::new);
    }
}