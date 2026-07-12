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
                    "Extra block ids accepted in the 'Q' slots of the Research Unit structure.",
                    "Block ids as namespace:path, e.g. minecraft:iron_block."
            ).defineList(
                    "unitExtraQBlocks",
                    List.of(),
                    obj -> obj instanceof String s && ResourceLocation.tryParse(s) != null
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
    }

    private InsaneConfig() {
    }
}
