package net.oktawia.insaneae2addons.integration;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.oktawia.crazyae2addons.integration.ResearchDiskHook;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.items.DataDrive;

import java.util.List;

public class ResearchDiskHookImpl implements ResearchDiskHook {

    @Override
    public boolean isResearchDisk(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof DataDrive;
    }

    @Override
    public boolean hasResearch(ItemStack disk, String key) {
        if (!isResearchDisk(disk)) {
            return false;
        }
        ResourceLocation parsed = ResourceLocation.tryParse(key);
        return parsed != null && DataDrive.hasKey(disk, parsed);
    }

    @Override
    public ItemStack copyResearch(ItemStack source, ItemStack target) {
        if (!isResearchDisk(source) || !isResearchDisk(target)) {
            return target;
        }
        for (ResourceLocation key : DataDrive.getUnlockedKeys(source)) {
            DataDrive.addKey(target, key);
        }
        return target;
    }

    @Override
    public boolean wouldAddResearch(ItemStack source, ItemStack target) {
        if (!isResearchDisk(source) || !isResearchDisk(target)) {
            return false;
        }
        for (ResourceLocation key : DataDrive.getUnlockedKeys(source)) {
            if (!DataDrive.hasKey(target, key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Component> researchSlotTooltip() {
        return List.of(Component.translatable(LangDefs.RECIPE_FABRICATOR_RESEARCH_DISK.getTranslationKey()));
    }

    @Override
    public List<Component> researchGateInfo(ResourceLocation requiredKey) {
        String label = resolveUnlockLabel(requiredKey);
        return List.of(
                Component.translatable(LangDefs.RESEARCH_GATE_REQUIRES.getTranslationKey(), label),
                Component.translatable(LangDefs.RESEARCH_GATE_HINT.getTranslationKey())
        );
    }

    @Override
    public ItemStack researchGateIcon() {
        return new ItemStack(InsaneItemRegistrar.DATA_DRIVE.get());
    }

    private static String resolveUnlockLabel(ResourceLocation requiredKey) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            for (var recipe : level.getRecipeManager().getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())) {
                if (recipe.unlock.key.equals(requiredKey)
                        && recipe.unlock.label != null
                        && !recipe.unlock.label.isEmpty()) {
                    return recipe.unlock.label;
                }
            }
        }
        return requiredKey.toString();
    }
}
