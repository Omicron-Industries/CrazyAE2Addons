package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;

import net.oktawia.crazyae2addons.parts.Display;

public class DisplayPartItem extends PartItem<Display> {

    public DisplayPartItem(Properties properties) {
        super(properties, Display.class, Display::new);
    }
}
