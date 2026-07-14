package net.oktawia.insaneae2addons.defs;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition.TrackingMode;
import net.oktawia.insaneae2addons.InsaneConfig;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class InsaneMultiblocks {
    private InsaneMultiblocks() {
    }

    private static final String[] UNIT_CORE_BASE_BLOCKS = {
            "ae2:1k_crafting_storage",
            "ae2:4k_crafting_storage",
            "ae2:16k_crafting_storage",
            "ae2:64k_crafting_storage",
            "ae2:256k_crafting_storage",
            "minecraft:air"
    };

    private static volatile MultiblockDefinition researchUnit;

    public static MultiblockDefinition researchUnit() {
        MultiblockDefinition local = researchUnit;
        if (local != null) {
            return local;
        }

        synchronized (InsaneMultiblocks.class) {
            if (researchUnit == null) {
                researchUnit = buildResearchUnit();
            }
            return researchUnit;
        }
    }

    private static volatile MultiblockDefinition entropyCradle;

    public static MultiblockDefinition entropyCradle() {
        MultiblockDefinition local = entropyCradle;
        if (local != null) {
            return local;
        }

        synchronized (InsaneMultiblocks.class) {
            if (entropyCradle == null) {
                entropyCradle = buildEntropyCradle();
            }
            return entropyCradle;
        }
    }

    private static MultiblockDefinition buildEntropyCradle() {
        return MultiblockDefinition.builder()
                .symbol('B', TrackingMode.CALLBACK, "insaneae2addons:entropy_cradle")
                .symbol('A', TrackingMode.CALLBACK, "insaneae2addons:entropy_cradle_capacitor")
                .layer(
                        "A.BBBBBBB.A",
                        ".BBBBBBBBB.",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        "BBBBBBBBBBB",
                        ".BBBBBBBBB.",
                        "A.BBBCBBB.A"
                )
                .layer(
                        "A.B.....B.A",
                        ".BB.....BB.",
                        "BBB.....BBB",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "BBB.....BBB",
                        ".BB.....BB.",
                        "A.B.....B.A"
                )
                .layer(
                        "A.B.....B.A",
                        ".BB.....BB.",
                        "BBB.....BBB",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "BBB.....BBB",
                        ".BB.....BB.",
                        "A.B.....B.A"
                )
                .layer(
                        "A.B.....B.A",
                        ".BB.....BB.",
                        "BBB.....BBB",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "BBB.....BBB",
                        ".BB.....BB.",
                        "A.B.....B.A"
                )
                .layer(
                        "A.........A",
                        "..B.....B..",
                        ".BB.....BB.",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        ".BB.....BB.",
                        "..B.....B..",
                        "A.........A"
                )
                .layer(
                        "A.........A",
                        "..B.....B..",
                        ".BB.....BB.",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        "...........",
                        ".BB.....BB.",
                        "..B.....B..",
                        "A.........A"
                )
                .layer(
                        "...........",
                        "...........",
                        "..BBB.BBB..",
                        "..B.....B..",
                        "..B.....B..",
                        "...........",
                        "..B.....B..",
                        "..B.....B..",
                        "..BBB.BBB..",
                        "...........",
                        "..........."
                )
                .layer(
                        "...........",
                        "...........",
                        "....BBB....",
                        "...........",
                        "..B.....B..",
                        "..B.....B..",
                        "..B.....B..",
                        "...........",
                        "....BBB....",
                        "...........",
                        "..........."
                )
                .build();
    }

    private static MultiblockDefinition buildResearchUnit() {
        return MultiblockDefinition.builder()
                .symbol('A', TrackingMode.CALLBACK, "insaneae2addons:research_unit_frame")
                .symbol('B', TrackingMode.POLLED, "ae2:quartz_vibrant_glass")
                .symbol('E', TrackingMode.POLLED, "ae2:sky_stone_tank")
                .symbol('Q', TrackingMode.POLLED, coreBlocks())
                .layer("AAAAA", "ABBBA", "ABBBA", "ABBBA", "AACAA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("AAAAA", "ABBBA", "ABEBA", "ABBBA", "AAAAA")
                .build();
    }

    private static String[] coreBlocks() {
        Set<String> ids = new LinkedHashSet<>(List.of(UNIT_CORE_BASE_BLOCKS));

        for (String entry : InsaneConfig.COMMON.RESEARCH_UNIT_EXTRA_Q_BLOCKS.get()) {
            int eq = entry.indexOf('=');
            String idPart = eq >= 0 ? entry.substring(0, eq).trim() : entry.trim();
            ResourceLocation id = ResourceLocation.tryParse(idPart);
            if (id != null && ForgeRegistries.BLOCKS.containsKey(id)) {
                ids.add(id.toString());
            }
        }

        return ids.toArray(String[]::new);
    }
}
