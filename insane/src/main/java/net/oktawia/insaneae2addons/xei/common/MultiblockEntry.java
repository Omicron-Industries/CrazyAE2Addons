package net.oktawia.insaneae2addons.xei.common;

import net.minecraft.world.item.ItemStack;

import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;

public record MultiblockEntry(String id, ItemStack controller, MultiblockDefinition definition, boolean showPreview) {
}
