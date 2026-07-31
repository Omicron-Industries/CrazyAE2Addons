package net.oktawia.crazyae2addons.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.parts.p2p.P2PTunnelPart;

@Mixin(P2PTunnelPart.class)
public interface P2PTunnelPartAccessor {
    @Accessor("output")
    void setOutput(boolean output);
}
