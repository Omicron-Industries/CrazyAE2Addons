package net.oktawia.insaneae2addons.defs.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResearchRecipes {

    public record ConsumableEntry(String item, int count, int computation) {}

    public record UnlockDef(String key, String label, String item) {}

    public record RecipeDef(
            String id,
            int duration,
            int energyPerTick,
            List<ConsumableEntry> consumables,
            UnlockDef unlock
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
        recipe("builder_pattern")
                .duration(768000)
                .energyPerTick(200)
                .consumable("minecraft:lapis_block", 1, 128)
                .consumable("ae2:interface", 1, 72)
                .consumable("ae2:pattern_provider", 1, 96)
                .consumable("crazyae2addons:ejector", 1, 128)
                .unlock("insaneae2addons:builder_pattern_research", "Autobuilder", "insaneae2addons:builder_pattern")
                .register();

        // item nie istnieje jeszcze w insane (auto enchanter - TODO)
        /*
        recipe("auto_enchanter")
                .duration(1036800)
                .energyPerTick(270)
                .consumable("minecraft:enchanting_table", 1, 72)
                .consumable("minecraft:enchanting_table", 1, 72)
                .consumable("minecraft:enchanting_table", 1, 72)
                .consumable("minecraft:enchanting_table", 1, 72)
                .unlock("insaneae2addons:auto_enchanter_research", "Auto enchanter", "insaneae2addons:auto_enchanter")
                .register();
        */

        // item nie istnieje jeszcze w insane (mob storage - TODO)
        /*
        recipe("mob_cell")
                .duration(3456000)
                .energyPerTick(500)
                .consumable("minecraft:emerald", 32, 128)
                .consumable("minecraft:emerald", 32, 128)
                .consumable("minecraft:soul_sand", 16, 256)
                .consumable("minecraft:soul_sand", 16, 256)
                .consumable("minecraft:obsidian", 16, 96)
                .consumable("minecraft:obsidian", 16, 96)
                .consumable("minecraft:glowstone", 8, 256)
                .consumable("minecraft:glowstone", 8, 256)
                .unlock("insaneae2addons:mob_cell_research", "Mob Storage", "insaneae2addons:mob_cell_housing")
                .register();
        */

        // item nie istnieje jeszcze w insane (penrose sphere - TODO); consumable super_singularity tez nie istnieje
        /*
        recipe("super_singularity_block")
                .duration(41472000)
                .energyPerTick(500)
                .consumable("insaneae2addons:super_singularity", 8, 432)
                .consumable("insaneae2addons:super_singularity", 8, 432)
                .consumable("insaneae2addons:super_singularity", 8, 432)
                .consumable("minecraft:netherite_block", 8, 432)
                .consumable("minecraft:netherite_block", 8, 432)
                .consumable("ae2:dense_energy_cell", 64, 432)
                .consumable("ae2:dense_energy_cell", 64, 432)
                .consumable("ae2:fluix_block", 8, 432)
                .unlock("insaneae2addons:super_singularity_research", "Penrose Sphere", "insaneae2addons:super_singularity_block")
                .register();
        */

        // item nie istnieje jeszcze w insane (entity ticker - TODO)
        /*
        recipe("ticker")
                .duration(1206400)
                .energyPerTick(500)
                .consumable("minecraft:nether_star", 4, 256)
                .consumable("minecraft:nether_star", 4, 256)
                .consumable("minecraft:nether_star", 4, 256)
                .consumable("minecraft:nether_star", 4, 256)
                .unlock("insaneae2addons:entity_ticker_research", "Entity ticker", "insaneae2addons:entity_ticker")
                .register();
        */

        // gate cradla - entropy cradle jeszcze nie istnieje (TODO insane); cradle nie produkuje nic do setupu researchu wiec mozna gate'owac
        /*
        recipe("entropy_cradle")
                .duration(1728000)
                .energyPerTick(440)
                .consumable("ae2:dense_energy_cell", 8, 128)
                .consumable("minecraft:obsidian", 16, 96)
                .consumable("ae2:fluix_block", 8, 128)
                .consumable("minecraft:netherite_block", 1, 256)
                .unlock("insaneae2addons:entropy_cradle_research", "Entropy Cradle", "insaneae2addons:entropy_cradle")
                .register();
        */
    }

    public static class Builder {
        private final String id;
        private final List<ConsumableEntry> consumables = new ArrayList<>();
        private int duration = 0;
        private int energyPerTick = 0;
        private UnlockDef unlock = null;

        private Builder(String id) {
            this.id = id;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder energyPerTick(int energyPerTick) {
            this.energyPerTick = energyPerTick;
            return this;
        }

        public Builder consumable(String item, int count, int computation) {
            consumables.add(new ConsumableEntry(item, count, computation));
            return this;
        }

        public Builder unlock(String key, String label, String item) {
            this.unlock = new UnlockDef(key, label, item);
            return this;
        }

        public void register() {
            RECIPES.add(new RecipeDef(id, duration, energyPerTick, List.copyOf(consumables), unlock));
        }
    }
}
