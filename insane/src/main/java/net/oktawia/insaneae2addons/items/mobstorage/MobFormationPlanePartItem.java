package net.oktawia.insaneae2addons.items.mobstorage;

import appeng.items.parts.PartItem;

import net.oktawia.insaneae2addons.parts.mobstorage.MobFormationPlanePart;

public class MobFormationPlanePartItem extends PartItem<MobFormationPlanePart> {
    public MobFormationPlanePartItem(Properties properties) {
        super(properties, MobFormationPlanePart.class, MobFormationPlanePart::new);
    }
}
