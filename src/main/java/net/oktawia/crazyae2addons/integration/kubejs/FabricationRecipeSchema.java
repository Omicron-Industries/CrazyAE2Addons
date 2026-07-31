package net.oktawia.crazyae2addons.integration.kubejs;

import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface FabricationRecipeSchema {

    RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("output").defaultOptional().allowEmpty();
    RecipeKey<InputItem[]> INPUT = CountedIngredientComponent.INSTANCE.asArray().key("input");
    RecipeKey<String> REQUIRED_KEY = StringComponent.ANY.key("required_key").preferred("requiredKey").defaultOptional();
    RecipeKey<InputFluid> FLUID_INPUT = FluidComponents.INPUT.key("fluid_input").preferred("fluidInput")
            .defaultOptional();
    RecipeKey<OutputFluid> FLUID_OUTPUT = FluidComponents.OUTPUT.key("fluid_output").preferred("fluidOutput")
            .defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, REQUIRED_KEY, FLUID_INPUT, FLUID_OUTPUT);
}
