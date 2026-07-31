package net.oktawia.insaneae2addons.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.oktawia.insaneae2addons.entities.research.ResearchPedestalTopBE;

public class ResearchPedestalTopRenderer implements BlockEntityRenderer<ResearchPedestalTopBE> {

    private static final float BOB_AMPLITUDE = 0.1F;
    private static final float BOB_SPEED = 0.1F;
    private static final float ROTATION_SPEED = 1.0F;
    private static final float SCALE = 0.5F;

    public ResearchPedestalTopRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(ResearchPedestalTopBE be, float partialTick,
            PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay) {
        ItemStack stack = be.getStoredStack();
        if (stack.isEmpty() || be.getLevel() == null) {
            return;
        }

        float time = be.getLevel().getGameTime() + partialTick;
        float bob = (float) Math.sin(time * BOB_SPEED) * BOB_AMPLITUDE;
        float angle = time * ROTATION_SPEED;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5 + bob, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        poseStack.mulPose(Axis.YP.rotationDegrees(angle * 1.3F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle * 0.7F));
        poseStack.scale(SCALE, SCALE, SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                be.getLevel(),
                0);

        poseStack.popPose();
    }
}
