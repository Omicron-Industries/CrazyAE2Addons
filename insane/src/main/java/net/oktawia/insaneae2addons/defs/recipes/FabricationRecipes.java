package net.oktawia.insaneae2addons.defs.recipes;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FabricationRecipes {

    public record InputEntry(String item, int count) {}
    public record FluidEntry(String fluid, int amount) {}

    public record RecipeDef(
            String id,
            List<InputEntry> inputs,
            @Nullable String outputItem,
            int outputCount,
            @Nullable FluidEntry fluidInput,
            @Nullable FluidEntry fluidOutput,
            @Nullable String requiredKey
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
        recipe("copy_data_drive")
                .input("insaneae2addons:data_drive", 1)
                .output("insaneae2addons:data_drive", 1)
                .register();

        recipe("builder_pattern")
                .input("ae2:view_cell", 1)
                .input("minecraft:redstone", 4)
                .input("minecraft:emerald", 1)
                .output("insaneae2addons:builder_pattern", 1)
                .requiredKey("insaneae2addons:builder_pattern_research")
                .register();

        recipe("ticker")
                .input("ae2:dense_energy_cell", 1)
                .input("minecraft:nether_star", 1)
                .output("insaneae2addons:entity_ticker", 1)
                .requiredKey("insaneae2addons:entity_ticker_research")
                .register();

        recipe("auto_enchanter")
                .input("minecraft:enchanting_table", 2)
                .input("ae2:import_bus", 1)
                .input("ae2:export_bus", 1)
                .input("ae2:energy_cell", 1)
                .output("insaneae2addons:auto_enchanter", 1)
                .requiredKey("insaneae2addons:auto_enchanter_research")
                .register();

        recipe("research_fluid")
                .input("minecraft:lapis_lazuli", 1)
                .input("minecraft:redstone", 1)
                .fluidInput("minecraft:water", 1000)
                .fluidOutput("insaneae2addons:research_fluid", 1000)
                .register();

        recipe("penrose_coolant")
                .input("ae2:certus_quartz_crystal", 8)
                .input("minecraft:amethyst_shard", 4)
                .fluidInput("minecraft:water", 1000)
                .fluidOutput("insaneae2addons:penrose_coolant", 1000)
                .requiredKey("insaneae2addons:super_singularity_research")
                .register();

        recipe("super_singularity_block")
                .input("insaneae2addons:super_singularity", 9)
                .input("minecraft:netherite_ingot", 4)
                .input("minecraft:nether_star", 1)
                .output("insaneae2addons:super_singularity_block", 1)
                .requiredKey("insaneae2addons:super_singularity_research")
                .register();

        recipe("entropy_cradle_controller")
                .input("insaneae2addons:entropy_cradle", 4)
                .input("minecraft:diamond", 1)
                .output("insaneae2addons:entropy_cradle_controller", 1)
                .requiredKey("insaneae2addons:entropy_cradle_research")
                .register();

        recipe("mob_cell")
                .input("ae2:item_cell_housing", 1)
                .input("minecraft:echo_shard", 1)
                .output("insaneae2addons:mob_cell_housing", 1)
                .requiredKey("insaneae2addons:mob_cell_research")
                .register();
    }

    public static class Builder {
        private final String id;
        private final List<InputEntry> inputs = new ArrayList<>();
        private String outputItem = null;
        private int outputCount = 1;
        private FluidEntry fluidInput = null;
        private FluidEntry fluidOutput = null;
        private String requiredKey = null;

        private Builder(String id) {
            this.id = id;
        }

        public Builder input(String item, int count) {
            inputs.add(new InputEntry(item, count));
            return this;
        }

        public Builder output(String item, int count) {
            this.outputItem = item;
            this.outputCount = count;
            return this;
        }

        public Builder fluidInput(String fluid, int amount) {
            this.fluidInput = new FluidEntry(fluid, amount);
            return this;
        }

        public Builder fluidOutput(String fluid, int amount) {
            this.fluidOutput = new FluidEntry(fluid, amount);
            return this;
        }

        public Builder requiredKey(String requiredKey) {
            this.requiredKey = requiredKey;
            return this;
        }

        public void register() {
            RECIPES.add(new RecipeDef(id, List.copyOf(inputs), outputItem, outputCount, fluidInput, fluidOutput, requiredKey));
        }
    }
}
