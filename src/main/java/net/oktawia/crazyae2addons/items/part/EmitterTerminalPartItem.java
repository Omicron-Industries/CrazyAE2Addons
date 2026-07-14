package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;
import net.oktawia.crazyae2addons.parts.EmitterTerminal;


public class EmitterTerminalPartItem extends PartItem<EmitterTerminal> {

    public EmitterTerminalPartItem(Properties props) {
        super(props, EmitterTerminal.class, EmitterTerminal::new);
    }
}