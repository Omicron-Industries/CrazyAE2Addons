package net.oktawia.crazyae2addons.xei.emi;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.client.screens.part.DisplayScreen;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.crazyae2addons.xei.common.CrazyRecipes;

@EmiEntrypoint
public class CrazyEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(
                DisplayScreen.class,
                CrazyEmiPlugin::addDisplayScreenExclusions);

        EmiRecipeCategory fabricationCategory = new EmiRecipeCategory(
                CrazyAddons.makeId("fabrication_recipes"),
                EmiStack.of(CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get().asItem())) {
            @Override
            public Component getName() {
                return Component.translatable(LangDefs.FABRICATION_CATEGORY.getTranslationKey());
            }
        };

        registry.addCategory(fabricationCategory);

        for (var entry : CrazyRecipes.getFabricationEntries()) {
            registry.addRecipe(new FabricationEmiRecipe(entry, fabricationCategory));
        }

        registry.addWorkstation(
                fabricationCategory,
                EmiStack.of(CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get().asItem()));

        registry.addRecipeHandler(null, new FabricationEmiRecipeHandler());

        registry.addRecipe(providerConversion("/crazy_provider_to_part",
                CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(),
                CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get()));
        registry.addRecipe(providerConversion("/crazy_provider_to_block",
                CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get(),
                CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get()));

        FeatureGates.forEachDisabled(CrazyAddons.MODID, item -> registry.removeEmiStacks(EmiStack.of(item)));
    }

    private static EmiCraftingRecipe providerConversion(String id, ItemLike input, ItemLike output) {
        return new EmiCraftingRecipe(
                List.of(EmiStack.of(input)),
                EmiStack.of(output),
                CrazyAddons.makeId(id));
    }

    private static void addDisplayScreenExclusions(
            DisplayScreen<?> screen,
            Consumer<Bounds> consumer) {
        for (Rect2i area : screen.getXeiExtraAreas()) {
            consumer.accept(new Bounds(
                    area.getX(),
                    area.getY(),
                    area.getWidth(),
                    area.getHeight()));
        }
    }
}
