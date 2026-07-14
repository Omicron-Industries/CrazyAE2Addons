package net.oktawia.crazyae2addons.xei.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.client.screens.part.DisplayScreen;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.crazyae2addons.xei.common.CrazyRecipes;

import java.util.function.Consumer;

@EmiEntrypoint
public class CrazyEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(
                DisplayScreen.class,
                CrazyEmiPlugin::addDisplayScreenExclusions
        );

        EmiRecipeCategory fabricationCategory = new EmiRecipeCategory(
                CrazyAddons.makeId("fabrication_recipes"),
                EmiStack.of(CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get().asItem())
        ) {
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
                EmiStack.of(CrazyBlockRegistrar.RECIPE_FABRICATOR_BLOCK.get().asItem())
        );

        registry.addRecipeHandler(null, new FabricationEmiRecipeHandler());

        FeatureGates.forEachDisabled(CrazyAddons.MODID, item -> registry.removeEmiStacks(EmiStack.of(item)));
    }

    private static void addDisplayScreenExclusions(
            DisplayScreen<?> screen,
            Consumer<Bounds> consumer
    ) {
        for (Rect2i area : screen.getXeiExtraAreas()) {
            consumer.accept(new Bounds(
                    area.getX(),
                    area.getY(),
                    area.getWidth(),
                    area.getHeight()
            ));
        }
    }
}