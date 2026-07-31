package net.oktawia.insaneae2addons.integration.kubejs;

import com.google.gson.JsonObject;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CradleRecipeSchema {

    RecipeKey<String> RESULT_BLOCK = StringComponent.ID.key("result_block");
    RecipeKey<JsonObject> PATTERN = JsonObjectComponent.INSTANCE.key("pattern");
    RecipeKey<String> DESCRIPTION = StringComponent.ANY.key("description").optional("");

    RecipeSchema SCHEMA = new RecipeSchema(RESULT_BLOCK, PATTERN, DESCRIPTION);
}
