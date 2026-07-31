package net.oktawia.crazyae2addons.client.renderer.preview.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;

import net.oktawia.crazyae2addons.client.textures.PreviewQuadProvider;

public final class PreviewRenderer {
    public static float previewAlpha = 0.38f;
    public static float alphaStep = 0.08f;

    private static final float OUTLINE_HALF_WIDTH = 0.012f;

    private static final float FRONTIER_RED = 1.0f;
    private static final float FRONTIER_GREEN = 0.15f;
    private static final float FRONTIER_BLUE = 0.85f;

    private static final int MAX_RENDER_DISTANCE = 48;
    private static final int MIN_RENDER_DISTANCE = 12;
    private static final int DISTANCE_STEP = 4;
    private static final int FPS_FLOOR = 30;
    private static final int FPS_CEILING = 55;
    private static final long DISTANCE_ADJUST_INTERVAL_MS = 500L;

    private static final long QUAD_SEED = 42L;
    private static final double PICK_STEP = 0.05;

    private static final RandomSource QUAD_RANDOM = RandomSource.create();
    private static final BlockPos.MutableBlockPos PICK_CURSOR = new BlockPos.MutableBlockPos();
    private static final Map<BlockState, List<BakedQuad>> QUAD_CACHE = new HashMap<>();

    private static int renderDistance = MAX_RENDER_DISTANCE;
    private static long lastDistanceAdjustMs;

    public static void clearQuadCache() {
        QUAD_CACHE.clear();
    }

    public static int getRenderDistance() {
        return renderDistance;
    }

    private static int adjustRenderDistance(Minecraft mc) {
        long now = Util.getMillis();
        if (now - lastDistanceAdjustMs >= DISTANCE_ADJUST_INTERVAL_MS) {
            lastDistanceAdjustMs = now;

            int fps = mc.getFps();
            if (fps < FPS_FLOOR) {
                renderDistance = Math.max(MIN_RENDER_DISTANCE, renderDistance - DISTANCE_STEP);
            } else if (fps > FPS_CEILING) {
                renderDistance = Math.min(MAX_RENDER_DISTANCE, renderDistance + DISTANCE_STEP);
            }
        }

        return renderDistance * renderDistance;
    }

    private PreviewRenderer() {
    }

    public static void render(MultiblockPreviewInfo previewInfo, RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || previewInfo == null)
            return;

        Level level = mc.level;
        previewInfo.validate(level, level.getGameTime());

        float tick = level.getGameTime() + event.getPartialTick();
        previewInfo.advanceAlpha(tick - previewInfo.lastTick, previewAlpha, alphaStep);
        previewInfo.lastTick = tick;

        updateTooltip(previewInfo, mc, level);

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        Frustum frustum = event.getFrustum();
        BlockPos playerPos = mc.player.blockPosition();
        int renderDistanceSqr = adjustRenderDistance(mc);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        VertexConsumer translucentBuffer = buffer.getBuffer(RenderType.translucent());

        for (MultiblockPreviewInfo.Section section : previewInfo.sections()) {
            if (section.missing().isEmpty() || !frustum.isVisible(section.bounds())) {
                continue;
            }

            for (MultiblockPreviewInfo.BlockInfo info : section.missing()) {
                BlockPos pos = info.pos();
                if (pos.distSqr(playerPos) > renderDistanceSqr) {
                    continue;
                }

                float alpha = previewInfo.alphaAt(pos.getY());
                if (alpha <= 0.0f) {
                    continue;
                }

                renderGhost(poseStack, translucentBuffer, blockRenderer, info, alpha);
            }
        }

        VertexConsumer outlineBuffer = buffer.getBuffer(PreviewRenderTypes.OVERLAY_OUTLINE);
        Matrix4f matrix = poseStack.last().pose();

        for (BlockPos pos : previewInfo.frontierBlocks()) {
            if (pos.distSqr(playerPos) > renderDistanceSqr) {
                continue;
            }

            renderOutline(outlineBuffer, matrix, cameraPos, pos, FRONTIER_RED, FRONTIER_GREEN, FRONTIER_BLUE);
        }

        for (BlockPos pos : previewInfo.invalidBlocks()) {
            if (pos.distSqr(playerPos) > renderDistanceSqr) {
                continue;
            }

            renderOutline(outlineBuffer, matrix, cameraPos, pos, 1.0f, 0.15f, 0.15f);
        }

        buffer.endBatch(PreviewRenderTypes.OVERLAY_OUTLINE);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        poseStack.popPose();

        buffer.endBatch(RenderType.translucent());
    }

    private static void renderOutline(
            VertexConsumer target,
            Matrix4f matrix,
            Vec3 camera,
            BlockPos pos,
            float red,
            float green,
            float blue) {
        float x1 = pos.getX();
        float y1 = pos.getY();
        float z1 = pos.getZ();
        float x2 = x1 + 1.0f;
        float y2 = y1 + 1.0f;
        float z2 = z1 + 1.0f;

        edge(target, matrix, camera, x1, y1, z1, x2, y1, z1, red, green, blue);
        edge(target, matrix, camera, x2, y1, z1, x2, y1, z2, red, green, blue);
        edge(target, matrix, camera, x2, y1, z2, x1, y1, z2, red, green, blue);
        edge(target, matrix, camera, x1, y1, z2, x1, y1, z1, red, green, blue);

        edge(target, matrix, camera, x1, y2, z1, x2, y2, z1, red, green, blue);
        edge(target, matrix, camera, x2, y2, z1, x2, y2, z2, red, green, blue);
        edge(target, matrix, camera, x2, y2, z2, x1, y2, z2, red, green, blue);
        edge(target, matrix, camera, x1, y2, z2, x1, y2, z1, red, green, blue);

        edge(target, matrix, camera, x1, y1, z1, x1, y2, z1, red, green, blue);
        edge(target, matrix, camera, x2, y1, z1, x2, y2, z1, red, green, blue);
        edge(target, matrix, camera, x2, y1, z2, x2, y2, z2, red, green, blue);
        edge(target, matrix, camera, x1, y1, z2, x1, y2, z2, red, green, blue);
    }

    private static void edge(
            VertexConsumer target,
            Matrix4f matrix,
            Vec3 camera,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float red, float green, float blue) {
        Vector3f direction = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
        if (direction.lengthSquared() < 1.0e-6f) {
            return;
        }

        Vector3f toCamera = new Vector3f(
                (float) (camera.x - (x1 + x2) * 0.5),
                (float) (camera.y - (y1 + y2) * 0.5),
                (float) (camera.z - (z1 + z2) * 0.5));

        Vector3f side = direction.cross(toCamera, new Vector3f());
        if (side.lengthSquared() < 1.0e-6f) {
            return;
        }
        side.normalize().mul(OUTLINE_HALF_WIDTH);

        vertex(target, matrix, x1 + side.x, y1 + side.y, z1 + side.z, red, green, blue);
        vertex(target, matrix, x2 + side.x, y2 + side.y, z2 + side.z, red, green, blue);
        vertex(target, matrix, x2 - side.x, y2 - side.y, z2 - side.z, red, green, blue);
        vertex(target, matrix, x1 - side.x, y1 - side.y, z1 - side.z, red, green, blue);
    }

    private static void vertex(
            VertexConsumer target,
            Matrix4f matrix,
            float x, float y, float z,
            float red, float green, float blue) {
        target.vertex(matrix, x, y, z).color(red, green, blue, 1.0f).endVertex();
    }

    private static void renderGhost(
            PoseStack poseStack,
            VertexConsumer target,
            BlockRenderDispatcher blockRenderer,
            MultiblockPreviewInfo.BlockInfo info,
            float alpha) {
        BlockPos pos = info.pos();

        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.06f, pos.getY() + 0.06f, pos.getZ() + 0.06f);
        poseStack.scale(0.88f, 0.88f, 0.88f);

        putQuads(poseStack, target, quadsFor(blockRenderer, info.state()), alpha);

        poseStack.popPose();
    }

    private static List<BakedQuad> quadsFor(BlockRenderDispatcher blockRenderer, BlockState state) {
        List<BakedQuad> cached = QUAD_CACHE.get(state);
        if (cached != null) {
            return cached;
        }

        BakedModel model = blockRenderer.getBlockModel(state);
        List<BakedQuad> quads = new ArrayList<>();

        if (model instanceof PreviewQuadProvider provider) {
            for (Direction direction : Direction.values()) {
                quads.addAll(provider.previewQuads(state, direction));
            }
        } else {
            QUAD_RANDOM.setSeed(QUAD_SEED);
            for (RenderType layer : model.getRenderTypes(state, QUAD_RANDOM, ModelData.EMPTY)) {
                for (Direction direction : Direction.values()) {
                    QUAD_RANDOM.setSeed(QUAD_SEED);
                    quads.addAll(model.getQuads(state, direction, QUAD_RANDOM, ModelData.EMPTY, layer));
                }

                QUAD_RANDOM.setSeed(QUAD_SEED);
                quads.addAll(model.getQuads(state, null, QUAD_RANDOM, ModelData.EMPTY, layer));
            }
        }

        List<BakedQuad> result = List.copyOf(quads);
        QUAD_CACHE.put(state, result);
        return result;
    }

    private static void putQuads(PoseStack poseStack, VertexConsumer target, Iterable<BakedQuad> quads, float alpha) {
        for (BakedQuad quad : quads) {
            target.putBulkData(
                    poseStack.last(), quad,
                    1f, 1f, 1f, alpha,
                    0x00F000F0, OverlayTexture.NO_OVERLAY, true);
        }
    }

    private static void updateTooltip(MultiblockPreviewInfo previewInfo, Minecraft mc, Level level) {
        float reach = mc.gameMode != null ? mc.gameMode.getPickRange() : 5.0f;
        MultiblockPreviewInfo.BlockInfo pointed = pick(previewInfo, mc, level, reach);

        if (pointed == null) {
            PreviewTooltipLayer.set(null, null, 0L);
            return;
        }

        PreviewTooltipLayer.set(
                pointed.state().getBlock().getName().getString(),
                null,
                PreviewTooltipLayer.DEFAULT_TTL_MS);
    }

    private static @Nullable MultiblockPreviewInfo.BlockInfo pick(
            MultiblockPreviewInfo previewInfo,
            Minecraft mc,
            Level level,
            float reach) {
        Vec3 eye = mc.player.getEyePosition();
        Vec3 step = mc.player.getLookAngle().scale(PICK_STEP);
        double x = eye.x;
        double y = eye.y;
        double z = eye.z;

        int steps = (int) Math.ceil(reach / PICK_STEP);
        long lastKey = Long.MIN_VALUE;

        for (int i = 0; i < steps; i++) {
            x += step.x;
            y += step.y;
            z += step.z;

            PICK_CURSOR.set(Math.floor(x), Math.floor(y), Math.floor(z));
            long key = PICK_CURSOR.asLong();
            if (key == lastKey) {
                continue;
            }
            lastKey = key;

            MultiblockPreviewInfo.BlockInfo info = previewInfo.at(PICK_CURSOR);
            if (info != null && previewInfo.isMissing(level, info)) {
                return info;
            }
        }

        return null;
    }

}
