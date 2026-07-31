package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;

import net.oktawia.crazyae2addons.parts.p2p.WormholeP2PTunnelPart;

public class WormholeP2PTunnelPartItem extends PartItem<WormholeP2PTunnelPart> {

    public WormholeP2PTunnelPartItem(Properties props) {
        super(props, WormholeP2PTunnelPart.class, WormholeP2PTunnelPart::new);
    }
}
