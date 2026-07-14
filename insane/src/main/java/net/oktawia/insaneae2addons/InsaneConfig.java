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

        public final ForgeConfigSpec.BooleanValue NBT_VIEW_CELL_ENABLED;
        public final ForgeConfigSpec.BooleanValue NBT_STORAGE_BUS_ENABLED;
        public final ForgeConfigSpec.BooleanValue NBT_EXPORT_BUS_ENABLED;

        public final ForgeConfigSpec.IntValue CRADLE_CAPACITY;
        public final ForgeConfigSpec.IntValue CRADLE_CHARGING_SPEED;
        public final ForgeConfigSpec.IntValue CRADLE_COST;

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
