package net.oktawia.insaneae2addons.defs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition.TrackingMode;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.logic.penrose.PenroseEnergyExport;

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

    private static final int SPHERE_RADIUS = 23;
    private static final int SPHERE_CORE_RADIUS = 9;
    private static final int RING_THICKNESS = 3;
    private static final int SKIN_THICKNESS = 2;
    private static final int SPOKE_GAP = 2;
    private static final int CORE_SHELL_THICKNESS = 2;
    private static final int CORE_BAND_HALF = 1;
    private static final int SPOKE_THICKNESS = 5;
    private static final int SPOKE_TIP_LENGTH = 3;
    private static final int SPHERE_HALF_SIZE = SPHERE_RADIUS + SKIN_THICKNESS;
    private static final double HOOP_BULGE = 2.0;

    private static volatile MultiblockDefinition penroseSphere;

    public static int penroseCoreRadius() {
        return SPHERE_CORE_RADIUS;
    }

    public static MultiblockDefinition penroseSphere() {
        MultiblockDefinition local = penroseSphere;
        if (local != null) {
            return local;
        }

        synchronized (InsaneMultiblocks.class) {
            if (penroseSphere == null) {
                penroseSphere = buildPenroseSphere();
            }
            return penroseSphere;
        }
    }

    private static MultiblockDefinition buildPenroseSphere() {
        int size = 2 * SPHERE_HALF_SIZE + 1;
        char[][][] grid = new char[size][size][size];
        for (char[][] layer : grid) {
            for (char[] row : layer) {
                Arrays.fill(row, '.');
            }
        }

        addCoreSphere(grid);

        Set<BlockPos> hoops = new LinkedHashSet<>();
        addRoundHoops(hoops, SPHERE_RADIUS, RING_THICKNESS);

        Set<BlockPos> spokes = new LinkedHashSet<>();
        addSpokes(spokes, SPOKE_THICKNESS);

        for (BlockPos coil : hoops) {
            put(grid, coil, 'B');
        }

        for (BlockPos coil : spokes) {
            put(grid, coil, 'B');
        }

        addSkin(grid, hoops, true);
        addSkin(grid, spokes, true);

        put(grid, new BlockPos(SPHERE_RADIUS + 2, 0, 0), 'P');
        put(grid, new BlockPos(-SPHERE_RADIUS - 2, 0, 0), 'P');
        put(grid, new BlockPos(0, 0, SPHERE_RADIUS + 2), 'P');
        put(grid, new BlockPos(0, 0, -SPHERE_RADIUS - 2), 'P');

        int laserAlong = SPHERE_CORE_RADIUS + SKIN_THICKNESS + SPOKE_GAP + 1 - 3;
        put(grid, new BlockPos(laserAlong, 0, 0), 'L');
        put(grid, new BlockPos(-laserAlong, 0, 0), 'L');
        put(grid, new BlockPos(0, laserAlong, 0), 'L');
        put(grid, new BlockPos(0, -laserAlong, 0), 'L');

        put(grid, new BlockPos(0, 0, SPHERE_CORE_RADIUS - 1), 'C');

        MultiblockDefinition.Builder builder = MultiblockDefinition.builder()
                .symbol('A', TrackingMode.CALLBACK,
                        "insaneae2addons:penrose_frame",
                        "insaneae2addons:penrose_injection_port",
                        "insaneae2addons:penrose_heat_vent",
                        "insaneae2addons:penrose_hawking_vent",
                        "insaneae2addons:penrose_mass_emitter",
                        "insaneae2addons:penrose_heat_emitter")
                .symbol('B', TrackingMode.CALLBACK, "insaneae2addons:penrose_coil")
                .symbol('G', TrackingMode.CALLBACK,
                        "insaneae2addons:penrose_glass",
                        "insaneae2addons:penrose_frame")
                .symbol('L', TrackingMode.POLLED,
                        "insaneae2addons:penrose_laser",
                        "minecraft:air")
                .symbol('P', TrackingMode.POLLED, penrosePortBlockIds());

        for (char[][] layer : grid) {
            String[] rows = new String[size];
            for (int z = 0; z < size; z++) {
                rows[z] = new String(layer[z]);
            }
            builder.layer(rows);
        }

        return builder.build();
    }

    private static String[] penrosePortBlockIds() {
        List<String> ids = new ArrayList<>(List.of("insaneae2addons:penrose_port"));
        PenroseEnergyExport export = PenroseEnergyExport.get();
        if (export != null && InsaneConfig.COMMON.PENROSE_EU_OUTPUT_ENABLED.get()) {
            ids.addAll(export.portBlockIds());
        }
        return ids.toArray(String[]::new);
    }

    private static void addRoundHoops(Set<BlockPos> out, int radius, int thickness) {
        int half = (thickness - 1) / 2;
        double outerLimit = radius + 0.5;
        double innerLimit = radius - thickness + 0.5;

        for (int a = -radius; a <= radius; a++) {
            for (int b = -radius; b <= radius; b++) {
                double distance = hoopDistance(a, b);
                if (distance > outerLimit || distance < innerLimit) {
                    continue;
                }

                for (int d = -half; d <= half; d++) {
                    out.add(new BlockPos(a, b, d));
                    out.add(new BlockPos(a, d, b));
                    out.add(new BlockPos(d, a, b));
                }
            }
        }
    }

    private static double hoopDistance(int a, int b) {
        double squared = a * a + b * b;
        if (squared <= 0.0) {
            return 0.0;
        }

        double doubleAngleSine = 2.0 * a * b / squared;
        return Math.sqrt(squared) - HOOP_BULGE * doubleAngleSine * doubleAngleSine;
    }

    private static void addCoreSphere(char[][][] grid) {
        Set<BlockPos> remaining = new LinkedHashSet<>();
        for (int x = -SPHERE_CORE_RADIUS; x <= SPHERE_CORE_RADIUS; x++) {
            for (int y = -SPHERE_CORE_RADIUS; y <= SPHERE_CORE_RADIUS; y++) {
                for (int z = -SPHERE_CORE_RADIUS; z <= SPHERE_CORE_RADIUS; z++) {
                    if (Math.sqrt(x * x + y * y + z * z) <= SPHERE_CORE_RADIUS) {
                        remaining.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        Set<BlockPos> shell = new LinkedHashSet<>();
        for (int layer = 0; layer < CORE_SHELL_THICKNESS; layer++) {
            Set<BlockPos> peeled = new LinkedHashSet<>();

            for (BlockPos pos : remaining) {
                for (Direction direction : Direction.values()) {
                    if (!remaining.contains(pos.relative(direction))) {
                        peeled.add(pos);
                        break;
                    }
                }
            }

            shell.addAll(peeled);
            remaining.removeAll(peeled);
        }

        for (BlockPos pos : shell) {
            if (isAxisTip(pos.getX(), pos.getY(), pos.getZ())) {
                continue;
            }

            boolean band = Math.abs(pos.getX()) <= CORE_BAND_HALF
                    || Math.abs(pos.getY()) <= CORE_BAND_HALF
                    || Math.abs(pos.getZ()) <= CORE_BAND_HALF;

            put(grid, pos, band ? 'B' : 'G');
        }
    }

    private static void addSpokes(Set<BlockPos> out, int thickness) {
        int half = (thickness - 1) / 2;
        int start = SPHERE_CORE_RADIUS + SKIN_THICKNESS + SPOKE_GAP + 1;

        for (int along = start; along <= SPHERE_RADIUS; along++) {
            double radius = spokeRadius(along - start, half);

            for (int u = -half; u <= half; u++) {
                for (int v = -half; v <= half; v++) {
                    if (u * u + v * v > radius * radius) {
                        continue;
                    }

                    out.add(new BlockPos(along, u, v));
                    out.add(new BlockPos(-along, u, v));
                    out.add(new BlockPos(v, u, along));
                    out.add(new BlockPos(v, u, -along));
                    out.add(new BlockPos(u, along, v));
                    out.add(new BlockPos(u, -along, v));
                }
            }
        }
    }

    private static double spokeRadius(int fromTip, int half) {
        if (fromTip >= SPOKE_TIP_LENGTH) {
            return half;
        }

        double taper = (double) (SPOKE_TIP_LENGTH - fromTip) / (SPOKE_TIP_LENGTH + 1);
        return Math.max(1.0, half * Math.sqrt(Math.max(0.0, 1.0 - taper * taper)));
    }

    private static void addSkin(char[][][] grid, Set<BlockPos> coils, boolean rounded) {
        int limit = SKIN_THICKNESS * SKIN_THICKNESS;

        for (BlockPos coil : coils) {
            for (int dx = -SKIN_THICKNESS; dx <= SKIN_THICKNESS; dx++) {
                for (int dy = -SKIN_THICKNESS; dy <= SKIN_THICKNESS; dy++) {
                    for (int dz = -SKIN_THICKNESS; dz <= SKIN_THICKNESS; dz++) {
                        if (rounded && dx * dx + dy * dy + dz * dz > limit) {
                            continue;
                        }

                        BlockPos skin = coil.offset(dx, dy, dz);
                        if (inside(skin) && get(grid, skin) == '.') {
                            put(grid, skin, 'A');
                        }
                    }
                }
            }
        }
    }

    private static boolean inside(BlockPos pos) {
        return Math.max(Math.max(Math.abs(pos.getX()), Math.abs(pos.getY())), Math.abs(pos.getZ())) <= SPHERE_HALF_SIZE;
    }

    private static boolean isAxisTip(int x, int y, int z) {
        int nonZero = (x != 0 ? 1 : 0) + (y != 0 ? 1 : 0) + (z != 0 ? 1 : 0);
        return nonZero == 1
                && Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z)) == SPHERE_CORE_RADIUS;
    }

    private static char get(char[][][] grid, BlockPos pos) {
        return grid[pos.getY() + SPHERE_HALF_SIZE][pos.getZ() + SPHERE_HALF_SIZE][pos.getX() + SPHERE_HALF_SIZE];
    }

    private static void put(char[][][] grid, BlockPos pos, char symbol) {
        grid[pos.getY() + SPHERE_HALF_SIZE][pos.getZ() + SPHERE_HALF_SIZE][pos.getX() + SPHERE_HALF_SIZE] = symbol;
    }

    private static volatile MultiblockDefinition mobFarm;

    public static MultiblockDefinition mobFarm() {
        MultiblockDefinition local = mobFarm;
        if (local != null) {
            return local;
        }

        synchronized (InsaneMultiblocks.class) {
            if (mobFarm == null) {
                mobFarm = buildMobFarm();
            }
            return mobFarm;
        }
    }

    private static MultiblockDefinition buildMobFarm() {
        return MultiblockDefinition.builder()
                .symbol('A', TrackingMode.CALLBACK, "insaneae2addons:mob_farm_wall")
                .symbol('B', TrackingMode.CALLBACK, "insaneae2addons:mob_farm_collector")
                .symbol('F', TrackingMode.CALLBACK, "insaneae2addons:mob_farm_input")
                .symbol('G', TrackingMode.CALLBACK, "insaneae2addons:mob_farm_damage")
                .symbol('E', TrackingMode.POLLED, "ae2:quartz_glass")
                .layer("AAAAA", "AAAAA", "AAAAA", "AAAAA", "AAAAA")
                .layer("AAAAA", "ABBBA", "ABBBA", "ABBBA", "AACAA")
                .layer("AEEEA", "E...E", "E...E", "E...E", "AEEEA")
                .layer("AEEEA", "E...E", "E...E", "E...E", "AEEEA")
                .layer("AFFFA", "FGGGF", "FGGGF", "FGGGF", "AFFFA")
                .layer("AFFFA", "FGGGF", "FGGGF", "FGGGF", "AFFFA")
                .layer("AFFFA", "FGGGF", "FGGGF", "FGGGF", "AFFFA")
                .layer("AAAAA", "AAAAA", "AAAAA", "AAAAA", "AAAAA")
                .build();
    }

    private static volatile MultiblockDefinition spawnerExtractor;

    public static MultiblockDefinition spawnerExtractor() {
        MultiblockDefinition local = spawnerExtractor;
        if (local != null) {
            return local;
        }

        synchronized (InsaneMultiblocks.class) {
            if (spawnerExtractor == null) {
                spawnerExtractor = buildSpawnerExtractor();
            }
            return spawnerExtractor;
        }
    }

    private static MultiblockDefinition buildSpawnerExtractor() {
        return MultiblockDefinition.builder()
                .symbol('A', TrackingMode.CALLBACK, "insaneae2addons:spawner_extractor_wall")
                .symbol('B', TrackingMode.POLLED, "ae2:quartz_vibrant_glass")
                .symbol('D', TrackingMode.POLLED, "minecraft:spawner")
                .layer(
                        ".......",
                        "..AAA..",
                        ".AAAAA.",
                        ".AAAAA.",
                        ".AAAAA.",
                        "..AAA..",
                        ".......")
                .layer(
                        "..AAA..",
                        ".A...A.",
                        "A.....A",
                        "A..A..A",
                        "A.....A",
                        ".A...A.",
                        "..ACA..")
                .layer(
                        ".ABBBA.",
                        "A.....A",
                        "B.....B",
                        "B..A..B",
                        "B.....B",
                        "A.....A",
                        ".ABBBA.")
                .layer(
                        ".ABBBA.",
                        "A.....A",
                        "B.....B",
                        "B..D..B",
                        "B.....B",
                        "A.....A",
                        ".ABBBA.")
                .layer(
                        ".ABBBA.",
                        "A.....A",
                        "B.....B",
                        "B..A..B",
                        "B.....B",
                        "A.....A",
                        ".ABBBA.")
                .layer(
                        "..AAA..",
                        ".A...A.",
                        "A.....A",
                        "A..A..A",
                        "A.....A",
                        ".A...A.",
                        "..AAA..")
                .layer(
                        ".......",
                        "..AAA..",
                        ".AAAAA.",
                        ".AAAAA.",
                        ".AAAAA.",
                        "..AAA..",
                        ".......")
                .build();
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
                        "A.BBBCBBB.A")
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
                        "A.B.....B.A")
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
                        "A.B.....B.A")
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
                        "A.B.....B.A")
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
                        "A.........A")
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
                        "A.........A")
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
                        "...........")
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
                        "...........")
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
