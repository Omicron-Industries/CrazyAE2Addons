package net.oktawia.crazyae2addons.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.api.networking.crafting.ICraftingCPU;

@Mixin(targets = "appeng.menu.me.crafting.CraftingCPURecord", remap = false)
public interface CraftingCPURecordAccessor {
    @Accessor("cpu")
    ICraftingCPU getTheCpu();
}
