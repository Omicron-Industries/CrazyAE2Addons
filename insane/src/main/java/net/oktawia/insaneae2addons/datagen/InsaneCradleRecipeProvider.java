package net.oktawia.insaneae2addons.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.recipes.CradleRecipes;

public class InsaneCradleRecipeProvider implements DataProvider {

    private final PackOutput output;

    public InsaneCradleRecipeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CradleRecipes.registerRecipes();

        var pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipes/cradle");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (var recipe : CradleRecipes.getRecipes()) {
            JsonObject json = new JsonObject();
            json.addProperty("type", InsaneAddons.MODID + ":cradle");
            json.addProperty("result_block", recipe.resultBlock());
            json.addProperty("description", recipe.description());

            JsonObject symbols = new JsonObject();
            for (var symbol : recipe.symbols()) {
                JsonArray blocks = new JsonArray();
                for (String blockId : symbol.blockIds()) {
                    blocks.add(blockId);
                }
                symbols.add(symbol.symbol(), blocks);
            }

            JsonArray layers = new JsonArray();
            for (var layer : recipe.layers()) {
                JsonArray rows = new JsonArray();
                for (String row : layer) {
                    rows.add(row);
                }
                layers.add(rows);
            }

            JsonObject pattern = new JsonObject();
            pattern.add("symbols", symbols);
            pattern.add("layers", layers);
            json.add("pattern", pattern);

            var path = pathResolver.json(InsaneAddons.makeId(recipe.id()));
            futures.add(DataProvider.saveStable(cache, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Insane AE2 Addons Cradle Recipes";
    }
}
