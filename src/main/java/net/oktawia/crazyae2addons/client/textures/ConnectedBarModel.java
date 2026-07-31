package net.oktawia.crazyae2addons.client.textures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public final class ConnectedBarModel extends BakedModelWrapper<BakedModel> implements PreviewQuadProvider {

    private static final ModelProperty<Integer> CONNECTIONS = new ModelProperty<>();

    private static final float TILE_U = 16.0f / 5.0f;
    private static final int TILE_OUTER = 0;
    private static final int TILE_MIDDLE = 1;
    private static final int TILE_VERTICAL = 2;
    private static final int TILE_HORIZONTAL = 3;
    private static final int TILE_INNER = 4;

    private static final int DIAGONAL_BIT_OFFSET = 6;
    private static final int CORNER_BIT_OFFSET = 18;
    private static final int[][] PAIR_INDEX = buildPairIndex();

    private final ConnectedTextureEntry entry;
    private final float thickness;
    private final FaceBakery bakery = new FaceBakery();

    private final Map<ResourceLocation, TextureAtlasSprite> spriteCache = new ConcurrentHashMap<>();
    private final Map<BarCacheKey, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();
    private final Map<BarCacheKey, List<BakedQuad>> previewCache = new ConcurrentHashMap<>();

    public ConnectedBarModel(BakedModel originalModel, ConnectedTextureEntry entry) {
        super(originalModel);
        this.entry = entry;
        this.thickness = entry.barThickness();
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        int mask = 0;

        for (Direction dir : Direction.values()) {
            if (connects(level, pos, state, pos.relative(dir), dir)) {
                mask |= 1 << dir.ordinal();
            }
        }

        for (Direction first : Direction.values()) {
            for (Direction second : Direction.values()) {
                if (first.getAxis() == second.getAxis() || first.ordinal() > second.ordinal()) {
                    continue;
                }

                BlockPos diagonal = pos.relative(first).relative(second);
                if (connects(level, pos, state, diagonal, first)) {
                    mask |= 1 << (DIAGONAL_BIT_OFFSET + PAIR_INDEX[first.ordinal()][second.ordinal()]);
                }
            }
        }

        for (Direction x : axisDirections(Direction.Axis.X)) {
            for (Direction y : axisDirections(Direction.Axis.Y)) {
                for (Direction z : axisDirections(Direction.Axis.Z)) {
                    BlockPos corner = pos.relative(x).relative(y).relative(z);
                    if (connects(level, pos, state, corner, x)) {
                        mask |= 1 << (CORNER_BIT_OFFSET + cornerIndex(x, y, z));
                    }
                }
            }
        }

        return modelData.derive().with(CONNECTIONS, mask).build();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData,
            RenderType renderType) {
        if (state == null) {
            return originalModel.getQuads(state, side, rand, modelData, renderType);
        }

        if (side != null) {
            return List.of();
        }

        int mask = modelData.has(CONNECTIONS) ? modelData.get(CONNECTIONS) : 0;
        return quadCache.computeIfAbsent(new BarCacheKey(entry.texture(state), mask), this::bake);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return originalModel.getParticleIcon(modelData);
    }

    @Override
    public List<BakedQuad> previewQuads(BlockState state, Direction face) {
        if (state == null || face == null) {
            return List.of();
        }
        return previewCache.computeIfAbsent(new BarCacheKey(entry.texture(state), face.ordinal()), key -> {
            TextureAtlasSprite sprite = spriteCache.computeIfAbsent(
                    key.texture(),
                    tex -> Minecraft.getInstance()
                            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                            .apply(tex));

            float[] from = { 0.0f, 0.0f, 0.0f };
            float[] to = { 16.0f, 16.0f, 16.0f };
            int f = axisIndex(face.getAxis());
            if (face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                from[f] = 16.0f;
            } else {
                to[f] = 0.0f;
            }

            return List.of(bakeFace(from, to, face, TILE_MIDDLE, sprite, key.texture()));
        });
    }

    private boolean connects(BlockAndTintGetter level, BlockPos pos, BlockState state, BlockPos other, Direction face) {
        return entry.rule().connects(level, pos, state, other, level.getBlockState(other), face);
    }

    private List<BakedQuad> bake(BarCacheKey key) {
        TextureAtlasSprite sprite = spriteCache.computeIfAbsent(
                key.texture(),
                tex -> Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(tex));

        List<BakedQuad> quads = new ArrayList<>();

        for (Direction first : Direction.values()) {
            for (Direction second : Direction.values()) {
                if (first.getAxis() == second.getAxis() || first.ordinal() > second.ordinal()) {
                    continue;
                }

                if (hasBar(key.mask(), first, second)) {
                    addBar(quads, first, second, key.mask(), sprite, key.texture());
                }
            }
        }

        addCorners(quads, key.mask(), sprite, key.texture());
        addPanes(quads, key.mask(), sprite, key.texture());
        return List.copyOf(quads);
    }

    private void addBar(List<BakedQuad> out,
            Direction first,
            Direction second,
            int mask,
            TextureAtlasSprite sprite,
            ResourceLocation texture) {
        Direction.Axis length = lengthAxis(first, second);
        Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, length);
        Direction negative = positive.getOpposite();
        int axis = axisIndex(length);

        float[] from = { 0.0f, 0.0f, 0.0f };
        float[] to = { 16.0f, 16.0f, 16.0f };
        clampToEdge(from, to, first);
        clampToEdge(from, to, second);
        if (cornerPlacedAt(mask, first, second, negative)) {
            from[axis] = thickness;
        }
        if (cornerPlacedAt(mask, first, second, positive)) {
            to[axis] = 16.0f - thickness;
        }

        for (Direction face : Direction.values()) {
            if (face.getAxis() == length) {
                continue;
            }
            out.add(bakeFace(from, to, face, plainTile(face, length), sprite, texture));
        }
    }

    private void addCorners(List<BakedQuad> out, int mask, TextureAtlasSprite sprite, ResourceLocation texture) {
        for (Direction x : axisDirections(Direction.Axis.X)) {
            for (Direction y : axisDirections(Direction.Axis.Y)) {
                for (Direction z : axisDirections(Direction.Axis.Z)) {
                    if (!cornerPlaced(mask, x, y, z)) {
                        continue;
                    }

                    int tile = concaveCorner(mask, x, y, z) ? TILE_INNER : TILE_OUTER;

                    float[] from = { 0.0f, 0.0f, 0.0f };
                    float[] to = { 16.0f, 16.0f, 16.0f };
                    clampToEdge(from, to, x);
                    clampToEdge(from, to, y);
                    clampToEdge(from, to, z);

                    for (Direction face : Direction.values()) {
                        out.add(bakeFace(from, to, face, tile, sprite, texture));
                    }
                }
            }
        }
    }

    private void addPanes(List<BakedQuad> out, int mask, TextureAtlasSprite sprite, ResourceLocation texture) {
        for (Direction face : Direction.values()) {
            if (isConnected(mask, face)) {
                continue;
            }

            float[] from = { 0.0f, 0.0f, 0.0f };
            float[] to = { 16.0f, 16.0f, 16.0f };
            for (Direction.Axis axis : Direction.Axis.values()) {
                if (axis == face.getAxis()) {
                    continue;
                }
                int i = axisIndex(axis);
                from[i] = thickness;
                to[i] = 16.0f - thickness;
            }

            int f = axisIndex(face.getAxis());
            if (face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                from[f] = 16.0f;
            } else {
                to[f] = 0.0f;
            }

            out.add(bakeFace(from, to, face, TILE_MIDDLE, sprite, texture));
        }
    }

    private static boolean cornerPlaced(int mask, Direction x, Direction y, Direction z) {
        int bars = (hasBar(mask, y, z) ? 1 : 0)
                + (hasBar(mask, x, z) ? 1 : 0)
                + (hasBar(mask, x, y) ? 1 : 0);
        if (bars >= 2) {
            return true;
        }
        if (bars == 1) {
            return cornerNeeded(mask, x, y, z);
        }
        return deepConcave(mask, x, y, z);
    }

    private static boolean cornerPlacedAt(int mask, Direction a, Direction b, Direction c) {
        Direction x = a.getAxis() == Direction.Axis.X ? a : b.getAxis() == Direction.Axis.X ? b : c;
        Direction y = a.getAxis() == Direction.Axis.Y ? a : b.getAxis() == Direction.Axis.Y ? b : c;
        Direction z = a.getAxis() == Direction.Axis.Z ? a : b.getAxis() == Direction.Axis.Z ? b : c;
        return cornerPlaced(mask, x, y, z);
    }

    private static boolean cornerNeeded(int mask, Direction x, Direction y, Direction z) {
        return (hasBar(mask, y, z) && !isConnected(mask, x))
                || (hasBar(mask, x, z) && !isConnected(mask, y))
                || (hasBar(mask, x, y) && !isConnected(mask, z));
    }

    private static boolean concaveCorner(int mask, Direction x, Direction y, Direction z) {
        if (deepConcave(mask, x, y, z)) {
            return true;
        }
        return (innerBar(mask, y, z) && !isConnected(mask, x))
                || (innerBar(mask, x, z) && !isConnected(mask, y))
                || (innerBar(mask, x, y) && !isConnected(mask, z));
    }

    private static boolean innerBar(int mask, Direction a, Direction b) {
        return isConnected(mask, a) && isConnected(mask, b) && !isDiagonalConnected(mask, a, b);
    }

    private static boolean deepConcave(int mask, Direction x, Direction y, Direction z) {
        return isConnected(mask, x) && isConnected(mask, y) && isConnected(mask, z)
                && isDiagonalConnected(mask, x, y)
                && isDiagonalConnected(mask, x, z)
                && isDiagonalConnected(mask, y, z)
                && !isCornerConnected(mask, x, y, z);
    }

    private static int cornerIndex(Direction x, Direction y, Direction z) {
        int index = 0;
        if (x.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            index |= 1;
        }
        if (y.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            index |= 2;
        }
        if (z.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            index |= 4;
        }
        return index;
    }

    private static boolean isCornerConnected(int mask, Direction x, Direction y, Direction z) {
        return (mask & (1 << (CORNER_BIT_OFFSET + cornerIndex(x, y, z)))) != 0;
    }

    private static boolean hasBar(int mask, Direction first, Direction second) {
        return (!isConnected(mask, first) && !isConnected(mask, second))
                || innerBar(mask, first, second);
    }

    private BakedQuad bakeFace(float[] from,
            float[] to,
            Direction face,
            int tile,
            TextureAtlasSprite sprite,
            ResourceLocation texture) {
        BlockElementFace elementFace = new BlockElementFace(
                null,
                -1,
                "",
                new BlockFaceUV(tileUv(tile, autoUv(face, from, to)), 0));

        return bakery.bakeQuad(
                new Vector3f(from[0], from[1], from[2]),
                new Vector3f(to[0], to[1], to[2]),
                elementFace,
                sprite,
                face,
                BlockModelRotation.X0_Y0,
                null,
                true,
                texture);
    }

    private static float[] autoUv(Direction face, float[] from, float[] to) {
        return switch (face) {
            case DOWN -> new float[] { from[0], 16.0f - to[2], to[0], 16.0f - from[2] };
            case UP -> new float[] { from[0], from[2], to[0], to[2] };
            case NORTH -> new float[] { 16.0f - to[0], 16.0f - to[1], 16.0f - from[0], 16.0f - from[1] };
            case SOUTH -> new float[] { from[0], 16.0f - to[1], to[0], 16.0f - from[1] };
            case WEST -> new float[] { from[2], 16.0f - to[1], to[2], 16.0f - from[1] };
            case EAST -> new float[] { 16.0f - to[2], 16.0f - to[1], 16.0f - from[2], 16.0f - from[1] };
        };
    }

    private static float[] tileUv(int tile, float[] uv) {
        float scale = TILE_U / 16.0f;
        float base = tile * TILE_U;
        return new float[] { base + uv[0] * scale, uv[1], base + uv[2] * scale, uv[3] };
    }

    private void clampToEdge(float[] from, float[] to, Direction dir) {
        int axis = axisIndex(dir.getAxis());

        if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            from[axis] = 16.0f - thickness;
        } else {
            to[axis] = thickness;
        }
    }

    private static int plainTile(Direction face, Direction.Axis length) {
        return uAxis(face) == length ? TILE_HORIZONTAL : TILE_VERTICAL;
    }

    private static Direction.Axis uAxis(Direction face) {
        return face.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

    private static Direction.Axis lengthAxis(Direction first, Direction second) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != first.getAxis() && axis != second.getAxis()) {
                return axis;
            }
        }
        throw new IllegalArgumentException("Directions share an axis: " + first + " " + second);
    }

    private static Direction[] axisDirections(Direction.Axis axis) {
        return new Direction[] {
                Direction.get(Direction.AxisDirection.NEGATIVE, axis),
                Direction.get(Direction.AxisDirection.POSITIVE, axis)
        };
    }

    private static int axisIndex(Direction.Axis axis) {
        return switch (axis) {
            case X -> 0;
            case Y -> 1;
            case Z -> 2;
        };
    }

    private static boolean isConnected(int mask, Direction dir) {
        return (mask & (1 << dir.ordinal())) != 0;
    }

    private static boolean isDiagonalConnected(int mask, Direction first, Direction second) {
        return (mask & (1 << (DIAGONAL_BIT_OFFSET + PAIR_INDEX[first.ordinal()][second.ordinal()]))) != 0;
    }

    private static int[][] buildPairIndex() {
        int[][] table = new int[6][6];
        for (int[] row : table) {
            Arrays.fill(row, -1);
        }

        int index = 0;
        for (Direction first : Direction.values()) {
            for (Direction second : Direction.values()) {
                if (first.getAxis() == second.getAxis() || first.ordinal() > second.ordinal()) {
                    continue;
                }

                table[first.ordinal()][second.ordinal()] = index;
                table[second.ordinal()][first.ordinal()] = index;
                index++;
            }
        }

        return table;
    }

    private record BarCacheKey(ResourceLocation texture, int mask) {
    }
}
