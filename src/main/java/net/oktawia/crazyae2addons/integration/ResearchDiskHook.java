package net.oktawia.crazyae2addons.integration;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ResearchDiskHook {

    boolean isResearchDisk(ItemStack stack);

    boolean hasResearch(ItemStack disk, String key);

    ItemStack copyResearch(ItemStack source, ItemStack target);

    boolean wouldAddResearch(ItemStack source, ItemStack target);

    List<Component> researchSlotTooltip();

    List<Component> researchGateInfo(ResourceLocation requiredKey);

    ItemStack researchGateIcon();
}
