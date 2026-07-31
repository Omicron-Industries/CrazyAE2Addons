package net.oktawia.insaneae2addons.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.joml.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.entities.penrose.PenroseBlackHoleEntity;

public class PenroseBlackHoleEntityRenderer extends EntityRenderer<PenroseBlackHoleEntity> {

    private static final ResourceLocation TEXTURE = InsaneAddons.makeId("textures/misc/penrose_black_hole.png");
    private static final float RADIUS = 2.0f;
    private static final int SLICES = 24;
    private static final int STACKS = 16;

    public PenroseBlackHoleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(PenroseBlackHoleEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(PenroseBlackHoleEntity entity, float yaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTick, poseStack, buffer, light);

        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < STACKS; i++) {
            double lat0 = Math.PI * (-0.5 + (double) i / STACKS);
            double lat1 = Math.PI * (-0.5 + (double) (i + 1) / STACKS);
            double y0 = Math.sin(lat0);
            double y1 = Math.sin(lat1);
            double r0 = Math.cos(lat0);
            double r1 = Math.cos(lat1);

            for (int j = 0; j < SLICES; j++) {
                double lng0 = 2.0 * Math.PI * (double) j / SLICES;
                double lng1 = 2.0 * Math.PI * (double) (j + 1) / SLICES;
                double cos0 = Math.cos(lng0);
                double sin0 = Math.sin(lng0);
                double cos1 = Math.cos(lng1);
                double sin1 = Math.sin(lng1);

                float ax = (float) (cos0 * r0 * RADIUS);
                float ay = (float) (y0 * RADIUS);
                float az = (float) (sin0 * r0 * RADIUS);
                float bx = (float) (cos0 * r1 * RADIUS);
                float by = (float) (y1 * RADIUS);
                float bz = (float) (sin0 * r1 * RADIUS);
                float cx = (float) (cos1 * r1 * RADIUS);
                float cy = (float) (y1 * RADIUS);
                float cz = (float) (sin1 * r1 * RADIUS);
                float dx = (float) (cos1 * r0 * RADIUS);
                float dy = (float) (y0 * RADIUS);
                float dz = (float) (sin1 * r0 * RADIUS);

                vertex(builder, matrix, ax, ay, az);
                vertex(builder, matrix, bx, by, bz);
                vertex(builder, matrix, cx, cy, cz);

                vertex(builder, matrix, ax, ay, az);
                vertex(builder, matrix, cx, cy, cz);
                vertex(builder, matrix, dx, dy, dz);
            }
        }

        tesselator.end();
        RenderSystem.enableCull();
    }

    private static void vertex(BufferBuilder builder, Matrix4f matrix, float x, float y, float z) {
        builder.vertex(matrix, x, y, z).color(0.0f, 0.0f, 0.0f, 1.0f).endVertex();
    }
}
