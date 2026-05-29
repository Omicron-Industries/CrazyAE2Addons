package net.oktawia.crazyae2addons.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oktawia.crazyae2addons.CrazyAddons;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalDouble;

@Mod.EventBusSubscriber(modid = CrazyAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class InterfaceHighlighter {

    private static final long DURATION_MS = 10_000L;
    private static final double EXPAND = 0.005;

    private static final MultiBufferSource.BufferSource BUFFER =
            MultiBufferSource.immediate(new BufferBuilder(2048));

    private static @Nullable BlockPos highlightedPos;
    private static long activatedMs;

    private InterfaceHighlighter() {}

    public static void highlight(BlockPos pos) {
        highlightedPos = pos.immutable();
        activatedMs = System.currentTimeMillis();
    }

    public static void clear() {
        highlightedPos = null;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (highlightedPos == null) return;
        if (Minecraft.getInstance().level == null) return;

        long now = System.currentTimeMillis();

        if (now - activatedMs > DURATION_MS) {
            highlightedPos = null;
            return;
        }

        if ((now % 1000) >= 500) return;

        Vec3 cam = event.getCamera().getPosition();

        double minX = highlightedPos.getX() - cam.x - EXPAND;
        double minY = highlightedPos.getY() - cam.y - EXPAND;
        double minZ = highlightedPos.getZ() - cam.z - EXPAND;

        AABB aabb = new AABB(
                minX,
                minY,
                minZ,
                minX + 1.0 + 2.0 * EXPAND,
                minY + 1.0 + 2.0 * EXPAND,
                minZ + 1.0 + 2.0 * EXPAND
        );

        RenderSystem.disableDepthTest();
        VertexConsumer consumer = BUFFER.getBuffer(HighlightLines.TYPE);
        LevelRenderer.renderLineBox(event.getPoseStack(), consumer, aabb, 1.0f, 0.2f, 0.1f, 1.0f);
        BUFFER.endBatch(HighlightLines.TYPE);
        RenderSystem.enableDepthTest();
    }

    private static final class HighlightLines extends RenderType {

        static final RenderType TYPE = create(
                "crazyae2addons:interface_highlight",
                DefaultVertexFormat.POSITION_COLOR_NORMAL,
                VertexFormat.Mode.LINES,
                256,
                false,
                false,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_LINES_SHADER)
                        .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(4.0)))
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setOutputState(MAIN_TARGET)
                        .createCompositeState(false)
        );

        private HighlightLines(
                String name,
                VertexFormat format,
                VertexFormat.Mode mode,
                int bufferSize,
                boolean affectsCrumbling,
                boolean sortOnUpload,
                Runnable setupState,
                Runnable clearState
        ) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
            throw new AssertionError();
        }
    }
}