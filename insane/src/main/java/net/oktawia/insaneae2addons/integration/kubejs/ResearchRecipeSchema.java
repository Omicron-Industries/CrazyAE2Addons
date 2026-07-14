package net.oktawia.insaneae2addons.integration.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ResearchRecipeSchema {

    RecipeComponent<RecipeComponentBuilderMap> CONSUMABLE = RecipeComponent.builder(
            StringComponent.ID.key("item"),
            NumberComponent.INT.key("count").optional(1),
            NumberComponent.INT.key("computation").optional(1)
    );

    RecipeComponent<RecipeComponentBuilderMap> UNLOCK_ENTRY = RecipeComponent.builder(
            StringComponent.ID.key("key"),
            StringComponent.ANY.key("label").optional(""),
            StringComponent.ANY.key("item").optional("")
    );

    RecipeKey<Integer> DURATION = NumberComponent.INT.key("duration");
    RecipeKey<Integer> ENERGY_PER_TICK = NumberComponent.INT.key("energy_per_tick").preferred("energyPerTick");
    RecipeKey<RecipeComponentBuilderMap[]> CONSUMABLES = CONSUMABLE.asArray().key("consumables");
    RecipeKey<RecipeComponentBuilderMap> UNLOCK = UNLOCK_ENTRY.key("unlock");

    RecipeSchema SCHEMA = new RecipeSchema(DURATION, ENERGY_PER_TICK, CONSUMABLES, UNLOCK);
}
