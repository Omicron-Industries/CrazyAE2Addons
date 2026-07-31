package net.oktawia.crazyae2addons.integration.kubejs;

import net.minecraft.resources.ResourceLocation;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class CrazyKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(new ResourceLocation("crazyae2addons", "fabrication"), FabricationRecipeSchema.SCHEMA);
    }
}
