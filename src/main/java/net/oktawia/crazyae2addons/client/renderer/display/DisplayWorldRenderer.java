package net.oktawia.crazyae2addons.client.renderer.display;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CrazyAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DisplayWorldRenderer {

    private static final float RENDER_DIST_SQ = 256f * 256f;
    private static final float DISPLAY_SURFACE_OFFSET = 0.501f;
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

        ps.pushPose();

        try {
            ps.translate(originPos.getX() - cam.x, originPos.getY() - cam.y, originPos.getZ() - cam.z);
            applyFacingTransform(ps, renderOrigin);
            ps.translate(0.0f, 0.0f, DISPLAY_SURFACE_OFFSET);
            ps.scale(1f / 64f, -1f / 64f, 1f / 64f);

            DisplayRendererCommon.renderPrepared(prepared, ps, buf, font, 0xF000F0);
            return true;
        } finally {
            ps.popPose();
        }
    }

    private record Transformation(float tx, float ty, float tz, float yRot, float xRot) {
    }

    private static void applyFacingTransform(PoseStack ps, Display part) {
        Transformation t = getFacingTransformation(part.getSide());

        ps.translate(t.tx, t.ty, t.tz);
        ps.mulPose(Axis.YP.rotationDegrees(t.yRot));
        ps.mulPose(Axis.XP.rotationDegrees(t.xRot));

        if (t.xRot != 0f) {
            applySpinTransformation(ps, part, t.xRot);
        }
    }

    private static Transformation getFacingTransformation(Direction facing) {
        return switch (facing) {
            case SOUTH -> new Transformation(0f, 1f, 0.5f, 0f, 0f);
            case WEST -> new Transformation(0.5f, 1f, 0f, -90f, 0f);
            case EAST -> new Transformation(0.5f, 1f, 1f, 90f, 0f);
            case NORTH -> new Transformation(1f, 1f, 0.5f, 180f, 0f);
            case UP -> new Transformation(0f, 0.5f, 0f, 0f, -90f);
            case DOWN -> new Transformation(1f, 0.5f, 0f, 0f, 90f);
        };
    }

    private static void applySpinTransformation(PoseStack ps, Display part, float xRot) {
        float spin = 0f;

        if (xRot == 90f) {
            switch (part.getSpin()) {
                case 0 -> {
                    spin = 0f;
                    ps.translate(-1f, 1f, 0f);
                }
                case 1 -> {
                    spin = 90f;
                    ps.translate(-1f, 0f, 0f);
                }
                case 2 -> {
                    spin = 180f;
                    ps.translate(0f, 0f, 0f);
                }
                case 3 -> {
                    spin = -90f;
                    ps.translate(0f, 1f, 0f);
                }
            }
        } else {
            switch (part.getSpin()) {
                case 0 -> {
                    spin = 0f;
                    ps.translate(0f, 0f, 0f);
                }
                case 1 -> {
                    spin = -90f;
                    ps.translate(1f, 0f, 0f);
                }
                case 2 -> {
                    spin = 180f;
                    ps.translate(1f, -1f, 0f);
                }
                case 3 -> {
                    spin = 90f;
                    ps.translate(0f, -1f, 0f);
                }
            }
        }

        ps.mulPose(Axis.ZP.rotationDegrees(spin));
    }
}