package net.oktawia.insaneae2addons.xei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.xei.common.InsaneXeiRecipes;

import java.util.List;

@JeiPlugin
public class InsaneJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = InsaneAddons.makeId("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CradleCategory(guiHelper));
        registration.addRecipeCategories(new ResearchCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                CradleCategory.TYPE,
                InsaneXeiRecipes.getCradleEntries().stream().map(CradleWrapper::new).toList()
        );
        registration.addRecipes(
                ResearchCategory.TYPE,
                InsaneXeiRecipes.getResearchEntries().stream().map(ResearchWrapper::new).toList()
        );

        FeatureGates.forEachDisabled(InsaneAddons.MODID, item -> registration.getIngredientManager().removeIngredientsAtRuntime(
                VanillaTypes.ITEM_STACK,
                List.of(new ItemStack(item))
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get(),
                CradleCategory.TYPE
        );
        registration.addRecipeCatalyst(
                InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get(),
                ResearchCategory.TYPE
        );
    }
}
