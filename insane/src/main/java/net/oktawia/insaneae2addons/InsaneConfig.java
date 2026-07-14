package net.oktawia.insaneae2addons;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class InsaneConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    public static final class Common {

        public final ForgeConfigSpec.IntValue AUTOBUILDER_COST_MULT;
        public final ForgeConfigSpec.IntValue AUTOBUILDER_MINE_DELAY;
        public final ForgeConfigSpec.IntValue AUTOBUILDER_SPEED;
        public final ForgeConfigSpec.IntValue AUTOBUILDER_PREVIEW_LIMIT;

        public final ForgeConfigSpec.BooleanValue RESEARCH_REQUIRED;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> RESEARCH_UNIT_EXTRA_Q_BLOCKS;
        public final ForgeConfigSpec.IntValue RESEARCH_UNIT_COMPUTATION_DIVISOR;
        public final ForgeConfigSpec.IntValue RESEARCH_UNIT_COMPUTATION_POWER_COST;
        public final ForgeConfigSpec.IntValue RESEARCH_UNIT_FLUID_DIVISOR;
        public final ForgeConfigSpec.IntValue RESEARCH_UNIT_FLUID_BUFFER;
        public final ForgeConfigSpec.IntValue RESEARCH_UNIT_POWER_BUFFER;
        public final ForgeConfigSpec.IntValue RESEARCH_STATION_PEDESTAL_RANGE;

        public final ForgeConfigSpec.BooleanValue NBT_VIEW_CELL_ENABLED;
        public final ForgeConfigSpec.BooleanValue NBT_STORAGE_BUS_ENABLED;
        public final ForgeConfigSpec.BooleanValue NBT_EXPORT_BUS_ENABLED;
        public final ForgeConfigSpec.IntValue NBT_EXPORT_BUS_TRANSFER_FACTOR;

        public final ForgeConfigSpec.IntValue AMPERE_METER_INACTIVITY_RESET_TICKS;

        public final ForgeConfigSpec.BooleanValue BROKEN_PATTERN_PROVIDER_ENABLED;

        public final ForgeConfigSpec.BooleanValue PROVIDER_CARDS_ENABLED;

        public final ForgeConfigSpec.BooleanValue AUTO_ENCHANTER_ENABLED;
        public final ForgeConfigSpec.IntValue AUTO_ENCHANTER_COST;

        public final ForgeConfigSpec.BooleanValue ENERGY_STORAGE_ENABLED;
        public final ForgeConfigSpec.IntValue ENERGY_STORAGE_CAPACITY_MULTIPLIER;

        public final ForgeConfigSpec.IntValue CRADLE_CAPACITY;
        public final ForgeConfigSpec.IntValue CRADLE_CHARGING_SPEED;
        public final ForgeConfigSpec.IntValue CRADLE_COST;

        public final ForgeConfigSpec.BooleanValue ENTITY_TICKER_ENABLED;
        public final ForgeConfigSpec.IntValue ENTITY_TICKER_COST;
        public final ForgeConfigSpec.IntValue ENTITY_TICKER_MAX_SPEED_CARDS;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_TICKER_BLACKLIST;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment(
                    "Insane AE2 Addons - Common Config",
                    "All feature toggles and feature-specific options are defined here.",
                    "For every config entry that defines a limit, -1 means no limit."
            ).push("features");

            builder.comment(
                    "Autobuilder feature.",
                    "The autobuilder places or breaks blocks in a region based on a builder pattern."
            ).push("autoBuilder");

            AUTOBUILDER_COST_MULT = intInRange(builder,
                    "costMultiplier", 5, 0, 100,
                    "FE cost multiplier for the autobuilder."
            );

            AUTOBUILDER_MINE_DELAY = intInRange(builder,
                    "mineDelay", 2, 0, 10,
                    "Ticks to wait after each broken block."
            );

            AUTOBUILDER_SPEED = nonNegativeInt(builder,
                    "speed", 128,
                    "Operations per tick the autobuilder can perform."
            );

            AUTOBUILDER_PREVIEW_LIMIT = nonNegativeInt(builder,
                    "previewLimit", 8192,
                    "How many preview blocks the autobuilder can show at once."
            );

            builder.pop();

            builder.comment(
                    "Research feature.",
                    "The research multiblock unlocks insane fabrication recipes onto a Data Drive."
            ).push("research");

            RESEARCH_REQUIRED = builder.comment(
                    "When true, insane fabrication recipes require the matching research unlock on a Data Drive."
            ).define("required", true);

            RESEARCH_UNIT_EXTRA_Q_BLOCKS = builder.comment(
                    "Extra blocks allowed in the 3x3 core ('Q' slots) of the Research Unit, each with the",
                    "computation it grants per block in the core.",
                    "Format: one entry per line as \"namespace:block=computation\", e.g. minecraft:diamond_block=8.",
                    "The value is added per matching block (block count * value). The built-in crafting storage",
                    "blocks (1k-256k) are always allowed and counted separately."
            ).defineList(
                    "unitExtraQBlocks",
                    List.of(),
                    obj -> obj instanceof String s && isExtraQEntry(s)
            );

            RESEARCH_UNIT_COMPUTATION_DIVISOR = intInRange(builder,
                    "unitComputationDivisor", 16, 1, Integer.MAX_VALUE,
                    "Divisor applied to the summed tier value of the core blocks to get base computation.",
                    "Lower means more computation per core. computation = tierSum / divisor + extra blocks."
            );

            RESEARCH_UNIT_COMPUTATION_POWER_COST = intInRange(builder,
                    "unitComputationPowerCost", 64, 0, Integer.MAX_VALUE,
                    "AE drawn per point of computation per tick while the unit is researching."
            );

            RESEARCH_UNIT_FLUID_DIVISOR = intInRange(builder,
                    "unitFluidDivisor", 4, 1, Integer.MAX_VALUE,
                    "Computation divided by this gives research fluid (mB) consumed per tick."
            );

            RESEARCH_UNIT_FLUID_BUFFER = intInRange(builder,
                    "unitFluidBuffer", 64_000, 1, Integer.MAX_VALUE,
                    "Research fluid buffer capacity (mB) held by the research unit."
            );

            RESEARCH_UNIT_POWER_BUFFER = intInRange(builder,
                    "unitPowerBuffer", 200_000, 1, Integer.MAX_VALUE,
                    "AE power buffer capacity held by the research unit."
            );

            RESEARCH_STATION_PEDESTAL_RANGE = intInRange(builder,
                    "stationPedestalRange", 3, 1, 16,
                    "Radius in blocks around the research station scanned for pedestals."
            );

            builder.pop();

            builder.comment(
                    "NBT tools feature.",
                    "NBT view cell and NBT filtering buses match items by their NBT via a matcher expression."
            ).push("nbtTools");

            NBT_VIEW_CELL_ENABLED = builder.comment(
                    "Enables the NBT view cell and its recognition in AE2 view cell slots."
            ).define("nbtViewCellEnabled", true);

            NBT_STORAGE_BUS_ENABLED = builder.comment(
                    "Enables the NBT storage bus."
            ).define("nbtStorageBusEnabled", true);

            NBT_EXPORT_BUS_ENABLED = builder.comment(
                    "Enables the NBT export bus."
            ).define("nbtExportBusEnabled", true);

            NBT_EXPORT_BUS_TRANSFER_FACTOR = intInRange(builder,
                    "nbtExportBusTransferFactor", 4, 1, Integer.MAX_VALUE,
                    "Items moved per operation by the NBT export bus (operations * this factor)."
            );

            builder.pop();

            builder.comment(
                    "Ampere meter feature.",
                    "An inline block that measures FE/EU throughput passing through it and can",
                    "emit a redstone signal based on the measured transfer rate."
            ).push("ampereMeter");

            AMPERE_METER_INACTIVITY_RESET_TICKS = intInRange(builder,
                    "inactivityResetTicks", 10, 0, Integer.MAX_VALUE,
                    "Ticks without any transfer after which the displayed rate resets to zero."
            );

            builder.pop();

            builder.comment(
                    "Broken pattern provider feature.",
                    "A pattern provider with a single pattern slot.",
                    "Note: disabling only hides it from JEI/EMI and marks it in the tooltip;",
                    "already-placed providers keep working (a full functional off would need a mixin)."
            ).push("brokenPatternProvider");

            BROKEN_PATTERN_PROVIDER_ENABLED = builder.comment(
                    "Enables the broken pattern provider (hide from JEI/EMI and tooltip when off)."
            ).define("enabled", true);

            builder.pop();

            builder.comment(
                    "Crazy pattern provider upgrade cards.",
                    "Automation card: the provider only serves crafting requests coming from machines.",
                    "Player card: the provider only serves requests coming from players.",
                    "Both cards work on the block and the cable part."
            ).push("providerCards");

            PROVIDER_CARDS_ENABLED = builder.comment(
                    "Enables the player/automation cards (hide from JEI/EMI, tooltip, and source filtering when off)."
            ).define("enabled", true);

            builder.pop();

            builder.comment(
                    "Auto enchanter feature.",
                    "A machine placed under an enchanting table that enchants items/books pulled from the",
                    "network, spending lapis and XP stored as xp shards or XP fluids (forge:experience,",
                    "forge:xpjuice). Uses Apotheosis enchanting stats when Apotheosis is installed."
            ).push("autoEnchanter");

            AUTO_ENCHANTER_ENABLED = builder.comment(
                    "Enables the auto enchanter (hide from JEI/EMI, tooltip, and stops working when off)."
            ).define("enabled", true);

            AUTO_ENCHANTER_COST = intInRange(builder,
                    "cost", 10, 0, 100,
                    "XP cost multiplier for the auto enchanter.");

            builder.pop();

            builder.comment(
                    "Energy storage feature.",
                    "Standalone AE energy cells in ten tiers (1k to 256m) that store network power.",
                    "Crafted in the entropy cradle from AE energy cells and crafting storages.",
                    "Note: disabling only hides them from JEI/EMI and marks them in the tooltip;",
                    "placed blocks keep storing power."
            ).push("energyStorage");

            ENERGY_STORAGE_ENABLED = builder.comment(
                    "Enables the energy storage blocks (hide from JEI/EMI and tooltip when off)."
            ).define("enabled", true);

            ENERGY_STORAGE_CAPACITY_MULTIPLIER = intInRange(builder,
                    "capacityMultiplier", 1, 1, Integer.MAX_VALUE,
                    "Multiplies the stored-energy capacity of every energy storage tier."
            );

            builder.pop();

            builder.comment(
                    "Entropy cradle feature.",
                    "A multiblock that stores AE power as FE and, on a redstone pulse, transmutes a",
                    "block pattern placed inside its 5x5x5 chamber into a result block."
            ).push("entropyCradle");

            CRADLE_CAPACITY = intInRange(builder,
                    "capacity", 600_000_000, 1, Integer.MAX_VALUE,
                    "Maximum FE the entropy cradle can store."
            );

            CRADLE_CHARGING_SPEED = intInRange(builder,
                    "chargingSpeed", 10_000_000, 1, Integer.MAX_VALUE,
                    "Maximum AE the entropy cradle can pull from the network per tick."
            );

            CRADLE_COST = intInRange(builder,
                    "cost", 600_000_000, 1, Integer.MAX_VALUE,
                    "FE consumed by the entropy cradle per transmutation pulse."
            );

            builder.pop();

            builder.comment(
                    "Entity ticker feature.",
                    "A cable part that force-ticks the block entity it faces, multiplying its speed",
                    "based on installed speed cards at the cost of network power."
            ).push("entityTicker");

            ENTITY_TICKER_ENABLED = builder.comment(
                    "Enables the entity ticker part."
            ).define("enabled", true);

            ENTITY_TICKER_COST = intInRange(builder,
                    "cost", 512, 0, Integer.MAX_VALUE,
                    "Base AE cost per tick. Scales by 4^(speed cards)."
            );

            ENTITY_TICKER_MAX_SPEED_CARDS = intInRange(builder,
                    "maxSpeedCards", 8, 0, 8,
                    "Maximum speed cards the entity ticker accepts."
            );

            ENTITY_TICKER_BLACKLIST = builder.comment(
                    "Block ids the entity ticker refuses to tick, e.g. minecraft:spawner."
            ).defineList("blacklist", List.of(), obj -> obj instanceof String);

            builder.pop();

            builder.pop();
        }

        private static ForgeConfigSpec.IntValue nonNegativeInt(
                ForgeConfigSpec.Builder builder,
                String key,
                int defaultValue,
                String... comment
        ) {
            return builder.comment(comment).defineInRange(key, defaultValue, 0, Integer.MAX_VALUE);
        }

        private static ForgeConfigSpec.IntValue intInRange(
                ForgeConfigSpec.Builder builder,
                String key,
                int defaultValue,
                int min,
                int max,
                String... comment
        ) {
            return builder.comment(comment).defineInRange(key, defaultValue, min, max);
        }

        private static boolean isExtraQEntry(String entry) {
            int eq = entry.indexOf('=');
            if (eq <= 0 || eq == entry.length() - 1) {
                return false;
            }
            if (ResourceLocation.tryParse(entry.substring(0, eq).trim()) == null) {
                return false;
            }
            try {
                Integer.parseInt(entry.substring(eq + 1).trim());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private InsaneConfig() {
    }
}
