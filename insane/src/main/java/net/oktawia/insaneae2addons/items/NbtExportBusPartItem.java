package net.oktawia.insaneae2addons.items;

import appeng.items.parts.PartItem;
import net.oktawia.insaneae2addons.parts.NbtExportBusPart;

public class NbtExportBusPartItem extends PartItem<NbtExportBusPart> {
    public NbtExportBusPartItem(Properties properties) {
        super(properties, NbtExportBusPart.class, NbtExportBusPart::new);
    }
}
