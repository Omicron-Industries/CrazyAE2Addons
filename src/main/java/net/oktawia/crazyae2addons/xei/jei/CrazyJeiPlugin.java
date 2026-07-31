package net.oktawia.crazyae2addons.xei.jei;

import java.util.List;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.client.screens.part.DisplayScreen;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.crazyae2addons.xei.common.CrazyRecipes;

@JeiPlugin
public class CrazyJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = CrazyAddons.makeId("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FabricationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(
                (Class) DisplayScreen.class,
                new DisplayScreenGuiHandler());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var fabricationWrapped = CrazyRecipes.getFabricationEntries().stream()
                .map(FabricationWrapper::new)
                .toList();

        registration.addRecipes(FabricationCategory.TYPE, fabricationWrapped);

        registration.addRecipes(RecipeTypes.CRAFTING, List.of(
                providerConversion("crazy_provider_to_part",
                        CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(),
                        CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get()),
                providerConversion("crazy_provider_to_block",
                        CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get(),
                        CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get())));

        FeatureGates.forEachDisabled(CrazyAddons.MODID,
                item -> registration.getIngredientManager().removeIngredientsAtRuntime(
                        VanillaTypes.ITEM_STACK,
                        List.of(new ItemStack(item))));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get(),
                FabricationCategory.TYPE);
    }

    private static ShapelessRecipe providerConversion(String id, ItemLike input, ItemLike output) {
        return new ShapelessRecipe(
                CrazyAddons.makeId(id),
                "",
                CraftingBookCategory.MISC,
                new ItemStack(output),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(input)));
    }

    private static final class DisplayScreenGuiHandler<T extends DisplayScreen<?>>
            implements IGuiContainerHandler<T> {

        @Override
        public List<Rect2i> getGuiExtraAreas(T screen) {
            return screen.getXeiExtraAreas();
        }
    }
}
