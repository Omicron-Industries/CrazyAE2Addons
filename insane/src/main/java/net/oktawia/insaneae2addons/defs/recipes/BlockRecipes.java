package net.oktawia.insaneae2addons.defs.recipes;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockRecipes {

    public record RecipeDef(
            String id,
            @Nullable String pattern,
            Map<Character, Item> keys,
            List<Item> shapelessIngredients,
            @Nullable Item smeltingInput,
            Item output,
            int count
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
        recipe("broken_pp")
                .smelting(AEBlocks.PATTERN_PROVIDER)
                .output(InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK.get())
                .register();

        recipe("ampere_meter")
                .shaped(" L /ICE")
                .define('L', AEBlocks.CONTROLLER.asItem())
                .define('I', AEParts.IMPORT_BUS.asItem())
                .define('C', AEBlocks.DENSE_ENERGY_CELL.asItem())
                .define('E', AEParts.EXPORT_BUS.asItem())
                .output(InsaneBlockRegistrar.AMPERE_METER_BLOCK.get())
                .register();

        recipe("auto_builder")
                .shaped("EPE/BRN/EPE")
                .define('E', Items.EMERALD)
                .define('P', InsaneItemRegistrar.BUILDER_PATTERN.get())
                .define('B', AEParts.IMPORT_BUS.asItem())
                .define('R', AEBlocks.PATTERN_PROVIDER.asItem())
                .define('N', AEParts.EXPORT_BUS.asItem())
                .output(InsaneBlockRegistrar.AUTO_BUILDER_BLOCK.get())
                .register();

        recipe("research_station")
                .shaped(" C /IKO/ E ")
                .define('C', Items.CLOCK)
                .define('I', AEParts.IMPORT_BUS.asItem())
                .define('K', AEBlocks.CONTROLLER.asItem())
                .define('O', AEParts.EXPORT_BUS.asItem())
                .define('E', AEBlocks.ENERGY_ACCEPTOR.asItem())
                .output(InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get())
                .register();

        recipe("research_unit")
                .shaped("CT")
                .define('C', InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get())
                .define('T', AEBlocks.CONTROLLER.asItem())
                .output(InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get())
                .register();

        recipe("research_unit_frame")
                .shaped("CCC/CIC/CCC")
                .define('I', Blocks.IRON_BLOCK)
                .define('C', InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get())
                .output(InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get())
                .register();

        recipe("research_pedestal_top")
                .shaped("SC")
                .define('S', AEBlocks.SKY_STONE_TANK.asItem())
                .define('C', InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get())
                .output(InsaneBlockRegistrar.RESEARCH_PEDESTAL_TOP_BLOCK.get())
                .register();

        recipe("research_pedestal_bottom")
                .shaped("SC")
                .define('S', AEBlocks.SMOOTH_SKY_STONE_BLOCK.asItem())
                .define('C', InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get())
                .output(InsaneBlockRegistrar.RESEARCH_PEDESTAL_BOTTOM_BLOCK.get())
                .register();

        recipe("research_cable")
                .shaped("CSC")
                .define('S', AEParts.GLASS_CABLE.stack(AEColor.TRANSPARENT).getItem())
                .define('C', Items.REDSTONE)
                .output(InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get())
                .register();

        recipe("research_cable_pink")
                .shapeless(InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get(), Items.PINK_DYE)
                .output(InsaneBlockRegistrar.RESEARCH_CABLE_PINK_BLOCK.get())
                .register();

        recipe("research_cable_white")
                .shapeless(InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get(), Items.WHITE_DYE)
                .output(InsaneBlockRegistrar.RESEARCH_CABLE_WHITE_BLOCK.get())
                .register();

        recipe("entropy_cradle")
                .shaped("BQB/QBQ/BQB")
                .define('B', Blocks.OBSIDIAN)
                .define('Q', Blocks.QUARTZ_BLOCK)
                .output(InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get())
                .register();

        recipe("reinforced_matter_condenser")
                .shaped("IPI/GMG/ICI")
                .define('I', Items.IRON_INGOT)
                .define('P', Blocks.IRON_BLOCK)
                .define('G', AEBlocks.QUARTZ_GLASS.asItem())
                .define('M', AEBlocks.CONDENSER.asItem())
                .define('C', AEItems.CELL_COMPONENT_256K.asItem())
                .output(InsaneBlockRegistrar.REINFORCED_MATTER_CONDENSER_BLOCK.get())
                .register();

        recipe("mob_farm_wall")
                .shaped("BIB/IRI/BIB")
                .define('B', Blocks.IRON_BARS)
                .define('I', Blocks.IRON_BLOCK)
                .define('R', Items.ROTTEN_FLESH)
                .output(InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get())
                .register();

        recipe("mob_farm_input")
                .shaped("WWW/WEW/WWW")
                .define('W', InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get())
                .define('E', InsaneItemRegistrar.MOB_EXPORT_BUS.get())
                .output(InsaneBlockRegistrar.MOB_FARM_INPUT_BLOCK.get())
                .register();

        recipe("mob_farm_collector")
                .shaped("WHW/HEH/WHW")
                .define('W', InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get())
                .define('H', AEParts.IMPORT_BUS.asItem())
                .define('E', AEItems.FLUIX_PEARL.asItem())
                .output(InsaneBlockRegistrar.MOB_FARM_COLLECTOR_BLOCK.get())
                .register();

        recipe("mob_farm_damage")
                .shaped("DND/NEN/DND")
                .define('D', AEBlocks.DENSE_ENERGY_CELL.asItem())
                .define('N', Items.NETHERITE_INGOT)
                .define('E', Items.ECHO_SHARD)
                .output(InsaneBlockRegistrar.MOB_FARM_DAMAGE_BLOCK.get())
                .register();

        recipe("mob_farm_controller")
                .shapeless(InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get(), Items.NETHER_STAR)
                .output(InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get())
                .register();

        recipe("spawner_extractor_wall")
                .shaped("WEW/EFE/WEW")
                .define('W', AEBlocks.SMOOTH_SKY_STONE_BLOCK.asItem())
                .define('E', Items.BLAZE_ROD)
                .define('F', AEItems.FLUIX_PEARL.asItem())
                .output(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get())
                .register();

        recipe("spawner_extractor_controller")
                .shapeless(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get(), Items.NETHER_STAR)
                .output(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get())
                .register();

        recipe("penrose_coil")
                .shaped("AAA/ADA/AAA")
                .define('A', Blocks.COPPER_BLOCK)
                .define('D', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .output(InsaneBlockRegistrar.PENROSE_COIL_BLOCK.get())
                .register();

        recipe("portable_penrose_sphere_controller")
                .shaped("AAA/ANA/AAA")
                .define('A', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .define('N', Items.NETHER_STAR)
                .output(InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get())
                .register();

        recipe("penrose_port")
                .shaped(" E /IPI/ E ")
                .define('E', AEBlocks.ENERGY_ACCEPTOR.asItem())
                .define('I', AEBlocks.INTERFACE.asItem())
                .define('P', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .output(InsaneBlockRegistrar.PENROSE_PORT_BLOCK.get())
                .register();

        recipe("penrose_mass_emitter")
                .shaped("ECS")
                .define('E', AEParts.LEVEL_EMITTER.asItem())
                .define('C', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .define('S', InsaneItemRegistrar.SUPER_SINGULARITY.get())
                .output(InsaneBlockRegistrar.PENROSE_MASS_EMITTER_BLOCK.get())
                .register();

        recipe("penrose_heat_emitter")
                .shaped("ECS")
                .define('E', AEParts.LEVEL_EMITTER.asItem())
                .define('C', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .define('S', Items.FIRE_CHARGE)
                .output(InsaneBlockRegistrar.PENROSE_HEAT_EMITTER_BLOCK.get())
                .register();

        recipe("penrose_injection_port")
                .shaped("ECB")
                .define('E', AEBlocks.PATTERN_PROVIDER.asItem())
                .define('C', InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .define('B', Blocks.HOPPER)
                .output(InsaneBlockRegistrar.PENROSE_INJECTION_PORT_BLOCK.get())
                .register();

        recipe("penrose_heat_vent")
                .shaped("ECB")
                .define('E', CrazyItemRegistrar.REDSTONE_EMITTER.get())
                .define('C', InsaneBlockRegistrar.PENROSE_HEAT_EMITTER_BLOCK.get())
                .define('B', AEBlocks.ENERGY_ACCEPTOR.asItem())
                .output(InsaneBlockRegistrar.PENROSE_HEAT_VENT_BLOCK.get())
                .register();

        recipe("penrose_hawking_vent")
                .shaped("ECB")
                .define('E', CrazyItemRegistrar.REDSTONE_EMITTER.get())
                .define('C', InsaneBlockRegistrar.PENROSE_MASS_EMITTER_BLOCK.get())
                .define('B', InsaneItemRegistrar.ENTITY_TICKER.get())
                .output(InsaneBlockRegistrar.PENROSE_HAWKING_VENT_BLOCK.get())
                .register();

        recipe("entropy_cradle_capacitor")
                .shaped(" F /FRF/ F ")
                .define('F', InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get())
                .define('R', Blocks.REDSTONE_BLOCK)
                .output(InsaneBlockRegistrar.ENTROPY_CRADLE_CAPACITOR_BLOCK.get())
                .register();

        recipe("penrose_glass")
                .shapeless(Items.GLASS, InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get())
                .output(InsaneBlockRegistrar.PENROSE_GLASS_BLOCK.get())
                .register();
    }

    public static class Builder {
        private final String id;
        private String pattern = null;
        private final Map<Character, Item> keys = new LinkedHashMap<>();
        private final List<Item> shapelessIngredients = new ArrayList<>();
        private Item smeltingInput = null;
        private Item output = null;
        private int count = 1;

        private Builder(String id) {
            this.id = id;
        }

        public Builder smelting(ItemLike input) {
            this.smeltingInput = input.asItem();
            return this;
        }

        public Builder shaped(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder define(char key, ItemLike item) {
            keys.put(key, item.asItem());
            return this;
        }

        public Builder shapeless(ItemLike... ingredients) {
            for (var i : ingredients) shapelessIngredients.add(i.asItem());
            return this;
        }

        public Builder output(ItemLike item) {
            this.output = item.asItem();
            return this;
        }

        public Builder output(ItemLike item, int count) {
            this.output = item.asItem();
            this.count = count;
            return this;
        }

        public void register() {
            RECIPES.add(new RecipeDef(id, pattern, Map.copyOf(keys), List.copyOf(shapelessIngredients), smeltingInput, output, count));
        }
    }
}
