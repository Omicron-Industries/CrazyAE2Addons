package net.oktawia.insaneae2addons.multiblock;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition.TrackingMode;
import net.oktawia.insaneae2addons.InsaneConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class InsaneMultiblocks {
    private InsaneMultiblocks() {
    }

    public static final char UNIT_FRAME_SYMBOL = 'A';
    public static final char UNIT_GLASS_SYMBOL = 'B';
    public static final char UNIT_TANK_SYMBOL = 'E';
    public static final char UNIT_CORE_SYMBOL = 'Q';

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

    private static MultiblockDefinition buildResearchUnit() {
        return MultiblockDefinition.builder()
                .symbol(UNIT_FRAME_SYMBOL, TrackingMode.CALLBACK, "insaneae2addons:research_unit_frame")
                .symbol(UNIT_GLASS_SYMBOL, TrackingMode.POLLED, "ae2:quartz_vibrant_glass")
                .symbol(UNIT_TANK_SYMBOL, TrackingMode.POLLED, "ae2:sky_stone_tank")
                .symbol(UNIT_CORE_SYMBOL, TrackingMode.POLLED, coreBlocks())
                .layer("AAAAA", "ABBBA", "ABBBA", "ABBBA", "AACAA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("ABBBA", "BQQQB", "BQQQB", "BQQQB", "ABBBA")
                .layer("AAAAA", "ABBBA", "ABEBA", "ABBBA", "AAAAA")
                .build();
    }

    private static String[] coreBlocks() {
        Set<String> ids = new LinkedHashSet<>(List.of(UNIT_CORE_BASE_BLOCKS));

        for (String extra : InsaneConfig.COMMON.RESEARCH_UNIT_EXTRA_Q_BLOCKS.get()) {
            ResourceLocation id = ResourceLocation.tryParse(extra);
            if (id != null && ForgeRegistries.BLOCKS.containsKey(id)) {
                ids.add(id.toString());
            }
        }

        return ids.toArray(String[]::new);
    }
}
