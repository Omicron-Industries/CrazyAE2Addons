package net.oktawia.insaneae2addons.mixins;

import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.crafting.CraftingCalculation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingCalculation.class, remap = false)
public interface CraftingCalculationAccessor {
    @Accessor("simRequester")
    ICraftingSimulationRequester getSimRequester();
}
