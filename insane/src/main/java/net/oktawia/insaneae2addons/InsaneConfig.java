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

        public final ForgeConfigSpec.BooleanValue SPAWNER_EXTRACTOR_PEACEFUL;

        public final ForgeConfigSpec.BooleanValue PENROSE_SPHERE_ENABLED;
        public final ForgeConfigSpec.BooleanValue PENROSE_FE_OUTPUT_ENABLED;
        public final ForgeConfigSpec.BooleanValue PENROSE_EU_OUTPUT_ENABLED;
        public final ForgeConfigSpec.BooleanValue PENROSE_MELTDOWN_EXPLOSIONS;
        public final ForgeConfigSpec.IntValue PENROSE_MELTDOWN_FIELD_RADIUS;
        public final ForgeConfigSpec.IntValue PENROSE_MELTDOWN_FIELD_BUDGET_MICROS;
        public final ForgeConfigSpec.LongValue PENROSE_CREATION_COST_SINGU;
        public final ForgeConfigSpec.LongValue PENROSE_INITIAL_MASS_MU;
        public final ForgeConfigSpec.LongValue PENROSE_MASS_WINDOW_MU;
        public final ForgeConfigSpec.DoubleValue PENROSE_MASS_FACTOR_MAX;
        public final ForgeConfigSpec.DoubleValue PENROSE_DUTY_COMPENSATION;
        public final ForgeConfigSpec.DoubleValue PENROSE_FE_BASE_PER_SINGU_FLOW;
        public final ForgeConfigSpec.DoubleValue PENROSE_HEAT_PER_SINGU_FLOW;
        public final ForgeConfigSpec.DoubleValue PENROSE_HEAT_PEAK_GK;
        public final ForgeConfigSpec.DoubleValue PENROSE_MAX_HEAT_GK;
        public final ForgeConfigSpec.IntValue PENROSE_MAX_FEED_PER_TICK;
        public final ForgeConfigSpec.ConfigValue<String> PENROSE_COOLANT_FLUID;
        public final ForgeConfigSpec.DoubleValue PENROSE_COOLANT_MB_PER_GK;
        public final ForgeConfigSpec.IntValue PENROSE_COOLANT_VENT_CAPACITY;

        public final ForgeConfigSpec.IntValue MOB_FARM_BASE_SPEED;
        public final ForgeConfigSpec.IntValue MOB_FARM_SPEED_PER_CARD;

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
                    "Portable Penrose Sphere feature.",
                    "A multiblock that feeds matter into a black hole and taps the accretion disc for power.",
                    "Note: disabling only hides its items from JEI/EMI and marks them in the tooltip;",
                    "placed spheres keep running."
            ).push("penroseSphere");

            PENROSE_SPHERE_ENABLED = builder.comment(
                    "Enables the Portable Penrose Sphere (hide from JEI/EMI and tooltip when off)."
            ).define("enabled", true);

            PENROSE_FE_OUTPUT_ENABLED = builder.comment(
                    "Lets the sphere expose its FE buffer through the controller and its output ports."
            ).define("feOutputEnabled", true);

            PENROSE_EU_OUTPUT_ENABLED = builder.comment(
                    "Lets GregTech energy output hatches count as sphere ports and drain its FE buffer as EU (1:1).",
                    "Has no effect without GregTech."
            ).define("euOutputEnabled", true);

            PENROSE_MAX_FEED_PER_TICK = intInRange(builder,
                    "maxFeedPerTick", 4_096, 0, Integer.MAX_VALUE,
                    "Hard cap on singularities injected into the accretion disc per tick.",
                    "Units: items/t."
            );

            builder.comment(
                    "Mass, heat and power curves. Changing these rebalances the whole feature."
            ).push("balance");

            PENROSE_CREATION_COST_SINGU = longInRange(builder,
                    "creationCostSingularities", 32_512L, 0L, Long.MAX_VALUE,
                    "Super singularities that must sit in the disk on the central research pedestal to create the black hole.",
                    "Units: items."
            );

            PENROSE_INITIAL_MASS_MU = longInRange(builder,
                    "initialMass", 32_768L * 8_192L, 0L, Long.MAX_VALUE,
                    "Black hole mass right after ignition. Units: MU (internal mass unit)."
            );

            PENROSE_MASS_WINDOW_MU = longInRange(builder,
                    "massWindow", 1_113_600L, 0L, Long.MAX_VALUE,
                    "How far above the initial mass the black hole may grow before meltdown.",
                    "Max mass = initialMass + massWindow, sweet spot sits in the middle. Units: MU."
            );

            PENROSE_MASS_FACTOR_MAX = doubleInRange(builder,
                    "massFactorMax", 2.0, 1.0, 64.0,
                    "Output multiplier at the sweet spot. Falls off linearly to 1.0 at the window edges."
            );

            PENROSE_DUTY_COMPENSATION = doubleInRange(builder,
                    "dutyCompensation", 4.0 / 3.0, 0.0, 1000.0,
                    "Duty-cycle compensation applied to generated power."
            );

            PENROSE_FE_BASE_PER_SINGU_FLOW = doubleInRange(builder,
                    "feBasePerSinguFlow", (double) (1L << 27) * 0.5, 0.0, 1.0e18,
                    "FE per tick produced per 1.0 singu/t of disc flow at full heat efficiency",
                    "and mass factor 1.0."
            );

            PENROSE_HEAT_PER_SINGU_FLOW = doubleInRange(builder,
                    "heatPerSinguFlow", 0.5, 0.0, 1.0e12,
                    "Heat added per tick per 1.0 singu/t of disc flow at mass factor 1.0. Units: GK/t."
            );

            PENROSE_HEAT_PEAK_GK = doubleInRange(builder,
                    "heatPeak", 50_000.0, 1.0, 1.0e12,
                    "Heat at which efficiency peaks. Efficiency curve is 2x-x^2 for x=heat/peak,",
                    "clamped to [0..1], so running colder or hotter than the peak both cost output.",
                    "Units: GK."
            );

            PENROSE_MAX_HEAT_GK = doubleInRange(builder,
                    "maxHeat", 100_000.0, 0.0, 1.0e12,
                    "Meltdown threshold. Reaching this heat destroys the black hole. Units: GK."
            );

            builder.pop();

            builder.comment(
                    "Coolant consumed by heat vents. Swap the fluid for anything the pack provides -",
                    "the vent only cares that the fluid id resolves and that it is fed enough of it."
            ).push("coolant");

            PENROSE_COOLANT_FLUID = builder.comment(
                    "Fluid the heat vents accept, as a registry id."
            ).define("fluid", "insaneae2addons:penrose_coolant");

            PENROSE_COOLANT_MB_PER_GK = doubleInRange(builder,
                    "mbPerGK", 125.0, 0.0001, 1.0e6,
                    "Millibuckets of coolant needed to remove 1 GK of heat.",
                    "Default 125 makes the 16 singu/t optimum (8 GK/t) draw ~1 B/t; overfeeding scales the",
                    "coolant demand into the unproducible range as an intentional noob trap."
            );

            PENROSE_COOLANT_VENT_CAPACITY = intInRange(builder,
                    "ventCapacity", 64_000, 1, Integer.MAX_VALUE,
                    "Coolant buffer of a single heat vent, in millibuckets."
            );

            builder.pop();

            builder.comment(
                    "What happens when the sphere melts down."
            ).push("meltdown");

            PENROSE_MELTDOWN_EXPLOSIONS = builder.comment(
                    "Lets a meltdown explode and spawn the black hole field. Turn off for a silent reset."
            ).define("explosionsEnabled", true);

            PENROSE_MELTDOWN_FIELD_RADIUS = intInRange(builder,
                    "fieldRadius", 768, 0, 4096,
                    "Radius of the block-eating black hole field left behind by a meltdown.",
                    "0 disables the field but keeps the explosion."
            );

            PENROSE_MELTDOWN_FIELD_BUDGET_MICROS = intInRange(builder,
                    "fieldBudgetMicros", 100000, 100, 200000,
                    "Server time per tick the black hole field may spend eating blocks, in microseconds.",
                    "Shared between all active fields. The default deliberately exceeds a full tick (50000 us):",
                    "a meltdown is meant to hurt, and the field should carve the sphere out fast."
            );

            builder.pop();

            builder.pop();

            builder.comment(
                    "Spawner extractor feature.",
                    "A multiblock built around a vanilla spawner. It suppresses the spawner and",
                    "inserts the mob it would spawn into the network as a mob key instead."
            ).push("spawnerExtractor");

            SPAWNER_EXTRACTOR_PEACEFUL = builder.comment(
                    "Lets the spawner extractor keep working on peaceful difficulty."
            ).define("worksOnPeaceful", true);

            builder.pop();

            builder.comment(
                    "Mob farm feature.",
                    "A multiblock that consumes mob keys from the network and inserts the loot and",
                    "experience shards those mobs would drop when killed by a player."
            ).push("mobFarm");

            MOB_FARM_BASE_SPEED = intInRange(builder,
                    "baseSpeed", 16, 1, Integer.MAX_VALUE,
                    "Mobs killed per cycle (every 20 ticks) without any speed cards."
            );

            MOB_FARM_SPEED_PER_CARD = intInRange(builder,
                    "speedPerCard", 12, 0, Integer.MAX_VALUE,
                    "Extra mobs killed per cycle for each installed speed card."
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

        private static ForgeConfigSpec.LongValue longInRange(
                ForgeConfigSpec.Builder builder,
                String key,
                long defaultValue,
                long min,
                long max,
                String... comment
        ) {
            return builder.comment(comment).defineInRange(key, defaultValue, min, max);
        }

        private static ForgeConfigSpec.DoubleValue doubleInRange(
                ForgeConfigSpec.Builder builder,
                String key,
                double defaultValue,
                double min,
                double max,
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
