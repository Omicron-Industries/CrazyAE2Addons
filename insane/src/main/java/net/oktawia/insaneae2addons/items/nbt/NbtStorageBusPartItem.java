package net.oktawia.insaneae2addons.items.nbt;

import appeng.items.parts.PartItem;

import net.oktawia.insaneae2addons.parts.nbt.NbtStorageBusPart;

public class NbtStorageBusPartItem extends PartItem<NbtStorageBusPart> {
    public NbtStorageBusPartItem(Properties properties) {
        super(properties, NbtStorageBusPart.class, NbtStorageBusPart::new);
    }
}
