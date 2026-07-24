package net.oktawia.insaneae2addons.items.mobstorage;

import appeng.items.parts.PartItem;
import net.oktawia.insaneae2addons.parts.mobstorage.MobAnnihilationPlanePart;

public class MobAnnihilationPlanePartItem extends PartItem<MobAnnihilationPlanePart> {
    public MobAnnihilationPlanePartItem(Properties properties) {
        super(properties, MobAnnihilationPlanePart.class, MobAnnihilationPlanePart::new);
    }
}
