package net.oktawia.insaneae2addons.items.mobstorage;

import appeng.items.parts.PartItem;
import net.oktawia.insaneae2addons.parts.mobstorage.MobExportBusPart;

public class MobExportBusPartItem extends PartItem<MobExportBusPart> {
    public MobExportBusPartItem(Properties properties) {
        super(properties, MobExportBusPart.class, MobExportBusPart::new);
    }
}
