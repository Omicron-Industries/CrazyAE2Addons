package net.oktawia.insaneae2addons.items.nbt;

import appeng.items.parts.PartItem;
import net.oktawia.insaneae2addons.parts.nbt.NbtExportBusPart;

public class NbtExportBusPartItem extends PartItem<NbtExportBusPart> {
    public NbtExportBusPartItem(Properties properties) {
        super(properties, NbtExportBusPart.class, NbtExportBusPart::new);
    }
}
