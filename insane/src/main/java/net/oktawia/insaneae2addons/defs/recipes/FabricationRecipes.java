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
            @Nullable FluidEntry fluidOutput
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
    }

    public static class Builder {
        private final String id;
        private final List<InputEntry> inputs = new ArrayList<>();
        private String outputItem = null;
        private int outputCount = 1;
        private FluidEntry fluidInput = null;
        private FluidEntry fluidOutput = null;

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

        public void register() {
            RECIPES.add(new RecipeDef(id, List.copyOf(inputs), outputItem, outputCount, fluidInput, fluidOutput));
        }
    }
}
