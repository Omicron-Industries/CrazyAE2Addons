package net.oktawia.insaneae2addons.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.recipes.ResearchRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InsaneResearchRecipeProvider implements DataProvider {

    private final PackOutput output;

    public InsaneResearchRecipeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        ResearchRecipes.registerRecipes();

        var pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes/research");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (var recipe : ResearchRecipes.getRecipes()) {
            JsonObject json = new JsonObject();
            json.addProperty("type", InsaneAddons.MODID + ":research");
            json.addProperty("duration", recipe.duration());
            json.addProperty("energy_per_tick", recipe.energyPerTick());

            JsonArray consumables = new JsonArray();
            for (var c : recipe.consumables()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("item", c.item());
                entry.addProperty("count", c.count());
                entry.addProperty("computation", c.computation());
                consumables.add(entry);
            }
            json.add("consumables", consumables);

            JsonObject unlock = new JsonObject();
            unlock.addProperty("key", recipe.unlock().key());
            unlock.addProperty("label", recipe.unlock().label());
            unlock.addProperty("item", recipe.unlock().item());
            json.add("unlock", unlock);

            var path = pathResolver.json(InsaneAddons.makeId(recipe.id()));
            futures.add(DataProvider.saveStable(cache, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Insane AE2 Addons Research Recipes";
    }
}
