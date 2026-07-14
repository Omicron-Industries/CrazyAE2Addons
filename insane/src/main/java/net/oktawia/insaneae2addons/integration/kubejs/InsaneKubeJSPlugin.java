package net.oktawia.insaneae2addons.integration.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.resources.ResourceLocation;

public class InsaneKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(new ResourceLocation("insaneae2addons", "cradle"), CradleRecipeSchema.SCHEMA);
        event.register(new ResourceLocation("insaneae2addons", "research"), ResearchRecipeSchema.SCHEMA);
    }
}
