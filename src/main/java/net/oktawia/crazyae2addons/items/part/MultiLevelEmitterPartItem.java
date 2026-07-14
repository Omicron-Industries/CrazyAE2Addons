package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;
import net.oktawia.crazyae2addons.parts.MultiLevelEmitter;


public class MultiLevelEmitterPartItem extends PartItem<MultiLevelEmitter> {

    public MultiLevelEmitterPartItem(Properties props) {
        super(props, MultiLevelEmitter.class, MultiLevelEmitter::new);
    }
}