package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;
import net.oktawia.crazyae2addons.parts.ResourceTrackingTerminalPart;


public class ResourceTrackingTerminalPartItem extends PartItem<ResourceTrackingTerminalPart> {

    public ResourceTrackingTerminalPartItem(Properties props) {
        super(props, ResourceTrackingTerminalPart.class, ResourceTrackingTerminalPart::new);
    }
}
