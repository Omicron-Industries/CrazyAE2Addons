package net.oktawia.insaneae2addons.xei.common;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CradleEntry(ResourceLocation structureId, List<ItemStack> inputs, ItemStack output, String description) {
}
