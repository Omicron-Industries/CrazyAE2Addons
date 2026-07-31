package net.oktawia.insaneae2addons.xei.common;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ResearchEntry(
        ResourceLocation recipeId,
        List<ItemStack> inputs,
        ItemStack driveOrOutput,
        String label,
        ResourceLocation unlockKey) {
}
