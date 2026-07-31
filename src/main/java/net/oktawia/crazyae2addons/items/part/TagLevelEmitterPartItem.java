package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;

import net.oktawia.crazyae2addons.parts.TagLevelEmitter;

public class TagLevelEmitterPartItem extends PartItem<TagLevelEmitter> {

    public TagLevelEmitterPartItem(Properties props) {
        super(props, TagLevelEmitter.class, TagLevelEmitter::new);
    }
}
