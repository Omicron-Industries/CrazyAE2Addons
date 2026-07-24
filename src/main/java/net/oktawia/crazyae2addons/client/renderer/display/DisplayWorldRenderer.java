package net.oktawia.crazyae2addons.client.renderer.display;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.logic.display.DisplayGrid;
import net.oktawia.crazyae2addons.parts.Display;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CrazyAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DisplayWorldRenderer {

    private static final float RENDER_DIST_SQ = 256f * 256f;
    private static final float DISPLAY_SURFACE_OFFSET = 0.53225f;
    private static final int DISPLAY_BUFFER_SIZE = 262_144;
    private static final int MAX_RENDERED_GROUPS_PER_FRAME = 256;
    private static final int MAX_COMMANDS_PER_GROUP = 16_384;

    private static final BufferBuilder DISPLAY_BUFFER_BUILDER = new BufferBuilder(DISPLAY_BUFFER_SIZE);
    private static final MultiBufferSource.BufferSource DISPLAY_BUFFER_SOURCE =
            MultiBufferSource.immediate(DISPLAY_BUFFER_BUILDER);

    private DisplayWorldRenderer() {
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) {
            return;
        }

        PoseStack ps = event.getPoseStack();
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        Frustum frustum = event.getFrustum();

        boolean renderedAnything = false;

        try {
            boolean removedAny = Display.CLIENT_INSTANCES.removeIf(p -> {
                if (p == null) {
                    return true;
                }

                var be = p.getBlockEntity();
                return be == null || be.isRemoved() || be.getLevel() != mc.level;
            });

            if (removedAny) {
                DisplayGrid.invalidateClientCache();
            }

            Set<Display> visited = Collections.newSetFromMap(new IdentityHashMap<>());

            int renderedGroups = 0;

            for (Display part : Display.CLIENT_INSTANCES) {
                if (part == null || visited.contains(part)) {
                    continue;
                }

                DisplayGrid.RenderGroup group = DisplayGrid.getRenderGroup(part);

                if (group == null) {
                    visited.add(part);
                    continue;
                }

                Set<Display> grid = group.parts();

                if (grid == null || grid.isEmpty()) {
                    visited.add(part);
                    continue;
                }

                Display renderOrigin = group.renderOrigin();

                if (renderOrigin == null) {
                    visited.add(part);
                    visited.addAll(grid);
                    continue;
                }

                boolean alreadyRendered = visited.contains(renderOrigin);

                visited.add(part);
                visited.addAll(grid);

                if (alreadyRendered) {
                    continue;
                }

                AABB aabb = group.aabb();

                if (aabb == null) {
                    continue;
                }

                Vec3 center = aabb.getCenter();

                if (center.distanceToSqr(cam) > RENDER_DIST_SQ) {
                    continue;
                }

                if (frustum != null && !frustum.isVisible(aabb)) {
                    continue;
                }

                if (renderedGroups >= MAX_RENDERED_GROUPS_PER_FRAME) {
                    break;
                }

                if (renderMatrix(renderOrigin, grid, ps, DISPLAY_BUFFER_SOURCE, cam)) {
                    renderedAnything = true;
                    renderedGroups++;
                }
            }
        } finally {
            if (renderedAnything) {
                try {
                    DISPLAY_BUFFER_SOURCE.endBatch();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static boolean renderMatrix(
            Display renderOrigin,
            Set<Display> grid,
            PoseStack ps,
            MultiBufferSource.BufferSource buf,
            Vec3 cam
    ) {
        if (renderOrigin == null || grid == null || grid.isEmpty()) {
            return false;
        }

        var be = renderOrigin.getBlockEntity();

        if (be == null || be.isRemoved()) {
            return false;
        }

        Font font = Minecraft.getInstance().font;
        DisplayRendererCommon.PreparedDisplay prepared = DisplayRendererCommon.prepare(font, renderOrigin, grid);

        if (prepared == null || prepared.commands().isEmpty()) {
            return false;
        }

        if (prepared.commands().size() > MAX_COMMANDS_PER_GROUP) {
            return false;
        }

        BlockPos originPos = be.getBlockPos();

        Direction facing = renderOrigin.getSide();
        double nx = facing.getStepX(), ny = facing.getStepY(), nz = facing.getStepZ();
        double sx = originPos.getX() + 0.5 + nx * 0.5;
        double sy = originPos.getY() + 0.5 + ny * 0.5;
        double sz = originPos.getZ() + 0.5 + nz * 0.5;
        if (nx * (cam.x - sx) + ny * (cam.y - sy) + nz * (cam.z - sz) <= 0) {
            return false;
        }

        DisplayGrid.PlaneAxes axes = DisplayGrid.surfaceAxes(facing, renderOrigin.getSpin());
        Vec3 gridRight = toVec(axes.right());
        Vec3 gridUp = toVec(axes.up());
        Vec3 normalVec = toVec(facing);

        Vec3 screenRight = gridRight.cross(gridUp).dot(normalVec) > 0 ? gridRight : gridRight.scale(-1.0);
        Vec3 screenDown = gridUp.scale(-1.0);

        if (facing == Direction.UP) {
            screenRight = screenRight.scale(-1.0);
            screenDown = screenDown.scale(-1.0);
        }

        double minR = Double.MAX_VALUE;
        double minD = Double.MAX_VALUE;
        for (Display member : grid) {
            var mbe = member.getBlockEntity();
            if (mbe == null) {
                continue;
            }
            BlockPos mp = mbe.getBlockPos();
            double cx = mp.getX() + 0.5;
            double cy = mp.getY() + 0.5;
            double cz = mp.getZ() + 0.5;
            minR = Math.min(minR, cx * screenRight.x + cy * screenRight.y + cz * screenRight.z);
            minD = Math.min(minD, cx * screenDown.x + cy * screenDown.y + cz * screenDown.z);
        }
        minR -= 0.5;
        minD -= 0.5;

        double ocx = originPos.getX() + 0.5;
        double ocy = originPos.getY() + 0.5;
        double ocz = originPos.getZ() + 0.5;
        double faceN = ocx * normalVec.x + ocy * normalVec.y + ocz * normalVec.z + DISPLAY_SURFACE_OFFSET;

        Vec3 corner = screenRight.scale(minR).add(screenDown.scale(minD)).add(normalVec.scale(faceN));

        float s = 1f / 64f;
        Matrix4f basis = new Matrix4f();
        basis.m00((float) (screenRight.x * s));
        basis.m01((float) (screenRight.y * s));
        basis.m02((float) (screenRight.z * s));
        basis.m10((float) (screenDown.x * s));
        basis.m11((float) (screenDown.y * s));
        basis.m12((float) (screenDown.z * s));
        basis.m20((float) (normalVec.x * s));
        basis.m21((float) (normalVec.y * s));
        basis.m22((float) (normalVec.z * s));

        ps.pushPose();

        try {
            ps.translate(corner.x - cam.x, corner.y - cam.y, corner.z - cam.z);
            ps.mulPoseMatrix(basis);

            DisplayRendererCommon.renderPrepared(prepared, ps, buf, font, 0xF000F0);
            return true;
        } finally {
            ps.popPose();
        }
    }

    private static Vec3 toVec(Direction dir) {
        return new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
    }
}