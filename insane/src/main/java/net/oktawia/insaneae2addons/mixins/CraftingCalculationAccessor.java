package net.oktawia.insaneae2addons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.crafting.CraftingCalculation;

@Mixin(value = CraftingCalculation.class, remap = false)
public interface CraftingCalculationAccessor {
    @Accessor("simRequester")
    ICraftingSimulationRequester getSimRequester();
}
