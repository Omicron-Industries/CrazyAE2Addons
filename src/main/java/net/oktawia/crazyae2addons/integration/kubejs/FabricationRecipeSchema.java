package net.oktawia.crazyae2addons.integration.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface FabricationRecipeSchema {

    RecipeComponent<RecipeComponentBuilderMap> ITEM_ENTRY = RecipeComponent.builder(
            StringComponent.ID.key("item"),
            NumberComponent.INT.key("count").optional(1)
    );

    RecipeKey<RecipeComponentBuilderMap> OUTPUT = ITEM_ENTRY.key("output").defaultOptional();
    RecipeKey<RecipeComponentBuilderMap[]> INPUT = ITEM_ENTRY.asArray().key("input");
    RecipeKey<String> REQUIRED_KEY = StringComponent.ID.key("required_key").optional("");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, REQUIRED_KEY);
}
