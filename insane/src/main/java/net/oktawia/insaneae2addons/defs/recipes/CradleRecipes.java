package net.oktawia.insaneae2addons.defs.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CradleRecipes {

    public record SymbolDef(String symbol, List<String> blockIds) {}

    public record RecipeDef(
            String id,
            String resultBlock,
            String description,
            List<SymbolDef> symbols,
            List<List<String>> layers
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
        energyRecipe("energy1", "ae2:energy_cell", "ae2:1k_crafting_storage", "insaneae2addons:energy_storage_1k", "ec.1k");
        energyRecipe("energy4k", "ae2:energy_cell", "ae2:4k_crafting_storage", "insaneae2addons:energy_storage_4k", "ec.4k");
        energyRecipe("energy16k", "ae2:energy_cell", "ae2:16k_crafting_storage", "insaneae2addons:energy_storage_16k", "ec.16k");
        energyRecipe("energy64k", "ae2:energy_cell", "ae2:64k_crafting_storage", "insaneae2addons:energy_storage_64k", "ec.64k");
        energyRecipe("energy256k", "ae2:energy_cell", "ae2:256k_crafting_storage", "insaneae2addons:energy_storage_256k", "ec.256k");

        energyRecipe("dense1k", "ae2:dense_energy_cell", "insaneae2addons:energy_storage_1k", "insaneae2addons:energy_storage_1m", "ec.dense.1k");
        energyRecipe("dense4k", "ae2:dense_energy_cell", "insaneae2addons:energy_storage_4k", "insaneae2addons:energy_storage_4m", "ec.dense.4k");
        energyRecipe("dense16k", "ae2:dense_energy_cell", "insaneae2addons:energy_storage_16k", "insaneae2addons:energy_storage_16m", "ec.dense.16k");
        energyRecipe("dense64k", "ae2:dense_energy_cell", "insaneae2addons:energy_storage_64k", "insaneae2addons:energy_storage_64m", "ec.dense.64k");
        energyRecipe("dense256k", "ae2:dense_energy_cell", "insaneae2addons:energy_storage_256k", "insaneae2addons:energy_storage_256m", "ec.dense.256k");

        recipe("penrose")
                .result("insaneae2addons:penrose_frame")
                .description("penrose.xei.description")
                .symbol("A", "ae2:fluix_block")
                .symbol("B", "minecraft:obsidian")
                .symbol("D", "minecraft:iron_block")
                .symbol("E", "insaneae2addons:super_singularity_block")
                .layer("A A A A A", "A B B B A", "A B B B A", "A B B B A", "A A A A A")
                .layer("A B B B A", "B D D D B", "B D D D B", "B D D D B", "A B B B A")
                .layer("A B B B A", "B D D D B", "B D E D B", "B D D D B", "A B B B A")
                .layer("A B B B A", "B D D D B", "B D D D B", "B D D D B", "A B B B A")
                .layer("A A A A A", "A B B B A", "A B B B A", "A B B B A", "A A A A A")
                .register();
    }

    private static void energyRecipe(String id, String cell, String core, String result, String description) {
        recipe(id)
                .result(result)
                .description(description)
                .symbol("A", cell)
                .symbol("B", "ae2:fluix_block")
                .symbol("D", "minecraft:iron_block")
                .symbol("E", core)
                .layer("A A B A A", "A A D A A", "B D D D B", "A A D A A", "A A B A A")
                .layer("A A D A A", "A E E E A", "D E E E D", "A E E E A", "A A D A A")
                .layer("B D D D B", "D E E E D", "D E E E D", "D E E E D", "B D D D B")
                .layer("A A D A A", "A E E E A", "D E E E D", "A E E E A", "A A D A A")
                .layer("A A B A A", "A A D A A", "B D D D B", "A A D A A", "A A B A A")
                .register();
    }

    public static class Builder {
        private final String id;
        private final List<SymbolDef> symbols = new ArrayList<>();
        private final List<List<String>> layers = new ArrayList<>();
        private String resultBlock = "";
        private String description = "";

        private Builder(String id) {
            this.id = id;
        }

        public Builder result(String blockId) {
            this.resultBlock = blockId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder symbol(String symbol, String... blockIds) {
            symbols.add(new SymbolDef(symbol, List.of(blockIds)));
            return this;
        }

        public Builder layer(String... rows) {
            layers.add(List.of(rows));
            return this;
        }

        public void register() {
            RECIPES.add(new RecipeDef(id, resultBlock, description, List.copyOf(symbols), List.copyOf(layers)));
        }
    }
}
