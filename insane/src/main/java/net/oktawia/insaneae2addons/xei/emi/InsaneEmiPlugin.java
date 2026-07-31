package net.oktawia.insaneae2addons.xei.emi;

import java.util.List;

import net.minecraft.network.chat.Component;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.xei.common.InsaneXeiRecipes;

@EmiEntrypoint
public class InsaneEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        EmiRecipeCategory cradleCategory = new EmiRecipeCategory(
                InsaneAddons.makeId("cradle"),
                EmiStack.of(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get().asItem())) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.CRADLE_CATEGORY.getTranslationKey());
            }
        };

        EmiRecipeCategory researchCategory = new EmiRecipeCategory(
                InsaneAddons.makeId("research"),
                EmiStack.of(InsaneItemRegistrar.DATA_DRIVE.get())) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.RESEARCH_CATEGORY.getTranslationKey());
            }
        };

        EmiRecipeCategory multiblockCategory = new EmiRecipeCategory(
                InsaneAddons.makeId("multiblock"),
                EmiStack.of(InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get().asItem())) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.MULTIBLOCK_CATEGORY.getTranslationKey());
            }
        };

        registry.addCategory(cradleCategory);
        registry.addCategory(researchCategory);
        registry.addCategory(multiblockCategory);

        registry.addWorkstation(
                cradleCategory,
                EmiStack.of(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get().asItem()));
        registry.addWorkstation(
                researchCategory,
                EmiStack.of(InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get().asItem()));
        for (var entry : InsaneXeiRecipes.getMultiblockEntries()) {
            registry.addWorkstation(multiblockCategory, EmiStack.of(entry.controller()));
        }

        for (var entry : InsaneXeiRecipes.getCradleEntries()) {
            registry.addRecipe(new CradleEmiRecipe(entry, cradleCategory));
        }
        for (var entry : InsaneXeiRecipes.getResearchEntries()) {
            registry.addRecipe(new ResearchEmiRecipe(entry, researchCategory));
        }
        for (var entry : InsaneXeiRecipes.getMultiblockEntries()) {
            registry.addRecipe(new MultiblockEmiRecipe(entry, multiblockCategory));
        }

        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(InsaneItemRegistrar.SUPER_SINGULARITY.get())),
                List.of(InsaneXeiRecipes.superSingularityInfo()),
                InsaneAddons.makeId("/super_singularity_info")));

        registry.addRecipeHandler(null, new CradleEmiRecipeHandler());
        registry.addRecipeHandler(null, new ResearchEmiRecipeHandler());

        FeatureGates.forEachDisabled(InsaneAddons.MODID, item -> registry.removeEmiStacks(EmiStack.of(item)));
    }
}
