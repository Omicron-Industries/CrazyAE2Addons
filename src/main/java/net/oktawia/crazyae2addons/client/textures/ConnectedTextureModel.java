package net.oktawia.crazyae2addons.client.textures;

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
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectedTextureModel extends BakedModelWrapper<BakedModel> implements PreviewQuadProvider {
    private static final ModelProperty<Connections> CONNECTIONS = new ModelProperty<>();
    private static final float OVERLAY_EPS = 0.1f;

    private final ConnectedTextureEntry entry;
    private final FaceBakery bakery = new FaceBakery();

    private final Map<ResourceLocation, TextureAtlasSprite> spriteCache = new ConcurrentHashMap<>();
    private final Map<QuadCacheKey, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();
    private final Map<FullFaceKey, List<BakedQuad>> fullFaceCache = new ConcurrentHashMap<>();
    private final Map<FullFaceKey, List<BakedQuad>> previewCache = new ConcurrentHashMap<>();

    public ConnectedTextureModel(BakedModel originalModel, ConnectedTextureEntry entry) {
        super(originalModel);
        this.entry = entry;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        EnumMap<Direction, NeighborBits> byFace = new EnumMap<>(Direction.class);

        for (Direction face : Direction.values()) {
            byFace.put(face, computeNeighbors(level, pos, state, face));
        }

        return modelData.derive()
                .with(CONNECTIONS, new Connections(byFace))
                .build();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType) {
        if (state == null || side == null) {
            return originalModel.getQuads(state, side, rand, modelData, renderType);
        }

        if (renderType == RenderType.translucent()) {
            ResourceLocation overlay = entry.faceOverlay(state, side);
            if (overlay == null) {
                return List.of();
            }
            FullFaceKey key = new FullFaceKey(overlay, side);
            return fullFaceCache.computeIfAbsent(key, ignored -> List.of(bakeFullFace(side, overlay)));
        }

        Connections connections = modelData.has(CONNECTIONS) ? modelData.get(CONNECTIONS) : Connections.EMPTY;
        NeighborBits bits = connections.forFace(side);
        FaceQuarters quarters = QuarterLogic.resolve(bits);

        ResourceLocation texture = entry.texture(state);
        QuadCacheKey key = new QuadCacheKey(texture, side, quarters);

        return quadCache.computeIfAbsent(key, ignored -> bakeFace(side, texture, quarters));
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        if (entry.hasOverlay()) {
            return ChunkRenderTypeSet.of(RenderType.solid(), RenderType.translucent());
        }
        return super.getRenderTypes(state, rand, data);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return originalModel.getParticleIcon(modelData);
    }

    private List<BakedQuad> bakeFace(Direction face, ResourceLocation texture, FaceQuarters quarters) {
        TextureAtlasSprite sprite = spriteCache.computeIfAbsent(
                texture,
                tex -> Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(tex)
        );

        List<BakedQuad> quads = new ArrayList<>(4);

        quads.add(bakeQuarter(face, Quarter.TOP_LEFT, quarters.topLeft(), sprite, texture));
        quads.add(bakeQuarter(face, Quarter.TOP_RIGHT, quarters.topRight(), sprite, texture));
        quads.add(bakeQuarter(face, Quarter.BOTTOM_LEFT, quarters.bottomLeft(), sprite, texture));
        quads.add(bakeQuarter(face, Quarter.BOTTOM_RIGHT, quarters.bottomRight(), sprite, texture));

        return List.copyOf(quads);
    }

    private BakedQuad bakeQuarter(Direction face, Quarter quarter, QuarterType type, TextureAtlasSprite sprite, ResourceLocation rl) {
        FaceBounds bounds = FaceBounds.forQuarter(face, quarter);
        float[] uv = quarterUv(type.tile, quarter);

        BlockElementFace elementFace = new BlockElementFace(
                face,
                -1,
                "",
                new BlockFaceUV(uv, 0)
        );

        return bakery.bakeQuad(
                bounds.from,
                bounds.to,
                elementFace,
                sprite,
                face,
                BlockModelRotation.X0_Y0,
                null,
                true,
                rl
        );
    }

    @Override
    public List<BakedQuad> previewQuads(BlockState state, Direction face) {
        if (state == null || face == null) {
            return List.of();
        }

        List<BakedQuad> quads = new ArrayList<>(2);

        ResourceLocation texture = entry.texture(state);
        quads.addAll(previewCache.computeIfAbsent(
                new FullFaceKey(texture, face),
                key -> List.of(bakeSingleTile(key.face(), key.texture()))));

        ResourceLocation overlay = entry.faceOverlay(state, face);
        if (overlay != null) {
            quads.addAll(fullFaceCache.computeIfAbsent(
                    new FullFaceKey(overlay, face),
                    key -> List.of(bakeFullFace(key.face(), key.texture()))));
        }

        return quads;
    }

    private BakedQuad bakeSingleTile(Direction face, ResourceLocation texture) {
        TextureAtlasSprite sprite = spriteCache.computeIfAbsent(
                texture,
                tex -> Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(tex)
        );

        Vector3f[] bounds = cubeFaceBounds(face);
        BlockElementFace elementFace = new BlockElementFace(
                face,
                -1,
                "",
                new BlockFaceUV(new float[]{0.0f, 0.0f, 16.0f / 5.0f, 16.0f}, 0)
        );

        return bakery.bakeQuad(
                bounds[0],
                bounds[1],
                elementFace,
                sprite,
                face,
                BlockModelRotation.X0_Y0,
                null,
                true,
                texture
        );
    }

    private static Vector3f[] cubeFaceBounds(Direction face) {
        return switch (face) {
            case NORTH -> new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(16, 16, 0)};
            case SOUTH -> new Vector3f[]{new Vector3f(0, 0, 16), new Vector3f(16, 16, 16)};
            case WEST -> new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0, 16, 16)};
            case EAST -> new Vector3f[]{new Vector3f(16, 0, 0), new Vector3f(16, 16, 16)};
            case UP -> new Vector3f[]{new Vector3f(0, 16, 0), new Vector3f(16, 16, 16)};
            case DOWN -> new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(16, 0, 16)};
        };
    }

    private BakedQuad bakeFullFace(Direction face, ResourceLocation texture) {
        TextureAtlasSprite sprite = spriteCache.computeIfAbsent(
                texture,
                tex -> Minecraft.getInstance()
                        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                        .apply(tex)
        );

        Vector3f[] bounds = fullFaceBounds(face);
        BlockElementFace elementFace = new BlockElementFace(
                face,
                -1,
                "",
                new BlockFaceUV(new float[]{0.0f, 0.0f, 16.0f, 16.0f}, 0)
        );

        return bakery.bakeQuad(
                bounds[0],
                bounds[1],
                elementFace,
                sprite,
                face,
                BlockModelRotation.X0_Y0,
                null,
                true,
                texture
        );
    }

    private static Vector3f[] fullFaceBounds(Direction face) {
        float e = OVERLAY_EPS;
        return switch (face) {
            case NORTH -> new Vector3f[]{new Vector3f(0, 0, -e), new Vector3f(16, 16, -e)};
            case SOUTH -> new Vector3f[]{new Vector3f(0, 0, 16 + e), new Vector3f(16, 16, 16 + e)};
            case WEST -> new Vector3f[]{new Vector3f(-e, 0, 0), new Vector3f(-e, 16, 16)};
            case EAST -> new Vector3f[]{new Vector3f(16 + e, 0, 0), new Vector3f(16 + e, 16, 16)};
            case UP -> new Vector3f[]{new Vector3f(0, 16 + e, 0), new Vector3f(16, 16 + e, 16)};
            case DOWN -> new Vector3f[]{new Vector3f(0, -e, 0), new Vector3f(16, -e, 16)};
        };
    }

    private static float[] quarterUv(int tile, Quarter quarter) {
        float tileW = 16.0f / 5.0f;
        float tileH = 16.0f;

        float quarterW = tileW / 2.0f;
        float quarterH = tileH / 2.0f;

        float baseU = tile * tileW;
        float baseV = 0.0f;

        float u0 = baseU + (quarter.uPart * quarterW);
        float v0 = baseV + (quarter.vPart * quarterH);
        float u1 = u0 + quarterW;
        float v1 = v0 + quarterH;

        return new float[]{u0, v0, u1, v1};
    }

    private NeighborBits computeNeighbors(BlockAndTintGetter level,
                                          BlockPos pos,
                                          BlockState selfState,
                                          Direction renderedFace) {
        FaceSpace space = FaceSpace.of(renderedFace);

        boolean up = connects(level, pos, selfState, pos.relative(space.up), renderedFace);
        boolean right = connects(level, pos, selfState, pos.relative(space.right), renderedFace);
        boolean down = connects(level, pos, selfState, pos.relative(space.down), renderedFace);
        boolean left = connects(level, pos, selfState, pos.relative(space.left), renderedFace);

        boolean upRight = up && right && connects(
                level, pos, selfState,
                pos.relative(space.up).relative(space.right),
                renderedFace
        );

        boolean upLeft = up && left && connects(
                level, pos, selfState,
                pos.relative(space.up).relative(space.left),
                renderedFace
        );

        boolean downRight = down && right && connects(
                level, pos, selfState,
                pos.relative(space.down).relative(space.right),
                renderedFace
        );

        boolean downLeft = down && left && connects(
                level, pos, selfState,
                pos.relative(space.down).relative(space.left),
                renderedFace
        );

        return new NeighborBits(up, right, down, left, upRight, upLeft, downRight, downLeft);
    }

    private boolean connects(BlockAndTintGetter level,
                             BlockPos selfPos,
                             BlockState selfState,
                             BlockPos otherPos,
                             Direction face) {
        BlockState otherState = level.getBlockState(otherPos);
        return entry.rule().connects(level, selfPos, selfState, otherPos, otherState, face);
    }

    private enum Quarter {
        TOP_LEFT(0, 0),
        TOP_RIGHT(1, 0),
        BOTTOM_LEFT(0, 1),
        BOTTOM_RIGHT(1, 1);

        final int uPart;
        final int vPart;

        Quarter(int uPart, int vPart) {
            this.uPart = uPart;
            this.vPart = vPart;
        }
    }

    private record QuadCacheKey(ResourceLocation texture, Direction face, FaceQuarters quarters) {}

    private record FullFaceKey(ResourceLocation texture, Direction face) {}

    private record Connections(Map<Direction, NeighborBits> byFace) {
        static final Connections EMPTY = new Connections(Map.of());

        Connections {
            byFace = Map.copyOf(byFace);
        }

        NeighborBits forFace(Direction face) {
            return byFace.getOrDefault(face, new NeighborBits(false, false, false, false, false, false, false, false));
        }
    }

    private record FaceSpace(Direction up, Direction right, Direction down, Direction left) {
        static FaceSpace of(Direction face) {
            return switch (face) {
                case NORTH -> new FaceSpace(Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST);
                case SOUTH -> new FaceSpace(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST);
                case WEST  -> new FaceSpace(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH);
                case EAST  -> new FaceSpace(Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH);
                case UP    -> new FaceSpace(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
                case DOWN  -> new FaceSpace(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST);
            };
        }
    }

    private record FaceBounds(Vector3f from, Vector3f to) {
        static FaceBounds forQuarter(Direction face, Quarter quarter) {
            return switch (face) {
                case NORTH -> switch (quarter) {
                    case TOP_LEFT -> of(8, 8, 0, 16, 16, 0);
                    case TOP_RIGHT -> of(0, 8, 0, 8, 16, 0);
                    case BOTTOM_LEFT -> of(8, 0, 0, 16, 8, 0);
                    case BOTTOM_RIGHT -> of(0, 0, 0, 8, 8, 0);
                };
                case SOUTH -> switch (quarter) {
                    case TOP_LEFT -> of(0, 8, 16, 8, 16, 16);
                    case TOP_RIGHT -> of(8, 8, 16, 16, 16, 16);
                    case BOTTOM_LEFT -> of(0, 0, 16, 8, 8, 16);
                    case BOTTOM_RIGHT -> of(8, 0, 16, 16, 8, 16);
                };
                case WEST -> switch (quarter) {
                    case TOP_LEFT -> of(0, 8, 0, 0, 16, 8);
                    case TOP_RIGHT -> of(0, 8, 8, 0, 16, 16);
                    case BOTTOM_LEFT -> of(0, 0, 0, 0, 8, 8);
                    case BOTTOM_RIGHT -> of(0, 0, 8, 0, 8, 16);
                };
                case EAST -> switch (quarter) {
                    case TOP_LEFT -> of(16, 8, 8, 16, 16, 16);
                    case TOP_RIGHT -> of(16, 8, 0, 16, 16, 8);
                    case BOTTOM_LEFT -> of(16, 0, 8, 16, 8, 16);
                    case BOTTOM_RIGHT -> of(16, 0, 0, 16, 8, 8);
                };
                case UP -> switch (quarter) {
                    case TOP_LEFT -> of(0, 16, 0, 8, 16, 8);
                    case TOP_RIGHT -> of(8, 16, 0, 16, 16, 8);
                    case BOTTOM_LEFT -> of(0, 16, 8, 8, 16, 16);
                    case BOTTOM_RIGHT -> of(8, 16, 8, 16, 16, 16);
                };
                case DOWN -> switch (quarter) {
                    case TOP_LEFT -> of(0, 0, 8, 8, 0, 16);
                    case TOP_RIGHT -> of(8, 0, 8, 16, 0, 16);
                    case BOTTOM_LEFT -> of(0, 0, 0, 8, 0, 8);
                    case BOTTOM_RIGHT -> of(8, 0, 0, 16, 0, 8);
                };
            };
        }

        private static FaceBounds of(float x1, float y1, float z1, float x2, float y2, float z2) {
            return new FaceBounds(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        }
    }
}