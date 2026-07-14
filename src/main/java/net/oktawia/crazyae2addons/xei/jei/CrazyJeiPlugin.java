package net.oktawia.crazyae2addons.xei.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.client.screens.part.DisplayScreen;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.crazyae2addons.xei.common.CrazyRecipes;

import java.util.List;

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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(
                (Class) DisplayScreen.class,
                new DisplayScreenGuiHandler()
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var fabricationWrapped = CrazyRecipes.getFabricationEntries().stream()
                .map(FabricationWrapper::new)
                .toList();

        registration.addRecipes(FabricationCategory.TYPE, fabricationWrapped);

        FeatureGates.forEachDisabled(CrazyAddons.MODID, item -> registration.getIngredientManager().removeIngredientsAtRuntime(
                VanillaTypes.ITEM_STACK,
                List.of(new ItemStack(item))
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get(),
                FabricationCategory.TYPE
        );
    }

    private static final class DisplayScreenGuiHandler<T extends DisplayScreen<?>>
            implements IGuiContainerHandler<T> {

        @Override
        public List<Rect2i> getGuiExtraAreas(T screen) {
            return screen.getXeiExtraAreas();
        }
    }
}