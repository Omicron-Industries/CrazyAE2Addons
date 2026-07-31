package net.oktawia.insaneae2addons.client.renderer;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.client.renderer.PenroseLaserBeamStyle.Tint;

@Mod.EventBusSubscriber(modid = InsaneAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PenroseLaserBeamRenderer {

    private static final List<Beam> BEAMS = new ArrayList<>();

    private PenroseLaserBeamRenderer() {
    }

    public static void addBeam(BlockPos pos, Direction direction, float length, float intensity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || length <= 0.0f) {
            return;
        }

        if (BEAMS.size() >= PenroseLaserBeamStyle.MAX_ACTIVE_BEAMS) {
            BEAMS.remove(0);
        }

        Vec3 dir = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 origin = Vec3.atCenterOf(pos).add(dir.scale(0.5));
        BEAMS.add(new Beam(origin, dir, length, Mth.clamp(intensity, 0.0f, 1.0f), mc.level.getGameTime()));
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || BEAMS.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        long gameTime = mc.level.getGameTime();
        float partial = event.getPartialTick();
        BEAMS.removeIf(beam -> beam.age(gameTime, partial) >= PenroseLaserBeamStyle.LIFETIME_TICKS);
        if (BEAMS.isEmpty()) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Beam beam : BEAMS) {
            drawBeam(builder, matrix, beam, camera, beam.age(gameTime, partial));
        }

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawBeam(BufferBuilder builder, Matrix4f matrix, Beam beam, Vec3 camera, float age) {
        float life = PenroseLaserBeamStyle.life(age);
        float fade = PenroseLaserBeamStyle.fade(life);
        float intensity = beam.intensity;
        float coreRadius = PenroseLaserBeamStyle.coreRadius(intensity, life, age);

        Vec3 dir = beam.dir;
        Vec3 axis = Math.abs(dir.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
        Vec3 u = dir.cross(axis).normalize();
        Vec3 v = dir.cross(u).normalize();

        tube(builder, matrix, beam, u, v, coreRadius * PenroseLaserBeamStyle.GLOW_RADIUS_FACTOR,
                PenroseLaserBeamStyle.GLOW_SIDES, PenroseLaserBeamStyle.GLOW.scaled(fade));
        tube(builder, matrix, beam, u, v, coreRadius,
                PenroseLaserBeamStyle.CORE_SIDES, PenroseLaserBeamStyle.CORE.scaled(fade));

        Vec3 midpoint = beam.origin.add(dir.scale(beam.length * 0.5));
        Vec3 side = dir.cross(camera.subtract(midpoint));
        Vec3 flare = side.lengthSqr() < 1.0e-6 ? u : side.normalize();

        ribbon(builder, matrix, beam, flare, coreRadius * PenroseLaserBeamStyle.FLARE_RADIUS_FACTOR,
                PenroseLaserBeamStyle.FLARE.scaled(fade));
        ribbon(builder, matrix, beam, flare, PenroseLaserBeamStyle.hazeRadius(coreRadius, intensity),
                PenroseLaserBeamStyle.HAZE.scaled(fade));

        float muzzleRadius = coreRadius * PenroseLaserBeamStyle.GLOW_RADIUS_FACTOR
                * PenroseLaserBeamStyle.MUZZLE_RADIUS_FACTOR * PenroseLaserBeamStyle.muzzlePulse(age);
        disc(builder, matrix, beam.origin, camera, muzzleRadius, PenroseLaserBeamStyle.MUZZLE.scaled(fade));

        Vec3 impact = beam.origin.add(dir.scale(beam.length));
        disc(builder, matrix, impact, camera, PenroseLaserBeamStyle.impactRadius(coreRadius, life),
                PenroseLaserBeamStyle.IMPACT.scaled(fade));
        ring(builder, matrix, impact, camera,
                PenroseLaserBeamStyle.ringInnerRadius(intensity, life),
                PenroseLaserBeamStyle.ringOuterRadius(intensity, life),
                PenroseLaserBeamStyle.RING.scaled(fade));
    }

    private static void tube(BufferBuilder builder, Matrix4f matrix, Beam beam, Vec3 u, Vec3 v,
            float radius, int sides, Tint tint) {
        Vec3 tip = beam.origin.add(beam.dir.scale(beam.length));
        Tint tipTint = tint.scaled(PenroseLaserBeamStyle.TIP_ALPHA);

        for (int i = 0; i < sides; i++) {
            Vec3 offsetA = offset(u, v, radius, i, sides);
            Vec3 offsetB = offset(u, v, radius, i + 1, sides);

            vertex(builder, matrix, beam.origin.add(offsetA), tint);
            vertex(builder, matrix, beam.origin.add(offsetB), tint);
            vertex(builder, matrix, tip.add(offsetB), tipTint);
            vertex(builder, matrix, tip.add(offsetA), tipTint);
        }
    }

    private static void ribbon(BufferBuilder builder, Matrix4f matrix, Beam beam, Vec3 side,
            float halfWidth, Tint tint) {
        Vec3 tip = beam.origin.add(beam.dir.scale(beam.length));
        Vec3 offset = side.scale(halfWidth);
        Tint edge = tint.scaled(0.0f);

        vertex(builder, matrix, beam.origin.subtract(offset), edge);
        vertex(builder, matrix, beam.origin, tint);
        vertex(builder, matrix, tip, tint);
        vertex(builder, matrix, tip.subtract(offset), edge);

        vertex(builder, matrix, beam.origin, tint);
        vertex(builder, matrix, beam.origin.add(offset), edge);
        vertex(builder, matrix, tip.add(offset), edge);
        vertex(builder, matrix, tip, tint);
    }

    private static void disc(BufferBuilder builder, Matrix4f matrix, Vec3 center, Vec3 camera,
            float radius, Tint tint) {
        Vec3 normal = camera.subtract(center);
        if (normal.lengthSqr() < 1.0e-6) {
            return;
        }

        Vec3 u = billboardU(normal.normalize());
        Vec3 v = normal.normalize().cross(u).normalize();
        Tint edge = tint.scaled(0.0f);
        int sides = PenroseLaserBeamStyle.DISC_SIDES;

        for (int i = 0; i < sides; i++) {
            vertex(builder, matrix, center, tint);
            vertex(builder, matrix, center, tint);
            vertex(builder, matrix, center.add(offset(u, v, radius, i + 1, sides)), edge);
            vertex(builder, matrix, center.add(offset(u, v, radius, i, sides)), edge);
        }
    }

    private static void ring(BufferBuilder builder, Matrix4f matrix, Vec3 center, Vec3 camera,
            float inner, float outer, Tint tint) {
        Vec3 normal = camera.subtract(center);
        if (normal.lengthSqr() < 1.0e-6) {
            return;
        }

        Vec3 u = billboardU(normal.normalize());
        Vec3 v = normal.normalize().cross(u).normalize();
        Tint edge = tint.scaled(0.0f);
        int sides = PenroseLaserBeamStyle.DISC_SIDES;

        for (int i = 0; i < sides; i++) {
            vertex(builder, matrix, center.add(offset(u, v, inner, i, sides)), tint);
            vertex(builder, matrix, center.add(offset(u, v, inner, i + 1, sides)), tint);
            vertex(builder, matrix, center.add(offset(u, v, outer, i + 1, sides)), edge);
            vertex(builder, matrix, center.add(offset(u, v, outer, i, sides)), edge);
        }
    }

    private static Vec3 billboardU(Vec3 normal) {
        Vec3 axis = Math.abs(normal.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
        return normal.cross(axis).normalize();
    }

    private static Vec3 offset(Vec3 u, Vec3 v, float radius, int index, int sides) {
        double angle = 2.0 * Math.PI * index / sides;
        return u.scale(Math.cos(angle) * radius).add(v.scale(Math.sin(angle) * radius));
    }

    private static void vertex(BufferBuilder builder, Matrix4f matrix, Vec3 pos, Tint tint) {
        builder.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(tint.red(), tint.green(), tint.blue(), tint.alpha())
                .endVertex();
    }

    private record Beam(Vec3 origin, Vec3 dir, float length, float intensity, long startTick) {
        float age(long gameTime, float partial) {
            return (float) (gameTime - this.startTick) + partial;
        }
    }
}
