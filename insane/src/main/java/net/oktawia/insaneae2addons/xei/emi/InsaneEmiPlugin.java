package net.oktawia.insaneae2addons.xei.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
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
                EmiStack.of(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get().asItem())
        ) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.CRADLE_CATEGORY.getTranslationKey());
            }
        };

        EmiRecipeCategory researchCategory = new EmiRecipeCategory(
                InsaneAddons.makeId("research"),
                EmiStack.of(InsaneItemRegistrar.DATA_DRIVE.get())
        ) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.RESEARCH_CATEGORY.getTranslationKey());
            }
        };

        registry.addCategory(cradleCategory);
        registry.addCategory(researchCategory);

        registry.addWorkstation(
                cradleCategory,
                EmiStack.of(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get().asItem())
        );
        registry.addWorkstation(
                researchCategory,
                EmiStack.of(InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get().asItem())
        );

        for (var entry : InsaneXeiRecipes.getCradleEntries()) {
            registry.addRecipe(new CradleEmiRecipe(entry, cradleCategory));
        }
        for (var entry : InsaneXeiRecipes.getResearchEntries()) {
            registry.addRecipe(new ResearchEmiRecipe(entry, researchCategory));
        }

        registry.addRecipeHandler(null, new CradleEmiRecipeHandler());
        registry.addRecipeHandler(null, new ResearchEmiRecipeHandler());

        FeatureGates.forEachDisabled(InsaneAddons.MODID, item -> registry.removeEmiStacks(EmiStack.of(item)));
    }
}
