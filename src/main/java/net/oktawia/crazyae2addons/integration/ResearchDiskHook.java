package net.oktawia.crazyae2addons.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ResearchDiskHook {

    boolean isResearchDisk(ItemStack stack);

    boolean hasResearch(ItemStack disk, String key);

    ItemStack copyResearch(ItemStack source, ItemStack target);

    List<Component> researchSlotTooltip();

    List<Component> researchGateInfo(ResourceLocation requiredKey);

    ItemStack researchGateIcon();
}
