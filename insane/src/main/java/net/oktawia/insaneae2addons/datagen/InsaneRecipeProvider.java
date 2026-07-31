package net.oktawia.insaneae2addons.datagen;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import appeng.core.definitions.AEBlocks;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.recipes.BlockRecipes;
import net.oktawia.insaneae2addons.defs.recipes.ItemRecipes;

public class InsaneRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public InsaneRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        BlockRecipes.registerRecipes();
        ItemRecipes.registerRecipes();

        for (var recipe : BlockRecipes.getRecipes()) {
            save(writer, recipe.id(), recipe.pattern(), recipe.keys(), recipe.shapelessIngredients(),
                    recipe.smeltingInput(), recipe.output(), recipe.count());
        }
        for (var recipe : ItemRecipes.getRecipes()) {
            save(writer, recipe.id(), recipe.pattern(), recipe.keys(), recipe.shapelessIngredients(), null,
                    recipe.output(), recipe.count());
        }
    }

    private void save(Consumer<FinishedRecipe> writer, String id, String pattern,
            Map<Character, Item> keys, List<Item> shapeless, Item smeltingInput, Item output, int count) {
        var unlock = has(AEBlocks.CONTROLLER.asItem());
        var unlockName = getHasName(AEBlocks.CONTROLLER.asItem());
        var recipeId = InsaneAddons.makeId(id);

        if (smeltingInput != null) {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(smeltingInput), RecipeCategory.MISC, output, 0.1f, 200)
                    .unlockedBy(unlockName, unlock)
                    .save(writer, recipeId);
        } else if (pattern == null) {
            var builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, count);
            shapeless.forEach(builder::requires);
            builder.unlockedBy(unlockName, unlock);
            builder.save(writer, recipeId);
        } else {
            var builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, count);
            for (var row : pattern.split("/"))
                builder.pattern(row);
            keys.forEach(builder::define);
            builder.unlockedBy(unlockName, unlock);
            builder.save(writer, recipeId);
        }
    }
}
