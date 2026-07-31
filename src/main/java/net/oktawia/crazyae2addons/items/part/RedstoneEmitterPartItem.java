package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;

import net.oktawia.crazyae2addons.parts.RedstoneEmitter;

public class RedstoneEmitterPartItem extends PartItem<RedstoneEmitter> {

    public RedstoneEmitterPartItem(Properties props) {
        super(props, RedstoneEmitter.class, RedstoneEmitter::new);
    }
}
